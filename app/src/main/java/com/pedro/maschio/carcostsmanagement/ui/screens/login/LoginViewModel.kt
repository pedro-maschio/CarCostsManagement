package com.pedro.maschio.carcostsmanagement.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: CarCostsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<LoginUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    fun onUsernameChanged(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onLoginClicked() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Simulate login
            kotlinx.coroutines.delay(1000)
            repository.setIsLoggedIn(true)
            _uiState.update { it.copy(isLoading = false) }
            _uiEvents.emit(LoginUiEvent.LoginSuccess)
        }
    }

    fun onGoogleLoginClicked() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Simulate google login
            kotlinx.coroutines.delay(1000)
            repository.setIsLoggedIn(true)
            _uiState.update { it.copy(isLoading = false) }
            _uiEvents.emit(LoginUiEvent.LoginSuccess)
        }
    }
}

sealed interface LoginUiEvent {
    data object LoginSuccess : LoginUiEvent
}
