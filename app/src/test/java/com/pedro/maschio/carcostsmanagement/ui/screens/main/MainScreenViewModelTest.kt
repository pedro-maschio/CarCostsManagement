package com.pedro.maschio.carcostsmanagement.ui.screens.main

import androidx.paging.PagingData
import app.cash.turbine.test
import com.pedro.maschio.carcostsmanagement.data.database.entities.Car
import com.pedro.maschio.carcostsmanagement.data.database.entities.CarCost
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepository
import com.pedro.maschio.carcostsmanagement.rules.MainDispatcherRule
import com.pedro.maschio.carcostsmanagement.worker.RecurrenceManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: CarCostsRepository = mockk(relaxed = true)
    private val recurrenceManager: RecurrenceManager = mockk(relaxed = true)
    private lateinit var viewModel: MainScreenViewModel

    private val selectedCarIdFlow = MutableStateFlow<Long?>(null)
    private val ethanolPriceFlow = MutableStateFlow(0.0)
    private val gasolinePriceFlow = MutableStateFlow(0.0)

    private val testCar = Car(id = 1, name = "Fusca", mileage = 100000, lastOilChangeMileage = 95000)

    @Before
    fun setup() {
        every { repository.selectedCar } returns selectedCarIdFlow
        every { repository.ethanolPrice } returns ethanolPriceFlow
        every { repository.gasolinePrice } returns gasolinePriceFlow
        every { repository.getCosts(any()) } returns flowOf(PagingData.empty())
        
        // Setup defaults for init blocks
        coEvery { repository.getCars() } returns emptyList()
        coEvery { repository.getTotalCosts(any()) } returns 0.0
        coEvery { repository.getCar(any()) } returns null
    }

    @Test
    fun `uiState updates when car is selected via flow`() = runTest {
        val cars = listOf(testCar)
        coEvery { repository.getCars() } returns cars
        coEvery { repository.getTotalCosts(1) } returns 100.0
        coEvery { repository.getCar(1) } returns testCar
        
        viewModel = MainScreenViewModel(repository, recurrenceManager)
        
        selectedCarIdFlow.value = 1
        
        // Turbine on uiState to wait for emission
        viewModel.uiState.test {
            var item = awaitItem()
            while (item.totalCosts != 100.0) {
                item = awaitItem()
            }
            assertEquals(cars, item.cars)
            assertEquals(100.0, item.totalCosts, 0.0)
        }
    }

    @Test
    fun `updateMileage calls repository and updates state`() = runTest {
        selectedCarIdFlow.value = 1
        coEvery { repository.getCar(1) } returns testCar
        coEvery { repository.getCars() } returns listOf(testCar.copy(mileage = 105000))

        viewModel = MainScreenViewModel(repository, recurrenceManager)

        viewModel.updateMileage(105000)
        
        coVerify { repository.updateCar(match { it.id == 1L && it.mileage == 105000 }) }
        assertEquals(105000, viewModel.uiState.value.currentMileage)
    }

    @Test
    fun `checkMaintenance shows alert when oil change is near`() = runTest {
        val nearMaintenanceCar = testCar.copy(mileage = 104600)
        
        selectedCarIdFlow.value = 1
        coEvery { repository.getCars() } returns listOf(nearMaintenanceCar)
        coEvery { repository.getCar(1) } returns nearMaintenanceCar
        
        viewModel = MainScreenViewModel(repository, recurrenceManager)
        
        viewModel.uiState.test {
            var item = awaitItem()
            while(item.maintenanceAlert == null) {
                item = awaitItem()
            }
            assertEquals("Troca de óleo em 400 km", item.maintenanceAlert)
        }
    }

    @Test
    fun `setFuelPrices calls repository`() = runTest {
        viewModel = MainScreenViewModel(repository, recurrenceManager)
        viewModel.setFuelPrices(3.5, 5.5)
        coVerify { repository.setFuelPrices(3.5, 5.5) }
    }

    @Test
    fun `toggleFuelPriceDialog updates uiState`() {
        viewModel = MainScreenViewModel(repository, recurrenceManager)
        assertFalse(viewModel.uiState.value.isFuelPriceDialogShown)
        viewModel.toggleFuelPriceDialog()
        assertTrue(viewModel.uiState.value.isFuelPriceDialogShown)
    }

    @Test
    fun `costs flow emits data when car is selected`() = runTest {
        val carId = 1L
        val pagingData = PagingData.from(listOf(
            CarCost(id = 1, type = 0, price = 50.0, date = 1000L, description = "Gas", carId = carId)
        ))
        every { repository.getCosts(carId) } returns flowOf(pagingData)
        
        viewModel = MainScreenViewModel(repository, recurrenceManager)
        selectedCarIdFlow.value = carId
        
        viewModel.costs.test {
            awaitItem() // PagingData emission
            io.mockk.verify { repository.getCosts(carId) }
        }
    }

    @Test
    fun `showAddEntry updates uiState`() {
        viewModel = MainScreenViewModel(repository, recurrenceManager)
        assertFalse(viewModel.uiState.value.isAddEntryShown)
        viewModel.showAddEntry()
        assertTrue(viewModel.uiState.value.isAddEntryShown)
    }

    private fun assertFalse(condition: Boolean) = org.junit.Assert.assertFalse(condition)
}
