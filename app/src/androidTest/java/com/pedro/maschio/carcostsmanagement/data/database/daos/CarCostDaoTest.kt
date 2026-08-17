package com.pedro.maschio.carcostsmanagement.data.database.daos

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pedro.maschio.carcostsmanagement.data.database.AppDatabase
import com.pedro.maschio.carcostsmanagement.data.database.entities.Car
import com.pedro.maschio.carcostsmanagement.data.database.entities.CarCost
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class CarCostDaoTest {
    private lateinit var carCostDao: CarCostDao
    private lateinit var carDao: CarDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        carCostDao = db.carCostDao()
        carDao = db.carDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndSumCosts() = runBlocking {
        val car = Car(id = 1, name = "Civic", mileage = 0)
        carDao.insertCar(car)
        
        val cost1 = CarCost(id = 1, type = 0, price = 150.0, date = 1000L, carId = 1, description = "Gas")
        val cost2 = CarCost(id = 2, type = 0, price = 100.0, date = 2000L, carId = 1, description = "Oil")
        
        carCostDao.insertCosts(listOf(cost1, cost2))
        
        val total = carCostDao.getTotalCosts(1).first()
        assertEquals(250.0, total!!, 0.0)
    }

    @Test
    fun getRecurringCosts() = runBlocking {
        val car = Car(id = 1, name = "Civic", mileage = 0)
        carDao.insertCar(car)
        
        val recurringCost = CarCost(id = 1, type = 1, price = 500.0, date = 1000L, carId = 1, recurrence = 1, description = "Tax")
        val normalCost = CarCost(id = 2, type = 0, price = 100.0, date = 2000L, carId = 1, description = "Gas")
        
        carCostDao.insertCosts(listOf(recurringCost, normalCost))
        
        val recurring = carCostDao.getAllRecurringCosts()
        assertEquals(1, recurring.size)
        assertEquals("Tax", recurring[0].description)
    }
}
