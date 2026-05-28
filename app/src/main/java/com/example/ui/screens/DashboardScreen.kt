package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.MonthEntity
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: MainViewModel, navController: NavController) {
    val months by viewModel.months.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    val currentMonthIndex = viewModel.getCurrentMonthIndex()
    val currentMonth = months.find { it.monthIndex == currentMonthIndex } ?: MonthEntity(monthIndex = currentMonthIndex)

    val salesProgress = if (currentMonth.salesTarget > 0) 
        (currentMonth.salesActual / currentMonth.salesTarget * 100).toFloat()
    else 0f

    val coverageProgress = if (currentMonth.coverageTarget > 0)
        (currentMonth.coverageActual / currentMonth.coverageTarget * 100).toFloat()
    else 0f

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var newTaskText by remember { mutableStateOf("") }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddTaskDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm nhiệm vụ", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("GIAO VIỆC MỚI", fontWeight = FontWeight.Black, style = MaterialTheme.typography.labelLarge)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 32.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    text = "NAM DƯỢC STRATEGY",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Quản lý mục tiêu Tháng ${currentMonthIndex + 1}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    KpiCard(
                        modifier = Modifier.weight(1f),
                        title = "DOANH SỐ",
                        actual = currentMonth.salesActual,
                        target = currentMonth.salesTarget,
                        progress = salesProgress
                    )
                    KpiCard(
                        modifier = Modifier.weight(1f),
                        title = "BAO PHỦ",
                        actual = currentMonth.coverageActual,
                        target = currentMonth.coverageTarget,
                        progress = coverageProgress,
                        isCurrency = false
                    )
                }
            }

            item {
                KpiCard(
                    modifier = Modifier.fillMaxWidth(),
                    title = "SKU FOCUS",
                    actual = currentMonth.skuActual,
                    target = 4.7, // Target giả định
                    progress = if (currentMonth.skuActual > 0) (currentMonth.skuActual / 4.7f * 100).toFloat() else 0f,
                    isCurrency = false
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "BIỂU ĐỒ DOANH SỐ",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Black,
                )
                
                var chartFilter by remember { mutableStateOf(0) }
                val filters = listOf("Năm", "Q1", "Q2", "Q3", "Q4", "6T Đầu", "6T Cuối")
                
                ScrollableTabRow(
                    selectedTabIndex = chartFilter,
                    edgePadding = 0.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    filters.forEachIndexed { index, title ->
                        Tab(
                            selected = chartFilter == index,
                            onClick = { chartFilter = index },
                            text = { Text(title) }
                        )
                    }
                }
                
                val displayMonths = when (chartFilter) {
                    1 -> months.filter { it.monthIndex in 0..2 }
                    2 -> months.filter { it.monthIndex in 3..5 }
                    3 -> months.filter { it.monthIndex in 6..8 }
                    4 -> months.filter { it.monthIndex in 9..11 }
                    5 -> months.filter { it.monthIndex in 0..5 }
                    6 -> months.filter { it.monthIndex in 6..11 }
                    else -> months
                }
                
                val maxSales = (displayMonths.maxOfOrNull { it.salesActual.coerceAtLeast(it.salesTarget) } ?: 100.0).coerceAtLeast(100.0).toFloat()
                
                val primaryColor = MaterialTheme.colorScheme.primary
                val targetColor = MaterialTheme.colorScheme.tertiary
                
                Card(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val barWidth = size.width / (displayMonths.size * 2.5f)
                            val spacing = (size.width - (barWidth * 2 * displayMonths.size)) / Math.max(1, displayMonths.size)
                            
                            displayMonths.forEachIndexed { index, month ->
                                val actualHeight = (month.salesActual.toFloat() / maxSales) * size.height
                                val targetHeight = (month.salesTarget.toFloat() / maxSales) * size.height
                                
                                val startX = (index * (barWidth * 2 + spacing)) + spacing / 2 - 10f
                                
                                // Target Bar
                                drawRoundRect(
                                    color = targetColor.copy(alpha = 0.5f),
                                    topLeft = Offset(startX, size.height - targetHeight),
                                    size = Size(barWidth, targetHeight),
                                    cornerRadius = CornerRadius(4.dp.toPx())
                                )
                                // Actual Bar
                                drawRoundRect(
                                    color = primaryColor,
                                    topLeft = Offset(startX + barWidth, size.height - actualHeight),
                                    size = Size(barWidth, actualHeight),
                                    cornerRadius = CornerRadius(4.dp.toPx())
                                )
                            }
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(12.dp).background(primaryColor, CircleShape))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Thực đạt", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(12.dp).background(targetColor.copy(alpha=0.5f), CircleShape))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mục tiêu", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "HÀNH ĐỘNG HÔM NAY",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Black,
                )
            }

            items(tasks) { task ->
                TaskRow(
                    text = task.text,
                    isCompleted = task.isCompleted,
                    onToggle = { viewModel.toggleTask(task.id, task.isCompleted) }
                )
            }
            
            if (tasks.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Chưa có chiến dịch nào được giao.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    if (showAddTaskDialog) {
        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = { Text("THÊM NHIỆM VỤ") },
            text = {
                OutlinedTextField(
                    value = newTaskText,
                    onValueChange = { newTaskText = it },
                    label = { Text("Nội dung công việc") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newTaskText.isNotBlank()) {
                        viewModel.addTask(newTaskText)
                        newTaskText = ""
                        showAddTaskDialog = false
                    }
                }) {
                    Text("TẠO")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) {
                    Text("HỦY")
                }
            }
        )
    }
}

@Composable
fun KpiCard(
    modifier: Modifier = Modifier,
    title: String,
    actual: Double,
    target: Double,
    progress: Float,
    isCurrency: Boolean = true
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(16.dp))
            val actualFormatted = if (isCurrency) "%,.0f".format(actual) else actual.toString()
            val targetFormatted = if (isCurrency) "%,.0f".format(target) else target.toString()
            Text(
                text = actualFormatted,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (progress / 100f).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "${progress.toInt()}% / $targetFormatted", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}

@Composable
fun TaskRow(text: String, isCompleted: Boolean, onToggle: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        onClick = onToggle,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isCompleted) 0.5f else 1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text, 
                style = MaterialTheme.typography.bodyLarge,
                color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.SemiBold
            )
        }
    }
}
