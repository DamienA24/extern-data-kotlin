package viewModel.viewModel

import android.util.Log
import androidx.activity.result.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _loginState = MutableStateFlow<LoginResponse>(LoginResponse(false))
    val loginState: StateFlow<LoginResponse> = _loginState.asStateFlow()

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
            _loginState.value = LoginResponse(false)
            return
        }

        viewModelScope.launch {
            val request = LoginRequest(id = _id.value, password = _password.value)

            authRepository.loginUser(request)
                // Optionnel : Si vous voulez intercepter une erreur qui n'aurait pas été transformée en SimpleLoginState.Error par le repo
                // .catch { exception ->
                // _loginState.value = SimpleLoginState.Error("Erreur VM: ${exception.message}")
                // Log.e("LoginViewModel", "Exception from repo flow", exception)
                // }
                .collect { state ->
                    Log.d("LoginViewModel", "Current _loginState.value BEFORE update: ${_loginState.value}")
                    Log.d("LoginViewModel", "Value from repository to be set: $state")
                    _loginState.value = state
                    Log.d("LoginViewModel", "Current _loginState.value AFTER update: ${_loginState.value}")
                    if (state == LoginResponse(true)) {
                        Log.i("LoginViewModel", "Login success")

                    } else if (state == LoginResponse(false)) {
                        Log.i("LoginViewModel", "Login false")
                    }
                }
        }
    }
}