package com.datastax.springone.http;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Created by scotthendrickson on 10/21/17.
 */
public class MeetupHttpClient {


    public final static void main(String[] args) throws Exception {

        CloseableHttpResponse response1 = null;

        CloseableHttpClient httpclient = HttpClients.createDefault();;

        try {

            HttpGet httpGet = new HttpGet("http://stream.meetup.com/2/open_events");
            //HttpGet httpGet = new HttpGet("http://httpbin.org/");
            //HttpGet httpGet = new HttpGet("http://stream.meetup.com/2/rsvps");
            System.out.println("Executing request " + httpGet.getRequestLine());
            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                @Override
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    System.out.println("status "+status);
                    if (status >= 200 && status < 300) {
                        System.out.println("getting entity");
                        HttpEntity entity = response.getEntity();
                        System.out.println("got entity "+entity.getContentLength()+" "+entity.getContentType()+" "+entity.isStreaming());
                        InputStream instream = entity.getContent();
                        FileOutputStream output = new FileOutputStream("/tmp/meetup.json");
                        try {
                            int l;
                            byte[] tmp = new byte[2048];
                            while ( (l = instream.read(tmp)) != -1 ) {
                                output.write(tmp, 0, l);
                            }
                        } finally {
                            output.close();
                            instream.close();
                        }
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            System.out.println("----------------------------------------");
            String responseBody = httpclient.execute(httpGet, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);


        } finally {
            response1.close();
            httpclient.close();


        }

        //testResults.add("HTTP call to Solr console PASSED");


        //Cluster cluster;
        //Session session;
        // Connect to the cluster and keyspace "demo"
        //cluster = Cluster.builder().addContactPoint(dseip).withCredentials("cassandra", pword).build();
/*
        session = cluster.connect();
        ResultSet results = session.execute("select * from test_keyspace.colors where solr_query='color:yellow';");
        int c = results.getAvailableWithoutFetching();
        if (c > 0) {
            testResults.add("Solr query Passed");
        }
        */
    }


}
