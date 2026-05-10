package com.pedro.maschio.carcostsmanagement.data.network

import com.pedro.maschio.carcostsmanagement.data.network.models.CarResponse
import retrofit2.http.GET


interface CarCostsApi {

    @GET(CARS)
    fun getCars(): List<CarResponse>

    private companion object {
        const val CARS = "/cars"
    }
}