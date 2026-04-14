package com.pedro.maschio.carcostsmanagement.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "carCosts",
    foreignKeys = [ForeignKey(
        entity = Car::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("carId"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("carId")]
)
data class CarCost(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "createdAt") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updatedAt") val updatedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "price") val price: Double,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "carId") val carId: Long = -1,
)
