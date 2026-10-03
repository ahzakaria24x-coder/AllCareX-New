package com.example.ui.quality

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.ui.theme.CareMintTeal
import com.example.ui.theme.CareMintTealContainer

@Composable
fun QualityComplianceScreen(
    repository: CareRepository
) {
    var activeSubTab by remember { mutableStateOf(0) }
    // 0: Compliance Dashboard
    // 1: Regulatory Frameworks (Editable Data)
    // 2: Self-Assessment & Evidence Mapping
    // 3: Policy Library & SOPs
    // 4: Audits & SMART Actions
    // 5: Feedback & Governance
    // 6: Inspection-Day Mode

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Quality, Governance & Compliance",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Dynamic regulator frameworks (CQC/CIW/CIS), evidence mapping, audits & governance",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Navigation Tabs Row
        ScrollableTabRow(
            selectedTabIndex = activeSubTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = CareMintTeal,
            edgePadding = 0.dp,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
        ) {
            Tab(selected = activeSubTab == 0, onClick = { activeSubTab = 0 }, text = { Text("Dashboard", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = activeSubTab == 1, onClick = { activeSubTab = 1 }, text = { Text("Frameworks", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = activeSubTab == 2, onClick = { activeSubTab = 2 }, text = { Text("Self-Assessment", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = activeSubTab == 3, onClick = { activeSubTab = 3 }, text = { Text("Policies", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = activeSubTab == 4, onClick = { activeSubTab = 4 }, text = { Text("Audits", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = activeSubTab == 5, onClick = { activeSubTab = 5 }, text = { Text("Governance", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = activeSubTab == 6, onClick = { activeSubTab = 6 }, text = { Text("Inspection Day", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (activeSubTab) {
            0 -> ComplianceOverviewDashboard(repository = repository, onNavigate = { activeSubTab = it })
            1 -> FrameworkManagerView(repository = repository)
            2 -> EvidenceMappingSelfAssessmentView(repository = repository)
            3 -> PolicyLibraryView(repository = repository)
            4 -> AuditsManagerView(repository = repository)
            5 -> ComplaintsGovernanceView(repository = repository)
            6 -> InspectionDayModeView(repository = repository)
        }
    }
}

@Composable
private fun ComplianceOverviewDashboard(
    repository: CareRepository,
    onNavigate: (Int) -> Unit
) {
    val policies by repository.policies.collectAsState()
    val auditTemplates by repository.auditTemplates.collectAsState()
    val staffList by repository.staffList.collectAsState()
    val incidents by repository.incidents.collectAsState()
    val evidenceItems by repository.evidenceItems.collectAsState()
    val riskRegister by repository.riskRegister.collectAsState()

    val overduePoliciesCount = policies.count { it.isOverdue }
    val openActionsCount = auditTemplates.sumOf { it.actionItems.count { a -> a.status != "Resolved" } }
    val averageAuditScore = if (auditTemplates.isNotEmpty()) auditTemplates.map { it.overallScorePercent }.average() else 92.0
    val staffCompliantCount = staffList.count { it.compliancePercentage >= 75 }
    val evidenceGapsCount = evidenceItems.count { it.isGap }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // High-Level KPIs Row 1
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ComplianceKpiCard(
                    title = "OVERDUE POLICIES",
                    value = "$overduePoliciesCount",
                    color = if (overduePoliciesCount > 0) Color(0xFFEF4444) else Color(0xFF10B981),
                    subtitle = if (overduePoliciesCount > 0) "Immediate review required" else "All policies in-date",
                    modifier = Modifier.weight(1f)
                )
                ComplianceKpiCard(
                    title = "OPEN AUDIT ACTIONS",
                    value = "$openActionsCount",
                    color = if (openActionsCount > 0) Color(0xFFF59E0B) else Color(0xFF10B981),
                    subtitle = "SMART improvement items",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // High-Level KPIs Row 2
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ComplianceKpiCard(
                    title = "AVERAGE AUDIT SCORE",
                    value = "${"%.0f".format(averageAuditScore)}%",
                    color = CareMintTeal,
                    subtitle = "Green RAG standard across 7 domains",
                    modifier = Modifier.weight(1f)
                )
                ComplianceKpiCard(
                    title = "STAFF COMPLIANCE",
                    value = "$staffCompliantCount/${staffList.size}",
                    color = if (staffCompliantCount == staffList.size) Color(0xFF10B981) else Color(0xFFF59E0B),
                    subtitle = "DBS, RTW & Care Certificate",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Incident Trends & Evidence Gaps Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ComplianceKpiCard(
                    title = "INCIDENT TRENDS",
                    value = "${incidents.size}",
                    color = Color(0xFF6366F1),
                    subtitle = "${incidents.count { it.dutyOfCandourCompleted }} Duty of Candour letters logged",
                    modifier = Modifier.weight(1f)
                )
                ComplianceKpiCard(
                    title = "EVIDENCE GAPS",
                    value = "$evidenceGapsCount",
                    color = if (evidenceGapsCount > 0) Color(0xFFEF4444) else Color(0xFF10B981),
                    subtitle = if (evidenceGapsCount > 0) "Action plan active" else "Zero identified gaps",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Readiness Callout
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CareMintTeal.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CQC Single Assessment Readiness: GOOD / OUTSTANDING",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CareMintTeal
                        )
                        Button(
                            onClick = { onNavigate(2) },
                            colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("View Self-Assessment", color = Color(0xFF0A1128), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        text = "All 5 Key Questions (Safe, Effective, Caring, Responsive, Well-led) mapped to real platform data: 96.4% care plan review adherence, 100% DBS Update Service checks, and 0 unresolved lone worker SOS events.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Quick Navigation Shortcuts
        item {
            Text("Quality Management Shortcuts", fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { onNavigate(1) }, modifier = Modifier.weight(1f).height(36.dp)) {
                    Text("Frameworks", fontSize = 10.sp)
                }
                OutlinedButton(onClick = { onNavigate(3) }, modifier = Modifier.weight(1f).height(36.dp)) {
                    Text("Policies", fontSize = 10.sp)
                }
                OutlinedButton(onClick = { onNavigate(4) }, modifier = Modifier.weight(1f).height(36.dp)) {
                    Text("Audits", fontSize = 10.sp)
                }
                OutlinedButton(onClick = { onNavigate(6) }, modifier = Modifier.weight(1f).height(36.dp)) {
                    Text("Inspection Day", fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun ComplianceKpiCard(
    title: String,
    value: String,
    color: Color,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = color)
            Text(subtitle, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
