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
import com.example.model.PolicyDocument
import com.example.model.PolicyVersionEntry
import com.example.ui.theme.CareMintTeal
import com.example.ui.theme.CareMintTealContainer
import java.util.UUID

@Composable
fun PolicyLibraryView(
    repository: CareRepository
) {
    val policies by repository.policies.collectAsState()

    var showCreatePolicyDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedPolicyForHistory by remember { mutableStateOf<PolicyDocument?>(null) }

    val filteredPolicies = policies.filter { selectedCategory == null || it.category == selectedCategory }
    val overdueCount = policies.count { it.isOverdue }

    Column(modifier = Modifier.fillMaxSize()) {
        // Overdue Policies Alert Banner
        if (overdueCount > 0) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFEF4444).copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$overdueCount policy review(s) overdue. CQC Regulation 17 requires annual review of standard operating procedures.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF4444)
                    )
                }
            }
        }

        // Action & Filter Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("All (${policies.size})", fontSize = 10.sp) }
                )
                FilterChip(
                    selected = selectedCategory == "Clinical Governance",
                    onClick = { selectedCategory = "Clinical Governance" },
                    label = { Text("Clinical", fontSize = 10.sp) }
                )
                FilterChip(
                    selected = selectedCategory == "Safeguarding",
                    onClick = { selectedCategory = "Safeguarding" },
                    label = { Text("Safeguarding", fontSize = 10.sp) }
                )
                FilterChip(
                    selected = selectedCategory == "Health & Safety",
                    onClick = { selectedCategory = "Health & Safety" },
                    label = { Text("H&S", fontSize = 10.sp) }
                )
            }

            Button(
                onClick = { showCreatePolicyDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(32.dp).testTag("new_policy_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color(0xFF0A1128), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Policy", color = Color(0xFF0A1128), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Policy Cards List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredPolicies) { policy ->
                PolicyCard(
                    policy = policy,
                    onAcknowledge = { repository.acknowledgePolicy(policy.id) },
                    onViewHistory = { selectedPolicyForHistory = policy }
                )
            }
        }
    }

    // Modal: Create New Policy
    if (showCreatePolicyDialog) {
        var polTitle by remember { mutableStateOf("Dignity, Respect and Mental Capacity (MCA/DoLS)") }
        var polCategory by remember { mutableStateOf("Clinical Governance") }
        var polVersion by remember { mutableStateOf("v1.0") }
        var polSummary by remember { mutableStateOf("Establishes best interest decision protocols, two-stage capacity test, and emergency DoLS notifications.") }
        var linkedStatements by remember { mutableStateOf("CAR-01, EFF-01") }

        AlertDialog(
            onDismissRequest = { showCreatePolicyDialog = false },
            title = { Text("Publish New Policy / SOP", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = polTitle,
                        onValueChange = { polTitle = it },
                        label = { Text("Policy Title") },
                        modifier = Modifier.fillMaxWidth().testTag("policy_title_input")
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = polCategory,
                        onValueChange = { polCategory = it },
                        label = { Text("Category (Clinical / Safeguarding / HR / H&S)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = linkedStatements,
                        onValueChange = { linkedStatements = it },
                        label = { Text("Linked Statements (e.g. SAFE-01, WELL-01)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = polSummary,
                        onValueChange = { polSummary = it },
                        label = { Text("Operational Summary") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newPol = PolicyDocument(
                            id = "pol-" + UUID.randomUUID().toString().take(6),
                            title = polTitle,
                            category = polCategory,
                            version = polVersion,
                            owner = "Eleanor Vance (Registered Manager)",
                            lastReviewedDate = "03/10/2026",
                            nextReviewDate = "03/10/2027",
                            approvedBy = "Eleanor Vance",
                            staffAcknowledgedCount = 1,
                            totalStaffCount = 12,
                            summaryContent = polSummary,
                            linkedStatementCodes = linkedStatements.split(",").map { it.trim() },
                            versionHistory = listOf(
                                PolicyVersionEntry(polVersion, "03/10/2026", "Initial approved publication", "Eleanor Vance")
                            )
                        )
                        repository.addPolicy(newPol)
                        showCreatePolicyDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                    modifier = Modifier.testTag("save_policy_button")
                ) {
                    Text("Publish Policy", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreatePolicyDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Modal: Policy Version History
    selectedPolicyForHistory?.let { policy ->
        AlertDialog(
            onDismissRequest = { selectedPolicyForHistory = null },
            title = { Text("Version History: ${policy.title}", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(text = "Current: ${policy.version} • Owner: ${policy.owner}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (policy.versionHistory.isEmpty()) {
                        Text("No prior version entries archived. Base version ${policy.version} active.", fontSize = 11.sp)
                    } else {
                        policy.versionHistory.forEach { v ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(v.version, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                                        Text(v.changeDate, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text(v.changeSummary, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                                    Text("Author: ${v.author}", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { selectedPolicyForHistory = null }, colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal)) {
                    Text("Close", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun PolicyCard(
    policy: PolicyDocument,
    onAcknowledge: () -> Unit,
    onViewHistory: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = policy.title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(shape = RoundedCornerShape(4.dp), color = CareMintTealContainer) {
                            Text(text = policy.version, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CareMintTeal, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                    }
                    Text(
                        text = "${policy.category} • Review: ${policy.lastReviewedDate} • Next: ${policy.nextReviewDate}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "${policy.staffAcknowledgedCount}/${policy.totalStaffCount} Signed",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CareMintTeal,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = policy.summaryContent, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)

            if (policy.linkedStatementCodes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Supports CQC: ", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    policy.linkedStatementCodes.forEach { code ->
                        Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = CareMintTealContainer.copy(alpha = 0.5f),
                            modifier = Modifier.padding(horizontal = 2.dp)
                        ) {
                            Text(text = code, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CareMintTeal, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Version History",
                    fontSize = 10.sp,
                    color = CareMintTeal,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onViewHistory() }
                )

                OutlinedButton(
                    onClick = onAcknowledge,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(28.dp).testTag("ack_policy_${policy.id}")
                ) {
                    Text("Read & Acknowledge", fontSize = 10.sp)
                }
            }
        }
    }
}
