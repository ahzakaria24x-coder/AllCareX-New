package com.example.ui.training

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
import com.example.ui.common.NeutralPill
import com.example.ui.common.StatCard
import com.example.ui.common.StatusPill
import com.example.ui.theme.*

@Composable
fun StaffTrainingScreen(
    repository: CareRepository
) {
    val staffList by repository.staffList.collectAsState()
    val trainingList by repository.trainingList.collectAsState()

    var showLogModuleModal by remember { mutableStateOf(false) }

    // Modal state
    var selectedStaffName by remember { mutableStateOf("Rachel Stein - Coordinator") }
    var moduleName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Clinical") }
    var selectedValidity by remember { mutableStateOf("1 year") }
    var completedDate by remember { mutableStateOf("01/10/2026") }
    var staffDropdownOpen by remember { mutableStateOf(false) }
    var categoryDropdownOpen by remember { mutableStateOf(false) }
    var validityDropdownOpen by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(BasePageBg)) {
        val isDesktop = maxWidth >= 1024.dp
        val contentPadding = if (isDesktop) 32.dp else 16.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .testTag("staff_training_screen"),
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
                            text = "Staff Training",
                            fontSize = if (isDesktop) 30.sp else 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = BaseTextHeading
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Certification expiry dates and completed modules for every team member.",
                            fontSize = 14.sp,
                            color = BaseTextMuted
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(
                            onClick = { /* Send reminders */ },
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = BaseTextMuted, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Send reminders", fontSize = 14.sp)
                        }

                        Button(
                            onClick = { showLogModuleModal = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                            modifier = Modifier.height(44.dp).testTag("log_module_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Log module", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                    }
                }
            }

            // 4 Stat Cards
            item {
                if (isDesktop) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatCard("Team members", "${staffList.size}", "", Icons.Default.Group, BaseEmerald300, Modifier.weight(1f))
                        StatCard("Expiring ≤30 days", "0", "", Icons.Default.AccessTime, StatusMedText, Modifier.weight(1f))
                        StatCard("Expired", "5", "", Icons.Default.Warning, StatusCritText, Modifier.weight(1f))
                        StatCard("Modules logged", "9", "", Icons.Default.School, StatusVioletText, Modifier.weight(1f))
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard("Team members", "${staffList.size}", "", Icons.Default.Group, BaseEmerald300, Modifier.weight(1f))
                            StatCard("Expiring ≤30 days", "0", "", Icons.Default.AccessTime, StatusMedText, Modifier.weight(1f))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard("Expired", "5", "", Icons.Default.Warning, StatusCritText, Modifier.weight(1f))
                            StatCard("Modules logged", "9", "", Icons.Default.School, StatusVioletText, Modifier.weight(1f))
                        }
                    }
                }
            }

            // Card 1: Upcoming certification expiry
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BaseSurface)
                        .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Text("Upcoming certification expiry", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                    Text("Sorted by date — renew before expiry to stay compliant.", fontSize = 13.sp, color = BaseTextMuted)
                    Spacer(modifier = Modifier.height(14.dp))

                    val expiryItems = listOf(
                        Triple("Daniel Okafor", "Care Worker • Moving & Handling", "2026-08-09" to "Expired"),
                        Triple("Daniel Okafor", "Care Worker • Mandatory Training at work", "2026-08-10" to "Expired"),
                        Triple("Tom Brennan", "Senior Carer • Mandatory Training", "2026-08-20" to "Expired"),
                        Triple("Tom Brennan", "Senior Carer • Moving & Handling Refresher", "2026-08-20" to "Expired"),
                        Triple("Tom Brennan", "Senior Carer • Moving & Handling", "2026-09-15" to "Expired"),
                        Triple("Tom Brennan", "Senior Carer • First Aid at Work", "2027-01-12" to "Current"),
                        Triple("Aisha Khan", "Registered Nurse • Safeguarding Adults", "2027-02-01" to "Current")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        expiryItems.forEach { (name, desc, statusPair) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BaseTileChip)
                                    .border(1.dp, Color(0xFF1E2638), RoundedCornerShape(10.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(StatusInfoBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(name.firstOrNull()?.toString() ?: "S", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = StatusInfoText)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                                        Text(desc, fontSize = 12.sp, color = BaseTextMuted)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(statusPair.first, fontSize = 12.sp, color = BaseTextFaint)
                                    StatusPill(
                                        status = statusPair.second,
                                        color = if (statusPair.second == "Expired") StatusCritText else BaseEmerald300
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Card 2: Completed modules by team member
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BaseSurface)
                        .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Text("Completed modules by team member", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                    Text("Training history logged for each person.", fontSize = 13.sp, color = BaseTextMuted)
                    Spacer(modifier = Modifier.height(14.dp))

                    val memberModules = listOf(
                        "Lucia Marques" to listOf("Food Hygiene Level 2 • 2026-06-01", "Data Protection (GDPR) • 2026-04-15"),
                        "Daniel Okafor" to listOf("Data Protection (GDPR) • 2026-04-15", "Moving & Handling • 2024-08-09"),
                        "Tom Brennan" to listOf("First Aid at Work • 2024-11-05", "Moving & Handling Refresher • 2025-09-20", "Infection Prevention & Control • 2026-01-12"),
                        "Rachel Stein" to emptyList<String>(),
                        "Aisha Khan" to listOf("Safeguarding Adults Level 3 • 2026-02-18", "Medication Management • 2026-05-18")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        memberModules.forEach { (name, modules) ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BaseTileChip)
                                    .border(1.dp, Color(0xFF1E2638), RoundedCornerShape(10.dp))
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(StatusVioletBg),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(name.firstOrNull()?.toString() ?: "S", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StatusVioletText)
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                                    }
                                    NeutralPill(text = "${modules.size} modules")
                                }

                                if (modules.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        modules.forEach { mod ->
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BaseEmerald300, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(mod, fontSize = 12.sp, color = BaseTextBody)
                                            }
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("No modules logged yet.", fontSize = 12.sp, color = BaseTextFaint)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Modal: Log completed module
        if (showLogModuleModal) {
            AlertDialog(
                onDismissRequest = { showLogModuleModal = false },
                containerColor = BaseModalBg,
                shape = RoundedCornerShape(16.dp),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Log completed module", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                        IconButton(onClick = { showLogModuleModal = false }) {
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

                        // Module name
                        Column {
                            Text("Module name", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = moduleName,
                                onValueChange = { moduleName = it },
                                placeholder = { Text("e.g. Moving & Handling Refresher", color = BaseTextFaint) },
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

                        // Category and Validity Row
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Category", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = selectedCategory,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = {
                                            IconButton(onClick = { categoryDropdownOpen = true }) {
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
                                        modifier = Modifier.fillMaxWidth().clickable { categoryDropdownOpen = true }
                                    )
                                    DropdownMenu(
                                        expanded = categoryDropdownOpen,
                                        onDismissRequest = { categoryDropdownOpen = false },
                                        modifier = Modifier.background(BaseModalBg)
                                    ) {
                                        listOf("Clinical", "Safeguarding", "Moving & Handling", "Infection Control", "Medication", "First Aid", "Other").forEach { c ->
                                            DropdownMenuItem(
                                                text = { Text(c, color = BaseTextHeading) },
                                                onClick = {
                                                    selectedCategory = c
                                                    categoryDropdownOpen = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Validity", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = selectedValidity,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = {
                                            IconButton(onClick = { validityDropdownOpen = true }) {
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
                                        modifier = Modifier.fillMaxWidth().clickable { validityDropdownOpen = true }
                                    )
                                    DropdownMenu(
                                        expanded = validityDropdownOpen,
                                        onDismissRequest = { validityDropdownOpen = false },
                                        modifier = Modifier.background(BaseModalBg)
                                    ) {
                                        listOf("1 year", "2 years", "3 years").forEach { v ->
                                            DropdownMenuItem(
                                                text = { Text(v, color = BaseTextHeading) },
                                                onClick = {
                                                    selectedValidity = v
                                                    validityDropdownOpen = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Completed date
                        Column {
                            Text("Completed date", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = completedDate,
                                onValueChange = { completedDate = it },
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
                        onClick = { showLogModuleModal = false },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("Save", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showLogModuleModal = false },
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("Cancel", fontSize = 14.sp, color = BaseTextMuted)
                    }
                }
            )
        }
    }
}
