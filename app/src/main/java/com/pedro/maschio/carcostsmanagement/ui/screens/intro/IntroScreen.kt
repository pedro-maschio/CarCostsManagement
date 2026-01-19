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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
                Text(text = "Create your first car")
            })
    }, floatingActionButton = {
        FloatingActionButton(onClick = {
            viewModel.onSaveCar()
        }) {
            Text(text = "Next")
        }
    }) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Before you start, add your first car")

            TextField(value = uiState.value.carName, onValueChange = {
                viewModel.onCarNameChanged(it)
            }, maxLines = 1)
        }
    }
}