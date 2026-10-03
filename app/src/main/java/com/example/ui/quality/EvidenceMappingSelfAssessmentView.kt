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
import com.example.model.EvidenceItem
import com.example.model.EvidenceSourceType
import com.example.model.QualityStatementOrKloe
import com.example.ui.theme.CareMintTeal
import com.example.ui.theme.CareMintTealContainer

@Composable
fun EvidenceMappingSelfAssessmentView(
    repository: CareRepository
) {
    val frameworks by repository.frameworks.collectAsState()
    val evidenceItems by repository.evidenceItems.collectAsState()

    val activeFramework = frameworks.find { it.isActive } ?: frameworks.firstOrNull()
    var selectedKeyQuestionId by remember { mutableStateOf("SAFE") }
    var selectedStatementForScoring by remember { mutableStateOf<QualityStatementOrKloe?>(null) }
    var showAddEvidenceDialog by remember { mutableStateOf<QualityStatementOrKloe?>(null) }

    if (activeFramework == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active framework found. Please activate one in Framework Manager.", fontSize = 13.sp)
        }
        return
    }

    val currentKq = activeFramework.keyQuestions.find { it.id == selectedKeyQuestionId }
        ?: activeFramework.keyQuestions.firstOrNull()

    Column(modifier = Modifier.fillMaxSize()) {
        // Disclaimer & Readiness Summary Banner
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
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = CareMintTeal, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "INTERNAL SELF-ASSESSMENT & READINESS VIEW",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CareMintTeal
                            )
                        }
                        Text(
                            text = "This dashboard displays internal self-assessment scores and automated platform evidence. It is NOT an official regulator rating.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "READINESS: ${currentKq?.ratingLabel?.uppercase() ?: "GOOD"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF10B981),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Key Questions Filter Row (Safe, Effective, Caring, Responsive, Well-led)
        ScrollableTabRow(
            selectedTabIndex = activeFramework.keyQuestions.indexOfFirst { it.id == selectedKeyQuestionId }.coerceAtLeast(0),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = CareMintTeal,
            edgePadding = 0.dp,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
        ) {
            activeFramework.keyQuestions.forEach { kq ->
                Tab(
                    selected = selectedKeyQuestionId == kq.id,
                    onClick = { selectedKeyQuestionId = kq.id },
                    text = {
                        Text(
                            text = "${kq.name} (${"%.1f".format(kq.averageScore)}/4)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Statements List under selected Key Question
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val statements = currentKq?.statements ?: emptyList()

            items(statements) { statement ->
                val linkedEv = evidenceItems.filter { it.statementId == statement.id }
                val hasGaps = linkedEv.any { it.isGap }

                StatementEvidenceCard(
                    statement = statement,
                    evidenceList = linkedEv,
                    hasGaps = hasGaps,
                    onScoreClick = { selectedStatementForScoring = statement },
                    onAddEvidence = { showAddEvidenceDialog = statement }
                )
            }
        }
    }

    // Modal: Score Statement & Self-Assessment Notes
    selectedStatementForScoring?.let { stmt ->
        var score by remember { mutableStateOf(stmt.currentSelfAssessmentScore) }
        var narrative by remember { mutableStateOf(stmt.selfAssessmentNarrative) }

        val scoreLabels = listOf(
            1 to "Inadequate",
            2 to "Requires Improvement",
            3 to "Good",
            4 to "Outstanding"
        )

        AlertDialog(
            onDismissRequest = { selectedStatementForScoring = null },
            title = { Text("Self-Assess: ${stmt.code} ${stmt.title}", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(stmt.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Rating Characteristics & Criteria:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    stmt.ratingCharacteristics.forEach { (sc, text) ->
                        Text(text = "• $sc: $text", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Self-Assessment Score", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    scoreLabels.forEach { (scVal, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { score = scVal }
                                .padding(vertical = 2.dp)
                        ) {
                            RadioButton(
                                selected = score == scVal,
                                onClick = { score = scVal },
                                colors = RadioButtonDefaults.colors(selectedColor = CareMintTeal)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "$scVal - $label", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = narrative,
                        onValueChange = { narrative = it },
                        label = { Text("Evidence Narrative & Inspection Notes") },
                        modifier = Modifier.fillMaxWidth().testTag("self_assessment_notes_input"),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.updateSelfAssessmentScore(activeFramework.id, stmt.id, score, narrative)
                        selectedStatementForScoring = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                    modifier = Modifier.testTag("save_statement_score_button")
                ) {
                    Text("Save Self-Assessment", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedStatementForScoring = null }) { Text("Cancel") }
            }
        )
    }

    // Modal: Add Evidence Item
    showAddEvidenceDialog?.let { stmt ->
        var evTitle by remember { mutableStateOf("Quarterly Relatives Forum Feedback Summary") }
        var evDesc by remember { mutableStateOf("Documented minutes with 12 attendees reviewing menu choices and tea times.") }
        var evSource by remember { mutableStateOf(EvidenceSourceType.MANUAL_UPLOAD) }
        var isGap by remember { mutableStateOf(false) }
        var gapPlan by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddEvidenceDialog = null },
            title = { Text("Link Evidence to ${stmt.code}", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Upload document, note, or platform observation to satisfy regulatory requirements.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = evTitle,
                        onValueChange = { evTitle = it },
                        label = { Text("Evidence Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = evDesc,
                        onValueChange = { evDesc = it },
                        label = { Text("Description & Findings") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isGap,
                            onCheckedChange = { isGap = it }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Flag as an Evidence Gap / Weakness", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isGap) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface)
                    }

                    if (isGap) {
                        OutlinedTextField(
                            value = gapPlan,
                            onValueChange = { gapPlan = it },
                            label = { Text("Remediation Action Plan") },
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newEv = EvidenceItem(
                            statementId = stmt.id,
                            title = evTitle,
                            sourceType = evSource,
                            description = evDesc,
                            dateRecorded = "03/10/2026",
                            freshnessRating = "Fresh",
                            isGap = isGap,
                            gapRemediationPlan = if (isGap) gapPlan else null
                        )
                        repository.addEvidenceItem(newEv)
                        showAddEvidenceDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal)
                ) {
                    Text("Attach Evidence", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddEvidenceDialog = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun StatementEvidenceCard(
    statement: QualityStatementOrKloe,
    evidenceList: List<EvidenceItem>,
    hasGaps: Boolean,
    onScoreClick: () -> Unit,
    onAddEvidence: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val scoreColor = when (statement.currentSelfAssessmentScore) {
        4 -> CareMintTeal
        3 -> Color(0xFF10B981)
        2 -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }

    val scoreLabel = when (statement.currentSelfAssessmentScore) {
        4 -> "Outstanding"
        3 -> "Good"
        2 -> "Requires Improvement"
        else -> "Inadequate"
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (hasGaps) Color(0xFFEF4444).copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
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
                    Text(text = "${statement.code}: ${statement.title}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = statement.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = scoreColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "$scoreLabel (${statement.currentSelfAssessmentScore}/4)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = scoreColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Evidence Count, Freshness, and Gap Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${evidenceList.size} Evidence Items Linked",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CareMintTeal
                    )
                    if (hasGaps) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFEF4444).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "GAP IDENTIFIED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Text(
                    text = if (expanded) "Hide Evidence ▲" else "View Evidence ▼",
                    fontSize = 10.sp,
                    color = CareMintTeal,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(6.dp))

                if (statement.selfAssessmentNarrative.isNotBlank()) {
                    Text(
                        text = "Operational Narrative: ${statement.selfAssessmentNarrative}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                evidenceList.forEach { ev ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = ev.title, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(text = "${ev.sourceType.displayName} • ${ev.freshnessRating}", fontSize = 9.sp, color = CareMintTeal)
                            }
                            Text(text = ev.description, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                            if (ev.metricValue != null) {
                                Text(text = "Metric: ${ev.metricValue}", fontSize = 10.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                            }
                            if (ev.isGap && ev.gapRemediationPlan != null) {
                                Text(text = "Action Plan: ${ev.gapRemediationPlan}", fontSize = 10.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onAddEvidence,
                        modifier = Modifier.weight(1f).height(30.dp)
                    ) {
                        Text("+ Link Evidence", fontSize = 10.sp)
                    }
                    Button(
                        onClick = onScoreClick,
                        colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                        modifier = Modifier.weight(1f).height(30.dp)
                    ) {
                        Text("Self-Assess Score", color = Color(0xFF0A1128), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
