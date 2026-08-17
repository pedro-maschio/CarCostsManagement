package com.pedro.maschio.carcostsmanagement.worker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.workDataOf
import com.pedro.maschio.carcostsmanagement.data.database.entities.CarCost
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepository
import com.pedro.maschio.carcostsmanagement.utils.NotificationHelper
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecurrenceWorkerTest {
    private lateinit var context: Context
    private val repository: CarCostsRepository = mockk(relaxed = true)
    private val notificationHelper: NotificationHelper = mockk(relaxed = true)
    private val recurrenceManager: RecurrenceManager = mockk(relaxed = true)

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testRecurrenceWorkerSuccess() = runBlocking {
        val costId = 1L
        val cost = CarCost(id = 1, type = 0, price = 100.0, date = System.currentTimeMillis(), carId = 1, description = "Test")
        
        coEvery { repository.getCost(costId) } returns cost

        val worker = TestListenableWorkerBuilder<RecurrenceWorker>(
            context = context,
            inputData = workDataOf("cost_id" to costId)
        ).setWorkerFactory(object : androidx.work.WorkerFactory() {
            override fun createWorker(
                appContext: Context,
                workerClassName: String,
                workerParameters: WorkerParameters
            ): ListenableWorker {
                return RecurrenceWorker(appContext, workerParameters, repository, notificationHelper, recurrenceManager)
            }
        }).build()

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        verify { notificationHelper.showRecurrenceNotification(costId, "Test") }
        verify { recurrenceManager.schedule(costId, cost) }
    }
}
