package com.pedro.maschio.carcostsmanagement.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedro.maschio.carcostsmanagement.data.database.entities.Car
import com.pedro.maschio.carcostsmanagement.data.database.entities.CarCost
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainScreenViewModel(private val repository: CarCostsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(MainScreenUiState())
    val uiState = _uiState.asStateFlow()

    val selectedCarId = repository.selectedCar
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun getMainScreenData() = viewModelScope.launch {
        selectedCarId.filterNotNull().collectLatest { carId ->
            getCars()
            getMileage()
            getTotalCosts()
            getCosts(selectedCarId.value ?: 0)
        }

    }

    fun getCosts(selectedCarId: Long = 0) = viewModelScope.launch {
        _uiState.update { it.copy(costs = repository.getCosts(selectedCarId).toList()) }
    }

    fun deleteCostEntry(cost: CarCost) = viewModelScope.launch {
        repository.deleteCost(cost)
        getMainScreenData()
    }

    fun toggleAddEntry() {
        _uiState.value = _uiState.value.copy(isAddEntryShown = !_uiState.value.isAddEntryShown)
    }

    fun addCostEntry(cost: CarCost) = viewModelScope.launch {
        val selectedCarId = selectedCarId.value
        if(selectedCarId != null) {
            if(cost.id == 0L) repository.insertCost(cost.copy(carId = selectedCarId))
            else repository.updateCost(cost.copy(carId = selectedCarId))
            getMainScreenData()
        }
    }

    private fun getMileage() = viewModelScope.launch {
        selectedCarId.filterNotNull().collectLatest { carId ->
            _uiState.update { it.copy(totalMileage = repository.getMileage(carId)) }
        }

    }

    private fun getTotalCosts() = viewModelScope.launch {
        selectedCarId.filterNotNull().collectLatest { carId ->
            _uiState.update { it.copy(totalCosts = repository.getTotalCosts(carId)) }
        }
    }
    fun toggleAddCarDialog() {
        _uiState.value =
            _uiState.value.copy(isAddCarDialogShown = !_uiState.value.isAddCarDialogShown)
    }

    fun updateCarName(name: String) {
        _uiState.update { it.copy(currentCarName = name) }
    }

    fun getCars() = viewModelScope.launch {
        _uiState.update { it.copy(cars = repository.getCars()) }
    }

    fun addCar() = viewModelScope.launch {
        if (_uiState.value.currentCarName.isBlank()) return@launch

        repository.insertCar(car = Car(name = _uiState.value.currentCarName))
        getCars()
    }

    fun selectCar(car: Car) = viewModelScope.launch {
        repository.setSelectedCar(carId = car.id)
        getCosts(car.id)
    }
}