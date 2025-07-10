package data.repository.service

import data.model.account.AccountUserResponse
import kotlinx.coroutines.flow.Flow

interface AccountUserApi {
    suspend fun getAccountUser(userId: String): Flow<List<AccountUserResponse>>
}