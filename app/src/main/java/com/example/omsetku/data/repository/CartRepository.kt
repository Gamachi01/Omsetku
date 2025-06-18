package com.example.omsetku.data.repository

import com.example.omsetku.data.local.CartItemDao
import com.example.omsetku.data.local.CartItemEntity
import com.example.omsetku.data.remote.model.CartItemDto
import com.example.omsetku.domain.model.CartItem
import com.example.omsetku.domain.repository.CartRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.firstOrNull

class CartRepositoryImpl(
    private val cartItemDao: CartItemDao,
    private val firestore: FirebaseFirestore
) : CartRepository {
    override fun getCartItems(): Flow<List<CartItem>> =
        cartItemDao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun addCartItem(item: CartItem) {
        cartItemDao.insert(item.toEntity())
        firestore.collection("cart_items").document(item.id).set(item.toDto()).await()
    }

    override suspend fun updateCartItem(item: CartItem) {
        cartItemDao.insert(item.toEntity())
        firestore.collection("cart_items").document(item.id).set(item.toDto()).await()
    }

    override suspend fun deleteCartItem(id: String) {
        val entity = cartItemDao.getAll().firstOrNull()?.find { it.id == id }
        entity?.let { cartItemDao.delete(it) }
        firestore.collection("cart_items").document(id).delete().await()
    }

    override suspend fun clearCart() {
        cartItemDao.deleteAll()
        // Hapus semua cart di Firestore (opsional, bisa disesuaikan kebutuhan)
        val snapshot = firestore.collection("cart_items").get().await()
        for (doc in snapshot.documents) {
            doc.reference.delete().await()
        }
    }
}

// Mapping extension
fun CartItemEntity.toDomain() = CartItem(
    id = id,
    productId = productId,
    name = name,
    price = price,
    quantity = quantity,
    imageUrl = imageUrl,
    hpp = hpp,
    subtotal = subtotal
)

fun CartItem.toEntity() = CartItemEntity(
    id = id,
    productId = productId,
    name = name,
    price = price,
    quantity = quantity,
    imageUrl = imageUrl,
    hpp = hpp,
    subtotal = subtotal
)

fun CartItem.toDto() = CartItemDto(
    id = id,
    productId = productId,
    name = name,
    price = price,
    quantity = quantity,
    imageUrl = imageUrl,
    hpp = hpp,
    subtotal = subtotal
) 