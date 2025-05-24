package com.example.omsetku.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omsetku.firebase.FirebaseModule
import com.example.omsetku.models.TaxSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaxViewModel : ViewModel() {
    private val firestoreRepository = FirebaseModule.firestoreRepository
    private val authRepository = FirebaseModule.authRepository

    private val _taxSettings = MutableStateFlow(TaxSettings())
    val taxSettings: StateFlow<TaxSettings> = _taxSettings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadTaxSettings()
    }

    fun loadTaxSettings() {
        val currentUser = authRepository.getCurrentUser() ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val settings = firestoreRepository.getTaxSettings(currentUser.uid)
                _taxSettings.value = settings ?: TaxSettings(userId = currentUser.uid)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Gagal memuat pengaturan pajak: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateTaxSettings(enabled: Boolean, rate: Int) {
        val currentUser = authRepository.getCurrentUser() ?: return
        val updatedSettings = _taxSettings.value.copy(
            userId = currentUser.uid,
            enabled = enabled,
            rate = rate,
            updatedAt = System.currentTimeMillis()
        )
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                firestoreRepository.saveTaxSettings(updatedSettings)
                _taxSettings.value = updatedSettings
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Gagal menyimpan pengaturan pajak: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
} 