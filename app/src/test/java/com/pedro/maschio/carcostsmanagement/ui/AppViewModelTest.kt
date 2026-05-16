package com.pedro.maschio.carcostsmanagement.ui

import app.cash.turbine.test
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepository
import com.pedro.maschio.carcostsmanagement.rules.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: CarCostsRepository = mockk(relaxed = true)
    private lateinit var viewModel: AppViewModel

    @Test
    fun `introShown reflects repository value`() = runTest {
        val introShownFlow = MutableStateFlow(true)
        every { repository.introShown } returns introShownFlow
        
        viewModel = AppViewModel(repository)
        
        viewModel.introShown.test {
            assertEquals(true, awaitItem())
        }
    }

    @Test
    fun `isLoggedIn reflects repository value`() = runTest {
        val isLoggedInFlow = MutableStateFlow(false)
        every { repository.isLoggedIn } returns isLoggedInFlow
        
        viewModel = AppViewModel(repository)
        
        viewModel.isLoggedIn.test {
            assertEquals(false, awaitItem())
        }
    }
}
