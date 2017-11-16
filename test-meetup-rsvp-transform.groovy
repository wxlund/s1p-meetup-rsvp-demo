/**
 * Created by wlund on 6/13/17.
 */
import groovy.json.*

def payload1 = '''{"utc_offset":-18000000,"venue":{"zip":"10013","country":"US","city":"New York","address_1":"145 Mulberry Street","name":"The Producer Table","lon":-73.997734,"lat":40.718994},"rsvp_limit":0,"venue_visibility":"public","visibility":"public","maybe_rsvp_count":0,"description":"<p>Join us for another round of dinner with friends and film. <b>Driving Miss Daisy<\\/b>&nbsp;is a 1989 American&nbsp;comedy-drama&nbsp;film directed by&nbsp;Bruce Beresford. This film won many Academy Awards including \\"Best Picture.\\"<\\/p>\\n<p>\\"An old Jewish woman and her African-American chauffeur in the American South have a relationship that grows and improves over the years.\\"&nbsp;<b>IMDB<\\/b><br><\\/p>\\n<p>Arrive at 6pm and join us for an Italian meal. Go into Capri and head downstairs to find The Producer Table, which will be all ours for dinner. We'll seat everyone at a table with new faces to chat and get to know each other. The film starts at 7:40pm.<\\/p>\\n<p>The screening is absolutely free, however we kindly encourage everyone to patronize the bar to support this venue.&nbsp;<\\/p>\\n<p>See you soon!<\\/p>\\n<p>Rory [masked]<\\/p>\\n<p><a class=\\"embedded\\" href=\\"https:\\/\\/www.youtube.com\\/watch?v=TQ3wXC5jqKE\\">https:\\/\\/www.youtube.com\\/watch?v=TQ3wXC5jqKE<\\/a><\\/p> \\n<p><img src=\\"https:\\/\\/lh6.googleusercontent.com\\/xmSe5GjCy6FYTkmabUBcEnmaXy_RRT-bZ2W3lj5mzob0GeOhDPXx6CSxm32uBh1IFAYNL4vcaJ22Y-KDUG_CnpwCxPy5UZwrLje0IrP15kTxIyRiMBA_XNjqG7UthTdHAKwYUwFk\\" width=\\"308\\" height=\\"431\\"><br><br><br><\\/p>\\n<br>","mtime":1509741529331,"event_url":"https:\\/\\/www.meetup.com\\/Film-Club-NYC\\/events\\/244796031\\/","yes_rsvp_count":1,"payment_required":"0","name":"Dinner and Film:  Driving Miss Daisy","id":"244796031","time":1510441200000,"group":{"join_mode":"open","country":"us","city":"New York","name":"Film Club NYC","group_lon":-73.99,"id":23567920,"state":"NY","urlname":"Film-Club-NYC","category":{"name":"movies\\/film","id":20,"shortname":"movies-film"},"group_photo":{"highres_link":"https:\\/\\/secure.meetupstatic.com\\/photos\\/event\\/1\\/b\\/f\\/6\\/highres_460627158.jpeg","photo_link":"https:\\/\\/secure.meetupstatic.com\\/photos\\/event\\/1\\/b\\/f\\/6\\/600_460627158.jpeg","photo_id":460627158,"thumb_link":"https:\\/\\/secure.meetupstatic.com\\/photos\\/event\\/1\\/b\\/f\\/6\\/thumb_460627158.jpeg"},"group_lat":40.75},"status":"upcoming"}'''
def payload2 = '''{"utc_offset":-18000000,"rsvp_limit":0,"venue_visibility":"public","visibility":"public","maybe_rsvp_count":0,"mtime":1509741635141,"event_url":"https:\\/\\/www.meetup.com\\/Automation-One\\/events\\/244796116\\/","yes_rsvp_count":0,"payment_required":"0","name":"accusantium16279","id":"244796116","time":1510346435040,"group":{"join_mode":"open","country":"us","city":"New York","name":"Automation One","group_lon":-73.99,"id":26442183,"state":"NY","urlname":"Automation-One","category":{"name":"tech","id":34,"shortname":"tech"},"group_lat":40.75},"status":"upcoming"}'''
testPayloads = [payload1, payload2]


testPayloads.each() { payload ->
    def jsonVenue = new JsonSlurper().parseText(payload)
    def meetupPayload = new JsonBuilder()

    println "######################"
    println payload
    println jsonVenue
    println jsonVenue.group.id
    println jsonVenue.venue == null ? "UNKNOWN ADDRESS" : jsonVenue.venue.address_1
    println Integer.MAX_VALUE
    println "######################"
    meetupPayload {
        mid(jsonVenue.id)
        address_1(jsonVenue.venue == null ? "UNKNOWN ADDRESS" : jsonVenue.venue.address_1)
        city(jsonVenue.venue == null ? "UNKNOWN CITY" : jsonVenue.venue.city)
        country(jsonVenue.venue == null ? "UNKNOWN COUNTRY" : jsonVenue.venue.country)
        description(jsonVenue.fee == null ? "UNKNOWN DESCRIPTION" : jsonVenue.description)
        duration(jsonVenue.venue == null ? 0.0 : jsonVenue.venue.lon)
        event_url(jsonVenue.event_url)
        geo((jsonVenue.venue == null ? null : jsonVenue.venue.lat) + ',' + (jsonVenue.venue == null ? null : jsonVenue.venue.lon))
        lat(jsonVenue.venue == null ? null : jsonVenue.venue.lat)
        lon(jsonVenue.venue == null ? null : jsonVenue.venue.lon)
        mtime(jsonVenue.time)
        name(jsonVenue.name)
        state(jsonVenue.venue == null ? "UNKNOWN STATE" : jsonVenue.venue.state)
        utc_offset(jsonVenue.utc_offset.toString())
        venue_name(jsonVenue.venue == null ? "UNKNOWN NAME" : jsonVenue.name)
        zip(jsonVenue.venue == null ? "UNKNOWN ZIP" : jsonVenue.venue.zip)
    }
    println meetupPayload.toString()
    return meetupPayload.toString()
}


