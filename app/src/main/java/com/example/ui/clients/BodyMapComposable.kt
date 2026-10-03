package com.example.ui.clients

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BodyMapMark
import com.example.ui.theme.*

@Composable
fun BodyMapViewer(
    clientId: String,
    marks: List<BodyMapMark>,
    onAddMark: (BodyMapMark) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFrontSelected by remember { mutableStateOf(true) }
    var selectedMark by remember { mutableStateOf<BodyMapMark?>(null) }
    var pendingTapPosition by remember { mutableStateOf<Offset?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val currentMarks = marks.filter { it.isFront == isFrontSelected }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Clinical Body Map & Skin Integrity",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Tap on body silhouette to log bruises, sores, wounds, or tears",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // View Selector (Front / Back)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(3.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isFrontSelected) CareMintTeal else Color.Transparent,
                    modifier = Modifier.clickable { isFrontSelected = true }
                ) {
                    Text(
                        text = "Anterior (Front)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isFrontSelected) Color(0xFF0A1128) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (!isFrontSelected) CareMintTeal else Color.Transparent,
                    modifier = Modifier.clickable { isFrontSelected = false }
                ) {
                    Text(
                        text = "Posterior (Back)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!isFrontSelected) Color(0xFF0A1128) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Body Map Canvas with Markers
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(isFrontSelected) {
                        detectTapGestures { offset ->
                            val normX = offset.x / size.width
                            val normY = offset.y / size.height

                            // Check if tapped near an existing mark
                            val hit = currentMarks.find { mark ->
                                val markX = mark.xNormalized * size.width
                                val markY = mark.yNormalized * size.height
                                val dist = Math.hypot((markX - offset.x).toDouble(), (markY - offset.y).toDouble())
                                dist < 40
                            }

                            if (hit != null) {
                                selectedMark = hit
                            } else {
                                pendingTapPosition = Offset(normX, normY)
                                showAddDialog = true
                            }
                        }
                    }
                    .testTag("body_map_canvas")
            ) {
                drawHumanSilhouette(isFrontSelected)

                // Render markers
                currentMarks.forEach { mark ->
                    val posX = mark.xNormalized * size.width
                    val posY = mark.yNormalized * size.height

                    val pinColor = when (mark.markType.lowercase()) {
                        "pressure sore" -> Color(0xFFEF4444)
                        "bruise" -> Color(0xFF8B5CF6)
                        "wound", "skin tear" -> Color(0xFFF59E0B)
                        else -> Color(0xFFEC4899)
                    }

                    // Outer pulse
                    drawCircle(
                        color = pinColor.copy(alpha = 0.35f),
                        radius = 16f,
                        center = Offset(posX, posY)
                    )
                    // Core pin
                    drawCircle(
                        color = pinColor,
                        radius = 8f,
                        center = Offset(posX, posY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3f,
                        center = Offset(posX, posY)
                    )
                }
            }

            // Legend Overlay
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = Color(0xFFEF4444), label = "Pressure Sore")
                LegendItem(color = Color(0xFF8B5CF6), label = "Bruise")
                LegendItem(color = Color(0xFFF59E0B), label = "Skin Tear / Wound")
            }
        }

        // Details of Selected Marker
        selectedMark?.let { mark ->
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${mark.markType} (${mark.severity})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Logged on ${mark.dateLogged}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = mark.description,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Text(
                            text = "Recorded by: ${mark.recordedBy}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    IconButton(onClick = { selectedMark = null }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    // Dialog: Add New Mark on Body Map
    if (showAddDialog && pendingTapPosition != null) {
        var markType by remember { mutableStateOf("Bruise") }
        var severity by remember { mutableStateOf("Moderate") }
        var description by remember { mutableStateOf("") }

        val typeOptions = listOf("Bruise", "Pressure Sore", "Skin Tear", "Wound", "Rash")
        val severityOptions = listOf("Mild", "Moderate", "Severe")

        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                pendingTapPosition = null
            },
            title = {
                Text(
                    text = "Log Skin Observation (${if (isFrontSelected) "Front" else "Back"})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Mark Type",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        typeOptions.take(3).forEach { t ->
                            FilterChip(
                                selected = markType == t,
                                onClick = { markType = t },
                                label = { Text(t, fontSize = 10.sp) },
                                modifier = Modifier.height(30.dp)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        typeOptions.drop(3).forEach { t ->
                            FilterChip(
                                selected = markType == t,
                                onClick = { markType = t },
                                label = { Text(t, fontSize = 10.sp) },
                                modifier = Modifier.height(30.dp)
                            )
                        }
                    }

                    Text(
                        text = "Severity",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        severityOptions.forEach { s ->
                            FilterChip(
                                selected = severity == s,
                                onClick = { severity = s },
                                label = { Text(s, fontSize = 10.sp) },
                                modifier = Modifier.height(30.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Clinical Description / Size / Dressing") },
                        placeholder = { Text("e.g. 2x1cm superficial break, mepilex border applied") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("body_mark_desc_input"),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pos = pendingTapPosition!!
                        val newMark = BodyMapMark(
                            clientId = clientId,
                            isFront = isFrontSelected,
                            xNormalized = pos.x,
                            yNormalized = pos.y,
                            markType = markType,
                            severity = severity,
                            description = description.ifBlank { "$severity $markType noted on assessment" },
                            dateLogged = "03/10/2026",
                            recordedBy = "Assigned Carer / Nurse"
                        )
                        onAddMark(newMark)
                        showAddDialog = false
                        pendingTapPosition = null
                    },
                    modifier = Modifier.testTag("save_body_mark_button")
                ) {
                    Text("Save to Body Map")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    pendingTapPosition = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

private fun DrawScope.drawHumanSilhouette(isFront: Boolean) {
    val bodyColor = Color(0xFF64748B).copy(alpha = 0.4f)
    val strokeColor = Color(0xFF94A3B8)
    val cx = size.width / 2f
    val h = size.height

    // Head
    drawCircle(
        color = bodyColor,
        radius = h * 0.08f,
        center = Offset(cx, h * 0.12f)
    )
    drawCircle(
        color = strokeColor,
        radius = h * 0.08f,
        center = Offset(cx, h * 0.12f),
        style = Stroke(width = 2f)
    )

    // Neck
    drawRect(
        color = bodyColor,
        topLeft = Offset(cx - h * 0.025f, h * 0.19f),
        size = Size(h * 0.05f, h * 0.04f)
    )

    // Torso
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(cx - h * 0.13f, h * 0.22f),
        size = Size(h * 0.26f, h * 0.32f),
        cornerRadius = CornerRadius(16f, 16f)
    )
    drawRoundRect(
        color = strokeColor,
        topLeft = Offset(cx - h * 0.13f, h * 0.22f),
        size = Size(h * 0.26f, h * 0.32f),
        cornerRadius = CornerRadius(16f, 16f),
        style = Stroke(width = 2f)
    )

    // Arms
    // Left Arm
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(cx - h * 0.22f, h * 0.23f),
        size = Size(h * 0.07f, h * 0.35f),
        cornerRadius = CornerRadius(12f, 12f)
    )
    // Right Arm
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(cx + h * 0.15f, h * 0.23f),
        size = Size(h * 0.07f, h * 0.35f),
        cornerRadius = CornerRadius(12f, 12f)
    )

    // Legs
    // Left Leg
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(cx - h * 0.11f, h * 0.53f),
        size = Size(h * 0.09f, h * 0.42f),
        cornerRadius = CornerRadius(12f, 12f)
    )
    drawRoundRect(
        color = strokeColor,
        topLeft = Offset(cx - h * 0.11f, h * 0.53f),
        size = Size(h * 0.09f, h * 0.42f),
        cornerRadius = CornerRadius(12f, 12f),
        style = Stroke(width = 2f)
    )

    // Right Leg
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(cx + h * 0.02f, h * 0.53f),
        size = Size(h * 0.09f, h * 0.42f),
        cornerRadius = CornerRadius(12f, 12f)
    )
    drawRoundRect(
        color = strokeColor,
        topLeft = Offset(cx + h * 0.02f, h * 0.53f),
        size = Size(h * 0.09f, h * 0.42f),
        cornerRadius = CornerRadius(12f, 12f),
        style = Stroke(width = 2f)
    )
}
