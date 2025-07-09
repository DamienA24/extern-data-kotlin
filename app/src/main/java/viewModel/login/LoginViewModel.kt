package viewModel.viewModel

import android.util.Log
import androidx.activity.result.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.ui.login.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import data.api.ApiService
import data.model.LoginRequest
import data.model.LoginResponse
import data.repository.AuthRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * The view model for the login activity.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _id = MutableStateFlow("")
    private val _password = MutableStateFlow("")

    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    /**
     * The email of the user.
     * @param newEmail The new email of the user.
     */
    fun setEmail(newEmail: String) {
        _id.value = newEmail
    }

    /**
     * The password of the user.
     * @param newPassword The new password of the user.
     */
    fun setPassword(newPassword: String) {
        _password.value = newPassword
    }

    /**
     * The login form is valid.
     */
    val isLoginFormValid : StateFlow<Boolean> =
        combine(_id, _password) { id, password ->
            id.isNotBlank() && password.isNotBlank()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun loginUser() {
        if (!isLoginFormValid.value) {
            _loginUiState.value = LoginUiState.Error(LoginResponse(false))
            return
        }

        _loginUiState.value = LoginUiState.Loading

        viewModelScope.launch {
            val request = LoginRequest(id = _id.value, password = _password.value)

            try {
                authRepository.loginUser(request)
                    .collect { state ->
                        if (state == LoginResponse(true)) {
                            _loginUiState.value = LoginUiState.Success(LoginResponse(true))
                            Log.i("LoginViewModel", "Login true: $state")
                        } else {
                            _loginUiState.value = LoginUiState.Error(LoginResponse(false))
                            Log.i("LoginViewModel", "Login false: $state")
                        }
                    }
            } catch (e: Exception) {
                _loginUiState.value = LoginUiState.Error(LoginResponse(false))
            }

        }
    }
}