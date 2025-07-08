package data.repository

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
        // 1. Émettre l'état de chargement
        //emit(SimpleLoginState.Loading)


        val response = authApiService.login(loginRequest)

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
            val errorMessage = "Erreur ${response.code()}: ${response.message()}" +
                    if (!errorBody.isNullOrBlank()) " - $errorBody" else ""
            emit(LoginResponse(false))
        }
    }.catch { e -> //
        /*if (e is IOException) {
            emit(SimpleLoginState.Error("Erreur réseau: ${e.message}"))
        } else {
            emit(SimpleLoginState.Error("Une erreur inattendue est survenue: ${e.message}"))
        }*/
    }.flowOn(Dispatchers.IO)
}

