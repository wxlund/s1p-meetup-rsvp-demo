/**
 * Created by wlund on 6/13/17.
 */
import groovy.json.*

def payload = '''
{"visibility":"public","response":"yes","guests":0,"member":{"member_id":215263647,"photo":"https:\\/\\/secure.meetupstatic.com\\/photos\\/member\\/d\\/7\\/f\\/e\\/thumb_261175294.jpeg","member_name":"Jorge Teixeira"},"rsvp_id":1696119123,"mtime":1509142003000,"event":{"event_name":"HALLOWEEN PARTY 1st $1000 to WINNER TOURN SP #91 (NOV11) $60 BuyinW\\/10K D\\/O","event_id":"bsnswnywnbkc","time":1509147000000,"event_url":"https:\\/\\/www.meetup.com\\/TORONTO-ACES-POKER-CLUB-GROUP\\/events\\/244375874\\/"},"group":{"group_topics":[{"urlkey":"adventure","topic_name":"Adventure"},{"urlkey":"nightlife","topic_name":"Nightlife"},{"urlkey":"fun-times","topic_name":"Fun Times"},{"urlkey":"social","topic_name":"Social"},{"urlkey":"poker","topic_name":"Poker"},{"urlkey":"poker-home-games","topic_name":"Poker Home Games"},{"urlkey":"no-limit-texas-hold-em","topic_name":"No Limit Texas Hold 'em"},{"urlkey":"cardgame","topic_name":"Card Games"},{"urlkey":"texas-hold-em","topic_name":"Texas Hold 'em"},{"urlkey":"poker-sit-n-gos","topic_name":"Poker Sit-n-Go's"},{"urlkey":"texas-holdem-poker-tournaments","topic_name":"Texas Hold 'em Tournaments"},{"urlkey":"poker-tournaments","topic_name":"Poker Tournaments"}],"group_city":"Scarborough","group_country":"ca","group_id":2520662,"group_name":"Toronto Ontario Aces Poker Club","group_lon":-79.27,"group_urlname":"TORONTO-ACES-POKER-CLUB-GROUP","group_state":"ON","group_lat":43.7}}
'''
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

println "######################"
println jsonVenue
println payload

if (jsonVenue.group == null || jsonVenue.venue == null) {
    println "payload is missing group or venue data for input to csql"
    return false
} else {
    println "payload is good"
    return true
}


