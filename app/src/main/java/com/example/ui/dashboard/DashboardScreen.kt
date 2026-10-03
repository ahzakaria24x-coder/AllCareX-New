package com.example.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.model.IncidentSeverity
import com.example.model.Visit
import com.example.model.VisitStatus
import com.example.ui.common.StatCard
import com.example.ui.common.StatusPill
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    repository: CareRepository,
    onNavigateToClients: () -> Unit,
    onNavigateToRota: () -> Unit,
    onNavigateToEmar: () -> Unit,
    onNavigateToIncidents: () -> Unit,
    onNavigateToWorkforce: () -> Unit,
    onNavigateToCarerMode: () -> Unit,
    onNavigateToCqc: () -> Unit = {}
) {
    val clients by repository.clients.collectAsState()
    val visits by repository.visits.collectAsState()
    val incidents by repository.incidents.collectAsState()
    val staffList by repository.staffList.collectAsState()

    val todayVisits = visits.filter { it.date == "03/10/2026" }
    val completedVisitsCount = todayVisits.count { it.status == VisitStatus.COMPLETED }
    val averageCompliance = if (staffList.isNotEmpty()) staffList.map { it.compliancePercentage }.average().toInt() else 89

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isDesktop = maxWidth >= 1024.dp
        val contentPadding = if (isDesktop) 32.dp else 16.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BasePageBg)
                .padding(contentPadding)
                .testTag("dashboard_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // H1 "Good morning" with subtitle
            item {
                Column {
                    Text(
                        text = "Good morning",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = BaseTextHeading
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Here's what needs your attention across your care services today.",
                        fontSize = 14.sp,
                        color = BaseTextMuted
                    )
                }
            }

            // 4 Stat Cards: 4 columns on desktop, 2 columns on mobile
            item {
                if (isDesktop) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard(
                            title = "Active clients",
                            value = "${clients.size}",
                            subtitle = "Across all services",
                            icon = Icons.Default.Group,
                            accentColor = BaseEmerald300,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToClients
                        )
                        StatCard(
                            title = "Visits today",
                            value = "${todayVisits.size}",
                            subtitle = "$completedVisitsCount completed",
                            icon = Icons.Default.CalendarMonth,
                            accentColor = Color(0xFF7DD3FC),
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToRota
                        )
                        StatCard(
                            title = "Open incidents",
                            value = "${incidents.size}",
                            subtitle = "${incidents.count { it.severity == IncidentSeverity.SEVERE || it.severity == IncidentSeverity.MODERATE }} high priority",
                            icon = Icons.Default.Warning,
                            accentColor = Color(0xFFFDA4AF),
                            isAlert = true,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToIncidents
                        )
                        StatCard(
                            title = "Workforce compliance",
                            value = "$averageCompliance%",
                            subtitle = "Training & DBS average",
                            icon = Icons.Default.Verified,
                            accentColor = Color(0xFFC4B5FD),
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToWorkforce
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            StatCard(
                                title = "Active clients",
                                value = "${clients.size}",
                                subtitle = "Across all services",
                                icon = Icons.Default.Group,
                                accentColor = BaseEmerald300,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToClients
                            )
                            StatCard(
                                title = "Visits today",
                                value = "${todayVisits.size}",
                                subtitle = "$completedVisitsCount completed",
                                icon = Icons.Default.CalendarMonth,
                                accentColor = Color(0xFF7DD3FC),
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToRota
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            StatCard(
                                title = "Open incidents",
                                value = "${incidents.size}",
                                subtitle = "${incidents.count { it.severity == IncidentSeverity.SEVERE || it.severity == IncidentSeverity.MODERATE }} high priority",
                                icon = Icons.Default.Warning,
                                accentColor = Color(0xFFFDA4AF),
                                isAlert = true,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToIncidents
                            )
                            StatCard(
                                title = "Workforce compliance",
                                value = "$averageCompliance%",
                                subtitle = "Training & DBS average",
                                icon = Icons.Default.Verified,
                                accentColor = Color(0xFFC4B5FD),
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToWorkforce
                            )
                        }
                    }
                }
            }

            // "Weekly Visit Schedule" Card
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = BaseSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BaseBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        // Title row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = BaseEmerald500,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Weekly Visit Schedule",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BaseTextHeading
                                )
                            }

                            // Date Range & Controls
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "28 Sept – 4 Oct 2026",
                                    fontSize = 13.sp,
                                    color = BaseTextMuted,
                                    modifier = Modifier.padding(end = 4.dp)
                                )

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF1E293B).copy(alpha = 0.6f),
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clickable { }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous", tint = BaseTextBody, modifier = Modifier.size(18.dp))
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF1E293B).copy(alpha = 0.6f),
                                    modifier = Modifier
                                        .height(36.dp)
                                        .clickable { }
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
                                        Text("Today", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF1E293B).copy(alpha = 0.6f),
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clickable { }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = BaseTextBody, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Sub-row: "N visits this week" & "N staff assigned"
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = BaseTextFaint, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("18 visits this week", fontSize = 13.sp, color = BaseTextMuted)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Group, contentDescription = null, tint = BaseTextFaint, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${staffList.size} staff assigned", fontSize = 13.sp, color = BaseTextMuted)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 7 Equal Day Tiles (Horizontal scroll container on mobile)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val daysOfWeek = listOf(
                                Triple("MON", "28", listOf(Triple("08:00", "Arthur P.", "David O."))),
                                Triple("TUE", "29", listOf(Triple("09:00", "Dot Higgins", "Chloe B."))),
                                Triple("WED", "30", listOf(Triple("08:30", "Evelyn D.", "Priya P."))),
                                Triple("THU", "01", listOf(Triple("08:00", "Arthur P.", "David O."))),
                                Triple("FRI", "02", listOf(Triple("10:00", "Maggie Brown", "Chloe B."))),
                                Triple("SAT", "03", listOf(
                                    Triple("08:00", "Arthur P.", "David O."),
                                    Triple("11:00", "Dot Higgins", "Unassigned"),
                                    Triple("12:30", "Arthur P.", "David O.")
                                )),
                                Triple("SUN", "04", listOf(Triple("08:00", "Arthur P.", "David O.")))
                            )

                            daysOfWeek.forEach { (dayLabel, dayNum, dayVisits) ->
                                val isToday = dayLabel == "SAT" && dayNum == "03"

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isToday) Color(0xFF0F1E24) else BaseRaisedSurface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isToday) BaseEmerald500.copy(alpha = 0.4f) else BaseBorder
                                    ),
                                    modifier = Modifier
                                        .width(136.dp)
                                        .heightIn(min = 160.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        // Header
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = dayLabel,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isToday) BaseEmerald300 else BaseTextMuted
                                            )
                                            Text(
                                                text = "${dayVisits.size}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isToday) BaseEmerald300 else BaseTextFaint
                                            )
                                        }

                                        Text(
                                            text = dayNum,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isToday) BaseEmerald300 else BaseTextHeading
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        if (dayVisits.isEmpty()) {
                                            Text("No visits", fontSize = 11.sp, color = BaseTextFaint, modifier = Modifier.padding(top = 10.dp))
                                        } else {
                                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                dayVisits.forEach { (time, clientName, carerName) ->
                                                    val isUnassigned = carerName == "Unassigned"
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = BaseTileChip,
                                                        border = androidx.compose.foundation.BorderStroke(
                                                            1.dp,
                                                            if (isUnassigned) Color(0xFFF59E0B).copy(alpha = 0.4f) else BaseBorder
                                                        ),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(6.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(6.dp)
                                                                    .clip(CircleShape)
                                                                    .background(if (isUnassigned) DotMissed else DotCompleted)
                                                            )
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Column {
                                                                Text(text = "$time $clientName", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                                                Text(text = carerName, fontSize = 9.sp, color = if (isUnassigned) Color(0xFFF59E0B) else BaseTextFaint)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = BaseBorder, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Legend row: Scheduled, In progress, Completed, Missed, Cancelled
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(
                                "Scheduled" to DotScheduled,
                                "In progress" to DotInProgress,
                                "Completed" to DotCompleted,
                                "Missed" to DotMissed,
                                "Cancelled" to DotCancelled
                            ).forEach { (label, dotColor) ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(dotColor))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = label, fontSize = 12.sp, color = BaseTextMuted)
                                }
                            }
                        }
                    }
                }
            }

            // Two Cards Side by Side (2/3 and 1/3 on desktop, stacked on mobile)
            item {
                if (isDesktop) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Urgent Alerts (2/3)
                        UrgentAlertsCard(
                            incidents = incidents,
                            modifier = Modifier.weight(2f),
                            onViewAll = onNavigateToIncidents
                        )
                        // Today's Schedule (1/3)
                        TodayScheduleCard(
                            visits = todayVisits,
                            modifier = Modifier.weight(1f),
                            onViewAll = onNavigateToRota
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        UrgentAlertsCard(
                            incidents = incidents,
                            modifier = Modifier.fillMaxWidth(),
                            onViewAll = onNavigateToIncidents
                        )
                        TodayScheduleCard(
                            visits = todayVisits,
                            modifier = Modifier.fillMaxWidth(),
                            onViewAll = onNavigateToRota
                        )
                    }
                }
            }

            // Full-Width Banner: "CQC Digital Record Alignment"
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0A1F26),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BaseEmerald500.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(verticalAlignment = Alignment.Top, modifier = Modifier.weight(1f)) {
                                // 56px heart-pulse icon tile with brand gradient
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(BrandGradient),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                    Text(
                                        text = "CQC Digital Record Alignment",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BaseTextHeading
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Records are captured at the point of care, with audit trails, versioned care plans, and integrated clinical data — supporting safe, effective, compassionate, high-quality care.",
                                        fontSize = 14.sp,
                                        color = BaseTextBody
                                    )
                                }
                            }

                            Text(
                                text = "Open dashboard →",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BaseEmerald400,
                                modifier = Modifier
                                    .clickable { onNavigateToCqc() }
                                    .padding(start = 12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 5 Emerald Pills: Safe, Effective, Caring, Responsive, Well-led
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Safe", "Effective", "Caring", "Responsive", "Well-led").forEach { pill ->
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = BaseEmerald500.copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BaseEmerald500.copy(alpha = 0.35f))
                                ) {
                                    Text(
                                        text = pill,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = BaseEmerald300,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UrgentAlertsCard(
    incidents: List<com.example.model.IncidentReport>,
    modifier: Modifier = Modifier,
    onViewAll: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = BaseSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BaseBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFDA4AF), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Urgent Alerts", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                }
                Text(
                    text = "View all →",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = BaseEmerald400,
                    modifier = Modifier.clickable { onViewAll() }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Alert Rows
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        StatusPill(text = "High", statusColor = StatusHighText)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "Arthur Pendelton — Slip, Trip or Fall", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Text(text = "Living room slip, Zimmer frame physio review completed", fontSize = 12.sp, color = BaseTextMuted)
                        }
                    }
                    Text(text = "actioned", fontSize = 12.sp, color = BaseTextFaint)
                }

                Divider(color = BaseBorder, thickness = 1.dp)

                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        StatusPill(text = "Medium", statusColor = StatusMedText)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "Dorothy Higgins — Unassigned visit", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Text(text = "11:00 call unallocated, carer reassignment requested", fontSize = 12.sp, color = BaseTextMuted)
                        }
                    }
                    Text(text = "actioned", fontSize = 12.sp, color = BaseTextFaint)
                }
            }
        }
    }
}

@Composable
private fun TodayScheduleCard(
    visits: List<Visit>,
    modifier: Modifier = Modifier,
    onViewAll: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = BaseSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BaseBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF7DD3FC), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Today's Schedule", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (visits.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No visits scheduled today.", fontSize = 13.sp, color = BaseTextFaint)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    visits.take(3).forEach { v ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(BaseRaisedSurface)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("${v.startTime} - ${v.clientName}", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Text(v.visitType, fontSize = 10.sp, color = BaseTextFaint)
                            }
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(DotCompleted))
                        }
                    }
                }
            }
        }
    }
}
