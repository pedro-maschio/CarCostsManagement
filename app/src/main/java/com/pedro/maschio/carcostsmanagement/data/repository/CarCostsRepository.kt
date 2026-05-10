package com.pedro.maschio.carcostsmanagement.data.repository

import com.pedro.maschio.carcostsmanagement.data.database.entities.Car
import com.pedro.maschio.carcostsmanagement.data.database.entities.CarCost
import kotlinx.coroutines.flow.Flow

interface CarCostsRepository {
    val introShown: Flow<Boolean>
    val selectedCar: Flow<Long>
    val isLoggedIn: Flow<Boolean>

    suspend fun setIsIntroShown()
    suspend fun setSelectedCar(carId: Long)
    suspend fun insertCost(cost: CarCost)
    suspend fun updateCost(cost: CarCost)
    suspend fun deleteCost(cost: CarCost)
    suspend fun getCosts(selectedCarId: Long): List<CarCost>
    suspend fun getTotalCosts(selectedCarId: Long): Double
    suspend fun insertCar(car: Car)
    suspend fun updateCar(car: Car)
    suspend fun getCars(): List<Car>
    suspend fun deleteCar(car: Car)
    suspend fun setIsLoggedIn(isLoggedIn: Boolean)
}