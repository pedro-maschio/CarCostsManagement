package com.pedro.maschio.carcostsmanagement.data.repository

import android.content.Context
import com.pedro.maschio.carcostsmanagement.data.SettingsKeys
import com.pedro.maschio.carcostsmanagement.data.dataStore
import com.pedro.maschio.carcostsmanagement.data.database.daos.CarCostDao
import com.pedro.maschio.carcostsmanagement.data.database.daos.CarDao
import com.pedro.maschio.carcostsmanagement.data.database.entities.Car
import com.pedro.maschio.carcostsmanagement.data.database.entities.CarCost
import com.pedro.maschio.carcostsmanagement.data.setIntroShown
import com.pedro.maschio.carcostsmanagement.data.setSelectedCarId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class CarCostsRepositoryImpl(
    private val context: Context,
    private val carCostDao: CarCostDao,
    private val carDao: CarDao,
) :
    CarCostsRepository {

    override suspend fun setIsIntroShown() {
        setIntroShown(context)
    }

    override val introShown: Flow<Boolean> =
        context.dataStore.data.map {
            it[SettingsKeys.INTRO_SHOWN] ?: false
        }

    override suspend fun setSelectedCar(carId: Long) {
        setSelectedCarId(context, carId)
    }

    override val selectedCar: Flow<Long> = context.dataStore.data.map {
        it[SettingsKeys.SELECTED_CAR_ID]
            ?: 1 // initial room id (when there's only one car, this one is the default selected!)
    }

    override suspend fun insertCost(cost: CarCost) {
        carCostDao.insertCost(cost.copy(createdAt = System.currentTimeMillis()))
    }

    override suspend fun updateCost(cost: CarCost) {
        carCostDao.updateCost(cost.copy(updatedAt = System.currentTimeMillis()))
    }


    override suspend fun deleteCost(cost: CarCost) {
        carCostDao.deleteCost(cost)
    }

    override suspend fun getCosts(selectedCarId: Long): List<CarCost> {
        return carCostDao.getAllCosts(selectedCarId)
    }

    override suspend fun getMileage(selectedCarId: Long): Int {
        return carCostDao.getCurrentMileage(selectedCarId)
    }

    override suspend fun getTotalCosts(selectedCarId: Long): Double {
        return carCostDao.getTotalCosts(selectedCarId)
    }

    override suspend fun insertCar(car: Car) {
        carDao.insertCar(car)

    }

    override suspend fun updateCar(car: Car) {
        carDao.updateCar(car)
    }

    override suspend fun getCars(): List<Car> {
        return carDao.getAllCars()
    }

    override suspend fun deleteCar(car: Car) {
        carDao.deleteCar(car)

        // Ensure that there is a selected car after deletion
        val cars = getCars()
        val currentSelectedCar = selectedCar.first()
        if (currentSelectedCar !in cars.map { it.id }) {
            setSelectedCar(cars.first().id)
        }
    }

}