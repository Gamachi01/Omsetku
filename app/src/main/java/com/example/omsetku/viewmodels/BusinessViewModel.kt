package com.example.omsetku.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omsetku.firebase.FirebaseModule
import com.example.omsetku.models.Business
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BusinessViewModel : ViewModel() {
    private val firestoreRepository = FirebaseModule.firestoreRepository
    private val storageRepository = FirebaseModule.storageRepository
    
    // State untuk loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // State untuk error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // State untuk status sukses
    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()
    
    // State untuk data bisnis saat ini
    private val _currentBusiness = MutableStateFlow<Business?>(null)
    val currentBusiness: StateFlow<Business?> = _currentBusiness.asStateFlow()
    
    init {
        // Load data bisnis saat ViewModel dibuat
        loadBusinessData()
    }
    
    /**
     * Menyimpan data bisnis
     */
    fun saveBusinessData(
        name: String,
        type: String,
        address: String,
        email: String? = null,
        phone: String? = null,
        logo: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _isSuccess.value = false
            
            try {
                val businessId = firestoreRepository.saveBusinessData(
                    name = name,
                    type = type,
                    address = address,
                    email = email,
                    phone = phone,
                    logo = logo
                )
                
                // Refresh data bisnis
                loadBusinessData()
                
                // Set status sukses
                _isSuccess.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal menyimpan data bisnis"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Memuat data bisnis dari Firestore
     */
    fun loadBusinessData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val businesses = firestoreRepository.getUserBusinesses()
                
                if (businesses.isNotEmpty()) {
                    // Ambil bisnis pertama (untuk saat ini cukup 1 bisnis per user)
                    val businessData = businesses.first()
                    _currentBusiness.value = Business.fromMap(businessData)
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal memuat data bisnis"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Mengupdate data bisnis
     */
    fun updateBusinessData(
        businessId: String,
        name: String? = null,
        type: String? = null,
        address: String? = null,
        email: String? = null,
        phone: String? = null,
        logo: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Update di Firestore
                firestoreRepository.updateBusinessData(
                    businessId = businessId,
                    name = name,
                    type = type,
                    address = address,
                    email = email,
                    phone = phone,
                    logo = logo
                )
                
                // Refresh data bisnis
                loadBusinessData()
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal mengupdate data bisnis"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Membuat bisnis baru
     */
    fun createBusiness(
        name: String,
        type: String,
        address: String,
        email: String? = null,
        phone: String? = null,
        logoUri: Uri? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Upload logo jika ada
                var logoUrl: String? = null
                if (logoUri != null) {
                    logoUrl = storageRepository.uploadBusinessLogo(logoUri)
                }
                
                // Simpan data bisnis
                val businessId = firestoreRepository.saveBusinessData(
                    name = name,
                    type = type,
                    address = address,
                    email = email,
                    phone = phone,
                    logo = logoUrl
                )
                
                // Refresh daftar bisnis
                loadBusinessData()
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal membuat bisnis baru"
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