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
import com.example.model.*
import com.example.ui.theme.CareMintTeal
import com.example.ui.theme.CareMintTealContainer

@Composable
fun ComplaintsGovernanceView(
    repository: CareRepository
) {
    val complaints by repository.complaintsCompliments.collectAsState()
    val surveys by repository.satisfactionSurveys.collectAsState()
    val notifications by repository.regulatorNotifications.collectAsState()
    val meetings by repository.governanceMeetings.collectAsState()
    val riskRegister by repository.riskRegister.collectAsState()
    val bcp by repository.businessContinuityPlan.collectAsState()
    val youSaidWeDid by repository.youSaidWeDid.collectAsState()

    var activeSubSection by remember { mutableStateOf(0) }
    // 0: Feedback & Surveys
    // 1: Regulator Notifications (CQC & LA)
    // 2: Governance (Meetings, Risk Register & BCP)
    // 3: "You Said, We Did" Board

    var showLogFeedbackDialog by remember { mutableStateOf(false) }
    var showAddRiskDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = activeSubSection,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = CareMintTeal,
            edgePadding = 0.dp,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
        ) {
            Tab(selected = activeSubSection == 0, onClick = { activeSubSection = 0 }, text = { Text("Complaints & Surveys (${complaints.size})", fontSize = 10.sp) })
            Tab(selected = activeSubSection == 1, onClick = { activeSubSection = 1 }, text = { Text("CQC Notifications (${notifications.size})", fontSize = 10.sp) })
            Tab(selected = activeSubSection == 2, onClick = { activeSubSection = 2 }, text = { Text("Governance & Risks (${riskRegister.size})", fontSize = 10.sp) })
            Tab(selected = activeSubSection == 3, onClick = { activeSubSection = 3 }, text = { Text("You Said, We Did (${youSaidWeDid.size})", fontSize = 10.sp) })
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (activeSubSection) {
            0 -> {
                // Complaints, Compliments & Satisfaction Surveys
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
                            Text("Complaints & Compliments Log", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Button(
                                onClick = { showLogFeedbackDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(30.dp).testTag("log_feedback_button")
                            ) {
                                Text("+ Log Feedback", color = Color(0xFF0A1128), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    items(complaints) { item ->
                        ComplaintCard(item = item)
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Annual Satisfaction Surveys", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    items(surveys) { survey ->
                        SurveyCard(survey = survey)
                    }
                }
            }

            1 -> {
                // Regulator Notifications (CQC & LA)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CareMintTealContainer.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("CQC Regulation 18 & Local Authority Section 42 Tracker", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                                Text("Logs statutory submissions for serious injuries, abuse allegations, and deprivation of liberty authorisations.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    items(notifications) { notif ->
                        NotificationCard(notification = notif)
                    }
                }
            }

            2 -> {
                // Governance: Meetings, Risk Register & BCP
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Risk Register
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Dynamic Risk Register (Likelihood x Impact)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Button(
                                onClick = { showAddRiskDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(28.dp).testTag("add_risk_button")
                            ) {
                                Text("+ New Risk", color = Color(0xFF0A1128), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    items(riskRegister) { risk ->
                        RiskCard(risk = risk)
                    }

                    // Business Continuity Plan
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
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
                                    Text(bcp.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                                    Surface(shape = RoundedCornerShape(4.dp), color = CareMintTealContainer) {
                                        Text("${bcp.version} • Tested: ${bcp.lastTestedDate}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CareMintTeal, modifier = Modifier.padding(4.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Emergency Escalation Contact Tree:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                bcp.emergencyContactTree.forEach { c ->
                                    Text("Tier ${c.escalationTier}: ${c.role} - ${c.name} (${c.phone})", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }

                    // Governance Committee Meetings
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Clinical Governance Committee Meetings", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    items(meetings) { m ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(m.meetingType, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(m.date, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text("Chair: ${m.chairName}", fontSize = 10.sp, color = CareMintTeal)
                                Text(m.minutesSummary, fontSize = 10.sp, modifier = Modifier.padding(vertical = 4.dp))
                                m.keyDecisions.forEach { dec ->
                                    Text("• $dec", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // "You Said, We Did" Board
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CareMintTealContainer.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("'You Said, We Did' Co-Production Board", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                                Text("Demonstrates continuous responsive improvement directly driven by client and relative feedback.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    items(youSaidWeDid) { item ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CareMintTeal.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(item.feedbackSource, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                                    Text("Implemented: ${item.dateImplemented}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("💬 YOU SAID:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                                Text(item.youSaid, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)

                                Spacer(modifier = Modifier.height(4.dp))
                                Text("✅ WE DID:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                                Text(item.weDid, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal: Log Feedback / Complaint
    if (showLogFeedbackDialog) {
        var isComp by remember { mutableStateOf(false) }
        var clientName by remember { mutableStateOf("Arthur Pendelton") }
        var category by remember { mutableStateOf("Punctuality") }
        var desc by remember { mutableStateOf("Call arrived 20 minutes late due to traffic delay.") }
        var isCandour by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { showLogFeedbackDialog = false },
            title = { Text("Log Complaint / Compliment", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = !isComp,
                            onClick = { isComp = false },
                            label = { Text("Complaint / Concern", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = isComp,
                            onClick = { isComp = true },
                            label = { Text("Compliment", fontSize = 10.sp) }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = clientName,
                        onValueChange = { clientName = it },
                        label = { Text("Client Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isCandour, onCheckedChange = { isCandour = it })
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Duty of Candour applies (Statutory letter to family)", fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newFeedback = ComplaintCompliment(
                            isCompliment = isComp,
                            clientName = clientName,
                            dateLogged = "03/10/2026",
                            category = category,
                            description = desc,
                            dutyOfCandourApplies = isCandour,
                            dutyOfCandourSatisfied = true,
                            dutyOfCandourLetterRef = if (isCandour) "DOC-2026-091" else null,
                            status = "Resolved",
                            resolutionDate = "03/10/2026"
                        )
                        repository.addComplaintOrCompliment(newFeedback)
                        showLogFeedbackDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal)
                ) {
                    Text("Save Record", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogFeedbackDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Modal: Add Risk Register Item
    if (showAddRiskDialog) {
        var cat by remember { mutableStateOf("Clinical Care") }
        var desc by remember { mutableStateOf("Delayed hospital discharge information transfer") }
        var like by remember { mutableStateOf("2") }
        var imp by remember { mutableStateOf("4") }
        var mit by remember { mutableStateOf("Discharge coordinator liaison meeting every Friday") }
        var owner by remember { mutableStateOf("Priya Patel") }

        AlertDialog(
            onDismissRequest = { showAddRiskDialog = false },
            title = { Text("Add Risk to Register", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(value = cat, onValueChange = { cat = it }, label = { Text("Risk Category") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Risk Description") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(value = like, onValueChange = { like = it }, label = { Text("Likelihood (1-5)") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = imp, onValueChange = { imp = it }, label = { Text("Impact (1-5)") }, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = mit, onValueChange = { mit = it }, label = { Text("Mitigation Controls") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = owner, onValueChange = { owner = it }, label = { Text("Risk Owner") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newRisk = RiskRegisterItem(
                            riskCategory = cat,
                            description = desc,
                            likelihood = like.toIntOrNull() ?: 2,
                            impact = imp.toIntOrNull() ?: 3,
                            mitigations = mit,
                            riskOwner = owner,
                            reviewDate = "01/11/2026"
                        )
                        repository.addRiskRegisterItem(newRisk)
                        showAddRiskDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal)
                ) {
                    Text("Add Risk", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddRiskDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun ComplaintCard(item: ComplaintCompliment) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (item.isCompliment) CareMintTeal.copy(alpha = 0.4f) else Color(0xFFEF4444).copy(alpha = 0.4f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (item.isCompliment) Icons.Default.ThumbUp else Icons.Default.ReportProblem,
                        contentDescription = null,
                        tint = if (item.isCompliment) CareMintTeal else Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (item.isCompliment) "Compliment" else "Complaint / Concern",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isCompliment) CareMintTeal else Color(0xFFEF4444)
                    )
                }
                Text(item.dateLogged, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(text = "Client: ${item.clientName} • Category: ${item.category}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Text(text = item.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(vertical = 4.dp))

            if (item.dutyOfCandourApplies) {
                Surface(shape = RoundedCornerShape(4.dp), color = CareMintTealContainer) {
                    Text(
                        text = "✓ Statutory Duty of Candour satisfied (Ref: ${item.dutyOfCandourLetterRef ?: "DOC-991"})",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = CareMintTeal,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
            if (!item.lessonsLearned.isNullOrBlank()) {
                Text(text = "Lessons Learned: ${item.lessonsLearned}", fontSize = 10.sp, color = Color(0xFFF59E0B), modifier = Modifier.padding(top = 2.dp))
            }
        }
    }
}

@Composable
private fun SurveyCard(survey: SatisfactionSurveyResult) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(survey.title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("${survey.overallSatisfactionPercent}% Satisfaction", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = CareMintTeal)
            }
            Text("Respondents: ${survey.respondentsCount} • Period: ${survey.surveyPeriod}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            survey.keyFindings.forEach { f ->
                Text("• $f", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun NotificationCard(notification: RegulatorNotification) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(notification.notificationType, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                Surface(shape = RoundedCornerShape(4.dp), color = CareMintTealContainer) {
                    Text(notification.status, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CareMintTeal, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                }
            }
            Text("Subject: ${notification.clientOrStaffName} • Ref: ${notification.submissionReference} • Submitted: ${notification.submissionDate}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(notification.summaryDetails, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(vertical = 4.dp))
            Text("Statutory Requirement: ${notification.statutoryDeadline}", fontSize = 9.sp, color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RiskCard(risk: RiskRegisterItem) {
    val levelColor = when {
        risk.riskScore >= 15 -> Color(0xFFEF4444)
        risk.riskScore >= 8 -> Color(0xFFF59E0B)
        else -> Color(0xFF10B981)
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, levelColor.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${risk.riskCategory}: ${risk.description}", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Surface(shape = RoundedCornerShape(4.dp), color = levelColor.copy(alpha = 0.2f)) {
                    Text(
                        text = "Score ${risk.riskScore} (${risk.likelihood}x${risk.impact})",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = levelColor,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            Text("Mitigation: ${risk.mitigations}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(vertical = 2.dp))
            Text("Owner: ${risk.riskOwner} • Review: ${risk.reviewDate}", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
