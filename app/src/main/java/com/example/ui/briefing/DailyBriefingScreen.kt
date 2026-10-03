package com.example.ui.briefing

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
import com.example.ui.common.NeutralPill
import com.example.ui.common.StatCard
import com.example.ui.common.StatusPill
import com.example.ui.theme.*

@Composable
fun DailyBriefingScreen(
    repository: CareRepository
) {
    val staffList by repository.staffList.collectAsState()
    var sentBriefs by remember { mutableStateOf(setOf<String>()) }
    var allSentNotice by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(BasePageBg)) {
        val isDesktop = maxWidth >= 1024.dp
        val contentPadding = if (isDesktop) 32.dp else 16.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .testTag("daily_briefing_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Header Row: Morning Briefing Tag, Title & Subtitle + Send all briefs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WbSunny, contentDescription = null, tint = BaseEmerald300, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MORNING BRIEFING",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BaseEmerald300,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Daily Visit Briefing",
                            fontSize = if (isDesktop) 30.sp else 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = BaseTextHeading
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Thursday, 1 October 2026",
                            fontSize = 14.sp,
                            color = BaseTextMuted
                        )
                    }

                    Button(
                        onClick = {
                            allSentNotice = true
                            sentBriefs = staffList.map { it.id }.toSet()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                        modifier = Modifier.height(44.dp).testTag("send_all_briefs_button")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send all briefs", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }

            // 4 Stat Cards
            item {
                if (isDesktop) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatCard("Visits today", "1", "", Icons.Default.CalendarToday, BaseTextHeading, Modifier.weight(1f))
                        StatCard("Staff on rota", "0", "", Icons.Default.Group, BaseTextHeading, Modifier.weight(1f))
                        StatCard("Assigned", "0", "", Icons.Default.CheckCircle, BaseTextHeading, Modifier.weight(1f))
                        StatCard("Unassigned", "1", "", Icons.Default.Warning, StatusCritText, Modifier.weight(1f))
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard("Visits today", "1", "", Icons.Default.CalendarToday, BaseTextHeading, Modifier.weight(1f))
                            StatCard("Staff on rota", "0", "", Icons.Default.Group, BaseTextHeading, Modifier.weight(1f))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard("Assigned", "0", "", Icons.Default.CheckCircle, BaseTextHeading, Modifier.weight(1f))
                            StatCard("Unassigned", "1", "", Icons.Default.Warning, StatusCritText, Modifier.weight(1f))
                        }
                    }
                }
            }

            // Unassigned Card
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(StatusMedBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("UN", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = StatusMedText)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Unassigned", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                                Text("1 visit today • no email on record", fontSize = 12.sp, color = BaseTextMuted)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Visit chip inside unassigned
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
                            Text("09:00 - 10:00", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text("Naim", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                                Text("routine", fontSize = 12.sp, color = BaseTextMuted)
                            }
                        }

                        StatusPill(status = "in_progress", color = DotInProgress)
                    }
                }
            }

            // Staff Cards List with "Send brief" buttons
            staffList.forEach { staff ->
                item {
                    val isSent = sentBriefs.contains(staff.id)

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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(StatusInfoBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        staff.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusInfoText
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(staff.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                                    Text("0 visits today • ${staff.email}", fontSize = 12.sp, color = BaseTextMuted)
                                }
                            }

                            Button(
                                onClick = { sentBriefs = sentBriefs + staff.id },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSent) BaseModalInput else BaseEmerald500
                                ),
                                border = if (isSent) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)) else null,
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSent) Icons.Default.Check else Icons.Default.Email,
                                    contentDescription = null,
                                    tint = if (isSent) BaseEmerald300 else Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isSent) "Brief sent" else "Send brief",
                                    fontSize = 12.sp,
                                    color = if (isSent) BaseTextMuted else Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No visits scheduled.", fontSize = 13.sp, color = BaseTextFaint)
                    }
                }
            }

            // Footer info note
            item {
                Text(
                    text = "Automated daily email delivery requires a Builder+ upgrade (scheduled backend functions). This page gives coordinators a one-click manual send each morning.",
                    fontSize = 12.sp,
                    color = BaseTextFaint,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        }
    }
}
