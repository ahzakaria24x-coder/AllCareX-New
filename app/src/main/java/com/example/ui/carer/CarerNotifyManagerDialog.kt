package com.example.ui.carer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CareRepository
import com.example.model.UserRole
import com.example.ui.theme.CareMintTeal

@Composable
fun CarerNotifyManagerDialog(
    repository: CareRepository,
    onDismiss: () -> Unit
) {
    val currentUser by repository.currentUser.collectAsState()
    val effectiveStaffId = if (currentUser.role == UserRole.CARE_WORKER) currentUser.id else "staff-04"

    var selectedCategory by remember { mutableStateOf("Safety / Client Concern") }
    var detailsText by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }

    val categories = listOf(
        "Safety / Client Concern",
        "Upcoming Unavailability",
        "Mileage / Travel Query",
        "General Management Request"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Notify My Manager", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                if (isSubmitted) {
                    Text("✓ Message successfully transmitted to on-call manager Sarah Jenkins. Recorded in audit log.", color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                } else {
                    Text("Select notice category and provide details for immediate coordination review:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    categories.forEach { cat ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 40.dp)
                                .clickable { selectedCategory = cat }
                        ) {
                            RadioButton(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                colors = RadioButtonDefaults.colors(selectedColor = CareMintTeal)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(cat, fontSize = 12.sp, fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = detailsText,
                        onValueChange = { detailsText = it },
                        label = { Text("Details / Reason") },
                        placeholder = { Text("Describe the concern or unavailability dates...") },
                        modifier = Modifier.fillMaxWidth().testTag("notify_manager_input"),
                        minLines = 3
                    )
                }
            }
        },
        confirmButton = {
            if (!isSubmitted) {
                Button(
                    onClick = {
                        if (detailsText.isNotBlank()) {
                            repository.sendManagerNotification(
                                staffId = effectiveStaffId,
                                staffName = currentUser.name,
                                category = selectedCategory,
                                details = detailsText
                            )
                            isSubmitted = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                    modifier = Modifier.heightIn(min = 48.dp).testTag("submit_manager_notice_button")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color(0xFF0A1128), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Send to Manager", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            } else {
                Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal), modifier = Modifier.heightIn(min = 48.dp)) {
                    Text("Done", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (!isSubmitted) {
                TextButton(onClick = onDismiss, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text("Cancel")
                }
            }
        }
    )
}
