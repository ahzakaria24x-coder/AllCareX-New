package com.example.ui.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CareRepository
import com.example.ui.common.StatCard
import com.example.ui.theme.*

@Composable
fun AnalyticsScreen(
    repository: CareRepository
) {
    var analyticsMode by remember { mutableStateOf(0) } // 0: Role Dashboards, 1: Overview Charts, 2: Alert Thresholds

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
            listOf("Role Dashboards", "Overview Charts", "Alert Thresholds").forEachIndexed { idx, label ->
                val isSel = analyticsMode == idx
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) BaseEmerald500 else Color.Transparent)
                        .border(1.dp, if (isSel) BaseEmerald300 else Color(0xFF1E283F), RoundedCornerShape(8.dp))
                        .clickable { analyticsMode = idx }
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
            when (analyticsMode) {
                0 -> RoleDashboardScreen(repository = repository)
                2 -> com.example.ui.alerts.AlertThresholdsScreen(repository = repository)
                else -> AnalyticsOverviewChartsContent(repository = repository)
            }
        }
    }
}

@Composable
private fun AnalyticsOverviewChartsContent(
    repository: CareRepository
) {
    val clients by repository.clients.collectAsState()
    val visits by repository.visits.collectAsState()
    val incidents by repository.incidents.collectAsState()
    val staffList by repository.staffList.collectAsState()

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(BasePageBg)) {
        val isDesktop = maxWidth >= 1024.dp
        val contentPadding = if (isDesktop) 32.dp else 16.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .testTag("analytics_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Header Row: Title & Subtitle
            item {
                Column {
                    Text(
                        text = "Analytics & Insights",
                        fontSize = if (isDesktop) 30.sp else 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = BaseTextHeading
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Operational, clinical and workforce intelligence.",
                        fontSize = 14.sp,
                        color = BaseTextMuted
                    )
                }
            }

            // 4 Stat Cards
            item {
                if (isDesktop) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatCard("TOTAL CLIENTS", "${clients.size}", "", Icons.Default.Group, BaseTextHeading, Modifier.weight(1f))
                        StatCard("VISITS (PERIOD)", "9", "", Icons.Default.CalendarToday, BaseTextHeading, Modifier.weight(1f))
                        StatCard("INCIDENTS", "${incidents.size}", "", Icons.Default.Warning, BaseTextHeading, Modifier.weight(1f))
                        StatCard("WORKFORCE", "${staffList.size}", "", Icons.Default.Badge, BaseTextHeading, Modifier.weight(1f))
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard("TOTAL CLIENTS", "${clients.size}", "", Icons.Default.Group, BaseTextHeading, Modifier.weight(1f))
                            StatCard("VISITS (PERIOD)", "9", "", Icons.Default.CalendarToday, BaseTextHeading, Modifier.weight(1f))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard("INCIDENTS", "${incidents.size}", "", Icons.Default.Warning, BaseTextHeading, Modifier.weight(1f))
                            StatCard("WORKFORCE", "${staffList.size}", "", Icons.Default.Badge, BaseTextHeading, Modifier.weight(1f))
                        }
                    }
                }
            }

            // Chart 1: Visits last 7 days & Chart 2: Incidents by Category (Row on Desktop, Column on Mobile)
            item {
                if (isDesktop) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        VisitsLineChartCard(modifier = Modifier.weight(1f))
                        IncidentsBarChartCard(modifier = Modifier.weight(1f))
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        VisitsLineChartCard(modifier = Modifier.fillMaxWidth())
                        IncidentsBarChartCard(modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            // Chart 3: Visit Status Mix & Chart 4: Care Type Mix
            item {
                if (isDesktop) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        VisitStatusMixCard(modifier = Modifier.weight(1f))
                        CareTypeMixCard(modifier = Modifier.weight(1f))
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        VisitStatusMixCard(modifier = Modifier.fillMaxWidth())
                        CareTypeMixCard(modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            // Chart 5: Risk Heatmap — Incidents by Category x Month
            item {
                RiskHeatmapCard()
            }
        }
    }
}

@Composable
private fun VisitsLineChartCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BaseSurface)
            .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ShowChart, contentDescription = null, tint = StatusInfoText, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Visits — last 7 days", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Custom Canvas Line Chart
        Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val points = listOf(
                    0.05f to 0.15f,
                    0.25f to 0.70f,
                    0.45f to 0.75f,
                    0.65f to 0.75f,
                    0.85f to 0.75f
                )

                // Grid lines
                for (i in 1..4) {
                    val y = height * (i / 5f)
                    drawLine(
                        color = Color(0xFF1E283F),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Line path
                val path = Path()
                points.forEachIndexed { index, (xRatio, yRatio) ->
                    val x = xRatio * width
                    val y = yRatio * height
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path = path, color = StatusInfoText, style = Stroke(width = 3.dp.toPx()))

                // Points
                points.forEach { (xRatio, yRatio) ->
                    drawCircle(color = StatusInfoText, radius = 5.dp.toPx(), center = Offset(xRatio * width, yRatio * height))
                    drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = Offset(xRatio * width, yRatio * height))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("08-31", "09-01", "09-04", "10-01", "10-02").forEach { label ->
                Text(label, fontSize = 11.sp, color = BaseTextMuted)
            }
        }
    }
}

