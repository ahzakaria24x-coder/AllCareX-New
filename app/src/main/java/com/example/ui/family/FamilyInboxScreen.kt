package com.example.ui.family

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.ui.theme.*

data class ChatMessageItem(
    val id: String,
    val sender: String,
    val text: String,
    val time: String,
    val isFromStaff: Boolean
)

@Composable
fun FamilyInboxScreen(
    repository: CareRepository
) {
    var replyText by remember { mutableStateOf("") }
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessageItem(
                    id = "msg-1",
                    sender = "All Care X Team",
                    text = "Hello Susan, Margaret had a good morning — she was up and dressed and ate a full breakfast. Her blood sugar was within target range.",
                    time = "31 Aug, 10:04",
                    isFromStaff = true
                ),
                ChatMessageItem(
                    id = "msg-2",
                    sender = "Susan Holloway",
                    text = "That's wonderful to hear, thank you. Will she have the district nurse visit this week?",
                    time = "31 Aug, 10:04",
                    isFromStaff = false
                ),
                ChatMessageItem(
                    id = "msg-3",
                    sender = "All Care X Team",
                    text = "Yes — the district nurse is scheduled for Thursday at 11am. We'll share an update afterwards.",
                    time = "31 Aug, 10:04",
                    isFromStaff = true
                )
            )
        )
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(BasePageBg)) {
        val isDesktop = maxWidth >= 1024.dp
        val contentPadding = if (isDesktop) 32.dp else 16.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .testTag("family_inbox_screen")
        ) {
            // Header Row: Title & Subtitle
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                Text(
                    text = "Family Messages",
                    fontSize = if (isDesktop) 30.sp else 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = BaseTextHeading
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Inbox of conversations with family & friends.",
                    fontSize = 14.sp,
                    color = BaseTextMuted
                )
            }

            if (isDesktop) {
                // 2-Column Layout on Desktop
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Conversation List
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(BaseSurface)
                            .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        ConversationSnippetItem(
                            clientName = "Margaret Holloway",
                            contactName = "Susan Holloway — child",
                            snippet = "Yes — the district nurse is scheduled for Thursday at 11...",
                            isSelected = true
                        )
                    }

                    // Right Thread & Reply Area
                    Column(
                        modifier = Modifier
                            .weight(2f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(BaseSurface)
                            .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
                            .padding(20.dp)
                    ) {
                        MessageThreadHeader(clientName = "Margaret Holloway", email = "susan.holloway@example.com")
                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(messages) { msg ->
                                MessageBubble(msg)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        ReplyInputRow(
                            replyText = replyText,
                            onReplyChange = { replyText = it },
                            onSend = {
                                if (replyText.isNotBlank()) {
                                    messages = messages + ChatMessageItem(
                                        id = "msg-${System.currentTimeMillis()}",
                                        sender = "All Care X Team",
                                        text = replyText,
                                        time = "Just now",
                                        isFromStaff = true
                                    )
                                    replyText = ""
                                }
                            }
                        )
                    }
                }
            } else {
                // Single Column Mobile Layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BaseSurface)
                        .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    ConversationSnippetItem(
                        clientName = "Margaret Holloway",
                        contactName = "Susan Holloway — child",
                        snippet = "Yes — the district nurse is scheduled for Thursday at 11...",
                        isSelected = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    MessageThreadHeader(clientName = "Margaret Holloway", email = "susan.holloway@example.com")
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(messages) { msg ->
                            MessageBubble(msg)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    ReplyInputRow(
                        replyText = replyText,
                        onReplyChange = { replyText = it },
                        onSend = {
                            if (replyText.isNotBlank()) {
                                messages = messages + ChatMessageItem(
                                    id = "msg-${System.currentTimeMillis()}",
                                    sender = "All Care X Team",
                                    text = replyText,
                                    time = "Just now",
                                    isFromStaff = true
                                )
                                replyText = ""
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ConversationSnippetItem(
    clientName: String,
    contactName: String,
    snippet: String,
    isSelected: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFF131A2F) else Color.Transparent)
            .border(1.dp, if (isSelected) BaseEmerald500 else Color(0xFF1E2638), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Text(clientName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
        Text(contactName, fontSize = 13.sp, color = BaseEmerald300)
        Spacer(modifier = Modifier.height(4.dp))
        Text(snippet, fontSize = 12.sp, color = BaseTextMuted, maxLines = 1)
    }
}

@Composable
private fun MessageThreadHeader(clientName: String, email: String) {
    Column {
        Text(clientName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
        Text(email, fontSize = 12.sp, color = BaseTextMuted)
    }
}

@Composable
private fun MessageBubble(msg: ChatMessageItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (msg.isFromStaff) Color(0xFF151D33) else Color(0xFF1E2742))
            .border(1.dp, Color(0xFF243050), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(msg.sender, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (msg.isFromStaff) BaseEmerald300 else StatusInfoText)
            Text(msg.time, fontSize = 11.sp, color = BaseTextFaint)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(msg.text, fontSize = 13.sp, color = BaseTextBody, lineHeight = 18.sp)
    }
}

@Composable
private fun ReplyInputRow(
    replyText: String,
    onReplyChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = replyText,
            onValueChange = onReplyChange,
            placeholder = { Text("Reply to the family...", color = BaseTextFaint, fontSize = 13.sp) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BaseModalInput,
                unfocusedContainerColor = BaseModalInput,
                focusedTextColor = BaseTextHeading,
                unfocusedTextColor = BaseTextHeading,
                focusedBorderColor = BaseEmerald500,
                unfocusedBorderColor = Color(0xFF334155)
            ),
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = onSend,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
            modifier = Modifier.size(48.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
        }
    }
}
