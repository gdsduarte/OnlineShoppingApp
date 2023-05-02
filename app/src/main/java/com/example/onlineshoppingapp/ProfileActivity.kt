package com.example.onlineshoppingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.onlineshoppingapp.helpers.FakeStoreApiClient
import com.example.onlineshoppingapp.helpers.SharedPreferencesHelper
import com.example.onlineshoppingapp.models.User
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ProfileActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fakeStoreApiClient: FakeStoreApiClient
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var profileUsername: TextView
    private lateinit var profilePhone: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sharedPreferencesHelper = SharedPreferencesHelper(this)
        fakeStoreApiClient = FakeStoreApiClient()

        profileName = findViewById(R.id.profile_name)
        profileEmail = findViewById(R.id.profile_email)
        profileUsername = findViewById(R.id.profile_username)
        profilePhone = findViewById(R.id.profile_phone)
        val profileImage = findViewById<ImageView>(R.id.profile_image)
        val logoutButton = findViewById<Button>(R.id.logout_button)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavView)

        bottomNavigationView.selectedItemId = R.id.profile
        bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.shop -> {
                    startActivity(Intent(applicationContext, ProductActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.cart -> {
                    startActivity(Intent(applicationContext, CartActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.orders -> {
                    startActivity(Intent(applicationContext, OrderActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.profile -> true
                else -> false
            }
        }

        // Using Picasso to load a random profile image
        val imgNumber = (3316..4316).random()
        Picasso.get()
            .load("https://thispersondoesnotexist.xyz/img/$imgNumber.jpg")
            .into(profileImage)

        // Set user profile data
        val userId = sharedPreferencesHelper.getUserId()

        CoroutineScope(Dispatchers.IO).launch {
            val user = fakeStoreApiClient.getUser(userId!!)
            if (user != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    setUserProfileData(user)
                }
            } else {
                Log.e("ProfileActivity", "Error getting user")
            }
        }

        val aboutAppButton: Button = findViewById(R.id.about_app_button)
        val container: FrameLayout = findViewById(R.id.container)

        aboutAppButton.setOnClickListener {
            container.visibility = View.VISIBLE
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, AboutAppFragment())
                .addToBackStack(null)
                .commit()
        }

        logoutButton.setOnClickListener {
            // Clear the stored user ID/token here
            sharedPreferencesHelper.clearUserId()
            sharedPreferencesHelper.clearUserToken()

            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    private fun setUserProfileData(user: User) {
        profileName.text = "${user.name.firstname} ${user.name.lastname}"
        profileEmail.text = user.email
        profileUsername.text = user.username
        profilePhone.text = user.phone

        val latitude = user.address.geolocation.lat.toDouble()
        val longitude = user.address.geolocation.long.toDouble()
        val userLocation = LatLng(latitude, longitude)
        mMap.addMarker(MarkerOptions().position(userLocation).title("User's Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
    }
}
