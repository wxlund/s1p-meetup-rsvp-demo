package com.datastax.springone.json;

import com.datastax.springone.dao.MeetupDAO;
import com.datastax.springone.model.Meetup;
import com.datastax.springone.model.Venue;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Created by scotthendrickson on 10/21/17.
 */
public class MeetupObjectMapper {

    public static void main(String[] args) throws IOException {


        Options options = new Options();

        Option host = new Option("dseip", "dseip", true, "dse host address");
        host .setRequired(true);
        options.addOption(host);

        Option password = new Option("dsepword", "dsepword", true, "dse password");
        password.setRequired(true);
        options.addOption(password);

        Option meetupCnt = new Option("cnt", "cnt", true, "test mapper");
        password.setRequired(true);
        options.addOption(meetupCnt);

        Option webdata = new Option("webdata", "webdata", true, "webdata file");
        password.setRequired(true);
        options.addOption(webdata);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("MeetupObjectMapper", options);

            System.exit(1);
            return;
        }

        String inputHost = cmd.getOptionValue("dseip");
        int inputCnt = Integer.valueOf(cmd.getOptionValue("cnt"));
        String inputPassword = cmd.getOptionValue("dsepword");
        String inputWebdata = cmd.getOptionValue("webdata");

        System.out.println("class args "+inputHost+" "+inputCnt+" "+inputPassword+" "+inputWebdata);


        //testMapper ();

