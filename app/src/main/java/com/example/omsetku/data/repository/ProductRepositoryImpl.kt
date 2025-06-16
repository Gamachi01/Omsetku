package com.example.omsetku.data.repository

import com.example.omsetku.data.local.ProductDao
import com.example.omsetku.data.local.ProductEntity
import com.example.omsetku.data.remote.model.ProductDto
import com.example.omsetku.domain.model.Product
import com.example.omsetku.domain.repository.ProductRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.firstOrNull

class ProductRepositoryImpl(
    private val productDao: ProductDao,
    private val firestore: FirebaseFirestore
) : ProductRepository {
    override fun getProducts(): Flow<List<Product>> =
        productDao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getProductById(id: String): Product? =
        productDao.getAll().firstOrNull()?.find { it.id == id }?.toDomain()

    override suspend fun addProduct(product: Product) {
        productDao.insert(product.toEntity())
        firestore.collection("products").document(product.id).set(product.toDto()).await()
    }

    override suspend fun updateProduct(product: Product) {
        productDao.insert(product.toEntity())
        firestore.collection("products").document(product.id).set(product.toDto()).await()
    }

    override suspend fun deleteProduct(id: String) {
        val entity = productDao.getAll().firstOrNull()?.find { it.id == id }
        entity?.let { productDao.delete(it) }
        firestore.collection("products").document(id).delete().await()
    }
}

// Mapping extension
fun ProductEntity.toDomain() = Product(
    id = id,
    name = name,
    price = price,
    imageUrl = imageUrl,
    hpp = hpp,
    stock = stock
)

fun Product.toEntity() = ProductEntity(
    id = id,
    name = name,
    price = price,
    imageUrl = imageUrl,
    hpp = hpp,
    stock = stock
)

fun Product.toDto() = ProductDto(
    id = id,
    name = name,
    price = price,
    imageUrl = imageUrl,
    hpp = hpp,
    stock = stock
) 