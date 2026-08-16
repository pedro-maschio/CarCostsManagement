package com.pedro.maschio.carcostsmanagement.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.pedro.maschio.carcostsmanagement.data.database.entities.CarCost
import com.pedro.maschio.carcostsmanagement.data.database.entities.CostType
import com.pedro.maschio.carcostsmanagement.data.database.entities.RecurrenceType
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RescheduleWorkerTest {

    private val context: Context = mockk(relaxed = true)
    private val workerParams: WorkerParameters = mockk(relaxed = true)
    private val repository: CarCostsRepository = mockk()
    private val recurrenceManager: RecurrenceManager = mockk(relaxed = true)

    private lateinit var worker: RescheduleWorker

    @Before
    fun setup() {
        worker = RescheduleWorker(
            context,
            workerParams,
            repository,
            recurrenceManager
        )
    }

    @Test
    fun `doWork reschedules all recurring costs`() = runTest {
        val costs = listOf(
            CarCost(id = 1, description = "A", price = 10.0, date = 0, type = CostType.MAINTENANCE.value, carId = 1, recurrence = RecurrenceType.MONTHLY.value),
            CarCost(id = 2, description = "B", price = 20.0, date = 0, type = CostType.OTHERS.value, carId = 1, recurrence = RecurrenceType.YEARLY.value)
        )
        coEvery { repository.getRecurringCosts() } returns costs

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        coVerify {
            recurrenceManager.schedule(1, costs[0])
            recurrenceManager.schedule(2, costs[1])
        }
    }
}
