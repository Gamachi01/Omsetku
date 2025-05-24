package com.example.omsetku.models

data class Business(
    val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val type: String = "",
    val address: String = "",
    val email: String = "",
    val phone: String = "",
    val logo: String = "",
    val createdAt: Long = 0
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Business {
            return Business(
                id = map["id"] as? String ?: "",
                ownerId = map["ownerId"] as? String ?: "",
                name = map["name"] as? String ?: "",
                type = map["type"] as? String ?: "",
                address = map["address"] as? String ?: "",
                email = map["email"] as? String ?: "",
                phone = map["phone"] as? String ?: "",
                logo = map["logo"] as? String ?: "",
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: 0
            )
        }
    }
} 