package com.pedro.maschio.carcostsmanagement.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pedro.maschio.carcostsmanagement.R
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collectLatest { event ->
            when (event) {
                LoginUiEvent.LoginSuccess -> onLoginSuccess()
            }
        }
    }

    LoginContent(
        uiState = uiState,
        onUsernameChanged = viewModel::onUsernameChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onLoginClicked = viewModel::onLoginClicked,
        onGoogleLoginClicked = viewModel::onGoogleLoginClicked
    )
}

@Composable
fun LoginContent(
    uiState: LoginUiState,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClicked: () -> Unit,
    onGoogleLoginClicked: () -> Unit
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.login_title),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = uiState.username,
                onValueChange = onUsernameChanged,
                label = { Text(stringResource(R.string.username_label)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChanged,
                label = { Text(stringResource(R.string.password_label)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = onLoginClicked,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.login_button))
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onGoogleLoginClicked,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.login_with_google))
                }
            }

            uiState.error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginContentPreview() {
    LoginContent(
        uiState = LoginUiState(),
        onUsernameChanged = {},
        onPasswordChanged = {},
        onLoginClicked = {},
        onGoogleLoginClicked = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun LoginContentLoadingPreview() {
    LoginContent(
        uiState = LoginUiState(isLoading = true),
        onUsernameChanged = {},
        onPasswordChanged = {},
        onLoginClicked = {},
        onGoogleLoginClicked = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun LoginContentErrorPreview() {
    LoginContent(
        uiState = LoginUiState(error = "Invalid credentials"),
        onUsernameChanged = {},
        onPasswordChanged = {},
        onLoginClicked = {},
        onGoogleLoginClicked = {}
    )
}
