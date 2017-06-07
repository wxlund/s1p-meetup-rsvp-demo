package org.springframework.cloud.stream.app.meetup.rsvp.source;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootApplication
public class MeetupRsvpApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeetupRsvpApplication.class, args);
    }

    @Bean
    public AtomicReference<URI> uri() {
        return new AtomicReference<URI>();
    }

}
