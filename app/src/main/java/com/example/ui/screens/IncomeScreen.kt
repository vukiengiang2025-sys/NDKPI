package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.MonthEntity
import com.example.lib.KpiCalculator
import com.example.ui.MainViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeScreen(viewModel: MainViewModel, navController: NavController) {
    val months by viewModel.months.collectAsStateWithLifecycle()
    val currentMonthIndex = viewModel.getCurrentMonthIndex()
    val month = months.find { it.monthIndex == currentMonthIndex } ?: MonthEntity(monthIndex = currentMonthIndex)

    var n1Str by remember(month.n1Sales) { mutableStateOf(if (month.n1Sales > 0) month.n1Sales.toString() else "") }
    var n2Str by remember(month.n2Sales) { mutableStateOf(if (month.n2Sales > 0) month.n2Sales.toString() else "") }
    var n3Str by remember(month.n3Sales) { mutableStateOf(if (month.n3Sales > 0) month.n3Sales.toString() else "") }

    val kpiPoints = KpiCalculator.calcTotalKPI(month.salesActual, month.salesTarget, month.coverageActual, month.coverageTarget, month.skuActual)
    val totalIncome = KpiCalculator.calculateBonus(month.n1Sales, month.n2Sales, month.n3Sales, kpiPoints)

    val formatter = NumberFormat.getNumberInstance(Locale.Builder().setLanguage("vi").setRegion("VN").build())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tính thu nhập (Tháng ${currentMonthIndex + 1})", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("ĐIỂM KPI HIỆN TẠI", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(
                        "${String.format("%.1f", kpiPoints)} điểm",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("THU NHẬP DỰ KIẾN (VNĐ)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(
                        "${formatter.format(totalIncome)} ₫",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text("Nhập doanh số nhóm sản phẩm (VNĐ)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            
            OutlinedTextField(
                value = n1Str,
                onValueChange = { 
                    n1Str = it
                    viewModel.updateMonthN(month, it.toDoubleOrNull() ?: 0.0, month.n2Sales, month.n3Sales)
                },
                label = { Text("Nhóm N1 (3%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = n2Str,
                onValueChange = { 
                    n2Str = it
                    viewModel.updateMonthN(month, month.n1Sales, it.toDoubleOrNull() ?: 0.0, month.n3Sales)
                },
                label = { Text("Nhóm N2 (5%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = n3Str,
                onValueChange = { 
                    n3Str = it
                    viewModel.updateMonthN(month, month.n1Sales, month.n2Sales, it.toDoubleOrNull() ?: 0.0)
                },
                label = { Text("Nhóm N3 (8%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Quy tắc tính thu nhập", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Thu nhập = (N1x3% + N2x5% + N3x8%) x Hệ số KPI + Thưởng thêm", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Hệ số KPI:\n- Dưới 70: 0\n- Từ 70 - 79: 0.5\n- Từ 80 - 150: Từ 0.8 đến 1.5", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Thưởng thêm:\n- Hệ số 1.2: +500.000đ\n- Hệ số 1.3 - 1.4: +1.000.000đ\n- Hệ số 1.5: +1.500.000đ", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
