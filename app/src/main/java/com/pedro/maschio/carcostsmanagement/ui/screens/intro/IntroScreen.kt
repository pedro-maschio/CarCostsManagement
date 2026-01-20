package com.pedro.maschio.carcostsmanagement.ui.screens.intro

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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

    Scaffold(modifier = modifier, topBar = {
        TopAppBar(
            title = {
                Text(text = stringResource(R.string.add_your_first_car))
            })
    }, floatingActionButton = {
        FloatingActionButton(onClick = {
            viewModel.onSaveCar()
        }) {
            Text(text = stringResource(R.string.next))
        }
    }) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = stringResource(R.string.add_your_first_car_message))
            TextField(value = uiState.value.carName, onValueChange = {
                viewModel.onCarNameChanged(it)
            }, maxLines = 1)
        }
    }
}