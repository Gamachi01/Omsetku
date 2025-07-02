package com.example.omsetku.di

import android.content.Context
import androidx.room.Room
import com.example.omsetku.data.local.AppDatabase
import com.example.omsetku.data.local.UserDao
import com.example.omsetku.data.local.TransactionDao
import com.example.omsetku.data.local.ProductDao
import com.example.omsetku.data.local.CartItemDao
import com.example.omsetku.data.repository.AuthRepositoryImpl
import com.example.omsetku.data.repository.UserRepositoryImpl
import com.example.omsetku.data.repository.TransactionRepositoryImpl
import com.example.omsetku.data.repository.ProductRepositoryImpl
import com.example.omsetku.data.repository.CartRepositoryImpl
import com.example.omsetku.domain.repository.AuthRepository
import com.example.omsetku.domain.repository.UserRepository
import com.example.omsetku.domain.repository.TransactionRepository
import com.example.omsetku.domain.repository.ProductRepository
import com.example.omsetku.domain.repository.CartRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.omsetku.data.AIPricingService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(firebaseAuth: FirebaseAuth): AuthRepository =
        AuthRepositoryImpl(firebaseAuth)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "omsetku_db").build()

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao, firestore: FirebaseFirestore): UserRepository =
        UserRepositoryImpl(userDao, firestore)

    @Provides
    fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()

    @Provides
    @Singleton
    fun provideTransactionRepository(transactionDao: TransactionDao, firestore: FirebaseFirestore): TransactionRepository =
        TransactionRepositoryImpl(transactionDao, firestore)

    @Provides
    fun provideProductDao(db: AppDatabase): ProductDao = db.productDao()

    @Provides
    @Singleton
    fun provideProductRepository(productDao: ProductDao, firestore: FirebaseFirestore): ProductRepository =
        ProductRepositoryImpl(productDao, firestore)

    @Provides
    fun provideCartItemDao(db: AppDatabase): CartItemDao = db.cartItemDao()

    @Provides
    @Singleton
    fun provideCartRepository(cartItemDao: CartItemDao, firestore: FirebaseFirestore): CartRepository =
        CartRepositoryImpl(cartItemDao, firestore)

    @Provides
    @Singleton
    fun provideAIPricingService(): AIPricingService {
        return AIPricingService()
    }
} 