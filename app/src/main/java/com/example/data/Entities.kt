package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "months")
data class MonthEntity(
    @PrimaryKey val monthIndex: Int,
    val salesTarget: Double = 0.0,
    val salesActual: Double = 0.0,
    val coverageTarget: Double = 0.0,
    val coverageActual: Double = 0.0,
    val skuActual: Double = 0.0,
    val n1Sales: Double = 0.0,
    val n2Sales: Double = 0.0,
    val n3Sales: Double = 0.0
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val text: String,
    val target: Int = 0,
    val done: Int = 0,
    val isCompleted: Boolean = false,
    val priority: String = "medium", // high, medium, low
    val type: String = "other", // coverage, sales, sku, other
    val createdAt: Long = System.currentTimeMillis()
)
