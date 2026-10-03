package com.example.ui.incidents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun IncidentsScreen(
    repository: CareRepository
) {
    val incidents by repository.incidents.collectAsState()
    val clients by repository.clients.collectAsState()
    val currentUser by repository.currentUser.collectAsState()

    var showLogIncidentDialog by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf<IncidentCategory?>(null) }
    var selectedSeverityFilter by remember { mutableStateOf<IncidentSeverity?>(null) }

    val filteredIncidents = incidents.filter { inc ->
        val matchesCat = selectedCategoryFilter == null || inc.category == selectedCategoryFilter
        val matchesSev = selectedSeverityFilter == null || inc.severity == selectedSeverityFilter
        matchesCat && matchesSev
    }

    val cqcCount = incidents.count { it.isCqcNotifiable }
    val safeguardingCount = incidents.count { it.isLocalAuthoritySafeguardingReferral }

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
                    text = "Incidents & Safeguarding",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Statutory CQC Reg 18, LA referrals & Duty of Candour tracker",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { showLogIncidentDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("log_incident_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Log Incident", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Summary Metric Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "TOTAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "${incidents.size}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "Logged this month", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "CQC NOTIFIABLE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                    Text(text = "$cqcCount", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFEF4444))
                    Text(text = "Reg 18 Submissions", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "SAFEGUARDING", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                    Text(text = "$safeguardingCount", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFF59E0B))
                    Text(text = "LA S42 Referrals", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Incidents List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredIncidents) { incident ->
                IncidentCardItem(incident = incident)
            }
        }
    }

    // Modal: Log New Incident Report
    if (showLogIncidentDialog) {
        var selectedClientId by remember { mutableStateOf(clients.first().id) }
        var category by remember { mutableStateOf(IncidentCategory.FALL) }
        var severity by remember { mutableStateOf(IncidentSeverity.MODERATE) }
        var location by remember { mutableStateOf("Living Room") }
        var description by remember { mutableStateOf("") }
        var immediateActions by remember { mutableStateOf("") }
        var isCqc by remember { mutableStateOf(false) }
        var isSafeguarding by remember { mutableStateOf(false) }
        var dutyOfCandour by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { showLogIncidentDialog = false },
            title = { Text("Log Clinical / Care Incident", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("Involved Client", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                    Text("Category", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            IncidentCategory.FALL,
                            IncidentCategory.MEDICATION_ERROR,
                            IncidentCategory.PRESSURE_INJURY
                        ).forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat.displayName.split(" ").first(), fontSize = 10.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Severity", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IncidentSeverity.values().forEach { sev ->
                            FilterChip(
                                selected = severity == sev,
                                onClick = { severity = sev },
                                label = { Text(sev.name, fontSize = 10.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location of Incident") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Detailed Incident Description") },
                        placeholder = { Text("Describe sequence of events, injuries noted, witness observations...") },
                        modifier = Modifier.fillMaxWidth().testTag("incident_desc_input"),
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = immediateActions,
                        onValueChange = { immediateActions = it },
                        label = { Text("Immediate Actions & First Aid Taken") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Statutory Flags
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isCqc, onCheckedChange = { isCqc = it }, colors = CheckboxDefaults.colors(checkedColor = Color(0xFFEF4444)))
                        Text("CQC Regulation 18 Notifiable Incident", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isSafeguarding, onCheckedChange = { isSafeguarding = it }, colors = CheckboxDefaults.colors(checkedColor = Color(0xFFF59E0B)))
                        Text("Local Authority Safeguarding S42 Referral", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = dutyOfCandour, onCheckedChange = { dutyOfCandour = it }, colors = CheckboxDefaults.colors(checkedColor = CareMintTeal))
                        Text("Duty of Candour: Next of Kin informed", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val client = clients.find { it.id == selectedClientId } ?: clients.first()
                        val newInc = IncidentReport(
                            id = "inc-${UUID.randomUUID().toString().take(6)}",
                            organisationId = "org-allcarex-01",
                            branchId = client.branchId,
                            clientId = client.id,
                            clientName = client.fullName,
                            reportedByStaffId = currentUser.id,
                            reportedByStaffName = currentUser.name,
                            incidentDate = "03/10/2026",
                            incidentTime = "10:30",
                            category = category,
                            severity = severity,
                            location = location,
                            description = description.ifBlank { "Client sustained an incident during visit. Evaluated by staff." },
                            immediateActionsTaken = immediateActions.ifBlank { "Full physical check completed. Emergency protocols enacted." },
                            paramedicsCalled = severity == IncidentSeverity.SEVERE || severity == IncidentSeverity.CRITICAL,
                            isCqcNotifiable = isCqc,
                            isLocalAuthoritySafeguardingReferral = isSafeguarding,
                            safeguardingReferenceNumber = if (isSafeguarding) "SG-${UUID.randomUUID().toString().take(6).uppercase()}" else null,
                            dutyOfCandourCompleted = dutyOfCandour,
                            dutyOfCandourNotes = if (dutyOfCandour) "Next of kin notified via telephone and incident summary documented." else null,
                            investigationStatus = "Under Investigation",
                            lessonsLearned = "Care plan risk assessment scheduled for re-evaluation."
                        )
                        repository.logIncident(newInc)
                        showLogIncidentDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    modifier = Modifier.testTag("submit_incident_button")
                ) {
                    Text("Submit Incident Report")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogIncidentDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun IncidentCardItem(incident: IncidentReport) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (incident.isCqcNotifiable) Color(0xFFEF4444).copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        ),
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
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
                            text = incident.category.displayName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Client: ${incident.clientName} • ${incident.incidentDate} at ${incident.incidentTime}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Location: ${incident.location}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(incident.severity.colorHex).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = incident.severity.name,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(incident.severity.colorHex),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Flags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (incident.isCqcNotifiable) {
                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFEF4444).copy(alpha = 0.2f)) {
                        Text("CQC REG 18", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
                if (incident.isLocalAuthoritySafeguardingReferral) {
                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFF59E0B).copy(alpha = 0.2f)) {
                        Text("LA SAFEGUARDING", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
                if (incident.dutyOfCandourCompleted) {
                    Surface(shape = RoundedCornerShape(4.dp), color = CareMintTealContainer.copy(alpha = 0.5f)) {
                        Text("DUTY OF CANDOUR COMPLETED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CareMintTeal, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = incident.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)

            if (expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                Text("Immediate Actions Taken:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                Text(incident.immediateActionsTaken, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)

                if (!incident.lessonsLearned.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Root Cause & Lessons Learned:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                    Text(incident.lessonsLearned, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Reported by: ${incident.reportedByStaffName} • Investigation: ${incident.investigationStatus}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
