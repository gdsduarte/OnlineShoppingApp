package com.gds.onlineshoppingapp.models

import java.io.Serializable

data class Cart(
    val id: Int,
    val userId: Int,
    val date: String,
    var products: MutableList<CartItem>,
    var isExpanded: Boolean = false,
    val items: MutableList<CartItem> = mutableListOf()
) : Serializable

data class CartItem(
    val productId: Int,
    var quantity: Int,
    val product: Product? = null,
    var productQuantity: Int = 1
) : Serializable