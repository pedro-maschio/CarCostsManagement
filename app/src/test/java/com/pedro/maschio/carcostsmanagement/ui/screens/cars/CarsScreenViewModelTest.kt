package com.pedro.maschio.carcostsmanagement.ui.screens.cars

import com.pedro.maschio.carcostsmanagement.data.database.entities.Car
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepository
import com.pedro.maschio.carcostsmanagement.rules.MainDispatcherRule
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CarsScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: CarCostsRepository = mockk(relaxed = true)
    private lateinit var viewModel: CarsScreenViewModel

    private val testCar = Car(id = 1, name = "Fusca", mileage = 100000, lastOilChangeMileage = 95000)
    private val carsFlow = MutableStateFlow<List<Car>>(emptyList())

    @Test
    fun `uiState updates with cars from repository flow`() = runTest {
        val cars = listOf(testCar)
        every { repository.getCars() } returns carsFlow
        viewModel = CarsScreenViewModel(repository)
        
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        
        carsFlow.value = cars
        assertEquals(cars, viewModel.uiState.value.cars)
        collectJob.cancel()
    }

    @Test
    fun `toggleDeleteDialog updates uiState`() = runTest {
        every { repository.getCars() } returns carsFlow
        viewModel = CarsScreenViewModel(repository)
        
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        
        // Toggle on
        viewModel.toggleDeleteDialog(testCar)
        assertTrue(viewModel.uiState.value.isDeleteDialogShowing)
        assertEquals(testCar, viewModel.uiState.value.selectedToDeleteCar)
        
        // Toggle off
        viewModel.toggleDeleteDialog(null)
        assertFalse(viewModel.uiState.value.isDeleteDialogShowing)
        assertNull(viewModel.uiState.value.selectedToDeleteCar)
        collectJob.cancel()
    }

    @Test
    fun `deleteCar calls repository`() = runTest {
        every { repository.getCars() } returns carsFlow
        viewModel = CarsScreenViewModel(repository)
        
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        
        viewModel.toggleDeleteDialog(testCar)
        viewModel.deleteCar()
        
        coVerify { repository.deleteCar(testCar) }
        assertFalse(viewModel.uiState.value.isDeleteDialogShowing)
        assertNull(viewModel.uiState.value.selectedToDeleteCar)
        collectJob.cancel()
    }

    @Test
    fun `updateCar calls repository`() = runTest {
        every { repository.getCars() } returns carsFlow
        viewModel = CarsScreenViewModel(repository)
        
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        
        val updatedCar = testCar.copy(name = "Fusca Turbo")
        viewModel.updateCar(updatedCar)
        
        coVerify { repository.updateCar(updatedCar) }
        collectJob.cancel()
    }
}
