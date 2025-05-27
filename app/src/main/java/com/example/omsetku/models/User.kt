package com.example.omsetku.models

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val gender: String = "",
    val position: String = "",
    val createdAt: Long = 0
) {
    companion object {
        fun fromMap(map: Map<String, Any>): User {
            return User(
                id = map["id"] as? String ?: "",
                name = map["name"] as? String ?: "",
                email = map["email"] as? String ?: "",
                phone = map["phone"] as? String ?: "",
                gender = map["gender"] as? String ?: "",
                position = map["position"] as? String ?: "",
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: 0
            )
        }
    }
}