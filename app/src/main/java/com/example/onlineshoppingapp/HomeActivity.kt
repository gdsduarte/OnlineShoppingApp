package com.example.onlineshoppingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineshoppingapp.adapters.CategoryAdapter
import com.example.onlineshoppingapp.helpers.SharedPreferencesHelper
import com.example.onlineshoppingapp.models.Category
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.json.JSONArray

class HomeActivity : AppCompatActivity() {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sharedPreferencesHelper = SharedPreferencesHelper(this)

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

    override fun onResume() {
        super.onResume()
        checkUserToken()
    }

    private fun checkUserToken() {
        val token = sharedPreferencesHelper.getUserToken()
        if (token == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchCategories() {
        val request = Request.Builder()
            .url("https://fakestoreapi.com/products/categories")
            .get()
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle error
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val categoriesJsonArray = JSONArray(response.body?.string())
                    val categories = mutableListOf<Category>()
                    for (i in 0 until categoriesJsonArray.length()) {
                        categories.add(Category(i, categoriesJsonArray.getString(i)))
                    }

                    runOnUiThread {
                        setupCategoryRecyclerView(categories)
                    }
                }
            }
        })
    }

    private fun setupCategoryRecyclerView(categories: List<Category>) {
        val categoryRecyclerView: RecyclerView = findViewById(R.id.categoryRecyclerView)
        val categoryAdapter = CategoryAdapter(categories) { category ->
            // Handle category click
            val intent = Intent(this@HomeActivity, ProductActivity::class.java)
            intent.putExtra("selectedCategory", category.name)
            startActivity(intent)
        }

        categoryRecyclerView.layoutManager = GridLayoutManager(this, 2) // Change the number 2 to the number of columns you want
        categoryRecyclerView.adapter = categoryAdapter
    }
}