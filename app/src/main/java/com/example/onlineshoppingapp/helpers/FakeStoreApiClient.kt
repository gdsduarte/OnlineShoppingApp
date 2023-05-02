package com.example.onlineshoppingapp.helpers

import android.content.SharedPreferences
import android.util.Log
import com.example.onlineshoppingapp.models.Cart
import com.example.onlineshoppingapp.models.CartItem
import com.example.onlineshoppingapp.models.Product
import com.example.onlineshoppingapp.models.Token
import com.example.onlineshoppingapp.models.User
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException

class FakeStoreApiClient {

    private lateinit var sharedPreferences: SharedPreferences
    private val client = OkHttpClient()
    private val baseUrl = "https://fakestoreapi.com"

    private fun get(url: String): String {
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body!!.string()
        }
    }

    private fun post(url: String, json: Map<String, String>): String {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val jsonString = Gson().toJson(json)
        val body: RequestBody = jsonString.toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body!!.string()
        }
    }

    suspend fun loginUser(username: String, password: String): Token? {
        return withContext(Dispatchers.IO) {
            try {
                val response = post("$baseUrl/auth/login", mapOf("username" to username, "password" to password))
                Log.d("FakeStoreApiClient", "API response: $response")
                Gson().fromJson(response, Token::class.java)
            } catch (e: Exception) {
                Log.e("FakeStoreApiClient", "Error logging in user", e)
                null
            }
        }
    }

    suspend fun getUser(token: String): User? {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$baseUrl/users")
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    val responseBody = response.body!!.string()
                    Log.d("FakeStoreApiClient", "API response: $responseBody")
                    val userList = Gson().fromJson(responseBody, Array<User>::class.java)
                    if (userList.isNotEmpty()) userList[0] else null
                }
            } catch (e: Exception) {
                Log.e("FakeStoreApiClient", "Error getting user", e)
                null
            }
        }
    }

    fun getUser(token: Int): User? {
        val response = get("$baseUrl/users/$token")
        Log.d("FakeStoreApiClient", "API response: $response")
        return Gson().fromJson(response, User::class.java)
    }

    fun getProducts(): List<Product> {
        val response = get("$baseUrl/products")
        Log.d("FakeStoreApiClient", "API response: $response")
        return Gson().fromJson(response, Array<Product>::class.java).toList()
    }

    fun getProductById(id: Int): Product? {
        val response = get("$baseUrl/products/$id")
        Log.d("FakeStoreApiClient", "API response: $response")
        return Gson().fromJson(response, Product::class.java)
    }

    fun getProductCategories(): List<String> {
        val response = get("$baseUrl/products/categories")
        Log.d("FakeStoreApiClient", "API response: $response")
        return Gson().fromJson(response, Array<String>::class.java).toList()
    }

    fun getProductsByCategory(category: String): List<Product> {
        val response = get("$baseUrl/products/category/$category")
        Log.d("FakeStoreApiClient", "API response: $response")
        return Gson().fromJson(response, Array<Product>::class.java).toList()
    }

    suspend fun getCartItems(userId: Int): List<Cart> {
        return withContext(Dispatchers.IO) {
            try {
                val response = get("$baseUrl/carts/user/$userId")
                Log.d("FakeStoreApiClient", "API response: $response")
                Gson().fromJson(response, Array<Cart>::class.java).toList()
            } catch (e: Exception) {
                Log.e("FakeStoreApiClient", "Error getting cart items", e)
                emptyList()
            }
        }
    }

    fun addToCart(userId: Int, cartItem: CartItem): Product? {
        val response = post("$baseUrl/carts", mapOf("productId" to cartItem.productId.toString()))
        Log.d("FakeStoreApiClient", "API response: $response")
        return Gson().fromJson(response, Product::class.java)
    }

    fun removeFromCart(userId: Int, cartItem: CartItem): Product? {
        // Use the correct API endpoint for removing an item from the cart
        val response = post("$baseUrl/carts/user/$userId/remove", mapOf("productId" to cartItem.productId.toString()))
        Log.d("FakeStoreApiClient", "API response: $response")
        return Gson().fromJson(response, Product::class.java)
    }

    suspend fun clearCart(userId: Int) {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$baseUrl/carts/user/$userId/clear")
                    .build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                }
            } catch (e: Exception) {
                Log.e("FakeStoreApiClient", "Error clearing cart", e)
            }
        }
    }

}
