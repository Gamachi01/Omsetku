package com.example.omsetku.models

/**
 * Model untuk pengaturan pajak
 */
data class TaxSettings(
    val id: String = "",
    val userId: String = "",
    val enabled: Boolean = false,
    val rate: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        fun fromMap(map: Map<String, Any>): TaxSettings {
            return TaxSettings(
                id = map["id"] as? String ?: "",
                userId = map["userId"] as? String ?: "",
                enabled = map["enabled"] as? Boolean ?: false,
                rate = (map["rate"] as? Number)?.toInt() ?: 0,
                updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: 0
            )
        }

        fun toMap(taxSettings: TaxSettings): Map<String, Any> {
            return mapOf(
                "id" to taxSettings.id,
                "userId" to taxSettings.userId,
                "enabled" to taxSettings.enabled,
                "rate" to taxSettings.rate,
                "updatedAt" to taxSettings.updatedAt
            )
        }
    }
}