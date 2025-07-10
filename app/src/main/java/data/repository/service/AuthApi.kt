package data.repository.service

import data.model.login.LoginRequest
import data.model.login.LoginResponse
import kotlinx.coroutines.flow.Flow

interface AuthApi {
    suspend fun loginUser(loginRequest: LoginRequest): Flow<LoginResponse>
}