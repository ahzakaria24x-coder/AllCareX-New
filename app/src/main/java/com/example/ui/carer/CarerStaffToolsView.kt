package com.example.ui.carer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.model.*
import com.example.ui.theme.CareMintTeal
import com.example.ui.theme.CareMintTealContainer

@Composable
fun CarerStaffToolsView(
    repository: CareRepository,
    isHighContrast: Boolean,
    isLargeText: Boolean,
    onOpenNotifyManager: () -> Unit
) {
    val currentUser by repository.currentUser.collectAsState()
    val timesheets by repository.timesheets.collectAsState()
    val holidayBalances by repository.holidayBalances.collectAsState()
    val staffDocs by repository.staffDocuments.collectAsState()
    val trainingRecords by repository.trainingList.collectAsState()
    val managerNotifications by repository.managerNotifications.collectAsState()

    val textScale = if (isLargeText) 1.2f else 1.0f

    val effectiveStaffId = if (currentUser.role == UserRole.CARE_WORKER) currentUser.id else "staff-04"
    val carerTimesheets = timesheets.filter { it.staffId == effectiveStaffId || it.staffId == "staff-04" }
    val carerHolidayBalance = holidayBalances.find { it.staffId == effectiveStaffId }
        ?: holidayBalances.find { it.staffId == "staff-04" }
        ?: StaffHolidayBalance("staff-04", currentUser.name, 240.0, 28.97, 14.0, 14.97)

    val carerTraining = trainingRecords.filter { it.staffId == effectiveStaffId || it.staffId == "staff-04" }

    var selectedTimesheetForDispute by remember { mutableStateOf<TimesheetEntry?>(null) }
    var disputeReasonInput by remember { mutableStateOf("") }

    var showRequestLeaveDialog by remember { mutableStateOf(false) }
    var leaveStartDate by remember { mutableStateOf("15/10/2026") }
    var leaveEndDate by remember { mutableStateOf("17/10/2026") }
    var leaveHours by remember { mutableStateOf("15.0") }
    var leaveNotes by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("carer_staff_tools_view"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Staff Profile Header Card
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (isHighContrast) Color.White else CareMintTeal.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = currentUser.name,
                                fontSize = (18 * textScale).sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Care Worker • Westminster Branch • Staff ID: $effectiveStaffId",
                                fontSize = (11 * textScale).sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(shape = RoundedCornerShape(6.dp), color = CareMintTealContainer) {
                            Text(
                                text = "DBS VERIFIED",
                                fontSize = (9 * textScale).sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CareMintTeal,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // "Notify My Manager" Button (min 48dp)
                    Button(
                        onClick = onOpenNotifyManager,
                        colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .testTag("notify_my_manager_me_button")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color(0xFF0A1128), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Notify My Manager (Unavailability, Safety / Concern)",
                            color = Color(0xFF0A1128),
                            fontWeight = FontWeight.Bold,
                            fontSize = (11 * textScale).sp
                        )
                    }
                }
            }
        }

        // Section 1: Statutory Holiday Balance
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.BeachAccess, contentDescription = null, tint = CareMintTeal, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("My Holiday Balance (12.07% Accrual)", fontSize = (13 * textScale).sp, fontWeight = FontWeight.Bold)
                        }
                        Surface(shape = RoundedCornerShape(4.dp), color = CareMintTealContainer) {
                            Text(
                                text = "UK Statutory",
                                fontSize = (9 * textScale).sp,
                                color = CareMintTeal,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("TOTAL ACCRUED", fontSize = (9 * textScale).sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${carerHolidayBalance.accruedHours} hrs", fontSize = (14 * textScale).sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("TAKEN / BOOKED", fontSize = (9 * textScale).sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${carerHolidayBalance.takenHours} hrs", fontSize = (14 * textScale).sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                        }
                        Column {
                            Text("REMAINING BALANCE", fontSize = (9 * textScale).sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${carerHolidayBalance.remainingBalanceHours} hrs", fontSize = (15 * textScale).sp, fontWeight = FontWeight.ExtraBold, color = CareMintTeal)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { showRequestLeaveDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .testTag("request_leave_me_button")
                    ) {
                        Icon(imageVector = Icons.Default.Event, contentDescription = null, tint = Color(0xFF0A1128), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Request Annual Leave", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold, fontSize = (12 * textScale).sp)
                    }
                }
            }
        }

        // Section 2: My Timesheets & Dispute
        item {
            Text(
                text = "My Recorded Timesheets (${carerTimesheets.size})",
                fontSize = (14 * textScale).sp,
                fontWeight = FontWeight.Bold,
                color = if (isHighContrast) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }

        items(carerTimesheets) { entry ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (entry.isDisputed) Color(0xFFEF4444) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(entry.clientName, fontSize = (13 * textScale).sp, fontWeight = FontWeight.Bold)
                            Text("${entry.entryType.displayName} • ${entry.date}", fontSize = (10 * textScale).sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(shape = RoundedCornerShape(4.dp), color = CareMintTealContainer) {
                            Text(
                                text = "£${"%.2f".format(entry.totalPayable)}",
                                fontSize = (12 * textScale).sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CareMintTeal,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Actual: ${entry.actualDeliveredHours}h (Sched: ${entry.scheduledHours}h)",
                            fontSize = (11 * textScale).sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Travel: ${entry.travelTimeMinutes}m (£${"%.2f".format(entry.travelPay)})",
                            fontSize = (10 * textScale).sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Holiday Accrual: £${"%.2f".format(entry.holidayPayAccrued)}",
                            fontSize = (10 * textScale).sp,
                            color = Color(0xFF0284C7)
                        )

                        if (entry.isDisputed) {
                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFEF4444).copy(alpha = 0.15f)) {
                                Text("DISPUTED", fontSize = (9 * textScale).sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444), modifier = Modifier.padding(4.dp))
                            }
                        } else {
                            TextButton(
                                onClick = {
                                    selectedTimesheetForDispute = entry
                                    disputeReasonInput = ""
                                },
                                modifier = Modifier.heightIn(min = 48.dp)
                            ) {
                                Text("Dispute Time", fontSize = (11 * textScale).sp, color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (entry.isDisputed && !entry.disputeReason.isNullOrBlank()) {
                        Text("Reason: ${entry.disputeReason}", fontSize = (10 * textScale).sp, color = Color(0xFFEF4444))
                    }
                }
            }
        }

        // Section 3: Training Expiries
        item {
            Text(
                text = "My Mandatory Training Expiries",
                fontSize = (14 * textScale).sp,
                fontWeight = FontWeight.Bold,
                color = if (isHighContrast) Color.White else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(carerTraining) { tr ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(tr.moduleName, fontSize = (12 * textScale).sp, fontWeight = FontWeight.Bold)
                        Text("Completed: ${tr.completedDate} • Expiry: ${tr.expiryDate}", fontSize = (10 * textScale).sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (tr.isExpired) Color(0xFFEF4444).copy(alpha = 0.2f) else Color(0xFF10B981).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = if (tr.isExpired) "EXPIRED" else "IN-DATE",
                            fontSize = (9 * textScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = if (tr.isExpired) Color(0xFFEF4444) else Color(0xFF10B981),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Section 4: My Verified Documents
        item {
            Text(
                text = "My Verified Documents & Credentials",
                fontSize = (14 * textScale).sp,
                fontWeight = FontWeight.Bold,
                color = if (isHighContrast) Color.White else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(staffDocs) { doc ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(doc.title, fontSize = (12 * textScale).sp, fontWeight = FontWeight.Bold)
                        Text("Ref: ${doc.fileRef} • Issued: ${doc.issueDate}", fontSize = (10 * textScale).sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Surface(shape = RoundedCornerShape(4.dp), color = CareMintTealContainer) {
                        Text(
                            text = doc.status.uppercase(),
                            fontSize = (9 * textScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = CareMintTeal,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Section 5: PWA & Offline Engine Status Card
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CareMintTealContainer.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.InstallMobile, contentDescription = null, tint = CareMintTeal)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PWA & Offline Architecture Ready", fontSize = (12 * textScale).sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                    }
                    Text(
                        text = "• Web Manifest & Service Worker configured for standalone mobile install.\n• 48-Hour visit rota & client essentials cached in memory.\n• Repository data-service layer isolated for easy backend swapping.",
                        fontSize = (10 * textScale).sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }

    // Modal: Dispute Timesheet
    selectedTimesheetForDispute?.let { entry ->
        AlertDialog(
            onDismissRequest = { selectedTimesheetForDispute = null },
            title = { Text("Dispute Visit Time", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Visit: ${entry.clientName} (${entry.date})", fontWeight = FontWeight.Bold, color = CareMintTeal)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("State reason for dispute (e.g. 25m unpaid travel delay, road closure):")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = disputeReasonInput,
                        onValueChange = { disputeReasonInput = it },
                        modifier = Modifier.fillMaxWidth().testTag("dispute_reason_field")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (disputeReasonInput.isNotBlank()) {
                            repository.raiseTimesheetDispute(entry.id, disputeReasonInput)
                            selectedTimesheetForDispute = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text("Submit Dispute", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { selectedTimesheetForDispute = null },
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Modal: Request Leave
    if (showRequestLeaveDialog) {
        AlertDialog(
            onDismissRequest = { showRequestLeaveDialog = false },
            title = { Text("Request Annual Leave", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Remaining Balance: ${carerHolidayBalance.remainingBalanceHours} hours", fontWeight = FontWeight.Bold, color = CareMintTeal)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = leaveStartDate,
                        onValueChange = { leaveStartDate = it },
                        label = { Text("Start Date (DD/MM/YYYY)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = leaveEndDate,
                        onValueChange = { leaveEndDate = it },
                        label = { Text("End Date (DD/MM/YYYY)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = leaveHours,
                        onValueChange = { leaveHours = it },
                        label = { Text("Total Hours (e.g. 15.0)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = leaveNotes,
                        onValueChange = { leaveNotes = it },
                        label = { Text("Notes / Reason") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val hrs = leaveHours.toDoubleOrNull() ?: 15.0
                        repository.requestHoliday(
                            staffId = effectiveStaffId,
                            staffName = currentUser.name,
                            startDate = leaveStartDate,
                            endDate = leaveEndDate,
                            hours = hrs,
                            notes = leaveNotes
                        )
                        showRequestLeaveDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text("Submit Holiday Request", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRequestLeaveDialog = false },
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
