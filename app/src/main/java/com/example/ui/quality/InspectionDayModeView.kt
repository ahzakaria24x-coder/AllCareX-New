package com.example.ui.quality

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
import com.example.ui.theme.CareMintTeal
import com.example.ui.theme.CareMintTealContainer

@Composable
fun InspectionDayModeView(
    repository: CareRepository
) {
    val inspectionSession by repository.inspectionSession.collectAsState()

    var showEvidencePackDialog by remember { mutableStateOf(false) }
    var selectedKeyQuestionExport by remember { mutableStateOf("All Key Questions") }
    var dateRangeExport by remember { mutableStateOf("01/01/2026 - 03/10/2026") }
    var showPdfSuccessModal by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Inspection-Day Banner
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (inspectionSession.isSessionActive) Color(0xFF6366F1).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (inspectionSession.isSessionActive) Color(0xFF6366F1) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                ),
                modifier = Modifier.fillMaxWidth().testTag("inspection_day_mode_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color(0xFF6366F1), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "INSPECTION-DAY AUDITOR MODE",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF6366F1)
                                )
                            }
                            Text(
                                text = "Enables temporary read-only access for CQC / CIW inspectors with immutable session access logs.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = inspectionSession.isSessionActive,
                            onCheckedChange = { repository.toggleInspectionSession(it) },
                            modifier = Modifier.testTag("toggle_inspection_mode_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Auditor: ${inspectionSession.auditorName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Expires: ${inspectionSession.accessExpiresAt}",
                            fontSize = 11.sp,
                            color = CareMintTeal,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Evidence Pack Export Card
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Statutory Evidence Pack Export (PDF)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                            Text("Compiles audit results, training/DBS compliance, incidents, and policy acknowledgements into a formal bundle.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Button(
                            onClick = { showEvidencePackDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp).testTag("generate_evidence_pack_button")
                        ) {
                            Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFF0A1128), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Generate Pack", color = Color(0xFF0A1128), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Auditor Access Log
        item {
            Text(
                text = "Auditor Access Trail (${inspectionSession.accessLog.size} Events)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(inspectionSession.accessLog) { log ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "${log.sectionAccessed} • ${log.recordReference}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Auditor: ${log.auditorName}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(text = log.timestamp, fontSize = 9.sp, color = CareMintTeal, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Modal: Evidence Pack Date Range & Key Question Selector
    if (showEvidencePackDialog) {
        AlertDialog(
            onDismissRequest = { showEvidencePackDialog = false },
            title = { Text("Configure Evidence Pack Export", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Select regulatory domain and date range for the compiled inspection dossier.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = dateRangeExport,
                        onValueChange = { dateRangeExport = it },
                        label = { Text("Date Range") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Scope of Key Questions:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    listOf("All Key Questions (Full SAF)", "Safe Only", "Effective Only", "Well-Led Only").forEach { opt ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().clickable { selectedKeyQuestionExport = opt }
                        ) {
                            RadioButton(
                                selected = selectedKeyQuestionExport == opt,
                                onClick = { selectedKeyQuestionExport = opt },
                                colors = RadioButtonDefaults.colors(selectedColor = CareMintTeal)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(opt, fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.logAuditorAccess("Evidence Pack Export", "$selectedKeyQuestionExport ($dateRangeExport)")
                        showEvidencePackDialog = false
                        showPdfSuccessModal = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal)
                ) {
                    Text("Export Bundle (PDF)", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEvidencePackDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Modal: PDF Generated Confirmation
    if (showPdfSuccessModal) {
        AlertDialog(
            onDismissRequest = { showPdfSuccessModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = CareMintTeal)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Evidence Pack Ready", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("The CQC Provider Inspection Dossier has been compiled and rendered:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = CareMintTealContainer.copy(alpha = 0.5f), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("✓ 34 Quality Statements self-assessment narratives", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("✓ 7 Clinical Audits with SMART action completion logs", fontSize = 10.sp)
                            Text("✓ 100% Staff DBS & Mandatory Training matrices", fontSize = 10.sp)
                            Text("✓ Duty of Candour statutory letters & incident logs", fontSize = 10.sp)
                            Text("✓ Range: $dateRangeExport • Scope: $selectedKeyQuestionExport", fontSize = 10.sp, color = CareMintTeal)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showPdfSuccessModal = false }, colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal)) {
                    Text("Done", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
