package data.api

import data.model.LoginRequest
import data.model.LoginResponse
import retrofit2.http.Body


interface AuthApiService {

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}