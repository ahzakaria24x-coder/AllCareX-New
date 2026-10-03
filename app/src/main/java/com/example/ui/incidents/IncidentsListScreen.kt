package com.example.ui.incidents

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
import com.example.model.IncidentCategory
import com.example.model.IncidentRecord
import com.example.model.IncidentSeverity
import com.example.ui.common.NeutralPill
import com.example.ui.common.StatusPill
import com.example.ui.theme.*

@Composable
fun IncidentsListScreen(
    repository: CareRepository
) {
    val incidents by repository.incidents.collectAsState()
    val clients by repository.clients.collectAsState()

    var showLogModal by remember { mutableStateOf(false) }

    // Modal state
    var selectedClientId by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("fall") }
    var selectedSeverity by remember { mutableStateOf("medium") }
    var incidentDate by remember { mutableStateOf("01/10/2026") }
    var isCqcNotifiable by remember { mutableStateOf(false) }
    var incidentDescription by remember { mutableStateOf("") }
    var incidentActions by remember { mutableStateOf("") }
    var clientDropdownOpen by remember { mutableStateOf(false) }
    var categoryDropdownOpen by remember { mutableStateOf(false) }
    var severityDropdownOpen by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(BasePageBg)) {
        val isDesktop = maxWidth >= 1024.dp
        val contentPadding = if (isDesktop) 32.dp else 16.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .testTag("incidents_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Header Row: Title & Subtitle + Action Button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Incidents",
                            fontSize = if (isDesktop) 30.sp else 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = BaseTextHeading
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Log, investigate and resolve safeguarding and clinical incidents.",
                            fontSize = 14.sp,
                            color = BaseTextMuted
                        )
                    }

                    Button(
                        onClick = { showLogModal = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E)),
                        modifier = Modifier.height(44.dp).testTag("log_incident_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log incident", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }

            // Incident cards
            items(incidents) { incident ->
                val client = clients.find { it.id == incident.clientId }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BaseSurface)
                        .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    // Header row: Client name, Category, Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(StatusCritBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = StatusCritText, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${client?.fullName ?: "Doris Penrose"} • ${incident.category.displayName}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BaseTextHeading
                                )
                                Text(
                                    text = "${incident.timestamp.split(" ").first()} • reported by ${incident.reportedByStaffName}",
                                    fontSize = 12.sp,
                                    color = BaseTextMuted
                                )
                            }
                        }

                        // Badges Row: Severity, CQC, Actioned/Closed
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            StatusPill(
                                status = incident.severity.name.lowercase(),
                                color = when (incident.severity) {
                                    IncidentSeverity.CRITICAL -> StatusCritText
                                    IncidentSeverity.HIGH -> StatusHighText
                                    IncidentSeverity.MEDIUM -> StatusMedText
                                    else -> StatusLowText
                                }
                            )
                            if (incident.isCqcNotifiable) {
                                StatusPill(status = "CQC", color = StatusVioletText)
                            }
                            NeutralPill(text = if (incident.status == "CLOSED" || incident.investigationStatus == "Closed") "Closed" else "Actioned")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Description text
                    Text(
                        text = incident.description,
                        fontSize = 14.sp,
                        color = BaseTextBody,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Actions taken
                    Text(
                        text = "Actions: ${incident.immediateActionTaken.ifEmpty { "GP notified, clinical review initiated." }}",
                        fontSize = 13.sp,
                        color = BaseTextMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = { /* Close or update */ },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Close", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Modal: Log an Incident
        if (showLogModal) {
            AlertDialog(
                onDismissRequest = { showLogModal = false },
                containerColor = BaseModalBg,
                shape = RoundedCornerShape(16.dp),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Log an Incident", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                        IconButton(onClick = { showLogModal = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = BaseTextMuted)
                        }
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Client dropdown
                        Column {
                            Text("Client", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = clients.find { it.id == selectedClientId }?.fullName ?: "Select client",
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        IconButton(onClick = { clientDropdownOpen = true }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = BaseTextMuted)
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = BaseModalInput,
                                        unfocusedContainerColor = BaseModalInput,
                                        focusedTextColor = BaseTextHeading,
                                        unfocusedTextColor = BaseTextHeading,
                                        focusedBorderColor = BaseEmerald500,
                                        unfocusedBorderColor = Color(0xFF334155)
                                    ),
                                    modifier = Modifier.fillMaxWidth().clickable { clientDropdownOpen = true }
                                )
                                DropdownMenu(
                                    expanded = clientDropdownOpen,
                                    onDismissRequest = { clientDropdownOpen = false },
                                    modifier = Modifier.background(BaseModalBg)
                                ) {
                                    clients.forEach { c ->
                                        DropdownMenuItem(
                                            text = { Text(c.fullName, color = BaseTextHeading) },
                                            onClick = {
                                                selectedClientId = c.id
                                                clientDropdownOpen = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Date and Category Row
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Date", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = incidentDate,
                                    onValueChange = { incidentDate = it },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = BaseModalInput,
                                        unfocusedContainerColor = BaseModalInput,
                                        focusedTextColor = BaseTextHeading,
                                        unfocusedTextColor = BaseTextHeading,
                                        focusedBorderColor = BaseEmerald500,
                                        unfocusedBorderColor = Color(0xFF334155)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Category", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = selectedCategory,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = {
                                            IconButton(onClick = { categoryDropdownOpen = true }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = BaseTextMuted)
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = BaseModalInput,
                                            unfocusedContainerColor = BaseModalInput,
                                            focusedTextColor = BaseTextHeading,
                                            unfocusedTextColor = BaseTextHeading,
                                            focusedBorderColor = BaseEmerald500,
                                            unfocusedBorderColor = Color(0xFF334155)
                                        ),
                                        modifier = Modifier.fillMaxWidth().clickable { categoryDropdownOpen = true }
                                    )
                                    DropdownMenu(
                                        expanded = categoryDropdownOpen,
                                        onDismissRequest = { categoryDropdownOpen = false },
                                        modifier = Modifier.background(BaseModalBg)
                                    ) {
                                        listOf("fall", "medication error", "aggression", "abscond", "clinical deterioration", "safeguarding", "other").forEach { cat ->
                                            DropdownMenuItem(
                                                text = { Text(cat, color = BaseTextHeading) },
                                                onClick = {
                                                    selectedCategory = cat
                                                    categoryDropdownOpen = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Severity and CQC Notifiable Row
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Severity", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = selectedSeverity,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = {
                                            IconButton(onClick = { severityDropdownOpen = true }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = BaseTextMuted)
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = BaseModalInput,
                                            unfocusedContainerColor = BaseModalInput,
                                            focusedTextColor = BaseTextHeading,
                                            unfocusedTextColor = BaseTextHeading,
                                            focusedBorderColor = BaseEmerald500,
                                            unfocusedBorderColor = Color(0xFF334155)
                                        ),
                                        modifier = Modifier.fillMaxWidth().clickable { severityDropdownOpen = true }
                                    )
                                    DropdownMenu(
                                        expanded = severityDropdownOpen,
                                        onDismissRequest = { severityDropdownOpen = false },
                                        modifier = Modifier.background(BaseModalBg)
                                    ) {
                                        listOf("low", "medium", "high", "critical").forEach { s ->
                                            DropdownMenuItem(
                                                text = { Text(s, color = BaseTextHeading) },
                                                onClick = {
                                                    selectedSeverity = s
                                                    severityDropdownOpen = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f).padding(top = 20.dp)) {
                                Checkbox(
                                    checked = isCqcNotifiable,
                                    onCheckedChange = { isCqcNotifiable = it },
                                    colors = CheckboxDefaults.colors(checkedColor = BaseEmerald500)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("CQC notifiable", fontSize = 13.sp, color = BaseTextHeading)
                            }
                        }

                        // Description
                        Column {
                            Text("Description", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = incidentDescription,
                                onValueChange = { incidentDescription = it },
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = BaseModalInput,
                                    unfocusedContainerColor = BaseModalInput,
                                    focusedTextColor = BaseTextHeading,
                                    unfocusedTextColor = BaseTextHeading,
                                    focusedBorderColor = BaseEmerald500,
                                    unfocusedBorderColor = Color(0xFF334155)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Actions taken
                        Column {
                            Text("Actions taken", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = incidentActions,
                                onValueChange = { incidentActions = it },
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = BaseModalInput,
                                    unfocusedContainerColor = BaseModalInput,
                                    focusedTextColor = BaseTextHeading,
                                    unfocusedTextColor = BaseTextHeading,
                                    focusedBorderColor = BaseEmerald500,
                                    unfocusedBorderColor = Color(0xFF334155)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val client = clients.find { it.id == selectedClientId } ?: clients.first()
                            val newInc = IncidentRecord(
                                organisationId = "org-allcarex-01",
                                branchId = client.branchId,
                                clientId = client.id,
                                clientName = client.fullName,
                                reportedByStaffId = "staff-01",
                                reportedByStaffName = "Aisha Khan",
                                incidentDate = incidentDate,
                                incidentTime = "10:00",
                                category = when (selectedCategory) {
                                    "medication error" -> IncidentCategory.MEDICATION_ERROR
                                    "clinical deterioration" -> IncidentCategory.CLINICAL_DETERIORATION
                                    "safeguarding" -> IncidentCategory.SAFEGUARDING_CONCERN
                                    else -> IncidentCategory.FALL
                                },
                                severity = when (selectedSeverity) {
                                    "critical" -> IncidentSeverity.CRITICAL
                                    "high" -> IncidentSeverity.HIGH
                                    "low" -> IncidentSeverity.LOW
                                    else -> IncidentSeverity.MEDIUM
                                },
                                description = incidentDescription.ifEmpty { "Incident reported." },
                                immediateActionsTaken = incidentActions.ifEmpty { "GP notified, clinical assessment conducted." },
                                isCqcNotifiable = isCqcNotifiable
                            )
                            repository.logIncident(newInc)
                            showLogModal = false
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E)),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("Log incident", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showLogModal = false },
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("Cancel", fontSize = 14.sp, color = BaseTextMuted)
                    }
                }
            )
        }
    }
}
