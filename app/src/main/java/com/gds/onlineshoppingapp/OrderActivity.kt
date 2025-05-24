package com.gds.onlineshoppingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gds.onlineshoppingapp.adapters.OrderAdapter
import com.gds.onlineshoppingapp.helpers.CartUtils
import com.gds.onlineshoppingapp.helpers.FakeStoreApiClient
import com.gds.onlineshoppingapp.helpers.SharedPreferencesHelper
import com.gds.onlineshoppingapp.models.Cart
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class OrderActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var fakeStoreApiClient: FakeStoreApiClient
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var carts: List<Cart>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        // Initialize shared preferences helper and fake store api client
        sharedPreferencesHelper = SharedPreferencesHelper(this)
        fakeStoreApiClient = FakeStoreApiClient()

        // Setup bottom navigation view
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavView)
        bottomNavigationView.selectedItemId = R.id.orders
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
                R.id.orders -> true
                R.id.profile -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }

        fetchAndSetupUI()
    }

    // Fetches the carts from the API and sets up the UI
    private fun fetchAndSetupUI() {
        launch(Dispatchers.Main) {
            carts = CartUtils.getCarts(sharedPreferencesHelper, fakeStoreApiClient).toMutableList()

            // Load the placed orders from SharedPreferences
            val placedOrders = sharedPreferencesHelper.loadPlacedOrders()

            // Fetch carts from the API
            val apiCarts = CartUtils.getCarts(sharedPreferencesHelper, fakeStoreApiClient)

            // Merge the carts fetched from the API with the placedOrders
            val allCarts = mutableListOf<Cart>().apply {
                addAll(apiCarts)
                addAll(placedOrders)
            }

            // Show the empty cart message if there are no carts and no order items
            val emptyCartTextView: TextView = findViewById(R.id.emptyCartTextView)
            if (allCarts.isEmpty()) {
                emptyCartTextView.visibility = View.VISIBLE
            } else {
                emptyCartTextView.visibility = View.GONE
            }

            // Setup cart RecyclerView
            val cartRecyclerView: RecyclerView = findViewById(R.id.cartRecyclerView)
            orderAdapter = OrderAdapter(allCarts)
            cartRecyclerView.layoutManager = LinearLayoutManager(this@OrderActivity)
            cartRecyclerView.adapter = orderAdapter
        }
    }
}





