package com.example.ui.reports

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
import com.example.ui.common.AiChip
import com.example.ui.common.NeutralPill
import com.example.ui.common.StatCard
import com.example.ui.common.StatusPill
import com.example.ui.theme.*

@Composable
fun ComplianceReportingScreen(
    repository: CareRepository
) {
    var reportMode by remember { mutableStateOf(0) } // 0: Compliance Dashboard, 1: Report Builder

    Column(modifier = Modifier.fillMaxSize().background(BasePageBg)) {
        // Mode Selector Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BaseSurface)
                .border(1.dp, BaseBorder)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("Compliance Dashboard", "Custom Report Builder").forEachIndexed { idx, label ->
                val isSel = reportMode == idx
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) BaseEmerald500 else Color.Transparent)
                        .border(1.dp, if (isSel) BaseEmerald300 else Color(0xFF1E283F), RoundedCornerShape(8.dp))
                        .clickable { reportMode = idx }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSel) Color.White else BaseTextMuted
                    )
                }
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (reportMode == 1) {
                ReportBuilderScreen(repository = repository)
            } else {
                ComplianceDashboardOverviewContent(repository = repository)
            }
        }
    }
}

@Composable
private fun ComplianceDashboardOverviewContent(
    repository: CareRepository
) {
    val clients by repository.clients.collectAsState()
    val carePlans by repository.carePlans.collectAsState()

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(BasePageBg)) {
        val isDesktop = maxWidth >= 1024.dp
        val contentPadding = if (isDesktop) 32.dp else 16.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .testTag("reports_screen"),
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
                            text = "Compliance Reporting Dashboard",
                            fontSize = if (isDesktop) 30.sp else 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = BaseTextHeading
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Policy review dates and pending care plan updates at a glance.",
                            fontSize = 14.sp,
                            color = BaseTextMuted
                        )
                    }

                    OutlinedButton(
                        onClick = { /* Export / print */ },
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, tint = BaseTextMuted, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export / print", fontSize = 14.sp)
                    }
                }
            }

            // 4 Stat Cards
            item {
                if (isDesktop) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatCard("TOTAL POLICIES", "4", "", Icons.Default.Description, BaseTextHeading, Modifier.weight(1f))
                        StatCard("REVIEWS OVERDUE", "2", "", Icons.Default.Warning, StatusCritText, Modifier.weight(1f))
                        StatCard("DUE ≤30 DAYS", "0", "", Icons.Default.AccessTime, BaseTextHeading, Modifier.weight(1f))
                        StatCard("PENDING CARE PLANS", "6", "", Icons.Default.FactCheck, StatusVioletText, Modifier.weight(1f))
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard("TOTAL POLICIES", "4", "", Icons.Default.Description, BaseTextHeading, Modifier.weight(1f))
                            StatCard("REVIEWS OVERDUE", "2", "", Icons.Default.Warning, StatusCritText, Modifier.weight(1f))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard("DUE ≤30 DAYS", "0", "", Icons.Default.AccessTime, BaseTextHeading, Modifier.weight(1f))
                            StatCard("PENDING CARE PLANS", "6", "", Icons.Default.FactCheck, StatusVioletText, Modifier.weight(1f))
                        }
                    }
                }
            }

            // Card 1: Policy Review Schedule
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BaseSurface)
                        .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Policy Review Schedule", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                        Text("sorted by next review", fontSize = 12.sp, color = BaseTextFaint)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val scheduleItems = listOf(
                        Triple("Data Protection & GDPR Policy", "Data Protection • V1.4 • Rachel Stein", "Next: 2026-03-01" to "216d overdue"),
                        Triple("Infection Prevention & Control Policy", "Infection Control • V2.1 • Aisha Khan", "Next: 2026-05-01" to "155d overdue"),
                        Triple("Safeguarding Adults Policy", "Safeguarding • V4.0 • Rachel Stein", "Next: 2026-11-01" to "in 29d"),
                        Triple("Medication Administration Policy", "Clinical • V3.2 • Aisha Khan", "Next: 2027-02-01" to "in 121d")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        scheduleItems.forEach { (title, subtitle, dates) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BaseTileChip)
                                    .border(1.dp, Color(0xFF1E2638), RoundedCornerShape(12.dp))
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Description, contentDescription = null, tint = StatusVioletText, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                                        Text(subtitle, fontSize = 12.sp, color = BaseTextMuted)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(dates.first, fontSize = 12.sp, color = BaseTextFaint)
                                    StatusPill(
                                        status = dates.second,
                                        color = if (dates.second.contains("overdue")) StatusCritText else StatusInfoText
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Card 2: Pending Care Plan Updates
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BaseSurface)
                        .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Pending Care Plan Updates", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                            Text("drafts & reviews awaiting approval", fontSize = 12.sp, color = BaseTextMuted)
                        }
                        Text("All clients", fontSize = 13.sp, color = BaseEmerald300, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val pendingPlans = listOf(
                        "Person-Centred Care Plan: Critical Support for End-Stage Heart Failure" to "Doris Penrose",
                        "Person-Centred Support Plan for Zola" to "Zola",
                        "Care and Support Plan for Geoffrey Whitfield" to "Geoffrey Whitfield",
                        "Person-Centred Care Plan for Doris Penrose" to "Doris Penrose",
                        "Person-Centred Care Plan for Naim" to "Naim"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        pendingPlans.forEach { (planTitle, clientName) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BaseTileChip)
                                    .border(1.dp, Color(0xFF1E2638), RoundedCornerShape(12.dp))
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Icon(Icons.Default.Description, contentDescription = null, tint = BaseEmerald300, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(planTitle, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading, maxLines = 1)
                                        Text(clientName, fontSize = 12.sp, color = BaseTextMuted)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AiChip()
                                    Text("Review: 2025-06-14", fontSize = 12.sp, color = BaseTextFaint)
                                    NeutralPill(text = "Draft")
                                }
                            }
                        }
                    }
                }
            }

            // Bottom 3 Summary Tiles
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BaseSurface)
                            .border(1.dp, BaseBorder, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Text("ACTIVE CARE PLANS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BaseTextMuted)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("1", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BaseSurface)
                            .border(1.dp, BaseBorder, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Text("POLICIES UP TO DATE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BaseTextMuted)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("2", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = BaseEmerald300)
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BaseSurface)
                            .border(1.dp, BaseBorder, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Text("POLICIES WITHOUT REVIEW DATE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BaseTextMuted)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("0", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                    }
                }
            }
        }
    }
}
