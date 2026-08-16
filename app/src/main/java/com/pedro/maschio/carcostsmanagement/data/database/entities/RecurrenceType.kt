package com.pedro.maschio.carcostsmanagement.data.database.entities

enum class RecurrenceType(val value: Int) {
    NONE(0),
    MONTHLY(1),
    YEARLY(2);

    companion object {
        fun fromInt(value: Int) = entries.find { it.value == value } ?: NONE
    }
}
