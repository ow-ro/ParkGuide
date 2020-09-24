package ca.owro.npapp.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ToggleButton
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import ca.owro.npapp.R
import ca.owro.npapp.Utilities.*
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.activity_settings.*
import java.text.SimpleDateFormat


class SettingsActivity : AppCompatActivity() {

    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var drawer: DrawerLayout
    lateinit var currentUser: FirebaseUser

    lateinit var toggle: ToggleButton

    private val TAG = "SettingsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        title = "Settings"
        currentUser = FirebaseAuth.getInstance().currentUser!!

        toggle = findViewById(R.id.tempToggle)
        toggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                //C
                val userData = HashMap<String, Any>()
                userData.put(USER_TEMP, "c")
                FirebaseFirestore.getInstance().collection(USER_SETTINGS_REF).document(currentUser.uid)
                        .update(userData)
                        .addOnSuccessListener {
                            Log.d(TAG, "User temp setting updated to: Celsius")
                        }
                        .addOnFailureListener { e -> Log.e("Exception", "Could not update user temp: " + e.localizedMessage) }
            } else {
                //F
                val userData = HashMap<String, Any>()
                userData.put(USER_TEMP, "f")
                FirebaseFirestore.getInstance().collection(USER_SETTINGS_REF).document(currentUser.uid)
                        .update(userData)
                        .addOnSuccessListener {
                            Log.d(TAG, "User temp setting updated to: Fahrenheit")
                        }
                        .addOnFailureListener { e -> Log.e("Exception", "Could not update user temp: " + e.localizedMessage) }
            }
        }

        setupNavigationDrawer()
        getUserSettings()

    }

    private fun getUserSettings() {
        val docRef = FirebaseFirestore.getInstance().collection(USER_SETTINGS_REF).document(currentUser.uid)
        docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d(TAG, currentUser.uid)
                        val data = document.data
                        val temp = data?.get(USER_TEMP) as String

                        if (temp == "f") {
                            toggle.isChecked = false
                        } else if (temp == "c") {
                            toggle.isChecked = true
                        }

                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Error", "Firestore exception: " + exception.localizedMessage)
                }
    }

    fun setupNavigationDrawer() {
        toolbar = findViewById(R.id.settings_toolbar)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.settings_draw_layout)

        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        // User avatar in drawer nav
        var navProfilePhoto = findViewById<NavigationView>(R.id.settings_draw_nav_view).getHeaderView(0).findViewById<RoundedImageView>(R.id.profile_image)
        if (currentUser.photoUrl != null) {
            Glide.with(this).load(UtilitiesManager.getAvatarImageUrl(currentUser.photoUrl.toString())).into(navProfilePhoto)
            navProfilePhoto.scaleType = ImageView.ScaleType.FIT_CENTER
            navProfilePhoto.cornerRadius = 30f
            navProfilePhoto.isOval = true
        }

        settings_draw_nav_view.setNavigationItemSelectedListener {
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
}