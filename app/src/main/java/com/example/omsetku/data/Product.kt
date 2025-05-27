package com.example.omsetku.data

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val description: String = "",
    val imageUrl: String = "",
    val isAvailable: Boolean = true
)

// Repository interface
interface ProductRepository {
    fun getAllProducts(): List<Product>
    fun getProductById(id: String): Product?
    fun addProduct(product: Product)
    fun updateProduct(product: Product)
    fun deleteProduct(id: String)
}

// Implementasi repository dengan data dummy untuk testing
class DummyProductRepository : ProductRepository {
    override fun getAllProducts(): List<Product> {
        return listOf(
            Product("1", "Kopi Susu", 15000.0, "Minuman"),
            Product("2", "Americano", 18000.0, "Minuman"),
            Product("3", "Cappuccino", 20000.0, "Minuman"),
            Product("4", "Latte", 22000.0, "Minuman")
        )
    }

    override fun getProductById(id: String): Product? = getAllProducts().find { it.id == id }

    override fun addProduct(product: Product) {
        // Implementation needed
    }

    override fun updateProduct(product: Product) {
        // Implementation needed
    }

    override fun deleteProduct(id: String) {
        // Implementation needed
    }
}