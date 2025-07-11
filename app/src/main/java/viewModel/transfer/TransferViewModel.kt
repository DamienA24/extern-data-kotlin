package viewModel.transfer

import android.util.Log
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import com.aura.ui.transfer.TransferUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import data.model.transfer.TransferRequest
import data.model.transfer.TransferResponse
import data.repository.TransferAmountRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import java.io.IOException

/**
 * The view model for the login activity.
 */
@HiltViewModel
class TransferViewModel @Inject constructor(
    private val transferAmountRepository: TransferAmountRepository
): ViewModel() {

    private companion object {
        private const val TAG = "TransferViewModel"
    }

    private val _amount = MutableStateFlow<Double>(0.0)
    private val _recipient= MutableStateFlow("")

    private val _transferAmountUiState = MutableStateFlow<TransferUiState>(TransferUiState.Idle)
    val transferAmountUiState: StateFlow<TransferUiState> = _transferAmountUiState.asStateFlow()

    /**
     * The amount of the user.
     * @param newAmount The amount of the user.
     */
    fun setAmount(newAmount: String)  {
        _amount.value = newAmount.replace(',', '.').toDouble()
    }

    /**
     * The recipient of the user.
     * @param newRecipient The new recipient of the user.
     */
    fun setRecipient(newRecipient: String) {
        _recipient.value = newRecipient
    }

    /**
     * The transfer of the user form is valid.
     */
    val isTransferFormValid : StateFlow<Boolean> =
        combine(_amount, _recipient) { amount, recipient ->
            amount.toString().isNotBlank() && recipient.isNotBlank()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun transferAmount(sender: String) {
        if (!isTransferFormValid.value) {
            _transferAmountUiState.value = TransferUiState.Error("Please fill in the amount and recipient.")
            return
        }

        _transferAmountUiState.value = TransferUiState.Loading

        viewModelScope.launch {
            val request = TransferRequest(sender = sender, recipient = _recipient.value, amount = _amount.value)
            try {
                transferAmountRepository.transferAmount(request)
                    .collect { state ->
                        Log.i(TAG, "Transfer state: $state")
                        if (state == TransferResponse(true)) {
                            _transferAmountUiState.value = TransferUiState.Success(TransferResponse(true))
                            Log.i(TAG, "Transfer true: $state")
                        } else {
                            _transferAmountUiState.value = TransferUiState.Error("Incorrect recipient, amount or sender.")
                            Log.i(TAG, "Transfer false: $state")
                        }
                    }
            } catch (e: IOException) {
                Log.e("$TAG IOException", "Network error: ${e.message}")
                _transferAmountUiState.value = TransferUiState.Error("Connection error. Check your internet connection.")
            }
            catch (e: Exception) {
                Log.e("$TAG Exception", "Unexpected error: ${e.message}")
                _transferAmountUiState.value = TransferUiState.Error("An unexpected error has occurred: ${e.message ?: "unknown"}")            }
        }
    }
}