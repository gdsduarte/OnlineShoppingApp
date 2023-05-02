package com.example.onlineshoppingapp.models

import java.io.Serializable

data class Token(
    val token: String,
    val userId: Int
) : Serializable
