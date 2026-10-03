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
import com.example.model.AssessmentFramework
import com.example.model.KeyQuestion
import com.example.model.QualityStatementOrKloe
import com.example.ui.theme.CareMintTeal
import com.example.ui.theme.CareMintTealContainer
import java.util.UUID

@Composable
fun FrameworkManagerView(
    repository: CareRepository
) {
    val frameworks by repository.frameworks.collectAsState()

    var showCreateFrameworkDialog by remember { mutableStateOf(false) }
    var selectedFrameworkForDetail by remember { mutableStateOf<AssessmentFramework?>(null) }
    var showAddKeyQuestionDialog by remember { mutableStateOf<String?>(null) }
    var showAddStatementDialog by remember { mutableStateOf<Pair<String, String>?>(null) } // frameworkId to kqId

    Column(modifier = Modifier.fillMaxSize()) {
        // Header Banner explaining dynamic data model
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Dynamic Regulatory Framework Manager",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CareMintTeal
                        )
                        Text(
                            text = "Regulator frameworks stored as editable data. Versionable for CQC transition to KLOEs, CIW, Care Inspectorate Scotland, or RQIA.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { showCreateFrameworkDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp).testTag("new_framework_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color(0xFF0A1128), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ New Framework", color = Color(0xFF0A1128), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Framework List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(frameworks) { framework ->
                val isExpanded = selectedFrameworkForDetail?.id == framework.id

                FrameworkCard(
                    framework = framework,
                    isExpanded = isExpanded,
                    onToggleExpand = {
                        selectedFrameworkForDetail = if (isExpanded) null else framework
                    },
                    onSetActive = { repository.setActiveFramework(framework.id) },
                    onAddKeyQuestion = { showAddKeyQuestionDialog = framework.id },
                    onAddStatement = { kqId -> showAddStatementDialog = Pair(framework.id, kqId) }
                )
            }
        }
    }

    // Modal: Create or Import Framework
    if (showCreateFrameworkDialog) {
        var fwName by remember { mutableStateOf("Care Inspectorate Scotland Framework") }
        var fwRegulator by remember { mutableStateOf("Care Inspectorate Scotland") }
        var fwVersion by remember { mutableStateOf("v1.0") }
        var fwEffectiveDate by remember { mutableStateOf("01/01/2027") }

        AlertDialog(
            onDismissRequest = { showCreateFrameworkDialog = false },
            title = { Text("Add / Import Regulatory Framework", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Configurable framework structure for multi-regulator compliance (CQC, CIW, CIS, RQIA).", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = fwName,
                        onValueChange = { fwName = it },
                        label = { Text("Framework Title") },
                        modifier = Modifier.fillMaxWidth().testTag("framework_name_input")
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = fwRegulator,
                        onValueChange = { fwRegulator = it },
                        label = { Text("Regulator (e.g. CQC / CIW / CIS)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = fwVersion,
                            onValueChange = { fwVersion = it },
                            label = { Text("Version") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = fwEffectiveDate,
                            onValueChange = { fwEffectiveDate = it },
                            label = { Text("Effective Date") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newFw = AssessmentFramework(
                            id = "fw-" + UUID.randomUUID().toString().take(6),
                            name = fwName,
                            regulatorName = fwRegulator,
                            version = fwVersion,
                            effectiveDate = fwEffectiveDate,
                            isActive = false,
                            isArchived = false,
                            keyQuestions = listOf(
                                KeyQuestion("KQ-1", "Domain 1", "Shield", "Primary quality standards", 1, emptyList())
                            )
                        )
                        repository.addOrUpdateFramework(newFw)
                        showCreateFrameworkDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                    modifier = Modifier.testTag("save_new_framework_button")
                ) {
                    Text("Save Framework", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateFrameworkDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Modal: Add Key Question
    if (showAddKeyQuestionDialog != null) {
        val fwId = showAddKeyQuestionDialog!!
        var kqName by remember { mutableStateOf("Responsive & Adaptive Care") }
        var kqDesc by remember { mutableStateOf("Evaluates how quickly support is modified to match changing needs.") }

        AlertDialog(
            onDismissRequest = { showAddKeyQuestionDialog = null },
            title = { Text("Add Key Question / Domain", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = kqName,
                        onValueChange = { kqName = it },
                        label = { Text("Key Question Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = kqDesc,
                        onValueChange = { kqDesc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newKq = KeyQuestion(
                            id = "kq-" + UUID.randomUUID().toString().take(6),
                            name = kqName,
                            description = kqDesc,
                            order = 6,
                            statements = emptyList()
                        )
                        repository.addKeyQuestionToFramework(fwId, newKq)
                        showAddKeyQuestionDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal)
                ) {
                    Text("Add Question", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddKeyQuestionDialog = null }) { Text("Cancel") }
            }
        )
    }

    // Modal: Add Quality Statement / KLOE
    if (showAddStatementDialog != null) {
        val (fwId, kqId) = showAddStatementDialog!!
        var stmtCode by remember { mutableStateOf("SAFE-05") }
        var stmtTitle by remember { mutableStateOf("Infection prevention & outbreak control") }
        var stmtDesc by remember { mutableStateOf("Assesses clinical hygiene, PPE access, and winter infectious illness protocols.") }

        AlertDialog(
            onDismissRequest = { showAddStatementDialog = null },
            title = { Text("Add Quality Statement / KLOE", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = stmtCode,
                            onValueChange = { stmtCode = it },
                            label = { Text("Code") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = stmtTitle,
                            onValueChange = { stmtTitle = it },
                            label = { Text("Statement Title") },
                            modifier = Modifier.weight(2f)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = stmtDesc,
                        onValueChange = { stmtDesc = it },
                        label = { Text("Full Requirement Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newStmt = QualityStatementOrKloe(
                            id = "stmt-" + UUID.randomUUID().toString().take(6),
                            keyQuestionId = kqId,
                            code = stmtCode,
                            title = stmtTitle,
                            description = stmtDesc,
                            currentSelfAssessmentScore = 3
                        )
                        repository.addOrUpdateStatement(fwId, kqId, newStmt)
                        showAddStatementDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal)
                ) {
                    Text("Add Statement", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStatementDialog = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun FrameworkCard(
    framework: AssessmentFramework,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onSetActive: () -> Unit,
    onAddKeyQuestion: () -> Unit,
    onAddStatement: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (framework.isActive) CareMintTeal else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        ),
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
                        Text(text = framework.name, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (framework.isActive) CareMintTealContainer else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = if (framework.isActive) "ACTIVE" else "ARCHIVED / INACTIVE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (framework.isActive) CareMintTeal else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Regulator: ${framework.regulatorName} • Version: ${framework.version} • Effective: ${framework.effectiveDate}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (!framework.isActive) {
                    OutlinedButton(
                        onClick = onSetActive,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(30.dp).testTag("set_active_fw_${framework.id}")
                    ) {
                        Text("Set Active", fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Expandable Key Questions / Statements
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${framework.keyQuestions.size} Key Questions • ${framework.keyQuestions.sumOf { it.statements.size }} Statements/KLOEs",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CareMintTeal
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = CareMintTeal,
                    modifier = Modifier.size(18.dp)
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                framework.keyQuestions.forEach { kq ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${kq.name} (${kq.statements.size} Statements)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                TextButton(
                                    onClick = { onAddStatement(kq.id) },
                                    modifier = Modifier.height(26.dp)
                                ) {
                                    Text("+ Statement", fontSize = 10.sp, color = CareMintTeal)
                                }
                            }
                            Text(text = kq.description, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            if (kq.statements.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                kq.statements.forEach { stmt ->
                                    Text(
                                        text = "• ${stmt.code}: ${stmt.title} (Self-Score: ${stmt.currentSelfAssessmentScore}/4)",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onAddKeyQuestion,
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTealContainer),
                    modifier = Modifier.fillMaxWidth().height(32.dp)
                ) {
                    Text("+ Add Key Question / Domain", color = CareMintTeal, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
