package com.example.ui.family

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
import com.example.ui.theme.CareMintTeal
import com.example.ui.theme.CareMintTealContainer

@Composable
fun FamilyPortalScreen(
    repository: CareRepository
) {
    val familyMessages by repository.familyMessages.collectAsState()
    val clients by repository.clients.collectAsState()
    val visits by repository.visits.collectAsState()

    var messageInput by remember { mutableStateOf("") }
    var activeSubTab by remember { mutableStateOf(0) } // 0: Care Updates, 1: Secure Messages

    val arthur = clients.find { it.id == "client-01" } ?: clients.first()
    val arthurVisits = visits.filter { it.clientId == arthur.id }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Family Portal Banner
        Surface(
            shape = RoundedCornerShape(16.dp),
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
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = CareMintTeal, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FAMILY & CIRCLE OF CARE PORTAL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CareMintTeal,
                                letterSpacing = 0.6.sp
                            )
                        }
                        Text(
                            text = "Arthur Pendelton's Care Updates",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Logged in as Clara Pendelton (Daughter / LPA)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CareMintTealContainer.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "Consent Verified ✓",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = CareMintTeal,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Navigation Tabs
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
                text = { Text("Daily Care & Nutrition", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeSubTab == 1,
                onClick = { activeSubTab = 1 },
                text = { Text("Secure Messaging (${familyMessages.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (activeSubTab) {
            0 -> {
                // Care summaries & nutrition
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Hydration & Nutrition Today", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Total Fluids Recorded", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("750 ml / 1500 ml goal", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = CareMintTeal)
                                    }
                                    Column {
                                        Text("Appetite & Mood", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("Good / Cheerful", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text("Today's Carer Visit Log", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    items(arthurVisits) { v ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "${v.startTime} - ${v.endTime}: ${v.visitType}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = v.status.displayName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(v.status.colorHex))
                                }
                                Text(text = "Carer: ${v.carerName ?: "David Okonkwo"}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (!v.dailyNotes.isNullOrBlank()) v.dailyNotes!! else "Assisted with washing and dressing in warm clothes. Porridge prepared and eaten. Morning medications taken with tea.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            1 -> {
                // Secure Messaging Thread
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(familyMessages) { msg ->
                            val isFromFamily = msg.senderRole.contains("Family")
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = if (isFromFamily) Alignment.End else Alignment.Start
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isFromFamily) CareMintTealContainer else MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isFromFamily) CareMintTeal.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                                    modifier = Modifier.widthIn(max = 280.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(text = "${msg.senderName} (${msg.senderRole})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isFromFamily) CareMintTeal else MaterialTheme.colorScheme.primary)
                                        Text(text = msg.messageText, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                        Text(text = msg.timestamp, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), modifier = Modifier.align(Alignment.End))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Input Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            placeholder = { Text("Send secure message to Care Team...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("family_message_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (messageInput.isNotBlank()) {
                                    repository.sendFamilyMessage(arthur.id, messageInput)
                                    messageInput = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(CareMintTeal)
                                .testTag("send_family_message_button")
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color(0xFF0A1128))
                        }
                    }
                }
            }
        }
    }
}
