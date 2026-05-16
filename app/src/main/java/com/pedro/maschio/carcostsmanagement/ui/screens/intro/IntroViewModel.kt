package com.pedro.maschio.carcostsmanagement.ui.screens.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedro.maschio.carcostsmanagement.data.database.entities.Car
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class IntroViewModel(private val repository: CarCostsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(IntroUiState())
    private val _uiEvents = MutableSharedFlow<IntroUiEvents>()
    val uiState = _uiState.asStateFlow()
    val uiEvents = _uiEvents.asSharedFlow()

    fun onSaveCar() = viewModelScope.launch {
        val mileage = _uiState.value.carMileage.toIntOrNull() ?: 0
        repository.insertCar(
            Car(
                name = _uiState.value.carName,
                mileage = mileage,
                lastOilChangeMileage = mileage
            )
        )
        repository.setIsIntroShown()
        _uiEvents.emit(IntroUiEvents.GoToCarListing)
    }

    fun onCarNameChanged(name: String) {
        _uiState.update { it.copy(carName = name) }
    }

    fun onCarMileageChanged(mileage: String) {
        _uiState.update { it.copy(carMileage = mileage) }
    }

}