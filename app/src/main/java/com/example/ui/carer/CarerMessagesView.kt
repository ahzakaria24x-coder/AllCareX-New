package com.example.ui.carer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.model.OfficeMessage
import com.example.ui.theme.CareMintTeal
import com.example.ui.theme.CareMintTealContainer

@Composable
fun CarerMessagesView(
    repository: CareRepository,
    isHighContrast: Boolean,
    isLargeText: Boolean,
    onOpenNotifyManager: () -> Unit
) {
    val messages by repository.officeMessages.collectAsState()
    val textScale = if (isLargeText) 1.2f else 1.0f

    var pushEnabled by remember { mutableStateOf(true) }
    var simulatedPushNotice by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("carer_messages_view")
    ) {
        // Push Notifications & On-Call Readiness Card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isHighContrast) Color.White else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = CareMintTeal, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Push Notifications (PWA)",
                            fontSize = (13 * textScale).sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Switch(
                        checked = pushEnabled,
                        onCheckedChange = { pushEnabled = it },
                        modifier = Modifier.testTag("toggle_push_notifications_switch")
                    )
                }

                Text(
                    text = if (pushEnabled) "Real-time office alerts & urgent rota updates enabled." else "Push notifications paused.",
                    fontSize = (10 * textScale).sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Simulate incoming Push Notification Button (min 48dp)
                Button(
                    onClick = {
                        simulatedPushNotice = "OFFICE ALERT: Family requested arrival 10 mins earlier for Arthur Pendelton (SW1A 1AA) to assist before GP home visit."
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .testTag("simulate_push_notification_button")
                ) {
                    Icon(imageVector = Icons.Default.SendToMobile, contentDescription = null, tint = Color(0xFF0A1128), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Test Push Alert from Office", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold, fontSize = (11 * textScale).sp)
                }
            }
        }

        // Active Simulated Push Banner
        if (simulatedPushNotice != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF6366F1).copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF6366F1)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Message, contentDescription = null, tint = Color(0xFF6366F1))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("PUSH NOTIFICATION RECEIVED", fontSize = (10 * textScale).sp, fontWeight = FontWeight.Black, color = Color(0xFF6366F1))
                        Text(simulatedPushNotice!!, fontSize = (11 * textScale).sp, fontWeight = FontWeight.SemiBold)
                    }
                    IconButton(onClick = { simulatedPushNotice = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Messages Header & Notify Manager Trigger
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Office Messages (${messages.size})",
                fontSize = (15 * textScale).sp,
                fontWeight = FontWeight.Bold,
                color = if (isHighContrast) Color.White else MaterialTheme.colorScheme.onSurface
            )

            Button(
                onClick = onOpenNotifyManager,
                colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag("notify_manager_top_button")
            ) {
                Icon(imageVector = Icons.Default.ReportProblem, contentDescription = null, tint = Color(0xFF0A1128), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Notify Manager", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold, fontSize = (11 * textScale).sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(messages) { msg ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        if (msg.isUrgent) 1.5.dp else 1.dp,
                        if (msg.isUrgent) Color(0xFFEF4444) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { repository.markMessageRead(msg.id) }
                        .testTag("office_message_card_${msg.id}")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!msg.isRead) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CareMintTeal))
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = "${msg.senderName} (${msg.senderRole})",
                                    fontSize = (11 * textScale).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CareMintTeal
                                )
                            }

                            if (msg.isUrgent) {
                                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFEF4444).copy(alpha = 0.2f)) {
                                    Text("URGENT", fontSize = (9 * textScale).sp, fontWeight = FontWeight.Black, color = Color(0xFFEF4444), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = msg.subject,
                            fontSize = (14 * textScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = msg.messageText,
                            fontSize = (12 * textScale).sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Text(
                            text = msg.timestamp,
                            fontSize = (9 * textScale).sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}
