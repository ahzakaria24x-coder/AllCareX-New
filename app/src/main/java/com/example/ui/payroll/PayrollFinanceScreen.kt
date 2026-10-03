package com.example.ui.payroll

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
import com.example.ui.common.NeutralPill
import com.example.ui.common.StatCard
import com.example.ui.common.StatusPill
import com.example.ui.theme.*

data class TimesheetEntry(
    val id: String,
    val staffName: String,
    val staffRole: String,
    val date: String,
    val hours: Double,
    val breakMin: Int,
    val rate: Double,
    val mileageKm: Int,
    val pay: Double,
    var status: String // draft, submitted, approved, paid
)

@Composable
fun PayrollFinanceScreen(
    repository: CareRepository
) {
    val staffList by repository.staffList.collectAsState()

    var activeTab by remember { mutableStateOf("Timesheets") } // Timesheets or Invoices
    var activeFilter by remember { mutableStateOf("All") } // All, Draft, Submitted, Approved, Paid
    var showLogTimesheetModal by remember { mutableStateOf(false) }
    var showCreateInvoiceModal by remember { mutableStateOf(false) }

    // Timesheet form state
    var selectedStaffName by remember { mutableStateOf("Rachel Stein - Coordinator") }
    var timesheetDate by remember { mutableStateOf("01/10/2026") }
    var breakMinutes by remember { mutableStateOf("30") }
    var startTime by remember { mutableStateOf("08:00") }
    var endTime by remember { mutableStateOf("16:00") }
    var payRate by remember { mutableStateOf("18") }
    var mileageKm by remember { mutableStateOf("10") }
    var timesheetStatus by remember { mutableStateOf("draft") }
    var staffDropdownOpen by remember { mutableStateOf(false) }
    var statusDropdownOpen by remember { mutableStateOf(false) }

    // Invoice form state
    var payerName by remember { mutableStateOf("") }
    var payerType by remember { mutableStateOf("client") }
    var invoiceAmount by remember { mutableStateOf("") }
    var periodStart by remember { mutableStateOf("27/09/2026") }
    var periodEnd by remember { mutableStateOf("01/10/2026") }
    var issueDate by remember { mutableStateOf("01/10/2026") }
    var dueDate by remember { mutableStateOf("15/10/2026") }
    var lineItems by remember { mutableStateOf("") }
    var payerTypeDropdownOpen by remember { mutableStateOf(false) }

    var timesheetEntries by remember {
        mutableStateOf(
            listOf(
                TimesheetEntry("ts-1", "Rachel Stein", "Coordinator", "2026-10-01", 8.0, 30, 18.0, 0, 135.00, "submitted"),
                TimesheetEntry("ts-2", "Daniel Okafor", "Care Worker", "2026-10-01", 8.0, 30, 14.5, 0, 108.75, "approved"),
                TimesheetEntry("ts-3", "Daniel Okafor", "Care Worker", "2026-09-03", 8.0, 30, 14.5, 0, 108.75, "paid"),
                TimesheetEntry("ts-4", "Lucia Marques", "Care Worker", "2026-09-02", 5.0, 0, 14.5, 3, 79.85, "draft"),
                TimesheetEntry("ts-5", "Tom Brennan", "Senior Carer", "2026-09-01", 6.0, 30, 17.0, 0, 93.50, "approved"),
                TimesheetEntry("ts-6", "Aisha Khan", "Registered Nurse", "2026-09-01", 12.0, 60, 24.0, 12, 268.40, "submitted"),
                TimesheetEntry("ts-7", "Daniel Okafor", "Care Worker", "2026-08-31", 6.0, 0, 14.5, 5, 92.00, "paid")
            )
        )
    }

    val filteredEntries = if (activeFilter == "All") {
        timesheetEntries
    } else {
        timesheetEntries.filter { it.status.equals(activeFilter, ignoreCase = true) }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(BasePageBg)) {
        val isDesktop = maxWidth >= 1024.dp
        val contentPadding = if (isDesktop) 32.dp else 16.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .testTag("payroll_finance_screen"),
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
                            text = "Payroll & Finance",
                            fontSize = if (isDesktop) 30.sp else 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = BaseTextHeading
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Timesheets, pay, invoicing and mileage for the finance service.",
                            fontSize = 14.sp,
                            color = BaseTextMuted
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(
                            onClick = { showCreateInvoiceModal = true },
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(Icons.Default.Receipt, contentDescription = null, tint = BaseTextMuted, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Create invoice", fontSize = 14.sp)
                        }

                        Button(
                            onClick = { showLogTimesheetModal = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                            modifier = Modifier.height(44.dp).testTag("log_timesheet_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Log timesheet", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                    }
                }
            }

            // 4 Stat Cards
            item {
                if (isDesktop) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatCard("Hours this week", "16.0", "", Icons.Default.AccessTime, BaseTextHeading, Modifier.weight(1f))
                        StatCard("Paid pay (period)", "£82.00", "", Icons.Default.AccountBalanceWallet, BaseTextHeading, Modifier.weight(1f))
                        StatCard("Outstanding invoices", "£0.00", "", Icons.Default.Description, BaseTextHeading, Modifier.weight(1f))
                        StatCard("Invoices paid", "£9,420.50", "", Icons.Default.CheckCircle, BaseTextHeading, Modifier.weight(1f))
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard("Hours this week", "16.0", "", Icons.Default.AccessTime, BaseTextHeading, Modifier.weight(1f))
                            StatCard("Paid pay (period)", "£82.00", "", Icons.Default.AccountBalanceWallet, BaseTextHeading, Modifier.weight(1f))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard("Outstanding invoices", "£0.00", "", Icons.Default.Description, BaseTextHeading, Modifier.weight(1f))
                            StatCard("Invoices paid", "£9,420.50", "", Icons.Default.CheckCircle, BaseTextHeading, Modifier.weight(1f))
                        }
                    }
                }
            }

            // Segmented Tabs: Timesheets, Invoices
            item {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E293B).copy(alpha = 0.6f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Timesheets", "Invoices").forEach { tab ->
                        val isSel = activeTab == tab
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) Color(0xFF0A0A0A) else Color.Transparent)
                                .clickable { activeTab = tab }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(tab, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = if (isSel) Color.White else BaseTextMuted)
                        }
                    }
                }
            }

            // Sub-filters for Timesheets: All, Draft, Submitted, Approved, Paid
            if (activeTab == "Timesheets") {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("All", "Draft", "Submitted", "Approved", "Paid").forEach { f ->
                            val isSel = activeFilter == f
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSel) BaseEmerald500 else Color(0xFF161C2D))
                                    .clickable { activeFilter = f }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(f, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = if (isSel) Color.White else BaseTextMuted)
                            }
                        }
                    }
                }

                // Timesheet Table Card
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(BaseSurface)
                            .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        // Table content in a horizontal scrollable view for responsiveness
                        val hScroll = rememberScrollState()
                        Column(modifier = Modifier.fillMaxWidth().horizontalScroll(hScroll)) {
                            // Table Header Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(0.dp, Color.Transparent)
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(24.dp)
                            ) {
                                Text("STAFF", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BaseTextMuted, modifier = Modifier.width(160.dp))
                                Text("DATE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BaseTextMuted, modifier = Modifier.width(90.dp))
                                Text("HOURS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BaseTextMuted, modifier = Modifier.width(90.dp))
                                Text("RATE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BaseTextMuted, modifier = Modifier.width(80.dp))
                                Text("MILEAGE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BaseTextMuted, modifier = Modifier.width(70.dp))
                                Text("PAY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BaseTextMuted, modifier = Modifier.width(80.dp))
                                Text("STATUS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BaseTextMuted, modifier = Modifier.width(90.dp))
                                Text("ACTION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BaseTextMuted, modifier = Modifier.width(100.dp))
                            }

                            Divider(color = Color(0xFF1E2638))

                            // Table Rows
                            filteredEntries.forEach { ts ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                                ) {
                                    Column(modifier = Modifier.width(160.dp)) {
                                        Text(ts.staffName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                                        Text(ts.staffRole, fontSize = 12.sp, color = BaseTextMuted)
                                    }
                                    Text(ts.date, fontSize = 13.sp, color = BaseTextHeading, modifier = Modifier.width(90.dp))
                                    Column(modifier = Modifier.width(90.dp)) {
                                        Text("${ts.hours}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                                        Text("break ${ts.breakMin}m", fontSize = 11.sp, color = BaseTextFaint)
                                    }
                                    Text("£${ts.rate}/hr", fontSize = 13.sp, color = BaseTextHeading, modifier = Modifier.width(80.dp))
                                    Text("${ts.mileageKm}km", fontSize = 13.sp, color = BaseTextHeading, modifier = Modifier.width(70.dp))
                                    Text("£${String.format("%.2f", ts.pay)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BaseEmerald300, modifier = Modifier.width(80.dp))
                                    Box(modifier = Modifier.width(90.dp)) {
                                        StatusPill(
                                            status = ts.status,
                                            color = when (ts.status) {
                                                "paid" -> BaseEmerald300
                                                "approved" -> StatusInfoText
                                                "submitted" -> StatusVioletText
                                                else -> BaseTextFaint
                                            }
                                        )
                                    }
                                    Box(modifier = Modifier.width(100.dp)) {
                                        if (ts.status == "submitted") {
                                            Button(
                                                onClick = {
                                                    timesheetEntries = timesheetEntries.map {
                                                        if (it.id == ts.id) it.copy(status = "approved") else it
                                                    }
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = BaseModalInput),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                                                modifier = Modifier.height(30.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp)
                                            ) {
                                                Text("Approve", fontSize = 11.sp, color = Color.White)
                                            }
                                        } else if (ts.status == "approved") {
                                            Button(
                                                onClick = {
                                                    timesheetEntries = timesheetEntries.map {
                                                        if (it.id == ts.id) it.copy(status = "paid") else it
                                                    }
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                                                modifier = Modifier.height(30.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp)
                                            ) {
                                                Text("Mark paid", fontSize = 11.sp, color = Color.White)
                                            }
                                        }
                                    }
                                }
                                Divider(color = Color(0xFF171C2E))
                            }
                        }
                    }
                }
            } else {
                // Invoices list empty/placeholder
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(BaseSurface)
                            .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No outstanding invoices.", fontSize = 14.sp, color = BaseTextMuted)
                    }
                }
            }
        }

        // Modal: Log timesheet
        if (showLogTimesheetModal) {
            AlertDialog(
                onDismissRequest = { showLogTimesheetModal = false },
                containerColor = BaseModalBg,
                shape = RoundedCornerShape(16.dp),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Log timesheet", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                        IconButton(onClick = { showLogTimesheetModal = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = BaseTextMuted)
                        }
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Team member dropdown
                        Column {
                            Text("Team member", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = selectedStaffName,
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
                                            text = { Text("${s.name} - ${s.role.displayName}", color = BaseTextHeading) },
                                            onClick = {
                                                selectedStaffName = "${s.name} - ${s.role.displayName}"
                                                staffDropdownOpen = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Date and Break
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Date", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = timesheetDate,
                                    onValueChange = { timesheetDate = it },
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
                                Text("Break (min)", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = breakMinutes,
                                    onValueChange = { breakMinutes = it },
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

                        // Start and End time
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Start time", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = startTime,
                                    onValueChange = { startTime = it },
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
                                Text("End time", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = endTime,
                                    onValueChange = { endTime = it },
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

                        // Pay rate and Mileage
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Pay rate (£/hr)", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = payRate,
                                    onValueChange = { payRate = it },
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
                                Text("Mileage (km)", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = mileageKm,
                                    onValueChange = { mileageKm = it },
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

                        // Status dropdown
                        Column {
                            Text("Status", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = timesheetStatus,
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
                                    listOf("draft", "submitted", "approved", "paid").forEach { st ->
                                        DropdownMenuItem(
                                            text = { Text(st, color = BaseTextHeading) },
                                            onClick = {
                                                timesheetStatus = st
                                                statusDropdownOpen = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Computed duration notice
                        Text(
                            text = "Computed duration: 8.0 hrs • £135.00",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BaseEmerald300
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newEntry = TimesheetEntry(
                                id = "ts-${System.currentTimeMillis()}",
                                staffName = selectedStaffName.split(" - ").first(),
                                staffRole = selectedStaffName.split(" - ").getOrElse(1) { "Care Worker" },
                                date = timesheetDate,
                                hours = 8.0,
                                breakMin = breakMinutes.toIntOrNull() ?: 30,
                                rate = payRate.toDoubleOrNull() ?: 14.5,
                                mileageKm = mileageKm.toIntOrNull() ?: 0,
                                pay = (payRate.toDoubleOrNull() ?: 14.5) * 8.0,
                                status = timesheetStatus
                            )
                            timesheetEntries = listOf(newEntry) + timesheetEntries
                            showLogTimesheetModal = false
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("Save", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showLogTimesheetModal = false },
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("Cancel", fontSize = 14.sp, color = BaseTextMuted)
                    }
                }
            )
        }

        // Modal: Create invoice
        if (showCreateInvoiceModal) {
            AlertDialog(
                onDismissRequest = { showCreateInvoiceModal = false },
                containerColor = BaseModalBg,
                shape = RoundedCornerShape(16.dp),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Create Invoice", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                        IconButton(onClick = { showCreateInvoiceModal = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = BaseTextMuted)
                        }
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Payer name
                        Column {
                            Text("Payer name", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = payerName,
                                onValueChange = { payerName = it },
                                placeholder = { Text("e.g. Westminster Council", color = BaseTextFaint) },
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

                        // Payer type & Amount
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Payer type", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = payerType,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = {
                                            IconButton(onClick = { payerTypeDropdownOpen = true }) {
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
                                        modifier = Modifier.fillMaxWidth().clickable { payerTypeDropdownOpen = true }
                                    )
                                    DropdownMenu(
                                        expanded = payerTypeDropdownOpen,
                                        onDismissRequest = { payerTypeDropdownOpen = false },
                                        modifier = Modifier.background(BaseModalBg)
                                    ) {
                                        listOf("client", "commissioner", "local authority", "private").forEach { pt ->
                                            DropdownMenuItem(
                                                text = { Text(pt, color = BaseTextHeading) },
                                                onClick = {
                                                    payerType = pt
                                                    payerTypeDropdownOpen = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Amount (£)", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = invoiceAmount,
                                    onValueChange = { invoiceAmount = it },
                                    placeholder = { Text("0.00", color = BaseTextFaint) },
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

                        // Period start & end
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Period start", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = periodStart,
                                    onValueChange = { periodStart = it },
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
                                Text("Period end", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = periodEnd,
                                    onValueChange = { periodEnd = it },
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

                        // Line items / description
                        Column {
                            Text("Line items / description", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = lineItems,
                                onValueChange = { lineItems = it },
                                placeholder = { Text("e.g. 42 care hours + mileage", color = BaseTextFaint) },
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
                        onClick = { showCreateInvoiceModal = false },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("Save", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showCreateInvoiceModal = false },
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("Cancel", fontSize = 14.sp, color = BaseTextMuted)
                    }
                }
            )
        }
    }
}
