package com.pedro.maschio.carcostsmanagement.ui.screens.cars

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pedro.maschio.carcostsmanagement.R
import com.pedro.maschio.carcostsmanagement.data.database.entities.Car
import com.pedro.maschio.carcostsmanagement.ui.components.EmptyList
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarsScreen(
    modifier: Modifier = Modifier,
    viewModel: CarsScreenViewModel = koinViewModel(),
    onBackPress: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getCars()
    }

    CarsScreenContent(
        modifier = modifier,
        uiState = uiState,
        onBackPress = onBackPress,
        onDeleteCar = { viewModel.deleteCar() },
        onToggleDeleteDialog = { viewModel.toggleDeleteDialog(it) },
        onUpdateCar = { viewModel.updateCar(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarsScreenContent(
    modifier: Modifier = Modifier,
    uiState: CarsScreenUiState,
    onBackPress: () -> Unit,
    onDeleteCar: () -> Unit,
    onToggleDeleteDialog: (Car?) -> Unit,
    onUpdateCar: (Car) -> Unit
) {
    if (uiState.isDeleteDialogShowing) {
        AlertDialog(
            icon = {
                Icon(Icons.Default.Delete, contentDescription = "Example Icon")
            },
            title = {
                Text(text = stringResource(R.string.delete_dialog_title))
            },
            text = {
                Text(text = stringResource(R.string.delete_dialog_message))
            },
            onDismissRequest = {
                onToggleDeleteDialog(null)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteCar()
                    }
                ) {
                    Text(stringResource(R.string.positive_button))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onToggleDeleteDialog(null)
                    }
                ) {
                    Text(stringResource(R.string.dismiss_button))
                }
            }
        )
    }

    Scaffold(topBar = {
        TopAppBar(navigationIcon = {
            IconButton(onClick = {
                onBackPress()
            }) {
                Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
            }
        }, title = {
            Text(text = stringResource(R.string.cars_screen_title))
        })
    }) { paddingValues ->
        LazyColumn(modifier = modifier.padding(paddingValues)) {
            item {
                if (uiState.cars.isNotEmpty()) {
                    Surface(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color.LightGray.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, Color.Gray)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                            Text(
                                text = stringResource(R.string.rename_instruction),
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
            items(uiState.cars) { car ->
                CarItem(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    car = car,
                    isDeletable = uiState.cars.size > 1,
                    onDeleteClick = {
                        onToggleDeleteDialog(car)
                    },
                    onRename = { updatedCar ->
                        onUpdateCar(updatedCar)
                    }
                )
            }


            item {
                if (uiState.cars.isEmpty()) {
                    EmptyList(
                        modifier = Modifier.padding(top = 16.dp),
                        message = stringResource(R.string.empty_cars_message)
                    )
                }
            }

        }
    }
}


@Composable
fun CarItem(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(size = 8.dp),
    car: Car,
    isDeletable: Boolean,
    onDeleteClick: () -> Unit,
    onRename: (Car) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember(car.name) { mutableStateOf(car.name) }
    Surface(
        modifier = modifier
            .clip(shape),
        shape = shape,
        shadowElevation = 4.dp,
        border = BorderStroke(width = 1.dp, color = Color.Black)
    ) {
        Row(
            modifier = Modifier.padding(all = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isEditing) {
                TextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (editedName.isNotBlank()) {
                            onRename(car.copy(name = editedName))
                            isEditing = false
                        }
                    })
                )
                IconButton(onClick = {
                    if (editedName.isNotBlank()) {
                        onRename(car.copy(name = editedName))
                        isEditing = false
                    }
                }) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                }
                IconButton(onClick = {
                    isEditing = false
                    editedName = car.name
                }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null)
                }
            } else {
                Text(
                    text = car.name,
                    modifier = Modifier
                        .weight(1f)
                        .pointerInput(Unit) {
                            detectTapGestures(onDoubleTap = {
                                isEditing = true
                            })
                        }
                )
                if (isDeletable) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CarsScreenPreview() {
    CarsScreenContent(
        uiState = CarsScreenUiState(
            cars = listOf(
                Car(id = 1, name = "Sandero"),
                Car(id = 2, name = "Gol")
            )
        ),
        onBackPress = {},
        onDeleteCar = {},
        onToggleDeleteDialog = {},
        onUpdateCar = {}
    )
}

@Preview
@Composable
fun CarItemPreview(modifier: Modifier = Modifier) {
    CarItem(
        car = Car(id = -1, name = "Sandero"),
        isDeletable = true,
        onDeleteClick = {},
        onRename = {}
    )
}
