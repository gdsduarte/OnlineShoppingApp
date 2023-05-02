package com.example.onlineshoppingapp.helpers

import com.example.onlineshoppingapp.models.Cart
import com.example.onlineshoppingapp.models.CartItem

object AppCart {
    var cart = Cart(id = 0, userId = 0, date = "", products = mutableListOf())
    val productQuantities = mutableMapOf<Int, Int>()

    fun addItem(item: CartItem) {
        val existingItem = cart.products.find { it.productId == item.productId }
        if (existingItem != null) {
            existingItem.quantity += item.quantity
            existingItem.productQuantity = item.productQuantity
        } else {
            cart.products.add(item)
        }
    }

    fun updateQuantity(productId: Int, newQuantity: Int) {
        cart.products.find { it.productId == productId }?.quantity = newQuantity
    }

    fun updateProductQuantity(productId: Int, newProductQuantity: Int) {
        cart.products.find { it.productId == productId }?.productQuantity = newProductQuantity
    }

    fun clearCart() {
        cart.products = mutableListOf()
    }
}


