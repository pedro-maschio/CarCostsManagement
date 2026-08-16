package com.pedro.maschio.carcostsmanagement.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.pedro.maschio.carcostsmanagement.data.database.entities.CarCost
import com.pedro.maschio.carcostsmanagement.data.database.entities.CostType
import com.pedro.maschio.carcostsmanagement.data.database.entities.RecurrenceType
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepository
import com.pedro.maschio.carcostsmanagement.utils.NotificationHelper
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RecurrenceWorkerTest {

    private val context: Context = mockk(relaxed = true)
    private val workerParams: WorkerParameters = mockk(relaxed = true)
    private val repository: CarCostsRepository = mockk()
    private val notificationHelper: NotificationHelper = mockk(relaxed = true)
    private val recurrenceManager: RecurrenceManager = mockk(relaxed = true)

    private lateinit var worker: RecurrenceWorker

    @Before
    fun setup() {
        worker = RecurrenceWorker(
            context,
            workerParams,
            repository,
            notificationHelper,
            recurrenceManager
        )
    }

    @Test
    fun `doWork shows notification and schedules next occurrence`() = runTest {
        val costId = 1L
        val cost = CarCost(
            id = costId,
            description = "Test Maintenance",
            price = 100.0,
            date = System.currentTimeMillis(),
            type = CostType.MAINTENANCE.value,
            carId = 1,
            recurrence = RecurrenceType.MONTHLY.value
        )

        every { workerParams.inputData } returns workDataOf("cost_id" to costId)
        coEvery { repository.getCost(costId) } returns cost

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        
        coVerify { 
            notificationHelper.showRecurrenceNotification(costId, cost.description)
            recurrenceManager.schedule(costId, cost)
        }
    }

    @Test
    fun `doWork returns failure if costId is missing`() = runTest {
        every { workerParams.inputData } returns workDataOf()

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.failure(), result)
    }

    @Test
    fun `doWork returns failure if cost is not found`() = runTest {
        val costId = 1L
        every { workerParams.inputData } returns workDataOf("cost_id" to costId)
        coEvery { repository.getCost(costId) } returns null

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.failure(), result)
    }
}
