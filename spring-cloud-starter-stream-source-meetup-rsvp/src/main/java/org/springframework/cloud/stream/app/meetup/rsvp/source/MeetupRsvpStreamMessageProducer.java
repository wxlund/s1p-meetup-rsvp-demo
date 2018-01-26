/*
 * Copyright 2015-2016 the original author or authors.
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

package org.springframework.cloud.stream.app.meetup.rsvp.source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

/**
 * {@link org.springframework.integration.core.MessageProducer} implementation to send meetup rsvp stream messages.
 *
 * @author Ilayaperumal Gopinathan
 * @author Gary Russell
 * @author Wayne Lund
 * Renamed the class from TwitterStream to MeetupRsvp and changed the API_URL_BASE
 */
class MeetupRsvpStreamMessageProducer extends AbstractMeetupRsvpInboundChannelAdapter {

    @Autowired
    Environment environment;

    private final String API_URL_BASE = "http://stream.meetup.com/2/open_events";

    private final MeetupRsvpStreamProperties meetupRsvpStreamProperties;
    private final RestTemplate restTemplate;

    MeetupRsvpStreamMessageProducer(RestTemplate restTemplate, MeetupRsvpStreamProperties meetupRsvpStreamProperties) {
        this.restTemplate = restTemplate;
        this.meetupRsvpStreamProperties = meetupRsvpStreamProperties;
    }

    @Override
    protected String buildUri() {

        String url = API_URL_BASE;
        //TODO: Support all the available properties
        /*if (StringUtils.hasText(this.meetupRsvpStreamProperties.getLanguage())) {
			b.queryParam("language", this.meetupRsvpStreamProperties.getLanguage());
		}*/
        return url;
    }

    @Override
    protected void doSendLine(String line) {
        System.out.println("\n Result Run \n" + line);
        sendMessage(org.springframework.integration.support.MessageBuilder.withPayload(line).build());
    }

}
