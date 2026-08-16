package com.pedro.maschio.carcostsmanagement.data.database.entities

enum class CostType(val value: Int) {
    GAS(0),
    MAINTENANCE(1),
    OTHERS(2);

    companion object {
        fun fromInt(value: Int) = entries.find { it.value == value } ?: GAS
    }
}
