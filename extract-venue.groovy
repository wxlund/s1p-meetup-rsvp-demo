/**
 * Created by wlund on 6/13/17.
 */
import groovy.json.*

/*
def payload = '''
   {
  	"utc_offset": -18000000,
  	"venue": {
  		"zip": "33004",
  		"country": "us",
  		"city": "Dania Beach",
  		"phone": "9547627492",
  		"address_1": "649 East Dania Beach BLvd",
  		"name": "I Love Yoga Studio& Healing Center",
  		"lon": -80.132904,
  		"state": "FL",
  		"lat": 26.052732
  	},
  	"rsvp_limit": 0,
  	"venue_visibility": "public",
  	"visibility": "public",
  	"fee": {
  		"description": "per person",
  		"amount": 10,
  		"currency": "USD"
  	},
  	"maybe_rsvp_count": 0,
  	"mtime": 1483633861000,
  	"event_url": "https://www.meetup.com/ILoveYoga/events/xxlwkmywpbzb/",
  	"yes_rsvp_count": 1,
  	"payment_required": "1",
  	"name": "Sound Healing",
  	"id": "xxlwkmywpbzb",
  	"time": 1511141400000,
  	"group": {
  		"join_mode": "open",
  		"country": "us",
  		"city": "Dania",
  		"name": "I Love Yoga Studio and Healing Center",
  		"group_lon": -80.15,
  		"id": 15460882,
  		"state": "FL",
  		"urlname": "ILoveYoga",
  		"category": {
  			"name": "health/wellbeing",
  			"id": 14,
  			"shortname": "health-wellbeing"
  		},
  		"group_photo": {
  			"highres_link": "https://secure.meetupstatic.com/photos/event/d/4/1/7/highres_459594295.jpeg",
  			"photo_link": "https://secure.meetupstatic.com/photos/event/d/4/1/7/600_459594295.jpeg",
  			"photo_id": 459594295,
  			"thumb_link": "https://secure.meetupstatic.com/photos/event/d/4/1/7/thumb_459594295.jpeg"
  		},
  		"group_lat": 26.05
  	},
  	"status": "upcoming"
  } 
'''
*/

def jsonVenue = new JsonSlurper().parseText(payload)

//println jsonVenue.venue.venue_name
def venueName = jsonVenue.venue.venue_name
println venueName

def meetupPayload = new JsonBuilder()
meetupPayload {
    mid(jsonVenue.group.id)
    address_1(jsonVenue.venue.address_1)
    city(jsonVenue.venue.city)
    country(jsonVenue.venue.country)
    description(jsonVenue.fee.description)
    duration(jsonVenue.venue.lon)
    event_url(jsonVenue.event_url)
    geo(jsonVenue.name)
    lat(jsonVenue.group.lat)
    lon(jsonVenue.group.lon)
    mtime(jsonVenue.time)
    name(jsonVenue.name)
    state(jsonVenue.venue.state)
    utc_offset(jsonVenue.utc_offset)
    venue_name(jsonVenue.venue.name)
    zip(jsonVenue.venue.zip)
}

println meetupPayload.toString()
return meetupPayload.toString()


