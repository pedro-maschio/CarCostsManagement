package com.pedro.maschio.carcostsmanagement.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pedro.maschio.carcostsmanagement.R
import com.pedro.maschio.carcostsmanagement.data.database.entities.CarCost
import com.pedro.maschio.carcostsmanagement.ui.components.AddCostEntry
import com.pedro.maschio.carcostsmanagement.ui.components.CircleLetter
import com.pedro.maschio.carcostsmanagement.ui.components.CostEntry
import com.pedro.maschio.carcostsmanagement.ui.components.CostsHeader
import com.pedro.maschio.carcostsmanagement.ui.components.EmptyList
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
    var selectedCostEntry by remember { mutableStateOf<CarCost?>(null) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.getMainScreenData()
    }

    when {
        uiState.isAddCarDialogShown -> {
            AlertDialog(
                title = {
                    Text(text = stringResource(R.string.add_new_car))
                },
                text = {
                    TextField(value = uiState.currentCarName, onValueChange = {
                        viewModel.updateCarName(it)
                    })
                },
                onDismissRequest = {
                    viewModel.toggleAddCarDialog()
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.addCar()
                            viewModel.toggleAddCarDialog()
                            viewModel.updateCarName("")
                        }
                    ) {
                        Text(stringResource(R.string.positive_button))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            viewModel.toggleAddCarDialog()
                            viewModel.updateCarName("")
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
                            viewModel.selectCar(car)
                        }
                    }
                    if (uiState.cars.size <= 3) {
                        IconButton(onClick = {
                            viewModel.toggleAddCarDialog()
                        }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        }
                    }
                })


        }) { paddingValues ->
            LazyColumn(
                modifier = modifier.padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    CostsHeader(totalCosts = uiState.totalCosts,)
                    AnimatedVisibility(visible = !(uiState.isAddEntryShown)) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            onClick = { viewModel.toggleAddEntry() }
                        ) {
                            Text(text = stringResource(R.string.add_cost))
                        }
                    }
                    AnimatedVisibility(visible = uiState.isAddEntryShown) {
                        AddCostEntry(
                            costEntry = selectedCostEntry,
                            onCostEntryCancelled = {
                                selectedCostEntry = null
                                viewModel.toggleAddEntry()
                            }, onCostEntryAdded = { cost ->
                                selectedCostEntry = null
                                viewModel.addCostEntry(cost)
                                viewModel.toggleAddEntry()
                            })
                    }
                }

                items(uiState.costs) { cost ->
                    CostEntry(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        title = cost.description,
                        typeName = cost.type,
                        price = cost.price,
                        date = cost.date,
                        onCostClick = {
                            selectedCostEntry = cost
                            viewModel.toggleAddEntry()
                        },
                        onDeleteButtonClick = {
                            viewModel.deleteCostEntry(cost)
                        }
                    )
                }

                item {
                    if (uiState.costs.isEmpty()) {
                        EmptyList()
                    }
                }

            }
        }
    }
}