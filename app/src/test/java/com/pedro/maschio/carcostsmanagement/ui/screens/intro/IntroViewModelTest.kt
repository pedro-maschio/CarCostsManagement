package com.pedro.maschio.carcostsmanagement.ui.screens.intro

import app.cash.turbine.test
import com.pedro.maschio.carcostsmanagement.data.database.entities.Car
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepository
import com.pedro.maschio.carcostsmanagement.rules.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class IntroViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: CarCostsRepository = mockk(relaxed = true)
    private lateinit var viewModel: IntroViewModel

    @Test
    fun `onCarNameChanged updates uiState`() = runTest {
        viewModel = IntroViewModel(repository)
        val name = "Toyota Corolla"
        
        viewModel.onCarNameChanged(name)
        
        assertEquals(name, viewModel.uiState.value.carName)
    }

    @Test
    fun `onCarMileageChanged updates uiState`() = runTest {
        viewModel = IntroViewModel(repository)
        val mileage = "15000"
        
        viewModel.onCarMileageChanged(mileage)
        
        assertEquals(mileage, viewModel.uiState.value.carMileage)
    }

    @Test
    fun `onSaveCar saves car and emits GoToCarListing event`() = runTest {
        viewModel = IntroViewModel(repository)
        val name = "Civic"
        val mileage = "10000"
        
        viewModel.onCarNameChanged(name)
        viewModel.onCarMileageChanged(mileage)
        
        viewModel.uiEvents.test {
            viewModel.onSaveCar()
            
            coVerify { 
                repository.insertCar(match { 
                    it.name == name && it.mileage == 10000 && it.lastOilChangeMileage == 10000 
                })
                repository.setIsIntroShown()
            }
            
            assertEquals(IntroUiEvents.GoToCarListing, awaitItem())
        }
    }
}
