package com.example.ui.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.model.PayAdjustment
import com.example.model.PayRulesConfig
import com.example.model.PayRun
import com.example.model.PayRunStatus
import com.example.ui.theme.CareMintTeal
import com.example.ui.theme.CareMintTealContainer

@Composable
fun PayRunsAndRulesView(
    repository: CareRepository
) {
    val payRuns by repository.payRuns.collectAsState()
    val payRules by repository.payRules.collectAsState()

    var showPayRulesDialog by remember { mutableStateOf(false) }
    var showAdjustmentDialog by remember { mutableStateOf(false) }
    var selectedPayRunForAdjustment by remember { mutableStateOf<String?>(null) }
    var showAuditReportModal by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Quick Action Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showPayRulesDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTealContainer),
                    modifier = Modifier.weight(1f).testTag("configure_pay_rules_button")
                ) {
                    Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = CareMintTeal, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Config Pay Rules", fontSize = 11.sp, color = CareMintTeal, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { showAuditReportModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.weight(1f).testTag("pay_period_audit_button")
                ) {
                    Icon(imageVector = Icons.Default.Assessment, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pay Audit Report", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Current Active Pay Rules Summary Card
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Active Pay Rules (UK Provider)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                        Text("Statutory NLW: £${payRules.nationalLivingWageRate}/h", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Base Rate: £${payRules.baseHourlyRate}/h", fontSize = 11.sp)
                        Text("Weekend: +£${payRules.weekendUpliftPerHour}/h", fontSize = 11.sp)
                        Text("Night: +£${payRules.nightUpliftPerHour}/h", fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Travel Time: £${payRules.travelTimeRatePerHour}/h", fontSize = 11.sp)
                        Text("Mileage: £${payRules.mileageRatePerMile}/mi (HMRC)", fontSize = 11.sp)
                        Text("Holiday Accrual: ${payRules.holidayAccrualPercentage}%", fontSize = 11.sp, color = Color(0xFF0284C7), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        item {
            Text(
                text = "Pay-Run Periods (${payRuns.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(payRuns) { run ->
            PayRunCard(
                payRun = run,
                onAdvanceStatus = { repository.advancePayRun(run.id) },
                onAddAdjustment = {
                    selectedPayRunForAdjustment = run.id
                    showAdjustmentDialog = true
                }
            )
        }
    }

    // Modal: Pay Rules Config Editor
    if (showPayRulesDialog) {
        var baseRate by remember { mutableStateOf("${payRules.baseHourlyRate}") }
        var weekendUplift by remember { mutableStateOf("${payRules.weekendUpliftPerHour}") }
        var nightUplift by remember { mutableStateOf("${payRules.nightUpliftPerHour}") }
        var bankHolidayMult by remember { mutableStateOf("${payRules.bankHolidayMultiplier}") }
        var travelRate by remember { mutableStateOf("${payRules.travelTimeRatePerHour}") }
        var mileageRate by remember { mutableStateOf("${payRules.mileageRatePerMile}") }
        var sleepInRate by remember { mutableStateOf("${payRules.sleepInFlatRate}") }
        var nlwRate by remember { mutableStateOf("${payRules.nationalLivingWageRate}") }
        var holidayAccrual by remember { mutableStateOf("${payRules.holidayAccrualPercentage}") }

        AlertDialog(
            onDismissRequest = { showPayRulesDialog = false },
            title = { Text("Configure Organisation Pay Rules", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Set statutory minimums, uplift multipliers and expense rates.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = baseRate,
                            onValueChange = { baseRate = it },
                            label = { Text("Base Carer (£/h)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = nlwRate,
                            onValueChange = { nlwRate = it },
                            label = { Text("Statutory NLW (£/h)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = weekendUplift,
                            onValueChange = { weekendUplift = it },
                            label = { Text("Weekend (+£/h)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = nightUplift,
                            onValueChange = { nightUplift = it },
                            label = { Text("Night (+£/h)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = travelRate,
                            onValueChange = { travelRate = it },
                            label = { Text("Travel Pay (£/h)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = mileageRate,
                            onValueChange = { mileageRate = it },
                            label = { Text("Mileage (£/mi)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = sleepInRate,
                            onValueChange = { sleepInRate = it },
                            label = { Text("Sleep-in (£ flat)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = holidayAccrual,
                            onValueChange = { holidayAccrual = it },
                            label = { Text("Holiday Accrual (%)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newConfig = payRules.copy(
                            baseHourlyRate = baseRate.toDoubleOrNull() ?: payRules.baseHourlyRate,
                            weekendUpliftPerHour = weekendUplift.toDoubleOrNull() ?: payRules.weekendUpliftPerHour,
                            nightUpliftPerHour = nightUplift.toDoubleOrNull() ?: payRules.nightUpliftPerHour,
                            bankHolidayMultiplier = bankHolidayMult.toDoubleOrNull() ?: payRules.bankHolidayMultiplier,
                            travelTimeRatePerHour = travelRate.toDoubleOrNull() ?: payRules.travelTimeRatePerHour,
                            mileageRatePerMile = mileageRate.toDoubleOrNull() ?: payRules.mileageRatePerMile,
                            sleepInFlatRate = sleepInRate.toDoubleOrNull() ?: payRules.sleepInFlatRate,
                            nationalLivingWageRate = nlwRate.toDoubleOrNull() ?: payRules.nationalLivingWageRate,
                            holidayAccrualPercentage = holidayAccrual.toDoubleOrNull() ?: payRules.holidayAccrualPercentage
                        )
                        repository.updatePayRules(newConfig)
                        showPayRulesDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                    modifier = Modifier.testTag("save_pay_rules_button")
                ) {
                    Text("Save Pay Rules", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPayRulesDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Modal: Add Pay Adjustment into Period
    if (showAdjustmentDialog && selectedPayRunForAdjustment != null) {
        var adjStaffName by remember { mutableStateOf("James Wilson") }
        var adjStaffId by remember { mutableStateOf("staff-07") }
        var adjAmount by remember { mutableStateOf("12.50") }
        var adjReason by remember { mutableStateOf("NLW travel time back-pay reimbursement") }

        AlertDialog(
            onDismissRequest = { showAdjustmentDialog = false },
            title = { Text("Add Pay Adjustment", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Locked pay runs cannot be edited. Corrections are logged as adjustments with full audit authorisations.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = adjStaffName,
                        onValueChange = { adjStaffName = it },
                        label = { Text("Staff Member") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = adjAmount,
                        onValueChange = { adjAmount = it },
                        label = { Text("Adjustment Amount (£)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = adjReason,
                        onValueChange = { adjReason = it },
                        label = { Text("Reason for Correction / Adjustment") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = adjAmount.toDoubleOrNull() ?: 0.0
                        repository.addPayAdjustment(
                            payRunId = selectedPayRunForAdjustment!!,
                            staffId = adjStaffId,
                            staffName = adjStaffName,
                            amount = amount,
                            reason = adjReason
                        )
                        showAdjustmentDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                    modifier = Modifier.testTag("save_pay_adjustment_button")
                ) {
                    Text("Save Adjustment", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdjustmentDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Modal: Pay Period Audit Report
    if (showAuditReportModal) {
        AlertDialog(
            onDismissRequest = { showAuditReportModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.FactCheck, contentDescription = null, tint = CareMintTeal)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pay-Period Audit Report", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("Full trace of gross pay, travel allowance, and adjustments for compliance inspection.", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    payRuns.forEach { run ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(run.periodName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("Cost: £${"%.2f".format(run.totalPayrollCost)} • Status: ${run.status.displayName}", fontSize = 11.sp, color = CareMintTeal)
                                Text("Approved by: ${run.approvedBy ?: "Pending"}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showAuditReportModal = false }, colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal)) {
                    Text("Done", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun PayRunCard(
    payRun: PayRun,
    onAdvanceStatus: () -> Unit,
    onAddAdjustment: () -> Unit
) {
    val isLocked = payRun.status == PayRunStatus.LOCKED

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isLocked) Color(0xFF6366F1).copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = payRun.periodName, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Dates: ${payRun.startDate} - ${payRun.endDate} • ${payRun.totalStaffCount} Staff",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(payRun.status.colorHex).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = payRun.status.displayName,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(payRun.status.colorHex),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Cost Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Gross Pay", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("£${"%.2f".format(payRun.grossPayTotal)}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Travel & Mileage", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("£${"%.2f".format(payRun.travelPayTotal + payRun.mileagePayTotal)}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Holiday (12.07%)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("£${"%.2f".format(payRun.holidayAccruedTotal)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                }
                Column {
                    Text("Total Cost", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("£${"%.2f".format(payRun.totalPayrollCost)}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = CareMintTeal)
                }
            }

            // Adjustments Section if any
            if (payRun.adjustments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(6.dp))
                Text("Period Adjustments / Corrections (${payRun.adjustments.size}):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                payRun.adjustments.forEach { adj ->
                    Text(
                        text = "• ${adj.staffName}: +£${"%.2f".format(adj.amount)} (${adj.reason}) - Auth: ${adj.authorisedBy}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Workflow Step Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isLocked) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFF6366F1), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Locked at ${payRun.lockedAt ?: "Closed"}", fontSize = 11.sp, color = Color(0xFF6366F1), fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    OutlinedButton(
                        onClick = onAddAdjustment,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("+ Add Adjustment", fontSize = 10.sp)
                    }
                }

                if (!isLocked) {
                    val nextActionLabel = when (payRun.status) {
                        PayRunStatus.DRAFT -> "Submit for Review →"
                        PayRunStatus.MANAGER_REVIEW -> "Approve (Finance) →"
                        PayRunStatus.APPROVED -> "Export to CSV →"
                        PayRunStatus.EXPORTED -> "Lock Pay-Run 🔒"
                        PayRunStatus.LOCKED -> "Locked"
                    }

                    Button(
                        onClick = onAdvanceStatus,
                        colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp).testTag("advance_payrun_${payRun.id}")
                    ) {
                        Text(nextActionLabel, color = Color(0xFF0A1128), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
