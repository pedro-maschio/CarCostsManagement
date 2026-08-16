package com.pedro.maschio.carcostsmanagement

import android.app.Application
import androidx.work.Configuration
import com.pedro.maschio.carcostsmanagement.di.appModule
import com.pedro.maschio.carcostsmanagement.utils.NotificationHelper
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.GlobalContext.startKoin

class MainApplication: Application(), Configuration.Provider {

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(get<KoinWorkerFactory>())
            .build()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            workManagerFactory()
            modules(appModule)
        }

        get<NotificationHelper>().createNotificationChannel()
    }
}
