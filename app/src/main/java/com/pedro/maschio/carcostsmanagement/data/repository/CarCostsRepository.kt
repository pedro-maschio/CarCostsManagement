package com.pedro.maschio.carcostsmanagement.data.repository

import androidx.paging.PagingData
import com.pedro.maschio.carcostsmanagement.data.database.entities.Car
import com.pedro.maschio.carcostsmanagement.data.database.entities.CarCost
import kotlinx.coroutines.flow.Flow

interface CarCostsRepository {
    val introShown: Flow<Boolean>
    val selectedCar: Flow<Long?>
    val isLoggedIn: Flow<Boolean>
    val ethanolPrice: Flow<Double>
    val gasolinePrice: Flow<Double>

    suspend fun setIsIntroShown()
    suspend fun setSelectedCar(carId: Long)
    suspend fun setFuelPrices(ethanol: Double, gasoline: Double)
    suspend fun insertCost(cost: CarCost): Long
    suspend fun insertCosts(costs: List<CarCost>)
    suspend fun updateCost(cost: CarCost)
    suspend fun deleteCost(cost: CarCost)
    fun getCosts(selectedCarId: Long): Flow<PagingData<CarCost>>
    suspend fun getTotalCosts(selectedCarId: Long): Double
    suspend fun insertCar(car: Car)
    suspend fun updateCar(car: Car)
    suspend fun getCars(): List<Car>
    suspend fun getCar(id: Long): Car?
    suspend fun deleteCar(car: Car)
    suspend fun setIsLoggedIn(isLoggedIn: Boolean)
    suspend fun getCost(id: Long): CarCost?
    suspend fun getRecurringCosts(): List<CarCost>
}
