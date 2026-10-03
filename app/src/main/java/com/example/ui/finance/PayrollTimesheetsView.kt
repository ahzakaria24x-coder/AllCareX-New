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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CareRepository
import com.example.model.EntryType
import com.example.model.TimesheetEntry
import com.example.ui.theme.CareMintTeal
import com.example.ui.theme.CareMintTealContainer

@Composable
fun PayrollTimesheetsView(
    repository: CareRepository
) {
    val timesheets by repository.timesheets.collectAsState()
    val payRules by repository.payRules.collectAsState()

    var showManualEntryDialog by remember { mutableStateOf(false) }
    var showExportSuccess by remember { mutableStateOf(false) }
    var filterType by remember { mutableStateOf<EntryType?>(null) }
    var selectedDisputeItem by remember { mutableStateOf<TimesheetEntry?>(null) }
    var disputeResolutionText by remember { mutableStateOf("") }

    val filteredTimesheets = timesheets.filter { filterType == null || it.entryType == filterType }
    val nlwShortfallItems = timesheets.filter { !it.isNlwCompliant || it.nlwShortfall > 0.0 }
    val disputedCount = timesheets.count { it.isDisputed }

    Column(modifier = Modifier.fillMaxSize()) {
        // NLW & Minimum Wage Warning Banner
        if (nlwShortfallItems.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFEF4444).copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .testTag("nlw_shortfall_warning_banner")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "NLW Shortfall Alert",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "NATIONAL LIVING WAGE SHORTFALL DETECTED (${nlwShortfallItems.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFEF4444)
                        )
                        nlwShortfallItems.forEach { item ->
                            Text(
                                text = "• ${item.staffName}: £${"%.2f".format(item.effectiveHourlyRate)}/hr effective rate (Shortfall: £${"%.2f".format(item.nlwShortfall)}/hr). Cause: ${item.shortfallReason ?: "Unpaid travel time"}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { repository.generateTimesheetsFromDeliveredVisits() },
                colors = ButtonDefaults.buttonColors(containerColor = CareMintTealContainer),
                modifier = Modifier.weight(1f).testTag("sync_gps_timesheets_button")
            ) {
                Icon(imageVector = Icons.Default.GpsFixed, contentDescription = null, tint = CareMintTeal, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Sync GPS Times", fontSize = 11.sp, color = CareMintTeal, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { showManualEntryDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.weight(1f).testTag("add_manual_entry_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("+ Manual Entry", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { showExportSuccess = true },
                colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                modifier = Modifier.weight(1f).testTag("export_payroll_csv_button")
            ) {
                Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = Color(0xFF0A1128), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export CSV", fontSize = 11.sp, color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
            }
        }

        if (showExportSuccess) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = CareMintTealContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = CareMintTeal, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Payroll CSV Export Generated: Includes gross pay, HMRC £0.45/mi mileage, paid travel time & 12.07% holiday accrual ready for Xero / Sage 50.",
                        fontSize = 11.sp,
                        color = CareMintTeal,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filters Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Filter:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            FilterChip(
                selected = filterType == null,
                onClick = { filterType = null },
                label = { Text("All (${timesheets.size})", fontSize = 10.sp) }
            )
            FilterChip(
                selected = filterType == EntryType.VISIT,
                onClick = { filterType = EntryType.VISIT },
                label = { Text("Visits", fontSize = 10.sp) }
            )
            FilterChip(
                selected = filterType == EntryType.TRAINING,
                onClick = { filterType = EntryType.TRAINING },
                label = { Text("Training", fontSize = 10.sp) }
            )
            FilterChip(
                selected = filterType == EntryType.SLEEP_IN,
                onClick = { filterType = EntryType.SLEEP_IN },
                label = { Text("Sleep-In", fontSize = 10.sp) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Timesheet Items List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredTimesheets) { entry ->
                TimesheetDetailCard(
                    entry = entry,
                    onOpenDispute = { selectedDisputeItem = entry }
                )
            }
        }
    }

    // Modal: Manual Entry (Training, Shadowing, Office Hours, Sleep-ins)
    if (showManualEntryDialog) {
        var selectedType by remember { mutableStateOf(EntryType.TRAINING) }
        var staffNameInput by remember { mutableStateOf("David Okonkwo") }
        var staffIdInput by remember { mutableStateOf("staff-04") }
        var descriptionInput by remember { mutableStateOf("Manual Moving & Handling Refresher") }
        var dateInput by remember { mutableStateOf("03/10/2026") }
        var hoursInput by remember { mutableStateOf("2.0") }
        var rateInput by remember { mutableStateOf("${payRules.trainingHourlyRate}") }

        AlertDialog(
            onDismissRequest = { showManualEntryDialog = false },
            title = { Text("Add Manual Timesheet Entry", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Record non-visit payable hours (training, shadowing, sleep-ins, office administration).", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(EntryType.TRAINING, EntryType.SHADOWING, EntryType.SLEEP_IN, EntryType.OFFICE_HOURS).forEach { type ->
                            FilterChip(
                                selected = selectedType == type,
                                onClick = {
                                    selectedType = type
                                    rateInput = when (type) {
                                        EntryType.TRAINING -> "${payRules.trainingHourlyRate}"
                                        EntryType.SHADOWING -> "${payRules.baseHourlyRate}"
                                        EntryType.SLEEP_IN -> "6.88"
                                        EntryType.OFFICE_HOURS -> "${payRules.baseHourlyRate}"
                                        else -> "${payRules.baseHourlyRate}"
                                    }
                                },
                                label = { Text(type.displayName.split(" ").first(), fontSize = 10.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = staffNameInput,
                        onValueChange = { staffNameInput = it },
                        label = { Text("Staff Member Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = descriptionInput,
                        onValueChange = { descriptionInput = it },
                        label = { Text("Activity / Client / Topic") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = hoursInput,
                            onValueChange = { hoursInput = it },
                            label = { Text("Hours") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = rateInput,
                            onValueChange = { rateInput = it },
                            label = { Text("Rate (£/h)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val hrs = hoursInput.toDoubleOrNull() ?: 1.0
                        val rate = rateInput.toDoubleOrNull() ?: payRules.baseHourlyRate
                        repository.addManualTimesheetEntry(
                            staffId = staffIdInput,
                            staffName = staffNameInput,
                            entryType = selectedType,
                            clientOrEventName = descriptionInput,
                            date = dateInput,
                            hours = hrs,
                            hourlyRate = rate
                        )
                        showManualEntryDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                    modifier = Modifier.testTag("submit_manual_timesheet_button")
                ) {
                    Text("Add to Timesheet", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualEntryDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Modal: Dispute View / Resolution
    if (selectedDisputeItem != null) {
        val item = selectedDisputeItem!!
        AlertDialog(
            onDismissRequest = { selectedDisputeItem = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Timesheet Dispute Details", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(text = "Staff: ${item.staffName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = "Activity: ${item.clientName} (${item.date})", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFEF4444).copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("DISPUTE REASON:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                            Text(text = item.disputeReason ?: "Carer disputed recorded visit/travel time", fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Coordinator / Manager Adjustment Resolution:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = disputeResolutionText,
                        onValueChange = { disputeResolutionText = it },
                        placeholder = { Text("e.g. Approved 30 min unpaid travel back-pay") },
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // In demo, add adjustment into current pay run
                        val payRuns = repository.payRuns.value
                        val activeRun = payRuns.firstOrNull()
                        if (activeRun != null) {
                            repository.addPayAdjustment(
                                payRunId = activeRun.id,
                                staffId = item.staffId,
                                staffName = item.staffName,
                                amount = 12.50,
                                reason = disputeResolutionText.ifBlank { "Dispute resolution adjustment" }
                            )
                        }
                        selectedDisputeItem = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal)
                ) {
                    Text("Resolve & Add Adjustment", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedDisputeItem = null }) { Text("Close") }
            }
        )
    }
}

@Composable
private fun TimesheetDetailCard(
    entry: TimesheetEntry,
    onOpenDispute: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (entry.isDisputed) Color(0xFFEF4444)
            else if (!entry.isNlwCompliant) Color(0xFFF59E0B)
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(text = entry.staffName, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${entry.entryType.displayName} • ${entry.clientName} • ${entry.date}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = CareMintTealContainer
                ) {
                    Text(
                        text = "Total: £${"%.2f".format(entry.totalPayable)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CareMintTeal,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Planned vs Actual Comparison
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Planned: ${entry.scheduledHours}h  |  Actual: ${entry.actualDeliveredHours}h",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (entry.differenceMinutes != 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFF59E0B).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "${if (entry.differenceMinutes > 0) "+" else ""}${entry.differenceMinutes}m diff",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF59E0B),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "Base: £${entry.hourlyPayRate}/h (£${"%.2f".format(entry.grossPay)})",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Travel and Mileage Breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Travel: ${entry.travelTimeMinutes}m (£${"%.2f".format(entry.travelPay)}) + Mileage ${entry.mileageMiles}mi (£${"%.2f".format(entry.mileagePay)})",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Holiday (12.07%): £${"%.2f".format(entry.holidayPayAccrued)}",
                    fontSize = 10.sp,
                    color = Color(0xFF0284C7),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Compliance & Dispute Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!entry.isNlwCompliant) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFEF4444).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "⚠ Below NLW (£${"%.2f".format(entry.effectiveHourlyRate)}/h)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    Text(text = "✓ NLW Compliant (£${"%.2f".format(entry.effectiveHourlyRate)}/h)", fontSize = 10.sp, color = CareMintTeal, fontWeight = FontWeight.Bold)
                }

                if (entry.isDisputed) {
                    Button(
                        onClick = onOpenDispute,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Disputed • View", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
