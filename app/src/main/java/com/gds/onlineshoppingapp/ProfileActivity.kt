package com.gds.onlineshoppingapp

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
import com.gds.onlineshoppingapp.helpers.FakeStoreApiClient
import com.gds.onlineshoppingapp.helpers.SharedPreferencesHelper
import com.gds.onlineshoppingapp.models.User
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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

        // Initialize shared preferences helper and fake store api client
        sharedPreferencesHelper = SharedPreferencesHelper(this)
        fakeStoreApiClient = FakeStoreApiClient()

        // Initialize views and buttons
        profileName = findViewById(R.id.profile_name)
        profileEmail = findViewById(R.id.profile_email)
        profileUsername = findViewById(R.id.profile_username)
        profilePhone = findViewById(R.id.profile_phone)
        val profileImage = findViewById<ImageView>(R.id.profile_image)
        val logoutButton = findViewById<Button>(R.id.logout_button)

        // Setup bottom navigation view
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

        // Get user data from the fake store api
        GlobalScope.launch(Dispatchers.IO) {
            val user = fakeStoreApiClient.getUser(userId!!)
            if (user != null) {
                runOnUiThread {
                    setUserProfileData(user)
                }
            } else {
                Log.e("ProfileActivity", "Error getting user")
            }
        }

        // Setup about app button and container
        val aboutAppButton: Button = findViewById(R.id.about_app_button)
        val container: FrameLayout = findViewById(R.id.container)

        // Hide the container when the activity is created
        aboutAppButton.setOnClickListener {
            container.visibility = View.VISIBLE
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, AboutAppFragment())
                .addToBackStack(null)
                .commit()
        }

        // Logout button
        logoutButton.setOnClickListener {
            // Clear the stored user data
            sharedPreferencesHelper.clearAll()

            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // Map callback function
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    // Set user profile data function
    private fun setUserProfileData(user: User) {

        // Set user profile data
        profileName.text = "${user.name.firstname} ${user.name.lastname}"
        profileEmail.text = user.email
        profileUsername.text = user.username
        profilePhone.text = user.phone

        // Add user location marker to the map
        val latitude = user.address.geolocation.lat
        val longitude = user.address.geolocation.long
        val userLocation = LatLng(latitude.toDouble(), longitude.toDouble())
        mMap.addMarker(MarkerOptions().position(userLocation).title("User's Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
    }
}
