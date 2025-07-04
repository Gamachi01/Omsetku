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

    // State untuk status registrasi berhasil (untuk navigasi ke form data diri)
    private val _isRegistered = MutableStateFlow(false)
    val isRegistered: StateFlow<Boolean> = _isRegistered.asStateFlow()

    // State untuk status data diri tersimpan (untuk navigasi ke form bisnis)
    private val _personalDataSaved = MutableStateFlow(false)
    val personalDataSaved: StateFlow<Boolean> = _personalDataSaved.asStateFlow()

    // State untuk status data bisnis tersimpan (untuk navigasi ke home)
    private val _businessDataSaved = MutableStateFlow(false)
    val businessDataSaved: StateFlow<Boolean> = _businessDataSaved.asStateFlow()

    // State untuk menampilkan dialog error
    private val _showErrorDialog = MutableStateFlow(false)
    val showErrorDialog: StateFlow<Boolean> = _showErrorDialog

    private val _errorDialogMessage = MutableStateFlow("")
    val errorDialogMessage: StateFlow<String> = _errorDialogMessage

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
                _isRegistered.value = false // Reset registrasi state
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
    fun register(name: String, email: String, password: String, confirmPassword: String, phone: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _error.value = "Semua field harus diisi"
            return
        }

        if (password != confirmPassword) {
            _error.value = "Password dan konfirmasi password tidak sama"
            _errorDialogMessage.value = "Password dan konfirmasi password tidak sama"
            _showErrorDialog.value = true
            return
        }

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
                _isRegistered.value = true // Set registrasi berhasil untuk navigasi ke form data diri
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal mendaftar"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Overload fungsi register untuk mendukung pemanggilan tanpa confirmPassword
     */
    fun register(name: String, email: String, password: String, phone: String) {
        register(name, email, password, password, phone)
    }

    /**
     * Menyimpan data diri user
     */
    fun savePersonalData(fullName: String, gender: String, position: String, phoneNumber: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Dapatkan ID user saat ini
                val userId = authRepository.getCurrentUser()?.uid
                    ?: throw Exception("User tidak ditemukan, silakan login ulang")

                // Update data user di Firestore
                firestoreRepository.updateUserData(
                    name = fullName,
                    gender = gender,
                    position = position,
                    phone = phoneNumber
                )

                // Update state
                _personalDataSaved.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal menyimpan data diri"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Reset status data diri tersimpan
     */
    fun resetPersonalDataStatus() {
        _personalDataSaved.value = false
    }

    /**
     * Menyimpan data usaha
     */
    fun saveBusiness(business: com.example.omsetku.models.Business) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                firestoreRepository.saveBusiness(business)
                _businessDataSaved.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal menyimpan data usaha"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Reset status data usaha tersimpan
     */
    fun resetBusinessDataStatus() {
        _businessDataSaved.value = false
    }

    /**
     * Reset status registrasi setelah navigasi ke form data diri
     */
    fun resetRegistrationStatus() {
        _isRegistered.value = false
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
                _isRegistered.value = false
                _personalDataSaved.value = false
                _businessDataSaved.value = false
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

    fun dismissErrorDialog() {
        _showErrorDialog.value = false
    }

    /**
     * Mengubah password user
     */
    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Mengubah password menggunakan AuthRepository
                authRepository.changePassword(currentPassword, newPassword)
                _error.value = "Password berhasil diubah"
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal mengubah password"
            } finally {
                _isLoading.value = false
            }
        }
    }
}