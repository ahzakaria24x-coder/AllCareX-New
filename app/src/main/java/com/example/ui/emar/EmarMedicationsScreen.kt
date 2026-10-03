package com.example.ui.emar

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
import com.example.model.AdministrationOutcome
import com.example.model.Medication
import com.example.model.MedicationAdministrationRecord
import com.example.ui.common.NeutralPill
import com.example.ui.common.StatusPill
import com.example.ui.theme.*

@Composable
fun EmarMedicationsScreen(
    repository: CareRepository,
    initialClientId: String? = null
) {
    val clients by repository.clients.collectAsState()
    val medications by repository.medications.collectAsState()
    val staffList by repository.staffList.collectAsState()
    val currentUser by repository.currentUser.collectAsState()

    var showAddMedModal by remember { mutableStateOf(false) }
    var recordingMed by remember { mutableStateOf<Medication?>(null) }
    var showAuditLogMedId by remember { mutableStateOf<String?>(null) }

    // Add med form state
    var addClientId by remember { mutableStateOf("") }
    var addMedName by remember { mutableStateOf("") }
    var addDosage by remember { mutableStateOf("") }
    var addFrequency by remember { mutableStateOf("") }
    var addRoute by remember { mutableStateOf("oral") }
    var addStock by remember { mutableStateOf("20") }
    var clientDropdownOpen by remember { mutableStateOf(false) }
    var routeDropdownOpen by remember { mutableStateOf(false) }

    // Record dose form state
    var selectedStaffId by remember { mutableStateOf(staffList.firstOrNull()?.id ?: "") }
    var staffDropdownOpen by remember { mutableStateOf(false) }
    var recordStatus by remember { mutableStateOf("given") }
    var statusDropdownOpen by remember { mutableStateOf(false) }
    var barcodeVerified by remember { mutableStateOf(true) }
    var recordNotes by remember { mutableStateOf("") }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(BasePageBg)) {
        val isDesktop = maxWidth >= 1024.dp
        val contentPadding = if (isDesktop) 32.dp else 16.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .testTag("emar_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Header Row: Title & Subtitle + Action Button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "eMAR — Medications",
                            fontSize = if (isDesktop) 30.sp else 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = BaseTextHeading
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Electronic medication administration records and stock.",
                            fontSize = 14.sp,
                            color = BaseTextMuted
                        )
                    }

                    Button(
                        onClick = { showAddMedModal = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                        modifier = Modifier.height(44.dp).testTag("add_medication_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add medication", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }

            // Warning Banner 1: Running low on stock
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(StatusMedBg)
                        .border(1.dp, StatusMedBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = StatusMedText, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "2 medication(s) running low on stock — pharmacy refill suggested.",
                        fontSize = 14.sp,
                        color = StatusMedText,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Warning Banner 2: Overdue doses
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(StatusMedBg)
                        .border(1.dp, StatusMedBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = StatusMedText, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "2 medication(s) overdue — possible missed dose(s):\nFurosemide (Doris Penrose) • Metformin (Margaret Holloway)",
                        fontSize = 13.sp,
                        color = StatusMedText,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Medication Cards (2 columns on desktop, 1 col on mobile)
            val chunked = medications.chunked(if (isDesktop) 2 else 1)
            chunked.forEach { rowMeds ->
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowMeds.forEach { med ->
                            val client = clients.find { it.id == med.clientId }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(BaseSurface)
                                    .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
                                    .padding(20.dp)
                            ) {
                                // Top row: Client name & active pill
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Medication, contentDescription = null, tint = StatusInfoText, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(client?.fullName ?: "Doris Penrose", fontSize = 13.sp, color = BaseTextMuted)
                                    }
                                    StatusPill(status = "active", color = BaseEmerald300)
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Med Name and Dose
                                Text(med.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "${med.dose} • ${med.frequencyDescription} • ${med.route}",
                                    fontSize = 13.sp,
                                    color = BaseTextMuted
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Stock info & Record dose action
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Stock: ", fontSize = 13.sp, color = BaseTextMuted)
                                        Text("${med.currentStock}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text("Last: 31/08/2026", fontSize = 12.sp, color = BaseTextFaint)
                                    }

                                    Button(
                                        onClick = { recordingMed = med },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BaseModalInput),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BaseEmerald300, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Record dose", fontSize = 12.sp, color = Color.White)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // View audit log link
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { showAuditLogMedId = med.id }
                                ) {
                                    Icon(Icons.Default.History, contentDescription = null, tint = BaseTextFaint, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("View audit log", fontSize = 12.sp, color = BaseTextFaint)
                                }
                            }
                        }
                        if (rowMeds.size == 1 && isDesktop) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Modal: Record Administration
        recordingMed?.let { med ->
            val client = clients.find { it.id == med.clientId }
            AlertDialog(
                onDismissRequest = { recordingMed = null },
                containerColor = BaseModalBg,
                shape = RoundedCornerShape(16.dp),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Record administration", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                        IconButton(onClick = { recordingMed = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = BaseTextMuted)
                        }
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Med Summary Card
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(BaseModalInput)
                                .padding(12.dp)
                        ) {
                            Text(med.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                            Text("${med.dose} • ${med.route}", fontSize = 13.sp, color = BaseTextMuted)
                            Text("Client: ${client?.fullName ?: "Doris Penrose"}", fontSize = 12.sp, color = BaseEmerald300)
                        }

                        // Administered by dropdown
                        Column {
                            Text("Administered by", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = staffList.find { it.id == selectedStaffId }?.name ?: "Lucia Marques",
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        IconButton(onClick = { staffDropdownOpen = true }) {
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
                                    modifier = Modifier.fillMaxWidth().clickable { staffDropdownOpen = true }
                                )
                                DropdownMenu(
                                    expanded = staffDropdownOpen,
                                    onDismissRequest = { staffDropdownOpen = false },
                                    modifier = Modifier.background(BaseModalBg)
                                ) {
                                    staffList.forEach { s ->
                                        DropdownMenuItem(
                                            text = { Text(s.name, color = BaseTextHeading) },
                                            onClick = {
                                                selectedStaffId = s.id
                                                staffDropdownOpen = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Status dropdown
                        Column {
                            Text("Status", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = recordStatus,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        IconButton(onClick = { statusDropdownOpen = true }) {
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
                                    modifier = Modifier.fillMaxWidth().clickable { statusDropdownOpen = true }
                                )
                                DropdownMenu(
                                    expanded = statusDropdownOpen,
                                    onDismissRequest = { statusDropdownOpen = false },
                                    modifier = Modifier.background(BaseModalBg)
                                ) {
                                    listOf("given", "refused", "omitted", "self-administered").forEach { st ->
                                        DropdownMenuItem(
                                            text = { Text(st, color = BaseTextHeading) },
                                            onClick = {
                                                recordStatus = st
                                                statusDropdownOpen = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Barcode verification checkbox
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = barcodeVerified,
                                onCheckedChange = { barcodeVerified = it },
                                colors = CheckboxDefaults.colors(checkedColor = BaseEmerald500)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Barcode (GS1 DataMatrix) verified", fontSize = 13.sp, color = BaseTextHeading)
                        }

                        // Notes textarea
                        Column {
                            Text("Notes", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = recordNotes,
                                onValueChange = { recordNotes = it },
                                placeholder = { Text("e.g. client refused — nausea", color = BaseTextFaint) },
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
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val staff = staffList.find { it.id == selectedStaffId } ?: staffList.first()
                            repository.recordMedicationAdministration(
                                MedicationAdministrationRecord(
                                    medicationId = med.id,
                                    medicationName = med.name,
                                    clientId = med.clientId,
                                    scheduledDate = "03/10/2026",
                                    scheduledTime = "09:00",
                                    actualTime = "09:02",
                                    outcome = if (recordStatus == "given") AdministrationOutcome.GIVEN else AdministrationOutcome.REFUSED,
                                    administeredByCarerId = staff.id,
                                    administeredByCarerName = staff.name,
                                    notes = recordNotes,
                                    doseGiven = med.dose
                                )
                            )
                            recordingMed = null
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("Log administration", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { recordingMed = null },
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("Cancel", fontSize = 14.sp, color = BaseTextMuted)
                    }
                }
            )
        }

        // Modal: Add Medication
        if (showAddMedModal) {
            AlertDialog(
                onDismissRequest = { showAddMedModal = false },
                containerColor = BaseModalBg,
                shape = RoundedCornerShape(16.dp),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Add Medication", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                        IconButton(onClick = { showAddMedModal = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = BaseTextMuted)
                        }
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Client dropdown
                        Column {
                            Text("Client", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = clients.find { it.id == addClientId }?.fullName ?: "Select client",
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        IconButton(onClick = { clientDropdownOpen = true }) {
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
                                    modifier = Modifier.fillMaxWidth().clickable { clientDropdownOpen = true }
                                )
                                DropdownMenu(
                                    expanded = clientDropdownOpen,
                                    onDismissRequest = { clientDropdownOpen = false },
                                    modifier = Modifier.background(BaseModalBg)
                                ) {
                                    clients.forEach { c ->
                                        DropdownMenuItem(
                                            text = { Text(c.fullName, color = BaseTextHeading) },
                                            onClick = {
                                                addClientId = c.id
                                                clientDropdownOpen = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Medication name
                        Column {
                            Text("Medication name", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = addMedName,
                                onValueChange = { addMedName = it },
                                placeholder = { Text("e.g. Paracetamol", color = BaseTextFaint) },
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

                        // Dosage and Frequency Row
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Dosage", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = addDosage,
                                    onValueChange = { addDosage = it },
                                    placeholder = { Text("e.g. 500mg", color = BaseTextFaint) },
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
                                Text("Frequency", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = addFrequency,
                                    onValueChange = { addFrequency = it },
                                    placeholder = { Text("e.g. twice daily", color = BaseTextFaint) },
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

                        // Route and Stock Row
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Route", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = addRoute,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = {
                                            IconButton(onClick = { routeDropdownOpen = true }) {
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
                                        modifier = Modifier.fillMaxWidth().clickable { routeDropdownOpen = true }
                                    )
                                    DropdownMenu(
                                        expanded = routeDropdownOpen,
                                        onDismissRequest = { routeDropdownOpen = false },
                                        modifier = Modifier.background(BaseModalBg)
                                    ) {
                                        listOf("oral", "topical", "inhaled", "injection", "subcutaneous", "other").forEach { r ->
                                            DropdownMenuItem(
                                                text = { Text(r, color = BaseTextHeading) },
                                                onClick = {
                                                    addRoute = r
                                                    routeDropdownOpen = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Stock", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = addStock,
                                    onValueChange = { addStock = it },
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
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val client = clients.find { it.id == addClientId } ?: clients.first()
                            val newMed = Medication(
                                id = "med-${System.currentTimeMillis()}",
                                organisationId = "org-allcarex-01",
                                branchId = client.branchId,
                                clientId = client.id,
                                name = addMedName.ifEmpty { "New Medication" },
                                dose = addDosage.ifEmpty { "1 tablet" },
                                route = addRoute,
                                frequencyDescription = addFrequency.ifEmpty { "Daily" },
                                scheduledTimes = listOf("08:00"),
                                prescriber = "Dr. Miller (GP)",
                                startDate = "01/10/2026",
                                currentStock = addStock.toIntOrNull() ?: 20
                            )
                            repository.addMedication(newMed)
                            showAddMedModal = false
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("Add", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showAddMedModal = false },
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("Cancel", fontSize = 14.sp, color = BaseTextMuted)
                    }
                }
            )
        }
    }
}
