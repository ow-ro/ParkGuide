package ca.owro.npapp.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import ca.owro.npapp.R
import ca.owro.npapp.Utilities.LAST_UPDATED_REF
import ca.owro.npapp.Utilities.PARKDATA_DOC_REF
import ca.owro.npapp.Utilities.TIMESTAMP
import ca.owro.npapp.Utilities.UtilitiesManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.activity_about.*
import java.text.SimpleDateFormat

class AboutActivity : AppCompatActivity() {

    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var drawer: DrawerLayout
    lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        title = "About"

        currentUser = FirebaseAuth.getInstance().currentUser!!

        setupNavigationDrawer()

        lntImage.setOnClickListener { v ->
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse("https://lnt.org/")
            startActivity(intent)
        }
    }

    fun setupNavigationDrawer() {
        toolbar = findViewById(R.id.about_toolbar)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.about_draw_layout)

        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        // User avatar in drawer nav
        var navProfilePhoto = findViewById<NavigationView>(R.id.about_draw_nav_view).getHeaderView(0).findViewById<RoundedImageView>(R.id.profile_image)
        if (currentUser.photoUrl != null) {
            Glide.with(this).load(UtilitiesManager.getAvatarImageUrl(currentUser.photoUrl.toString())).into(navProfilePhoto)
            navProfilePhoto.scaleType = ImageView.ScaleType.FIT_CENTER
            navProfilePhoto.cornerRadius = 30f
            navProfilePhoto.isOval = true
        }

        about_draw_nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_park_list -> {
                    val intent = Intent(this, NPListActivity::class.java)
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