package com.example.omsetku.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omsetku.domain.model.User
import com.example.omsetku.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    private val _authState = MutableStateFlow<Result<User>?>(null)
    val authState: StateFlow<Result<User>?> = _authState.asStateFlow()

    val currentUser = MutableStateFlow<User?>(null)

    init {
        observeCurrentUser()
    }

    private fun observeCurrentUser() {
        getCurrentUserUseCase().onEach { currentUser.value = it }.launchIn(viewModelScope)
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = loginUseCase(email, password)
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = registerUseCase(name, email, password)
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _authState.value = null
        }
    }
} 