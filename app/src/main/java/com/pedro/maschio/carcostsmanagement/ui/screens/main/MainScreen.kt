package com.pedro.maschio.carcostsmanagement.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.pedro.maschio.carcostsmanagement.R
import com.pedro.maschio.carcostsmanagement.data.database.entities.Car
import com.pedro.maschio.carcostsmanagement.data.database.entities.CarCost
import com.pedro.maschio.carcostsmanagement.ui.components.AddCostEntry
import com.pedro.maschio.carcostsmanagement.ui.components.CircleLetter
import com.pedro.maschio.carcostsmanagement.ui.components.CostEntry
import com.pedro.maschio.carcostsmanagement.ui.components.CostsHeader
import com.pedro.maschio.carcostsmanagement.ui.components.EmptyList
import com.pedro.maschio.carcostsmanagement.ui.components.FlexAlert
import com.pedro.maschio.carcostsmanagement.ui.components.MaintenanceAlert
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = koinViewModel(),
    onDeleteButtonClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedCarId = viewModel.selectedCarId.collectAsStateWithLifecycle().value
    val costs = viewModel.costs.collectAsLazyPagingItems()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.getMainScreenData()
    }

    MainScreenContent(
        modifier = modifier,
        uiState = uiState,
        selectedCarId = selectedCarId,
        costs = costs,
        onDeleteButtonClick = onDeleteButtonClick,
        onToggleAddEntry = { viewModel.toggleAddEntry() },
        onShowAddEntry = { viewModel.showAddEntry() },
        onToggleAddCarDialog = { viewModel.toggleAddCarDialog() },
        onToggleFuelPriceDialog = { viewModel.toggleFuelPriceDialog() },
        onToggleUpdateMileageDialog = { viewModel.toggleUpdateMileageDialog() },
        onUpdateCarName = { viewModel.updateCarName(it) },
        onAddCar = { viewModel.addCar(it) },
        onSelectCar = { viewModel.selectCar(it) },
        onAddCostEntry = { viewModel.addCostEntry(it) },
        onDeleteCostEntry = { viewModel.deleteCostEntry(it) },
        onSetFuelPrices = { ethanol, gasoline -> viewModel.setFuelPrices(ethanol, gasoline) },
        onUpdateMileage = { viewModel.updateMileage(it) },
        onMarkOilChanged = { viewModel.markOilChanged() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
    modifier: Modifier = Modifier,
    uiState: MainScreenUiState,
    selectedCarId: Long?,
    costs: LazyPagingItems<CarCost>,
    onDeleteButtonClick: () -> Unit,
    onToggleAddEntry: () -> Unit,
    onShowAddEntry: () -> Unit,
    onToggleAddCarDialog: () -> Unit,
    onToggleFuelPriceDialog: () -> Unit,
    onToggleUpdateMileageDialog: () -> Unit,
    onUpdateCarName: (String) -> Unit,
    onAddCar: (Int) -> Unit,
    onSelectCar: (Car) -> Unit,
    onAddCostEntry: (CarCost) -> Unit,
    onDeleteCostEntry: (CarCost) -> Unit,
    onSetFuelPrices: (Double, Double) -> Unit,
    onUpdateMileage: (Int) -> Unit,
    onMarkOilChanged: () -> Unit
) {
    var selectedCostEntry by remember { mutableStateOf<CarCost?>(null) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    when {
        uiState.isFuelPriceDialogShown -> {
            var ethanol by remember { mutableStateOf(uiState.ethanolPrice.toString()) }
            var gasoline by remember { mutableStateOf(uiState.gasolinePrice.toString()) }
            AlertDialog(
                onDismissRequest = { onToggleFuelPriceDialog() },
                title = { Text(stringResource(R.string.fuel_prices_title)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = ethanol,
                            onValueChange = { ethanol = it },
                            label = { Text(stringResource(R.string.ethanol_label)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        OutlinedTextField(
                            value = gasoline,
                            onValueChange = { gasoline = it },
                            label = { Text(stringResource(R.string.gasoline_label)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        onSetFuelPrices(
                            ethanol.toDoubleOrNull() ?: 0.0,
                            gasoline.toDoubleOrNull() ?: 0.0
                        )
                        onToggleFuelPriceDialog()
                    }) {
                        Text(stringResource(R.string.save_button))
                    }
                }
            )
        }
        uiState.isUpdateMileageDialogShown -> {
            var mileage by remember(uiState.isUpdateMileageDialogShown, uiState.currentMileage) {
                mutableStateOf(uiState.currentMileage.toString())
            }
            AlertDialog(
                onDismissRequest = { onToggleUpdateMileageDialog() },
                title = { Text("Atualizar Quilometragem") },
                text = {
                    OutlinedTextField(
                        value = mileage,
                        onValueChange = { mileage = it },
                        label = { Text("KM Atual") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        mileage.toIntOrNull()?.let { onUpdateMileage(it) }
                        onToggleUpdateMileageDialog()
                    }) {
                        Text("Salvar")
                    }
                }
            )
        }
        uiState.isAddCarDialogShown -> {
            var mileage by remember { mutableStateOf("") }
            AlertDialog(
                title = {
                    Text(text = stringResource(R.string.add_new_car))
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextField(
                            value = uiState.currentCarName,
                            onValueChange = { onUpdateCarName(it) },
                            label = { Text("Nome do Carro") }
                        )
                        OutlinedTextField(
                            value = mileage,
                            onValueChange = { mileage = it },
                            label = { Text("Quilometragem Atual") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                },
                onDismissRequest = {
                    onToggleAddCarDialog()
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onAddCar(mileage.toIntOrNull() ?: 0)
                            onToggleAddCarDialog()
                            onUpdateCarName("")
                        }
                    ) {
                        Text(stringResource(R.string.positive_button))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            onToggleAddCarDialog()
                            onUpdateCarName("")
                        }
                    ) {
                        Text(stringResource(R.string.dismiss_button))
                    }
                }
            )
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Settings", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = stringResource(R.string.manage_cars)) },
                    selected = false,
                    onClick = {
                        onDeleteButtonClick()
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null
                        )
                    }
                )

            }
        }
    ) {
        Scaffold(topBar = {
            TopAppBar(
                title = {
                }, navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = null)
                    }
                }, actions = {
                    uiState.cars.forEach { car ->
                        CircleLetter(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            isSelected = car.id == selectedCarId,
                            carName = car.name
                        ) {
                            onSelectCar(car)
                        }
                    }
                    if (uiState.cars.size <= 3) {
                        IconButton(onClick = {
                            onToggleAddCarDialog()
                        }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        }
                    }
                    IconButton(onClick = { onToggleFuelPriceDialog() }) {
                        Icon(imageVector = Icons.Default.LocalGasStation, contentDescription = null)
                    }
                    IconButton(onClick = { onToggleUpdateMileageDialog() }) {
                        Icon(imageVector = Icons.Default.Speed, contentDescription = null)
                    }
                })


        }) { paddingValues ->
            LazyColumn(
                modifier = modifier.padding(paddingValues),
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    CostsHeader(totalCosts = uiState.totalCosts,)
                    
                    FlexAlert(
                        ethanolPrice = uiState.ethanolPrice,
                        gasolinePrice = uiState.gasolinePrice,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    
                    uiState.maintenanceAlert?.let {
                        MaintenanceAlert(
                            message = it,
                            onActionClick = { onMarkOilChanged() },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    AnimatedVisibility(visible = !(uiState.isAddEntryShown)) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            onClick = {
                                onToggleAddEntry()
                                scope.launch {
                                    listState.animateScrollToItem(0)
                                }
                            }
                        ) {
                            Text(text = stringResource(R.string.add_cost))
                        }
                    }
                    AnimatedVisibility(visible = uiState.isAddEntryShown) {
                        AddCostEntry(
                            costEntry = selectedCostEntry,
                            onCostEntryCancelled = {
                                selectedCostEntry = null
                                onToggleAddEntry()
                            }, onCostEntryAdded = { cost ->
                                selectedCostEntry = null
                                onAddCostEntry(cost)
                                onToggleAddEntry()
                            })
                    }
                }

                items(
                    count = costs.itemCount,
                    key = costs.itemKey { it.id }
                ) { index ->
                    val cost = costs[index]
                    if (cost != null) {
                        CostEntry(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            title = cost.description,
                            type = cost.type,
                            price = cost.price,
                            date = cost.date,
                            onCostClick = {
                                selectedCostEntry = cost
                                onShowAddEntry()
                                scope.launch {
                                    listState.animateScrollToItem(0)
                                }
                            },
                            onDeleteButtonClick = {
                                onDeleteCostEntry(cost)
                            }
                        )
                    }
                }

                item {
                    if (costs.itemCount == 0) {
                        EmptyList(message = stringResource(R.string.empty_expenses_message))
                    }
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val costs = flowOf(PagingData.from(listOf(
        CarCost(id = 1, type = 0, price = 250.0, date = System.currentTimeMillis(), description = "Posto Shell", carId = 1)
    ))).collectAsLazyPagingItems()

    MainScreenContent(
        uiState = MainScreenUiState(
            cars = listOf(Car(id = 1, name = "Sandero")),
            totalCosts = 500.0,
            ethanolPrice = 3.50,
            gasolinePrice = 5.50
        ),
        selectedCarId = 1,
        costs = costs,
        onDeleteButtonClick = {},
        onToggleAddEntry = {},
        onShowAddEntry = {},
        onToggleAddCarDialog = {},
        onToggleFuelPriceDialog = {},
        onToggleUpdateMileageDialog = {},
        onUpdateCarName = {},
        onAddCar = {},
        onSelectCar = {},
        onAddCostEntry = {},
        onDeleteCostEntry = {},
        onSetFuelPrices = { _, _ -> },
        onUpdateMileage = {},
        onMarkOilChanged = {}
    )
}
