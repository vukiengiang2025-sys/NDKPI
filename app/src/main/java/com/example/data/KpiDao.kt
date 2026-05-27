package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface KpiDao {
    @Query("SELECT * FROM months")
    fun getAllMonths(): Flow<List<MonthEntity>>

    @Query("SELECT * FROM months WHERE monthIndex = :index")
    fun getMonth(index: Int): Flow<MonthEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonth(month: MonthEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonths(months: List<MonthEntity>)

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateTaskStatus(id: String, isCompleted: Boolean)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: String)
}
