package com.pedro.maschio.carcostsmanagement.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pedro.maschio.carcostsmanagement.data.database.daos.CarCostDao
import com.pedro.maschio.carcostsmanagement.data.database.daos.CarDao
import com.pedro.maschio.carcostsmanagement.data.database.entities.Car
import com.pedro.maschio.carcostsmanagement.data.database.entities.CarCost

@Database(entities = [Car::class, CarCost::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun carDao(): CarDao
    abstract fun carCostDao(): CarCostDao
}
