package ca.owro.npapp.Activities

import android.app.Application
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import ca.owro.npapp.Adapters.ParksAdapter
import ca.owro.npapp.Models.Park
import ca.owro.npapp.R
import ca.owro.npapp.Utilities.*
import ca.owro.npapp.Utilities.UtilitiesManager.ResourceManager.getAvatarImageUrl
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.common.collect.Maps
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.iid.FirebaseInstanceId
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.activity_nplist.*
import java.text.SimpleDateFormat

class NPListActivity : AppCompatActivity() {

    lateinit var parksAdapter: ParksAdapter
    lateinit var listShimmer: ShimmerFrameLayout
    lateinit var drawer: DrawerLayout
    lateinit var toolbar: Toolbar


    lateinit var currentUser: FirebaseUser

    var parks = arrayListOf<Park>()
    val parksMasterList = arrayListOf<Park>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nplist)
        title = "Parks"
        currentUser = FirebaseAuth.getInstance().currentUser!!

        setupNavigationDrawer()
        setupParksListAdapter()

        getAllParks()

    }

    private fun setupParksListAdapter() {
        parksAdapter = ParksAdapter(applicationContext, parks) { park ->
            val intent = Intent(this, NPDetailActivity::class.java)
            intent.putExtra(DOCUMENT_KEY, park.documentId)
            startActivity(intent)
        }
        val layoutManager = LinearLayoutManager(this)
        parkListView.layoutManager = layoutManager
        parkListView.adapter = parksAdapter
    }

    fun setupNavigationDrawer() {
        //Toolbar & navigation drawer
        toolbar = findViewById(R.id.list_toolbar)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.list_draw_layout)

        // User avatar in drawer nav
        var navProfilePhoto = findViewById<NavigationView>(R.id.list_draw_nav_view).getHeaderView(0).findViewById<RoundedImageView>(R.id.profile_image)
        if (currentUser.photoUrl != null) {
            Glide.with(this).load(getAvatarImageUrl(currentUser.photoUrl.toString())).into(navProfilePhoto)
            navProfilePhoto.scaleType = ImageView.ScaleType.FIT_CENTER
            navProfilePhoto.cornerRadius = 30f
            navProfilePhoto.isOval = true
        }

        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        list_draw_nav_view.setNavigationItemSelectedListener {
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
                R.id.nav_park_trips -> {
                    val intent = Intent(this, TripsActivity::class.java)
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

    fun getAllParks() {

            FirebaseFirestore.getInstance().collection(PARKS_REF)
                    .orderBy(NAME, Query.Direction.ASCENDING)
                    .addSnapshotListener { snapshot, exception ->

                        if (exception != null) {
                            Log.e("Exception", "Could not retrieve Parks: ${exception.localizedMessage}")
                        }

                        if (snapshot != null) {
                            parks.clear()
                            Log.d(TAG, snapshot.documents.size.toString())
                            for (document in snapshot.documents) {

                                // ALL DATA

                                /*val data = document.data
                                val name = data?.get(NAME) as String
                                val desc = data?.get(DESCRIPTION) as String
                                val bannerImage = data?.get(BANNER_IMAGE) as String
                                val location = data?.get(LOCATION) as String
                                val address = data?.get(ADDRESS) as String
                                val activities = data?.get(ACTIVITIES) as ArrayList<String>
                                val terrain = data?.get(TERRAIN) as ArrayList<String>
                                val bikeFriendly = data?.get(BIKE_FRIENDLY) as Long
                                val petFriendly = data?.get(PET_FRIENDLY) as Long
                                val cost = data?.get(COST) as Long
                                val tripLength = data?.get(TRIP_LENGTH) as String
                                val visit = data?.get(VISIT) as String
                                val remote = data?.get(REMOTE) as Boolean
                                val popularity = data?.get(POPULARITY) as Long
                                val funFact = data?.get(FUN_FACT) as String
                                val newPark = Park(name, desc, bannerImage, location,
                                        address, activities, terrain, bikeFriendly.toInt(),
                                        petFriendly.toInt(), cost.toInt(), tripLength, visit,
                                        remote, popularity.toInt(), funFact, document.id)*/

                                val data = document.data
                                val name = data?.get(NAME) as String
                                val desc = data.get(DESCRIPTION) as String
                                val listImage = data.get(LIST_IMAGE) as String
                                val location = data.get(LOCATION) as String
                                val activities = data.get(ACTIVITIES) as ArrayList<String>
                                val terrain = data.get(TERRAIN) as ArrayList<String>
                                val bikeFriendly = data.get(BIKE_FRIENDLY) as Long
                                val petFriendly = data.get(PET_FRIENDLY) as Long
                                val cost = data.get(COST) as Long
                                val popularity = data.get(POPULARITY) as Long
                                val car = data.get(CAR) as Boolean
                                val preserve = data.get(PRESERVE) as Boolean

                                val newPark =
                                        Park(document.id, name, desc, listImage,"", arrayListOf(), location,"",
                                                activities, terrain, bikeFriendly.toInt(), petFriendly.toInt(), cost.toInt(),"",
                                                "", popularity.toInt(),"", arrayListOf(), arrayListOf(),0.0,
                                                0.0, hashMapOf(),car,preserve)

                                parks.add(newPark)
                            }
                            parksMasterList.addAll(parks)
                            parksAdapter.notifyDataSetChanged()
                            //Toast.makeText(this,"Welcome Back ${currentUser.displayName}",Toast.LENGTH_SHORT).show()
                        }

                    }

    }

    private fun initFCM() {
        val token = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "initFCM: token: $token")
        //send to server
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.np_list_top, menu)

        val searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.park_list_search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                parksAdapter.filter.filter(newText)
                return false
            }

        })


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.signOutMenu) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
        } else {

            // Filtering & Sorting

            when (itemId) {

                //region SORTING
                // Name ASC
                R.id.filterNameAsc -> {
                    var sortedList = parks.sortedWith(compareByDescending { it.name })
                    parks.clear()
                    parks.addAll(sortedList)
                    parksAdapter.notifyDataSetChanged()
                }
                // Name DESC
                R.id.filterNameDesc -> {
                    var sortedList = parks.sortedWith(compareBy { it.name })
                    parks.clear()
                    parks.addAll(sortedList)
                    parksAdapter.notifyDataSetChanged()
                }
                // Location ASC
                R.id.filterLocationAsc -> {
                    var sortedList = parks.sortedWith(compareByDescending { it.location })
                    parks.clear()
                    parks.addAll(sortedList)
                    parksAdapter.notifyDataSetChanged()
                }
                // Location DESC
                R.id.filterLocationDesc -> {
                    var sortedList = parks.sortedWith(compareBy { it.location })
                    parks.clear()
                    parks.addAll(sortedList)
                    parksAdapter.notifyDataSetChanged()
                }
                // Popularity ASC
                R.id.filterPopularityAsc -> {
                    var sortedList = parks.sortedWith(compareByDescending { it.popularity })
                    parks.clear()
                    parks.addAll(sortedList)
                    parksAdapter.notifyDataSetChanged()
                }
                // Popularity DESC
                R.id.filterPopularityDesc -> {
                    var sortedList = parks.sortedWith(compareBy { it.popularity })
                    parks.clear()
                    parks.addAll(sortedList)
                    parksAdapter.notifyDataSetChanged()
                }
                //endregion

                //region PRIMARY FILTERS

                //WISHLIST
                R.id.filterWishlist -> {
                    val uId = FirebaseAuth.getInstance().currentUser?.uid as String
                    val docRef = FirebaseFirestore.getInstance().collection("userSettings").document(uId)
                    docRef.get().addOnSuccessListener { snapshot ->
                        if (snapshot != null) {
                            Log.d(TAG, "DocumentSnapshot data: ${snapshot.data}")

                            val data = snapshot.data
                            val wishlistParks = data?.get("wishlist") as HashMap<*, *>

                            var parksCopy = arrayListOf<Park>()

                            for (park in parks) {
                                if (wishlistParks[park.documentId] == true) {
                                    parksCopy.add(park)
                                }
                            }

                            parks.clear()
                            parks.addAll(parksCopy)
                            parksAdapter.notifyDataSetChanged()
                            showFilterSnackbar(findViewById(R.id.listLinearLayout), "Wishlist")

                        }
                    }
                }

                //VISITED
                R.id.filterVisited -> {
                    val uId = FirebaseAuth.getInstance().currentUser?.uid as String
                    val docRef = FirebaseFirestore.getInstance().collection("userSettings").document(uId)
                    docRef.get().addOnSuccessListener { snapshot ->
                        if (snapshot != null) {
                            Log.d(TAG, "DocumentSnapshot data: ${snapshot.data}")

                            val data = snapshot.data
                            val visitedParks = data?.get("visited") as HashMap<*, *>

                            var parksCopy = arrayListOf<Park>()

                            for (park in parks) {
                                if (visitedParks[park.documentId] == true) {
                                    parksCopy.add(park)
                                }
                            }

                            parks.clear()
                            parks.addAll(parksCopy)
                            parksAdapter.notifyDataSetChanged()
                            showFilterSnackbar(findViewById(R.id.listLinearLayout), "Visited")

                        }
                    }
                }


                //NO ENTRANCE FEE
                R.id.filterEntranceFee -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.cost == 3) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "No Entrance Fee")
                }
                //PETS
                R.id.filterPets -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.petFriendly in 2..3) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Pet Friendly")
                }
                //BIKES
                R.id.filterBikes -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.bikeFriendly in 2..3) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Bike Friendly")
                }
                //CAR ACCESSIBLE
                R.id.filterCar -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.car) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Accessible By Car")
                }
                //NATIONAL PRESERVE
                R.id.filterPreserve -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.preserve) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "National Preserve")
                }
                //endregion

                //region ACTIVITIES
                //Auto Touring
                R.id.filterAutoTouring -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.activities.contains("Auto Touring")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Auto Touring")
                }
                //Bicycling
                R.id.filterBicycling -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.activities.contains("Bicycling")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Bicycling")
                }
                //Boating
                R.id.filterBoating -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.activities.contains("Boating")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Boating")
                }
                //Backpacking
                R.id.filterBackpacking -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.activities.contains("Backpacking")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Backpacking")
                }
                //Backpacking
                R.id.filterBirdwatching -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.activities.contains("Birdwatching")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Birdwatching")
                }
                //Canyoneering
                R.id.filterCanyoneering -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.activities.contains("Canyoneering")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Canyoneering")
                }
                //Climbing
                R.id.filterClimbing -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.activities.contains("Climbing")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Climbing")
                }
                //Dog Sledding
                R.id.filterDogSledding -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.activities.contains("Dog Sledding")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Dog Sledding")
                }
                //Hiking
                R.id.filterHiking -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.activities.contains("Hiking")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Hiking")
                }
                //Fishing
                R.id.filterFishing -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.activities.contains("Fishing")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Fishing")
                }
                //Horse Riding
                R.id.filterHorseriding -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.activities.contains("Horse Riding")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Horse Riding")
                }
                //Ice Fishing
                R.id.filterIceFishing -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.activities.contains("Ice Fishing")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Ice Fishing")
                }
                //Kayaking
                R.id.filterKayaking -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.activities.contains("Kayaking")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Kayaking")
                }
                //Scuba Diving
                R.id.filterScubaDiving -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.activities.contains("Scuba Diving")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Scuba Diving")
                }
                //Skidooing
                R.id.filterSkidooing -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.activities.contains("Skidooing")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Skidooing")
                }
                //Snorkeling
                R.id.filterSnorkeling -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.activities.contains("Snorkeling")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Snorkeling")
                }
                //Snowshoeing
                R.id.filterSnowshoeing -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.activities.contains("Snowshoeing")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Snowshoeing")
                }
                //Stargazing
                R.id.filterStargazing -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.activities.contains("Stargazing")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Stargazing")
                }
                //Surfing
                R.id.filterSurfing -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.activities.contains("Surfing")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Surfing")
                }
                //Swimming
                R.id.filterSwimming -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.activities.contains("Swimming")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Swimming")
                }
                //Tidepooling
                R.id.filterTidepooling -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.activities.contains("Tidepooling")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Tidepooling")
                }
                //Whale Views
                R.id.filterWhales -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.activities.contains("Whale Views")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Whale Views")
                }
                //XC Skiing
                R.id.filterXCSkiing -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.activities.contains("XC Skiing")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "XC Skiing")
                }
                //endregion

                //region TERRAINS
                //Bays
                R.id.filterBays -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.terrain.contains("Bays")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Bays")
                }
                R.id.filterBeaches -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.terrain.contains("Beaches")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Beaches")
                }
                R.id.filterCanyons -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.terrain.contains("Canyons")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Canyons")
                }
                R.id.filterCaves -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.terrain.contains("Caves")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Caves")
                }
                R.id.filerCoasts -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.terrain.contains("Coasts")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Coasts")
                }
                R.id.filterDeserts -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.terrain.contains("Deserts")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Deserts")
                }
                R.id.filterDunes -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.terrain.contains("Dunes")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Dunes")
                }
                R.id.filterForests -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.terrain.contains("Forests")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Forests")
                }
                R.id.filterGlaciers -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.terrain.contains("Glaciers")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Glaciers")
                }
                R.id.filterHotSprings -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.terrain.contains("Hot Springs")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Hot Springs")
                }
                R.id.filterLakes -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.terrain.contains("Lakes")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Lakes")
                }
                R.id.filterMountains -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.terrain.contains("Mountains")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Mountains")
                }
                R.id.filterPonds -> {
                var parksCopy = arrayListOf<Park>()
                for (park in parks) {
                    if (park.terrain.contains("Ponds")) {
                        parksCopy.add(park)
                    }
                }
                parks.clear()
                parks.addAll(parksCopy)
                parksAdapter.notifyDataSetChanged()
                showFilterSnackbar(findViewById(R.id.listLinearLayout), "Ponds")
                }
                R.id.filterPrairies -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.terrain.contains("Prairies")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Prairies")
                }
                R.id.filterRedRocks -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.terrain.contains("Red Rocks")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Red Rocks")
                }
                R.id.filterReefs -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.terrain.contains("Reefs")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Reefs")
                }
                R.id.filterSandstone -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.terrain.contains("Sandstone")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Sandstone")
                }
                R.id.filterVolcanoes -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.terrain.contains("Volcanoes")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Volcanoes")
                }
                R.id.filterWetlands -> {
                    var parksCopy = arrayListOf<Park>()
                    for (park in parks) {
                        if (park.terrain.contains("Wetlands")) {
                            parksCopy.add(park)
                        }
                    }
                    parks.clear()
                    parks.addAll(parksCopy)
                    parksAdapter.notifyDataSetChanged()
                    showFilterSnackbar(findViewById(R.id.listLinearLayout), "Wetlands")
                }

                //endregion

        }

        }

        return true
    }

    fun showFilterSnackbar(view: View, filter: String){
        val snackbar: Snackbar = Snackbar.make(view, "$filter filter applied", Snackbar.LENGTH_INDEFINITE)
        snackbar.setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
        snackbar.setActionTextColor(Color.WHITE)
        snackbar.setTextColor(Color.WHITE)
        snackbar.setAction("Clear") {
            parks.clear()
            parks.addAll(parksMasterList)
            parksAdapter.notifyDataSetChanged()
        }
        snackbar.show()
    }

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

    companion object {
        private const val TAG = "NPListActivity"
    }
}