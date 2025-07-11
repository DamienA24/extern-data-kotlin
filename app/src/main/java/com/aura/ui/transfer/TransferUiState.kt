package com.aura.ui.transfer

import data.model.transfer.TransferResponse

sealed class TransferUiState {
    object Idle : TransferUiState()
    object Loading : TransferUiState()
    data class Success(val message: TransferResponse) : TransferUiState()
    data class Error(val message: String) : TransferUiState()
}