        parseMeetupStream(inputHost,inputPassword,inputCnt,inputWebdata);

}

    private static void testMapper () {
        try {
            //read json file data to String
            byte[] jsonData = Files.readAllBytes(Paths.get("/Users/scotthendrickson/Datastax/meetup-ex.json"));

            //create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            //convert json string to object
            Meetup meetup = objectMapper.readValue(jsonData, Meetup.class);

            System.out.println("Meetup Object\n" + meetup.getVenue().getName() + " " + meetup.getVenue().getAddress_1());

            //convert Object to json string
            Meetup meetup1 = createMeetup();
            //configure Object mapper for pretty print
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

            //writing to console, can write to any output stream such as file
            StringWriter stringMeetup = new StringWriter();
            objectMapper.writeValue(stringMeetup, meetup1);
            System.out.println("Meetup JSON is\n" + stringMeetup);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Meetup createMeetup() {

        Meetup meetup = new Meetup();
        Venue venue = new Venue();
        meetup.setUtc_offset("-18000000");
        meetup.setGeo("37.7749,122.4194");
        venue.setLat(37.7749);
        venue.setLon(122.4194);
        venue.setZip("94103");
        venue.setCountry("us");
        venue.setCity("San Francisco");
        venue.setAddress_1("747 Howard St.");
        meetup.setName("Moscone Center");
        venue.setState("CA");
        meetup.setDescription("<p>Datastax Graph Meetup</p>");
        meetup.setEventUrl("http:\\/\\/www.datastax.com");
        meetup.setName("Graph Meetup");
        meetup.setDuration(216000000);
        meetup.setMid(283282913);
        meetup.setMtime(Long.valueOf("1508589890"));
        meetup.setVenue(venue);


        return meetup;
    }
    private static void parseMeetupStream (String inputHost, String inputPassword,int inputCnt,String inputWebdata) {

        String url = "http://stream.meetup.com/2/open_events";
        Meetup meetup = new Meetup();
        Venue venue = new Venue();
        MeetupDAO meetupDAO = new MeetupDAO();
        int cnt=0;

        // The number of past events the stream will return is limited. Applications that intend to consume all event activity should
        // reconnect within a few seconds to avoid missing event messages.

        try {
            JsonFactory factory = new JsonFactory();
            JsonParser parser = factory.createParser(new URL(url));


            // continue parsing the token till the end of input is reached
            while (!parser.isClosed()) {
                // get the token
                JsonToken token = parser.nextToken();
                // if its the last token then we are done
                if (token == null)
                    break;
                // we want to look for a field that says venue
                //System.out.println(" "+token.name());

                if (JsonToken.START_OBJECT.equals(token)) {
                    if (parser.getCurrentName() == null) {
                        //System.out.println("start of JSON");


                        while (true) {
                            token = parser.nextToken();

                            //System.out.println(" " + token.name() + parser.getCurrentName());
                            if (token == null)
                                break;
                            if (JsonToken.START_OBJECT.equals(token) && "venue".equals(parser.getCurrentName())) {
                                while (true) {
                                    token = parser.nextToken();
                                    if (JsonToken.FIELD_NAME.equals(token) && "country".equals(parser.getCurrentName())) {
                                        token = parser.nextToken();
                                        venue.setCountry(parser.getText());
                                    } else if (JsonToken.FIELD_NAME.equals(token) && "lat".equals(parser.getCurrentName())) {
                                        token = parser.nextToken();
                                        venue.setLat(Double.valueOf(parser.getText()));
                                    } else if (JsonToken.FIELD_NAME.equals(token) && "lon".equals(parser.getCurrentName())) {
                                        token = parser.nextToken();
                                        venue.setLon(Double.valueOf(parser.getText()));
                                    } else if (JsonToken.FIELD_NAME.equals(token) && "state".equals(parser.getCurrentName())) {
                                        token = parser.nextToken();
                                        venue.setState(parser.getText());
                                    } else if (JsonToken.FIELD_NAME.equals(token) && "city".equals(parser.getCurrentName())) {
                                        token = parser.nextToken();
                                        venue.setCity(parser.getText());
                                    } else if (JsonToken.FIELD_NAME.equals(token) && "zip".equals(parser.getCurrentName())) {
                                        token = parser.nextToken();
                                        venue.setZip(parser.getText());
                                    } else if (JsonToken.FIELD_NAME.equals(token) && "address_1".equals(parser.getCurrentName())) {
                                        token = parser.nextToken();
                                        venue.setAddress_1(parser.getText().replace("'", "''"));
                                    } else if (JsonToken.FIELD_NAME.equals(token) && "name".equals(parser.getCurrentName())) {
                                        token = parser.nextToken();
                                        venue.setName(parser.getText().replace("'", "''"));
                                    } else if (JsonToken.END_OBJECT.equals(token)) {
                                        meetup.setGeo(String.valueOf(venue.getLat())+","+String.valueOf(venue.getLon()));
                                        break;
                                    }
                                }

                            }
                            if (JsonToken.FIELD_NAME.equals(token) && "description".equals(parser.getCurrentName())) {
                                token = parser.nextToken();
                                meetup.setDescription(parser.getText().replace("'", "''"));

                            }
                            if (JsonToken.FIELD_NAME.equals(token) && "utc_offset".equals(parser.getCurrentName())) {
                                token = parser.nextToken();
                                meetup.setUtc_offset(parser.getText());

                            }
                            if (JsonToken.FIELD_NAME.equals(token) && "event_url".equals(parser.getCurrentName())) {
                                token = parser.nextToken();
                                meetup.setEventUrl(parser.getText());

                            }
                            if (JsonToken.FIELD_NAME.equals(token) && "duration".equals(parser.getCurrentName())) {
                                token = parser.nextToken();
                                meetup.setDuration(Double.valueOf(parser.getText().replace("'", "''")));

                            }
                            if (JsonToken.FIELD_NAME.equals(token) && "name".equals(parser.getCurrentName())) {
                                token = parser.nextToken();
                                meetup.setName(parser.getText());

                            }
                            if (JsonToken.FIELD_NAME.equals(token) && "id".equals(parser.getCurrentName())) {
                                token = parser.nextToken();
                                if (StringUtils.isNumeric(parser.getText())) {

                                    if (meetup.getMid() == 0)
                                        meetup.setMid(Integer.valueOf(parser.getText()));
                                }

                            }
                            if (JsonToken.FIELD_NAME.equals(token) && "time".equals(parser.getCurrentName())) {
                                token = parser.nextToken();

                                meetup.setMtime(Long.valueOf(parser.getText()));

                            }
                            if (JsonToken.END_OBJECT.equals(token) && parser.getCurrentName() == null) {
                                //System.out.println("end object ");
                                meetup.setVenue(venue);
                                System.out.println(meetup.getMid()+" "+meetup.getVenue().getName()+" "+cnt);
                                //
                                int rc = meetupDAO.insertMeetup(meetup,inputHost,inputPassword);
                                //
                                if (meetup.getVenue().getLat() != 0)
                                   appendToWebAppFile(inputWebdata,meetup);
                                //
                                meetup = new Meetup();
                                venue = new Venue();

                                cnt++;
                                if (cnt > inputCnt)
                                    System.exit(0);

                            }

                        }
                    }

                }

            }


        } catch (java.net.SocketException se) {

            se.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void appendToWebAppFile(String path, Meetup meetup) {
        try {
            File file = new File(path);
            FileWriter fr = new FileWriter(file, true);
            BufferedWriter br = new BufferedWriter(fr);
            br.write(meetup.getName()+","+meetup.getEventUrl()+","+meetup.getVenue().getLat()+","+meetup.getVenue().getLon()+"\n");

            br.close();
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
