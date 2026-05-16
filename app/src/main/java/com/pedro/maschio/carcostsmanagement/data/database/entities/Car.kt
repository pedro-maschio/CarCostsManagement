package com.pedro.maschio.carcostsmanagement.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cars")
data class Car(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "createdAt") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updatedAt") val updatedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "mileage", defaultValue = "0") val mileage: Int = 0,
    @ColumnInfo(name = "lastOilChangeMileage", defaultValue = "0") val lastOilChangeMileage: Int = 0,
    @ColumnInfo(name = "oilChangeInterval", defaultValue = "10000") val oilChangeInterval: Int = 10000
)
