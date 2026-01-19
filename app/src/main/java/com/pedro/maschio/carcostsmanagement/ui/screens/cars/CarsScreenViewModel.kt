package com.pedro.maschio.carcostsmanagement.ui.screens.cars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedro.maschio.carcostsmanagement.data.database.entities.Car
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CarsScreenViewModel(private val carCostsRepository: CarCostsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CarsScreenUiState())
    val uiState = _uiState.asStateFlow()

    fun getCars() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(cars = carCostsRepository.getCars().toList())
    }

    fun deleteCar() = viewModelScope.launch {
        val car = _uiState.value.selectedToDeleteCar ?: return@launch
        carCostsRepository.deleteCar(car)
        toggleDeleteDialog(null)
        getCars()
    }

    fun toggleDeleteDialog(car: Car?) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(
            isDeleteDialogShowing = !_uiState.value.isDeleteDialogShowing,
            selectedToDeleteCar = car
        )
    }
}
