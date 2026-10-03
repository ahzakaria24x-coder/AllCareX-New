package com.example.ui.workforce

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.model.StaffProfile
import com.example.ui.theme.CareMintTeal
import com.example.ui.theme.CareMintTealContainer

@Composable
fun WorkforceScreen(
    repository: CareRepository
) {
    val staffList by repository.staffList.collectAsState()
    val trainingList by repository.trainingList.collectAsState()
    var activeSubTab by remember { mutableStateOf(0) } // 0: Staff & Compliance, 1: Mandatory Training Matrix

    val compliantCount = staffList.count { it.compliancePercentage >= 90 }
    val warningCount = staffList.count { it.compliancePercentage in 75..89 }
    val blockedCount = staffList.count { it.compliancePercentage < 75 }

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
                    text = "Workforce & Compliance",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "DBS Update Service, Supervision schedule & Training Matrix",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Traffic Light Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = CareMintTealContainer.copy(alpha = 0.5f),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("COMPLIANT (90%+)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                    Text("$compliantCount Staff", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = CareMintTeal)
                }
            }
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("RENEWAL SOON", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                    Text("$warningCount Staff", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFF59E0B))
                }
            }
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFEF4444).copy(alpha = 0.2f),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("BLOCKED (<75%)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                    Text("$blockedCount Staff", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFEF4444))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Subtabs
        TabRow(
            selectedTabIndex = activeSubTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = CareMintTeal,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = activeSubTab == 0,
                onClick = { activeSubTab = 0 },
                text = { Text("Staff & DBS Status (${staffList.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeSubTab == 1,
                onClick = { activeSubTab = 1 },
                text = { Text("Mandatory Training Matrix", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (activeSubTab) {
            0 -> {
                // Staff Directory with DBS and Supervisions
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(staffList) { staff ->
                        StaffCardItem(staff = staff)
                    }
                }
            }

            1 -> {
                // Training Matrix Grid
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CareMintTeal.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = CareMintTeal, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("The Care Certificate (15 Standards)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                                }
                                Text("Skills for Care UK benchmark completed by 11 of 12 registered care workers.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Statutory UK Care Training Records",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    items(trainingList) { record ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (record.isExpired) Color(0xFFEF4444).copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = record.moduleName, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "Ref: ${record.certificateRef} • Completed: ${record.completedDate} • Expiry: ${record.expiryDate}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (record.isExpired) Color(0xFFEF4444).copy(alpha = 0.2f) else CareMintTealContainer
                                ) {
                                    Text(
                                        text = if (record.isExpired) "EXPIRED (Block)" else "VALID (${record.scorePercentage}%)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (record.isExpired) Color(0xFFEF4444) else CareMintTeal,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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
private fun StaffCardItem(staff: StaffProfile) {
    val complianceColor = when {
        staff.compliancePercentage >= 90 -> CareMintTeal
        staff.compliancePercentage >= 75 -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (staff.compliancePercentage < 75) Color(0xFFEF4444).copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        ),
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(staff.role.badgeColor)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = staff.fullName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = staff.fullName, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(text = "${staff.role.displayName} • ${staff.contractType.displayName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "${staff.email} • ${staff.phone}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = complianceColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "${staff.compliancePercentage}% Compliance",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = complianceColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            // DBS & Supervisions Info Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Enhanced DBS", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(staff.dbsCertificateNumber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (staff.dbsUpdateServiceRegistered) "Update Service: Active ✓" else "Update Service: No",
                        fontSize = 9.sp,
                        color = if (staff.dbsUpdateServiceRegistered) CareMintTeal else Color(0xFFEF4444)
                    )
                }
                Column {
                    Text("Next Supervision", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(staff.nextSupervisionDueDate, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Text("Appraisal: ${staff.annualAppraisalDueDate}", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Column {
                    Text("Weekly Hours", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${staff.hoursWorkedThisWeek}h / 48h WTD", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            if (staff.compliancePercentage < 75) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFEF4444).copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Block, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ROSTERING BLOCKED: DBS check or mandatory training expired. Recertification required.",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                    }
                }
            }
        }
    }
}
