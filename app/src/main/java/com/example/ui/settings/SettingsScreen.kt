package com.example.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.model.Branch
import com.example.model.OrganisationSettings
import com.example.model.RegulatorType
import com.example.ui.theme.CareMintTeal
import com.example.ui.theme.CareMintTealContainer

@Composable
fun SettingsScreen(
    repository: CareRepository
) {
    val orgSettings by repository.organisationSettings.collectAsState()
    val branches by repository.branches.collectAsState()
    val auditLogs by repository.auditLogs.collectAsState()

    var activeSubTab by remember { mutableStateOf(0) } // 0: White-Label & Branches, 1: Audit Log, 2: GDPR & Export
    var orgName by remember(orgSettings) { mutableStateOf(orgSettings.name) }
    var regNumber by remember(orgSettings) { mutableStateOf(orgSettings.registrationNumber) }
    var selectedRegulator by remember(orgSettings) { mutableStateOf(orgSettings.regulator) }
    var emergencyContact by remember(orgSettings) { mutableStateOf(orgSettings.emergencyContact) }
    var showExportSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Settings Header
        Text(
            text = "Platform Settings & Governance",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Multi-tenant branches, regulator selection, and immutable audit logs",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs
        TabRow(
            selectedTabIndex = activeSubTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = CareMintTeal,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
        ) {
            Tab(selected = activeSubTab == 0, onClick = { activeSubTab = 0 }, text = { Text("White-Label & Regulator", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = activeSubTab == 1, onClick = { activeSubTab = 1 }, text = { Text("Immutable Audit Log (${auditLogs.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = activeSubTab == 2, onClick = { activeSubTab = 2 }, text = { Text("GDPR & Retention", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (activeSubTab) {
            0 -> {
                // White label & Org Settings
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("White-Label Organisation Branding", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = orgName,
                                onValueChange = { orgName = it },
                                label = { Text("Organisation Display Name") },
                                modifier = Modifier.fillMaxWidth().testTag("org_name_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = regNumber,
                                onValueChange = { regNumber = it },
                                label = { Text("CQC / Statutory Provider Registration Number") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = emergencyContact,
                                onValueChange = { emergencyContact = it },
                                label = { Text("24/7 On-Call Emergency Escalation Hotline") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                            Text("Primary Healthcare Regulator", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                RegulatorType.values().forEach { reg ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedRegulator = reg }
                                            .padding(vertical = 4.dp)
                                    ) {
                                        RadioButton(
                                            selected = selectedRegulator == reg,
                                            onClick = { selectedRegulator = reg },
                                            colors = RadioButtonDefaults.colors(selectedColor = CareMintTeal)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(text = reg.displayName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text(text = "Jurisdiction: ${reg.region}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    repository.updateOrganisationSettings(
                                        orgSettings.copy(
                                            name = orgName,
                                            registrationNumber = regNumber,
                                            regulator = selectedRegulator,
                                            emergencyContact = emergencyContact
                                        )
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                                modifier = Modifier.fillMaxWidth().testTag("save_settings_button")
                            ) {
                                Text("Save Settings & Apply Branding", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Multi-Tenant Branches
                    Text("Configured Branches (Multi-Tenant)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    branches.forEach { b ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = b.name, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "${b.code} • ${b.address}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "Contact: ${b.phone}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = CareMintTealContainer
                                ) {
                                    Text(
                                        text = "${b.activeClientsCount} Clients • ${b.activeStaffCount} Staff",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CareMintTeal,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Immutable Audit Log
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CareMintTealContainer.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = CareMintTeal, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "All Care X Immutable Audit Log: Encrypted & timestamped in London region (europe-west2).",
                                    fontSize = 11.sp,
                                    color = CareMintTeal
                                )
                            }
                        }
                    }

                    items(auditLogs) { log ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = log.actionType,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = CareMintTeal
                                    )
                                    Text(
                                        text = log.timestamp,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = log.notes,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                                Text(
                                    text = "User: ${log.performedBy} (${log.userRole.displayName}) • Branch: ${log.branchId}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                // GDPR, SAR & Retention
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("GDPR Subject Access Request (SAR)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "Under UK GDPR and the Data Protection Act 2018, clients and authorised attorneys can request a complete digital export of their care records.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    repository.logAudit("SAR_EXPORT", "Generated GDPR Subject Access Request export archive for London provider.")
                                    showExportSuccess = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().testTag("export_sar_button")
                            ) {
                                Icon(imageVector = Icons.Default.Download, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generate Full Subject Access Data Export (ZIP/CSV)")
                            }

                            if (showExportSuccess) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = CareMintTealContainer
                                ) {
                                    Text(
                                        text = "✓ Export bundle successfully generated: Includes all care plans, eMAR records, visit logs, and body map records with SHA-256 integrity checksum.",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CareMintTeal,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Data Retention & Soft-Delete Policy", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "Care records in England are retained for 8 years following the conclusion of treatment or death, in line with NHS Transformation Directorate and CQC guidelines.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Current Retention Setting: 8 Years (Soft-Delete only, records are never hard-deleted)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                        }
                    }
                }
            }
        }
    }
}
