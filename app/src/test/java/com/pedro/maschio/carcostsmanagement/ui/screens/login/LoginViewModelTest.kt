package com.pedro.maschio.carcostsmanagement.ui.screens.login

import app.cash.turbine.test
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepository
import com.pedro.maschio.carcostsmanagement.rules.MainDispatcherRule
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
        viewModel.onUsernameChanged("user123")
        assertEquals("user123", viewModel.uiState.value.username)
    }

    @Test
    fun `onPasswordChanged updates uiState`() {
        viewModel = LoginViewModel(repository)
        viewModel.onPasswordChanged("password123")
        assertEquals("password123", viewModel.uiState.value.password)
    }

    @Test
    fun `onLoginClicked simulates login and emits success`() = runTest {
        viewModel = LoginViewModel(repository)
        
        viewModel.uiEvents.test {
            viewModel.onLoginClicked()
            
            // Wait for loading to start (not strictly necessary for events test but good practice)
            assertTrue(viewModel.uiState.value.isLoading)
            
            assertEquals(LoginUiEvent.LoginSuccess, awaitItem())
            coVerify { repository.setIsLoggedIn(true) }
            assertEquals(false, viewModel.uiState.value.isLoading)
        }
    }

    @Test
    fun `onGoogleLoginClicked simulates login and emits success`() = runTest {
        viewModel = LoginViewModel(repository)
        
        viewModel.uiEvents.test {
            viewModel.onGoogleLoginClicked()
            
            assertEquals(LoginUiEvent.LoginSuccess, awaitItem())
            coVerify { repository.setIsLoggedIn(true) }
            assertEquals(false, viewModel.uiState.value.isLoading)
        }
    }
}
