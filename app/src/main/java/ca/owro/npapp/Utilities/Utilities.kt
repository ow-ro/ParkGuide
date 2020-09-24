package ca.owro.npapp.Utilities

import android.util.Log
import ca.owro.npapp.R

class UtilitiesManager {

    companion object ResourceManager {

        fun getWeatherApiURL(lat: Double, lon: Double, unit: String): String {
            //units: imperial = F, metric = C, default = K
            val url = "https://api.openweathermap.org/data/2.5/onecall?lat=$lat&lon=$lon&units=$unit&exclude=hourly,current,minutely&appid=$WEATHER_API_KEY"
            Log.d("UtilitiesManager","Returned weather url: $url")

            return url
        }

        fun getNPSAlertApiURL(code: String) : String {
            val url = "https://developer.nps.gov/api/v1/alerts?parkCode=$code&api_key=$NPS_API_KEY"
            Log.d("UtilitiesManager","Returned nps url: $url")

            return url
        }

        fun getWeatherIconURL(iconCode: String) : String {
            val url = "https://openweathermap.org/img/wn/$iconCode@4x.png"
            Log.d("UtilitiesManager","Returned weather icon url: $url")

            return url
        }

        fun getAvatarImageUrl(url:String) : String {
            var modifiedUrl = url

            if (url.contains("google")) {
                modifiedUrl = url.replace("s96-c","s400-c")
            } else if (url.contains("facebook")) {
                modifiedUrl = "$url?height=400"
            }

            return modifiedUrl
        }

        fun getIconResourceId(id: Int, type: String): Int {

            if (type == BIKE_FRIENDLY) {

                when (id) {
                    1 -> return R.drawable.ic_bike_1
                    2 -> return R.drawable.ic_bike_2
                    3 -> return R.drawable.ic_bike_3
                }

            } else if (type == PET_FRIENDLY) {

                when (id) {
                    1 -> return R.drawable.ic_pet_1
                    2 -> return R.drawable.ic_pet_2
                    3 -> return R.drawable.ic_pet_3
                }

            } else if (type == COST) {
                when (id) {
                    1 -> return R.drawable.ic_cash_1
                    2 -> return R.drawable.ic_cash_2
                    3 -> return R.drawable.ic_cash_3
                }
            }

            return -1

        }

        fun getActivityOrTerrainResourceId(activityOrTerrain: String): Int {

            if (activityOrTerrain in ACTIVITY_LIST) {

                when (activityOrTerrain) {
                    "Bicycling" -> return R.drawable.ic_activity_bike
                    "Birdwatching" -> return R.drawable.ic_activity_birdwatching
                    "Boating" -> return R.drawable.ic_ferry_boat
                    "Kayaking" -> return R.drawable.ic_activity_kayak
                    "Climbing" -> return R.drawable.ic_activity_climber
                    "Fishing" -> return R.drawable.ic_activity_fish
                    "Hiking" -> return R.drawable.ic_activity_hike
                    "Horse Riding" -> return R.drawable.ic_activity_horse
                    "Swimming" -> return R.drawable.ic_activity_swimming
                    "Tidepooling" -> return R.drawable.ic_activity_tidepool
                    "XC Skiing" -> return R.drawable.ic_activity_xcski
                    "Snowshoeing" -> return R.drawable.ic_activity_snowshoes
                    "Skidooing" -> return R.drawable.ic_activity_snowmob
                    "Ice Fishing" -> return R.drawable.ic_activity_icefish
                    "Dog Sledding" -> return R.drawable.ic_activity_dogsled
                    "Auto Touring" -> return R.drawable.ic_activity_auto_touring
                    "Backpacking" -> return R.drawable.ic_activity_backpacking
                    "Canyoneering" -> return R.drawable.ic_activity_canyoneering
                    "Stargazing" -> return R.drawable.ic_activity_stargazing
                    "Snorkeling" -> return R.drawable.ic_diving
                    "Whale Views" -> return R.drawable.ic_whale
                    "Surfing" -> return R.drawable.ic_surfing
                    "Scuba Diving" -> return R.drawable.ic_scuba
                }

            } else if (activityOrTerrain in TERRAIN_LIST) {

                when (activityOrTerrain) {
                    "Beaches" -> return R.drawable.ic_terrain_beach
                    "Coasts" -> return R.drawable.ic_terrain_coast
                    "Forests" -> return R.drawable.ic_terrain_forest
                    "Lakes" -> return R.drawable.ic_terrain_lake
                    "Ponds" -> return R.drawable.ic_terrain_pond
                    "Mountains" -> return R.drawable.ic_terrain_mountain
                    "Wetlands" -> return R.drawable.ic_terrain_swamp
                    "Deserts" -> return R.drawable.ic_desert
                    "Red Rocks" -> return R.drawable.ic_red_rock
                    "Sandstone" -> return R.drawable.ic_sandstone
                    "Reefs" -> return R.drawable.ic_coral
                    "Bays" -> return R.drawable.ic_sea
                    "Caves" -> return R.drawable.ic_cave
                    "Dunes" -> return R.drawable.ic_dune
                    "Volcanoes" -> return R.drawable.ic_volcano
                    "Glaciers" -> return R.drawable.ic_glacier
                    "Canyons" -> return R.drawable.ic_canyon
                    "Prairies" -> return R.drawable.ic_prairie
                    "Hot Springs" -> return R.drawable.ic_sauna
                }

            }

            return -1

        }

        // Map quick fetch park details
        fun getParkPopularityFromTitle(title: String): Int {
            when (title) {
                "Acadia" -> return 1
                "American Samoa" -> return 5
                "Arches" -> return 2
                "Badlands" -> return 3
                "Big Bend" -> return 4
                "Biscayne" -> return 4
                "Black Canyon Of The Gunnison" -> return 4
                "Bryce Canyon" -> return 2
                "Canyonlands" -> return 3
                "Capitol Reef" -> return 2
                "Carlsbad Caverns" -> return 4
                "Channel Islands" -> return 4
                "Congaree" -> return 5
                "Crater Lake" -> return 3
                "Cuyahoga Valley" -> return 2
                "Death Valley" -> return 2
                "Denali" -> return 3
                "Dry Tortugas" -> return 5
                "Everglades" -> return 3
                "Gates Of The Arctic" -> return 5
                "Gateway Arch" -> return 2
                "Glacier" -> return 1
                "Glacier Bay" -> return 3
                "Grand Canyon" -> return 1
                "Grand Teton" -> return 1
                "Great Basin" -> return 5
                "Great Sand Dunes" -> return 4
                "Great Smoky Mountains" -> return 1
                "Guadalupe Mountains" -> return 4
                "Haleakalā" -> return 2
                "Hawai'i Volcanoes" -> return 2
                "Hot Springs" -> return 2
                "Indiana Dunes" -> return 2
                "Isle Royale" -> return 5
                "Joshua Tree" -> return 1
                "Katmai" -> return 5
                "Kenai Fjords" -> return 4
                "Kings Canyon" -> return 4
                "Kobuk Valley" -> return 5
                "Lake Clark" -> return 5
                "Lassen Volcanic" -> return 4
                "Mammoth Cave" -> return 4
                "Mesa Verde" -> return 3
                "Mount Rainier" -> return 2
                "North Cascades" -> return 5
                "Olympic" -> return 1
                "Petrified Forest" -> return 3
                "Pinnacles" -> return 4
                "Redwood" -> return 4
                "Rocky Mountain" -> return 1
                "Saguaro" -> return 3
                "Sequoia" -> return 3
                "Shenandoah" -> return 2
                "Theodore Roosevelt" -> return 3
                "Virgin Islands" -> return 5
                "Voyageurs" -> return 4
                "White Sands" -> return 3
                "Wind Cave" -> return 3
                "Wrangell-St. Elias" -> return 5
                "Yellowstone" -> return 1
                "Yosemite" -> return 1
                "Zion" -> return 1
            }

            return -1
        }
        fun getParkDocIdFromTitle(title: String): String {
            when (title) {
                "Acadia" -> return "acadia"
                "American Samoa" -> return "americanSamoa"
                "Arches" -> return "arches"
                "Badlands" -> return "badlands"
                "Big Bend" -> return "bigBend"
                "Biscayne" -> return "biscayne"
                "Black Canyon Of The Gunnison" -> return "blackCanyonOfTheGunnison"
                "Bryce Canyon" -> return "bryceCanyon"
                "Canyonlands" -> return "canyonlands"
                "Capitol Reef" -> return "capitolReef"
                "Carlsbad Caverns" -> return "carlsbadCaverns"
                "Channel Islands" -> return "channelIslands"
                "Congaree" -> return "congaree"
                "Crater Lake" -> return "craterLake"
                "Cuyahoga Valley" -> return "cuyahogaValley"
                "Death Valley" -> return "deathValley"
                "Denali" -> return "denali"
                "Dry Tortugas" -> return "dryTortugas"
                "Everglades" -> return "everglades"
                "Gates Of The Arctic" -> return "gatesOfTheArctic"
                "Gateway Arch" -> return "gatewayArch"
                "Glacier" -> return "glacier"
                "Glacier Bay" -> return "glacierBay"
                "Grand Canyon" -> return "grandCanyon"
                "Grand Teton" -> return "grandTeton"
                "Great Basin" -> return "greatBasin"
                "Great Sand Dunes" -> return "greatSandDunes"
                "Great Smoky Mountains" -> return "greatSmokyMountains"
                "Guadalupe Mountains" -> return "guadalupeMountains"
                "Haleakalā" -> return "haleakala"
                "Hawai'i Volcanoes" -> return "hawaiiVolcanoes"
                "Hot Springs" -> return "hotSprings"
                "Indiana Dunes" -> return "indianaDunes"
                "Isle Royale" -> return "isleRoyale"
                "Joshua Tree" -> return "joshuaTree"
                "Katmai" -> return "katmai"
                "Kenai Fjords" -> return "kenaiFjords"
                "Kings Canyon" -> return "kingsCanyon"
                "Kobuk Valley" -> return "kobukValley"
                "Lake Clark" -> return "lakeClark"
                "Lassen Volcanic" -> return "lassenVolcanic"
                "Mammoth Cave" -> return "mammothCave"
                "Mesa Verde" -> return "mesaVerde"
                "Mount Rainier" -> return "mountRainier"
                "North Cascades" -> return "northCascades"
                "Olympic" -> return "olympic"
                "Petrified Forest" -> return "petrifiedForest"
                "Pinnacles" -> return "pinnacles"
                "Redwood" -> return "redwood"
                "Rocky Mountain" -> return "rockyMountain"
                "Saguaro" -> return "saguaro"
                "Sequoia" -> return "sequoia"
                "Shenandoah" -> return "shenandoah"
                "Theodore Roosevelt" -> return "theodoreRoosevelt"
                "Virgin Islands" -> return "virginIslands"
                "Voyageurs" -> return "voyageurs"
                "White Sands" -> return "whiteSands"
                "Wind Cave" -> return "windCave"
                "Wrangell-St. Elias" -> return "wrangellStElias"
                "Yellowstone" -> return "yellowstone"
                "Yosemite" -> return "yosemite"
                "Zion" -> return "zion"
            }

            return ""
        }
        fun getParkLocationFromTitle(title: String): String {
            when (title) {
                "Acadia" -> return "Maine, USA"
                "American Samoa" -> return "American Samoa"
                "Arches" -> return "Utah, USA"
                "Badlands" -> return "South Dakota, USA"
                "Big Bend" -> return "Texas, USA"
                "Biscayne" -> return "Florida, USA"
                "Black Canyon Of The Gunnison" -> return "Colorado, USA"
                "Bryce Canyon" -> return "Utah, USA"
                "Canyonlands" -> return "Utah, USA"
                "Capitol Reef" -> return "Utah, USA"
                "Carlsbad Caverns" -> return "New Mexico, USA"
                "Channel Islands" -> return "California, USA"
                "Congaree" -> return "South Carolina, USA"
                "Crater Lake" -> return "Oregon, USA"
                "Cuyahoga Valley" -> return "Ohio, USA"
                "Death Valley" -> return "California, USA"
                "Denali" -> return "Alaska, USA"
                "Dry Tortugas" -> return "Florida, USA"
                "Everglades" -> return "Florida, USA"
                "Gates Of The Arctic" -> return "Alaska, USA"
                "Gateway Arch" -> return "Missouri, USA"
                "Glacier" -> return "Montana, USA"
                "Glacier Bay" -> return "Alaska, USA"
                "Grand Canyon" -> return "Arizona, USA"
                "Grand Teton" -> return "Wyoming, USA"
                "Great Basin" -> return "Nevada, USA"
                "Great Sand Dunes" -> return "Colorado, USA"
                "Great Smoky Mountains" -> return "TN & NC, USA"
                "Guadalupe Mountains" -> return "Texas, USA"
                "Haleakalā" -> return "Hawaii, USA"
                "Hawai'i Volcanoes" -> return "Hawaii, USA"
                "Hot Springs" -> return "Arkansas, USA"
                "Indiana Dunes" -> return "Indiana, USA"
                "Isle Royale" -> return "Michigan, USA"
                "Joshua Tree" -> return "California, USA"
                "Katmai" -> return "Alaska, USA"
                "Kenai Fjords" -> return "Alaska, USA"
                "Kings Canyon" -> return "California, USA"
                "Kobuk Valley" -> return "Alaska, USA"
                "Lake Clark" -> return "Alaska, USA"
                "Lassen Volcanic" -> return "California, USA"
                "Mammoth Cave" -> return "Kentucky, USA"
                "Mesa Verde" -> return "Colorado, USA"
                "Mount Rainier" -> return "Washington, USA"
                "North Cascades" -> return "Washington, USA"
                "Olympic" -> return "Washington, USA"
                "Petrified Forest" -> return "Arizona, USA"
                "Pinnacles" -> return "California, USA"
                "Redwood" -> return "California, USA"
                "Rocky Mountain" -> return "Colorado, USA"
                "Saguaro" -> return "Arizona, USA"
                "Sequoia" -> return "California, USA"
                "Shenandoah" -> return "Virginia, USA"
                "Theodore Roosevelt" -> return "North Dakota, USA"
                "Virgin Islands" -> return "US Virgin Islands"
                "Voyageurs" -> return "Minnesota, USA"
                "White Sands" -> return "New Mexico, USA"
                "Wind Cave" -> return "South Dakota, USA"
                "Wrangell-St. Elias" -> return "Alaska, USA"
                "Yellowstone" -> return "WY, ID & MT, USA"
                "Yosemite" -> return "California, USA"
                "Zion" -> return "Utah, USA"
            }
            return ""
        }

    }

}