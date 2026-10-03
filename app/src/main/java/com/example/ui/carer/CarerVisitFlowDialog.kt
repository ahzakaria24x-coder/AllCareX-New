package com.example.ui.carer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.model.*
import com.example.ui.theme.CareMintTeal
import com.example.ui.theme.CareMintTealContainer

@Composable
fun CarerVisitFlowDialog(
    visit: Visit,
    repository: CareRepository,
    isHighContrast: Boolean,
    isLargeText: Boolean,
    onDismiss: () -> Unit,
    onOpenEmar: (String) -> Unit,
    onOpenIncident: (Visit) -> Unit,
    onOpenBodyMap: (String) -> Unit
) {
    val textScale = if (isLargeText) 1.2f else 1.0f

    // Current step in the 6-stage visit flow:
    // 0: Check-in, 1: Key Info, 2: Task Checklist, 3: Medication, 4: Daily Note, 5: Check-out
    var currentStep by remember {
        mutableStateOf(if (visit.status == VisitStatus.CHECKED_IN) 2 else 0)
    }

    // Step 0: Check-in state
    var isManualGpsOverride by remember { mutableStateOf(false) }
    var manualGpsReason by remember { mutableStateOf("Basement flat - GPS satellite lock unavailable") }

    // Step 2: Task Checklist exception reason state
    var selectedTaskForException by remember { mutableStateOf<VisitTask?>(null) }
    var exceptionReasonInput by remember { mutableStateOf("") }

    // Step 4: Daily Note state
    var dailyNotesText by remember(visit.id) { mutableStateOf(visit.dailyNotes ?: "") }
    var isVoiceListening by remember { mutableStateOf(false) }
    var attachedPhotoCount by remember { mutableStateOf(0) }

    // Step 5: Check-out state
    var selectedFluidMl by remember(visit.id) { mutableStateOf(visit.fluidIntakeMl ?: 300) }
    var selectedMood by remember(visit.id) { mutableStateOf(visit.moodObservation ?: "Cheerful") }
    var nextVisitNotesText by remember(visit.id) { mutableStateOf(visit.nextVisitNotes ?: "") }

    val steps = listOf("Check-In", "Key Info", "Tasks", "Meds", "Note", "Check-Out")

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("carer_visit_flow_dialog"),
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = visit.clientName,
                            fontSize = (17 * textScale).sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${visit.visitType} • ${visit.startTime} - ${visit.endTime}",
                            fontSize = (11 * textScale).sp,
                            color = CareMintTeal,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Direct Utilities inside Visit: Body Map & Incident
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { onOpenBodyMap(visit.clientId) },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(CareMintTealContainer)
                                .testTag("flow_body_map_button")
                        ) {
                            Icon(imageVector = Icons.Default.Accessibility, contentDescription = "Body Map", tint = CareMintTeal, modifier = Modifier.size(18.dp))
                        }

                        IconButton(
                            onClick = { onOpenIncident(visit) },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444).copy(alpha = 0.2f))
                                .testTag("flow_report_incident_button")
                        ) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = "Incident", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Step Progression Tabs (min 48dp height overall)
                ScrollableTabRow(
                    selectedTabIndex = currentStep,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = CareMintTeal,
                    edgePadding = 0.dp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                ) {
                    steps.forEachIndexed { index, title ->
                        Tab(
                            selected = currentStep == index,
                            onClick = { currentStep = index },
                            modifier = Modifier.heightIn(min = 40.dp),
                            text = {
                                Text(
                                    text = "${index + 1}. $title",
                                    fontSize = (10 * textScale).sp,
                                    fontWeight = if (currentStep == index) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                when (currentStep) {
                    0 -> {
                        // 1. CHECK-IN (GPS & MANUAL REASON IF GPS FAILS)
                        Text(
                            text = "STEP 1: VISIT CHECK-IN",
                            fontSize = (12 * textScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = CareMintTeal
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CareMintTealContainer.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Client Address:", fontSize = (10 * textScale).sp, fontWeight = FontWeight.Bold)
                                Text("${visit.clientAddress}, ${visit.clientPostcode}", fontSize = (12 * textScale).sp, fontWeight = FontWeight.SemiBold)
                                Text("Key Safe Code: ${visit.keySafeCode ?: "#4920"}", fontSize = (12 * textScale).sp, color = CareMintTeal, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (visit.status == VisitStatus.CHECKED_IN) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Already Checked In at ${visit.checkInTime} (${visit.checkInGps})",
                                        fontSize = (12 * textScale).sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981)
                                    )
                                }
                            }
                        } else {
                            if (!isManualGpsOverride) {
                                Button(
                                    onClick = {
                                        repository.checkInVisit(visit.id)
                                        currentStep = 1 // Advance to Key Info
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 52.dp)
                                        .testTag("confirm_gps_check_in_button")
                                ) {
                                    Icon(imageVector = Icons.Default.MyLocation, contentDescription = null, tint = Color(0xFF0A1128))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Verify GPS Location & Check In (±6m)", color = Color(0xFF0A1128), fontWeight = FontWeight.ExtraBold, fontSize = (12 * textScale).sp)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedButton(
                                    onClick = { isManualGpsOverride = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 48.dp)
                                        .testTag("manual_gps_fail_button")
                                ) {
                                    Icon(imageVector = Icons.Default.SignalCellularConnectedNoInternet0Bar, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("GPS Failed / Poor Signal? Manual Check-In", fontSize = (11 * textScale).sp)
                                }
                            } else {
                                // Manual Override Reason Entry
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFF59E0B).copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("Manual Check-In Override", fontSize = (11 * textScale).sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                                        Text("Record audit reason why GPS verification could not be acquired:", fontSize = (10 * textScale).sp)

                                        Spacer(modifier = Modifier.height(6.dp))
                                        OutlinedTextField(
                                            value = manualGpsReason,
                                            onValueChange = { manualGpsReason = it },
                                            modifier = Modifier.fillMaxWidth().testTag("manual_gps_reason_input")
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = {
                                                repository.checkInVisit(
                                                    visitId = visit.id,
                                                    isManualOverride = true,
                                                    overrideReason = manualGpsReason
                                                )
                                                currentStep = 1
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("confirm_manual_check_in_button")
                                        ) {
                                            Text("Confirm Manual Check-In with Reason", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // 2. KEY INFO (CARE-PLAN SUMMARY, RISKS, ALLERGIES, ACCESS, DNACPR, PREFERENCES)
                        Text(
                            text = "STEP 2: KEY CLINICAL & ACCESS INFO",
                            fontSize = (12 * textScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = CareMintTeal
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // DNACPR / ReSPECT Status
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (visit.dnacprStatus) Color(0xFFEF4444).copy(alpha = 0.15f) else Color(0xFF10B981).copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (visit.dnacprStatus) Color(0xFFEF4444) else Color(0xFF10B981)
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("key_info_dnacpr_card")
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (visit.dnacprStatus) Icons.Default.MedicalServices else Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = if (visit.dnacprStatus) Color(0xFFEF4444) else Color(0xFF10B981)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (visit.dnacprStatus) "DNACPR / ReSPECT IN PLACE" else "FOR FULL ACTIVE RESUSCITATION",
                                        fontSize = (12 * textScale).sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (visit.dnacprStatus) Color(0xFFEF4444) else Color(0xFF10B981)
                                    )
                                    Text(
                                        text = if (visit.dnacprStatus) "Do Not Attempt CPR order signed by GP & Consultant Geriatrician." else "No resuscitation restrictions.",
                                        fontSize = (10 * textScale).sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Key-Safe & Access Details
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("ACCESS & KEY-SAFE CODE", fontSize = (10 * textScale).sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                                Text(
                                    text = "Code: ${visit.keySafeCode ?: "#4920"} (Located on gas meter box left of door)",
                                    fontSize = (13 * textScale).sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Known Allergies
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFEF4444).copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("ALLERGIES & CONTRAINDICATIONS", fontSize = (10 * textScale).sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                                Text(
                                    text = if (visit.allergies.isNotEmpty()) visit.allergies.joinToString(", ") else "No known drug allergies (NKDA)",
                                    fontSize = (12 * textScale).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEF4444)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Clinical Risks & Care Plan Summary
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("CARE-PLAN & CLINICAL RISKS", fontSize = (10 * textScale).sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text("• High Fall Risk: Zimmer frame required for all mobilisations.", fontSize = (11 * textScale).sp)
                                Text("• Skin Integrity: Inspect heels and sacrum at each bed/bath call.", fontSize = (11 * textScale).sp)
                                Text("• Nutrition & Hydration: Minimum 1,200ml daily target fluid intake.", fontSize = (11 * textScale).sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Client Personal Preferences
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CareMintTealContainer.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("CLIENT PREFERENCES & ROUTINES", fontSize = (10 * textScale).sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                                Text(
                                    text = visit.clientPreferences ?: "Prefers tea before washing; classic radio on.",
                                    fontSize = (11 * textScale).sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    2 -> {
                        // 3. TASK CHECKLIST (MANDATORY TASKS MUST BE COMPLETED OR HAVE EXCEPTION REASON)
                        Text(
                            text = "STEP 3: TASK CHECKLIST (${visit.tasks.count { it.isCompleted }}/${visit.tasks.size})",
                            fontSize = (12 * textScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = CareMintTeal
                        )
                        Text(
                            text = "All mandatory tasks must be ticked or have an exception reason logged before check-out.",
                            fontSize = (10 * textScale).sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        visit.tasks.forEach { task ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (task.isCompleted) Color(0xFF10B981) else if (task.exceptionReason != null) Color(0xFFF59E0B) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Checkbox(
                                            checked = task.isCompleted,
                                            onCheckedChange = { repository.toggleVisitTask(visit.id, task.id) },
                                            colors = CheckboxDefaults.colors(checkedColor = CareMintTeal),
                                            modifier = Modifier.testTag("task_check_${task.id}")
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = task.title,
                                                    fontSize = (13 * textScale).sp,
                                                    fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.Bold
                                                )
                                                if (task.isMandatory) {
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Surface(shape = RoundedCornerShape(3.dp), color = Color(0xFFEF4444).copy(alpha = 0.2f)) {
                                                        Text("REQUIRED", fontSize = 8.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                                    }
                                                }
                                            }
                                            Text(text = task.category, fontSize = (10 * textScale).sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    // Exception reason display or add button
                                    if (task.exceptionReason != null) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFF59E0B).copy(alpha = 0.15f),
                                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                        ) {
                                            Text(
                                                text = "Exception: ${task.exceptionReason}",
                                                fontSize = (10 * textScale).sp,
                                                color = Color(0xFFF59E0B),
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(6.dp)
                                            )
                                        }
                                    } else if (!task.isCompleted && task.isMandatory) {
                                        TextButton(
                                            onClick = {
                                                selectedTaskForException = task
                                                exceptionReasonInput = ""
                                            },
                                            modifier = Modifier.heightIn(min = 36.dp).align(Alignment.End)
                                        ) {
                                            Icon(imageVector = Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Can't complete? Log Reason", fontSize = (10 * textScale).sp, color = Color(0xFFF59E0B))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    3 -> {
                        // 4. MEDICATION (LINKS TO EMAR)
                        Text(
                            text = "STEP 4: MEDICATION ADMINISTRATION",
                            fontSize = (12 * textScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = CareMintTeal
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Scheduled Doses for this Window:", fontSize = (11 * textScale).sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))

                                Text("• Ramipril 5mg Tablets - 1 tab oral with water (Cardiovascular)", fontSize = (11 * textScale).sp)
                                Text("• Paracetamol 500mg Tablets - 2 tabs PRN for joint ache", fontSize = (11 * textScale).sp)

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = { onOpenEmar(visit.clientId) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("jump_to_emar_button")
                                ) {
                                    Icon(imageVector = Icons.Default.MedicalServices, contentDescription = null, tint = Color(0xFF0A1128), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Open Full eMAR & Sign Meds", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold, fontSize = (12 * textScale).sp)
                                }
                            }
                        }
                    }

                    4 -> {
                        // 5. DAILY NOTE (VOICE-TO-TEXT, PHOTO, QUICK PHRASES)
                        Text(
                            text = "STEP 5: DAILY CARE NOTE",
                            fontSize = (12 * textScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = CareMintTeal
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Voice-to-Text Button & Quick Phrases
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Voice-to-Text Dictation", fontSize = (11 * textScale).sp, fontWeight = FontWeight.SemiBold)

                            Button(
                                onClick = {
                                    isVoiceListening = !isVoiceListening
                                    if (isVoiceListening) {
                                        dailyNotesText = "Arthur was cheerful this visit. Assisted with washing and dressing in warm wool cardigan. Ate full bowl of hot porridge and drank 300ml Earl Grey tea. Mobilised safely with Zimmer frame. All morning medications administered and signed on eMAR. Arthur settled comfortably watching news."
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isVoiceListening) Color(0xFFEF4444) else CareMintTeal
                                ),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.heightIn(min = 44.dp).testTag("voice_to_text_trigger")
                            ) {
                                Icon(
                                    imageVector = if (isVoiceListening) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = if (isVoiceListening) Color.White else Color(0xFF0A1128),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isVoiceListening) "Listening..." else "Tap to Speak",
                                    color = if (isVoiceListening) Color.White else Color(0xFF0A1128),
                                    fontSize = (11 * textScale).sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Phrases Chips (speed up entry for field carers)
                        Text("Tap Quick Phrases to Insert:", fontSize = (10 * textScale).sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Ate well & hydrated", "Assisted personal care", "Skin intact & clear", "Mobilised with frame").forEach { phrase ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.clickable {
                                        dailyNotesText = if (dailyNotesText.isBlank()) phrase else "$dailyNotesText. $phrase"
                                    }
                                ) {
                                    Text(text = "+ $phrase", fontSize = (9 * textScale).sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = dailyNotesText,
                            onValueChange = { dailyNotesText = it },
                            placeholder = { Text("Clinical daily notes (speak or type)...") },
                            modifier = Modifier.fillMaxWidth().testTag("visit_notes_text_field"),
                            minLines = 4
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Attach Photo Simulation
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Meal / Environment Photo Evidence", fontSize = (11 * textScale).sp, fontWeight = FontWeight.SemiBold)

                            OutlinedButton(
                                onClick = { attachedPhotoCount++ },
                                modifier = Modifier.heightIn(min = 40.dp).testTag("attach_photo_button")
                            ) {
                                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (attachedPhotoCount > 0) "$attachedPhotoCount Photo Attached" else "Attach Photo", fontSize = (10 * textScale).sp)
                            }
                        }
                    }

                    5 -> {
                        // 6. CHECK-OUT (CONFIRM TASKS, RECORD MOOD/WELLBEING, NOTES FOR NEXT VISIT)
                        Text(
                            text = "STEP 6: CHECK-OUT & NEXT VISIT HANDOVER",
                            fontSize = (12 * textScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = CareMintTeal
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Task Completion Summary
                        val uncompletedMandatory = visit.tasks.filter { it.isMandatory && !it.isCompleted && it.exceptionReason == null }
                        if (uncompletedMandatory.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFEF4444).copy(alpha = 0.15f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "⚠️ Notice: ${uncompletedMandatory.size} required tasks lack completion or an exception reason.",
                                    fontSize = (11 * textScale).sp,
                                    color = Color(0xFFEF4444),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Fluid Intake
                        Text("Hydration Recorded:", fontSize = (11 * textScale).sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(150, 250, 350, 500).forEach { ml ->
                                FilterChip(
                                    selected = selectedFluidMl == ml,
                                    onClick = { selectedFluidMl = ml },
                                    label = { Text("${ml}ml", fontSize = (11 * textScale).sp) },
                                    modifier = Modifier.heightIn(min = 48.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Mood & Well-being
                        Text("Client Mood / Well-being Observation:", fontSize = (11 * textScale).sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Cheerful", "Calm", "Confused", "Anxious").forEach { mood ->
                                FilterChip(
                                    selected = selectedMood == mood,
                                    onClick = { selectedMood = mood },
                                    label = { Text(mood, fontSize = (10 * textScale).sp) },
                                    modifier = Modifier.heightIn(min = 48.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Notes for the Next Visit
                        Text("Notes for the Next Visit / Oncoming Carer:", fontSize = (11 * textScale).sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = nextVisitNotesText,
                            onValueChange = { nextVisitNotesText = it },
                            placeholder = { Text("e.g. Ensure fresh semi-skimmed milk is bought on lunch call...") },
                            modifier = Modifier.fillMaxWidth().testTag("next_visit_notes_field"),
                            minLines = 2
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Big Final Complete & Check Out Button (min 52dp)
                        Button(
                            onClick = {
                                repository.checkOutVisitExtended(
                                    visitId = visit.id,
                                    dailyNotes = dailyNotesText.ifBlank { "Visit completed successfully. Client comfortable and settled." },
                                    fluidMl = selectedFluidMl,
                                    mood = selectedMood,
                                    nextVisitNotes = nextVisitNotesText,
                                    photoUris = if (attachedPhotoCount > 0) listOf("photo-meal-01.jpg") else emptyList()
                                )
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 52.dp)
                                .testTag("final_check_out_button")
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color(0xFF0A1128))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "COMPLETE & CHECK OUT (VERIFY GPS)",
                                color = Color(0xFF0A1128),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = (12 * textScale).sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (currentStep > 0) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        modifier = Modifier.heightIn(min = 48.dp)
                    ) {
                        Text("Previous")
                    }
                }
                if (currentStep < steps.size - 1) {
                    Button(
                        onClick = { currentStep++ },
                        colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                        modifier = Modifier.heightIn(min = 48.dp)
                    ) {
                        Text("Next: ${steps[currentStep + 1]}", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                    }
                }
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

    // Exception Reason Logging Dialog
    if (selectedTaskForException != null) {
        val targetTask = selectedTaskForException!!
        AlertDialog(
            onDismissRequest = { selectedTaskForException = null },
            title = { Text("Log Exception Reason", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Task: ${targetTask.title}", fontWeight = FontWeight.Bold, color = CareMintTeal)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Select or enter reason why this task could not be completed:")
                    Spacer(modifier = Modifier.height(6.dp))
                    listOf(
                        "Client declined task today",
                        "Family member assisted prior to visit",
                        "Medication supply unavailable / delayed",
                        "Client was sleeping / chose not to be disturbed"
                    ).forEach { r ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 40.dp)
                                .clickable { exceptionReasonInput = r }
                        ) {
                            RadioButton(
                                selected = exceptionReasonInput == r,
                                onClick = { exceptionReasonInput = r },
                                colors = RadioButtonDefaults.colors(selectedColor = CareMintTeal)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(r, fontSize = (11 * textScale).sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = exceptionReasonInput,
                        onValueChange = { exceptionReasonInput = it },
                        label = { Text("Custom reason") },
                        modifier = Modifier.fillMaxWidth().testTag("custom_exception_reason_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (exceptionReasonInput.isNotBlank()) {
                            repository.setTaskExceptionReason(visit.id, targetTask.id, exceptionReasonInput)
                            selectedTaskForException = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text("Save Exception", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { selectedTaskForException = null },
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
