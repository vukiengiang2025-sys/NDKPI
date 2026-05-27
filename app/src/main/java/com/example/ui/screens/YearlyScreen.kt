package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.MonthEntity
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearlyScreen(viewModel: MainViewModel, navController: NavController) {
    val months by viewModel.months.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mục tiêu năm", fontWeight = FontWeight.Bold) },
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
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(months) { month ->
                MonthEntryCard(month = month, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MonthEntryCard(month: MonthEntity, viewModel: MainViewModel) {
    var salesTargetStr by remember(month.salesTarget) { mutableStateOf(if (month.salesTarget > 0) month.salesTarget.toString() else "") }
    var salesActualStr by remember(month.salesActual) { mutableStateOf(if (month.salesActual > 0) month.salesActual.toString() else "") }
    var covTargetStr by remember(month.coverageTarget) { mutableStateOf(if (month.coverageTarget > 0) month.coverageTarget.toString() else "") }
    var covActualStr by remember(month.coverageActual) { mutableStateOf(if (month.coverageActual > 0) month.coverageActual.toString() else "") }
    var skuActualStr by remember(month.skuActual) { mutableStateOf(if (month.skuActual > 0) month.skuActual.toString() else "") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "THÁNG ${month.monthIndex + 1}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Text("Doanh số", style = MaterialTheme.typography.labelMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = salesTargetStr,
                    onValueChange = { 
                        salesTargetStr = it
                        viewModel.updateMonthSales(month, it.toDoubleOrNull() ?: 0.0, month.salesActual)
                    },
                    label = { Text("Khoán (K)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = salesActualStr,
                    onValueChange = { 
                        salesActualStr = it
                        viewModel.updateMonthSales(month, month.salesTarget, it.toDoubleOrNull() ?: 0.0)
                    },
                    label = { Text("Đạt (K)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            Text("Bao phủ", style = MaterialTheme.typography.labelMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = covTargetStr,
                    onValueChange = { 
                        covTargetStr = it
                        viewModel.updateMonthCoverage(month, it.toDoubleOrNull() ?: 0.0, month.coverageActual)
                    },
                    label = { Text("Khoán (NT)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = covActualStr,
                    onValueChange = { 
                        covActualStr = it
                        viewModel.updateMonthCoverage(month, month.coverageTarget, it.toDoubleOrNull() ?: 0.0)
                    },
                    label = { Text("Đạt (NT)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = skuActualStr,
                    onValueChange = { 
                        skuActualStr = it
                        viewModel.updateMonthSku(month, it.toDoubleOrNull() ?: 0.0)
                    },
                    label = { Text("SKU Focus Thực đạt") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
