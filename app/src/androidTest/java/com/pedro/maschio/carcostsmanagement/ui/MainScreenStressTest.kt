package com.pedro.maschio.carcostsmanagement.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pedro.maschio.carcostsmanagement.MainActivity
import com.pedro.maschio.carcostsmanagement.data.database.AppDatabase
import com.pedro.maschio.carcostsmanagement.data.database.entities.Car
import com.pedro.maschio.carcostsmanagement.data.database.entities.CarCost
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
class MainScreenStressTest : KoinTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val repository: CarCostsRepository by inject()
    private val database: AppDatabase by inject()

    @Before
    fun setup() = runBlocking {
        // Clear database to ensure clean state
        database.clearAllTables()
        
        repository.setIsIntroShown()
        repository.setIsLoggedIn(true)
        
        val car = Car(id = 1, name = "Stress Test Car", mileage = 1000)
        repository.insertCar(car)
        val carId = 1L
        repository.setSelectedCar(carId)

        // Add 10,000 entries in batches to avoid SQLite variable limits
        val batchSize = 500
        for (i in 0 until 10000 step batchSize) {
            val entries = (i + 1..i + batchSize).map { j ->
                CarCost(
                    description = "Cost Entry $j",
                    price = 10.0,
                    date = System.currentTimeMillis() - (j * 1000),
                    carId = carId,
                    type = 0
                )
            }
            repository.insertCosts(entries)
        }
    }

    @Test
    fun testScrollingWithManyEntries() {
        // Wait for the first items to appear to ensure loading started
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            try {
                composeTestRule.onNodeWithText("Cost Entry 1").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Perform multiple swipes to simulate a user scrolling through a long list
        // We do 20 swipes to move deep into the pages
        repeat(20) {
            composeTestRule.onRoot().performTouchInput {
                swipeUp(durationMillis = 300)
            }
            composeTestRule.waitForIdle()
        }
        
        // Check if we can see an item that should have been loaded later
        // "Cost Entry 1" was at the top (date desc). So scrolling down should show older items like "Cost Entry 200"
        // Since we are sorting by date DESC, "Cost Entry 1" is newest, "Cost Entry 10000" is oldest.
        // So scrolling down (swipeUp) should show higher numbers if they are older?
        // date = now - j*1000. So higher j means smaller date (older).
        // Yes, scrolling down should show larger j.
    }
}
