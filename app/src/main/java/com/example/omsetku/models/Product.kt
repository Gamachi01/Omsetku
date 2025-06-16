package com.example.omsetku.models

data class Product(
    val id: String = "",
    val firestoreId: String = "",
    val ownerId: String = "",
    val name: String = "",
    val price: Long = 0,
    val imageUrl: String = "",
    val description: String = "",
    val createdAt: Long = 0,
    val imageRes: Int = 0,
    val hpp: Double = 0.0,
    val quantity: Int = 0
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Product {
            return Product(
                id = map["id"] as? String ?: "",
                firestoreId = map["firestoreId"] as? String ?: "",
                ownerId = map["ownerId"] as? String ?: "",
                name = map["name"] as? String ?: "",
                price = (map["price"] as? Number)?.toLong() ?: 0,
                imageUrl = map["imageUrl"] as? String ?: "",
                description = map["description"] as? String ?: "",
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: 0,
                imageRes = (map["imageRes"] as? Number)?.toInt() ?: 0,
                hpp = (map["hpp"] as? Number)?.toDouble() ?: 0.0,
                quantity = (map["quantity"] as? Number)?.toInt() ?: 0
            )
        }
    }

    val formattedPrice: String
        get() = "Rp ${price.toString().reversed().chunked(3).joinToString(".").reversed()}"
}