package ca.owro.npapp.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import ca.owro.npapp.Adapters.LegAdapter
import ca.owro.npapp.Models.Leg
import ca.owro.npapp.Models.Trip
import ca.owro.npapp.R
import ca.owro.npapp.Utilities.*
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.activity_trip_detail.*

class TripDetailActivity : AppCompatActivity(), ServerCallback {
    val myServerCallback = this

    lateinit var tripId: String
    lateinit var currentTrip: Trip

    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var drawer: DrawerLayout
    lateinit var currentUser: FirebaseUser


    lateinit var legAdapter: LegAdapter
    var legs = arrayListOf<Leg>()

    lateinit var fadeInAnimation: Animation

    private val TAG = "TripDetailActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_detail)

        tripScrollView.visibility = View.GONE

        tripId = intent.getStringExtra(DOCUMENT_KEY) as String
        currentUser = FirebaseAuth.getInstance().currentUser!!

        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        setupNavigationDrawer()
        setupLegsAdapter()

        getTripData()
    }


    fun setupLegsAdapter() {
        legAdapter = LegAdapter(applicationContext, legs) { leg ->
            // 3 link types: Maps (directions), in-app parks (e.g. when leg is 'Explore Zion') and other (e.g. non-NP web link)
            with(leg.link) {
                when {
                    contains("maps/dir") -> {
                        val intent = Intent(Intent.ACTION_VIEW,
                                Uri.parse(leg.link))
                        startActivity(intent)
                    }
                    PARK_ID_LIST.contains(this) -> {
                        val intent = Intent(applicationContext, NPDetailActivity::class.java)
                        intent.putExtra(DOCUMENT_KEY, leg.link)
                        startActivity(intent)
                    }
                    else -> {
                        val intent = Intent(applicationContext, WebViewActivity::class.java)
                        intent.putExtra("link", leg.link)
                        startActivity(intent)
                    }
                }
            }
        }
        val layoutManager = LinearLayoutManager(this)
        tripLegsRecyclerView.layoutManager = layoutManager
        tripLegsRecyclerView.isNestedScrollingEnabled = false
        tripLegsRecyclerView.adapter = legAdapter
    }

    fun getTripData() {
        val docRef = FirebaseFirestore.getInstance().collection(TRIPS_REF).document(tripId)
        docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val data = document.data
                        val description = data?.get(DESCRIPTION) as String
                        val name = data[NAME] as String
                        val driveTime = data[DRIVE_TIME] as String
                        val gradient = data[GRADIENT] as Long
                        val length = data[LENGTH] as String
                        val months = data[MONTHS] as String
                        val parkCount = data[PARK_COUNT] as Long
                        val parks = data[PARKS] as String
                        val start = data[START] as String
                        val bannerImage = data[BANNER_IMAGE] as String

                        FirebaseFirestore.getInstance().collection(TRIPS_REF).document(tripId).collection(TRIP_LEGS_REF)
                                .orderBy(ORDER, Query.Direction.ASCENDING)
                                .get()
                                .addOnSuccessListener { documents ->
                                    legs.clear()

                                    for (doc in documents) {
                                        val data = doc.data
                                        Log.d(TAG, "Leg data: $data")
                                        val order = data?.get(ORDER) as Long
                                        val description = data[DESCRIPTION] as String
                                        val image = data[IMAGE] as String
                                        val link = data[LINK] as String
                                        val time = data[TIME] as String

                                        val newLeg = Leg(order.toInt(),description,image,link,time)

                                        legs.add(newLeg)
                                    }

                                    currentTrip = Trip(document.id,name,parks,description,driveTime,length,months,start,gradient.toInt(),parkCount.toInt(), bannerImage, legs)

                                    legAdapter.notifyDataSetChanged()
                                    //callback
                                    myServerCallback.onTripDetailDataResponse(true)


                                }

                    }
                }
    }

    fun updateUI(trip: Trip) {
        toolbar.title = trip.name
        tripDetailDesc.text = trip.description
        tripDetailParks.text = trip.parks
        tripDetailLocationTitle.text = trip.start
        tripDetailDriveTitle.text = trip.driveTime
        tripDetailLengthTitle.text = trip.length
        tripDetailMonthsTitle.text = trip.months

        when (trip.gradient) {
            1 -> tripGradientView.setBackgroundResource(R.drawable.trips_1)
            2 -> tripGradientView.setBackgroundResource(R.drawable.trips_2)
            3 -> tripGradientView.setBackgroundResource(R.drawable.trips_3)
            4 -> tripGradientView.setBackgroundResource(R.drawable.trips_4)
            5 -> tripGradientView.setBackgroundResource(R.drawable.trips_5)
        }

        Glide.with(this).load(trip.bannerImage).centerCrop().into(tripDetailBannerImg)

        myServerCallback.onTripDetailUILoadResponse(true)

    }

    override fun onTripDetailUILoadResponse(response: Boolean) {
        tripScrollView.visibility = View.VISIBLE
        tripScrollView.startAnimation(fadeInAnimation)
    }

    fun setupNavigationDrawer() {
        toolbar = findViewById(R.id.trip_detail_toolbar)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.trip_detail_draw_layout)

        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        // User avatar in drawer nav
        var navProfilePhoto = findViewById<NavigationView>(R.id.trip_detail_draw_nav_view).getHeaderView(0).findViewById<RoundedImageView>(R.id.profile_image)
        if (currentUser.photoUrl != null) {
            Glide.with(this).load(UtilitiesManager.getAvatarImageUrl(currentUser.photoUrl.toString())).into(navProfilePhoto)
            navProfilePhoto.scaleType = ImageView.ScaleType.FIT_CENTER
            navProfilePhoto.cornerRadius = 30f
            navProfilePhoto.isOval = true
        }

        trip_detail_draw_nav_view.setNavigationItemSelectedListener {
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

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // Server callbacks
    override fun onTripDetailDataResponse(response: Boolean) {
        updateUI(currentTrip)
    }

    override fun onParkDataResponse(response: Boolean) {}
    override fun onWeatherApiResponse(response: Boolean) {}
    override fun onNPSAlertApiResponse(response: Boolean) {}

}