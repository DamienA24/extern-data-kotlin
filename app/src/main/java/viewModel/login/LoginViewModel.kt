package viewModel.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * The view model for the login activity.
 */
class LoginViewModel: ViewModel() {

    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")

    /**
     * The email of the user.
     * @param newEmail The new email of the user.
     */
    fun setEmail(newEmail: String) {
        _email.value = newEmail
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
        combine(_email, _password) { email, password ->
            email.isNotBlank() && password.isNotBlank()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
}