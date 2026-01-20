package com.pedro.maschio.carcostsmanagement.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pedro.maschio.carcostsmanagement.ui.screens.cars.CarsScreen
import com.pedro.maschio.carcostsmanagement.ui.screens.intro.IntroScreen
import com.pedro.maschio.carcostsmanagement.ui.screens.main.MainScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
object IntroScreen

@Serializable
object MainScreen

@Serializable
object CarsScreen

@Composable
fun NavHost(modifier: Modifier = Modifier, viewModel: AppViewModel = koinViewModel()) {
    val navController = rememberNavController()
    val introShown = viewModel.introShown.collectAsState().value

    when (introShown) {
        null -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }

        }

        true -> {
            NavHost(
                navController = navController,
                startDestination = MainScreen,
                modifier = modifier
            ) {
                mainGraph(navController)
            }
        }

        false -> {
            NavHost(
                navController = navController,
                startDestination = IntroScreen,
                modifier = modifier
            ) {
                mainGraph(navController)
            }
        }
    }
}

fun NavGraphBuilder.mainGraph(navController: NavHostController) {
    composable<IntroScreen> {
        IntroScreen(
            goToCostsListing = {
                navController.navigate(MainScreen) {
                    popUpTo(IntroScreen) { inclusive = true }
                }
            }
        )
    }

    composable<MainScreen> {
        MainScreen {
            navController.navigate(CarsScreen)
        }
    }

    composable<CarsScreen> {
        CarsScreen {
            navController.popBackStack()
        }
    }
}