package com.pedro.maschio.carcostsmanagement.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.pedro.maschio.carcostsmanagement.data.database.entities.Car
import com.pedro.maschio.carcostsmanagement.data.database.entities.CarCost
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val costs = selectedCarId.filterNotNull().flatMapLatest { carId ->
        repository.getCosts(carId).cachedIn(viewModelScope)
    }

    fun getMainScreenData() = viewModelScope.launch {
        selectedCarId.collectLatest { carId ->
            val cars = repository.getCars()
            _uiState.update { it.copy(cars = cars) }

            // Validate the selected car ID
            val validCarId = if (carId == null || cars.none { it.id == carId }) {
                cars.firstOrNull()?.id
            } else {
                carId
            }

            // If we found a different valid ID (or no ID), update the repository
            if (validCarId != null && validCarId != carId) {
                repository.setSelectedCar(validCarId)
                return@collectLatest // The flow will emit again
            }

            if (validCarId != null) {
                getTotalCosts()
                getFuelPrices()
                getGasolinePrices()
                checkMaintenance(validCarId)
            } else {
                // Reset state if no cars exist
                _uiState.update { MainScreenUiState() }
                getCars() // To keep the empty cars list
            }
        }
    }

    private fun getFuelPrices() = viewModelScope.launch {
        repository.ethanolPrice.collectLatest { ethanol ->
            _uiState.update { it.copy(ethanolPrice = ethanol) }
        }
    }

    private fun getGasolinePrices() = viewModelScope.launch {
        repository.gasolinePrice.collectLatest { gasoline ->
            _uiState.update { it.copy(gasolinePrice = gasoline) }
        }
    }

    fun setFuelPrices(ethanol: Double, gasoline: Double) = viewModelScope.launch {
        repository.setFuelPrices(ethanol, gasoline)
    }

    fun toggleFuelPriceDialog() {
        _uiState.update { it.copy(isFuelPriceDialogShown = !it.isFuelPriceDialogShown) }
    }

    fun toggleUpdateMileageDialog() {
        _uiState.update { it.copy(isUpdateMileageDialogShown = !it.isUpdateMileageDialogShown) }
    }

    fun updateMileage(mileage: Int) = viewModelScope.launch {
        val carId = selectedCarId.value ?: return@launch
        val car = repository.getCar(carId) ?: return@launch
        repository.updateCar(car.copy(mileage = mileage))
        // Update local state immediately for better responsiveness
        _uiState.update { it.copy(currentMileage = mileage) }
        checkMaintenance(carId)
        getCars()
    }

    fun markOilChanged() = viewModelScope.launch {
        val carId = selectedCarId.value ?: return@launch
        val car = repository.getCar(carId) ?: return@launch
        repository.updateCar(car.copy(lastOilChangeMileage = car.mileage))
        checkMaintenance(carId)
        getCars()
    }

    private fun checkMaintenance(carId: Long) = viewModelScope.launch {
        val car = repository.getCar(carId)
        if (car != null) {
            _uiState.update { it.copy(currentMileage = car.mileage) }
            val kmRemaining = (car.lastOilChangeMileage + car.oilChangeInterval) - car.mileage
            if (kmRemaining <= 500) {
                _uiState.update { it.copy(maintenanceAlert = "Troca de óleo em $kmRemaining km") }
            } else {
                _uiState.update { it.copy(maintenanceAlert = null) }
            }
        } else {
             _uiState.update { it.copy(currentMileage = 0, maintenanceAlert = null) }
        }
    }

    fun deleteCostEntry(cost: CarCost) = viewModelScope.launch {
        repository.deleteCost(cost)
        getTotalCosts()
    }

    fun toggleAddEntry() {
        _uiState.update { it.copy(isAddEntryShown = !it.isAddEntryShown) }
    }

    fun showAddEntry() {
        _uiState.update { it.copy(isAddEntryShown = true) }
    }

    fun addCostEntry(cost: CarCost) = viewModelScope.launch {
        val selectedCarId = selectedCarId.value
        if(selectedCarId != null) {
            if(cost.id == 0L) repository.insertCost(cost.copy(carId = selectedCarId))
            else repository.updateCost(cost.copy(carId = selectedCarId))
            getTotalCosts()
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
        val cars = repository.getCars()
        _uiState.update { it.copy(cars = cars) }
        selectedCarId.value?.let { carId ->
            cars.find { it.id == carId }?.let { car ->
                _uiState.update { it.copy(currentMileage = car.mileage) }
            }
        }
    }

    fun addCar(mileage: Int = 0) = viewModelScope.launch {
        if (_uiState.value.currentCarName.isBlank()) return@launch

        repository.insertCar(car = Car(name = _uiState.value.currentCarName, mileage = mileage, lastOilChangeMileage = mileage))
        getCars()
    }

    fun selectCar(car: Car) = viewModelScope.launch {
        repository.setSelectedCar(carId = car.id)
    }
}