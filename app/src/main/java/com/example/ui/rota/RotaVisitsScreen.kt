package com.example.ui.rota

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.model.CareType
import com.example.model.Visit
import com.example.model.VisitStatus
import com.example.ui.common.StatusPill
import com.example.ui.theme.*

@Composable
fun RotaVisitsScreen(
    repository: CareRepository
) {
    val visits by repository.visits.collectAsState()
    val staffList by repository.staffList.collectAsState()
    val clients by repository.clients.collectAsState()

    var showScheduleModal by remember { mutableStateOf(false) }
    var selectedVisitIds by remember { mutableStateOf(setOf<String>()) }
    var bulkStaffDropdownExpanded by remember { mutableStateOf(false) }
    var selectedBulkStaffId by remember { mutableStateOf<String?>(null) }
    var selectUpcomingAll by remember { mutableStateOf(false) }

    // Schedule modal form state
    var selectedClientId by remember { mutableStateOf("") }
    var selectedStaffId by remember { mutableStateOf("") }
    var modalDate by remember { mutableStateOf("02/10/2026") }
    var modalType by remember { mutableStateOf("routine") }
    var modalStart by remember { mutableStateOf("09:00") }
    var modalEnd by remember { mutableStateOf("10:00") }
    var modalTasks by remember { mutableStateOf("") }
    var clientDropdownExpanded by remember { mutableStateOf(false) }
    var staffDropdownExpanded by remember { mutableStateOf(false) }
    var typeDropdownExpanded by remember { mutableStateOf(false) }

    // Group visits by human readable date
    val dateGroupings = listOf(
        "Friday 2 October" to visits.filter { it.date == "02/10/2026" || it.id == "visit-02" },
        "Thursday 1 October" to visits.filter { it.date == "01/10/2026" || it.id == "visit-01" },
        "Friday 4 September" to visits.filter { it.date == "04/09/2026" || it.id == "visit-03" },
        "Tuesday 1 September" to visits.filter { it.date == "01/09/2026" || it.id == "visit-04" },
        "Monday 31 August" to visits.filter { it.date == "31/08/2026" || it.id.startsWith("visit-demo") || it.id.contains("aug") }
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(BasePageBg)) {
        val isDesktop = maxWidth >= 1024.dp
        val contentPadding = if (isDesktop) 32.dp else 16.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .testTag("rota_visits_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Header Row: Title & Subtitle + Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Rota & Visits",
                            fontSize = if (isDesktop) 30.sp else 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = BaseTextHeading
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Schedule, track and optimise care visits.",
                            fontSize = 14.sp,
                            color = BaseTextMuted
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        // Sync to calendar button
                        OutlinedButton(
                            onClick = { /* Sync to calendar */ },
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            modifier = Modifier.height(44.dp).testTag("sync_calendar_button")
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = BaseTextMuted, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sync to calendar", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }

                        // + Schedule visit button
                        Button(
                            onClick = { showScheduleModal = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                            modifier = Modifier.height(44.dp).testTag("schedule_visit_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Schedule visit", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                    }
                }
            }

            // Bulk actions row
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(BaseSurface)
                        .border(1.dp, BaseBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                selectUpcomingAll = !selectUpcomingAll
                                selectedVisitIds = if (selectUpcomingAll) setOf("visit-02", "visit-01") else emptySet()
                            }
                        ) {
                            Checkbox(
                                checked = selectUpcomingAll,
                                onCheckedChange = {
                                    selectUpcomingAll = it
                                    selectedVisitIds = if (it) setOf("visit-02", "visit-01") else emptySet()
                                },
                                colors = CheckboxDefaults.colors(checkedColor = BaseEmerald500)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Select upcoming (1)", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                        }
                    }

                    if (selectedVisitIds.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("${selectedVisitIds.size} selected", fontSize = 13.sp, color = BaseTextMuted)

                            // Dropdown for staff assignment
                            Box {
                                OutlinedButton(
                                    onClick = { bulkStaffDropdownExpanded = true },
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                                    colors = ButtonDefaults.outlinedButtonColors(containerColor = BaseModalInput)
                                ) {
                                    Text(
                                        text = staffList.find { it.id == selectedBulkStaffId }?.name ?: "Assign staff...",
                                        fontSize = 13.sp,
                                        color = BaseTextHeading
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = BaseTextMuted)
                                }

                                DropdownMenu(
                                    expanded = bulkStaffDropdownExpanded,
                                    onDismissRequest = { bulkStaffDropdownExpanded = false },
                                    modifier = Modifier.background(BaseModalBg)
                                ) {
                                    staffList.forEach { s ->
                                        DropdownMenuItem(
                                            text = { Text(s.name, color = BaseTextHeading) },
                                            onClick = {
                                                selectedBulkStaffId = s.id
                                                bulkStaffDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    selectedBulkStaffId?.let { sId ->
                                        selectedVisitIds.forEach { vId ->
                                            repository.assignVisitToCarer(vId, sId)
                                        }
                                        selectedVisitIds = emptySet()
                                        selectUpcomingAll = false
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                                enabled = selectedBulkStaffId != null
                            ) {
                                Text("Assign to selected", fontSize = 13.sp, color = Color.White)
                            }

                            OutlinedButton(
                                onClick = {
                                    selectedVisitIds.forEach { vId ->
                                        repository.assignVisitToCarer(vId, "")
                                    }
                                    selectedVisitIds = emptySet()
                                    selectUpcomingAll = false
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Icon(Icons.Default.PersonRemove, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Remove staff", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // Date grouped visit cards
            dateGroupings.forEach { (dateHeader, dateVisits) ->
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(BaseSurface)
                            .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
                            .padding(20.dp)
                    ) {
                        // Date header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = BaseEmerald300, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(dateHeader, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${dateVisits.size} visits", fontSize = 13.sp, color = BaseTextMuted)
                        }

                        // Visit rows
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            dateVisits.forEach { visit ->
                                val isSelected = selectedVisitIds.contains(visit.id)
                                val assignedStaff = staffList.find { it.id == visit.carerId }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(BaseTileChip)
                                        .border(1.dp, if (isSelected) BaseEmerald500 else Color(0xFF1E2638), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Checkbox(
                                            checked = isSelected,
                                            onCheckedChange = { chk ->
                                                selectedVisitIds = if (chk) selectedVisitIds + visit.id else selectedVisitIds - visit.id
                                            },
                                            colors = CheckboxDefaults.colors(checkedColor = BaseEmerald500)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))

                                        // Time
                                        Text(
                                            visit.scheduledStartTime,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BaseTextHeading
                                        )
                                        Spacer(modifier = Modifier.width(14.dp))

                                        // Client and Staff Info
                                        Column {
                                            Text(visit.clientName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                                            Text(
                                                text = if (assignedStaff != null) "${assignedStaff.name} • ${visit.careType.displayName.lowercase()}" else "Unassigned • ${visit.careType.displayName.lowercase()}",
                                                fontSize = 12.sp,
                                                color = BaseTextMuted
                                            )
                                        }
                                    }

                                    // Status pill & Action buttons
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        StatusPill(status = visit.status.displayName, color = Color(visit.status.colorHex))

                                        if (visit.status == VisitStatus.SCHEDULED) {
                                            TextButton(
                                                onClick = { repository.assignVisitToCarer(visit.id, "") }
                                            ) {
                                                Text("Remove", fontSize = 12.sp, color = BaseTextMuted)
                                            }

                                            Button(
                                                onClick = { repository.checkInVisit(visit.id) },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                                                modifier = Modifier.height(32.dp)
                                            ) {
                                                Text("Start", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                                            }
                                        }

                                        if (visit.status == VisitStatus.CHECKED_IN) {
                                            Button(
                                                onClick = { repository.checkOutVisit(visit.id) },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                                                modifier = Modifier.height(32.dp)
                                            ) {
                                                Text("Complete", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Modal: Schedule a Visit
        if (showScheduleModal) {
            AlertDialog(
                onDismissRequest = { showScheduleModal = false },
                containerColor = BaseModalBg,
                shape = RoundedCornerShape(16.dp),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Schedule a Visit", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                        IconButton(onClick = { showScheduleModal = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = BaseTextMuted)
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Client dropdown
                        Column {
                            Text("Client", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = clients.find { it.id == selectedClientId }?.fullName ?: "Select client",
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        IconButton(onClick = { clientDropdownExpanded = true }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = BaseTextMuted)
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = BaseModalInput,
                                        unfocusedContainerColor = BaseModalInput,
                                        focusedBorderColor = BaseEmerald500,
                                        unfocusedBorderColor = Color(0xFF334155),
                                        focusedTextColor = BaseTextHeading,
                                        unfocusedTextColor = BaseTextHeading
                                    ),
                                    modifier = Modifier.fillMaxWidth().clickable { clientDropdownExpanded = true }
                                )
                                DropdownMenu(
                                    expanded = clientDropdownExpanded,
                                    onDismissRequest = { clientDropdownExpanded = false },
                                    modifier = Modifier.background(BaseModalBg)
                                ) {
                                    clients.forEach { c ->
                                        DropdownMenuItem(
                                            text = { Text(c.fullName, color = BaseTextHeading) },
                                            onClick = {
                                                selectedClientId = c.id
                                                clientDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Assigned staff dropdown
                        Column {
                            Text("Assigned staff", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = staffList.find { it.id == selectedStaffId }?.name ?: "Unassigned",
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        IconButton(onClick = { staffDropdownExpanded = true }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = BaseTextMuted)
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = BaseModalInput,
                                        unfocusedContainerColor = BaseModalInput,
                                        focusedBorderColor = BaseEmerald500,
                                        unfocusedBorderColor = Color(0xFF334155),
                                        focusedTextColor = BaseTextHeading,
                                        unfocusedTextColor = BaseTextHeading
                                    ),
                                    modifier = Modifier.fillMaxWidth().clickable { staffDropdownExpanded = true }
                                )
                                DropdownMenu(
                                    expanded = staffDropdownExpanded,
                                    onDismissRequest = { staffDropdownExpanded = false },
                                    modifier = Modifier.background(BaseModalBg)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Unassigned", color = BaseTextHeading) },
                                        onClick = {
                                            selectedStaffId = ""
                                            staffDropdownExpanded = false
                                        }
                                    )
                                    staffList.forEach { s ->
                                        DropdownMenuItem(
                                            text = { Text(s.name, color = BaseTextHeading) },
                                            onClick = {
                                                selectedStaffId = s.id
                                                staffDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Date and Type Row
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Date", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = modalDate,
                                    onValueChange = { modalDate = it },
                                    trailingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = BaseTextMuted, modifier = Modifier.size(16.dp)) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = BaseModalInput,
                                        unfocusedContainerColor = BaseModalInput,
                                        focusedBorderColor = BaseEmerald500,
                                        unfocusedBorderColor = Color(0xFF334155),
                                        focusedTextColor = BaseTextHeading,
                                        unfocusedTextColor = BaseTextHeading
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Type", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = modalType,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = {
                                            IconButton(onClick = { typeDropdownExpanded = true }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = BaseTextMuted)
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = BaseModalInput,
                                            unfocusedContainerColor = BaseModalInput,
                                            focusedBorderColor = BaseEmerald500,
                                            unfocusedBorderColor = Color(0xFF334155),
                                            focusedTextColor = BaseTextHeading,
                                            unfocusedTextColor = BaseTextHeading
                                        ),
                                        modifier = Modifier.fillMaxWidth().clickable { typeDropdownExpanded = true }
                                    )
                                    DropdownMenu(
                                        expanded = typeDropdownExpanded,
                                        onDismissRequest = { typeDropdownExpanded = false },
                                        modifier = Modifier.background(BaseModalBg)
                                    ) {
                                        listOf("routine", "emergency", "review", "medication").forEach { t ->
                                            DropdownMenuItem(
                                                text = { Text(t, color = BaseTextHeading) },
                                                onClick = {
                                                    modalType = t
                                                    typeDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Start and End Time Row
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Start", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = modalStart,
                                    onValueChange = { modalStart = it },
                                    trailingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null, tint = BaseTextMuted, modifier = Modifier.size(16.dp)) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = BaseModalInput,
                                        unfocusedContainerColor = BaseModalInput,
                                        focusedBorderColor = BaseEmerald500,
                                        unfocusedBorderColor = Color(0xFF334155),
                                        focusedTextColor = BaseTextHeading,
                                        unfocusedTextColor = BaseTextHeading
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("End", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = modalEnd,
                                    onValueChange = { modalEnd = it },
                                    trailingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null, tint = BaseTextMuted, modifier = Modifier.size(16.dp)) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = BaseModalInput,
                                        unfocusedContainerColor = BaseModalInput,
                                        focusedBorderColor = BaseEmerald500,
                                        unfocusedBorderColor = Color(0xFF334155),
                                        focusedTextColor = BaseTextHeading,
                                        unfocusedTextColor = BaseTextHeading
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Tasks Input
                        Column {
                            Text("Tasks", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = modalTasks,
                                onValueChange = { modalTasks = it },
                                placeholder = { Text("Personal care, medication prompt, meal prep...", color = BaseTextFaint) },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = BaseModalInput,
                                    unfocusedContainerColor = BaseModalInput,
                                    focusedBorderColor = BaseEmerald500,
                                    unfocusedBorderColor = Color(0xFF334155),
                                    focusedTextColor = BaseTextHeading,
                                    unfocusedTextColor = BaseTextHeading
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val client = clients.find { it.id == selectedClientId } ?: clients.first()
                            val staff = staffList.find { it.id == selectedStaffId }
                            val newV = Visit(
                                id = "visit-${System.currentTimeMillis()}",
                                organisationId = "org-allcarex-01",
                                branchId = client.branchId,
                                clientId = client.id,
                                clientName = client.fullName,
                                clientAddress = client.address,
                                clientPostcode = "YO24 1AA",
                                carerId = staff?.id,
                                carerName = staff?.name,
                                date = modalDate,
                                startTime = modalStart,
                                endTime = modalEnd,
                                visitType = modalType,
                                status = VisitStatus.SCHEDULED,
                                tasks = emptyList(),
                                keySafeCode = "4821"
                            )
                            repository.addVisit(newV)
                            showScheduleModal = false
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("Schedule", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showScheduleModal = false },
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("Cancel", fontSize = 14.sp, color = BaseTextMuted)
                    }
                }
            )
        }
    }
}
