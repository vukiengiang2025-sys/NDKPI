package com.example.data

import kotlinx.coroutines.flow.Flow
import java.util.UUID

class KpiRepository(private val dao: KpiDao) {
    val allMonths: Flow<List<MonthEntity>> = dao.getAllMonths()
    val allTasks: Flow<List<TaskEntity>> = dao.getAllTasks()

    fun getMonth(index: Int): Flow<MonthEntity?> = dao.getMonth(index)

    suspend fun initializeMonthsIfEmpty(existingMonths: List<MonthEntity>) {
        if (existingMonths.isEmpty()) {
            val defaultMonths = (0..11).map { index ->
                MonthEntity(monthIndex = index)
            }
            dao.insertMonths(defaultMonths)
        }
    }

    suspend fun updateMonth(month: MonthEntity) {
        dao.insertMonth(month)
    }

    suspend fun addTask(text: String, priority: String, type: String) {
        val task = TaskEntity(
            id = UUID.randomUUID().toString(),
            text = text,
            priority = priority,
            type = type
        )
        dao.insertTask(task)
    }

    suspend fun toggleTaskComplete(id: String, isCompleted: Boolean) {
        dao.updateTaskStatus(id, !isCompleted)
    }

    suspend fun deleteTask(id: String) {
        dao.deleteTaskById(id)
    }
}
