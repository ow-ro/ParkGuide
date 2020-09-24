package ca.owro.npapp.Utilities

import android.util.Log
import ca.owro.npapp.Activities.NPListActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_nplist.*
import java.text.SimpleDateFormat

// OpenWeather api details
const val WEATHER_API_KEY = "fe719f3a4f2b198920df9ec4e4138e4c"

//NPS API details
const val NPS_API_KEY = "qZw79udyPUf8hYX3q9U4m2PfsfDKQ1c1PRYe1uej"

// Collection References

const val USERS_REF = "users"
const val PARKS_REF = "parks"
const val TRIPS_REF = "trips"
const val VISITED_REF = "visited"
const val WISHLIST_REF = "wishlist"
const val HIDDEN_GEMS_REF = "hidden_gems"
const val HIGHLIGHTS_REF = "highlights"
const val LAST_UPDATED_REF = "last_updated"
const val USER_SETTINGS_REF = "userSettings"
const val TRIP_LEGS_REF = "legs"

// Park fields

const val NAME = "name"
const val DESCRIPTION = "description"
const val DIRECTIONS = "directions"
const val BANNER_IMAGE = "bannerImage"
const val LIST_IMAGE = "listImage"
const val LOCATION = "location"
const val ADDRESS = "address"
const val ACTIVITIES = "activities"
const val TERRAIN = "terrain"
const val BIKE_FRIENDLY = "bikeFriendly"
const val PET_FRIENDLY = "petFriendly"
const val COST = "cost"
const val TRIP_LENGTH = "tripLength"
const val VISIT = "whenToVisit"
const val REMOTE = "isRemote"
const val POPULARITY = "popularity"
const val FUN_FACT = "funFact"
const val LAT_LON = "latLon"
const val LINKS = "links"
const val CAR = "car"
const val PRESERVE = "preserve"
const val CODE = "code"

// TRIP FIELDS
const val DRIVE_TIME = "driveTime"
const val GRADIENT = "gradient"
const val LENGTH = "length"
const val MONTHS = "months"
const val PARK_COUNT = "parkCount"
const val START = "start"
const val PARKS = "parks"

// TRIP LEG FIELDS
const val ORDER = "order"
const val TIME = "time"

// Hidden gem/highlight fields

const val PRIORITY = "priority"
const val TAGLINE = "tagline"
const val IMAGE = "image"
const val LINK = "link"

// Last updated fields
const val PARKDATA_DOC_REF = "parkData"
const val TIMESTAMP = "timestamp"

// Users fields

const val DISPLAY_NAME = "displayName"
const val DATE_CREATED = "createdDate"
const val AUTH_PROVIDER = "authProvider"
const val EMAIL = "email"
const val LAST_LOGIN = "lastLogin"
const val USER_TEMP = "temperatureUnit"

// Activities

val ACTIVITY_LIST =
        arrayListOf("Bicycling","Birdwatching","Boating","Climbing","Fishing","Hiking",
                    "Horse Riding","Swimming","Tidepooling","XC Skiing","Snowshoeing",
                    "Skidooing","Ice Fishing","Dog Sledding","Auto Touring","Backpacking",
                    "Canyoneering","Stargazing","Kayaking","Snorkeling","Whale Views","Surfing",
                    "Scuba Diving")

// Terrains

val TERRAIN_LIST =
        arrayListOf("Beaches","Coasts","Forests","Lakes","Ponds","Mountains","Wetlands",
                "Deserts","Red Rocks","Sandstone","Reefs","Bays","Caves","Dunes","Volcanoes",
                "Glaciers","Canyons","Prairies","Hot Springs")

// Park ID list

val PARK_ID_LIST =
        arrayListOf("acadia","americanSamoa","arches","badlands","bigBend","biscayne","blackCanyonOfTheGunnison","bryceCanyon","canyonlands",
                    "capitolReef","carlsbadCaverns","channelIslands","congaree","craterLake","cuyahogaValley","deathValley","denali","dryTortugas",
                    "everglades","gatesOfTheArctic","gatewayArch","glacier","glacierBay","grandCanyon","grandTeton","greatBasin","greatSandDunes",
                    "greatSmokyMountains","guadalupeMountains","haleakala","hawaiiVolcanoes","hotSprings","indianaDunes","isleRoyale","joshuaTree",
                    "katmai","kenaiFjords","kingsCanyon","kobukValley","lakeClark","lassenVolcanic","mammothCave","mesaVerde","mountRainier","northCascades",
                    "olympic","petrifiedForest","pinnacles","redwood","rockyMountain","saguaro","sequoia","shenandoah","theodoreRoosevelt","virginIslands",
                    "voyageurs","whiteSands","windCave","wrangellStElias","yellowstone","yosemite","zion")

