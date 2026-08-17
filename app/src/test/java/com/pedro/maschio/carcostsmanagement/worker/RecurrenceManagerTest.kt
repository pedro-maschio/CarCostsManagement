package com.pedro.maschio.carcostsmanagement.worker

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.pedro.maschio.carcostsmanagement.data.database.entities.CarCost
import com.pedro.maschio.carcostsmanagement.data.database.entities.RecurrenceType
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class RecurrenceManagerTest {

    private val workManager: WorkManager = mockk(relaxed = true)
    private lateinit var recurrenceManager: RecurrenceManager

    @Before
    fun setup() {
        recurrenceManager = RecurrenceManager(workManager)
    }

    @Test
    fun `schedule with NONE recurrence calls cancel`() {
        val cost = CarCost(id = 1, type = 0, price = 100.0, date = System.currentTimeMillis(), carId = 1, recurrence = RecurrenceType.NONE.value, description = "Test")
        recurrenceManager.schedule(1, cost)
        verify { workManager.cancelUniqueWork("recurrence_1") }
    }

    @Test
    fun `schedule with recurrence enqueues unique work`() {
        // Use a future date to ensure positive delay
        val futureDate = System.currentTimeMillis() + 1000 * 60 * 60 * 24 
        val cost = CarCost(id = 1, type = 0, price = 100.0, date = futureDate, carId = 1, recurrence = RecurrenceType.MONTHLY.value, description = "Test")
        
        recurrenceManager.schedule(1, cost)
        
        verify { workManager.enqueueUniqueWork("recurrence_1", any<ExistingWorkPolicy>(), any<OneTimeWorkRequest>()) }
    }

    @Test
    fun `cancel calls workManager`() {
        recurrenceManager.cancel(1)
        verify { workManager.cancelUniqueWork("recurrence_1") }
    }
}
