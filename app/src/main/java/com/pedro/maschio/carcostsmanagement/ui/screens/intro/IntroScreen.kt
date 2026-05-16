package com.pedro.maschio.carcostsmanagement.ui.screens.intro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pedro.maschio.carcostsmanagement.R
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntroScreen(
    modifier: Modifier = Modifier,
    goToCostsListing: () -> Unit,
    viewModel: IntroViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collectLatest {
            goToCostsListing()
        }
    }

    IntroScreenContent(
        modifier = modifier,
        uiState = uiState.value,
        onCarNameChanged = { viewModel.onCarNameChanged(it) },
        onCarMileageChanged = { viewModel.onCarMileageChanged(it) },
        onSaveCar = { viewModel.onSaveCar() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntroScreenContent(
    modifier: Modifier = Modifier,
    uiState: IntroUiState,
    onCarNameChanged: (String) -> Unit,
    onCarMileageChanged: (String) -> Unit,
    onSaveCar: () -> Unit
) {
    Scaffold(modifier = modifier, topBar = {
        TopAppBar(
            title = {
                Text(text = stringResource(R.string.add_your_first_car))
            })
    }, floatingActionButton = {
        FloatingActionButton(onClick = {
            onSaveCar()
        }) {
            Text(text = stringResource(R.string.next))
        }
    }) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.add_your_first_car_message),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.carName,
                onValueChange = {
                    onCarNameChanged(it)
                },
                label = { Text("Nome do Carro") },
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.carMileage,
                onValueChange = {
                    onCarMileageChanged(it)
                },
                label = { Text("Quilometragem Atual") },
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(onGo = {
                    onSaveCar()
                })
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IntroScreenPreview() {
    IntroScreenContent(
        uiState = IntroUiState(carName = "Sandero", carMileage = "50000"),
        onCarNameChanged = {},
        onCarMileageChanged = {},
        onSaveCar = {}
    )
}