// Park Geodata (for faster Map load)

val PARK_LATLON_DATA_LIST =
            mapOf(
                    "Acadia" to LatLng(44.3195407,-68.4368954), "American Samoa" to LatLng(-14.22865935,-169.8503777),
                    "Arches" to LatLng(38.733082,-109.592514), "American Samoa" to LatLng(-14.22865935,-169.8503777),
                    "Badlands" to LatLng(43.68584846,-102.482942), "Big Bend" to LatLng(29.29817767,-103.2297897),
                    "Biscayne" to LatLng(25.490587,-80.21023851), "Black Canyon Of The Gunnison" to LatLng(38.57779869,-107.7242756),
                    "Bryce Canyon" to LatLng(37.58399144,-112.1826689), "Canyonlands" to LatLng(38.24555783,-109.8801624),
                    "Capitol Reef" to LatLng(38.2821653130533,-111.247048377991), "Carlsbad Caverns" to LatLng(32.14089463,-104.5529688),
                    "Channel Islands" to LatLng(33.98680093,-119.9112735), "Congaree" to LatLng(33.79187523,-80.74867805),
                    "Crater Lake" to LatLng(42.94065854,-122.1338414), "Death Valley" to LatLng(36.48753731,-117.134395),
                    "Denali" to LatLng(63.29777484,-151.0526568), "Dry Tortugas" to LatLng(24.628741,-82.87319),
                    "Everglades" to LatLng(25.37294225,-80.88200301), "Gates Of The Arctic" to LatLng(67.75961636,-153.2917758),
                    "Gateway Arch" to LatLng(40.56536246,-73.9171087), "Glacier Bay" to LatLng(58.80086718,-136.8407579),
                    "Glacier" to LatLng(48.68414678,-113.8009306), "Grand Canyon" to LatLng(36.0001165336017,-112.12151636301),
                    "Grand Teton" to LatLng(43.81853565,-110.7054666), "Great Basin" to LatLng(38.94617378,-114.2579782),
                    "Great Sand Dunes" to LatLng(37.79256812,-105.5919572), "Great Smoky Mountains" to LatLng(35.60116374,-83.50818326),
                    "Guadalupe Mountains" to LatLng(31.92304462,-104.885527), "Haleakalā" to LatLng(20.70693015,-156.1591775),
                    "Hawai'i Volcanoes" to LatLng(19.3355036,-155.4700257), "Hot Springs" to LatLng(34.52414366,-93.06332936),
                    "Indiana Dunes" to LatLng(41.63765525,-87.09647445), "Isle Royale" to LatLng(48.01145819,-88.82780657),
                    "Joshua Tree" to LatLng(33.91418525,-115.8398125), "Katmai" to LatLng(58.62235668,-155.0126574),
                    "Kings Canyon" to LatLng(36.887856,-118.555145), "Kenai Fjords" to LatLng(59.81804414,-150.106502),
                    "Kobuk Valley" to LatLng(67.35631336,-159.2002293), "Lake Clark" to LatLng(60.57405857,-153.55535),
                    "Lassen Volcanic" to LatLng(40.49354575,-121.4075993), "Mammoth Cave" to LatLng(37.19760458,-86.13090198),
                    "Mesa Verde" to LatLng(37.23908345,-108.4624032), "Mount Rainier" to LatLng(46.86075416,-121.7043885),
                    "North Cascades" to LatLng(48.71171756,-121.2069423), "Olympic" to LatLng(47.80392754,-123.6663848),
                    "Petrified Forest" to LatLng(34.98387664,-109.7877678), "Pinnacles" to LatLng(36.49029208,-121.1813607),
                    "Redwood" to LatLng(41.37237268,-124.0318129), "Rocky Mountain" to LatLng(40.3556924,-105.6972879),
                    "Saguaro" to LatLng(32.20909636,-110.7574974), "Sequoia" to LatLng(36.4834552,-118.5865137),
                    "Shenandoah" to LatLng(38.49236644,-78.46907715), "Theodore Roosevelt" to LatLng(47.17777274,-103.4300083),
                    "Virgin Islands" to LatLng(18.34279656,-64.74194451), "Voyageurs" to LatLng(48.48370609,-92.8382913),
                    "White Sands" to LatLng(32.77907858,-106.3333461), "Wind Cave" to LatLng(43.58012365,-103.4394709),
                    "Wrangell-St. Elias" to LatLng(61.4182147,-142.6028439), "Yellowstone" to LatLng(44.59824417,-110.5471695),
                    "Yosemite" to LatLng(37.84883288,-119.5571873), "Zion" to LatLng(37.29839254,-113.0265138)
            )

// TODO: add wishlist & visited fields

// other

const val DOCUMENT_KEY = "docId"


object Utilities {
}