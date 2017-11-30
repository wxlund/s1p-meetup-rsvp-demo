package com.datastax.springone.services.meetup;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;


@Produces(MediaType.APPLICATION_JSON)
@Path("/meetup")
public class MeetupService {
    @Context
    ServletContext context;

    @GET
    @Path("/{geo}")
    public Response getMsg(@PathParam("geo") String geo) {

        String username = "cassandra";
        String password = null;
        String ip = null;
        String keyspace = null;
        List<Event> events = new ArrayList<>();
        Cluster cluster = null;
        Session session = null;

        try {

            password = context.getInitParameter("dsepword");
            ip = context.getInitParameter("dseip");
            keyspace = context.getInitParameter("dsekeyspace");

            // Connect to the cluster and keyspace
            cluster = Cluster.builder().addContactPoint(ip).withCredentials(username.trim(), password.trim()).build();
            session = cluster.connect(keyspace);

            // solr query search for events in FL and CA
            ResultSet results = session.execute("SELECT event_url,lat,lon FROM meetup WHERE  solr_query = '{\"q\": \"*:*\", \"fq\": \"{!bbox sfield=geo pt=" + geo + " d=550}\"}'");
            for (Row row : results) {
                Event event = new Event();
                event.setLat(String.valueOf(row.getDouble("lat")));
                event.setLon(String.valueOf(row.getDouble("lon")));
                event.setEvent_url(row.getString("event_url"));
                events.add(event);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            session.close();
            cluster.close();

        }

        //System.out.println(events.isEmpty());

        GenericEntity<List<Event>> list = new GenericEntity<List<Event>>(events) {
        };

        return Response.status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Headers",
                        "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods",
                        "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .entity(list).build();


    }
}
