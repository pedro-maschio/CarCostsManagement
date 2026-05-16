package com.pedro.maschio.carcostsmanagement.ui.screens.login

import app.cash.turbine.test
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepository
import com.pedro.maschio.carcostsmanagement.rules.MainDispatcherRule
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: CarCostsRepository = mockk(relaxed = true)
    private lateinit var viewModel: LoginViewModel

    @Test
    fun `onUsernameChanged updates uiState`() {
        viewModel = LoginViewModel(repository)
        val username = "user123"
        
        viewModel.onUsernameChanged(username)
        
        assertEquals(username, viewModel.uiState.value.username)
    }

    @Test
    fun `onPasswordChanged updates uiState`() {
        viewModel = LoginViewModel(repository)
        val password = "password123"
        
        viewModel.onPasswordChanged(password)
        
        assertEquals(password, viewModel.uiState.value.password)
    }

    @Test
    fun `onLoginClicked simulates login and emits LoginSuccess`() = runTest {
        viewModel = LoginViewModel(repository)
        
        viewModel.uiEvents.test {
            viewModel.onLoginClicked()
            
            // Initial state should be loading (might be too fast to catch without Turbine on uiState)
            // But we can check repository call and event
            
            assertEquals(LoginUiEvent.LoginSuccess, awaitItem())
            coVerify { repository.setIsLoggedIn(true) }
            assertFalse(viewModel.uiState.value.isLoading)
        }
    }

    @Test
    fun `onGoogleLoginClicked simulates login and emits LoginSuccess`() = runTest {
        viewModel = LoginViewModel(repository)
        
        viewModel.uiEvents.test {
            viewModel.onGoogleLoginClicked()
            
            assertEquals(LoginUiEvent.LoginSuccess, awaitItem())
            coVerify { repository.setIsLoggedIn(true) }
            assertFalse(viewModel.uiState.value.isLoading)
        }
    }
}
