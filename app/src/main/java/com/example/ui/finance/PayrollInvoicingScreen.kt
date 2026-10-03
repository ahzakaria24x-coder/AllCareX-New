package com.example.ui.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CareRepository
import com.example.ui.theme.CareMintTeal
import com.example.ui.theme.CareMintTealContainer

@Composable
fun PayrollInvoicingScreen(
    repository: CareRepository
) {
    val timesheets by repository.timesheets.collectAsState()
    val invoices by repository.invoices.collectAsState()
    val payRuns by repository.payRuns.collectAsState()

    var activeSubTab by remember { mutableStateOf(0) }
    // 0: Timesheets & NLW
    // 1: Pay-Runs & Rules
    // 2: Holiday & Sickness
    // 3: Client Invoicing & Ledger
    // 4: Finance Dashboard

    val totalGrossPayroll = timesheets.sumOf { it.totalPayable }
    val totalHolidayAccrued = timesheets.sumOf { it.holidayPayAccrued }
    val totalTravelPay = timesheets.sumOf { it.travelPay }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Payroll Prep & Invoicing",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "GPS visit timesheets, NLW compliance, travel pay & split funding",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // High-level quick stats banner
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("GROSS PAYABLE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("£${"%.2f".format(totalGrossPayroll)}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = CareMintTeal)
                }
                Column {
                    Text("HMRC TRAVEL (£0.45/mi)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("£${"%.2f".format(totalTravelPay)}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("HOLIDAY ACCRUAL (12.07%)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("£${"%.2f".format(totalHolidayAccrued)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sub Tabs
        ScrollableTabRow(
            selectedTabIndex = activeSubTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = CareMintTeal,
            edgePadding = 0.dp,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
        ) {
            Tab(
                selected = activeSubTab == 0,
                onClick = { activeSubTab = 0 },
                text = { Text("Timesheets & NLW (${timesheets.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeSubTab == 1,
                onClick = { activeSubTab = 1 },
                text = { Text("Pay Runs & Rules (${payRuns.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeSubTab == 2,
                onClick = { activeSubTab = 2 },
                text = { Text("Holiday & Sickness", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeSubTab == 3,
                onClick = { activeSubTab = 3 },
                text = { Text("Client Invoices (${invoices.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeSubTab == 4,
                onClick = { activeSubTab = 4 },
                text = { Text("Finance Dashboard", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (activeSubTab) {
            0 -> PayrollTimesheetsView(repository = repository)
            1 -> PayRunsAndRulesView(repository = repository)
            2 -> HolidaySicknessView(repository = repository)
            3 -> ClientInvoicingView(repository = repository)
            4 -> FinanceDashboardView(repository = repository)
        }
    }
}
