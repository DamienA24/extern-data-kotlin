package viewModel.home

import android.icu.text.NumberFormat
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.ui.home.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import data.model.account.AccountUserResponse
import data.repository.service.AccountUserApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountUserApi: AccountUserApi
) : ViewModel()  {

    private companion object {
        private const val TAG = "HomeViewModel"
    }
    private val _homeUiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val homeUiState: StateFlow<HomeUiState> = _homeUiState

    private var userId: String? = ""

    fun loadUserData(userId: String?) {
        if(userId == null) {
            _homeUiState.value = HomeUiState.Error("User ID is null")
            return
        }
        this.userId = userId
        fetchUserData(userId)
    }

    private fun fetchUserData(userId: String) {
        _homeUiState.value = HomeUiState.Loading
        Log.d(TAG, "Setting HomeUiState to Loading for fetching account with userId: $userId")
        viewModelScope.launch {
            try {
                accountUserApi.getAccountUser(userId)
                    .collect { userAccountList ->
                        Log.i(TAG, "Account fetched successfully: $userAccountList")
                        val accountToShow = userAccountList.firstOrNull { it.main } ?: userAccountList.first()
                        _homeUiState.value = HomeUiState.Success(accountToShow)
                    }
            } catch (e: IOException) {
                Log.e(TAG, "Network error while fetching account: ${e.message}", e)
                _homeUiState.value = HomeUiState.Error("Network error: ${e.message ?: "Unknown"}")
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error while fetching account: ${e.message}", e)
                _homeUiState.value = HomeUiState.Error("An unexpected error occurred: ${e.message ?: "Unknown"}")
            }
        }
    }

    fun formatBalanceToString(accountData: AccountUserResponse, locale: Locale = Locale.getDefault()): String {
        val currencySymbol = "€"
        val format = NumberFormat.getCurrencyInstance(locale)

        return try {
            val amount = accountData.balance.toDouble()
            "${format.format(amount)}"
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting balance number: ${accountData.balance}", e)
            "${accountData.balance}$currencySymbol (Error format)"
        }
    }
}