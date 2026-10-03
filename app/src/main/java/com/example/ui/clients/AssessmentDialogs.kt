package com.example.ui.clients

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.model.News2Assessment
import com.example.model.WaterlowAssessment
import com.example.ui.theme.CareMintTeal
import java.util.UUID

@Composable
fun News2CalculatorDialog(
    clientId: String,
    onDismiss: () -> Unit,
    onSaveAssessment: (News2Assessment) -> Unit
) {
    var respRate by remember { mutableStateOf("16") }
    var spO2 by remember { mutableStateOf("96") }
    var systolicBp by remember { mutableStateOf("125") }
    var pulse by remember { mutableStateOf("72") }
    var temp by remember { mutableStateOf("36.8") }
    var isAlert by remember { mutableStateOf(true) }

    // Calculate NEWS2 subscores
    val rrVal = respRate.toIntOrNull() ?: 16
    val spo2Val = spO2.toIntOrNull() ?: 96
    val bpVal = systolicBp.toIntOrNull() ?: 125
    val pulseVal = pulse.toIntOrNull() ?: 72
    val tempVal = temp.toFloatOrNull() ?: 36.8f

    val rrScore = when {
        rrVal <= 8 -> 3
        rrVal in 9..11 -> 1
        rrVal in 12..20 -> 0
        rrVal in 21..24 -> 2
        else -> 3
    }
    val spo2Score = when {
        spo2Val <= 91 -> 3
        spo2Val in 92..93 -> 2
        spo2Val in 94..95 -> 1
        else -> 0
    }
    val bpScore = when {
        bpVal <= 90 -> 3
        bpVal in 91..100 -> 2
        bpVal in 101..110 -> 1
        bpVal in 111..219 -> 0
        else -> 3
    }
    val pulseScore = when {
        pulseVal <= 40 -> 3
        pulseVal in 41..50 -> 1
        pulseVal in 51..90 -> 0
        pulseVal in 91..110 -> 1
        pulseVal in 111..130 -> 2
        else -> 3
    }
    val cvpuScore = if (isAlert) 0 else 3
    val tempScore = when {
        tempVal <= 35.0f -> 3
        tempVal in 35.1f..36.0f -> 1
        tempVal in 36.1f..38.0f -> 0
        tempVal in 38.1f..39.0f -> 1
        else -> 2
    }

    val totalNews2 = rrScore + spo2Score + bpScore + pulseScore + cvpuScore + tempScore
    val riskBand = when {
        totalNews2 >= 7 -> "High Clinical Risk (Emergency escalation)"
        totalNews2 in 5..6 || (totalNews2 in 1..4 && (rrScore == 3 || bpScore == 3 || cvpuScore == 3)) -> "Medium Clinical Risk (Urgent GP/Clinical review)"
        totalNews2 in 1..4 -> "Low Clinical Risk (Ward-based vigilance)"
        else -> "Normal / Baseline"
    }

    val riskColor = when {
        totalNews2 >= 7 -> Color(0xFFEF4444)
        totalNews2 in 5..6 -> Color(0xFFF59E0B)
        totalNews2 in 1..4 -> Color(0xFF0284C7)
        else -> CareMintTeal
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "NEWS2 Clinical Assessment",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Score Result Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = riskColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, riskColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TOTAL NEWS2 SCORE: $totalNews2",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = riskColor
                        )
                        Text(
                            text = riskBand,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = respRate,
                    onValueChange = { respRate = it },
                    label = { Text("Respiration Rate (breaths/min) [Norm 12-20]") },
                    modifier = Modifier.fillMaxWidth().testTag("news2_rr")
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = spO2,
                    onValueChange = { spO2 = it },
                    label = { Text("Oxygen Saturation SpO2 (%) [Norm ≥96%]") },
                    modifier = Modifier.fillMaxWidth().testTag("news2_spo2")
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = systolicBp,
                    onValueChange = { systolicBp = it },
                    label = { Text("Systolic Blood Pressure (mmHg) [Norm 111-219]") },
                    modifier = Modifier.fillMaxWidth().testTag("news2_bp")
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = pulse,
                    onValueChange = { pulse = it },
                    label = { Text("Heart Pulse Rate (bpm) [Norm 51-90]") },
                    modifier = Modifier.fillMaxWidth().testTag("news2_pulse")
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = temp,
                    onValueChange = { temp = it },
                    label = { Text("Body Temperature (°C) [Norm 36.1-38.0]") },
                    modifier = Modifier.fillMaxWidth().testTag("news2_temp")
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Consciousness Level (ACVPU)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = isAlert,
                        onClick = { isAlert = true },
                        label = { Text("Alert (Score 0)") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = !isAlert,
                        onClick = { isAlert = false },
                        label = { Text("CVPU / Confused (Score 3)") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val assessment = News2Assessment(
                        clientId = clientId,
                        assessedDate = "03/10/2026",
                        respirationRate = rrVal,
                        oxygenSaturation = spo2Val,
                        systolicBp = bpVal,
                        pulseRate = pulseVal,
                        consciousnessAcvpu = if (isAlert) "Alert" else "CVPU",
                        temperature = tempVal,
                        totalScore = totalNews2,
                        riskBand = riskBand,
                        notes = "Routine clinical observation."
                    )
                    onSaveAssessment(assessment)
                    onDismiss()
                },
                modifier = Modifier.testTag("save_news2_button")
            ) {
                Text("Record NEWS2")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun WaterlowCalculatorDialog(
    clientId: String,
    onDismiss: () -> Unit,
    onSaveAssessment: (WaterlowAssessment) -> Unit
) {
    var buildWeight by remember { mutableStateOf(1) } // 0-3
    var skinType by remember { mutableStateOf(2) } // 0-3
    var mobility by remember { mutableStateOf(2) } // 0-5
    var continence by remember { mutableStateOf(1) } // 0-3
    var appetite by remember { mutableStateOf(1) } // 0-2

    val totalWaterlow = buildWeight + skinType + mobility + continence + appetite + 4 // base risk
    val riskCategory = when {
        totalWaterlow >= 20 -> "Very High Risk (20+)"
        totalWaterlow >= 15 -> "High Risk (15-19)"
        totalWaterlow >= 10 -> "At Risk (10-14)"
        else -> "Low Risk (<10)"
    }

    val riskColor = when {
        totalWaterlow >= 20 -> Color(0xFFEF4444)
        totalWaterlow >= 15 -> Color(0xFFF59E0B)
        else -> Color(0xFF10B981)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Waterlow Pressure Ulcer Assessment",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = riskColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, riskColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "WATERLOW SCORE: $totalWaterlow",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = riskColor
                        )
                        Text(
                            text = riskCategory,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Mobility Level", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilterChip(selected = mobility == 0, onClick = { mobility = 0 }, label = { Text("Fully Mobile", fontSize = 10.sp) })
                    FilterChip(selected = mobility == 2, onClick = { mobility = 2 }, label = { Text("Restless/Fidgety", fontSize = 10.sp) })
                    FilterChip(selected = mobility == 3, onClick = { mobility = 3 }, label = { Text("Apathetic/Chair", fontSize = 10.sp) })
                }
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilterChip(selected = mobility == 4, onClick = { mobility = 4 }, label = { Text("Restricted Bed", fontSize = 10.sp) })
                    FilterChip(selected = mobility == 5, onClick = { mobility = 5 }, label = { Text("Immobile", fontSize = 10.sp) })
                }

                Text("Skin Condition / Breakdown Risk", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilterChip(selected = skinType == 0, onClick = { skinType = 0 }, label = { Text("Healthy", fontSize = 10.sp) })
                    FilterChip(selected = skinType == 1, onClick = { skinType = 1 }, label = { Text("Tissue Paper", fontSize = 10.sp) })
                    FilterChip(selected = skinType == 2, onClick = { skinType = 2 }, label = { Text("Dry / Oedematous", fontSize = 10.sp) })
                }

                Text("Continence", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilterChip(selected = continence == 0, onClick = { continence = 0 }, label = { Text("Continent", fontSize = 10.sp) })
                    FilterChip(selected = continence == 1, onClick = { continence = 1 }, label = { Text("Occasional Incontinent", fontSize = 10.sp) })
                    FilterChip(selected = continence == 2, onClick = { continence = 2 }, label = { Text("Catheterized", fontSize = 10.sp) })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val assessment = WaterlowAssessment(
                        clientId = clientId,
                        assessedDate = "03/10/2026",
                        buildWeight = buildWeight,
                        skinType = skinType,
                        sexAge = 3,
                        continence = continence,
                        mobility = mobility,
                        appetite = appetite,
                        specialRisks = 1,
                        totalScore = totalWaterlow,
                        riskCategory = riskCategory
                    )
                    onSaveAssessment(assessment)
                    onDismiss()
                },
                modifier = Modifier.testTag("save_waterlow_button")
            ) {
                Text("Save Waterlow")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
