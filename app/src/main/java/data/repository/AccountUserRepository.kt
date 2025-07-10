package data.repository

import android.util.Log
import data.api.ApiService
import data.model.account.AccountUserResponse
import data.repository.service.AccountUserApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountUserRepository @Inject constructor(
    private val accountUserApiService: ApiService
) : AccountUserApi {

    private companion object {
        private const val TAG = "AccountUserRepository"
    }

    override suspend fun getAccountUser(userId: String): Flow<List<AccountUserResponse>> = flow {
        Log.d(TAG, "Fetching account for userId: $userId")
        val response = accountUserApiService.getAccountUser(userId)
        Log.d(TAG, "Account API Response: $response")

        if (response.isSuccessful) {
            val balanceResponse = response.body()
            if (balanceResponse != null) {
                emit(balanceResponse)
            } else {
                Log.w(TAG, "GetAccount API response body is null")
                throw IOException("Account response body is null but request was successful.")
            }
        } else {
            val errorBody = response.errorBody()?.string() ?: "No error body"
            Log.e(TAG, "GetAccount API error: ${response.code()} - $errorBody")
            throw IOException("Failed to fetch account with code ${response.code()}: ${response.message()}")
        }
    }
        .catch { e ->
            Log.e(TAG, "Exception in getAccountUser flow: ${e.message}", e)
            throw e
        }
        .flowOn(Dispatchers.IO)
}

