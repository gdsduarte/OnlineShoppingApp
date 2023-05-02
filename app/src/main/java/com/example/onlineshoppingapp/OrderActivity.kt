package com.example.onlineshoppingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineshoppingapp.adapters.OrderAdapter
import com.example.onlineshoppingapp.helpers.CartUtils
import com.example.onlineshoppingapp.helpers.FakeStoreApiClient
import com.example.onlineshoppingapp.helpers.SharedPreferencesHelper
import com.example.onlineshoppingapp.models.Cart
import com.example.onlineshoppingapp.models.CartItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var fakeStoreApiClient: FakeStoreApiClient
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var carts: List<Cart>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        sharedPreferencesHelper = SharedPreferencesHelper(this)
        fakeStoreApiClient = FakeStoreApiClient()

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

        val clearCartButton = findViewById<Button>(R.id.clearCartButton)
        clearCartButton.setOnClickListener {
            sharedPreferencesHelper.clearPlacedOrders()
            fetchAndSetupUI()
        }

        fetchAndSetupUI()
    }

    private fun fetchAndSetupUI() {
        launch(Dispatchers.Main) {
//            carts = getCarts(sharedPreferencesHelper, fakeStoreApiClient).toMutableList()
            carts = CartUtils.getCarts(sharedPreferencesHelper, fakeStoreApiClient).toMutableList()


            // Load the placed orders from SharedPreferences
            val placedOrders = sharedPreferencesHelper.loadPlacedOrders()

            // Fetch carts from the API
            val apiCarts = CartUtils.getCarts(sharedPreferencesHelper, fakeStoreApiClient)

            Log.d("OrderActivity", "carts size: ${carts.size}")
            Log.d("OrderActivity", "placedOrders size: ${placedOrders.size}")
            Log.d("OrderActivity", "apiCarts size: ${apiCarts.size}")

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


//    suspend fun getCarts(sharedPreferencesHelper: SharedPreferencesHelper, fakeStoreApiClient: FakeStoreApiClient): MutableList<Cart> = withContext(Dispatchers.IO) {
//        val userId = sharedPreferencesHelper.getUserId()
//        val carts = userId?.let { fakeStoreApiClient.getCartItems(it) } ?: emptyList()
//        val products = fakeStoreApiClient.getProducts()
//
//        // Map cart items to carts with actual products
//        carts.map { cart ->
//            Cart(
//                id = cart.id,
//                userId = cart.userId,
//                date = cart.date,
//                products = cart.products.mapNotNull { cartItem ->
//                    val product = products.find { it.id == cartItem.productId }
//                    if (product != null) {
//                        Log.d("CartActivity", "Found product for cart item: ${product.title}")
//                        CartItem(
//                            productId = product.id,
//                            quantity = cartItem.quantity,
//                            product = product
//                        )
//                    } else {
//                        Log.d("CartActivity", "Product not found for cart item with product ID: ${cartItem.productId}")
//                        null
//                    }
//                } as MutableList<CartItem>
//            )
//        }
//    } as MutableList<Cart>
}





