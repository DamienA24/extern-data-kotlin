package data.repository.service

import data.model.transfer.TransferRequest
import data.model.transfer.TransferResponse
import kotlinx.coroutines.flow.Flow

interface TransferAmountApi {
    suspend fun transferAmount(transferRequest: TransferRequest): Flow<TransferResponse>
}