@Composable
private fun IncidentsBarChartCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BaseSurface)
            .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = StatusCritText, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Incidents by Category", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Custom Canvas Bar Chart
        Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Grid lines
                for (i in 1..4) {
                    val y = height * (i / 4f)
                    drawLine(color = Color(0xFF1E283F), start = Offset(0f, y), end = Offset(width, y), strokeWidth = 1.dp.toPx())
                }

                // 3 bars
                val barWidth = width * 0.16f
                val barPositions = listOf(width * 0.22f, width * 0.50f, width * 0.78f)
                barPositions.forEach { xCenter ->
                    val barHeight = height * 0.33f
                    val left = xCenter - barWidth / 2f
                    val top = height - barHeight
                    drawRoundRect(
                        color = StatusCritText,
                        topLeft = Offset(left, top),
                        size = Size(barWidth, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text("clinical deterioration", fontSize = 10.sp, color = BaseTextMuted)
            Text("fall", fontSize = 10.sp, color = BaseTextMuted)
            Text("medication error", fontSize = 10.sp, color = BaseTextMuted)
        }
    }
}

@Composable
private fun VisitStatusMixCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BaseSurface)
            .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.PieChart, contentDescription = null, tint = BaseEmerald300, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Visit Status Mix", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(120.dp)) {
                // Donut arc slices
                drawArc(
                    color = BaseEmerald400,
                    startAngle = 0f,
                    sweepAngle = 240f,
                    useCenter = false,
                    style = Stroke(width = 24.dp.toPx())
                )
                drawArc(
                    color = StatusInfoText,
                    startAngle = 240f,
                    sweepAngle = 70f,
                    useCenter = false,
                    style = Stroke(width = 24.dp.toPx())
                )
                drawArc(
                    color = StatusCritText,
                    startAngle = 310f,
                    sweepAngle = 50f,
                    useCenter = false,
                    style = Stroke(width = 24.dp.toPx())
                )
            }
        }
    }
}

@Composable
private fun CareTypeMixCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BaseSurface)
            .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.BarChart, contentDescription = null, tint = StatusVioletText, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Care Type Mix", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val types = listOf(
                "supported living" to (2 to 0.5f),
                "residential" to (1 to 0.25f),
                "home care" to (3 to 0.75f)
            )

            types.forEach { (label, countPair) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(label, fontSize = 12.sp, color = BaseTextMuted, modifier = Modifier.width(110.dp))
                    Box(modifier = Modifier.weight(1f).height(16.dp)) {
                        LinearProgressIndicator(
                            progress = { countPair.second },
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFFF472B6),
                            trackColor = Color(0xFF1E283F)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("${countPair.first}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                }
            }
        }
    }
}

@Composable
private fun RiskHeatmapCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BaseSurface)
            .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.GridOn, contentDescription = null, tint = StatusCritText, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Risk Heatmap — Incidents by Category × Month", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
        }
        Spacer(modifier = Modifier.height(16.dp))

        val hScroll = rememberScrollState()
        Column(modifier = Modifier.fillMaxWidth().horizontalScroll(hScroll)) {
            // Header Row
            Row(
                modifier = Modifier.padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Category", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BaseTextMuted, modifier = Modifier.width(160.dp))
                listOf("May", "Jun", "Jul", "Aug", "Sept", "Oct", "Total").forEach { m ->
                    Text(m, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BaseTextMuted, modifier = Modifier.width(44.dp))
                }
            }

            val heatmapData = listOf(
                "Fall" to mapOf("Aug" to 1, "Total" to 1),
                "Medication Error" to mapOf("Aug" to 1, "Total" to 1),
                "Aggression" to mapOf(),
                "Abscond" to mapOf(),
                "Clinical Deterioration" to mapOf("Aug" to 1, "Total" to 1),
                "Safeguarding" to mapOf(),
                "Other" to mapOf()
            )

            heatmapData.forEach { (cat, counts) ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(cat, fontSize = 13.sp, color = BaseTextBody, modifier = Modifier.width(160.dp))
                    listOf("May", "Jun", "Jul", "Aug", "Sept", "Oct", "Total").forEach { m ->
                        val count = counts[m]
                        Box(
                            modifier = Modifier
                                .size(width = 44.dp, height = 28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (count != null) StatusCritText else Color(0xFF131A2D))
                                .border(1.dp, Color(0xFF1E283F), RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (count != null) {
                                Text("$count", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
