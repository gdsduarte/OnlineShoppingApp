package com.example.onlineshoppingapp.helpers

import android.util.Log
import com.example.onlineshoppingapp.models.Cart
import com.example.onlineshoppingapp.models.CartItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CartUtils {
    suspend fun getCarts(sharedPreferencesHelper: SharedPreferencesHelper, fakeStoreApiClient: FakeStoreApiClient): MutableList<Cart> = withContext(Dispatchers.IO) {
        val userId = sharedPreferencesHelper.getUserId()
        val carts = userId?.let { fakeStoreApiClient.getCartItems(it) } ?: emptyList()
        val products = fakeStoreApiClient.getProducts()

        carts.map { cart ->
            Cart(
                id = cart.id,
                userId = cart.userId,
                date = cart.date,
                products = cart.products.mapNotNull { cartItem ->
                    val product = products.find { it.id == cartItem.productId }
                    if (product != null) {
                        Log.d("CartActivity", "Found product for cart item: ${product.title}")
                        CartItem(
                            productId = product.id,
                            quantity = cartItem.quantity,
                            product = product
                        )
                    } else {
                        Log.d("CartActivity", "Product not found for cart item with product ID: ${cartItem.productId}")
                        null
                    }
                } as MutableList<CartItem>
            )
        }
    } as MutableList<Cart>
}