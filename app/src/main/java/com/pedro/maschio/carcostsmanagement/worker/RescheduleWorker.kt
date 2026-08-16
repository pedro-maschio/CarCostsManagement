package com.pedro.maschio.carcostsmanagement.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepository

class RescheduleWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val repository: CarCostsRepository,
    private val recurrenceManager: RecurrenceManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val recurringCosts = repository.getRecurringCosts()
        
        recurringCosts.forEach { cost ->
            recurrenceManager.schedule(cost.id, cost)
        }

        return Result.success()
    }
}
