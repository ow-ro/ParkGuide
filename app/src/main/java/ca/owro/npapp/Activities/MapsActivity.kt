package ca.owro.npapp.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import ca.owro.npapp.R
import ca.owro.npapp.Utilities.*
import ca.owro.npapp.Utilities.UtilitiesManager.ResourceManager.getParkDocIdFromTitle
import ca.owro.npapp.Utilities.UtilitiesManager.ResourceManager.getParkLocationFromTitle
import ca.owro.npapp.Utilities.UtilitiesManager.ResourceManager.getParkPopularityFromTitle
import com.bumptech.glide.Glide
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.activity_maps.footerLastUpdatedTxt
import kotlinx.android.synthetic.main.activity_nplist.*
import java.text.SimpleDateFormat

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var mMap: GoogleMap
    lateinit var options: GoogleMapOptions

    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var drawer: DrawerLayout
    lateinit var currentUser: FirebaseUser

    private var permissionDenied = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        title = "Maps"

        currentUser = FirebaseAuth.getInstance().currentUser!!

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupNavigationDrawer()
    }

    override fun onResume() {
        super.onResume()
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            //showMissingPermissionError()
            permissionDenied = false
        }
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        //val sydney = LatLng(-34.0, 151.0)
        //mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))

        //Create a park marker for each park
        for ((k,v) in PARK_LATLON_DATA_LIST) {
            var iconId = -1

            when (getParkPopularityFromTitle(k)) {
                1 -> iconId = R.drawable.ic_marker_p1
                2 -> iconId = R.drawable.ic_marker_p2
                3 -> iconId = R.drawable.ic_marker_p3
                4 -> iconId = R.drawable.ic_marker_p4
                5 -> iconId = R.drawable.ic_marker_p5
            }

            mMap.addMarker(MarkerOptions().position(v).title("$k").icon(bitmapFromVector(iconId)).infoWindowAnchor(.32f,0f).snippet(getParkLocationFromTitle(k)))
        }

        val USA = LatLngBounds(LatLng(-44.0, 113.0), LatLng(71.6, -51.4))
        val lower48Center = LatLng(39.99, -97.55)

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lower48Center, 3f))
        //mMap.setLatLngBoundsForCameraTarget(USA)
        mMap.setMaxZoomPreference(14f)

        mMap.setOnMyLocationButtonClickListener(this)
        mMap.setOnMyLocationClickListener(this)
        mMap.setOnInfoWindowClickListener(this)

        mMap.uiSettings.isCompassEnabled = true

        enableUserLocation()
    }


    private fun bitmapFromVector(vectorResID:Int):BitmapDescriptor {
        val vectorDrawable= ContextCompat.getDrawable(this,vectorResID)
        vectorDrawable!!.setBounds(0,0,vectorDrawable!!.intrinsicWidth,vectorDrawable.intrinsicHeight)
        val bitmap= Bitmap.createBitmap(vectorDrawable.intrinsicWidth,vectorDrawable.intrinsicHeight,Bitmap.Config.ARGB_8888)
        val canvas= Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onInfoWindowClick(marker: Marker?) {
        val intent = Intent(this, NPDetailActivity::class.java)
        intent.putExtra(DOCUMENT_KEY, getParkDocIdFromTitle(marker!!.title))
        startActivity(intent)
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun onMyLocationClick(location: Location) {
    }

    fun setupNavigationDrawer() {
        toolbar = findViewById(R.id.map_toolbar)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.map_draw_layout)

        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        // User avatar in drawer nav
        var navProfilePhoto = findViewById<NavigationView>(R.id.map_draw_nav_view).getHeaderView(0).findViewById<RoundedImageView>(R.id.profile_image)
        if (currentUser.photoUrl != null) {
            Glide.with(this).load(UtilitiesManager.getAvatarImageUrl(currentUser.photoUrl.toString())).into(navProfilePhoto)
            navProfilePhoto.scaleType = ImageView.ScaleType.FIT_CENTER
            navProfilePhoto.cornerRadius = 30f
            navProfilePhoto.isOval = true
        }

        map_draw_nav_view.setNavigationItemSelectedListener {
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
                R.id.nav_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
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

    private fun enableUserLocation() {
        if (!::mMap.isInitialized) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }

        // has user granted permission?
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation()
        } else {
            permissionDenied = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.map_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.reload_menu_btn) {

            val lower48Center = LatLng(39.99, -97.55)
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.Builder()
                    .target(lower48Center)
                    .zoom(3f)
                    .bearing(0f)
                    .build()))

            return true

        } else if (itemId == R.id.map_terrain_menu_btn) {

            mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN

            return true

        } else if (itemId == R.id.map_satellite_menu_btn) {

            mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE

            return true

        } else if (itemId == R.id.map_hybrid_menu_btn) {

            mMap.mapType = GoogleMap.MAP_TYPE_HYBRID

            return true

        } else if (itemId == R.id.map_normal_menu_btn) {

            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            return true

        }

        return false
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
                    Log.d("Error", "Firestore exception: " + exception.localizedMessage)
                }
    }
    //endregion

    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}