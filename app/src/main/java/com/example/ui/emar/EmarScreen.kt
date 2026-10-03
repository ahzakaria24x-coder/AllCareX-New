package com.example.ui.emar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.model.*
import com.example.ui.theme.CareMintTeal
import com.example.ui.theme.CareMintTealContainer

@Composable
fun EmarScreen(
    repository: CareRepository,
    initialClientId: String? = null
) {
    val clients by repository.clients.collectAsState()
    val medications by repository.medications.collectAsState()
    val marHistory by repository.marHistory.collectAsState()
    val currentUser by repository.currentUser.collectAsState()

    var selectedClientId by remember { mutableStateOf(initialClientId ?: clients.first().id) }
    var activeAdministerMed by remember { mutableStateOf<Medication?>(null) }
    var dmdSearchQuery by remember { mutableStateOf("") }
    var showDmdSuggestions by remember { mutableStateOf(false) }

    val activeClient = clients.find { it.id == selectedClientId } ?: clients.first()
    val clientMeds = medications.filter { it.clientId == activeClient.id }
    val lowStockMeds = clientMeds.filter { it.currentStock <= it.warningThresholdStock }

    val dmdDatabaseList = listOf(
        "Paracetamol 500mg tablets (dm+d 39591411000001108)",
        "Ramipril 5mg capsules (dm+d 10852011000001106)",
        "Donepezil hydrochloride 10mg tablets (dm+d 7731211000001104)",
        "Atorvastatin 40mg tablets (dm+d 11528611000001108)",
        "Morphine sulfate 10mg/5ml oral solution (dm+d 594811000001104) [CD]",
        "Co-careldopa 25mg/100mg tablets (dm+d 991203)",
        "Salbutamol 100micrograms/dose inhaler (dm+d 10978311000001104)",
        "Omeprazole 20mg gastro-resistant capsules (dm+d 10839911000001101)"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // eMAR Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "eMAR Medication Administration",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Controlled drug double-sign & real-time stock reconciliation",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Client Selector Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            clients.take(4).forEach { client ->
                FilterChip(
                    selected = selectedClientId == client.id,
                    onClick = { selectedClientId = client.id },
                    label = { Text(client.fullName.split(" ").first(), fontSize = 11.sp) },
                    modifier = Modifier.height(34.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // NHS dm+d Medicines Search Bar
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = dmdSearchQuery,
                onValueChange = {
                    dmdSearchQuery = it
                    showDmdSuggestions = it.isNotBlank()
                },
                placeholder = { Text("Search NHS dm+d dictionary (e.g. Paracetamol, Oramorph)...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dmd_search_input")
            )

            if (showDmdSuggestions && dmdSearchQuery.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    tonalElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 56.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "NHS DICTIONARY OF MEDICINES & DEVICES (dm+d)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = CareMintTeal
                        )
                        dmdDatabaseList.filter { it.contains(dmdSearchQuery, ignoreCase = true) }.forEach { item ->
                            Text(
                                text = item,
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        dmdSearchQuery = item.split("(").first().trim()
                                        showDmdSuggestions = false
                                    }
                                    .padding(vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Low Stock Banner Alert
        if (lowStockMeds.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFEF4444).copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "LOW MEDICATION STOCK ALERT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                        Text(
                            text = "${lowStockMeds.first().name} has only ${lowStockMeds.first().currentStock} remaining (threshold: ${lowStockMeds.first().warningThresholdStock}). Reorder requested from GP.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Client Allergy Reminder Banner
        if (activeClient.allergies.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFEF4444).copy(alpha = 0.1f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.HealthAndSafety, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "VERIFIED ALLERGIES: ${activeClient.allergies.joinToString(", ")}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF4444)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Medications List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(clientMeds) { med ->
                EmarMedCard(
                    medication = med,
                    onAdministerClick = { activeAdministerMed = med }
                )
            }
        }
    }

    // Modal: Administer Medication & Controlled Drug Double-Sign
    activeAdministerMed?.let { targetMed ->
        var selectedOutcome by remember { mutableStateOf(AdministrationOutcome.ADMINISTERED) }
        var reasonCode by remember { mutableStateOf("") }
        var witnessName by remember { mutableStateOf("Sarah Jenkins (Senior Carer)") }
        var witnessPin by remember { mutableStateOf("9421") }
        var notesText by remember { mutableStateOf("") }

        val reasonOptions = listOf(
            "Client Refused",
            "Client Sleeping",
            "Nausea / Vomiting",
            "Hospital Admission",
            "Dropped / Damaged",
            "Stock Depleted"
        )

        AlertDialog(
            onDismissRequest = { activeAdministerMed = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Administer ${targetMed.name}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Dose: ${targetMed.dose} • Route: ${targetMed.route}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CareMintTeal
                    )
                    Text(
                        text = "Instructions: ${targetMed.specialInstructions.ifBlank { targetMed.frequencyDescription }}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Controlled Drug Notice
                    if (targetMed.isControlledDrug) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFEF4444).copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "SCHEDULE 2 CONTROLLED DRUG (CD)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFEF4444)
                                )
                                Text(
                                    text = "Requires two competent carers to verify stock balance, measurement, and witness ingestion.",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Administration Outcome", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            AdministrationOutcome.ADMINISTERED,
                            AdministrationOutcome.REFUSED,
                            AdministrationOutcome.NOT_REQUIRED
                        ).forEach { outcome ->
                            FilterChip(
                                selected = selectedOutcome == outcome,
                                onClick = { selectedOutcome = outcome },
                                label = { Text(outcome.code + " - " + outcome.displayName.split(" ").first(), fontSize = 10.sp) },
                                modifier = Modifier.height(30.dp)
                            )
                        }
                    }

                    if (selectedOutcome != AdministrationOutcome.ADMINISTERED) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Reason for Non-Administration", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            reasonOptions.take(3).forEach { r ->
                                FilterChip(
                                    selected = reasonCode == r,
                                    onClick = { reasonCode = r },
                                    label = { Text(r, fontSize = 9.sp) },
                                    modifier = Modifier.height(28.dp)
                                )
                            }
                        }
                    }

                    // Witness Fields for Controlled Drugs
                    if (targetMed.isControlledDrug) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Second Witness Verification", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                        OutlinedTextField(
                            value = witnessName,
                            onValueChange = { witnessName = it },
                            label = { Text("Witness Senior / Nurse Name") },
                            modifier = Modifier.fillMaxWidth().testTag("witness_name_input")
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = witnessPin,
                            onValueChange = { witnessPin = it },
                            label = { Text("Witness Security PIN") },
                            modifier = Modifier.fillMaxWidth().testTag("witness_pin_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        label = { Text("Clinical Administration Notes") },
                        placeholder = { Text("e.g. Ingested with full glass of water, no swallowing difficulty.") },
                        modifier = Modifier.fillMaxWidth().testTag("emar_notes_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.recordMedicationAdministration(
                            medicationId = targetMed.id,
                            clientId = targetMed.clientId,
                            outcome = selectedOutcome,
                            doseGiven = targetMed.dose,
                            carerName = currentUser.name,
                            reasonCode = if (selectedOutcome != AdministrationOutcome.ADMINISTERED) reasonCode else null,
                            witnessCarerName = if (targetMed.isControlledDrug) witnessName else null,
                            notes = notesText
                        )
                        activeAdministerMed = null
                    },
                    modifier = Modifier.testTag("confirm_administer_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal)
                ) {
                    Text("Confirm eMAR Sign-off", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { activeAdministerMed = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun EmarMedCard(
    medication: Medication,
    onAdministerClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (medication.isControlledDrug) Color(0xFFEF4444).copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        ),
        tonalElevation = 2.dp,
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
                        Text(
                            text = medication.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (medication.isControlledDrug) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFEF4444).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "CD SCHEDULE 2",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFEF4444),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "${medication.dose} • ${medication.route} • ${medication.frequencyDescription}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Prescribed by: ${medication.prescriber}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (medication.currentStock <= medication.warningThresholdStock) Color(0xFFEF4444).copy(alpha = 0.2f) else CareMintTealContainer
                ) {
                    Text(
                        text = "Stock: ${medication.currentStock}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (medication.currentStock <= medication.warningThresholdStock) Color(0xFFEF4444) else CareMintTeal,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (medication.isPrn && !medication.prnProtocol.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "PRN PROTOCOL: ${medication.prnProtocol}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Scheduled: ${medication.scheduledTimes.joinToString(", ")}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CareMintTeal
                )

                Button(
                    onClick = onAdministerClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                    modifier = Modifier.testTag("administer_med_${medication.id}").height(34.dp)
                ) {
                    Text("Administer & Sign", color = Color(0xFF0A1128), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
