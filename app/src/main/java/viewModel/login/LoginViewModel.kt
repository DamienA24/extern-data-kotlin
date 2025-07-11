package viewModel.login

import android.util.Log
import androidx.activity.result.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.ui.login.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import data.api.ApiService
import data.model.login.LoginRequest
import data.model.login.LoginResponse
import data.repository.AuthRepository
import data.repository.service.AuthApi
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
class LoginViewModel @Inject constructor(
    private val authApi: AuthApi
): ViewModel() {

    private val _id = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    val userId: StateFlow<String> = _id.asStateFlow()

    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    /**
     * The email of the user.
     * @param newId The new email of the user.
     */
    fun setId(newId: String) {
        _id.value = newId
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
            _loginUiState.value = LoginUiState.Error("Please fill in the username and password.")
            return
        }

        _loginUiState.value = LoginUiState.Loading

        viewModelScope.launch {
            val request = LoginRequest(id = _id.value, password = _password.value)
            try {
                authApi.loginUser(request)
                    .collect { state ->
                        Log.i("LoginViewModel", "Login state: $state")
                        if (state == LoginResponse(true)) {
                            _loginUiState.value = LoginUiState.Success(LoginResponse(true))
                            Log.i("LoginViewModel", "Login true: $state")
                        } else {
                            _loginUiState.value = LoginUiState.Error("Incorrect login or password.")
                            Log.i("LoginViewModel", "Login false: $state")
                        }
                    }
            } catch (e: IOException) {
                Log.e("LoginViewModel IOException", "Network error: ${e.message}")
                _loginUiState.value = LoginUiState.Error("Connection error. Check your internet connection.")
            }
            catch (e: Exception) {
                Log.e("LoginViewModel Exception", "Unexpected error: ${e.message}")
                _loginUiState.value = LoginUiState.Error("An unexpected error has occurred: ${e.message ?: "unknown"}")            }

        }
    }
}