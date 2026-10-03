package com.example.ui.carer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.model.HandoverDraft
import com.example.ui.theme.CareMintTeal
import com.example.ui.theme.CareMintTealContainer

@Composable
fun CarerHandoverDialog(
    repository: CareRepository,
    onDismiss: () -> Unit
) {
    val currentUser by repository.currentUser.collectAsState()
    val handovers by repository.handovers.collectAsState()

    val currentDraft = handovers.firstOrNull() ?: remember {
        repository.generateAiHandoverDraft(currentUser.id, currentUser.name)
    }

    var editedNotes by remember(currentDraft.id) {
        mutableStateOf(currentDraft.carerEditedNotes.ifBlank { currentDraft.aiGeneratedSummary })
    }
    var confirmedAccurate by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.AssignmentReturn, contentDescription = null, tint = CareMintTeal)
                Spacer(modifier = Modifier.width(6.dp))
                Text("End-of-Shift Handover Summary", fontWeight = FontWeight.ExtraBold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Shift: ${currentDraft.shiftType} • ${currentDraft.date}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Carer: ${currentDraft.carerName}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Stats Banner
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CareMintTealContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Calls: ${currentDraft.visitsCount}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Total Fluid: ${currentDraft.totalFluidMl}ml", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Status: ${if (currentDraft.isSubmitted) "SIGNED OFF" else "DRAFT"}", fontSize = 11.sp, color = CareMintTeal, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // AI-Assisted Auto-Draft Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CareMintTeal.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = CareMintTeal, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("AI-Assisted Handover Draft (Review & Edit):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                        }
                        Text(
                            text = currentDraft.aiGeneratedSummary,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Carer Verified Handover Notes (Editable):", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = editedNotes,
                    onValueChange = { editedNotes = it },
                    placeholder = { Text("Add specific notes for the next shift carer...") },
                    modifier = Modifier.fillMaxWidth().testTag("handover_notes_input"),
                    minLines = 4
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Statutory Review Confirmation
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .clickable { confirmedAccurate = !confirmedAccurate }
                ) {
                    Checkbox(
                        checked = confirmedAccurate,
                        onCheckedChange = { confirmedAccurate = it },
                        colors = CheckboxDefaults.colors(checkedColor = CareMintTeal),
                        modifier = Modifier.testTag("confirm_handover_checkbox")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "I have reviewed and edited this handover summary to confirm accurate reflection of delivered care.",
                        fontSize = 11.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    repository.saveAndSignHandover(currentDraft.id, editedNotes)
                    onDismiss()
                },
                enabled = confirmedAccurate,
                colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag("submit_handover_button")
            ) {
                Text("Sign & Submit Handover", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text("Close")
            }
        }
    )
}
