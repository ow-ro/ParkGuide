package ca.owro.npapp.Activities

import ZoomOutPageTransformer
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import ca.owro.npapp.Adapters.GridViewAdapter
import ca.owro.npapp.Models.*
import ca.owro.npapp.R
import ca.owro.npapp.Utilities.*
import ca.owro.npapp.Utilities.UtilitiesManager.ResourceManager.getIconResourceId
import ca.owro.npapp.Utilities.UtilitiesManager.ResourceManager.getWeatherIconURL
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.activity_npdetail.*
import kotlinx.android.synthetic.main.activity_npdetail.footerLastUpdatedTxt
import kotlinx.android.synthetic.main.activity_trip_detail.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class NPDetailActivity : AppCompatActivity(), ServerCallback {

    val myServerCallback = this

    private val TAG = "NPDetailActivity"
    lateinit var parkId : String
    lateinit var currentPark: Park

    lateinit var highlightPager: ViewPager2
    lateinit var highlightTab: TabLayout
    lateinit var gemPager: ViewPager2
    lateinit var gemTab: TabLayout

    lateinit var terrainGrid: GridView

    lateinit var highlights: ArrayList<Highlight>
    lateinit var hiddenGems: ArrayList<HiddenGem>

    lateinit var mShimmer: ShimmerFrameLayout
    lateinit var mShimmerLinearLayout: LinearLayout
    lateinit var mScrollView: ScrollView

    lateinit var drawer: DrawerLayout
    lateinit var toolbar: Toolbar

    lateinit var weatherJsonData: JSONObject
    lateinit var weatherJsonArray: JSONArray
    lateinit var weatherList: ArrayList<Weather>
    lateinit var parkAlertJsonData: JSONObject
    lateinit var parkAlertJsonArray: JSONArray
    lateinit var parkAlertList: ArrayList<Alert>

    lateinit var currentUser: FirebaseUser

    lateinit var expandableView : ConstraintLayout
    lateinit var arrowBtn : View
    lateinit var cardView: CardView

    var heartPressed = false
    var checkPressed = false

    var parkLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_npdetail)
        mScrollView = findViewById(R.id.mainScrollView)
        mScrollView.visibility = View.GONE
        parkId = intent.getStringExtra(DOCUMENT_KEY) as String

        currentUser = FirebaseAuth.getInstance().currentUser!!

        highlights = arrayListOf()
        hiddenGems = arrayListOf()
        weatherList = arrayListOf()
        parkAlertList = arrayListOf()

        mShimmer = findViewById(R.id.shimmer_view_container)
        mShimmerLinearLayout = findViewById(R.id.shimmerLinearLayout)

        //Park Alerts
        expandableView = findViewById(R.id.expandableView)
        arrowBtn = findViewById(R.id.parkArrowButton)
        cardView = findViewById(R.id.parkAlertLayout)

        setupNavigationDrawer()

        activitiesGridView.isExpanded = true
        terrainGridView.isExpanded = true

        getParkData()

        arrowBtn.setOnClickListener { view ->
            if (expandableView.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(cardView, AutoTransition())
                expandableView.visibility = View.VISIBLE
                arrowBtn.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            } else {
                TransitionManager.beginDelayedTransition(cardView, AutoTransition())
                expandableView.visibility = View.GONE
                arrowBtn.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            }
        }

    }

    fun setupNavigationDrawer() {
        //Toolbar & navigation drawer
        toolbar = findViewById(R.id.detail_toolbar)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.detail_draw_layout)
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        // User avatar in drawer nav
        var navProfilePhoto = findViewById<NavigationView>(R.id.detail_draw_nav_view).getHeaderView(0).findViewById<RoundedImageView>(R.id.profile_image)
        if (currentUser.photoUrl != null) {
            Glide.with(this).load(UtilitiesManager.getAvatarImageUrl(currentUser.photoUrl.toString())).into(navProfilePhoto)
            navProfilePhoto.scaleType = ImageView.ScaleType.FIT_CENTER
            navProfilePhoto.cornerRadius = 30f
            navProfilePhoto.isOval = true
        }

        detail_draw_nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_park_list -> {
                    val intent = Intent(this, NPListActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_about -> {
                    val intent = Intent(this, AboutActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_park_map -> {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_park_trips -> {
                    val intent = Intent(this, TripsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> {
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START)
                    }
                    false
                }
            }
        }
    }



    private fun fetchWeatherData(currentPark: Park) {

        val docRef = FirebaseFirestore.getInstance().collection(USER_SETTINGS_REF).document(currentUser.uid)
        docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val data = document.data
                        val temp = data?.get(USER_TEMP) as String

                        var unit = "imperial"
                        if (temp == "c") { unit = "metric" }

                        val apiUrl = UtilitiesManager.getWeatherApiURL(currentPark.latitude, currentPark.longitude, unit)
                        var gSon = Gson()

                        val jsonObjReq: JsonObjectRequest = object : JsonObjectRequest(Method.GET,
                                apiUrl, null, Response.Listener { response ->
                            Log.i(TAG, "weatherJsonData: $response")
                            weatherJsonData = response
                            weatherJsonArray = weatherJsonData.getJSONArray("daily")
                            Log.i(TAG, "weatherJsonArray: $weatherJsonArray")

                            weatherList.clear()

                            if (weatherJsonArray.length() >= 5) {
                                for (i in 0 until 5) {
                                    val weatherObj = weatherJsonArray.getJSONObject(i)
                                    val weatherArr = weatherObj.getJSONArray("weather").get(0).toString()

                                    var wth = gSon.fromJson(weatherArr,Weather::class.java)
                                    Log.d(TAG, "WEATHER ITEM: ${wth.main}")
                                    wth.unixDate = weatherObj.getLong("dt")
                                    Log.d(TAG, "WEATHER DATE: ${wth.unixDate}")

                                    wth.farenheit = DecimalFormat("#").format(weatherObj.getJSONObject("temp").getDouble("day")) + (if(temp == "c") "°C" else "°F")


                                    Log.d(TAG, "WEATHER TEMP: ${wth.farenheit}")
                                    weatherList.add(wth)
                                }

                                myServerCallback.onWeatherApiResponse(true)
                            }

                        }, Response.ErrorListener {
                            //VolleyLog.d("Volley error json object ", "Error: " + error.getMessage());
                        }) {
                            override fun getBodyContentType(): String {
                                return "application/json"
                            }
                        }

                        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjReq)


                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Error", "Firestore exception: " + exception.localizedMessage)
                }
    }

    private fun fetchParkAlertData(currentPark: Park) {
        val apiUrl = UtilitiesManager.getNPSAlertApiURL(currentPark.code)
        var gSon = Gson()

        val jsonObjReq: JsonObjectRequest = object : JsonObjectRequest(Method.GET,
                apiUrl, null, Response.Listener { response ->
            Log.i(TAG, "parkAlertJsonData: $response")
            parkAlertJsonData = response
            parkAlertJsonArray = parkAlertJsonData.getJSONArray("data")
            Log.i(TAG, "parkAlertJsonArray: $parkAlertJsonArray")

            parkAlertList.clear()

            if (parkAlertJsonArray.length() > 0) {
                for (i in 0 until parkAlertJsonArray.length()) {
                    val alertObj = parkAlertJsonArray.getJSONObject(i)

                    var alert = gSon.fromJson(alertObj.toString(),Alert::class.java)


                    Log.d(TAG, "Park Alert: $alert")
                    parkAlertList.add(alert)
                }
                myServerCallback.onNPSAlertApiResponse(true)
            }

        }, Response.ErrorListener {
            //VolleyLog.d("Volley error json object ", "Error: " + error.getMessage());
        }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }
        }

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjReq)
    }


    override fun onBackPressed() {
        /*if (highlightPager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            highlightPager.currentItem = highlightPager.currentItem - 1
        }*/
        if (parkLoaded) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
            } else {
                super.onBackPressed()
            }
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private inner class HighlightScreenSlidePagerAdapter(fa: FragmentActivity, val highlights: ArrayList<Highlight>) : FragmentStateAdapter(fa) {

        override fun getItemCount(): Int = highlights.size

        override fun createFragment(position: Int): Fragment = HighlightScreenSlidePageFragment(highlights[position])
    }

    private inner class HiddenGemScreenSlidePagerAdapter(fa: FragmentActivity, val hiddenGems: ArrayList<HiddenGem>) : FragmentStateAdapter(fa) {

        override fun getItemCount(): Int = hiddenGems.size

        override fun createFragment(position: Int): Fragment = HiddenGemScreenSlidePageFragment(hiddenGems[position])
    }

    fun getParkData() {

        val docRef = FirebaseFirestore.getInstance().collection(PARKS_REF).document(parkId)
        docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {

                        val data = document.data
                        val name = data?.get(NAME) as String
                        val desc = data[DESCRIPTION] as String
                        val directions = data[DIRECTIONS] as ArrayList<String>
                        val bannerImage = data[BANNER_IMAGE] as String
                        val listImage = data[LIST_IMAGE] as String
                        val location = data[LOCATION] as String
                        val address = data[ADDRESS] as String
                        val activities = data[ACTIVITIES] as ArrayList<String>
                        val terrain = data[TERRAIN] as ArrayList<String>
                        val bikeFriendly = data[BIKE_FRIENDLY] as Long
                        val petFriendly = data[PET_FRIENDLY] as Long
                        val cost = data[COST] as Long
                        val tripLength = data[TRIP_LENGTH] as String
                        val visit = data[VISIT] as String
                        val popularity = data[POPULARITY] as Long
                        val funFact = data[FUN_FACT] as String
                        val links = data[LINKS] as HashMap<String, String>
                        val car = data[CAR] as Boolean
                        val preserve = data[PRESERVE] as Boolean
                        val code = data[CODE] as String


                        val latLon = data.get(LAT_LON) as GeoPoint
                        Log.d(TAG, "lat: " + latLon.latitude.toString() + " long: " + latLon.longitude.toString())


                            FirebaseFirestore.getInstance().collection(PARKS_REF).document(parkId).collection(HIGHLIGHTS_REF)
                                    .orderBy(PRIORITY, Query.Direction.ASCENDING)
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        highlights.clear()
                                        for (document in documents) {

                                            val data = document.data
                                            val tagline = data[TAGLINE] as String
                                            val link = data[LINK] as String
                                            val image = data[IMAGE] as String
                                            val priority = data[PRIORITY] as Long

                                            val newHighlight = Highlight(document.id, tagline, link, image, priority.toInt())

                                            highlights.add(newHighlight)
                                        }


                                        FirebaseFirestore.getInstance().collection(PARKS_REF).document(parkId).collection(HIDDEN_GEMS_REF)
                                                .orderBy(PRIORITY, Query.Direction.ASCENDING)
                                                .get()
                                                .addOnSuccessListener { documents ->
                                                    hiddenGems.clear()
                                                    for (document in documents) {

                                                        val data = document.data
                                                        val tagline = data[TAGLINE] as String
                                                        val link = data[LINK] as String
                                                        val image = data[IMAGE] as String
                                                        val priority = data[PRIORITY] as Long

                                                        val newGem = HiddenGem(document.id, tagline, link, image, priority.toInt())

                                                        hiddenGems.add(newGem)
                                                    }

                                                    currentPark = Park(document.id, name, desc, listImage, bannerImage, directions, location,
                                                            address, activities, terrain, bikeFriendly.toInt(),
                                                            petFriendly.toInt(), cost.toInt(), tripLength, visit,
                                                            popularity.toInt(), funFact, highlights, hiddenGems,
                                                            latLon.latitude, latLon.longitude, links, car, preserve, code)


                                                    Log.d(TAG, "end of getParkData")
                                                    myServerCallback.onParkDataResponse(true)
                                                    //updateUI(currentPark)

                                                }


                                    }.addOnFailureListener { exception ->
                                        Log.e(TAG, "exception getting highlights: " + exception.localizedMessage)
                                    }



                        /*currentPark = Park(document.id, name, desc, bannerImage, location,
                                address, activities, terrain, bikeFriendly.toInt(),
                                petFriendly.toInt(), cost.toInt(), tripLength, visit,
                                remote, popularity.toInt(), funFact, highlights)

                        updateUI(currentPark)*/


                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }



    }

    private fun updateUI(currentPark: Park, weatherList: ArrayList<Weather>, parkAlertList: ArrayList<Alert>) {

        Log.d(TAG, "in updateUI")

        if (weatherList.size > 0) {
            Log.d(TAG, "weather list size: ${weatherList.size}")
            var count = 1
            for (weather in weatherList) {
                Log.d(TAG, "in count loop count: $count")

                val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
                val date = Date(weather.unixDate * 1000)
                val formattedDate = sdf.format(date)

                when (count) {
                    1 -> {
                        wTxt1.text = "${weather.main} • ${weather.farenheit}"
                        wDate1.text = formattedDate
                        Glide.with(this).load(getWeatherIconURL(weather.icon)).centerCrop().into(wIcon1)
                    }
                    2 -> {
                        wTxt2.text = "${weather.main} • ${weather.farenheit}"
                        wDate2.text = formattedDate
                        Glide.with(this).load(getWeatherIconURL(weather.icon)).centerCrop().into(wIcon2)
                    }
                    3 -> {
                        wTxt3.text = "${weather.main} • ${weather.farenheit}"
                        wDate3.text = formattedDate
                        Glide.with(this).load(getWeatherIconURL(weather.icon)).centerCrop().into(wIcon3)
                    }
                    4 -> {
                        wTxt4.text = "${weather.main} • ${weather.farenheit}"
                        wDate4.text = formattedDate
                        Glide.with(this).load(getWeatherIconURL(weather.icon)).centerCrop().into(wIcon4)
                    }
                    5 -> {
                        wTxt5.text = "${weather.main} • ${weather.farenheit}"
                        wDate5.text = formattedDate
                        Glide.with(this).load(getWeatherIconURL(weather.icon)).centerCrop().into(wIcon5)
                    }
                }

                count++
            }

        }

        if (parkAlertList.size > 0) {
            Log.d(TAG, "alert list size: ${parkAlertList.size}")
            var alertText = ""
            var count = 1

            for (alert in parkAlertList) {
                if (count == parkAlertList.size) {
                    alertText = alertText.plus("${alert.category}: ${alert.title} \n\n${alert.description}")
                } else {
                    alertText = alertText.plus("${alert.category}: ${alert.title} \n\n${alert.description}\n\n\n")
                }
                count++
            }

            Log.d(TAG, "alertText: $alertText")

            parkAlertsTextView.text = alertText
        }

        if (currentPark != null) {

            val parkBanner: ImageView = findViewById(R.id.parkBannerImg)
            Glide.with(this).load(currentPark.bannerImage).centerCrop().into(parkBanner)

            toolbar.title = currentPark.name
            parkDescTxt.text = currentPark.description
            parkLocationTxt.text = currentPark.location
            parkLocationTxt.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(generateGMapsURI(currentPark.address)))
                startActivity(intent)
            }

            var dir = ""
            var newLineFlag = false
            for (direction in currentPark.directions) {
                if (newLineFlag) {
                    dir += "\n" + direction
                } else {
                    dir += direction
                    newLineFlag = true
                }
            }

            updatePopulationIcons(currentPark.popularity)

            parkDirectionsTxt.text = dir

            parkTripLengthTxt.text = currentPark.tripLength
            parkPrimeMonthsTxt.text = currentPark.whenToVisit
            parkFunFactTxt.text = "DYK? ${currentPark.funFact}"
            parkBikeImg.setBackgroundResource(getIconResourceId(currentPark.bikeFriendly, BIKE_FRIENDLY))
            parkPetImg.setBackgroundResource(getIconResourceId(currentPark.petFriendly, PET_FRIENDLY))
            parkCostImg.setBackgroundResource(getIconResourceId(currentPark.cost, COST))
            parkAlertLabel.text = "${parkAlertLabel.text} (${parkAlertList.size})"

            val activityGridViewAdapter = GridViewAdapter(applicationContext, currentPark.activities)
            val terrainGridViewAdapter = GridViewAdapter(applicationContext, currentPark.terrain)

            activitiesGridView.adapter = activityGridViewAdapter
            terrainGridView.adapter = terrainGridViewAdapter

            activityGridViewAdapter.notifyDataSetChanged()
            terrainGridViewAdapter.notifyDataSetChanged()

            // Instantiate a ViewPager2 and a PagerAdapter.
            highlightPager = findViewById(R.id.highlightPager)
            highlightTab = findViewById(R.id.highlight_tab_layout)

            gemPager = findViewById(R.id.hiddenGemsPager)
            gemTab = findViewById(R.id.hidden_gems_tab_layout)

            // The pager adapter, which provides the pages to the view pager widget.
            val highlightAdapter = HighlightScreenSlidePagerAdapter(this, highlights)
            val gemAdapter = HiddenGemScreenSlidePagerAdapter(this, hiddenGems)

            highlightPager.setPageTransformer(ZoomOutPageTransformer())
            gemPager.setPageTransformer(ZoomOutPageTransformer())

            highlightPager.adapter = highlightAdapter
            gemPager.adapter = gemAdapter

            TabLayoutMediator(highlightTab, highlightPager)
            { tab, position ->}.attach()

            TabLayoutMediator(gemTab, gemPager)
            { tab, position ->}.attach()

            hideShimmer()
            parkLoaded = true
        }

    }

    fun hideShimmer() {
        mShimmer.stopShimmer()
        mShimmer.visibility = View.GONE
        mShimmerLinearLayout.visibility = View.GONE
        mScrollView.visibility = View.VISIBLE
    }

    private fun generateGMapsURI(query: String): String? {
        return "https://www.google.com/maps/search/?api=1&query=$query"
    }

    private fun updatePopulationIcons(popularity: Int) {

        if (popularity == 1) {
            parkPerson1.setBackgroundResource(R.drawable.ic_user_filled_red)
            parkPerson2.setBackgroundResource(R.drawable.ic_user_filled_red)
            parkPerson3.setBackgroundResource(R.drawable.ic_user_filled_red)
            parkPerson4.setBackgroundResource(R.drawable.ic_user_filled_red)
            parkPerson5.setBackgroundResource(R.drawable.ic_user_filled_red)
            parkPerson5.setBackgroundResource(R.drawable.ic_user_filled_red)
        } else if (popularity == 2) {
            parkPerson1.setBackgroundResource(R.drawable.ic_user_filled_yellow)
            parkPerson2.setBackgroundResource(R.drawable.ic_user_filled_yellow)
            parkPerson3.setBackgroundResource(R.drawable.ic_user_filled_yellow)
            parkPerson4.setBackgroundResource(R.drawable.ic_user_filled_yellow)
            parkPerson5.setBackgroundResource(R.drawable.ic_user_empty)
            parkPerson5.y -= -1f
        } else if (popularity == 3) {
            parkPerson1.setBackgroundResource(R.drawable.ic_user_filled_yellow)
            parkPerson2.setBackgroundResource(R.drawable.ic_user_filled_yellow)
            parkPerson3.setBackgroundResource(R.drawable.ic_user_filled_yellow)
            parkPerson4.setBackgroundResource(R.drawable.ic_user_empty)
            parkPerson5.setBackgroundResource(R.drawable.ic_user_empty)
            parkPerson4.y -= -1f
            parkPerson5.y -= -1f
        } else if (popularity == 4) {
            parkPerson1.setBackgroundResource(R.drawable.ic_user_filled_green)
            parkPerson2.setBackgroundResource(R.drawable.ic_user_filled_green)
            parkPerson3.setBackgroundResource(R.drawable.ic_user_empty)
            parkPerson4.setBackgroundResource(R.drawable.ic_user_empty)
            parkPerson5.setBackgroundResource(R.drawable.ic_user_empty)
            parkPerson3.y -= -1f
            parkPerson4.y -= -1f
            parkPerson5.y -= -1f
        } else if (popularity == 5) {
            parkPerson1.setBackgroundResource(R.drawable.ic_user_filled_green)
            parkPerson2.setBackgroundResource(R.drawable.ic_user_empty)
            parkPerson3.setBackgroundResource(R.drawable.ic_user_empty)
            parkPerson4.setBackgroundResource(R.drawable.ic_user_empty)
            parkPerson5.setBackgroundResource(R.drawable.ic_user_empty)
            parkPerson2.y -= -1f
            parkPerson3.y -= -1f
            parkPerson4.y -= -1f
            parkPerson5.y -= -1f
        }

    }

    // Menu options

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.park_detail_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        val wishlistIcon = menu?.findItem(R.id.heart_menu_btn)
        val visitedIcon = menu?.findItem(R.id.visited_menu_btn)

        val uId = FirebaseAuth.getInstance().currentUser?.uid as String
        val docRef = FirebaseFirestore.getInstance().collection("userSettings").document(uId)
        docRef.get().addOnSuccessListener { snapshot ->
            if (snapshot != null) {
                Log.d(TAG, "DocumentSnapshot data: ${snapshot.data}")

                val data = snapshot.data
                val visitedParks = data?.get("visited") as HashMap<String, Boolean>
                val wishlistParks = data?.get("wishlist") as HashMap<String, Boolean>


                if (visitedParks.containsKey(parkId) && wishlistParks.containsKey(parkId)) {
                    if (wishlistParks[parkId] as Boolean) {
                        heartPressed = true
                        wishlistIcon?.icon = getDrawable(R.drawable.heart_filled)
                    } else {
                        heartPressed = false
                        wishlistIcon?.icon = getDrawable(R.drawable.heart_empty)
                    }

                    if (visitedParks[parkId] as Boolean) {
                        checkPressed = true
                        visitedIcon?.icon = getDrawable(R.drawable.check_filled)
                    } else {
                        checkPressed = false
                        visitedIcon?.icon = getDrawable(R.drawable.check_empty)
                    }
                }


            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val uId = FirebaseAuth.getInstance().currentUser?.uid as String

        if (item.itemId == R.id.heart_menu_btn) {

            if (heartPressed) {
                heartPressed = false
                item.icon = getDrawable(R.drawable.heart_empty)

                FirebaseFirestore.getInstance().collection("userSettings").document(uId).update(mapOf("wishlist.${parkId}" to false))
            } else {
                heartPressed = true
                item.icon = getDrawable(R.drawable.heart_filled)

                FirebaseFirestore.getInstance().collection("userSettings").document(uId).update(mapOf("wishlist.${parkId}" to true))
            }

            return true

        } else if (item.itemId == R.id.visited_menu_btn) {

            if (checkPressed) {
                checkPressed = false
                item.icon = getDrawable(R.drawable.check_empty)

                FirebaseFirestore.getInstance().collection("userSettings").document(uId).update(mapOf("visited.${parkId}" to false))
            } else {
                checkPressed = true
                item.icon = getDrawable(R.drawable.check_filled)

                FirebaseFirestore.getInstance().collection("userSettings").document(uId).update(mapOf("visited.${parkId}" to true))
            }

            return true

        } else if (item.itemId == R.id.nps_site_menu_btn) {
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra("link", currentPark.links["npsSite"])
            startActivity(intent)
            return true
        }else if (item.itemId == R.id.nps_camp_menu_btn) {
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra("link", currentPark.links["npsCampgrounds"])
            startActivity(intent)
            return true
        }else if (item.itemId == R.id.nps_permits_menu_btn) {
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra("link", currentPark.links["npsPermits"])
            startActivity(intent)
            return true
        }

        return false
    }

    // Server callbacks
    override fun onParkDataResponse(response: Boolean) {
        Log.d(TAG, "inside onParkDataResponse")
        fetchWeatherData(currentPark)
    }
    override fun onWeatherApiResponse(response: Boolean) {
        Log.d(TAG, "inside onWeatherApiResponse")
        fetchParkAlertData(currentPark)
    }
    override fun onNPSAlertApiResponse(response: Boolean) {
        Log.d(TAG, "inside onNPSAlertApiResponse")
        updateUI(currentPark, weatherList, parkAlertList)
    }
    override fun onTripDetailDataResponse(response: Boolean) {}
    override fun onTripDetailUILoadResponse(response: Boolean) {}

    //region Archived
    private fun getLastUpdatedTimestamp() {
        val docRef = FirebaseFirestore.getInstance().collection(LAST_UPDATED_REF).document(PARKDATA_DOC_REF)
        docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val data = document.data
                        val timestamp = data?.get(TIMESTAMP) as com.google.firebase.Timestamp
                        val date = timestamp.toDate()
                        val sdf = SimpleDateFormat("MMM dd, yyyy").format(date)

                        footerLastUpdatedTxt.text = "Last Updated: $sdf"
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Firestore exception: " + exception.localizedMessage)
                }
    }
    //endregion

}
