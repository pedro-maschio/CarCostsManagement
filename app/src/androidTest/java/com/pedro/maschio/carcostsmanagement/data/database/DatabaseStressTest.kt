package com.pedro.maschio.carcostsmanagement.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pedro.maschio.carcostsmanagement.data.database.daos.CarCostDao
import com.pedro.maschio.carcostsmanagement.data.database.daos.CarDao
import com.pedro.maschio.carcostsmanagement.data.database.entities.Car
import com.pedro.maschio.carcostsmanagement.data.database.entities.CarCost
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.system.measureTimeMillis

@RunWith(AndroidJUnit4::class)
class DatabaseStressTest {

    private lateinit var db: AppDatabase
    private lateinit var carDao: CarDao
    private lateinit var carCostDao: CarCostDao

    @Before
    fun createDb() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        carDao = db.carDao()
        carCostDao = db.carCostDao()

        // Setup: Insert a car
        carDao.insertCar(Car(id = 1, name = "Test Car", mileage = 0))
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testPerformanceWithLargeAmountOfEntries() = runBlocking {
        val entriesCount = 10000
        
        // 1. Measure insertion time for 10,000 entries
        val insertionTime = measureTimeMillis {
            for (i in 1..entriesCount) {
                carCostDao.insertCost(
                    CarCost(
                        description = "Cost $i",
                        price = 10.0,
                        date = System.currentTimeMillis(),
                        carId = 1,
                        type = 0
                    )
                )
            }
        }
        println("Insertion of $entriesCount entries took $insertionTime ms")

        // 2. Measure aggregation time (SUM)
        val aggregationTime = measureTimeMillis {
            val total = carCostDao.getTotalCosts(1)
            assert(total == entriesCount * 10.0)
        }
        println("Aggregation of $entriesCount entries took $aggregationTime ms")

        // Assert reasonable performance (e.g., aggregation under 500ms for 10k entries)
        assert(aggregationTime < 500) { "Aggregation took too long: $aggregationTime ms" }
    }
}
