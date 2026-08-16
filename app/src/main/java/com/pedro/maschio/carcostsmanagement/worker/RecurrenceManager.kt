package com.pedro.maschio.carcostsmanagement.worker

import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.pedro.maschio.carcostsmanagement.data.database.entities.CarCost
import com.pedro.maschio.carcostsmanagement.data.database.entities.RecurrenceType
import com.pedro.maschio.carcostsmanagement.utils.DateUtils
import java.util.concurrent.TimeUnit

class RecurrenceManager(private val workManager: WorkManager) {

    fun schedule(costId: Long, cost: CarCost) {
        if (cost.recurrence == RecurrenceType.NONE.value) {
            cancel(costId)
            return
        }

        val delay = DateUtils.calculateNextOccurrenceDelay(cost.date, cost.recurrence)
        if (delay < 0) return

        val data = Data.Builder()
            .putLong("cost_id", costId)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<RecurrenceWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        workManager.enqueueUniqueWork(
            "recurrence_$costId",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancel(costId: Long) {
        workManager.cancelUniqueWork("recurrence_$costId")
    }
}
