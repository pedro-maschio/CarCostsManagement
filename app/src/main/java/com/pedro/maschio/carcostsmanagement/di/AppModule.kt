package com.pedro.maschio.carcostsmanagement.di

import androidx.room.Room
import com.pedro.maschio.carcostsmanagement.data.database.AppDatabase
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepository
import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepositoryImpl
import com.pedro.maschio.carcostsmanagement.ui.AppViewModel
import com.pedro.maschio.carcostsmanagement.ui.screens.cars.CarsScreenViewModel
import com.pedro.maschio.carcostsmanagement.ui.screens.intro.IntroViewModel
import com.pedro.maschio.carcostsmanagement.ui.screens.main.MainScreenViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<CarCostsRepository> { CarCostsRepositoryImpl(androidApplication(), get(), get()) }
    viewModelOf(::MainScreenViewModel)
    viewModelOf(::CarsScreenViewModel)
    viewModelOf(::IntroViewModel)
    viewModelOf(::AppViewModel)

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "car_costs_db")
            .fallbackToDestructiveMigration(true).build() // TODO: remove destructive later
    }

    single { get<AppDatabase>().carCostDao() }
    single { get<AppDatabase>().carDao() }
}
