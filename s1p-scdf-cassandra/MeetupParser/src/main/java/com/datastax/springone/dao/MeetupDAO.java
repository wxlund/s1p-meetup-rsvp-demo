package com.datastax.springone.dao;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.springone.model.Meetup;

/**
 * Created by scotthendrickson on 10/21/17.
 */
public class MeetupDAO {
    public int insertMeetup(Meetup meetup, String dseip, String pword) {
        Cluster cluster;
        Session session;
        try {

            // Connect to the cluster and keyspace "demo"
            cluster = Cluster.builder().addContactPoint(dseip).withCredentials("cassandra", pword).build();

            session = cluster.connect();

            ResultSet rs =session.execute("INSERT INTO meetups.meetup (utc_offset, geo, lat, lon, country, zip, city, address_1, venue_name,state,description,event_url,duration,name, mid, mtime) "     +
                        "VALUES ('"+ meetup.getUtc_offset()+"',"+
                                "'"+meetup.getGeo()+"',"+
                                meetup.getVenue().getLat()+"," +
                                meetup.getVenue().getLon()+"," +
                                "'"+meetup.getVenue().getCountry()+"',"+
                                "'"+meetup.getVenue().getZip()+"',"+
                                "'"+meetup.getVenue().getCity()+"',"+
                                "'"+meetup.getVenue().getAddress_1()+"',"+
                                "'"+meetup.getVenue().getName()+"',"+
                                "'"+meetup.getVenue().getState()+"',"+
                                "'"+meetup.getDescription()+"',"+
                                "'"+meetup.getEventUrl()+"',"+
                                meetup.getDuration()+","+
                                "'"+meetup.getName()+"',"+
                                meetup.getMid()+","+
                                meetup.getMtime()+")");


            session.close();
            cluster.close();

        } catch (Exception e) {
            e.printStackTrace();
            return (-1);
        }
        return  (0);

    }
}
