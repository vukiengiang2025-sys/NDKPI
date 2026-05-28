package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ui.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel, navController: NavController) {
    var apiKey by remember { mutableStateOf(viewModel.settingsManager.geminiApiKey) }
    var userName by remember { mutableStateOf(viewModel.settingsManager.userName) }
    var selectedModel by remember { mutableStateOf(viewModel.settingsManager.selectedModel) }
    
    val context = LocalContext.current
    var restoreData by remember { mutableStateOf("") }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Cài đặt Hệ thống", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Thông tin Người dùng",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Tên người dùng") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                var reminderEnabled by remember { mutableStateOf(viewModel.settingsManager.isReminderEnabled) }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Nhắc nhở công việc hàng ngày", fontWeight = FontWeight.Bold)
                        Text("Gửi thông báo nhắc nhở cập nhật KPI và hoàn thành nhiệm vụ.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = { 
                            reminderEnabled = it
                            viewModel.settingsManager.isReminderEnabled = it
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Cấu hình AI",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                var expanded by remember { mutableStateOf(false) }
                val models = listOf("gemini-3.1-pro-preview", "gemini-2.0-flash", "gemini-1.5-pro", "gemini-1.5-flash")
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedModel,
                        onValueChange = { },
                        label = { Text("AI Model") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        models.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    selectedModel = selectionOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("Gemini API Key") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                Text(
                    "API Key được lưu trữ cục bộ trên thiết bị của bạn để đảm bảo an toàn.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Sao lưu & Phục hồi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val data = viewModel.exportBackupData()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("KPI Backup", data))
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Đã sao chép dữ liệu sao lưu vào khay nhớ tạm!")
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("SAO LƯU")
                    }
                    OutlinedButton(
                        onClick = { showRestoreDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("PHỤC HỒI")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.settingsManager.geminiApiKey = apiKey
                        viewModel.settingsManager.userName = userName
                        viewModel.settingsManager.selectedModel = selectedModel
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Đã lưu cấu hình!")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("LƯU CÀI ĐẶT", modifier = Modifier.padding(8.dp), fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("Phục hồi dữ liệu") },
            text = {
                OutlinedTextField(
                    value = restoreData,
                    onValueChange = { restoreData = it },
                    label = { Text("Dán dữ liệu sao lưu vào đây") },
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    maxLines = 10
                )
            },
            confirmButton = {
                Button(onClick = {
                    val success = viewModel.importBackupData(restoreData)
                    coroutineScope.launch {
                        if (success) snackbarHostState.showSnackbar("Phục hồi dữ liệu thành công!")
                        else snackbarHostState.showSnackbar("Dữ liệu không hợp lệ!")
                    }
                    showRestoreDialog = false
                    restoreData = ""
                }) {
                    Text("PHỤC HỒI")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("HỦY")
                }
            }
        )
    }
}
