package com.example.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import com.example.ui.common.NeutralPill
import com.example.ui.common.StatusPill
import com.example.ui.theme.*

@Composable
fun ChartDrillDownDialog(
    title: String,
    metricDescription: String,
    records: List<DrillDownRecord>,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredRecords = records.filter {
        searchQuery.isEmpty() ||
            it.primaryText.contains(searchQuery, ignoreCase = true) ||
            it.secondaryText.contains(searchQuery, ignoreCase = true) ||
            it.category.contains(searchQuery, ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BaseModalBg,
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                    Text(metricDescription, fontSize = 12.sp, color = BaseTextMuted)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = BaseTextMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filter records...", color = BaseTextFaint, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BaseTextFaint, modifier = Modifier.size(18.dp)) },
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BaseModalInput,
                        unfocusedContainerColor = BaseModalInput,
                        focusedTextColor = BaseTextHeading,
                        unfocusedTextColor = BaseTextHeading,
                        focusedBorderColor = BaseEmerald500,
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                )

                Text(
                    text = "Showing ${filteredRecords.size} underlying records",
                    fontSize = 12.sp,
                    color = BaseTextFaint
                )

                // List of Records
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredRecords) { rec ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(BaseTileChip)
                                .border(1.dp, Color(0xFF1E283F), RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(rec.primaryText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                                Text(rec.secondaryText, fontSize = 12.sp, color = BaseTextMuted)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("${rec.category} • ${rec.timestamp}", fontSize = 11.sp, color = BaseTextFaint)
                            }

                            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                NeutralPill(text = rec.valueBadge)
                                StatusPill(
                                    status = rec.status,
                                    color = when (rec.status.lowercase()) {
                                        "completed", "on track", "normal", "pass" -> BaseEmerald300
                                        "late", "warning", "investigating" -> StatusMedText
                                        "missed", "breached", "critical", "fail" -> StatusCritText
                                        else -> StatusInfoText
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                modifier = Modifier.height(40.dp)
            ) {
                Text("Close", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}
