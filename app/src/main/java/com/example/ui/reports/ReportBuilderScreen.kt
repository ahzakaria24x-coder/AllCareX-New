package com.example.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.ui.analytics.SavedReportTemplate
import com.example.ui.common.NeutralPill
import com.example.ui.common.StatusPill
import com.example.ui.theme.*

@Composable
fun ReportBuilderScreen(
    repository: CareRepository
) {
    val clients by repository.clients.collectAsState()
    val visits by repository.visits.collectAsState()
    val staffList by repository.staffList.collectAsState()

    var selectedDataset by remember { mutableStateOf("Visits & Rota") }
    var selectedBranch by remember { mutableStateOf("All Branches") }
    var selectedGrouping by remember { mutableStateOf("Client") }
    var exportToastMessage by remember { mutableStateOf<String?>(null) }
    var showScheduleModal by remember { mutableStateOf(false) }

    // Column selection state
    val availableColumns = when (selectedDataset) {
        "Visits & Rota" -> listOf("Client Name", "Carer Name", "Date", "Start Time", "End Time", "Visit Type", "Status", "Key-Safe", "Postcode")
        "Medication & eMAR" -> listOf("Client Name", "Medication", "Dose", "Route", "Schedule", "Stock", "Outcome", "Administered By")
        "Incidents & Safeguarding" -> listOf("Client Name", "Category", "Severity", "Incident Date", "Reported By", "CQC Notifiable", "Status")
        "Workforce & Compliance" -> listOf("Staff Name", "Role", "Email", "Phone", "Compliance %", "DBS Status", "Training Expiry")
        "Finance & Payroll" -> listOf("Staff Name", "Date", "Hours Worked", "Hourly Rate", "Mileage (km)", "Total Pay (£)", "Status")
        else -> listOf("Audit Name", "Domain", "Score %", "Lead", "Status", "Review Date")
    }

    var selectedColumns by remember(selectedDataset) {
        mutableStateOf(availableColumns.take(5).toSet())
    }

    var savedTemplates by remember {
        mutableStateOf(
            listOf(
                SavedReportTemplate(
                    title = "Monthly CQC Provider Compliance Report",
                    dataset = "Quality & CQC",
                    scheduleFrequency = "Monthly 1st (08:00)",
                    lastGenerated = "01/10/2026",
                    recipientRoles = listOf("Registered Manager", "Director"),
                    format = "PDF + Excel"
                ),
                SavedReportTemplate(
                    title = "Weekly Carer Payroll & Mileage Breakdown",
                    dataset = "Finance & Payroll",
                    scheduleFrequency = "Weekly Mon (07:30)",
                    lastGenerated = "29/09/2026",
                    recipientRoles = listOf("Finance", "Coordinator"),
                    format = "Excel (.xlsx)"
                ),
                SavedReportTemplate(
                    title = "Critical Clinical Escalations & Falls Audit",
                    dataset = "Incidents & Safeguarding",
                    scheduleFrequency = "Daily (07:00)",
                    lastGenerated = "03/10/2026",
                    recipientRoles = listOf("Registered Manager", "Nurse"),
                    format = "PDF + CSV"
                )
            )
        )
    }

    // Modal state for saving schedule
    var newReportTitle by remember { mutableStateOf("") }
    var newFrequency by remember { mutableStateOf("Weekly Mon (08:00)") }
    var frequencyDropdownOpen by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(BasePageBg)) {
        val isDesktop = maxWidth >= 1024.dp
        val contentPadding = if (isDesktop) 32.dp else 16.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .testTag("report_builder_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Header Row: Title & Subtitle + Export Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Custom Report Builder",
                            fontSize = if (isDesktop) 30.sp else 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = BaseTextHeading
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Configure custom datasets, columns, grouping, scheduled dispatch and export.",
                            fontSize = 14.sp,
                            color = BaseTextMuted
                        )
                    }

                    // Export Buttons Row
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = { showScheduleModal = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BaseModalInput),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = BaseEmerald300, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Schedule", fontSize = 13.sp, color = Color.White)
                        }

                        Button(
                            onClick = { exportToastMessage = "Exporting '$selectedDataset' to Formatted PDF report..." },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("PDF", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        OutlinedButton(
                            onClick = { exportToastMessage = "Exporting '$selectedDataset' to Excel Workbook (.xlsx)..." },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Icon(Icons.Default.TableChart, contentDescription = null, tint = BaseEmerald300, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Excel", fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = { exportToastMessage = "Exporting '$selectedDataset' to CSV..." },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, tint = StatusInfoText, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("CSV", fontSize = 13.sp)
                        }
                    }
                }
            }

            // Export Toast Feedback Notice
            exportToastMessage?.let { msg ->
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(StatusLowBg)
                            .border(1.dp, StatusLowBorder, RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BaseEmerald300, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(msg, fontSize = 13.sp, color = BaseEmerald300, fontWeight = FontWeight.Medium)
                        }
                        IconButton(onClick = { exportToastMessage = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = BaseTextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Configuration Card: Dataset, Grouping, Branch
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BaseSurface)
                        .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("1. Select Dataset & Grouping", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)

                    // Dataset Selector Chips
                    val datasets = listOf("Visits & Rota", "Medication & eMAR", "Incidents & Safeguarding", "Workforce & Compliance", "Finance & Payroll", "Quality & CQC")
                    val dScroll = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(dScroll),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        datasets.forEach { ds ->
                            val isSel = selectedDataset == ds
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) BaseEmerald500 else BaseTileChip)
                                    .border(1.dp, if (isSel) BaseEmerald300 else Color(0xFF1E283F), RoundedCornerShape(10.dp))
                                    .clickable { selectedDataset = ds }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(ds, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = if (isSel) Color.White else BaseTextMuted)
                            }
                        }
                    }

                    Divider(color = Color(0xFF171C2E))

                    // Grouping & Branch Rows
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Grouping Level", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("Client", "Carer", "Branch", "None").forEach { g ->
                                    val isG = selectedGrouping == g
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isG) Color(0xFF16233B) else BaseTileChip)
                                            .border(1.dp, if (isG) StatusInfoText else Color(0xFF1E283F), RoundedCornerShape(8.dp))
                                            .clickable { selectedGrouping = g }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(g, fontSize = 12.sp, color = if (isG) StatusInfoText else BaseTextMuted)
                                    }
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Branch Filter", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("All Branches", "Camden", "York", "Bradford").forEach { b ->
                                    val isB = selectedBranch == b
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isB) Color(0xFF16233B) else BaseTileChip)
                                            .border(1.dp, if (isB) BaseEmerald300 else Color(0xFF1E283F), RoundedCornerShape(8.dp))
                                            .clickable { selectedBranch = b }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(b, fontSize = 12.sp, color = if (isB) BaseEmerald300 else BaseTextMuted)
                                    }
                                }
                            }
                        }
                    }

                    Divider(color = Color(0xFF171C2E))

                    // Column Picker Multiselect Chips
                    Column {
                        Text("2. Choose Columns to Include (${selectedColumns.size} selected)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                        Spacer(modifier = Modifier.height(8.dp))
                        val colScroll = rememberScrollState()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(colScroll),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            availableColumns.forEach { col ->
                                val isChecked = selectedColumns.contains(col)
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isChecked) Color(0xFF162C32) else BaseTileChip)
                                        .border(1.dp, if (isChecked) BaseEmerald500 else Color(0xFF1E283F), RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedColumns = if (isChecked) selectedColumns - col else selectedColumns + col
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isChecked) Icons.Default.Check else Icons.Default.Add,
                                        contentDescription = null,
                                        tint = if (isChecked) BaseEmerald300 else BaseTextFaint,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(col, fontSize = 12.sp, color = if (isChecked) BaseEmerald300 else BaseTextMuted)
                                }
                            }
                        }
                    }
                }
            }

            // Live Preview Table Card
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BaseSurface)
                        .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TableView, contentDescription = null, tint = BaseEmerald300, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Live Report Preview", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                        }
                        Text("Dataset: $selectedDataset • Grouped by $selectedGrouping", fontSize = 12.sp, color = BaseTextMuted)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val hScroll = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(hScroll)
                    ) {
                        // Table Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BaseTileChip)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            selectedColumns.forEach { col ->
                                Text(
                                    text = col.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BaseTextMuted,
                                    modifier = Modifier.width(140.dp)
                                )
                            }
                        }

                        Divider(color = Color(0xFF1E283F))

                        // Preview Rows
                        val previewRows = listOf(
                            listOf("Doris Penrose", "Aisha Khan", "03/10/2026", "08:00", "08:45", "Morning Run", "Completed", "4821", "YO24 1AA"),
                            listOf("Margaret Holloway", "Tom Brennan", "03/10/2026", "09:00", "09:45", "Morning Run", "Completed", "7712", "LS17 7QR"),
                            listOf("Naim", "Unassigned", "03/10/2026", "09:00", "10:00", "Morning Run", "In Progress", "1902", "BD1 4LR"),
                            listOf("Geoffrey Whitfield", "Lucia Marques", "03/10/2026", "11:30", "12:15", "Lunch Run", "Scheduled", "3450", "HG1 4PN"),
                            listOf("Zola", "Daniel Okafor", "03/10/2026", "13:00", "14:00", "Lunch Run", "Scheduled", "6621", "N1 2AB")
                        )

                        previewRows.forEach { rowData ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                selectedColumns.forEachIndexed { idx, _ ->
                                    val cellText = rowData.getOrElse(idx % rowData.size) { "—" }
                                    Text(
                                        text = cellText,
                                        fontSize = 13.sp,
                                        color = if (cellText == "Completed") BaseEmerald300 else if (cellText == "In Progress") StatusInfoText else BaseTextHeading,
                                        modifier = Modifier.width(140.dp)
                                    )
                                }
                            }
                            Divider(color = Color(0xFF171C2E))
                        }
                    }
                }
            }

            // Saved & Scheduled Reports Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BaseSurface)
                        .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AlarmOn, contentDescription = null, tint = StatusVioletText, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Saved & Scheduled Reports", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                        }
                        Text("${savedTemplates.size} active automations", fontSize = 12.sp, color = BaseTextMuted)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        savedTemplates.forEach { template ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BaseTileChip)
                                    .border(1.dp, Color(0xFF1E283F), RoundedCornerShape(12.dp))
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(template.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("Dataset: ${template.dataset} • Format: ${template.format}", fontSize = 12.sp, color = BaseTextMuted)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("Recipients: ${template.recipientRoles.joinToString(", ")}", fontSize = 11.sp, color = BaseTextFaint)
                                }

                                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    StatusPill(status = template.scheduleFrequency, color = StatusInfoText)
                                    Button(
                                        onClick = { exportToastMessage = "Running automation: '${template.title}' dispatched to recipients." },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BaseModalInput),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                                        modifier = Modifier.height(30.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp)
                                    ) {
                                        Text("Run now", fontSize = 11.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Modal: Save & Schedule Report
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
                        Text("Schedule Automated Report", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                        IconButton(onClick = { showScheduleModal = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = BaseTextMuted)
                        }
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Configure automatic generation and dispatch for '$selectedDataset'.", fontSize = 13.sp, color = BaseTextMuted)

                        Column {
                            Text("Report Title", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = newReportTitle,
                                onValueChange = { newReportTitle = it },
                                placeholder = { Text("e.g. Weekly Operations Summary", color = BaseTextFaint, fontSize = 13.sp) },
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

                        Column {
                            Text("Schedule Frequency", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = newFrequency,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        IconButton(onClick = { frequencyDropdownOpen = true }) {
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
                                    modifier = Modifier.fillMaxWidth().clickable { frequencyDropdownOpen = true }
                                )
                                DropdownMenu(
                                    expanded = frequencyDropdownOpen,
                                    onDismissRequest = { frequencyDropdownOpen = false },
                                    modifier = Modifier.background(BaseModalBg)
                                ) {
                                    listOf("Daily (07:00)", "Weekly Mon (08:00)", "Monthly 1st (08:00)", "End of Month").forEach { freq ->
                                        DropdownMenuItem(
                                            text = { Text(freq, color = BaseTextHeading) },
                                            onClick = {
                                                newFrequency = freq
                                                frequencyDropdownOpen = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Text("Recipients: Registered Manager, Coordinator, Finance", fontSize = 12.sp, color = BaseEmerald300)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newReport = SavedReportTemplate(
                                title = newReportTitle.ifEmpty { "Automated $selectedDataset Report" },
                                dataset = selectedDataset,
                                scheduleFrequency = newFrequency,
                                lastGenerated = "Scheduled",
                                recipientRoles = listOf("Registered Manager", "Coordinator")
                            )
                            savedTemplates = savedTemplates + newReport
                            showScheduleModal = false
                            exportToastMessage = "Report '${newReport.title}' successfully scheduled for $newFrequency!"
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Save & Schedule", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showScheduleModal = false }) {
                        Text("Cancel", color = BaseTextMuted)
                    }
                }
            )
        }
    }
}
