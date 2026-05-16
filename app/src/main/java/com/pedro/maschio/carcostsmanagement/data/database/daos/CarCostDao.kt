package com.pedro.maschio.carcostsmanagement.data.database.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pedro.maschio.carcostsmanagement.data.database.entities.CarCost

@Dao
interface CarCostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCost(cost: CarCost)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCosts(costs: List<CarCost>)

    @Update
    suspend fun updateCost(cost: CarCost)

    @Delete
    suspend fun deleteCost(cost: CarCost)

    @Query("SELECT * FROM carCosts WHERE carId = :selectedCarId ORDER BY date DESC")
    fun getAllCosts(selectedCarId: Long): PagingSource<Int, CarCost>

    @Query("SELECT SUM(carCosts.price) FROM carCosts WHERE carId = :selectedCarId")
    suspend fun getTotalCosts(selectedCarId: Long): Double
}