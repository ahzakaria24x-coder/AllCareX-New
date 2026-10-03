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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CareRepository
import com.example.model.HolidayRecord
import com.example.model.SicknessRecord
import com.example.model.StaffHolidayBalance
import com.example.ui.theme.CareMintTeal
import com.example.ui.theme.CareMintTealContainer

@Composable
fun HolidaySicknessView(
    repository: CareRepository
) {
    val holidays by repository.holidays.collectAsState()
    val holidayBalances by repository.holidayBalances.collectAsState()
    val sicknessRecords by repository.sicknessRecords.collectAsState()

    var activeSubSection by remember { mutableStateOf(0) } // 0: Holiday Accrual & Requests, 1: Sickness & SSP
    var showLogSicknessDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Toggle Sub-Section
        TabRow(
            selectedTabIndex = activeSubSection,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = CareMintTeal,
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
        ) {
            Tab(
                selected = activeSubSection == 0,
                onClick = { activeSubSection = 0 },
                text = { Text("Holiday Pay & Requests (${holidays.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeSubSection == 1,
                onClick = { activeSubSection = 1 },
                text = { Text("Sickness & SSP Log (${sicknessRecords.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (activeSubSection == 0) {
            // Holiday Accrual & Leave Requests
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    // Accrual Information Banner
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = CareMintTealContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = CareMintTeal, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("UK Statutory Holiday Accrual (12.07% Method)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                            }
                            Text(
                                "Per UK Employment Rights Act / Harpur Trust v Brazel reforms: irregular hours care staff accrue holiday at 12.07% of hours worked.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Staff Holiday Balances",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                items(holidayBalances) { balance ->
                    StaffBalanceCard(balance = balance)
                }

                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Leave Requests & Rota Clash Check",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                items(holidays) { req ->
                    HolidayRequestCard(
                        request = req,
                        onApprove = { repository.approveHoliday(req.id) }
                    )
                }
            }
        } else {
            // Sickness & SSP
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Statutory Sick Pay (SSP) & Absence Tracker",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Statutory weekly rate £116.75 (qualifies after 4+ consecutive days)",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(
                            onClick = { showLogSicknessDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp).testTag("log_sickness_button")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color(0xFF0A1128), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Log Sickness", color = Color(0xFF0A1128), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                items(sicknessRecords) { record ->
                    SicknessRecordCard(record = record)
                }
            }
        }
    }

    // Modal: Log Sickness
    if (showLogSicknessDialog) {
        var staffName by remember { mutableStateOf("James Wilson") }
        var staffId by remember { mutableStateOf("staff-07") }
        var startDate by remember { mutableStateOf("03/10/2026") }
        var endDate by remember { mutableStateOf("07/10/2026") }
        var qualDays by remember { mutableStateOf("5") }
        var fitNote by remember { mutableStateOf("MED3-88192 (Viral Respiratory)") }
        var rtwNotes by remember { mutableStateOf("Return to work discussion planned") }

        AlertDialog(
            onDismissRequest = { showLogSicknessDialog = false },
            title = { Text("Log Staff Sickness & Absence", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Records absence and automatically determines Statutory Sick Pay (SSP) eligibility.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = staffName,
                        onValueChange = { staffName = it },
                        label = { Text("Staff Member") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = startDate,
                            onValueChange = { startDate = it },
                            label = { Text("Start Date") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = endDate,
                            onValueChange = { endDate = it },
                            label = { Text("End Date") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = fitNote,
                        onValueChange = { fitNote = it },
                        label = { Text("Fit Note / Medical Ref") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = rtwNotes,
                        onValueChange = { rtwNotes = it },
                        label = { Text("Return to Work Notes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val days = qualDays.toIntOrNull() ?: 4
                        val isSsp = days >= 4
                        val newRecord = SicknessRecord(
                            staffId = staffId,
                            staffName = staffName,
                            startDate = startDate,
                            endDate = endDate,
                            qualifyingDays = days,
                            isSspEligible = isSsp,
                            sspNotes = if (isSsp) "Eligible for Statutory Sick Pay (SSP rate £116.75/wk pro-rata)" else "Under 4 days waiting period; not SSP eligible",
                            fitNoteRef = fitNote,
                            returnToWorkCompleted = true,
                            returnToWorkNotes = rtwNotes
                        )
                        repository.logSickness(newRecord)
                        showLogSicknessDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                    modifier = Modifier.testTag("confirm_log_sickness_button")
                ) {
                    Text("Save Sickness Log", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogSicknessDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun StaffBalanceCard(balance: StaffHolidayBalance) {
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
                Text(text = balance.staffName, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = "Worked: ${balance.totalWorkedHours}h (12.07% accrual)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("Accrued", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${balance.accruedHours}h", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Taken", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${balance.takenHours}h", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Balance", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${balance.remainingBalanceHours}h", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = CareMintTeal)
                }
            }
        }
    }
}

@Composable
private fun HolidayRequestCard(
    request: HolidayRecord,
    onApprove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (request.hasRotaClash) Color(0xFFF59E0B) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
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
                    Text(text = request.staffName, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${request.startDate} - ${request.endDate} (${request.hoursRequested} hrs)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (request.status == "Approved") CareMintTealContainer else Color(0xFFF59E0B).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = request.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (request.status == "Approved") CareMintTeal else Color(0xFFF59E0B),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            if (request.hasRotaClash) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF59E0B).copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = request.notes.ifBlank { "ROTA CLASH: Staff member has scheduled client visits during this period. Cover carer must be assigned." },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFF59E0B)
                        )
                    }
                }
            }

            if (request.status == "Pending") {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(30.dp).testTag("approve_holiday_${request.id}")
                    ) {
                        Text("Approve & Check Cover", color = Color(0xFF0A1128), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SicknessRecordCard(record: SicknessRecord) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(text = record.staffName, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Absence: ${record.startDate} - ${record.endDate} (${record.qualifyingDays} days)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (record.isSspEligible) CareMintTealContainer else Color(0xFF64748B).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (record.isSspEligible) "SSP Eligible" else "Under 4 Days",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (record.isSspEligible) CareMintTeal else Color(0xFF64748B),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = record.sspNotes, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)

            if (record.fitNoteRef != null) {
                Text(text = "Medical Evidence: ${record.fitNoteRef}", fontSize = 10.sp, color = CareMintTeal, fontWeight = FontWeight.SemiBold)
            }

            if (record.returnToWorkNotes != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "✓ Return to Work: ${record.returnToWorkNotes}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
