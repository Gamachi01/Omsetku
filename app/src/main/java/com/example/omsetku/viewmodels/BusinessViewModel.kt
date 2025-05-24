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
    
    // State untuk bisnis saat ini
    private val _businessList = MutableStateFlow<List<Business>>(emptyList())
    val businessList: StateFlow<List<Business>> = _businessList.asStateFlow()
    
    // State untuk bisnis yang dipilih
    private val _selectedBusiness = MutableStateFlow<Business?>(null)
    val selectedBusiness: StateFlow<Business?> = _selectedBusiness.asStateFlow()
    
    init {
        // Load daftar bisnis saat ViewModel dibuat
        loadBusinesses()
    }
    
    /**
     * Memuat daftar bisnis
     */
    fun loadBusinesses() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val businesses = firestoreRepository.getUserBusinesses()
                _businessList.value = businesses.map { Business.fromMap(it) }
                
                // Select the first business if available and none is selected
                if (_selectedBusiness.value == null && _businessList.value.isNotEmpty()) {
                    _selectedBusiness.value = _businessList.value.first()
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal memuat data bisnis"
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
                loadBusinesses()
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal membuat bisnis baru"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Memilih bisnis
     */
    fun selectBusiness(business: Business) {
        _selectedBusiness.value = business
    }
    
    /**
     * Memilih bisnis berdasarkan ID
     */
    fun selectBusinessById(businessId: String) {
        _businessList.value.find { it.id == businessId }?.let {
            _selectedBusiness.value = it
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }
} 