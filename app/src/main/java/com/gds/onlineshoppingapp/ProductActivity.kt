package com.gds.onlineshoppingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gds.onlineshoppingapp.adapters.ProductAdapter
import com.gds.onlineshoppingapp.helpers.AppCart
import com.gds.onlineshoppingapp.helpers.FakeStoreApiClient
import com.gds.onlineshoppingapp.helpers.SharedPreferencesHelper
import com.gds.onlineshoppingapp.models.Product
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProductActivity : AppCompatActivity() {

    private lateinit var fakeStoreApiClient: FakeStoreApiClient
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var selectedCategoryId: Int = 0
    private val cart = AppCart.cart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        // Initialize the API client and shared preferences helper
        fakeStoreApiClient = FakeStoreApiClient()
        sharedPreferencesHelper = SharedPreferencesHelper(this)

        // Set the bottom navigation view
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavView)
        bottomNavigationView.selectedItemId = R.id.shop
        bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.shop -> true
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
                R.id.profile -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }

        setupCategoryButtons()

        // Check if a category was selected from the home activity
        val selectedCategory = intent.getStringExtra("selectedCategory")
        if (selectedCategory != null) {
            onCategoryButtonClick(selectedCategory)
        } else {
            fetchProducts()
        }

    }

    //
    private fun setupCategoryButtons() {
        val allButton: Button = findViewById(R.id.allButton)
        val electronicsButton: Button = findViewById(R.id.electronicsButton)
        val jewelryButton: Button = findViewById(R.id.jewelryButton)
        val mensClothingButton: Button = findViewById(R.id.mensClothingButton)
        val womensClothingButton: Button = findViewById(R.id.womensClothingButton)

        // Set the on click listeners for the category buttons
        allButton.setOnClickListener { onCategoryButtonClick("All") }
        electronicsButton.setOnClickListener { onCategoryButtonClick("electronics") }
        jewelryButton.setOnClickListener { onCategoryButtonClick("jewelery") }
        mensClothingButton.setOnClickListener { onCategoryButtonClick("men's clothing") }
        womensClothingButton.setOnClickListener { onCategoryButtonClick("women's clothing") }

        // Set the initial selected button
        allButton.isSelected = true
        selectedCategoryId = allButton.id
    }

    // Function to handle the category button clicks
    private fun onCategoryButtonClick(category: String) {
        val allButton: Button = findViewById(R.id.allButton)
        val electronicsButton: Button = findViewById(R.id.electronicsButton)
        val jewelryButton: Button = findViewById(R.id.jewelryButton)
        val mensClothingButton: Button = findViewById(R.id.mensClothingButton)
        val womensClothingButton: Button = findViewById(R.id.womensClothingButton)

        // Deselect all buttons
        allButton.isSelected = false
        electronicsButton.isSelected = false
        jewelryButton.isSelected = false
        mensClothingButton.isSelected = false
        womensClothingButton.isSelected = false

        // Select the corresponding button based on the category
        when (category) {
            "All" -> allButton.isSelected = true
            "electronics" -> electronicsButton.isSelected = true
            "jewelery" -> jewelryButton.isSelected = true
            "men's clothing" -> mensClothingButton.isSelected = true
            "women's clothing" -> womensClothingButton.isSelected = true
        }

        // Fetch products based on the selected category
        if (category == "All") {
            fetchProducts()
        } else {
            fetchProductsByCategory(category)
        }
    }

    // Function to handle the product item clicks and navigate to the product description activity
    private fun onItemClick(selectedProduct: Product) {
        val intent = Intent(this, ProductDescriptionActivity::class.java)
        intent.putExtra("product", selectedProduct)
        intent.putExtra("cart", cart)
        startActivity(intent)
    }

    // Function to fetch all products from the API
    private fun fetchProducts() {
        GlobalScope.launch {
            val products = fakeStoreApiClient.getProducts()
            runOnUiThread {
                setupProductRecyclerView(products)
            }
        }
    }

    // Function to fetch products by category from the API
    private fun fetchProductsByCategory(category: String) {
        GlobalScope.launch {
            val products = fakeStoreApiClient.getProductsByCategory(category)
            runOnUiThread {
                setupProductRecyclerView(products)
            }
        }
    }

    // Function to setup the product recycler view with the products
    private fun setupProductRecyclerView(products: List<Product>) {
        val productRecyclerView: RecyclerView = findViewById(R.id.productRecyclerView)
        val productAdapter = ProductAdapter(products) { product ->
            onItemClick(product)
        }
        productRecyclerView.layoutManager = LinearLayoutManager(this)
        productRecyclerView.adapter = productAdapter
    }
}
