package com.pedro.maschio.carcostsmanagement.di

import androidx.room.Room
import androidx.work.WorkManager
import com.pedro.maschio.carcostsmanagement.data.database.AppDatabase
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepository
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepositoryImpl
import com.pedro.maschio.carcostsmanagement.ui.AppViewModel
import com.pedro.maschio.carcostsmanagement.ui.screens.cars.CarsScreenViewModel
import com.pedro.maschio.carcostsmanagement.ui.screens.intro.IntroViewModel
import com.pedro.maschio.carcostsmanagement.ui.screens.login.LoginViewModel
import com.pedro.maschio.carcostsmanagement.ui.screens.main.MainScreenViewModel
import com.pedro.maschio.carcostsmanagement.utils.NotificationHelper
import com.pedro.maschio.carcostsmanagement.worker.RecurrenceManager
import com.pedro.maschio.carcostsmanagement.worker.RecurrenceWorker
import com.pedro.maschio.carcostsmanagement.worker.RescheduleWorker
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<CarCostsRepository> { CarCostsRepositoryImpl(androidApplication(), get(), get()) }
    viewModelOf(::MainScreenViewModel)
    viewModelOf(::CarsScreenViewModel)
    viewModelOf(::IntroViewModel)
    viewModelOf(::AppViewModel)
    viewModelOf(::LoginViewModel)

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "car_costs_db")
            .fallbackToDestructiveMigration(true).build() // TODO: remove destructive later
    }

    single { get<AppDatabase>().carCostDao() }
    single { get<AppDatabase>().carDao() }

    single { NotificationHelper(androidContext()) }
    single { WorkManager.getInstance(androidContext()) }
    single { RecurrenceManager(get()) }

    workerOf(::RecurrenceWorker)
    workerOf(::RescheduleWorker)


//    single {
//        HttpLoggingInterceptor().apply {
//            level = HttpLoggingInterceptor.Level.BODY
//        }
//    }
//
//    single {
//        OkHttpClient.Builder()
//            .addInterceptor(get<HttpLoggingInterceptor>())
//            .build()
//    }
//
//    single {
//        Retrofit.Builder()
//            .baseUrl("https://10.0.2.2:8080/")
//            .client(get())
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//
//    single {
//        get<Retrofit>().create(CarCostsApi::class.java)
//    }
}
