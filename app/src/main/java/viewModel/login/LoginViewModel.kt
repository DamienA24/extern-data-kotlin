package viewModel.login

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

    private val email = MutableStateFlow("")
    private val password = MutableStateFlow("")

    /**
     * The email of the user.
     * @param newEmail The new email of the user.
     */
    fun setEmail(newEmail: String) {
        email.value = newEmail
    }

    /**
     * The password of the user.
     * @param newPassword The new password of the user.
     */
    fun setPassword(newPassword: String) {
        password.value = newPassword
    }

    /**
     * The login form is valid.
     */
    val isLoginFormValid : StateFlow<Boolean> =
        combine(email, password) { email, password ->
        email.isNotBlank() && password.isNotBlank()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
}