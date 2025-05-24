package com.gds.onlineshoppingapp.helpers

import android.util.Log
import com.gds.onlineshoppingapp.models.Cart
import com.gds.onlineshoppingapp.models.Product
import com.gds.onlineshoppingapp.models.Token
import com.gds.onlineshoppingapp.models.User
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException

class FakeStoreApiClient {

    private val client = OkHttpClient()
    private val baseUrl = "https://fakestoreapi.com"

    private fun makeGetRequest(url: String): String {
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body!!.string()
        }
    }

    private fun makePostRequest(url: String, json: Map<String, String>): String {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val jsonString = Gson().toJson(json)
        val requestBody: RequestBody = jsonString.toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body!!.string()
        }
    }

    suspend fun loginUser(username: String, password: String): Token? {
        return withContext(Dispatchers.IO) {
            try {
                val response = makePostRequest("$baseUrl/auth/login", mapOf("username" to username, "password" to password))
                Log.d("FakeStoreApiClient", "API response: $response")
                Gson().fromJson(response, Token::class.java)
            } catch (e: Exception) {
                Log.e("FakeStoreApiClient", "Error logging in user", e)
                null
            }
        }
    }

    suspend fun registerUser(username: String, email: String, password: String): Token? {
        val requestBody = FormBody.Builder()
            .add("username", username)
            .add("email", email)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url("$baseUrl/users")
            .post(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Log.d("FakeStoreApiClient", "Response body: $responseBody")
                val token = Gson().fromJson(responseBody, Token::class.java)
                token
            } else {
                null
            }
        }
    }

    suspend fun getUser(token: String, username: String): User? {
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
                    userList.find { it.username == username }
                }
            } catch (e: Exception) {
                Log.e("FakeStoreApiClient", "Error getting user", e)
                null
            }
        }
    }

    fun getUser(token: Int): User? {
        val response = makeGetRequest("$baseUrl/users/$token")
        Log.d("FakeStoreApiClient", "API response: $response")
        return Gson().fromJson(response, User::class.java)
    }

    fun getProducts(): List<Product> {
        val response = makeGetRequest("$baseUrl/products")
        Log.d("FakeStoreApiClient", "API response: $response")
        return Gson().fromJson(response, Array<Product>::class.java).toList()
    }

    fun getProductCategories(): List<String> {
        val response = makeGetRequest("$baseUrl/products/categories")
        Log.d("FakeStoreApiClient", "API response: $response")
        return Gson().fromJson(response, Array<String>::class.java).toList()
    }

    fun getProductsByCategory(category: String): List<Product> {
        val response = makeGetRequest("$baseUrl/products/category/$category")
        Log.d("FakeStoreApiClient", "API response: $response")
        return Gson().fromJson(response, Array<Product>::class.java).toList()
    }

    suspend fun getCartItems(userId: Int): List<Cart> {
        return withContext(Dispatchers.IO) {
            try {
                val response = makeGetRequest("$baseUrl/carts/user/$userId")
                Log.d("FakeStoreApiClient", "API response: $response")
                Gson().fromJson(response, Array<Cart>::class.java).toList()
            } catch (e: Exception) {
                Log.e("FakeStoreApiClient", "Error getting cart items", e)
                emptyList()
            }
        }
    }
}

