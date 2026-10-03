package com.example.ui.quality

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
import com.example.model.AuditActionItem
import com.example.model.AuditCheckItem
import com.example.model.AuditTemplate
import com.example.model.AuditType
import com.example.ui.theme.CareMintTeal
import com.example.ui.theme.CareMintTealContainer
import java.util.UUID

@Composable
fun AuditsManagerView(
    repository: CareRepository
) {
    val auditTemplates by repository.auditTemplates.collectAsState()

    var showCreateAuditDialog by remember { mutableStateOf(false) }
    var selectedAuditForConduct by remember { mutableStateOf<AuditTemplate?>(null) }
    var filterType by remember { mutableStateOf<AuditType?>(null) }

    val filteredAudits = auditTemplates.filter { filterType == null || it.auditType == filterType }

    Column(modifier = Modifier.fillMaxSize()) {
        // Actions & Filters Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Clinical & Quality Audit Schedules (${auditTemplates.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Covers all 7 core inspection domains with pass/fail checklists and SMART action plans",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { showCreateAuditDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(32.dp).testTag("new_audit_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color(0xFF0A1128), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("+ New Audit", color = Color(0xFF0A1128), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Domain Filter Chips
        ScrollableTabRow(
            selectedTabIndex = if (filterType == null) 0 else AuditType.values().indexOf(filterType) + 1,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = CareMintTeal,
            edgePadding = 0.dp,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
        ) {
            Tab(selected = filterType == null, onClick = { filterType = null }, text = { Text("All", fontSize = 10.sp) })
            AuditType.values().forEach { type ->
                Tab(
                    selected = filterType == type,
                    onClick = { filterType = type },
                    text = { Text(type.name.replace("_", " "), fontSize = 10.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Audits List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredAudits) { audit ->
                AuditCard(
                    audit = audit,
                    onConductAudit = { selectedAuditForConduct = audit },
                    onResolveAction = { actionId ->
                        repository.updateAuditActionStatus(actionId, "Resolved", "Resolved during manager review")
                    }
                )
            }
        }
    }

    // Modal: Conduct or Complete Audit with Pass/Fail Checklist
    selectedAuditForConduct?.let { audit ->
        var overallRag by remember { mutableStateOf(audit.overallRagStatus) }
        var auditorInput by remember { mutableStateOf(audit.auditorName) }

        // Local copy of check items for live checking
        var checkItems by remember {
            mutableStateOf(
                if (audit.checkItems.isNotEmpty()) audit.checkItems else listOf(
                    AuditCheckItem(category = "Records", question = "Are all records signed with timestamps?"),
                    AuditCheckItem(category = "Safety", question = "Zero unlabelled containers or loose medicines?"),
                    AuditCheckItem(category = "Governance", question = "Are escalations reported to registered manager?")
                )
            )
        }

        AlertDialog(
            onDismissRequest = { selectedAuditForConduct = null },
            title = { Text("Conduct: ${audit.title}", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Complete individual audit questions. Failed items trigger action plan creation.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(10.dp))

                    checkItems.forEachIndexed { idx, item ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(item.question, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (item.passed) "STATUS: PASS" else "STATUS: FAIL (Action Triggered)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.passed) Color(0xFF10B981) else Color(0xFFEF4444)
                                    )
                                    Switch(
                                        checked = item.passed,
                                        onCheckedChange = { isPass ->
                                            checkItems = checkItems.toMutableList().also {
                                                it[idx] = item.copy(passed = isPass)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Overall Audit RAG Rating", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("GREEN", "AMBER", "RED").forEach { rag ->
                            FilterChip(
                                selected = overallRag == rag,
                                onClick = { overallRag = rag },
                                label = { Text(rag, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val passCount = checkItems.count { it.passed }
                        val pct = if (checkItems.isNotEmpty()) (passCount.toDouble() / checkItems.size) * 100.0 else 90.0
                        val updatedAudit = audit.copy(
                            lastConductedDate = "03/10/2026",
                            overallRagStatus = overallRag,
                            overallScorePercent = pct,
                            checkItems = checkItems
                        )
                        repository.addAuditTemplate(updatedAudit)
                        selectedAuditForConduct = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                    modifier = Modifier.testTag("submit_conducted_audit_button")
                ) {
                    Text("Save Audit Result", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedAuditForConduct = null }) { Text("Cancel") }
            }
        )
    }

    // Modal: Create New Audit Template
    if (showCreateAuditDialog) {
        var title by remember { mutableStateOf("Mealtime & Nutrition MUST Audit") }
        var type by remember { mutableStateOf(AuditType.MEALTIME_DINING) }
        var freq by remember { mutableStateOf("Monthly") }
        var auditor by remember { mutableStateOf("Eleanor Vance") }

        AlertDialog(
            onDismissRequest = { showCreateAuditDialog = false },
            title = { Text("Schedule New Audit", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Audit Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = auditor,
                        onValueChange = { auditor = it },
                        label = { Text("Lead Auditor") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = freq,
                        onValueChange = { freq = it },
                        label = { Text("Frequency (Weekly / Monthly / Quarterly)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newAudit = AuditTemplate(
                            id = "aud-" + UUID.randomUUID().toString().take(6),
                            title = title,
                            auditType = type,
                            frequency = freq,
                            lastConductedDate = "03/10/2026",
                            auditorName = auditor,
                            overallRagStatus = "GREEN",
                            overallScorePercent = 95.0
                        )
                        repository.addAuditTemplate(newAudit)
                        showCreateAuditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal)
                ) {
                    Text("Schedule Audit", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateAuditDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun AuditCard(
    audit: AuditTemplate,
    onConductAudit: () -> Unit,
    onResolveAction: (String) -> Unit
) {
    val ragColor = when (audit.overallRagStatus) {
        "GREEN" -> Color(0xFF10B981)
        "AMBER" -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = audit.title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Conducted: ${audit.lastConductedDate} • Next: ${audit.nextDueDate} • Lead: ${audit.auditorName}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ragColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "RAG: ${audit.overallRagStatus} (${"%.0f".format(audit.overallScorePercent)}%)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ragColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Historical Trend
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Trend: ", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                audit.historicalScores.forEach { (month, score) ->
                    Text(
                        text = "$month: ${"%.0f".format(score)}%  ",
                        fontSize = 10.sp,
                        color = CareMintTeal,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // SMART Action Items
            if (audit.actionItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(6.dp))
                Text("SMART ACTION PLAN (${audit.actionItems.size}):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CareMintTeal)

                audit.actionItems.forEach { act ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(act.title, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (act.status == "Resolved") Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFFF59E0B).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = act.status,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (act.status == "Resolved") Color(0xFF10B981) else Color(0xFFF59E0B),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(text = "Action: ${act.smartActionRequired}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(text = "Owner: ${act.ownerName} • Target: ${act.targetDate}", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            if (act.status != "Resolved") {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                    TextButton(
                                        onClick = { onResolveAction(act.id) },
                                        modifier = Modifier.height(24.dp)
                                    ) {
                                        Text("Mark Resolved ✓", fontSize = 10.sp, color = CareMintTeal)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = onConductAudit,
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(30.dp).testTag("conduct_audit_${audit.id}")
                ) {
                    Text("Conduct Audit", color = Color(0xFF0A1128), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
