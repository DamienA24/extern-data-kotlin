package com.aura.ui.home

import data.model.account.AccountUserResponse

sealed class HomeUiState {
    object Idle : HomeUiState()
    object Loading : HomeUiState()
    data class Success(val userAccountData: AccountUserResponse) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}