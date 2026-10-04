package com.example.bengkelapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bengkelapp.data.pref.SessionManager
import com.example.bengkelapp.data.repository.BengkelRepository
import com.example.bengkelapp.model.User
import com.example.bengkelapp.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BengkelRepository(application)
    private val sessionManager = SessionManager(application)

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(repository.getCurrentUser())
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDatabaseIfEmpty()
            _currentUser.value = repository.getCurrentUser()
        }
    }

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = AuthUiState.Error("Email dan Password tidak boleh kosong")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = repository.login(email, pass)
            result.onSuccess { user ->
                _currentUser.value = user
                _uiState.value = AuthUiState.Success(user)
            }.onFailure { error ->
                _uiState.value = AuthUiState.Error(error.message ?: "Login gagal")
            }
        }
    }

    fun register(name: String, email: String, phone: String, pass: String, passConfirm: String) {
        if (name.isBlank() || email.isBlank() || phone.isBlank() || pass.isBlank()) {
            _uiState.value = AuthUiState.Error("Semua bidang harus diisi")
            return
        }
        if (pass != passConfirm) {
            _uiState.value = AuthUiState.Error("Konfirmasi password tidak cocok")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = repository.register(name, email, phone, pass)
            result.onSuccess { user ->
                _currentUser.value = user
                _uiState.value = AuthUiState.Success(user)
            }.onFailure { error ->
                _uiState.value = AuthUiState.Error(error.message ?: "Registrasi gagal")
            }
        }
    }

    fun logout() {
        repository.logout()
        _currentUser.value = null
        _uiState.value = AuthUiState.Idle
    }

    fun resetUiState() {
        _uiState.value = AuthUiState.Idle
    }

    fun isOnboardingCompleted(): Boolean = sessionManager.isOnboardingCompleted()

    fun setOnboardingCompleted() {
        sessionManager.setOnboardingCompleted(true)
    }

    fun isMockMode(): Boolean = sessionManager.isMockMode()

    fun setMockMode(enabled: Boolean) {
        sessionManager.setMockMode(enabled)
    }

    fun setBaseUrl(url: String) {
        sessionManager.setBaseUrl(url)
    }

    fun getBaseUrl(): String = sessionManager.getBaseUrl()
}
