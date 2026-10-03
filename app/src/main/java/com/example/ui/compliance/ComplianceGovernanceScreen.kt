package com.example.ui.compliance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.common.NeutralPill
import com.example.ui.common.StatusPill
import com.example.ui.theme.*

data class PolicyDoc(
    val title: String,
    val category: String,
    val version: String,
    val owner: String,
    val nextReview: String,
    val isOverdue: Boolean
)

data class AuditDoc(
    val title: String,
    val category: String,
    val date: String,
    val lead: String,
    val score: Int,
    val status: String
)

@Composable
fun ComplianceGovernanceScreen(
    repository: CareRepository
) {
    var activeTab by remember { mutableStateOf("Policies") } // Policies or Audits
    var showAddPolicyModal by remember { mutableStateOf(false) }

    // Form state
    var policyTitle by remember { mutableStateOf("") }
    var policyCategory by remember { mutableStateOf("clinical") }
    var policyVersion by remember { mutableStateOf("1.0") }
    var policyOwner by remember { mutableStateOf("") }
    var policyNextReview by remember { mutableStateOf("01/10/2026") }
    var policySummary by remember { mutableStateOf("") }
    var categoryDropdownOpen by remember { mutableStateOf(false) }

    val policies = listOf(
        PolicyDoc("Data Protection & GDPR Policy", "Data Protection", "V1.4", "Rachel Stein", "2026-03-01", true),
        PolicyDoc("Infection Prevention & Control Policy", "Infection Control", "V2.1", "Aisha Khan", "2026-05-01", true),
        PolicyDoc("Safeguarding Adults Policy", "Safeguarding", "V4.0", "Rachel Stein", "2026-11-01", false),
        PolicyDoc("Medication Administration Policy", "Clinical", "V3.2", "Aisha Khan", "2027-02-01", false)
    )

    val audits = listOf(
        AuditDoc("Q3 Medication Audit", "Medication", "2026-09-15", "Aisha Khan", 94, "Completed"),
        AuditDoc("Infection Control Spot Check", "Infection Control", "2026-08-10", "Rachel Stein", 88, "Completed"),
        AuditDoc("Safeguarding Annual Review", "Safeguarding", "2026-07-20", "Rachel Stein", 91, "Completed")
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(BasePageBg)) {
        val isDesktop = maxWidth >= 1024.dp
        val contentPadding = if (isDesktop) 32.dp else 16.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .testTag("compliance_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Header Row: Title & Subtitle
            item {
                Column {
                    Text(
                        text = "Compliance & Governance",
                        fontSize = if (isDesktop) 30.sp else 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = BaseTextHeading
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "CQC alignment, policy library, audits and clinical governance.",
                        fontSize = 14.sp,
                        color = BaseTextMuted
                    )
                }
            }

            // CQC Compliance Dashboard Card
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BaseSurface)
                        .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = BaseEmerald300, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CQC Compliance Dashboard", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 5 Progress Bars
                    val domains = listOf(
                        Triple("Safe", "People protected from abuse and avoidable harm", 0.90f to 90),
                        Triple("Effective", "Care, treatment and support achieves good outcomes", 0.92f to 92),
                        Triple("Caring", "Staff involve and treat people with compassion", 0.92f to 92),
                        Triple("Responsive", "Services organised to meet people's needs", 0.92f to 92),
                        Triple("Well-led", "Leadership, governance and culture support high-quality care", 0.91f to 91)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        domains.forEach { (name, desc, progressPair) ->
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                                        Text(desc, fontSize = 12.sp, color = BaseTextMuted)
                                    }
                                    Text("${progressPair.second}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BaseEmerald300)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { progressPair.first },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = BaseEmerald500,
                                    trackColor = Color(0xFF1E2638)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Divider(color = Color(0xFF171C2E))
                    Spacer(modifier = Modifier.height(16.dp))

                    // 4 Sub-stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Open incidents", fontSize = 12.sp, color = BaseTextMuted)
                            Text("2", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                        }
                        Column {
                            Text("CQC notifiable", fontSize = 12.sp, color = BaseTextMuted)
                            Text("1", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                        }
                        Column {
                            Text("Policies overdue", fontSize = 12.sp, color = BaseTextMuted)
                            Text("2", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = StatusCritText)
                        }
                        Column {
                            Text("Avg audit score", fontSize = 12.sp, color = BaseTextMuted)
                            Text("91%", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = BaseEmerald300)
                        }
                    }
                }
            }

            // Tabs: Policies, Audits + Action button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E293B).copy(alpha = 0.6f))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Policies", "Audits").forEach { tab ->
                            val isSel = activeTab == tab
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Color(0xFF0A0A0A) else Color.Transparent)
                                    .clickable { activeTab = tab }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (tab == "Policies") Icons.Default.Description else Icons.Default.FactCheck,
                                        contentDescription = null,
                                        tint = if (isSel) Color.White else BaseTextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(tab, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = if (isSel) Color.White else BaseTextMuted)
                                }
                            }
                        }
                    }

                    if (activeTab == "Policies") {
                        Button(
                            onClick = { showAddPolicyModal = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add policy", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                    }
                }
            }

            // Tab Content: Policies or Audits List
            if (activeTab == "Policies") {
                policies.forEach { pol ->
                    item {
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = StatusVioletText, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(pol.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                                    Text("${pol.category} • ${pol.version} • ${pol.owner}", fontSize = 12.sp, color = BaseTextMuted)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (pol.isOverdue) {
                                    StatusPill(status = "Review overdue", color = StatusCritText)
                                } else {
                                    Text("Review: ${pol.nextReview}", fontSize = 12.sp, color = BaseTextFaint)
                                }
                            }
                        }
                    }
                }
            } else {
                audits.forEach { aud ->
                    item {
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FactCheck, contentDescription = null, tint = StatusInfoText, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(aud.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                                    Text("${aud.category} • ${aud.date} • ${aud.lead}", fontSize = 12.sp, color = BaseTextMuted)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("${aud.score}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BaseEmerald300)
                                StatusPill(status = aud.status, color = BaseEmerald300)
                            }
                        }
                    }
                }
            }
        }

        // Modal: Add Policy
        if (showAddPolicyModal) {
            AlertDialog(
                onDismissRequest = { showAddPolicyModal = false },
                containerColor = BaseModalBg,
                shape = RoundedCornerShape(16.dp),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Add Policy", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                        IconButton(onClick = { showAddPolicyModal = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = BaseTextMuted)
                        }
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Title
                        Column {
                            Text("Title", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = policyTitle,
                                onValueChange = { policyTitle = it },
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

                        // Category and Version
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Category", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = policyCategory,
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
                                        listOf("clinical", "hr", "health safety", "safeguarding", "infection control", "data protection", "governance").forEach { c ->
                                            DropdownMenuItem(
                                                text = { Text(c, color = BaseTextHeading) },
                                                onClick = {
                                                    policyCategory = c
                                                    categoryDropdownOpen = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Version", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = policyVersion,
                                    onValueChange = { policyVersion = it },
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

                        // Owner and Next review
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Owner", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = policyOwner,
                                    onValueChange = { policyOwner = it },
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
                                Text("Next review", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = policyNextReview,
                                    onValueChange = { policyNextReview = it },
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

                        // Summary
                        Column {
                            Text("Summary", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = policySummary,
                                onValueChange = { policySummary = it },
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
                        onClick = { showAddPolicyModal = false },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("Save", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showAddPolicyModal = false },
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("Cancel", fontSize = 14.sp, color = BaseTextMuted)
                    }
                }
            )
        }
    }
}
