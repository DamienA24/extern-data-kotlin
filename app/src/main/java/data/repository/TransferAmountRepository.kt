package data.repository

import android.util.Log
import data.api.ApiService
import data.model.transfer.TransferRequest
import data.model.transfer.TransferResponse
import data.repository.service.TransferAmountApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransferAmountRepository @Inject constructor(
    private val apiService: ApiService
) : TransferAmountApi {

    private companion object {
        private const val TAG = "TransferAmountRepository"
    }

    override suspend fun transferAmount(transferRequest: TransferRequest): Flow<TransferResponse> = flow {
        val response = apiService.transferAmount(transferRequest)
        Log.d(TAG, response.toString())
        if (response.isSuccessful) {
            val apiResponse = response.body()
            if (apiResponse != null) {
                if (apiResponse.result == true) {
                    emit(TransferResponse(true))
                } else {
                    emit(TransferResponse(false))
                }

            } else {
                emit(TransferResponse(false))
            }
        } else {
            //
            val errorBody = response.errorBody()?.string()
            val errorMessage = "Error ${response.code()}: ${response.message()}" +
                    if (!errorBody.isNullOrBlank()) " - $errorBody" else ""
            Log.e(TAG, errorMessage)
            emit(TransferResponse(false))
        }
    }.catch { e -> //
        Log.e(TAG, "Exception occurred in loginUser flow: ${e.message}", e)
        throw e
    }.flowOn(Dispatchers.IO)
}