package com.pedro.maschio.carcostsmanagement.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepository
import com.pedro.maschio.carcostsmanagement.utils.NotificationHelper

class RecurrenceWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val repository: CarCostsRepository,
    private val notificationHelper: NotificationHelper,
    private val recurrenceManager: RecurrenceManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val costId = inputData.getLong("cost_id", -1L)

        if (costId == -1L) return Result.failure()

        val cost = repository.getCost(costId) ?: return Result.failure()

        notificationHelper.showRecurrenceNotification(costId, cost.description)

        // Schedule next occurrence
        recurrenceManager.schedule(costId, cost)

        return Result.success()
    }
}
