package com.example.ui.carer

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
import com.example.model.*
import com.example.ui.theme.CareMintTeal
import com.example.ui.theme.CareMintTealContainer

@Composable
fun CarerClientsView(
    repository: CareRepository,
    isHighContrast: Boolean,
    isLargeText: Boolean,
    onOpenEmar: (String) -> Unit,
    onOpenBodyMap: (String) -> Unit
) {
    val clients by repository.clients.collectAsState()
    val textScale = if (isLargeText) 1.2f else 1.0f

    var searchQuery by remember { mutableStateOf("") }
    var selectedClientForDetail by remember { mutableStateOf<ClientProfile?>(null) }

    // Filter to clients in current branch/rota
    val filteredClients = clients.filter {
        it.fullName.contains(searchQuery, ignoreCase = true) ||
        it.address.contains(searchQuery, ignoreCase = true) ||
        it.postcode.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("carer_clients_view")
    ) {
        // Search Input (min 48dp touch height)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search clients by name, address, postcode...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .testTag("carer_client_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "My Assigned Clients (${filteredClients.size}) • Essentials Cached Offline",
            fontSize = (12 * textScale).sp,
            fontWeight = FontWeight.Bold,
            color = if (isHighContrast) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filteredClients) { client ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        if (isHighContrast) 2.dp else 1.dp,
                        if (isHighContrast) Color.White else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedClientForDetail = client }
                        .testTag("carer_client_card_${client.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = client.fullName,
                                    fontSize = (16 * textScale).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${client.address}, ${client.postcode}",
                                    fontSize = (11 * textScale).sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (client.dnacprRespectStatus) {
                                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFEF4444)) {
                                    Text(
                                        text = "DNACPR",
                                        fontSize = (9 * textScale).sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Key-Safe & Phone Quick Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(shape = RoundedCornerShape(6.dp), color = CareMintTealContainer) {
                                Text(
                                    text = "🔑 Key Safe: ${client.keySafeCode}",
                                    fontSize = (11 * textScale).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CareMintTeal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Text(
                                text = "Emergency: ${client.nextOfKinName} (${client.nextOfKinPhone})",
                                fontSize = (10 * textScale).sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Allergies & Risk Tag
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(client.riskLevel.colorHex).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "Risk: ${client.riskLevel.displayName}",
                                    fontSize = (9 * textScale).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(client.riskLevel.colorHex),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            if (client.allergies.isNotEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFEF4444).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "Allergies: ${client.allergies.joinToString()}",
                                        fontSize = (9 * textScale).sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFEF4444),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action Buttons: View Details, Open eMAR, Body Map
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onOpenEmar(client.id) },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("client_open_emar_${client.id}"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("eMAR", fontSize = (11 * textScale).sp)
                            }

                            OutlinedButton(
                                onClick = { onOpenBodyMap(client.id) },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("client_open_bodymap_${client.id}"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Accessibility, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Body Map", fontSize = (11 * textScale).sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Client Full Essentials Modal
    selectedClientForDetail?.let { client ->
        AlertDialog(
            onDismissRequest = { selectedClientForDetail = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(client.fullName, fontWeight = FontWeight.Bold, fontSize = (17 * textScale).sp)
                    if (client.dnacprRespectStatus) {
                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFEF4444)) {
                            Text("DNACPR ACTIVE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.padding(4.dp))
                        }
                    }
                }
            },
            text = {
                Column {
                    Text("NHS Number: ${client.nhsNumber}", fontSize = (11 * textScale).sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("DOB: ${client.dateOfBirth}", fontSize = (11 * textScale).sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Address: ${client.address}, ${client.postcode}", fontSize = (12 * textScale).sp, fontWeight = FontWeight.SemiBold)
                    Text("Key Safe: ${client.keySafeCode}", fontSize = (12 * textScale).sp, fontWeight = FontWeight.ExtraBold, color = CareMintTeal)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("GP Practice: ${client.gpSurgery} (${client.gpName})", fontSize = (11 * textScale).sp)
                    Text("Next of Kin: ${client.nextOfKinName} (${client.nextOfKinRelationship}) - ${client.nextOfKinPhone}", fontSize = (11 * textScale).sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Clinical Notes Summary:", fontSize = (11 * textScale).sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                    Text(client.notesSummary, fontSize = (11 * textScale).sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cid = client.id
                        selectedClientForDetail = null
                        onOpenEmar(cid)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text("Open eMAR", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { selectedClientForDetail = null },
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text("Close")
                }
            }
        )
    }
}
