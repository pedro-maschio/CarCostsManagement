package com.pedro.maschio.carcostsmanagement.data.network.models

import kotlinx.serialization.Serializable

@Serializable
data class CarResponse(val id: Int, val name: String)

