package com.example.ui.manager

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Send
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
import com.example.model.ManagerNotification
import com.example.ui.theme.*

@Composable
fun NotifyLineManagerScreen(
    repository: CareRepository
) {
    val managerNotifications by repository.managerNotifications.collectAsState()

    var noticeType by remember { mutableStateOf("Unavailability") }
    var subject by remember { mutableStateOf("") }
    var shiftDate by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var typeDropdownOpen by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(BasePageBg)) {
        val isDesktop = maxWidth >= 1024.dp
        val contentPadding = if (isDesktop) 32.dp else 16.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .testTag("notify_manager_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Header Row: Title & Subtitle
            item {
                Column {
                    Text(
                        text = "Notify Line Manager",
                        fontSize = if (isDesktop) 30.sp else 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = BaseTextHeading
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Staff notices from your team. Acknowledge them as you action them.",
                        fontSize = 14.sp,
                        color = BaseTextMuted
                    )
                }
            }

            // 2 Cards Side-by-side on desktop, or stacked on mobile
            item {
                if (isDesktop) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        SendNoticeCard(
                            noticeType = noticeType,
                            onNoticeTypeChange = { noticeType = it },
                            subject = subject,
                            onSubjectChange = { subject = it },
                            shiftDate = shiftDate,
                            onShiftDateChange = { shiftDate = it },
                            message = message,
                            onMessageChange = { message = it },
                            typeDropdownOpen = typeDropdownOpen,
                            onTypeDropdownToggle = { typeDropdownOpen = it },
                            onSend = {
                                if (message.isNotBlank()) {
                                    repository.submitManagerNotification(
                                        ManagerNotification(
                                            type = noticeType,
                                            subject = subject.ifEmpty { noticeType },
                                            message = message,
                                            shiftDate = shiftDate.ifEmpty { null }
                                        )
                                    )
                                    subject = ""
                                    shiftDate = ""
                                    message = ""
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )

                        YourNoticesCard(
                            notifications = managerNotifications,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SendNoticeCard(
                            noticeType = noticeType,
                            onNoticeTypeChange = { noticeType = it },
                            subject = subject,
                            onSubjectChange = { subject = it },
                            shiftDate = shiftDate,
                            onShiftDateChange = { shiftDate = it },
                            message = message,
                            onMessageChange = { message = it },
                            typeDropdownOpen = typeDropdownOpen,
                            onTypeDropdownToggle = { typeDropdownOpen = it },
                            onSend = {
                                if (message.isNotBlank()) {
                                    repository.submitManagerNotification(
                                        ManagerNotification(
                                            type = noticeType,
                                            subject = subject.ifEmpty { noticeType },
                                            message = message,
                                            shiftDate = shiftDate.ifEmpty { null }
                                        )
                                    )
                                    subject = ""
                                    shiftDate = ""
                                    message = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        YourNoticesCard(
                            notifications = managerNotifications,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SendNoticeCard(
    noticeType: String,
    onNoticeTypeChange: (String) -> Unit,
    subject: String,
    onSubjectChange: (String) -> Unit,
    shiftDate: String,
    onShiftDateChange: (String) -> Unit,
    message: String,
    onMessageChange: (String) -> Unit,
    typeDropdownOpen: Boolean,
    onTypeDropdownToggle: (Boolean) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BaseSurface)
            .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Send a notice", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)

        // Notice type dropdown
        Column {
            Text("Notice type", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = noticeType,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { onTypeDropdownToggle(true) }) {
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
                    modifier = Modifier.fillMaxWidth().clickable { onTypeDropdownToggle(true) }
                )
                DropdownMenu(
                    expanded = typeDropdownOpen,
                    onDismissRequest = { onTypeDropdownToggle(false) },
                    modifier = Modifier.background(BaseModalBg)
                ) {
                    listOf("Unavailability", "Shift query", "General").forEach { t ->
                        DropdownMenuItem(
                            text = { Text(t, color = BaseTextHeading) },
                            onClick = {
                                onNoticeTypeChange(t)
                                onTypeDropdownToggle(false)
                            }
                        )
                    }
                }
            }
        }

        // Subject and Shift date
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Subject", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = subject,
                    onValueChange = onSubjectChange,
                    placeholder = { Text("e.g. Unavailable Thursday", color = BaseTextFaint, fontSize = 13.sp) },
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
                Text("Shift date (optional)", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = shiftDate,
                    onValueChange = onShiftDateChange,
                    placeholder = { Text("dd/mm/yyyy", color = BaseTextFaint, fontSize = 13.sp) },
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

        // Message textarea
        Column {
            Text("Message", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = message,
                onValueChange = onMessageChange,
                placeholder = { Text("Describe your concern or availability...", color = BaseTextFaint, fontSize = 13.sp) },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BaseModalInput,
                    unfocusedContainerColor = BaseModalInput,
                    focusedTextColor = BaseTextHeading,
                    unfocusedTextColor = BaseTextHeading,
                    focusedBorderColor = BaseEmerald500,
                    unfocusedBorderColor = Color(0xFF334155)
                ),
                modifier = Modifier.fillMaxWidth().height(110.dp)
            )
        }

        Button(
            onClick = onSend,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
            modifier = Modifier.height(44.dp)
        ) {
            Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Send to manager", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    }
}

@Composable
private fun YourNoticesCard(
    notifications: List<ManagerNotification>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BaseSurface)
            .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Text("Your notices", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
        Spacer(modifier = Modifier.height(16.dp))

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(160.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No notices yet.", fontSize = 14.sp, color = BaseTextMuted)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                notifications.forEach { notice ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(BaseTileChip)
                            .border(1.dp, Color(0xFF1E283F), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(notice.subject, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                            Text(notice.status, fontSize = 12.sp, color = BaseEmerald300)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(notice.message, fontSize = 13.sp, color = BaseTextBody)
                    }
                }
            }
        }
    }
}
