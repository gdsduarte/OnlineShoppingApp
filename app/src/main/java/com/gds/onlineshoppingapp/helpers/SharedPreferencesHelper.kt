package com.gds.onlineshoppingapp.helpers

import android.content.Context
import android.content.SharedPreferences
import com.gds.onlineshoppingapp.models.Cart
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesHelper(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveUserToken(token: String) {
        sharedPreferences.edit().apply {
            putString("USER_TOKEN", token)
            apply()
        }
    }

    fun getUserToken(): String? {
        return sharedPreferences.getString("USER_TOKEN", null)
    }

    fun saveUserId(userId: Int) {
        sharedPreferences.edit().apply {
            putInt("USER_ID", userId)
            apply()
        }
    }

    fun getUserId(): Int? {
        return sharedPreferences.getInt("USER_ID", -1)
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

    fun savePlacedOrders(orders: List<Cart>) {
        val jsonString = Gson().toJson(orders)
        sharedPreferences.edit().putString("placed_orders", jsonString).apply()
    }

    fun loadPlacedOrders(): List<Cart> {
        val jsonString = sharedPreferences.getString("placed_orders", null) ?: return emptyList()
        val type = object : TypeToken<List<Cart>>() {}.type
        return Gson().fromJson(jsonString, type)
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
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
