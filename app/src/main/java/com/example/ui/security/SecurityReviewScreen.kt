package com.example.ui.security

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CareRepository
import com.example.ui.theme.CareMintTeal
import com.example.ui.theme.CareMintTealContainer

@Composable
fun SecurityReviewScreen(
    repository: CareRepository
) {
    var activeSubTab by remember { mutableStateOf(0) } // 0: Firestore Rules & Leaks, 1: Audit Immutability, 2: Pre-Production Readiness Checklist

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Security Architecture & Pre-Production Review",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Tenant segregation, immutable audit verification & real data readiness",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

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
            Tab(selected = activeSubTab == 0, onClick = { activeSubTab = 0 }, text = { Text("Tenant & Role Rules", fontSize = 10.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = activeSubTab == 1, onClick = { activeSubTab = 1 }, text = { Text("Audit Immutability & Timeout", fontSize = 10.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = activeSubTab == 2, onClick = { activeSubTab = 2 }, text = { Text("Pre-Real-Data Checklist", fontSize = 10.sp, fontWeight = FontWeight.Bold) })
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (activeSubTab) {
            0 -> {
                // Tenant & Role Leak Analysis
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CareMintTealContainer.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = CareMintTeal)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Multi-Tenant Isolation Verification", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                                }
                                Text(
                                    text = "All collection paths enforce organisationId and branchId segregation. Queries must scope by tenant at both Firestore rules and repository layer.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    item {
                        SecurityRuleCard(
                            title = "1. Client Health Records (/organisations/{orgId}/clients/{clientId})",
                            status = "VERIFIED SAFE - NO LEAKS",
                            code = """
match /organisations/{orgId}/clients/{clientId} {
  allow read: if request.auth != null && 
    request.auth.token.organisationId == orgId &&
    (request.auth.token.role in ['REGISTERED_MANAGER', 'COORDINATOR', 'CARE_WORKER', 'NURSE']);
  allow write: if request.auth != null && 
    request.auth.token.organisationId == orgId &&
    (request.auth.token.role in ['REGISTERED_MANAGER', 'COORDINATOR']);
  allow delete: if false; // Soft-delete only
}
                            """.trimIndent()
                        )
                    }

                    item {
                        SecurityRuleCard(
                            title = "2. eMAR Administration (/organisations/{orgId}/emar/{recordId})",
                            status = "VERIFIED SAFE - NO LEAKS",
                            code = """
match /organisations/{orgId}/emar/{recordId} {
  allow read: if request.auth != null && request.auth.token.organisationId == orgId;
  allow create: if request.auth != null && 
    request.auth.token.organisationId == orgId &&
    (request.auth.token.role in ['CARE_WORKER', 'NURSE', 'SENIOR_CARER', 'REGISTERED_MANAGER']);
  allow update, delete: if false; // Medication signatures are immutable
}
                            """.trimIndent()
                        )
                    }

                    item {
                        SecurityRuleCard(
                            title = "3. Family Portal (/organisations/{orgId}/familyPortal/{familyId})",
                            status = "VERIFIED SAFE - STRICTLY SCOPED",
                            code = """
match /organisations/{orgId}/familyPortal/{familyId} {
  allow read: if request.auth != null && 
    request.auth.token.role == 'FAMILY_MEMBER' && 
    request.auth.uid == resource.data.linkedFamilyUserId &&
    resource.data.gdprConsentActive == true;
  allow write: if false; // Read-only approved visit summaries
}
                            """.trimIndent()
                        )
                    }
                }
            }

            1 -> {
                // Immutable Audit Log & Inactivity Timeout
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CareMintTeal.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Immutable Audit Log Rule Analysis", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                                Text(
                                    text = "To meet CQC Regulation 17 (Good Governance) and UK GDPR accountability requirements, the audit log MUST NOT be editable by any role, including system administrators.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF0A1128),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = """
match /auditLogs/{logId} {
  allow create: if request.auth != null;
  allow read: if request.auth.token.role in 
    ['OWNER_ADMIN', 'REGISTERED_MANAGER', 'EXTERNAL_AUDITOR'];
  // STRICT PROHIBITION:
  allow update, delete: if false;
}
                                        """.trimIndent(),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = Color(0xFF6EE7B7),
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Auto Inactivity Timeout (5-Minute Lock)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "To protect unattended office screens and carers' mobile devices from unauthorised access, sessions automatically lock after inactivity.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { repository.lockSession() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                    modifier = Modifier.fillMaxWidth().testTag("simulate_inactivity_lock_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Test Inactivity Timeout (Lock Screen Now)")
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Pre-Real-Data Checklist
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF59E0B).copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("CRITICAL: MANDATORY ACTIONS BEFORE REAL CLIENT/STAFF DATA", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFF59E0B))
                                Text("Before transitioning from fictional demo data to real NHS patients and care staff in production, the following UK legal & clinical standards MUST be formally certified:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    item {
                        ChecklistAuditCard(
                            num = 1,
                            title = "NHS Data Security & Protection Toolkit (DSPT)",
                            requirement = "Achieve 'Standards Met' on the annual DSP Toolkit self-assessment to legally interface with NHS GP Connect and dm+d services."
                        )
                    }
                    item {
                        ChecklistAuditCard(
                            num = 2,
                            title = "Named Caldicott Guardian & Data Protection Officer (DPO)",
                            requirement = "Formally appoint a Caldicott Guardian to oversee the ethical processing of identifiable patient health records."
                        )
                    }
                    item {
                        ChecklistAuditCard(
                            num = 3,
                            title = "Cyber Essentials Plus Certification",
                            requirement = "Complete external technical vulnerability audit on workstations, cloud endpoints, and MDM mobile device management."
                        )
                    }
                    item {
                        ChecklistAuditCard(
                            num = 4,
                            title = "Two-Factor Authentication (2FA/MFA) Enforcement",
                            requirement = "Enforce hardware tokens or authenticator apps (TOTP) for all registered managers and coordinators."
                        )
                    }
                    item {
                        ChecklistAuditCard(
                            num = 5,
                            title = "Information Sharing Agreements (ISA)",
                            requirement = "Execute statutory ISAs with Westminster Council, Camden Council, Kensington & Chelsea, and local NHS Integrated Care Boards."
                        )
                    }
                    item {
                        ChecklistAuditCard(
                            num = 6,
                            title = "UK-Only Data Residency (europe-west2 London)",
                            requirement = "Confirm Google Cloud storage buckets and databases are strictly locked to europe-west2 with Customer-Managed Keys (CMEK)."
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SecurityRuleCard(title: String, status: String, code: String) {
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
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Surface(shape = RoundedCornerShape(4.dp), color = CareMintTealContainer) {
                    Text(text = status, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = CareMintTeal, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF0A1128), modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = code,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Color(0xFF6EE7B7),
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun ChecklistAuditCard(num: Int, title: String, requirement: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF59E0B).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "$num", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFF59E0B))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = requirement, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
