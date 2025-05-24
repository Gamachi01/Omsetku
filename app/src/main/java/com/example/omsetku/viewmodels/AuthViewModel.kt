package com.example.omsetku.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omsetku.firebase.FirebaseModule
import com.example.omsetku.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authRepository = FirebaseModule.authRepository
    private val firestoreRepository = FirebaseModule.firestoreRepository
    
    // State untuk loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // State untuk error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // State untuk user saat ini
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    // State untuk status login
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    init {
        // Cek apakah user sudah login saat ViewModel dibuat
        checkLoginStatus()
    }
    
    /**
     * Melakukan login user
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Login menggunakan Firebase Auth
                val firebaseUser = authRepository.login(email, password)
                
                // Mendapatkan data user dari Firestore
                val userData = firestoreRepository.getUserData(firebaseUser.uid)
                _currentUser.value = User.fromMap(userData)
                _isLoggedIn.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal login"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Mendaftarkan user baru
     */
    fun register(name: String, email: String, password: String, phone: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Register menggunakan Firebase Auth
                val firebaseUser = authRepository.register(email, password)
                
                // Menyimpan data user ke Firestore
                firestoreRepository.saveUserData(
                    userId = firebaseUser.uid,
                    name = name,
                    email = email,
                    phone = phone
                )
                
                // Update state
                _currentUser.value = User(
                    id = firebaseUser.uid,
                    name = name,
                    email = email,
                    phone = phone,
                    createdAt = System.currentTimeMillis()
                )
                _isLoggedIn.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal mendaftar"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Logout user
     */
    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                authRepository.logout()
                _currentUser.value = null
                _isLoggedIn.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal logout"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Mengecek status login user
     */
    private fun checkLoginStatus() {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                val isUserLoggedIn = authRepository.isUserLoggedIn()
                _isLoggedIn.value = isUserLoggedIn
                
                if (isUserLoggedIn) {
                    // Mendapatkan data user dari Firestore jika sudah login
                    val currentUserId = authRepository.getCurrentUser()?.uid ?: return@launch
                    val userData = firestoreRepository.getUserData(currentUserId)
                    _currentUser.value = User.fromMap(userData)
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal mendapatkan status login"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Reset password
     */
    fun resetPassword(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                authRepository.sendPasswordResetEmail(email)
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal mengirim email reset password"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }
} 