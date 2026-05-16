package com.pedro.maschio.carcostsmanagement.data.repository

import android.content.Context
import app.cash.turbine.test
import com.pedro.maschio.carcostsmanagement.data.database.daos.CarCostDao
import com.pedro.maschio.carcostsmanagement.data.database.daos.CarDao
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CarCostsRepositoryImplTest {

    private val context: Context = mockk(relaxed = true)
    private val carCostDao: CarCostDao = mockk(relaxed = true)
    private val carDao: CarDao = mockk(relaxed = true)
    private lateinit var repository: CarCostsRepositoryImpl

    @Before
    fun setup() {
        repository = CarCostsRepositoryImpl(context, carCostDao, carDao)
    }

    @Test
    fun `getCosts calls carCostDao getAllCosts when collected`() = runTest {
        val carId = 1L
        
        repository.getCosts(carId).test {
            // Pager will call the factory when it needs to load data
            verify { carCostDao.getAllCosts(carId) }
            cancelAndIgnoreRemainingEvents()
        }
    }
}
