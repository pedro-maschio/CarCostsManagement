package com.pedro.maschio.carcostsmanagement.worker

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.pedro.maschio.carcostsmanagement.data.database.entities.CarCost
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
    fun `schedule enqueues unique work for recurring expense`() {
        val costId = 1L
        val cost = CarCost(
            id = costId,
            description = "Monthly Maintenance",
            price = 100.0,
            date = System.currentTimeMillis(),
            type = 1,
            carId = 1,
            recurrence = 1 // Monthly
        )

        recurrenceManager.schedule(costId, cost)

        verify { 
            workManager.enqueueUniqueWork(
                "recurrence_$costId",
                ExistingWorkPolicy.REPLACE,
                any<OneTimeWorkRequest>()
            )
        }
    }

    @Test
    fun `schedule cancels work if recurrence is none`() {
        val costId = 1L
        val cost = CarCost(
            id = costId,
            description = "One time cost",
            price = 100.0,
            date = System.currentTimeMillis(),
            type = 1,
            carId = 1,
            recurrence = 0 // None
        )

        recurrenceManager.schedule(costId, cost)

        verify { workManager.cancelUniqueWork("recurrence_$costId") }
    }

    @Test
    fun `cancel cancels unique work`() {
        val costId = 1L
        
        recurrenceManager.cancel(costId)

        verify { workManager.cancelUniqueWork("recurrence_$costId") }
    }
}
