package di

import data.repository.service.AuthApi
import data.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import data.repository.AccountUserRepository
import data.repository.TransferAmountRepository
import data.repository.service.AccountUserApi
import data.repository.service.TransferAmountApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepository: AuthRepository
    ): AuthApi

    @Binds
    @Singleton
    abstract fun bindTransferRepository(
        transferAmountRepository: TransferAmountRepository
    ): TransferAmountApi

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        accountUserRepository: AccountUserRepository
    ): AccountUserApi
}