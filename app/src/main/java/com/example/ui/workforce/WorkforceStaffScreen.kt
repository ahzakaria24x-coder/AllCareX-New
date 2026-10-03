package com.example.ui.workforce

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
import com.example.model.StaffProfile
import com.example.model.UserRole
import com.example.ui.common.NeutralPill
import com.example.ui.common.StatusPill
import com.example.ui.theme.*

@Composable
fun WorkforceStaffScreen(
    repository: CareRepository
) {
    val staffList by repository.staffList.collectAsState()

    var showAddStaffModal by remember { mutableStateOf(false) }

    // Form state for Add Staff
    var fullName by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Care Worker") }
    var selectedContract by remember { mutableStateOf("full time") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var trainingExpiry by remember { mutableStateOf("01/10/2026") }
    var compliancePct by remember { mutableStateOf("100") }
    var skills by remember { mutableStateOf("") }
    var dbsNumber by remember { mutableStateOf("") }
    var dbsStatus by remember { mutableStateOf("not submitted") }
    var issueDate by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var enrolledUpdateService by remember { mutableStateOf(false) }

    var roleDropdownOpen by remember { mutableStateOf(false) }
    var contractDropdownOpen by remember { mutableStateOf(false) }
    var dbsStatusDropdownOpen by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(BasePageBg)) {
        val isDesktop = maxWidth >= 1024.dp
        val contentPadding = if (isDesktop) 32.dp else 16.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .testTag("workforce_screen"),
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
                            text = "Workforce",
                            fontSize = if (isDesktop) 30.sp else 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = BaseTextHeading
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Staff profiles, roles, training, DBS and compliance.",
                            fontSize = 14.sp,
                            color = BaseTextMuted
                        )
                    }

                    Button(
                        onClick = { showAddStaffModal = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                        modifier = Modifier.height(44.dp).testTag("add_staff_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add staff", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }

            // Staff Cards (3 columns on desktop, 1 on mobile)
            val chunked = staffList.chunked(if (isDesktop) 3 else 1)
            chunked.forEach { rowStaff ->
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowStaff.forEach { staff ->
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(BaseSurface)
                                    .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
                                    .padding(20.dp)
                            ) {
                                // Header: Initial avatar, Name, Role, Status pill
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(StatusInfoBg),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = staff.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = StatusInfoText
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(staff.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                                            Text(staff.role.displayName, fontSize = 13.sp, color = BaseTextMuted)
                                        }
                                    }

                                    StatusPill(
                                        status = if (staff.isOnLeave) "on_leave" else "active",
                                        color = if (staff.isOnLeave) StatusMedText else BaseEmerald300
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Contact Info
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Email, contentDescription = null, tint = BaseTextFaint, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(staff.email, fontSize = 12.sp, color = BaseTextMuted)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = BaseTextFaint, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(staff.phone, fontSize = 12.sp, color = BaseTextMuted)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.School, contentDescription = null, tint = BaseTextFaint, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Training: 2026-11-30", fontSize = 12.sp, color = BaseTextMuted)
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Compliance and DBS
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Compliance", fontSize = 12.sp, color = BaseTextMuted)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BaseEmerald300, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${staff.compliancePercentage}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BaseEmerald300)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = BaseTextFaint, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("DBS: Not Submitted", fontSize = 11.sp, color = BaseTextFaint)
                                    }
                                }
                            }
                        }
                        if (rowStaff.size < 3 && isDesktop) {
                            repeat(3 - rowStaff.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // Modal: Add Staff Member
        if (showAddStaffModal) {
            AlertDialog(
                onDismissRequest = { showAddStaffModal = false },
                containerColor = BaseModalBg,
                shape = RoundedCornerShape(16.dp),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Add Staff Member", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                        IconButton(onClick = { showAddStaffModal = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = BaseTextMuted)
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Full name
                        Column {
                            Text("Full name", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
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

                        // Role and Contract Row
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Role", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = selectedRole,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = {
                                            IconButton(onClick = { roleDropdownOpen = true }) {
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
                                        modifier = Modifier.fillMaxWidth().clickable { roleDropdownOpen = true }
                                    )
                                    DropdownMenu(
                                        expanded = roleDropdownOpen,
                                        onDismissRequest = { roleDropdownOpen = false },
                                        modifier = Modifier.background(BaseModalBg)
                                    ) {
                                        listOf("Care Worker", "Senior Carer", "Registered Nurse", "Team Leader", "Care Manager", "Coordinator", "HR", "Admin").forEach { r ->
                                            DropdownMenuItem(
                                                text = { Text(r, color = BaseTextHeading) },
                                                onClick = {
                                                    selectedRole = r
                                                    roleDropdownOpen = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Contract", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = selectedContract,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = {
                                            IconButton(onClick = { contractDropdownOpen = true }) {
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
                                        modifier = Modifier.fillMaxWidth().clickable { contractDropdownOpen = true }
                                    )
                                    DropdownMenu(
                                        expanded = contractDropdownOpen,
                                        onDismissRequest = { contractDropdownOpen = false },
                                        modifier = Modifier.background(BaseModalBg)
                                    ) {
                                        listOf("full time", "part time", "bank", "agency").forEach { c ->
                                            DropdownMenuItem(
                                                text = { Text(c, color = BaseTextHeading) },
                                                onClick = {
                                                    selectedContract = c
                                                    contractDropdownOpen = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Email and Phone Row
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Email", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
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
                                Text("Phone", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = phone,
                                    onValueChange = { phone = it },
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

                        // Skills
                        Column {
                            Text("Skills", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = skills,
                                onValueChange = { skills = it },
                                placeholder = { Text("e.g. Medication, Moving & Handling, Dementia", color = BaseTextFaint) },
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

                        // DBS Update Service checkbox
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = enrolledUpdateService,
                                onCheckedChange = { enrolledUpdateService = it },
                                colors = CheckboxDefaults.colors(checkedColor = BaseEmerald500)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Enrolled in DBS Update Service", fontSize = 13.sp, color = BaseTextHeading)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newStaff = StaffProfile(
                                id = "staff-${System.currentTimeMillis()}",
                                branchId = "branch-cmd",
                                fullName = fullName.ifEmpty { "New Team Member" },
                                role = when (selectedRole) {
                                    "Senior Carer" -> UserRole.SENIOR_CARER
                                    "Registered Nurse" -> UserRole.NURSE
                                    "Coordinator" -> UserRole.COORDINATOR
                                    "Care Manager" -> UserRole.REGISTERED_MANAGER
                                    else -> UserRole.CARE_WORKER
                                },
                                email = email.ifEmpty { "staff@allcarex.com" },
                                phone = phone.ifEmpty { "07700 900000" },
                                compliancePercentage = compliancePct.toIntOrNull() ?: 100,
                                skills = if (skills.isNotEmpty()) skills.split(",").map { it.trim() } else listOf("Moving & Handling", "Personal Care")
                            )
                            repository.addStaff(newStaff)
                            showAddStaffModal = false
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
                        onClick = { showAddStaffModal = false },
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("Cancel", fontSize = 14.sp, color = BaseTextMuted)
                    }
                }
            )
        }
    }
}
