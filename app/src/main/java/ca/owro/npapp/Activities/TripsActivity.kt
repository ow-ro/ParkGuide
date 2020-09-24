package ca.owro.npapp.Activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import ca.owro.npapp.Adapters.TripsAdapter
import ca.owro.npapp.Models.Trip
import ca.owro.npapp.R
import ca.owro.npapp.Utilities.*
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.activity_trips.*
import kotlinx.android.synthetic.main.trips_list_view_item.*


class TripsActivity : AppCompatActivity() {

    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var drawer: DrawerLayout
    lateinit var currentUser: FirebaseUser
    lateinit var tripsAdapter: TripsAdapter
    var trips = arrayListOf<Trip>()

    lateinit var conLayout: ConstraintLayout

    private val TAG = "TripsActivity"

    lateinit var fadeInAnimation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trips)
        tripsCLayout.visibility = View.GONE

        title = "Trips"
        currentUser = FirebaseAuth.getInstance().currentUser!!

        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        conLayout = findViewById(R.id.tripsCLayout)

        setupNavigationDrawer()
        setupTripsListAdapter()

        getAllTrips()

    }

    private fun setupTripsListAdapter() {
        tripsAdapter = TripsAdapter(applicationContext, trips) { trip ->
            val intent = Intent(this, TripDetailActivity::class.java)
            intent.putExtra(DOCUMENT_KEY, trip.documentId)
            startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        val mGridLayoutManager = GridLayoutManager(this, 2)
        tripsRecyclerView.layoutManager = mGridLayoutManager
        tripsRecyclerView.adapter = tripsAdapter
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun getAllTrips() {
        FirebaseFirestore.getInstance().collection(TRIPS_REF)
                .orderBy(NAME, Direction.ASCENDING)
                .addSnapshotListener { snapshot, exception ->

                    if (exception != null) {
                        Log.e("Exception", "Could not retrieve Trips: ${exception.localizedMessage}")
                    }

                    if (snapshot != null) {
                        trips.clear()
                        Log.d(TAG, snapshot.documents.size.toString())
                        for (document in snapshot.documents) {

                            val data = document.data
                            val name = data?.get(NAME) as String
                            Log.d(TAG, "Trip name: $name")
                            val desc = data[DESCRIPTION] as String
                            val start = data[START] as String
                            val driveTime = data[DRIVE_TIME] as String
                            val gradient = data[GRADIENT] as Long
                            val length = data[LENGTH] as String
                            val months = data[MONTHS] as String
                            val parkCount = data[PARK_COUNT] as Long
                            val parks = data[PARKS] as String
                            val bannerImage = data[BANNER_IMAGE] as String

                            val newTrip =
                                    Trip(document.id,name,parks,desc,driveTime,length,months,start,gradient.toInt(),parkCount.toInt())

                            trips.add(newTrip)
                        }
                        tripsAdapter.notifyDataSetChanged()
                        conLayout.visibility = View.VISIBLE
                        conLayout.startAnimation(fadeInAnimation)
                        //Toast.makeText(this,"Welcome Back ${currentUser.displayName}",Toast.LENGTH_SHORT).show()
                    }

                }
    }


    fun setupNavigationDrawer() {
        //Toolbar & navigation drawer
        toolbar = findViewById(R.id.trips_toolbar)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.trips_draw_layout)

        // User avatar in drawer nav
        var navProfilePhoto = findViewById<NavigationView>(R.id.trips_draw_nav_view).getHeaderView(0).findViewById<RoundedImageView>(R.id.profile_image)
        if (currentUser.photoUrl != null) {
            Glide.with(this).load(UtilitiesManager.getAvatarImageUrl(currentUser.photoUrl.toString())).into(navProfilePhoto)
            navProfilePhoto.scaleType = ImageView.ScaleType.FIT_CENTER
            navProfilePhoto.cornerRadius = 30f
            navProfilePhoto.isOval = true
        }

        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        trips_draw_nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_about -> {
                    val intent = Intent(this, AboutActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_park_map -> {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_park_list -> {
                    val intent = Intent(this, NPListActivity::class.java)
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

}