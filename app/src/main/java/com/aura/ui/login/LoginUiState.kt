package com.aura.ui.login

import data.model.LoginResponse

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val message: LoginResponse) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}