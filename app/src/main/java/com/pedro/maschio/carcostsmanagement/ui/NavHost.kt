package com.pedro.maschio.carcostsmanagement.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pedro.maschio.carcostsmanagement.ui.screens.cars.CarsScreen
import com.pedro.maschio.carcostsmanagement.ui.screens.intro.IntroScreen
import com.pedro.maschio.carcostsmanagement.ui.screens.login.LoginScreen
import com.pedro.maschio.carcostsmanagement.ui.screens.main.MainScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
object LoginScreen

@Serializable
object IntroScreen

@Serializable
object MainScreen

@Serializable
object CarsScreen

@Composable
fun NavHost(modifier: Modifier = Modifier, viewModel: AppViewModel = koinViewModel()) {
    val navController = rememberNavController()
    val introShownState by viewModel.introShown.collectAsStateWithLifecycle()
    val isLoggedInState by viewModel.isLoggedIn.collectAsStateWithLifecycle()

    if (introShownState == null || isLoggedInState == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val startDestination = remember(introShownState, isLoggedInState) {
        when {
            isLoggedInState == false -> LoginScreen
            introShownState == false -> IntroScreen
            else -> MainScreen
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        mainGraph(navController)
    }
}

fun NavGraphBuilder.mainGraph(navController: NavHostController) {
    composable<LoginScreen> {
        LoginScreen(
            onLoginSuccess = {
                if (navController.currentBackStackEntry?.lifecycle?.currentState == androidx.lifecycle.Lifecycle.State.RESUMED) {
                    navController.navigate(IntroScreen) {
                        popUpTo(LoginScreen) { inclusive = true }
                    }
                }
            }
        )
    }

    composable<IntroScreen> {
        IntroScreen(
            goToCostsListing = {
                if (navController.currentBackStackEntry?.lifecycle?.currentState == androidx.lifecycle.Lifecycle.State.RESUMED) {
                    navController.navigate(MainScreen) {
                        popUpTo(IntroScreen) { inclusive = true }
                    }
                }
            }
        )
    }

    composable<MainScreen> {
        MainScreen {
            if (navController.currentBackStackEntry?.lifecycle?.currentState == androidx.lifecycle.Lifecycle.State.RESUMED) {
                navController.navigate(CarsScreen) {
                    launchSingleTop = true
                }
            }
        }
    }

    composable<CarsScreen> {
        CarsScreen {
            if (navController.currentBackStackEntry?.lifecycle?.currentState == androidx.lifecycle.Lifecycle.State.RESUMED) {
                navController.popBackStack()
            }
        }
    }
}
