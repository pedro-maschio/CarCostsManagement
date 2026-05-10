package com.pedro.maschio.carcostsmanagement.ui.screens.cars

import com.pedro.maschio.carcostsmanagement.data.database.entities.Car

data class CarsScreenUiState(
    val cars: List<Car> = emptyList(),
    val isDeleteDialogShowing: Boolean = false,
    val selectedToDeleteCar: Car? = null
)