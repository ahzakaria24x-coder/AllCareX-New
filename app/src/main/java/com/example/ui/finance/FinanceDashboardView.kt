package com.example.ui.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.model.ClientInvoice
import com.example.ui.theme.CareMintTeal
import com.example.ui.theme.CareMintTealContainer

@Composable
fun FinanceDashboardView(
    repository: CareRepository
) {
    val invoices by repository.invoices.collectAsState()
    val timesheets by repository.timesheets.collectAsState()

    val totalGrossRevenue = invoices.sumOf { it.grossTotal }
    val totalOutstanding = invoices.sumOf { it.outstandingBalance }
    val totalCashCollected = totalGrossRevenue - totalOutstanding
    val totalPayrollCost = timesheets.sumOf { it.totalPayable }

    val bucket0To30 = invoices.filter { it.agingBucket == "0-30 days" }.sumOf { it.outstandingBalance }
    val bucket31To60 = invoices.filter { it.agingBucket == "31-60 days" }.sumOf { it.outstandingBalance }
    val bucket61Plus = invoices.filter { it.agingBucket == "61+ days" }.sumOf { it.outstandingBalance }

    val overallProfit = totalGrossRevenue - totalPayrollCost
    val overallMarginPercent = if (totalGrossRevenue > 0) (overallProfit / totalGrossRevenue) * 100.0 else 0.0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // High-level KPI Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KpiMetricCard(
                    title = "BILLED REVENUE",
                    value = "£${"%.2f".format(totalGrossRevenue)}",
                    accentColor = CareMintTeal,
                    modifier = Modifier.weight(1f)
                )
                KpiMetricCard(
                    title = "CASH COLLECTED",
                    value = "£${"%.2f".format(totalCashCollected)}",
                    accentColor = Color(0xFF10B981),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KpiMetricCard(
                    title = "TOTAL OUTSTANDING",
                    value = "£${"%.2f".format(totalOutstanding)}",
                    accentColor = Color(0xFFF59E0B),
                    modifier = Modifier.weight(1f)
                )
                KpiMetricCard(
                    title = "PAYROLL VS REVENUE",
                    value = "${"%.1f".format((totalPayrollCost / if (totalGrossRevenue > 0) totalGrossRevenue else 1.0) * 100.0)}%",
                    accentColor = Color(0xFF6366F1),
                    subtitle = "Payroll: £${"%.2f".format(totalPayrollCost)}",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Overdue Ageing Buckets Card
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth().testTag("debtor_aging_buckets")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Debtor Ageing Buckets (NHS & Local Authority)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CareMintTeal
                        )
                        Text(
                            text = "Total Debt: £${"%.2f".format(totalOutstanding)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Current (0-30 Days)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("£${"%.2f".format(bucket0To30)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                        }
                        Column {
                            Text("31-60 Days", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("£${"%.2f".format(bucket31To60)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                        }
                        Column {
                            Text("61+ Days (Overdue)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("£${"%.2f".format(bucket61Plus)}", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFEF4444))
                        }
                    }
                }
            }
        }

        // Margin Per Client Breakdown
        item {
            Text(
                text = "Client Profitability & Gross Margin (Income - Carer Wage Cost)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(invoices) { inv ->
            ClientMarginCard(invoice = inv)
        }
    }
}

@Composable
private fun KpiMetricCard(
    title: String,
    value: String,
    accentColor: Color,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = accentColor)
            if (subtitle != null) {
                Text(text = subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ClientMarginCard(invoice: ClientInvoice) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = invoice.clientName, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "Billed: £${"%.2f".format(invoice.grossTotal)}  |  Carer Wages: £${"%.2f".format(invoice.carerWageCostTotal)}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Margin: £${"%.2f".format(invoice.grossMarginPounds)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CareMintTeal
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = CareMintTealContainer
                ) {
                    Text(
                        text = "${"%.1f".format(invoice.grossMarginPercent)}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CareMintTeal,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
