package com.pedro.maschio.carcostsmanagement.data.database.daos

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pedro.maschio.carcostsmanagement.data.database.AppDatabase
import com.pedro.maschio.carcostsmanagement.data.database.entities.Car
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class CarDaoTest {
    private lateinit var carDao: CarDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        carDao = db.carDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeCarAndReadInList() = runBlocking {
        val car = Car(id = 1, name = "Civic", mileage = 100)
        carDao.insertCar(car)
        val allCars = carDao.getAllCars().first()
        assertEquals(allCars[0].name, car.name)
    }

    @Test
    @Throws(Exception::class)
    fun updateCarAndRead() = runBlocking {
        val car = Car(id = 1, name = "Civic", mileage = 100)
        carDao.insertCar(car)
        val updatedCar = car.copy(name = "Civic Turbo")
        carDao.updateCar(updatedCar)
        val readCar = carDao.getCar(1)
        assertEquals("Civic Turbo", readCar?.name)
    }

    @Test
    @Throws(Exception::class)
    fun deleteCar() = runBlocking {
        val car = Car(id = 1, name = "Civic", mileage = 100)
        carDao.insertCar(car)
        carDao.deleteCar(car)
        val readCar = carDao.getCar(1)
        assertNull(readCar)
    }
}
