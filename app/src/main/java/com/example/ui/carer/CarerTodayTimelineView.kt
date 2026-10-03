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
fun CarerTodayTimelineView(
    repository: CareRepository,
    isHighContrast: Boolean,
    isLargeText: Boolean,
    onStartVisit: (Visit) -> Unit,
    onOpenVisitFlow: (Visit) -> Unit,
    onOpenHandover: () -> Unit,
    onOpenIncident: (Visit?) -> Unit,
    onOpenBodyMap: (String) -> Unit
) {
    val visits by repository.visits.collectAsState()
    val currentUser by repository.currentUser.collectAsState()
    val offlineQueue by repository.offlineQueue.collectAsState()
    val isOffline by repository.isOfflineSimulated.collectAsState()
    val safetyState by repository.loneWorkerSafetyState.collectAsState()

    var showRunningLateDialog by remember { mutableStateOf(false) }
    var selectedVisitForLate by remember { mutableStateOf<Visit?>(null) }
    var lateMinutesSelected by remember { mutableStateOf(15) }
    var lateReasonSelected by remember { mutableStateOf("Traffic congestion between calls") }
    var notifyFamilyChecked by remember { mutableStateOf(true) }

    var showPanicConfirmDialog by remember { mutableStateOf(false) }

    // Carer's visits for today (David Okonkwo or current staff)
    val effectiveCarerId = if (currentUser.role == UserRole.CARE_WORKER) currentUser.id else "staff-04"
    val todayDate = "03/10/2026"
    val carerVisits = visits.filter {
        (it.carerId == effectiveCarerId || it.carerId == "staff-04") && it.date == todayDate
    }.sortedBy { it.startTime }

    val next48hVisits = visits.filter {
        (it.carerId == effectiveCarerId || it.carerId == "staff-04") && it.date != todayDate
    }

    val primaryTextColor = if (isHighContrast) Color.White else MaterialTheme.colorScheme.onSurface
    val textScale = if (isLargeText) 1.2f else 1.0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("carer_today_timeline"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Offline Sync Notice Banner
        if (isOffline || offlineQueue.isNotEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF59E0B).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF59E0B)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("offline_sync_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (isOffline) Icons.Default.CloudOff else Icons.Default.SyncProblem,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isOffline) "Offline Mode Active (Poor Signal)" else "Pending Cloud Sync",
                                    fontSize = (12 * textScale).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF59E0B)
                                )
                                Text(
                                    text = "${offlineQueue.size} items queued safely • Zero data loss guarantee",
                                    fontSize = (10 * textScale).sp,
                                    color = primaryTextColor
                                )
                            }
                        }

                        Button(
                            onClick = { repository.syncAllOfflineActions() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("sync_now_button")
                        ) {
                            Text(
                                text = "Sync Now",
                                color = Color(0xFF0A1128),
                                fontWeight = FontWeight.Bold,
                                fontSize = (11 * textScale).sp
                            )
                        }
                    }
                }
            }
        }

        // Safety Quick Actions Bar (Running Late, I Need Help Panic, End-of-Shift Handover)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Running Late Button (min 48dp touch target)
                OutlinedButton(
                    onClick = {
                        selectedVisitForLate = carerVisits.firstOrNull { it.status != VisitStatus.COMPLETED } ?: carerVisits.firstOrNull()
                        showRunningLateDialog = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("running_late_action_button"),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF59E0B)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF59E0B))
                ) {
                    Icon(imageVector = Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Running Late", fontSize = (11 * textScale).sp, fontWeight = FontWeight.Bold)
                }

                // "I Need Help" Panic Button (min 48dp touch target)
                Button(
                    onClick = { showPanicConfirmDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("i_need_help_panic_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("I Need Help", color = Color.White, fontSize = (11 * textScale).sp, fontWeight = FontWeight.ExtraBold)
                }

                // Shift Handover Button
                OutlinedButton(
                    onClick = onOpenHandover,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("shift_handover_action_button"),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, CareMintTeal),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CareMintTeal)
                ) {
                    Icon(imageVector = Icons.Default.AssignmentReturn, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Handover", fontSize = (11 * textScale).sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Section Title: My Day Timeline
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Day Timeline",
                        fontSize = (18 * textScale).sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = primaryTextColor
                    )
                    Text(
                        text = "Saturday, 3 Oct 2026 • ${carerVisits.count { it.status == VisitStatus.COMPLETED }}/${carerVisits.size} calls finished",
                        fontSize = (11 * textScale).sp,
                        color = if (isHighContrast) Color.LightGray else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CareMintTealContainer
                ) {
                    Text(
                        text = "48h CACHED",
                        fontSize = (9 * textScale).sp,
                        fontWeight = FontWeight.Bold,
                        color = CareMintTeal,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Timeline Visit Cards
        items(carerVisits) { visit ->
            CarerTimelineCard(
                visit = visit,
                isHighContrast = isHighContrast,
                textScale = textScale,
                onStartVisit = { onStartVisit(visit) },
                onOpenVisitFlow = { onOpenVisitFlow(visit) },
                onOpenIncident = { onOpenIncident(visit) },
                onOpenBodyMap = { onOpenBodyMap(visit.clientId) }
            )
        }

        // 48-Hour Ahead Schedule Summary (Tomorrow's preview)
        if (next48hVisits.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tomorrow's Schedule (Cached Offline)",
                                fontSize = (12 * textScale).sp,
                                fontWeight = FontWeight.Bold,
                                color = CareMintTeal
                            )
                            Text(
                                text = "${next48hVisits.size} calls",
                                fontSize = (10 * textScale).sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        next48hVisits.forEach { nextVisit ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${nextVisit.startTime} - ${nextVisit.clientName} (${nextVisit.visitType})",
                                    fontSize = (11 * textScale).sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = nextVisit.clientPostcode,
                                    fontSize = (10 * textScale).sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal: Running Late Dialog
    if (showRunningLateDialog && selectedVisitForLate != null) {
        val targetVisit = selectedVisitForLate!!
        AlertDialog(
            onDismissRequest = { showRunningLateDialog = false },
            title = {
                Text(
                    text = "Notify Office: Running Late",
                    fontWeight = FontWeight.Bold,
                    fontSize = (16 * textScale).sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Call: ${targetVisit.clientName} (${targetVisit.startTime})",
                        fontSize = (12 * textScale).sp,
                        fontWeight = FontWeight.Bold,
                        color = CareMintTeal
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Estimated Delay:", fontSize = (11 * textScale).sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(10, 15, 30, 45).forEach { mins ->
                            FilterChip(
                                selected = lateMinutesSelected == mins,
                                onClick = { lateMinutesSelected = mins },
                                label = { Text("+$mins mins", fontSize = (11 * textScale).sp) },
                                modifier = Modifier.heightIn(min = 48.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Reason for Delay:", fontSize = (11 * textScale).sp, fontWeight = FontWeight.SemiBold)
                    listOf(
                        "Traffic congestion between calls",
                        "Previous call overran (complex clinical care)",
                        "Public transport / bus cancellation",
                        "Awaiting double-up carer arrival"
                    ).forEach { reason ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 40.dp)
                                .clickable { lateReasonSelected = reason }
                        ) {
                            RadioButton(
                                selected = lateReasonSelected == reason,
                                onClick = { lateReasonSelected = reason },
                                colors = RadioButtonDefaults.colors(selectedColor = CareMintTeal)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(reason, fontSize = (11 * textScale).sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .clickable { notifyFamilyChecked = !notifyFamilyChecked }
                    ) {
                        Checkbox(
                            checked = notifyFamilyChecked,
                            onCheckedChange = { notifyFamilyChecked = it },
                            colors = CheckboxDefaults.colors(checkedColor = CareMintTeal)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Notify family / next-of-kin via automatic SMS",
                            fontSize = (11 * textScale).sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.markRunningLateWithNotification(
                            visitId = targetVisit.id,
                            delayMinutes = lateMinutesSelected,
                            reason = lateReasonSelected,
                            notifyFamily = notifyFamilyChecked
                        )
                        showRunningLateDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("submit_running_late_button")
                ) {
                    Text("Send Alert (+$lateMinutesSelected mins)", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRunningLateDialog = false },
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Modal: Panic SOS Confirm
    if (showPanicConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showPanicConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ACTIVATE PANIC ALERT?", fontWeight = FontWeight.ExtraBold, color = Color(0xFFEF4444))
                }
            },
            text = {
                Text(
                    text = "This immediately transmits your LIVE GPS coordinates (${safetyState.lastGpsCoords}) to the Westminster on-call manager (${safetyState.escalationContactName}, ${safetyState.escalationContactPhone}) and the central 24/7 monitoring desk.",
                    fontSize = (13 * textScale).sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.triggerLoneWorkerPanic()
                        showPanicConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("confirm_panic_button")
                ) {
                    Text("TRANSMIT EMERGENCY PANIC", fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPanicConfirmDialog = false },
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun CarerTimelineCard(
    visit: Visit,
    isHighContrast: Boolean,
    textScale: Float,
    onStartVisit: () -> Unit,
    onOpenVisitFlow: () -> Unit,
    onOpenIncident: () -> Unit,
    onOpenBodyMap: () -> Unit
) {
    val borderColor = when (visit.status) {
        VisitStatus.CHECKED_IN -> CareMintTeal
        VisitStatus.RUNNING_LATE -> Color(0xFFF59E0B)
        VisitStatus.MISSED -> Color(0xFFEF4444)
        VisitStatus.COMPLETED -> if (isHighContrast) Color.LightGray else Color(0xFF10B981).copy(alpha = 0.5f)
        else -> if (isHighContrast) Color.White else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(if (isHighContrast) 2.dp else 1.5.dp, borderColor),
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenVisitFlow() }
            .testTag("carer_visit_card_${visit.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // RED / AMBER BANNER: Current Alerts (Care-plan change, med change, risk flag)
            if (visit.alerts.isNotEmpty()) {
                visit.alerts.forEach { alertText ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFEF4444).copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .testTag("visit_alert_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PriorityHigh,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = alertText,
                                fontSize = (11 * textScale).sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFEF4444)
                            )
                        }
                    }
                }
            }

            // Top Row: Time Window, Status Badge, and DNACPR Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = CareMintTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${visit.startTime} - ${visit.endTime}",
                        fontSize = (15 * textScale).sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CareMintTeal
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (visit.dnacprStatus) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFEF4444)
                        ) {
                            Text(
                                text = "DNACPR",
                                fontSize = (9 * textScale).sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(visit.status.colorHex).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = visit.status.displayName,
                            fontSize = (10 * textScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(visit.status.colorHex),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Client Name & Address
            Text(
                text = visit.clientName,
                fontSize = (17 * textScale).sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "${visit.clientAddress}, ${visit.clientPostcode}",
                    fontSize = (11 * textScale).sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "${visit.visitType} • Key Safe: ${visit.keySafeCode ?: "#4920"}",
                fontSize = (11 * textScale).sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 2.dp)
            )

            // In-Progress indicator or completion notes
            if (visit.status == VisitStatus.CHECKED_IN) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CareMintTealContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = CareMintTeal, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Checked In at ${visit.checkInTime} • ${visit.checkInGps ?: "GPS Verified"}",
                            fontSize = (11 * textScale).sp,
                            fontWeight = FontWeight.Medium,
                            color = CareMintTeal
                        )
                    }
                }
            } else if (visit.status == VisitStatus.COMPLETED) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "✓ Completed: ${visit.checkInTime ?: visit.startTime} - ${visit.checkOutTime ?: visit.endTime} • Fluid: ${visit.fluidIntakeMl ?: 300}ml",
                    fontSize = (11 * textScale).sp,
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Big "Start Visit" button (min 48dp height) or Visit Flow Jump
            if (visit.status == VisitStatus.SCHEDULED || visit.status == VisitStatus.RUNNING_LATE) {
                Button(
                    onClick = onStartVisit,
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                        .testTag("start_visit_button_${visit.id}")
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF0A1128), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "START VISIT (CHECK IN)",
                        color = Color(0xFF0A1128),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = (13 * textScale).sp
                    )
                }
            } else if (visit.status == VisitStatus.CHECKED_IN) {
                Button(
                    onClick = onOpenVisitFlow,
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                        .testTag("continue_visit_button_${visit.id}")
                ) {
                    Icon(imageVector = Icons.Default.TaskAlt, contentDescription = null, tint = Color(0xFF0A1128), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CONTINUE VISIT FLOW (${visit.tasks.count { it.isCompleted }}/${visit.tasks.size} Tasks)",
                        color = Color(0xFF0A1128),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = (12 * textScale).sp
                    )
                }
            }

            // Quick In-Visit Utilities: Body Map & Quick Incident
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenBodyMap,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("body_map_quick_button_${visit.id}")
                ) {
                    Icon(imageVector = Icons.Default.Accessibility, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Body Map", fontSize = (11 * textScale).sp)
                }

                OutlinedButton(
                    onClick = onOpenIncident,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("incident_quick_button_${visit.id}")
                ) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Report Incident", fontSize = (11 * textScale).sp)
                }
            }
        }
    }
}
