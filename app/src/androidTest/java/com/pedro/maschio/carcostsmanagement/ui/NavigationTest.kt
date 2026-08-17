package com.pedro.maschio.carcostsmanagement.ui

import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepository
import com.pedro.maschio.carcostsmanagement.ui.screens.cars.CarsScreenViewModel
import com.pedro.maschio.carcostsmanagement.ui.screens.intro.IntroViewModel
import com.pedro.maschio.carcostsmanagement.ui.screens.login.LoginViewModel
import com.pedro.maschio.carcostsmanagement.ui.screens.main.MainScreenViewModel
import com.pedro.maschio.carcostsmanagement.worker.RecurrenceManager
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest

@RunWith(AndroidJUnit4::class)
class NavigationTest : KoinTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val repository: CarCostsRepository = mockk(relaxed = true)
    private val recurrenceManager: RecurrenceManager = mockk(relaxed = true)
    
    private val introShownFlow = MutableStateFlow(false)
    private val isLoggedInFlow = MutableStateFlow(false)

    @Before
    fun setup() {
        stopKoin() // Ensure fresh start
        startKoin {
            modules(module {
                single { repository }
                single { recurrenceManager }
                single { AppViewModel(get()) }
                factory { LoginViewModel(get()) }
                factory { IntroViewModel(get()) }
                factory { MainScreenViewModel(get(), get()) }
                factory { CarsScreenViewModel(get()) }
            })
        }
        
        every { repository.introShown } returns introShownFlow
        every { repository.isLoggedIn } returns isLoggedInFlow
        every { repository.selectedCar } returns MutableStateFlow(null)
        every { repository.ethanolPrice } returns MutableStateFlow(0.0)
        every { repository.gasolinePrice } returns MutableStateFlow(0.0)
        every { repository.getCars() } returns MutableStateFlow(emptyList())
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun startDestination_isLogin_whenNotLoggedIn() {
        isLoggedInFlow.value = false
        
        composeTestRule.setContent {
            NavHost()
        }

        // Title and Button both have "Login". Check for the button.
        composeTestRule.onNode(hasText("Login") and hasClickAction()).assertExists()
    }

    @Test
    fun startDestination_isIntro_whenLoggedInButIntroNotShown() {
        isLoggedInFlow.value = true
        introShownFlow.value = false
        
        composeTestRule.setContent {
            NavHost()
        }

        composeTestRule.onNodeWithText("Add your first car").assertExists()
    }
}
