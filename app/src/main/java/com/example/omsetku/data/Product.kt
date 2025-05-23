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
    private val products = mutableListOf(
        Product("1", "Cappucino", 25000.0, "Minuman"),
        Product("2", "Americano", 20000.0, "Minuman"),
        Product("3", "Espresso", 18000.0, "Minuman"),
        Product("4", "Latte", 23000.0, "Minuman")
    )

    override fun getAllProducts(): List<Product> = products

    override fun getProductById(id: String): Product? = products.find { it.id == id }

    override fun addProduct(product: Product) {
        products.add(product)
    }

    override fun updateProduct(product: Product) {
        val index = products.indexOfFirst { it.id == product.id }
        if (index != -1) {
            products[index] = product
        }
    }

    override fun deleteProduct(id: String) {
        products.removeIf { it.id == id }
    }
} 