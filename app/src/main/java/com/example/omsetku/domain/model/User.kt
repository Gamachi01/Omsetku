package com.example.omsetku.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val businessName: String? = null
) 