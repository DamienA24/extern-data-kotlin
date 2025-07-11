package com.aura.login

import app.cash.turbine.test
import com.aura.ui.login.LoginUiState
import com.google.common.truth.Truth.assertThat
import data.model.login.LoginRequest
import data.model.login.LoginResponse
import data.repository.service.AuthApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import viewModel.login.LoginViewModel
import java.io.IOException

@ExperimentalCoroutinesApi
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : org.junit.rules.TestWatcher() {
    override fun starting(description: org.junit.runner.Description) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: org.junit.runner.Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}


@ExperimentalCoroutinesApi
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var mockAuthApi: AuthApi

    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        mockAuthApi = mock()

        loginViewModel = LoginViewModel(mockAuthApi)
    }

    @Test
    fun `setId WHEN newId is provided THEN _id StateFlow is updated`() = runTest {
        val testId = "testUser"
        loginViewModel.userId.test {
            assertThat(awaitItem()).isEqualTo("")
            loginViewModel.setId(testId)
            assertThat(awaitItem()).isEqualTo(testId)
        }
    }

    @Test
    fun `isLoginFormValid WHEN id and password are not blank THEN returns true`() = runTest {
        loginViewModel.isLoginFormValid.test {
            // Initial state should be false
            assertThat(awaitItem()).isFalse()

            // Set id
            loginViewModel.setId("testUser")
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            // Set password
            loginViewModel.setPassword("password123")
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            // Wait for the final emission
            assertThat(awaitItem()).isTrue()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `isLoginFormValid WHEN id and password are blank THEN returns false`() = runTest {
        loginViewModel.isLoginFormValid.test {
            assertThat(awaitItem()).isFalse() // Initial state

            loginViewModel.setId("")
            loginViewModel.setPassword("")

            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `isLoginFormValid WHEN id is not blank and password is blank THEN returns false`() = runTest {
        loginViewModel.isLoginFormValid.test {
            assertThat(awaitItem()).isFalse() // Initial state

            loginViewModel.setId("1234")
            loginViewModel.setPassword("")

            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `isLoginFormValid WHEN id is blank and password is not blank THEN returns false`() = runTest {
        loginViewModel.isLoginFormValid.test {
            assertThat(awaitItem()).isFalse() // Initial state

            loginViewModel.setId("")
            loginViewModel.setPassword("1234")

            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `loginUser WHEN form is invalid THEN UiState is Error and api not called`() = runTest {

        loginViewModel.loginUiState.test {
            // Act
            loginViewModel.loginUser()

            // Assert
            assertThat(awaitItem()).isInstanceOf(LoginUiState.Idle::class.java)
            val errorState = awaitItem()
            assertThat(errorState).isInstanceOf(LoginUiState.Error::class.java)
            assertThat((errorState as LoginUiState.Error).message).isEqualTo("Please fill in the username and password.")
            cancelAndIgnoreRemainingEvents()
        }
        verify(mockAuthApi, never()).loginUser(any())
    }


    @Test
    fun `loginUser WHEN form is valid AND api returns success THEN UiState is Success`() = runTest {
        // Arrange
        val testId = "testUser"
        val testPassword = "password123"
        val expectedRequest = LoginRequest(id = testId, password = testPassword)
        val successResponse = LoginResponse(true)

        whenever(mockAuthApi.loginUser(expectedRequest)).thenReturn(flowOf(successResponse))

        loginViewModel.setId(testId)
        loginViewModel.setPassword(testPassword)
        // Avancer après avoir défini les valeurs
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        loginViewModel.loginUiState.test {
            // Ignorer l'état initial
            val initialState = awaitItem()
            println("État initial: $initialState")

            // Act
            loginViewModel.loginUser()

            // Assert Loading state first
            val loadingState = awaitItem()
            assertThat(loadingState).isInstanceOf(LoginUiState.Loading::class.java)
            println("TEST_LOG: Received state after loading: $loadingState")

            // IMPORTANT: Avancer le scheduler après l'appel
            mainDispatcherRule.testDispatcher.scheduler.runCurrent()
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            // Assert Success
            val success = awaitItem() // Devrait être Success
            println("TEST_LOG: Received state after advanceUntilIdle: $success")
            assertThat(success).isInstanceOf(LoginUiState.Success::class.java)
            assertThat((success as LoginUiState.Success).message.granted).isEqualTo(successResponse.granted)

            cancelAndIgnoreRemainingEvents()
        }

        verify(mockAuthApi).loginUser(expectedRequest)
    }

    @Test
    fun `loginUser WHEN form is valid AND api returns failure (false) THEN UiState is Error`() = runTest {
        // Arrange
        val testId = "testUser"
        val testPassword = "password123"
        val expectedRequest = LoginRequest(id = testId, password = testPassword)
        val failureResponse = LoginResponse(false)

        whenever(mockAuthApi.loginUser(expectedRequest)).thenReturn(flowOf(failureResponse))

        loginViewModel.setId(testId)
        loginViewModel.setPassword(testPassword)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        loginViewModel.loginUiState.test {
            // Act
            loginViewModel.loginUser()

            // Assert
            assertThat(awaitItem()).isInstanceOf(LoginUiState.Idle::class.java)
            assertThat(awaitItem()).isInstanceOf(LoginUiState.Loading::class.java)
            val errorState = awaitItem()
            assertThat(errorState).isInstanceOf(LoginUiState.Error::class.java)
            assertThat((errorState as LoginUiState.Error).message).isEqualTo("Incorrect login or password.")
            cancelAndIgnoreRemainingEvents()
        }
        verify(mockAuthApi).loginUser(expectedRequest)
    }

    @Test
    fun `loginUser WHEN form is valid AND api throws IOException THEN UiState is Error`() = runTest {
        // Arrange
        val testId = "testUser"
        val testPassword = "password123"
        val expectedRequest = LoginRequest(id = testId, password = testPassword)
        val networkErrorMessage = "Network connection failed"

        whenever(mockAuthApi.loginUser(expectedRequest)).thenReturn(flow { throw IOException(networkErrorMessage) })

        loginViewModel.setId(testId)
        loginViewModel.setPassword(testPassword)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        loginViewModel.loginUiState.test {
            // Act
            loginViewModel.loginUser()

            // Assert
            assertThat(awaitItem()).isInstanceOf(LoginUiState.Idle::class.java) // Initial
            assertThat(awaitItem()).isInstanceOf(LoginUiState.Loading::class.java)
            val errorState = awaitItem()
            assertThat(errorState).isInstanceOf(LoginUiState.Error::class.java)
            assertThat((errorState as LoginUiState.Error).message).isEqualTo("Connection error. Check your internet connection.")
            cancelAndIgnoreRemainingEvents()
        }
        verify(mockAuthApi).loginUser(expectedRequest)
    }

    @Test
    fun `loginUser WHEN form is valid AND api throws generic Exception THEN UiState is Error`() = runTest {
        // Arrange
        val testId = "testUser"
        val testPassword = "password123"
        val expectedRequest = LoginRequest(id = testId, password = testPassword)
        val genericErrorMessage = "Something went wrong"

        whenever(mockAuthApi.loginUser(expectedRequest)).thenReturn(flow { throw Exception(genericErrorMessage) })

        loginViewModel.setId(testId)
        loginViewModel.setPassword(testPassword)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        loginViewModel.loginUiState.test {
            // Act
            loginViewModel.loginUser()

            // Assert
            assertThat(awaitItem()).isInstanceOf(LoginUiState.Idle::class.java)
            assertThat(awaitItem()).isInstanceOf(LoginUiState.Loading::class.java)
            val errorState = awaitItem()
            assertThat(errorState).isInstanceOf(LoginUiState.Error::class.java)
            assertThat((errorState as LoginUiState.Error).message).isEqualTo("An unexpected error has occurred: $genericErrorMessage")
            cancelAndIgnoreRemainingEvents()
        }
        verify(mockAuthApi).loginUser(expectedRequest)
    }
}
