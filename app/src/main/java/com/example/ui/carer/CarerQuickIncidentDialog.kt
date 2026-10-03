package com.example.ui.carer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
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
import com.example.model.*
import com.example.ui.theme.CareMintTeal

@Composable
fun CarerQuickIncidentDialog(
    visit: Visit?,
    repository: CareRepository,
    onDismiss: () -> Unit
) {
    val currentUser by repository.currentUser.collectAsState()
    val clients by repository.clients.collectAsState()

    var selectedClientId by remember { mutableStateOf(visit?.clientId ?: clients.firstOrNull()?.id ?: "client-01") }
    val client = clients.find { it.id == selectedClientId } ?: clients.firstOrNull()

    var incidentCategory by remember { mutableStateOf(IncidentCategory.FALL) }
    var incidentSeverity by remember { mutableStateOf(IncidentSeverity.MINOR) }
    var locationInput by remember { mutableStateOf("Living room floor") }
    var descriptionInput by remember { mutableStateOf("") }
    var immediateActionsInput by remember { mutableStateOf("Assisted into chair, checked vitals and skin integrity.") }
    var paramedicsCalled by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Log Quick Incident Report", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Client: ${client?.fullName ?: "Client"} • ${visit?.startTime ?: "Current Shift"}", fontWeight = FontWeight.Bold, color = CareMintTeal)
                Spacer(modifier = Modifier.height(8.dp))

                Text("Category:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(IncidentCategory.FALL, IncidentCategory.PRESSURE_INJURY, IncidentCategory.MEDICATION_ERROR, IncidentCategory.SAFEGUARDING_CONCERN).forEach { cat ->
                        FilterChip(
                            selected = incidentCategory == cat,
                            onClick = { incidentCategory = cat },
                            label = { Text(cat.displayName, fontSize = 10.sp) },
                            modifier = Modifier.heightIn(min = 44.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text("Severity:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(IncidentSeverity.MINOR, IncidentSeverity.MODERATE, IncidentSeverity.SEVERE).forEach { sev ->
                        FilterChip(
                            selected = incidentSeverity == sev,
                            onClick = { incidentSeverity = sev },
                            label = { Text(sev.displayName, fontSize = 10.sp) },
                            modifier = Modifier.heightIn(min = 44.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = locationInput,
                    onValueChange = { locationInput = it },
                    label = { Text("Exact Location in Home") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = descriptionInput,
                    onValueChange = { descriptionInput = it },
                    label = { Text("What Happened? (Description)") },
                    placeholder = { Text("e.g. Slipped off edge of sofa while reaching for walking stick...") },
                    modifier = Modifier.fillMaxWidth().testTag("incident_desc_input"),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = immediateActionsInput,
                    onValueChange = { immediateActionsInput = it },
                    label = { Text("Immediate Clinical / First Aid Actions Taken") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).clickable { paramedicsCalled = !paramedicsCalled }
                ) {
                    Checkbox(checked = paramedicsCalled, onCheckedChange = { paramedicsCalled = it })
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Emergency Ambulance / Paramedics (999) Called", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val report = IncidentReport(
                        id = "inc-" + java.util.UUID.randomUUID().toString().take(6),
                        organisationId = "org-allcarex-01",
                        branchId = "branch-wst",
                        clientId = selectedClientId,
                        clientName = client?.fullName ?: "Client",
                        reportedByStaffId = currentUser.id,
                        reportedByStaffName = currentUser.name,
                        incidentDate = "03/10/2026",
                        incidentTime = "08:30",
                        category = incidentCategory,
                        severity = incidentSeverity,
                        location = locationInput,
                        description = descriptionInput.ifBlank { "Incident reported during carer visit." },
                        immediateActionsTaken = immediateActionsInput,
                        paramedicsCalled = paramedicsCalled,
                        isCqcNotifiable = incidentSeverity == IncidentSeverity.SEVERE || incidentSeverity == IncidentSeverity.CRITICAL,
                        isLocalAuthoritySafeguardingReferral = incidentCategory == IncidentCategory.SAFEGUARDING_CONCERN,
                        dutyOfCandourCompleted = false,
                        investigationStatus = "Open"
                    )
                    repository.logIncident(report)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                modifier = Modifier.heightIn(min = 48.dp).testTag("submit_quick_incident_button")
            ) {
                Text("Submit Incident Report", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.heightIn(min = 48.dp)) {
                Text("Cancel")
            }
        }
    )
}
