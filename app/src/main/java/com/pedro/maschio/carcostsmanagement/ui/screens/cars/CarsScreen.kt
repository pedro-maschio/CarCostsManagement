package com.pedro.maschio.carcostsmanagement.ui.screens.cars

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    if (uiState.isDeleteDialogShowing) {
        AlertDialog(
            icon = {
                Icon(Icons.Default.Delete, contentDescription = "Example Icon")
            },
            title = {
                Text(text = "Are you sure you want to delete?")
            },
            text = {
                Text(text = "If you delete a car, all cost entries related to it will also be deleted permanently")
            },
            onDismissRequest = {
               viewModel.toggleDeleteDialog(null)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCar()
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.toggleDeleteDialog(null)
                    }
                ) {
                    Text("Dismiss")
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
            Text(text = "Your cars")
        })
    }) { paddingValues ->
        LazyColumn(modifier = modifier.padding(paddingValues)) {
            items(uiState.cars) { car ->
                CarItem(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    car = car,
                    isDeletable = uiState.cars.size > 1
                ) {
                    viewModel.toggleDeleteDialog(car)
                }
            }


            item {
                if (uiState.cars.isEmpty()) {
                    EmptyList(
                        modifier = Modifier.padding(top = 16.dp),
                        message = "There are no cars added yet. Go back to the home screen" +
                                "and click on the plus icon on the right corner"
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
    onClick: () -> Unit
) {

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
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = car.name)
            }
            if(isDeletable) {
                IconButton(onClick = onClick) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                }
            }
        }
    }
}

@Preview
@Composable
fun CarItemPreview(modifier: Modifier = Modifier) {
    CarItem(car = Car(id = -1, name = "Sandero"), isDeletable = true) { }
}