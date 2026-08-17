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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val repository: CarCostsRepository,
    private val recurrenceManager: RecurrenceManager
) : ViewModel() {

    private val _isAddEntryShown = MutableStateFlow(false)
    private val _isAddCarDialogShown = MutableStateFlow(false)
    private val _currentCarName = MutableStateFlow("")
    private val _isFuelPriceDialogShown = MutableStateFlow(false)
    private val _isUpdateMileageDialogShown = MutableStateFlow(false)

    val selectedCarId = repository.selectedCar
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    private val _cars = repository.getCars()

    @OptIn(ExperimentalCoroutinesApi::class)
    val costs = selectedCarId.filterNotNull().flatMapLatest { carId ->
        repository.getCosts(carId).cachedIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _totalCosts = selectedCarId.flatMapLatest { id ->
        if (id != null) repository.getTotalCosts(id) else flowOf(0.0)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = combine(
        _isAddEntryShown,
        _isAddCarDialogShown,
        _cars,
        _currentCarName,
        repository.ethanolPrice,
        repository.gasolinePrice,
        _isFuelPriceDialogShown,
        _isUpdateMileageDialogShown,
        selectedCarId,
        _totalCosts
    ) { args ->
        val isAddEntryShown = args[0] as Boolean
        val isAddCarDialogShown = args[1] as Boolean
        val cars = args[2] as List<Car>
        val currentCarName = args[3] as String
        val ethanolPrice = args[4] as Double
        val gasolinePrice = args[5] as Double
        val isFuelPriceDialogShown = args[6] as Boolean
        val isUpdateMileageDialogShown = args[7] as Boolean
        val selId = args[8] as Long?
        val totalCosts = (args[9] as Double?) ?: 0.0

        val selectedCar = cars.find { it.id == selId }
        val kmRemaining = selectedCar?.let { (it.lastOilChangeMileage + it.oilChangeInterval) - it.mileage }
        val maintenanceAlertKmRemaining = if (kmRemaining != null && kmRemaining <= 500) {
            kmRemaining
        } else null

        MainScreenUiState(
            isAddEntryShown = isAddEntryShown,
            isAddCarDialogShown = isAddCarDialogShown,
            cars = cars,
            currentCarName = currentCarName,
            totalCosts = totalCosts,
            ethanolPrice = ethanolPrice,
            gasolinePrice = gasolinePrice,
            isFuelPriceDialogShown = isFuelPriceDialogShown,
            isUpdateMileageDialogShown = isUpdateMileageDialogShown,
            maintenanceAlertKmRemaining = maintenanceAlertKmRemaining,
            currentMileage = selectedCar?.mileage ?: 0
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainScreenUiState()
    )

    init {
        // Validation logic for selectedCarId
        combine(selectedCarId, _cars) { carId, cars ->
            if (cars.isNotEmpty()) {
                val validCarId = if (carId == null || cars.none { it.id == carId }) {
                    cars.firstOrNull()?.id
                } else {
                    carId
                }
                if (validCarId != null && validCarId != carId) {
                    repository.setSelectedCar(validCarId)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun setFuelPrices(ethanol: Double, gasoline: Double) = viewModelScope.launch {
        repository.setFuelPrices(ethanol, gasoline)
    }

    fun toggleFuelPriceDialog() {
        _isFuelPriceDialogShown.value = !_isFuelPriceDialogShown.value
    }

    fun toggleUpdateMileageDialog() {
        _isUpdateMileageDialogShown.value = !_isUpdateMileageDialogShown.value
    }

    fun updateMileage(mileage: Int) = viewModelScope.launch {
        val carId = selectedCarId.value ?: return@launch
        val car = repository.getCar(carId) ?: return@launch
        repository.updateCar(car.copy(mileage = mileage))
    }

    fun markOilChanged() = viewModelScope.launch {
        val carId = selectedCarId.value ?: return@launch
        val car = repository.getCar(carId) ?: return@launch
        repository.updateCar(car.copy(lastOilChangeMileage = car.mileage))
    }

    fun deleteCostEntry(cost: CarCost) = viewModelScope.launch {
        repository.deleteCost(cost)
        recurrenceManager.cancel(cost.id)
    }

    fun toggleAddEntry() {
        _isAddEntryShown.value = !_isAddEntryShown.value
    }

    fun showAddEntry() {
        _isAddEntryShown.value = true
    }

    fun addCostEntry(cost: CarCost) = viewModelScope.launch {
        val carId = selectedCarId.value
        if (carId != null) {
            val costToSave = cost.copy(carId = carId)
            val costId = if (cost.id == 0L) {
                repository.insertCost(costToSave)
            } else {
                repository.updateCost(costToSave)
                cost.id
            }
            recurrenceManager.schedule(costId, costToSave.copy(id = costId))
        }
    }

    fun toggleAddCarDialog() {
        _isAddCarDialogShown.value = !_isAddCarDialogShown.value
    }

    fun updateCarName(name: String) {
        _currentCarName.value = name
    }

    fun addCar(mileage: Int = 0) = viewModelScope.launch {
        val name = _currentCarName.value
        if (name.isBlank()) return@launch
        repository.insertCar(car = Car(name = name, mileage = mileage, lastOilChangeMileage = mileage))
    }

    fun selectCar(car: Car) = viewModelScope.launch {
        repository.setSelectedCar(carId = car.id)
    }
}
