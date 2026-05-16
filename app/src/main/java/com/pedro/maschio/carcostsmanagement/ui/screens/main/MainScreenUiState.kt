package com.pedro.maschio.carcostsmanagement.ui.screens.main

import com.pedro.maschio.carcostsmanagement.data.database.entities.Car
import com.pedro.maschio.carcostsmanagement.data.database.entities.CarCost

data class MainScreenUiState(
    val isAddEntryShown: Boolean = false,
    val isAddCarDialogShown: Boolean = false,
    val cars: List<Car> = emptyList(),
    val currentCarName: String = "",
    val totalCosts: Double = 0.0,
    val ethanolPrice: Double = 0.0,
    val gasolinePrice: Double = 0.0,
    val isFuelPriceDialogShown: Boolean = false,
    val isUpdateMileageDialogShown: Boolean = false,
    val maintenanceAlert: String? = null,
    val currentMileage: Int = 0
)
