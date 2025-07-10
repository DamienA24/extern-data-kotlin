package viewModel.transfer

import android.util.Log
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import com.aura.ui.login.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import data.model.login.LoginRequest
import data.model.login.LoginResponse
import data.repository.AuthRepository
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
class TransferViewModel @Inject constructor(): ViewModel() {

    private val _amount = MutableStateFlow<Double>(0.0)
    private val _recipient= MutableStateFlow("")
    val amount: StateFlow<Double> = _amount.asStateFlow()
    val recipient: StateFlow<String> = _recipient.asStateFlow()

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
}