package com.pedro.maschio.carcostsmanagement.data.repository

import android.content.Context
import com.pedro.maschio.carcostsmanagement.data.database.daos.CarCostDao
import com.pedro.maschio.carcostsmanagement.data.database.daos.CarDao
import com.pedro.maschio.carcostsmanagement.data.database.entities.Car
import com.pedro.maschio.carcostsmanagement.data.database.entities.CarCost
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class CarCostsRepositoryImplTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val context: Context = mockk()
    private val carCostDao: CarCostDao = mockk(relaxed = true)
    private val carDao: CarDao = mockk(relaxed = true)
    private lateinit var repository: CarCostsRepositoryImpl

    @Before
    fun setup() {
        val filesDir = tempFolder.newFolder("files")
        every { context.filesDir } returns filesDir
        every { context.applicationContext } returns context
        
        // Mock default flows to avoid NoSuchElementException
        every { carDao.getAllCars() } returns flowOf(emptyList())
        
        repository = CarCostsRepositoryImpl(context, carCostDao, carDao)
    }

    @Test
    fun `insertCar calls carDao`() = runTest {
        val car = Car(name = "Civic", mileage = 0)
        coEvery { carDao.insertCar(car) } returns Unit
        repository.insertCar(car)
        coVerify { carDao.insertCar(car) }
    }

    @Test
    fun `getCars returns flow from carDao`() = runTest {
        val cars = listOf(Car(id = 1, name = "Civic", mileage = 0))
        every { carDao.getAllCars() } returns flowOf(cars)
        
        repository.getCars().collect {
            assertEquals(cars, it)
        }
    }

    @Test
    fun `insertCost adds timestamp and calls carCostDao`() = runTest {
        val cost = CarCost(type = 0, price = 100.0, date = 0, carId = 1, description = "Gas")
        coEvery { carCostDao.insertCost(any()) } returns 1L
        
        repository.insertCost(cost)
        
        coVerify { carCostDao.insertCost(match { it.price == 100.0 && it.createdAt > 0 }) }
    }

    @Test
    fun `deleteCar calls carDao`() = runTest {
        val carToDelete = Car(id = 1, name = "Civic", mileage = 0)
        every { carDao.getAllCars() } returns flowOf(emptyList())
        coEvery { carDao.deleteCar(carToDelete) } returns Unit
        
        repository.deleteCar(carToDelete)
        coVerify { carDao.deleteCar(carToDelete) }
    }
}
