package com.example.ui.rota

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
import java.util.UUID

@Composable
fun RotaScreen(
    repository: CareRepository
) {
    val visits by repository.visits.collectAsState()
    val staffList by repository.staffList.collectAsState()
    val clients by repository.clients.collectAsState()
    val branchFilter by repository.currentBranchId.collectAsState()

    var selectedDay by remember { mutableStateOf("03/10/2026") }
    var showAssignDialogForVisit by remember { mutableStateOf<Visit?>(null) }
    var showCreateVisitDialog by remember { mutableStateOf(false) }

    val filteredVisits = visits.filter { v ->
        val matchesBranch = branchFilter == null || v.branchId == branchFilter
        val matchesDay = v.date == selectedDay
        matchesBranch && matchesDay
    }

    val unassignedCount = filteredVisits.count { it.carerId == null }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Rota Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Weekly Rota & Scheduling",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Skills matching, travel optimization & WTD compliance",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { showCreateVisitDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("create_visit_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color(0xFF0A1128), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Schedule Visit", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0A1128))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Week Day Selector Chips
        val days = listOf(
            "01/10/2026" to "Wed 1 Oct",
            "02/10/2026" to "Thu 2 Oct",
            "03/10/2026" to "Fri 3 Oct (Today)",
            "04/10/2026" to "Sat 4 Oct",
            "05/10/2026" to "Sun 5 Oct"
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            days.forEach { (dateKey, label) ->
                FilterChip(
                    selected = selectedDay == dateKey,
                    onClick = { selectedDay = dateKey },
                    label = { Text(label, fontSize = 11.sp) },
                    modifier = Modifier.height(34.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Conflict / Unassigned Warning Alert Banner
        if (unassignedCount > 0) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFEF4444).copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("unassigned_alert_banner")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "UNASSIGNED VISIT ALERT ($unassignedCount)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                        Text(
                            text = "Immediate action needed: Client visit requires staff assignment.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Visits List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredVisits) { visit ->
                RotaVisitItem(
                    visit = visit,
                    onAssignClick = { showAssignDialogForVisit = visit }
                )
            }
        }
    }

    // Modal: Assign Carer Dialog with Skills Matching & Compliance Checks
    showAssignDialogForVisit?.let { targetVisit ->
        AlertDialog(
            onDismissRequest = { showAssignDialogForVisit = null },
            title = {
                Text(
                    text = "Assign Carer to Visit",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "${targetVisit.clientName} • ${targetVisit.startTime} - ${targetVisit.endTime}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Required skills: ${targetVisit.requiredSkills.joinToString(", ")}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Available Staff Matching", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(6.dp))
                    staffList.forEach { staff ->
                        val (canRoster, reason) = repository.canRosterCarer(staff.id)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (canRoster) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFEF4444).copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (canRoster) MaterialTheme.colorScheme.outline.copy(alpha = 0.2f) else Color(0xFFEF4444).copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable(enabled = canRoster) {
                                    val updatedVisits = visits.map { v ->
                                        if (v.id == targetVisit.id) {
                                            v.copy(carerId = staff.id, carerName = staff.fullName)
                                        } else v
                                    }
                                    // Update state
                                    repository.createVisit(targetVisit.copy(carerId = staff.id, carerName = staff.fullName))
                                    showAssignDialogForVisit = null
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = staff.fullName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "${staff.role.displayName} • ${staff.hoursWorkedThisWeek}h this week", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    if (!canRoster) {
                                        Text(text = "❌ $reason", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                                    }
                                }

                                if (canRoster) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = CareMintTealContainer
                                    ) {
                                        Text(
                                            text = "Assign",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CareMintTeal,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAssignDialogForVisit = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Modal: Schedule New Visit
    if (showCreateVisitDialog) {
        var selectedClientId by remember { mutableStateOf(clients.first().id) }
        var visitType by remember { mutableStateOf("Morning Call") }
        var startTime by remember { mutableStateOf("08:30") }
        var endTime by remember { mutableStateOf("09:15") }

        AlertDialog(
            onDismissRequest = { showCreateVisitDialog = false },
            title = { Text("Schedule New Visit", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Select Client", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        clients.take(3).forEach { c ->
                            FilterChip(
                                selected = selectedClientId == c.id,
                                onClick = { selectedClientId = c.id },
                                label = { Text(c.fullName.split(" ").first(), fontSize = 10.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = visitType,
                        onValueChange = { visitType = it },
                        label = { Text("Visit Type / Purpose") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = { startTime = it },
                            label = { Text("Start Time") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = { endTime = it },
                            label = { Text("End Time") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val client = clients.find { it.id == selectedClientId } ?: clients.first()
                        val newVisit = Visit(
                            id = "visit-${UUID.randomUUID().toString().take(6)}",
                            organisationId = "org-allcarex-01",
                            branchId = client.branchId,
                            clientId = client.id,
                            clientName = client.fullName,
                            clientAddress = client.address,
                            clientPostcode = client.postcode,
                            carerId = "staff-04",
                            carerName = "David Okonkwo",
                            date = selectedDay,
                            startTime = startTime,
                            endTime = endTime,
                            visitType = visitType,
                            status = VisitStatus.SCHEDULED,
                            tasks = listOf(
                                VisitTask(title = "General Care & Safety Check", category = "Personal Care", isMandatory = true),
                                VisitTask(title = "Hydration Prompt (250ml)", category = "Fluid Check", isMandatory = true)
                            )
                        )
                        repository.createVisit(newVisit)
                        showCreateVisitDialog = false
                    },
                    modifier = Modifier.testTag("submit_create_visit_button")
                ) {
                    Text("Confirm Schedule")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateVisitDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun RotaVisitItem(
    visit: Visit,
    onAssignClick: () -> Unit
) {
    val isUnassigned = visit.carerId == null

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isUnassigned) Color(0xFFEF4444).copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        ),
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${visit.startTime} - ${visit.endTime}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CareMintTeal
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(${visit.travelTimeMinutes}m travel buffer)",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = visit.clientName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${visit.clientAddress}, ${visit.clientPostcode}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(visit.status.colorHex).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = visit.status.displayName,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(visit.status.colorHex),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isUnassigned) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Unassigned Shift",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                    }

                    Button(
                        onClick = onAssignClick,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        modifier = Modifier.height(32.dp).testTag("assign_carer_button")
                    ) {
                        Text("Assign Staff", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(CareMintTealContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = CareMintTeal, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = visit.carerName ?: "",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (visit.secondCarerName != null) {
                                Text(
                                    text = "+ Second Carer: ${visit.secondCarerName}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Text(
                        text = "${visit.tasks.size} tasks planned",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
