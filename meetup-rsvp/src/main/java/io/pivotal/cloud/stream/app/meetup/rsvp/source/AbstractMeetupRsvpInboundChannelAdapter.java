/*
 * Copyright 2014-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.cloud.stream.app.meetup.rsvp.source;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.*;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Abstract class for the meetup-rsvp inbound channel adapter.
 *
 * @author David Turanski
 * @author Gary Russell
 * @author Artem Bilan
 * @author Wayne Lund
 */
//TODO: Only action done here was to rename the class to MeetupRsvp

	@Component
public abstract class AbstractMeetupRsvpInboundChannelAdapter extends MessageProducerSupport {

	private final static AtomicInteger instance = new AtomicInteger();

	@Autowired
	private RestTemplate restTemplate;

	private final ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

	private final Object monitor = new Object();

	private final AtomicBoolean running = new AtomicBoolean(false);

	// BackOff values
	private final AtomicInteger linearBackOff = new AtomicInteger(250);

	private final AtomicInteger httpErrorBackOff = new AtomicInteger(5000);

	private final AtomicInteger rateLimitBackOff = new AtomicInteger(60000);

	public AbstractMeetupRsvpInboundChannelAdapter() {
	}

	protected AbstractMeetupRsvpInboundChannelAdapter(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		// Fix to get round ErrorHandler not handling 401s etc.
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
		this.setPhase(Integer.MAX_VALUE);
	}

	/**
	 * The read timeout for the underlying URLConnection to the meetup rsvp stream.
	 */
	public void setReadTimeout(int millis) {
		// Hack to get round Spring's dynamic loading of http client stuff
		ClientHttpRequestFactory f = getRequestFactory();
		if (f instanceof SimpleClientHttpRequestFactory) {
			((SimpleClientHttpRequestFactory) f).setReadTimeout(millis);
		}
		else {
			((HttpComponentsClientHttpRequestFactory) f).setReadTimeout(millis);
		}
	}

	/**
	 * The connection timeout for making a connection to Meetup RSVP
	 */
	public void setConnectTimeout(int millis) {
		ClientHttpRequestFactory f = getRequestFactory();
		if (f instanceof SimpleClientHttpRequestFactory) {
			((SimpleClientHttpRequestFactory) f).setConnectTimeout(millis);
		}
		else {
			((HttpComponentsClientHttpRequestFactory) f).setConnectTimeout(millis);
		}
	}

	@Override
	protected void onInit() {
		this.taskExecutor.setThreadNamePrefix("meetup-rsvp-" + instance.incrementAndGet() + "-");
		this.taskExecutor.initialize();
	}

	@Override
	protected void doStart() {
		synchronized (this.monitor) {
			if (this.running.get()) {
				// already running
				return;
			}
			this.running.set(true);
			this.taskExecutor.execute(new StreamReadingTask());
		}
	}

	@Override
	protected void doStop() {
		this.running.set(false);
		this.taskExecutor.getThreadPoolExecutor().shutdownNow();
		try {
			if (!this.taskExecutor.getThreadPoolExecutor().awaitTermination(10, TimeUnit.SECONDS)) {
				logger.error("Reader task failed to stop");
			}
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}


	protected abstract String buildUri();

	protected abstract void doSendLine(String line);

	protected class StreamReadingTask implements Runnable {

		@Override
		public void run() {
			while (running.get()) {
				try {
					readStream(restTemplate);
				}
				catch (HttpStatusCodeException sce) {

					if (420 == sce.getStatusCode().value()) {
						waitRateLimitBackoff();
					}
					else {
						waitHttpErrorBackoff(sce);
					}
				}
				catch (Exception e) {
					logger.warn("Exception while reading stream.", e);
					waitLinearBackoff();
				}
			}
		}

		private void readStream(RestTemplate restTemplate) {
			restTemplate.execute(buildUri(), HttpMethod.GET, new RequestCallback() {

				@Override
				public void doWithRequest(ClientHttpRequest request) throws IOException {
				}
			},
					new ResponseExtractor<String>() {

						@Override
						public String extractData(ClientHttpResponse response) throws IOException {
							InputStream inputStream = response.getBody();
							LineNumberReader reader = null;
							try {
								reader = new LineNumberReader(new InputStreamReader(inputStream));
								resetBackOffs();
								while (running.get()) {
									String line = reader.readLine();
									if (!StringUtils.hasText(line)) {
										break;
									}
									doSendLine(line);
								}
							}
							finally {
								if (reader != null) {
									reader.close();
								}
							}

							return null;
						}
					}
					);
		}
	}

	private ClientHttpRequestFactory getRequestFactory() {
		// InterceptingClientHttpRequestFactory doesn't let us access the underlying object
		DirectFieldAccessor f = new DirectFieldAccessor(restTemplate.getRequestFactory());
		Object requestFactory = f.getPropertyValue("requestFactory");
		return (ClientHttpRequestFactory) requestFactory;
	}
	private void resetBackOffs() {
		linearBackOff.set(250);
		rateLimitBackOff.set(60000);
		httpErrorBackOff.set(5000);
	}

	private void waitLinearBackoff() {
		int millis = linearBackOff.get();
		if (logger.isWarnEnabled()) {
			logger.warn("Exception while reading stream, waiting for " + millis + " ms before restarting");
		}
		wait(millis);
		if (millis < 16000) {
			linearBackOff.set(millis + 250);
		}
	}

	private void waitRateLimitBackoff() {
		int millis = rateLimitBackOff.get();
		if (logger.isWarnEnabled()) {
			logger.warn("Rate limit error, waiting for " + millis / 1000 + " seconds before restarting");
		}
		wait(millis);
		rateLimitBackOff.set(millis * 2);
	}

	private void waitHttpErrorBackoff(HttpStatusCodeException sce) {
		int millis = httpErrorBackOff.get();
		if (logger.isWarnEnabled()) {
			logger.warn("Http error, waiting for " + millis / 1000 + " seconds before restarting", sce);
		}
		wait(millis);
		if (millis < 320000) {
			httpErrorBackOff.set(millis * 2);
		}
	}

	protected void wait(int millis) {
		try {
			Thread.sleep(millis);
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			if (!this.running.get()) {
				// no longer running
				return;
			}
			throw new IllegalStateException(e);
		}
	}

}
