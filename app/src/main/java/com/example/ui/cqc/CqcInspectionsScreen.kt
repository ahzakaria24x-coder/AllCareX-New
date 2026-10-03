package com.example.ui.cqc

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
import com.example.ui.common.NeutralPill
import com.example.ui.common.StatusPill
import com.example.ui.theme.*

data class MockInspectionItem(
    val id: String,
    val title: String,
    val date: String,
    val lead: String,
    val status: String,
    val overallScore: Int
)

@Composable
fun CqcInspectionsScreen(
    repository: CareRepository
) {
    var inspections by remember { mutableStateOf(listOf<MockInspectionItem>()) }
    var showNewInspectionModal by remember { mutableStateOf(false) }

    // Modal state
    var inspectionTitle by remember { mutableStateOf("Q3 2026 Self-Assessment") }
    var inspectionDate by remember { mutableStateOf("01/10/2026") }
    var inspectionLead by remember { mutableStateOf("") }
    var inspectionStatus by remember { mutableStateOf("scheduled") }
    var statusDropdownOpen by remember { mutableStateOf(false) }

    var safeScore by remember { mutableStateOf("0") }
    var safeEvidence by remember { mutableStateOf("") }
    var effectiveScore by remember { mutableStateOf("0") }
    var effectiveEvidence by remember { mutableStateOf("") }
    var caringScore by remember { mutableStateOf("0") }
    var caringEvidence by remember { mutableStateOf("") }
    var responsiveScore by remember { mutableStateOf("0") }
    var responsiveEvidence by remember { mutableStateOf("") }
    var wellLedScore by remember { mutableStateOf("0") }
    var wellLedEvidence by remember { mutableStateOf("") }

    var overallFindings by remember { mutableStateOf("") }
    var actionPlan by remember { mutableStateOf("") }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(BasePageBg)) {
        val isDesktop = maxWidth >= 1024.dp
        val contentPadding = if (isDesktop) 32.dp else 16.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .testTag("cqc_inspections_screen"),
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
                            text = "Mock CQC Inspections",
                            fontSize = if (isDesktop) 30.sp else 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = BaseTextHeading
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Self-assessment against the CQC Single Assessment Framework — Quality Statement evidence scoring.",
                            fontSize = 14.sp,
                            color = BaseTextMuted
                        )
                    }

                    Button(
                        onClick = { showNewInspectionModal = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                        modifier = Modifier.height(44.dp).testTag("new_inspection_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("New inspection", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }

            // List of Inspections or Empty State
            if (inspections.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(BaseSurface)
                            .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
                            .padding(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No inspections recorded. Create a mock CQC self-assessment to score your Quality Statements.",
                            fontSize = 14.sp,
                            color = BaseTextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(inspections) { insp ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(BaseSurface)
                            .border(1.dp, BaseBorder, RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(insp.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                            Text("${insp.date} • Lead: ${insp.lead.ifEmpty { "Registered Manager" }}", fontSize = 13.sp, color = BaseTextMuted)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Score: ${insp.overallScore}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BaseEmerald300)
                            StatusPill(status = insp.status, color = BaseEmerald300)
                        }
                    }
                }
            }
        }

        // Modal: New Mock CQC Inspection
        if (showNewInspectionModal) {
            AlertDialog(
                onDismissRequest = { showNewInspectionModal = false },
                containerColor = BaseModalBg,
                shape = RoundedCornerShape(16.dp),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("New Mock CQC Inspection", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                        IconButton(onClick = { showNewInspectionModal = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = BaseTextMuted)
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 480.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Title and Date
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Title", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = inspectionTitle,
                                    onValueChange = { inspectionTitle = it },
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
                                Text("Date", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = inspectionDate,
                                    onValueChange = { inspectionDate = it },
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

                        // Inspector / Lead & Status
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Inspector / Lead", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = inspectionLead,
                                    onValueChange = { inspectionLead = it },
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
                                Text("Status", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = inspectionStatus,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = {
                                            IconButton(onClick = { statusDropdownOpen = true }) {
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
                                        modifier = Modifier.fillMaxWidth().clickable { statusDropdownOpen = true }
                                    )
                                    DropdownMenu(
                                        expanded = statusDropdownOpen,
                                        onDismissRequest = { statusDropdownOpen = false },
                                        modifier = Modifier.background(BaseModalBg)
                                    ) {
                                        listOf("scheduled", "completed", "in review").forEach { st ->
                                            DropdownMenuItem(
                                                text = { Text(st, color = BaseTextHeading) },
                                                onClick = {
                                                    inspectionStatus = st
                                                    statusDropdownOpen = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Quality Statement Evidence Scoring Header
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = StatusMedText, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Quality Statement Evidence Scoring", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                        }

                        // 5 Evidence items
                        CqcScoringInputItem("Safe", "People protected from abuse and avoidable harm", safeScore, { safeScore = it }, safeEvidence, { safeEvidence = it })
                        CqcScoringInputItem("Effective", "Care achieves good outcomes, promotes wellbeing", effectiveScore, { effectiveScore = it }, effectiveEvidence, { effectiveEvidence = it })
                        CqcScoringInputItem("Caring", "Staff involve and treat people with compassion", caringScore, { caringScore = it }, caringEvidence, { caringEvidence = it })
                        CqcScoringInputItem("Responsive", "Services organised to meet people's needs", responsiveScore, { responsiveScore = it }, responsiveEvidence, { responsiveEvidence = it })
                        CqcScoringInputItem("Well-led", "Leadership, governance and culture support care", wellLedScore, { wellLedScore = it }, wellLedEvidence, { wellLedEvidence = it })

                        // Overall findings
                        Column {
                            Text("Overall findings", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = overallFindings,
                                onValueChange = { overallFindings = it },
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

                        // Action plan
                        Column {
                            Text("Action plan", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = actionPlan,
                                onValueChange = { actionPlan = it },
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
                            inspections = inspections + MockInspectionItem(
                                id = "mock-${System.currentTimeMillis()}",
                                title = inspectionTitle.ifEmpty { "Mock CQC Inspection" },
                                date = inspectionDate,
                                lead = inspectionLead,
                                status = inspectionStatus,
                                overallScore = 91
                            )
                            showNewInspectionModal = false
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("Create inspection", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showNewInspectionModal = false },
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("Cancel", fontSize = 14.sp, color = BaseTextMuted)
                    }
                }
            )
        }
    }
}

@Composable
private fun CqcScoringInputItem(
    title: String,
    subtitle: String,
    score: String,
    onScoreChange: (String) -> Unit,
    evidence: String,
    onEvidenceChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF131B2E))
            .border(1.dp, Color(0xFF1E283F), RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                Text(subtitle, fontSize = 11.sp, color = BaseTextMuted)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = score,
                    onValueChange = onScoreChange,
                    modifier = Modifier.width(60.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BaseModalInput,
                        unfocusedContainerColor = BaseModalInput,
                        focusedTextColor = BaseTextHeading,
                        unfocusedTextColor = BaseTextHeading,
                        focusedBorderColor = BaseEmerald500,
                        unfocusedBorderColor = Color(0xFF334155)
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("%", fontSize = 13.sp, color = BaseTextHeading)
            }
        }

        OutlinedTextField(
            value = evidence,
            onValueChange = onEvidenceChange,
            placeholder = { Text("Evidence: what proves this score? (policies, audits, records...)", color = BaseTextFaint, fontSize = 11.sp) },
            shape = RoundedCornerShape(8.dp),
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
