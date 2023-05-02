package com.example.onlineshoppingapp.helpers

import android.content.Context
import android.content.SharedPreferences
import com.example.onlineshoppingapp.models.Cart
import com.example.onlineshoppingapp.models.Token
import com.example.onlineshoppingapp.models.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesHelper(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveUser(user: User) {
        sharedPreferences.edit().apply {
            putString("USER", user.toString())
            apply()
        }
    }

    fun saveUserToken(token: Token?) {
        sharedPreferences.edit().apply {
            putString("USER_TOKEN", token.toString())
            apply()
        }
    }

    fun getUserToken(): String? {
        return sharedPreferences.getString("USER_TOKEN", null)
    }

    fun clearUserToken() {
        sharedPreferences.edit().apply {
            remove("USER_TOKEN")
            apply()
        }
    }

    fun getUserId(): Int? {
        return sharedPreferences.getInt("USER_ID", -1)
    }

    fun saveUserId(userId: Int) {
        sharedPreferences.edit().apply {
            putInt("USER_ID", userId)
            apply()
        }
    }

    fun clearUserId() {
        sharedPreferences.edit().apply {
            remove("USER_ID")
            apply()
        }
    }

    fun saveCart(cart: Cart) {
        val cartJson = gson.toJson(cart)
        sharedPreferences.edit().apply {
            putString("CART", cartJson)
            apply()
        }
    }

    fun getCart(): Cart? {
        val cartJson = sharedPreferences.getString("CART", null)
        return if (cartJson != null) {
            val cartType = object : TypeToken<Cart>() {}.type
            gson.fromJson(cartJson, cartType)
        } else {
            null
        }
    }

    fun clearCart() {
        sharedPreferences.edit().apply {
            remove("CART")
            apply()
        }
    }

    fun saveProductQuantity(productId: Int, quantity: Int) {
        sharedPreferences.edit().apply {
            putInt("PRODUCT_QUANTITY_$productId", quantity)
            apply()
        }
    }

    fun getProductQuantity(productId: Int): Int {
        return sharedPreferences.getInt("PRODUCT_QUANTITY_$productId", 1)
    }

    fun savePlacedOrders(orders: List<Cart>) {
        val jsonString = Gson().toJson(orders)
        sharedPreferences.edit().putString("placed_orders", jsonString).apply()
    }

    fun loadPlacedOrders(): List<Cart> {
        val jsonString = sharedPreferences.getString("placed_orders", null) ?: return emptyList()
        val type = object : TypeToken<List<Cart>>() {}.type
        return Gson().fromJson(jsonString, type)
    }

    fun clearPlacedOrders() {
        sharedPreferences.edit().apply {
            remove("placed_orders")
            apply()
        }
    }


    companion object {
        private var INSTANCE: SharedPreferencesHelper? = null

        fun getInstance(context: Context): SharedPreferencesHelper {
            if (INSTANCE == null) {
                INSTANCE = SharedPreferencesHelper(context.applicationContext)
            }
            return INSTANCE!!
        }
    }

}
