package data.api

import data.model.account.AccountUserResponse
import data.model.login.LoginRequest
import data.model.login.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("accounts/{userId}")
    suspend fun getAccountUser(@Path("userId") userId: String): Response<List<AccountUserResponse>>
}