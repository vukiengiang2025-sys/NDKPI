package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.KpiRepository
import com.example.data.BackupData
import com.example.data.MonthEntity
import com.example.data.SettingsManager
import com.example.network.Content
import com.example.network.GenerateContentRequest
import com.example.network.Part
import com.example.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.util.Calendar

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val settingsManager = SettingsManager(application)
    private val database = AppDatabase.getDatabase(application)
    private val repository = KpiRepository(database.kpiDao())

    val months: StateFlow<List<MonthEntity>> = repository.allMonths.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val tasks = repository.allTasks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _chatMessages = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val chatMessages = _chatMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking = _isAiThinking.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allMonths.collect { currentMonths ->
                if (currentMonths.isEmpty()) {
                    repository.initializeMonthsIfEmpty(currentMonths)
                }
            }
        }
    }

    fun updateMonthSales(month: MonthEntity, target: Double, actual: Double) {
        viewModelScope.launch {
            repository.updateMonth(month.copy(salesTarget = target, salesActual = actual))
        }
    }

    fun updateMonthCoverage(month: MonthEntity, target: Double, actual: Double) {
        viewModelScope.launch {
            repository.updateMonth(month.copy(coverageTarget = target, coverageActual = actual))
        }
    }

    fun updateMonthSku(month: MonthEntity, actual: Double) {
        viewModelScope.launch {
            repository.updateMonth(month.copy(skuActual = actual))
        }
    }

    fun updateMonthN(month: MonthEntity, n1: Double, n2: Double, n3: Double) {
        viewModelScope.launch {
            repository.updateMonth(month.copy(n1Sales = n1, n2Sales = n2, n3Sales = n3))
        }
    }

    fun addTask(text: String, priority: String = "medium", type: String = "other") {
        viewModelScope.launch {
            repository.addTask(text, priority, type)
        }
    }

    fun toggleTask(id: String, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleTaskComplete(id, isCompleted)
        }
    }

    fun getCurrentMonthIndex(): Int {
        return Calendar.getInstance().get(Calendar.MONTH)
    }

    fun sendAiMessage(message: String) {
        val apiKey = settingsManager.geminiApiKey
        if (apiKey.isBlank()) {
            _chatMessages.value = _chatMessages.value + Pair("AI", "Vui lòng nhập API Key trong phần Cài đặt trước khi chat.")
            return
        }

        _chatMessages.value = _chatMessages.value + Pair("User", message)
        _isAiThinking.value = true

        viewModelScope.launch {
            try {
                // Tạo context hiện tại từ KPI
                val currentMonthScore = months.value.find { it.monthIndex == getCurrentMonthIndex() }
                val context = currentMonthScore?.let {
                    "Thông tin KPI tháng ${it.monthIndex + 1}: Mục tiêu DS ${it.salesTarget}, Thực đạt DS ${it.salesActual}. Mục tiêu BP ${it.coverageTarget}, Thực đạt BP ${it.coverageActual}. SKU Focus ${it.skuActual}."
                } ?: "Chưa có thông tin KPI tháng hiện tại."

                val prompt = "Tôi là ${settingsManager.userName}. Bạn đóng vai trò là một cố vấn chiến lược của Nam Dược. Ngữ cảnh: $context. Dựa vào đó hãy trả lời câu hỏi: $message"

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt))))
                )

                val response = RetrofitClient.service.generateContent(settingsManager.selectedModel, apiKey, request)
                val reply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Không có phản hồi từ AI."
                
                _chatMessages.value = _chatMessages.value + Pair("AI", reply.trim())
            } catch (e: Exception) {
                _chatMessages.value = _chatMessages.value + Pair("AI", "Lỗi: ${e.message}")
            } finally {
                _isAiThinking.value = false
            }
        }
    }

    fun exportBackupData(): String {
        val backup = BackupData(months = months.value, tasks = tasks.value)
        return Json.encodeToString(backup)
    }

    fun importBackupData(jsonString: String): Boolean {
        return try {
            val backup = Json.decodeFromString<BackupData>(jsonString)
            viewModelScope.launch {
                backup.months.forEach { repository.updateMonth(it) }
                backup.tasks.forEach { repository.insertTask(it) }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
