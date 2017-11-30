package com.datastax.springone.services.meetup;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement()
public class Event {

    private String lat;
    private String lon;
    private String event_url;


    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getEvent_url() {
        return event_url;
    }

    public void setEvent_url(String event_url) {
        this.event_url = event_url;
    }
}
