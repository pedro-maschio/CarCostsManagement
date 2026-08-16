package com.pedro.maschio.carcostsmanagement.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.pedro.maschio.carcostsmanagement.data.database.entities.Car
import com.pedro.maschio.carcostsmanagement.data.database.entities.CarCost
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepository
import com.pedro.maschio.carcostsmanagement.worker.RecurrenceManager
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

class MainScreenViewModel(
    private val repository: CarCostsRepository,
    private val recurrenceManager: RecurrenceManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainScreenUiState())
    val uiState = _uiState.asStateFlow()

    val selectedCarId = repository.selectedCar
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly, // Changed to Eagerly to prevent navigation flicker
            initialValue = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val costs = selectedCarId.filterNotNull().flatMapLatest { carId ->
        repository.getCosts(carId).cachedIn(viewModelScope)
    }

    init {
        observeSelectedCar()
        observeFuelPrices()
    }

    private fun observeSelectedCar() = viewModelScope.launch {
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
                return@collectLatest
            }

            if (validCarId != null) {
                updateTotalCosts(validCarId)
                checkMaintenance(validCarId)
            } else {
                _uiState.update { MainScreenUiState() }
                getCars() 
            }
        }
    }

    private fun observeFuelPrices() = viewModelScope.launch {
        launch {
            repository.ethanolPrice.collect { ethanol ->
                _uiState.update { it.copy(ethanolPrice = ethanol) }
            }
        }
        launch {
            repository.gasolinePrice.collect { gasoline ->
                _uiState.update { it.copy(gasolinePrice = gasoline) }
            }
        }
    }

    private suspend fun updateTotalCosts(carId: Long) {
        val total = repository.getTotalCosts(carId)
        _uiState.update { it.copy(totalCosts = total) }
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
        recurrenceManager.cancel(cost.id)
        selectedCarId.value?.let { updateTotalCosts(it) }
    }

    fun toggleAddEntry() {
        _uiState.update { it.copy(isAddEntryShown = !it.isAddEntryShown) }
    }

    fun showAddEntry() {
        _uiState.update { it.copy(isAddEntryShown = true) }
    }

    fun addCostEntry(cost: CarCost) = viewModelScope.launch {
        val carId = selectedCarId.value
        if(carId != null) {
            val costToSave = cost.copy(carId = carId)
            val costId = if(cost.id == 0L) {
                repository.insertCost(costToSave)
            } else {
                repository.updateCost(costToSave)
                cost.id
            }
            recurrenceManager.schedule(costId, costToSave.copy(id = costId))
            updateTotalCosts(carId)
        }
    }

    fun toggleAddCarDialog() {
        _uiState.update { it.copy(isAddCarDialogShown = !it.isAddCarDialogShown) }
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
