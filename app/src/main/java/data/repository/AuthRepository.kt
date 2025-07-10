package data.repository

import android.util.Log
import data.model.LoginRequest
import data.api.ApiService
import data.model.LoginResponse
import data.repository.service.AuthApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApiService: ApiService
) : AuthApi {

    override suspend fun loginUser(loginRequest: LoginRequest): Flow<LoginResponse> = flow {
        val response = authApiService.login(loginRequest)
        Log.d("response", response.toString())
        if (response.isSuccessful) {
            val apiResponse = response.body()
            if (apiResponse != null) {
                if (apiResponse.granted == true) {
                    emit(LoginResponse(true))
                } else {
                    emit(LoginResponse(false))
                }

            } else {
                emit(LoginResponse(false))
            }
        } else {
            //
            val errorBody = response.errorBody()?.string()
            val errorMessage = "Error ${response.code()}: ${response.message()}" +
                    if (!errorBody.isNullOrBlank()) " - $errorBody" else ""
            emit(LoginResponse(false))
        }
    }.catch { e -> //
        Log.e("AuthRepository", "Exception occurred in loginUser flow: ${e.message}", e)
        throw e
    }.flowOn(Dispatchers.IO)
}

