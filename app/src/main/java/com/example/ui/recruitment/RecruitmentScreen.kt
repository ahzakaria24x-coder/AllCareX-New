package com.example.ui.recruitment

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
fun RecruitmentScreen(
    repository: CareRepository
) {
    val vacancies by repository.vacancies.collectAsState()
    val candidates by repository.candidates.collectAsState()

    var activeSubTab by remember { mutableStateOf(0) } // 0: Candidates Pipeline, 1: Active Vacancies
    var selectedCandidateForDetail by remember { mutableStateOf<Candidate?>(null) }
    var showInterviewDialog by remember { mutableStateOf(false) }

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
                    text = "Recruitment & Onboarding",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Safer recruitment, Right-to-Work, DBS tracking & staff conversion",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs
        TabRow(
            selectedTabIndex = activeSubTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = CareMintTeal,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
        ) {
            Tab(selected = activeSubTab == 0, onClick = { activeSubTab = 0 }, text = { Text("Applicant Pipeline (${candidates.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = activeSubTab == 1, onClick = { activeSubTab = 1 }, text = { Text("Open Vacancies (${vacancies.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (activeSubTab) {
            0 -> {
                // Candidates Pipeline List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(candidates) { candidate ->
                        CandidateCard(
                            candidate = candidate,
                            onClick = { selectedCandidateForDetail = candidate },
                            onInterviewClick = {
                                selectedCandidateForDetail = candidate
                                showInterviewDialog = true
                            }
                        )
                    }
                }
            }

            1 -> {
                // Vacancies List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(vacancies) { vacancy ->
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
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = vacancy.title, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Branch: ${vacancy.branchId} • ${vacancy.contractType.displayName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(text = "Pay: £${vacancy.hourlyRate}/hr • NLW Compliant", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = CareMintTeal)
                                    }
                                    Surface(shape = RoundedCornerShape(6.dp), color = CareMintTealContainer) {
                                        Text(
                                            text = "${vacancy.applicantsCount} Applicants",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CareMintTeal,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = vacancy.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal: Candidate Vetting & Onboarding Checklist Dialog
    selectedCandidateForDetail?.let { candidate ->
        if (!showInterviewDialog) {
            val checklist = candidate.onboarding
            val isReadyToConvert = checklist.isReadyForStaffProfile()

            AlertDialog(
                onDismissRequest = { selectedCandidateForDetail = null },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = candidate.fullName, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(candidate.stage.colorHex).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = candidate.stage.displayName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(candidate.stage.colorHex),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text("Contact: ${candidate.email} • ${candidate.phone}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Applied: ${candidate.appliedDate} • Offer: £${candidate.offerHourlyRate}/hr", fontSize = 11.sp, color = CareMintTeal, fontWeight = FontWeight.Bold)
                        Text("CV Summary: ${candidate.cvSummary}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(vertical = 4.dp))

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))

                        // Safer Recruitment Checks
                        Text("SAFER RECRUITMENT & STATUTORY CHECKS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                        Spacer(modifier = Modifier.height(4.dp))

                        Text("✓ Right-to-Work: ${candidate.rightToWork.shareCodeOrDoc} (Expiry: ${candidate.rightToWork.expiryDate})", fontSize = 11.sp)
                        Text("✓ References: 1. ${candidate.references.referee1Name} (${candidate.references.referee1Status}) | 2. ${candidate.references.referee2Name} (${candidate.references.referee2Status})", fontSize = 11.sp)
                        Text("✓ Enhanced DBS: ${candidate.dbsTracking.applicationRef} - Status: ${candidate.dbsTracking.stage}", fontSize = 11.sp)
                        Text("✓ Interview Score: ${candidate.interviewScore.totalScore}/20 (${candidate.interviewScore.recommendation})", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))

                        // Onboarding Checklist
                        Text("ONBOARDING CHECKLIST (${checklist.completedCount()}/5)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))

                        OnboardingCheckItem("Contract of Employment e-Signed", checklist.contractSigned) {
                            repository.toggleOnboardingItem(candidate.id, "contract")
                        }
                        OnboardingCheckItem("Care Uniform & PPE Issued", checklist.uniformIssued) {
                            repository.toggleOnboardingItem(candidate.id, "uniform")
                        }
                        OnboardingCheckItem("Shadowing Shifts Scheduled with Senior", checklist.shadowShiftsBooked) {
                            repository.toggleOnboardingItem(candidate.id, "shadow")
                        }
                        OnboardingCheckItem("Security ID Badge & Key Fob Printed", checklist.idBadgePrinted) {
                            repository.toggleOnboardingItem(candidate.id, "idBadge")
                        }
                        OnboardingCheckItem("The Care Certificate & Mandatory Training Enrolled", checklist.mandatoryTrainingEnrolled) {
                            repository.toggleOnboardingItem(candidate.id, "training")
                        }

                        if (candidate.stage == CandidateStage.ONBOARDED) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CareMintTealContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "✓ CANDIDATE FULLY ONBOARDED: Active staff profile created in Workforce directory.",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CareMintTeal,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    if (candidate.stage != CandidateStage.ONBOARDED) {
                        Button(
                            onClick = {
                                repository.convertCandidateToStaffProfile(candidate.id)
                                selectedCandidateForDetail = null
                            },
                            enabled = isReadyToConvert,
                            colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                            modifier = Modifier.testTag("convert_to_staff_button")
                        ) {
                            Text("Convert to Staff Profile", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedCandidateForDetail = null }) { Text("Close") }
                }
            )
        }
    }

    // Modal: Competency Interview Scoring Dialog
    if (showInterviewDialog && selectedCandidateForDetail != null) {
        val targetCandidate = selectedCandidateForDetail!!
        var valuesScore by remember { mutableStateOf(4) }
        var movingScore by remember { mutableStateOf(4) }
        var commsScore by remember { mutableStateOf(5) }
        var scenarioScore by remember { mutableStateOf(4) }
        var notes by remember { mutableStateOf("Demonstrated genuine compassion and strong grasp of person-centred dignity.") }
        var recommendation by remember { mutableStateOf("Proceed with Offer") }

        AlertDialog(
            onDismissRequest = { showInterviewDialog = false },
            title = { Text("Structured Interview Scoring", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("Candidate: ${targetCandidate.fullName}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("1. Values & Respect in Care (1-5): $valuesScore", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Slider(value = valuesScore.toFloat(), onValueChange = { valuesScore = it.toInt() }, valueRange = 1f..5f, steps = 3)

                    Text("2. Moving & Handling Practical Knowledge (1-5): $movingScore", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Slider(value = movingScore.toFloat(), onValueChange = { movingScore = it.toInt() }, valueRange = 1f..5f, steps = 3)

                    Text("3. Communication & Dysphasia Empathy (1-5): $commsScore", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Slider(value = commsScore.toFloat(), onValueChange = { commsScore = it.toInt() }, valueRange = 1f..5f, steps = 3)

                    Text("4. Emergency Scenario & De-escalation (1-5): $scenarioScore", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Slider(value = scenarioScore.toFloat(), onValueChange = { scenarioScore = it.toInt() }, valueRange = 1f..5f, steps = 3)

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Interviewer Evaluation Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Recommendation", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Strong Yes", "Proceed with Offer", "Hold", "Reject").forEach { rec ->
                            FilterChip(
                                selected = recommendation == rec,
                                onClick = { recommendation = rec },
                                label = { Text(rec, fontSize = 9.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val score = InterviewScore(
                            valuesScore = valuesScore,
                            movingHandlingScore = movingScore,
                            communicationScore = commsScore,
                            scenarioScore = scenarioScore,
                            interviewerNotes = notes,
                            recommendation = recommendation
                        )
                        repository.scoreCandidateInterview(targetCandidate.id, score)
                        showInterviewDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                    modifier = Modifier.testTag("save_interview_score_button")
                ) {
                    Text("Save Evaluation & Advance Candidate", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showInterviewDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun CandidateCard(
    candidate: Candidate,
    onClick: () -> Unit,
    onInterviewClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("candidate_card_${candidate.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = candidate.fullName, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${candidate.email} • ${candidate.phone}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "Applied: ${candidate.appliedDate} • Offer: £${candidate.offerHourlyRate}/hr", fontSize = 11.sp, color = CareMintTeal, fontWeight = FontWeight.SemiBold)
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(candidate.stage.colorHex).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = candidate.stage.displayName,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(candidate.stage.colorHex),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = candidate.cvSummary, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)

            Spacer(modifier = Modifier.height(10.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Onboarding checklist: ${candidate.onboarding.completedCount()}/5 completed",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onInterviewClick,
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Score Interview", fontSize = 10.sp)
                    }
                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Vetting & Onboarding", fontSize = 10.sp, color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingCheckItem(title: String, isChecked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = isChecked, onCheckedChange = { onToggle() }, colors = CheckboxDefaults.colors(checkedColor = CareMintTeal))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = title, fontSize = 12.sp, color = if (isChecked) CareMintTeal else MaterialTheme.colorScheme.onSurface, fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal)
    }
}
