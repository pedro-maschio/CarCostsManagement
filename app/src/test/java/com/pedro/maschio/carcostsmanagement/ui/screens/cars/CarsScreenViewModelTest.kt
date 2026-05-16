package com.pedro.maschio.carcostsmanagement.ui.screens.cars

import com.pedro.maschio.carcostsmanagement.data.database.entities.Car
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepository
import com.pedro.maschio.carcostsmanagement.rules.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    @Test
    fun `getCars updates uiState with cars from repository`() = runTest {
        val cars = listOf(testCar)
        coEvery { repository.getCars() } returns cars
        viewModel = CarsScreenViewModel(repository)
        
        viewModel.getCars()
        
        assertEquals(cars, viewModel.uiState.value.cars)
    }

    @Test
    fun `toggleDeleteDialog updates uiState`() = runTest {
        viewModel = CarsScreenViewModel(repository)
        
        // Toggle on
        viewModel.toggleDeleteDialog(testCar)
        assertTrue(viewModel.uiState.value.isDeleteDialogShowing)
        assertEquals(testCar, viewModel.uiState.value.selectedToDeleteCar)
        
        // Toggle off
        viewModel.toggleDeleteDialog(null)
        assertFalse(viewModel.uiState.value.isDeleteDialogShowing)
        assertNull(viewModel.uiState.value.selectedToDeleteCar)
    }

    @Test
    fun `deleteCar calls repository and refreshes list`() = runTest {
        coEvery { repository.getCars() } returns emptyList()
        viewModel = CarsScreenViewModel(repository)
        
        // Set car to delete in state manually since we are not calling getCars() initially
        viewModel.toggleDeleteDialog(testCar)
        
        viewModel.deleteCar()
        
        coVerify { repository.deleteCar(testCar) }
        assertTrue(viewModel.uiState.value.cars.isEmpty())
        assertFalse(viewModel.uiState.value.isDeleteDialogShowing)
        assertNull(viewModel.uiState.value.selectedToDeleteCar)
    }

    @Test
    fun `updateCar calls repository and refreshes list`() = runTest {
        val updatedCar = testCar.copy(name = "Fusca Turbo")
        coEvery { repository.getCars() } returns listOf(updatedCar)
        viewModel = CarsScreenViewModel(repository)
        
        viewModel.updateCar(updatedCar)
        
        coVerify { repository.updateCar(updatedCar) }
        assertEquals(updatedCar, viewModel.uiState.value.cars.first())
    }
}
