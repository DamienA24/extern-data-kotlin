package data.api

import data.model.account.AccountUserResponse
import data.model.login.LoginRequest
import data.model.login.LoginResponse
import data.model.transfer.TransferRequest
import data.model.transfer.TransferResponse
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

    @POST("transfer")
    suspend fun transferAmount(@Body request: TransferRequest): Response<TransferResponse>
}