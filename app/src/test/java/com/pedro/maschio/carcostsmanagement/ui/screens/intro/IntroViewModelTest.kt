package com.pedro.maschio.carcostsmanagement.ui.screens.intro

import app.cash.turbine.test
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepository
import com.pedro.maschio.carcostsmanagement.rules.MainDispatcherRule
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class IntroViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: CarCostsRepository = mockk(relaxed = true)
    private lateinit var viewModel: IntroViewModel

    @Test
    fun `onCarNameChanged updates uiState`() {
        viewModel = IntroViewModel(repository)
        viewModel.onCarNameChanged("Civic")
        assertEquals("Civic", viewModel.uiState.value.carName)
    }

    @Test
    fun `onCarMileageChanged updates uiState`() {
        viewModel = IntroViewModel(repository)
        viewModel.onCarMileageChanged("50000")
        assertEquals("50000", viewModel.uiState.value.carMileage)
    }

    @Test
    fun `onSaveCar saves car and emits navigation event`() = runTest {
        viewModel = IntroViewModel(repository)
        viewModel.onCarNameChanged("Civic")
        viewModel.onCarMileageChanged("50000")

        viewModel.uiEvents.test {
            viewModel.onSaveCar()
            
            coVerify { 
                repository.insertCar(match { it.name == "Civic" && it.mileage == 50000 })
                repository.setIsIntroShown()
            }
            assertEquals(IntroUiEvents.GoToCarListing, awaitItem())
        }
    }
}
