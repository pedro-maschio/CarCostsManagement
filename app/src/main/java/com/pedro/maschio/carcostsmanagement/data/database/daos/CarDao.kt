package com.pedro.maschio.carcostsmanagement.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pedro.maschio.carcostsmanagement.data.database.entities.Car

@Dao
interface CarDao {

    @Insert
    suspend fun insertCar(car: Car)

    @Delete
    suspend fun deleteCar(car: Car)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCar(car: Car)

    @Query("SELECT * FROM cars ORDER BY createdAt ASC")
    suspend fun getAllCars(): List<Car>

    @Query("SELECT * FROM cars WHERE id = :id")
    suspend fun getCar(id: Long): Car?

}