package com.gds.onlineshoppingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gds.onlineshoppingapp.adapters.CategoryAdapter
import com.gds.onlineshoppingapp.helpers.FakeStoreApiClient
import com.gds.onlineshoppingapp.helpers.SharedPreferencesHelper
import com.gds.onlineshoppingapp.models.Category
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var fakeStoreApiClient: FakeStoreApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize shared preferences helper and fake store api client
        sharedPreferencesHelper = SharedPreferencesHelper(this)
        fakeStoreApiClient = FakeStoreApiClient()

        // Setup bottom navigation view
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavView)
        bottomNavigationView.selectedItemId = R.id.home
        bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.home -> true
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
                R.id.profile -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }

        fetchCategories()
    }

    // Check if user token is present in shared preferences
    private fun checkUserToken() {
        val token = sharedPreferencesHelper.getUserToken()
        if (token == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Fetch categories from fake store api
    private fun fetchCategories() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val categories = withContext(Dispatchers.IO) {
                    fakeStoreApiClient.getProductCategories()
                }
                val categoryList = mutableListOf<Category>()
                for (i in categories.indices) {
                    categoryList.add(Category(i, categories[i]))
                }
                setupCategoryRecyclerView(categoryList)
            } catch (e: Exception) {
                Toast.makeText(this@HomeActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Setup category recycler view
    private fun setupCategoryRecyclerView(categories: List<Category>) {
        val categoryRecyclerView: RecyclerView = findViewById(R.id.categoryRecyclerView)
        val categoryAdapter = CategoryAdapter(categories) { category ->
            val intent = Intent(this@HomeActivity, ProductActivity::class.java)
            intent.putExtra("selectedCategory", category.name)
            startActivity(intent)
        }

        // Set layout manager and adapter for category recycler view
        categoryRecyclerView.layoutManager = GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false)
        categoryRecyclerView.adapter = categoryAdapter
    }

    override fun onResume() {
        super.onResume()
        checkUserToken()
    }
}