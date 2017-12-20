/*
 * Copyright 2016 the original author or authors.
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for meetup-rsvp stream source app.
 *
 * @author Gary Russell
 */

/* Sample data from meetup-rsvp
{"venue":
{"venue_name":"Archers Arena","lon":-79.467308,"lat":43.753609,"venue_id":23892321},"visibility":"public","response":"yes","guests":1,"member":{"member_id":1936372,"photo":"https:\/\/secure.meetupstatic.com\/photos\/member\/a\/9\/8\/7\/thumb_1963399.jpeg","member_name":"Nick"},"rsvp_id":1672114274,"mtime":1496767464097,"event":{"event_name":"Nerf Wars! (Thursday June 22nd)","event_id":"240465167","time":1498172400000,"event_url":"https:\/\/www.meetup.com\/TorontoTNT\/events\/240465167\/"},"group":{"group_topics":[{"urlkey":"newintown","topic_name":"New In Town"},{"urlkey":"self-exploration","topic_name":"Self Exploration"},{"urlkey":"diningout","topic_name":"Dining Out"},{"urlkey":"art","topic_name":"Art"},{"urlkey":"fun-times","topic_name":"Fun Times"},{"urlkey":"esl","topic_name":"English as a Second Language"},{"urlkey":"socialnetwork","topic_name":"Social Networking"},{"urlkey":"foodie","topic_name":"Foodie"},{"urlkey":"adventure","topic_name":"Adventure"},{"urlkey":"exchangestud","topic_name":"International and Exchange Students"},{"urlkey":"film","topic_name":"Film"},{"urlkey":"nightlife","topic_name":"Nightlife"},{"urlkey":"social","topic_name":"Social"},{"urlkey":"self-empowerment","topic_name":"Self-Empowerment"},{"urlkey":"language","topic_name":"Language & Culture"}],"group_city":"Toronto","group_country":"ca","group_id":1822458,"group_name":"Try New Things in Toronto!","group_lon":-79.38,"group_urlname":"TorontoTNT","group_state":"ON","group_lat":43.68}}
{"venue":
{"venue_name":"Pilates Core Center","lon":-74.964447,"lat":39.905388,"venue_id":18906732},"visibility":"public","response":"yes","guests":0,"member":{"member_id":224755189,"photo":"https:\/\/secure.meetupstatic.com\/photos\/member\/e\/5\/a\/3\/thumb_265618787.jpeg","member_name":"Nancy T Peace In The Pack"},"rsvp_id":1672114285,"mtime":1496767464585,"event":{"event_name":"Zen Meditation","event_id":"nlngxmywjbdc","time":1498168800000,"event_url":"https:\/\/www.meetup.com\/Cherry-Hill-Zen-Meditation-Meetup\/events\/240582707\/"},"group":{"group_topics":[{"urlkey":"yoga","topic_name":"Yoga"},{"urlkey":"meditation","topic_name":"Meditation"},{"urlkey":"zen-meditation","topic_name":"Zen Meditation"},{"urlkey":"zen-practice","topic_name":"Zen Practice"},{"urlkey":"zen-buddhism","topic_name":"Zen Buddhism"}],"group_city":"Cherry Hill","group_country":"us","group_id":13300372,"group_name":"Cherry Hill Zen Meditation Meetup","group_lon":-74.97,"group_urlname":"Cherry-Hill-Zen-Meditation-Meetup","group_state":"NJ","group_lat":39.88}}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@SpringBootTest
public abstract class MeetupRsvpSourceIntegrationTests {

    @Autowired
    protected Source meetupRsvpSource;

    @Autowired
    protected MessageCollector messageCollector;

    @Autowired
    protected AtomicReference<URI> uri;

    public static class DefaultPropertiesTests extends MeetupRsvpSourceIntegrationTests {

        @Test
        public void test() throws InterruptedException {
            Message<?> received = messageCollector.forChannel(this.meetupRsvpSource.output()).poll(10, TimeUnit.SECONDS);
            assertThat(received, notNullValue());
            System.out.println("\n Received \n" + received);
            //assertThat((String) received.getPayload(), is(equalTo("foo")));
            /*assertThat(this.uri.get().toString(), containsString("sample"));*/
        }

    }

    @SpringBootTest({"meetup.stream.streamType=FIREHOSE", "meetup.stream.language=english"})
    public static class FireHoseTests extends MeetupRsvpSourceIntegrationTests {

        @Test
        public void test() throws InterruptedException {
            Message<?> received = messageCollector.forChannel(this.meetupRsvpSource.output()).poll(10, TimeUnit.SECONDS);
            assertThat(received, notNullValue());

		/*	assertThat((String) received.getPayload(), is(equalTo("foo")));
			assertThat(this.uri.get().toString(), containsString("firehose"));
			assertThat(this.uri.get().toString(), containsString("language=english"));*/
        }

    }
/*
    @SpringBootApplication
    public static class TestMeetRsvpSourceApplication {

        @SuppressWarnings("unchecked")
        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }


    }*/}



