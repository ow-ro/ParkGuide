package ca.owro.npapp.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ca.owro.npapp.BuildConfig
import ca.owro.npapp.R
import ca.owro.npapp.Utilities.*
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.*
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.protobuf.Any
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.HashMap

class MainActivity : Activity() {
    private val TAG = "MainActivity"
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        if (BuildConfig.DEBUG) {
            val settings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build()
            FirebaseFirestore.getInstance().firestoreSettings = settings
        }

        if (auth.currentUser != null) {

            // repeat user. so add last login time
            val userData = HashMap<String, kotlin.Any>()
            userData.put(LAST_LOGIN, FieldValue.serverTimestamp())
            FirebaseFirestore.getInstance().collection(USERS_REF).document(auth.currentUser!!.uid)
                    .update(userData)
                    .addOnSuccessListener {
                        Log.d(TAG, "lastLogin updated for user: ${auth.currentUser!!.displayName}")
                        Log.d(TAG,"UserPhotoURL: ${auth.currentUser!!.photoUrl}")
                    }
                    .addOnFailureListener { e -> Log.e("Exception", "Could not update lastLogin: " + e.localizedMessage) }

            val intent = Intent(this, NPListActivity::class.java)
            intent.putExtra(USER_ID, auth.currentUser!!.uid)
            startActivity(intent)
        } else {
            val providers = Arrays.asList(
                    EmailBuilder().build(),
                    GoogleBuilder().build(),
                    FacebookBuilder().build())



            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setIsSmartLockEnabled(false)
                            .setTheme(R.style.LoginTheme)
                            .setLogo(R.drawable.logo_trans_2)
                            .build(),
                    RC_SIGN_IN)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser

                val displayName = user?.displayName
                val authProvider = user?.providerId
                val email = user?.email

                if (response!!.isNewUser) {
                    // first time user. so add doc in 'users' collection

                    val userData = HashMap<String, kotlin.Any>()

                    userData[DISPLAY_NAME] = displayName!!
                    userData[AUTH_PROVIDER] = authProvider!!
                    userData[EMAIL] = email!!
                    userData[DATE_CREATED] = FieldValue.serverTimestamp()
                    userData[LAST_LOGIN] = FieldValue.serverTimestamp()


                    // add to users collection
                    FirebaseFirestore.getInstance().collection(USERS_REF).document(user.uid)
                            .set(userData)
                            .addOnSuccessListener { Log.d(TAG, "New user doc added in collection users for user: $displayName") }
                            .addOnFailureListener { e -> Log.e("Exception", "Could not add user to Firestore: " + e.localizedMessage) }

                    // initialise user settings
                    val docData = hashMapOf<String, kotlin.Any>("temperatureUnit" to "f")
                    val nestedData = visitedAndWishlistDataMap()
                    docData["visited"] = nestedData
                    docData["wishlist"] = nestedData

                    FirebaseFirestore.getInstance().collection(USER_SETTINGS_REF).document(user.uid)
                            .set(docData)
                            .addOnSuccessListener {
                                Log.d(TAG, "User Settings initialised for: $displayName")
                            }
                            .addOnFailureListener {e ->
                                Log.d(TAG, "ERROR: User Settings could not be initialised for: $displayName" + e.localizedMessage)
                            }

                } else {
                    // repeat user. so add last login time
                    val userData = HashMap<String, kotlin.Any>()
                    userData.put(LAST_LOGIN, FieldValue.serverTimestamp())
                    FirebaseFirestore.getInstance().collection(USERS_REF).document(user!!.uid)
                            .update(userData)
                            .addOnSuccessListener { Log.d(TAG, "lastLogin updated for user: $displayName") }
                            .addOnFailureListener { e -> Log.e("Exception", "Could not update lastLogin: " + e.localizedMessage) }
                }

                val intent = Intent(this, NPListActivity::class.java)
                intent.putExtra(USER_ID, user.uid)
                startActivity(intent)
            } else {
                Log.e(TAG, "Sign in failed", response!!.error)
            }
        }
    }

    fun visitedAndWishlistDataMap() : HashMap<String, Boolean> {
        var hm = hashMapOf(
                "acadia" to false,
                "americanSamoa" to false,
                "arches" to false,
                "badlands" to false,
                "bigBend" to false,
                "biscayne" to false,
                "blackCanyonOfTheGunnison" to false,
                "bryceCanyon" to false,
                "canyonlands" to false,
                "capitolReef" to false,
                "carlsbadCaverns" to false,
                "channelIslands" to false,
                "congaree" to false,
                "craterLake" to false,
                "cuyahogaValley" to false,
                "deathValley" to false,
                "denali" to false,
                "dryTortugas" to false,
                "everglades" to false,
                "gatesOfTheArctic" to false,
                "gatewayArch" to false,
                "glacier" to false,
                "glacierBay" to false,
                "grandCanyon" to false,
                "grandTeton" to false,
                "greatBasin" to false,
                "greatSandDunes" to false,
                "greatSmokyMountains" to false,
                "guadalupeMountains" to false,
                "haleakala" to false,
                "hawaiiVolcanoes" to false,
                "hotSprings" to false,
                "indianaDunes" to false,
                "isleRoyale" to false,
                "joshuaTree" to false,
                "katmai" to false,
                "kenaiFjords" to false,
                "kingsCanyon" to false,
                "kobukValley" to false,
                "lakeClark" to false,
                "lassenVolcanic" to false,
                "mammothCave" to false,
                "mesaVerde" to false,
                "mountRainier" to false,
                "northCascades" to false,
                "olympic" to false,
                "petrifiedForest" to false,
                "pinnacles" to false,
                "redwood" to false,
                "rockyMountain" to false,
                "saguaro" to false,
                "sequoia" to false,
                "shenandoah" to false,
                "theodoreRoosevelt" to false,
                "virginIslands" to false,
                "voyageurs" to false,
                "whiteSands" to false,
                "windCave" to false,
                "wrangellStElias" to false,
                "yellowstone" to false,
                "yosemite" to false,
                "zion" to false
                )

        return hm
    }

    companion object {
        const val RC_SIGN_IN = 123
        var USER_ID = "user_id"
    }
}