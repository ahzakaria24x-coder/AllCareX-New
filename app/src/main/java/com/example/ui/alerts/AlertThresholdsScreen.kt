package com.example.ui.alerts

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
import com.example.ui.analytics.AlertThresholdRule
import com.example.ui.common.AiChip
import com.example.ui.common.NeutralPill
import com.example.ui.common.StatusPill
import com.example.ui.theme.*

@Composable
fun AlertThresholdsScreen(
    repository: CareRepository
) {
    var rules by remember {
        mutableStateOf(
            listOf(
                AlertThresholdRule(
                    name = "Missed Visits Critical Rate",
                    metricKey = "missed_visit_rate",
                    condition = "GREATER_THAN",
                    thresholdValue = 2.0,
                    currentValue = 0.4,
                    unit = "%",
                    notifyRoles = listOf("Coordinator", "Registered Manager"),
                    isBreached = false,
                    aiRecommendation = "Current missed rate is 0.4% (well within 2.0% threshold). Maintaining current contingency driver standby is recommended."
                ),
                AlertThresholdRule(
                    name = "Staff Mandatory Training Compliance Baseline",
                    metricKey = "training_compliance_pct",
                    condition = "LESS_THAN",
                    thresholdValue = 90.0,
                    currentValue = 89.0,
                    unit = "%",
                    notifyRoles = listOf("HR & Workforce", "Registered Manager"),
                    isBreached = true,
                    aiRecommendation = "BREACH: Training compliance dropped to 89%. 1 carer (Daniel Okafor) has expired Moving & Handling module; booking practical refresher course booked for 12/10/2026."
                ),
                AlertThresholdRule(
                    name = "Unallocated Care Hours Pipeline Threshold",
                    metricKey = "unallocated_hours",
                    condition = "GREATER_THAN",
                    thresholdValue = 10.0,
                    currentValue = 14.5,
                    unit = "hours",
                    notifyRoles = listOf("Coordinator"),
                    isBreached = true,
                    aiRecommendation = "BREACH: 14.5 unallocated hours on next week rota. Auto-allocate matching skills to Tom Brennan and Lucia Marques who have 12.0h capacity available."
                ),
                AlertThresholdRule(
                    name = "NEWS2 Clinical Deterioration Escalation",
                    metricKey = "news2_score",
                    condition = "GREATER_THAN",
                    thresholdValue = 5.0,
                    currentValue = 7.0,
                    unit = "score",
                    notifyRoles = listOf("Registered Nurse", "Registered Manager"),
                    isBreached = true,
                    aiRecommendation = "BREACH: Doris Penrose recorded NEWS2 score of 7 (Breathlessness). GP urgent review requested and District Nurse visit confirmed for Thursday 11:00."
                ),
                AlertThresholdRule(
                    name = "Overdue Client & Commissioner Debtors",
                    metricKey = "overdue_debtors_gbp",
                    condition = "GREATER_THAN",
                    thresholdValue = 5000.0,
                    currentValue = 4250.0,
                    unit = "£",
                    notifyRoles = listOf("Finance & Billing"),
                    isBreached = false,
                    aiRecommendation = "Current aged debt is £4,250 (below £5,000 threshold). Reminder statement sent to North Yorkshire County Council."
                ),
                AlertThresholdRule(
                    name = "Lone-Worker Overdue Check-in Safety Escalation",
                    metricKey = "lone_worker_delay",
                    condition = "GREATER_THAN",
                    thresholdValue = 15.0,
                    currentValue = 0.0,
                    unit = "mins",
                    notifyRoles = listOf("Coordinator", "On-Call Manager"),
                    isBreached = false,
                    aiRecommendation = "Zero active lone-worker overdue breaches. All field staff GPS heartbeats verified."
                )
            )
        )
    }

    var showAddRuleModal by remember { mutableStateOf(false) }
    var newRuleName by remember { mutableStateOf("") }
    var newCondition by remember { mutableStateOf("GREATER_THAN") }
    var newThreshold by remember { mutableStateOf("") }
    var newUnit by remember { mutableStateOf("%") }
    var newTargetRole by remember { mutableStateOf("Registered Manager") }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(BasePageBg)) {
        val isDesktop = maxWidth >= 1024.dp
        val contentPadding = if (isDesktop) 32.dp else 16.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .testTag("alert_thresholds_screen"),
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
                            text = "Alert Thresholds & Escalation Rules",
                            fontSize = if (isDesktop) 30.sp else 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = BaseTextHeading
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Define automated trigger levels that notify designated managerial and clinical roles.",
                            fontSize = 14.sp,
                            color = BaseTextMuted
                        )
                    }

                    Button(
                        onClick = { showAddRuleModal = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                        modifier = Modifier.height(44.dp).testTag("add_rule_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("New Alert Rule", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }

            // Summary stats banner
            item {
                val breachedCount = rules.count { it.isBreached && it.isEnabled }
                val totalActive = rules.count { it.isEnabled }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (breachedCount > 0) StatusCritBg else StatusLowBg)
                        .border(1.dp, if (breachedCount > 0) StatusCritBorder else StatusLowBorder, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (breachedCount > 0) Icons.Default.Warning else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (breachedCount > 0) StatusCritText else BaseEmerald300,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (breachedCount > 0) "$breachedCount Threshold Breaches Detected" else "All Thresholds Normal",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (breachedCount > 0) StatusCritText else BaseEmerald300
                            )
                            Text(
                                text = "$totalActive active monitored rules across operations, clinical, workforce and finance.",
                                fontSize = 12.sp,
                                color = BaseTextMuted
                            )
                        }
                    }

                    NeutralPill(text = "$breachedCount Breached / $totalActive Active")
                }
            }

            // Rules List Cards
            items(rules) { rule ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BaseSurface)
                        .border(1.dp, if (rule.isBreached && rule.isEnabled) StatusCritBorder else BaseBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header: Rule name, Breached / Normal Pill, Enabled Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (rule.isBreached && rule.isEnabled) StatusCritBg else StatusLowBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (rule.isBreached && rule.isEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                                    contentDescription = null,
                                    tint = if (rule.isBreached && rule.isEnabled) StatusCritText else BaseEmerald300,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(rule.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                                Text(
                                    text = "Condition: ${if (rule.condition == "GREATER_THAN") ">" else "<"} ${rule.thresholdValue}${rule.unit} • Current Value: ${rule.currentValue}${rule.unit}",
                                    fontSize = 12.sp,
                                    color = BaseTextMuted
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            StatusPill(
                                status = if (!rule.isEnabled) "DISABLED" else if (rule.isBreached) "BREACHED" else "NORMAL",
                                color = if (!rule.isEnabled) BaseTextFaint else if (rule.isBreached) StatusCritText else BaseEmerald300
                            )

                            Switch(
                                checked = rule.isEnabled,
                                onCheckedChange = { isChecked ->
                                    rules = rules.map { if (it.id == rule.id) it.copy(isEnabled = isChecked) else it }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = BaseEmerald500,
                                    uncheckedThumbColor = BaseTextFaint,
                                    uncheckedTrackColor = BaseModalInput
                                )
                            )
                        }
                    }

                    // Notification Routing Recipients
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Notifies: ", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = BaseTextMuted)
                        rule.notifyRoles.forEach { role ->
                            NeutralPill(text = role, modifier = Modifier.padding(end = 6.dp))
                        }
                    }

                    // AI Actionable Recommendation Line
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AiChipBg)
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AiChip(text = "AI Recommendation")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = rule.aiRecommendation,
                            fontSize = 12.sp,
                            color = AiChipText,
                            lineHeight = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Modal: Add Alert Rule
        if (showAddRuleModal) {
            AlertDialog(
                onDismissRequest = { showAddRuleModal = false },
                containerColor = BaseModalBg,
                shape = RoundedCornerShape(16.dp),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("New Alert Threshold Rule", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                        IconButton(onClick = { showAddRuleModal = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = BaseTextMuted)
                        }
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column {
                            Text("Rule Name", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = newRuleName,
                                onValueChange = { newRuleName = it },
                                placeholder = { Text("e.g. Falls Spike Threshold", color = BaseTextFaint, fontSize = 13.sp) },
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

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Condition", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    listOf(">" to "GREATER_THAN", "<" to "LESS_THAN").forEach { (symbol, cond) ->
                                        val isSel = newCondition == cond
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSel) BaseEmerald500 else BaseTileChip)
                                                .border(1.dp, if (isSel) BaseEmerald300 else Color(0xFF1E283F), RoundedCornerShape(8.dp))
                                                .clickable { newCondition = cond }
                                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                        ) {
                                            Text(symbol, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else BaseTextMuted)
                                        }
                                    }
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Threshold Value", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = newThreshold,
                                    onValueChange = { newThreshold = it },
                                    placeholder = { Text("e.g. 3.0", color = BaseTextFaint, fontSize = 13.sp) },
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

                        Column {
                            Text("Notify Target Role", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("Registered Manager", "Coordinator", "Nurse", "Finance").forEach { r ->
                                    val isR = newTargetRole == r
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isR) Color(0xFF16233B) else BaseTileChip)
                                            .border(1.dp, if (isR) BaseEmerald300 else Color(0xFF1E283F), RoundedCornerShape(8.dp))
                                            .clickable { newTargetRole = r }
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Text(r, fontSize = 11.sp, color = if (isR) BaseEmerald300 else BaseTextMuted)
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newRule = AlertThresholdRule(
                                name = newRuleName.ifEmpty { "Custom Monitored Alert" },
                                metricKey = "custom_metric",
                                condition = newCondition,
                                thresholdValue = newThreshold.toDoubleOrNull() ?: 5.0,
                                currentValue = 2.0,
                                unit = newUnit,
                                notifyRoles = listOf(newTargetRole),
                                isBreached = false,
                                aiRecommendation = "Custom rule initialized. Automated telemetry active."
                            )
                            rules = listOf(newRule) + rules
                            showAddRuleModal = false
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Add Rule", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddRuleModal = false }) {
                        Text("Cancel", color = BaseTextMuted)
                    }
                }
            )
        }
    }
}
