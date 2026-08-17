package com.pedro.maschio.carcostsmanagement.ui.screens.cars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedro.maschio.carcostsmanagement.data.database.entities.Car
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CarsScreenViewModel(private val carCostsRepository: CarCostsRepository) : ViewModel() {
    private val _isDeleteDialogShowing = MutableStateFlow(false)
    private val _selectedToDeleteCar = MutableStateFlow<Car?>(null)

    val uiState = combine(
        carCostsRepository.getCars(),
        _isDeleteDialogShowing,
        _selectedToDeleteCar
    ) { cars, isDeleteDialogShowing, selectedToDeleteCar ->
        CarsScreenUiState(
            cars = cars,
            isDeleteDialogShowing = isDeleteDialogShowing,
            selectedToDeleteCar = selectedToDeleteCar
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CarsScreenUiState()
    )

    fun deleteCar() = viewModelScope.launch {
        val car = _selectedToDeleteCar.value ?: return@launch
        carCostsRepository.deleteCar(car)
        toggleDeleteDialog(null)
    }

    fun toggleDeleteDialog(car: Car?) {
        _isDeleteDialogShowing.value = !_isDeleteDialogShowing.value
        _selectedToDeleteCar.value = car
    }

    fun updateCar(car: Car) = viewModelScope.launch {
        carCostsRepository.updateCar(car)
    }
}
