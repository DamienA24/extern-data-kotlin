package data.repository.service

import data.model.LoginRequest
import data.model.LoginResponse
import kotlinx.coroutines.flow.Flow

interface AuthApi {
    suspend fun loginUser(loginRequest: LoginRequest): Flow<LoginResponse>
}