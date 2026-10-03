package com.example.ui.clients

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.model.*
import com.example.ui.common.AiChip
import com.example.ui.common.NeutralPill
import com.example.ui.common.RiskBadge
import com.example.ui.common.StatusPill
import com.example.ui.theme.*

@Composable
fun ClientsScreen(
    repository: CareRepository,
    selectedClientId: String? = null,
    onSelectClient: (String?) -> Unit = {}
) {
    val clients by repository.clients.collectAsState()
    val carePlans by repository.carePlans.collectAsState()
    val bodyMapMarks by repository.bodyMapMarks.collectAsState()
    val incidents by repository.incidents.collectAsState()
    val medications by repository.medications.collectAsState()
    val visits by repository.visits.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showNewClientModal by remember { mutableStateOf(false) }

    // Form state for New Client Modal
    var newFullName by remember { mutableStateOf("") }
    var newDob by remember { mutableStateOf("") }
    var newGender by remember { mutableStateOf("Female") }
    var newCareType by remember { mutableStateOf(CareType.DOMICILIARY) }
    var newNhsNumber by remember { mutableStateOf("") }
    var newAddress by remember { mutableStateOf("") }
    var newPhone by remember { mutableStateOf("") }
    var newEmergencyContact by remember { mutableStateOf("") }
    var newClinicalHistory by remember { mutableStateOf("") }

    val activeClient = clients.find { it.id == selectedClientId }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(BasePageBg)) {
        val isDesktop = maxWidth >= 1024.dp
        val contentPadding = if (isDesktop) 32.dp else 16.dp

        if (activeClient != null) {
            // CLIENT DETAIL VIEW
            BackHandler { onSelectClient(null) }

            val clientPlans = carePlans.filter { it.clientId == activeClient.id }
            val clientIncidents = incidents.filter { it.clientId == activeClient.id }
            val clientMeds = medications.filter { it.clientId == activeClient.id }
            val clientMarks = bodyMapMarks.filter { it.clientId == activeClient.id }
            val clientVisits = visits.filter { it.clientId == activeClient.id }

            // Segmented tab selection: 0: Care Plans, 1: Assessments, 2: Incidents, 3: Medications, 4: Body Map, 5: Shift Notes
            var detailSegmentTab by remember { mutableStateOf(0) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .verticalScroll(rememberScrollState())
                    .testTag("client_detail_screen"),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // "← Back to clients" Link
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onSelectClient(null) }
                        .testTag("back_to_clients_link")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = BaseTextMuted, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Back to clients", fontSize = 14.sp, color = BaseTextMuted, fontWeight = FontWeight.Medium)
                }

                // Header Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = BaseSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BaseBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            // 64px gradient avatar (emerald to teal with bold white initials)
                            val initials = activeClient.fullName.split(" ")
                                .filter { it.isNotBlank() }
                                .take(2)
                                .map { it.first().uppercase() }
                                .joinToString("")

                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(BrandGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = initials.ifBlank { "AC" },
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = activeClient.fullName,
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BaseTextHeading
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Pills row: Care type neutral, Risk status, NHS
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    NeutralPill(text = activeClient.careType.displayName)
                                    RiskBadge(risk = activeClient.riskLevel)
                                    Text(
                                        text = "NHS: ${activeClient.nhsNumber}",
                                        fontSize = 12.sp,
                                        color = BaseTextFaint
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Two-column rows with MapPin address and Phone number
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Place, contentDescription = null, tint = BaseTextMuted, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "${activeClient.address}, ${activeClient.postcode}", fontSize = 14.sp, color = BaseTextMuted)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = BaseTextMuted, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = activeClient.phone, fontSize = 14.sp, color = BaseTextMuted)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Emergency contact row
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Emergency: ", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                            Text(
                                text = "${activeClient.nextOfKinName} (${activeClient.nextOfKinRelationship}) - ${activeClient.nextOfKinPhone}",
                                fontSize = 14.sp,
                                color = BaseTextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = BaseBorder, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Section Label: "CLINICAL HISTORY"
                        Text(
                            text = "CLINICAL HISTORY",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            color = BaseTextMuted
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = activeClient.notesSummary.ifBlank { "Primary diagnosis of mild cognitive impairment and cardiovascular hypertension. Requires daily assistance with meals, hydration monitoring, and mobility prompt." },
                            fontSize = 14.sp,
                            color = BaseTextBody
                        )
                    }
                }

                // Segmented Tabs: Care Plans, Assessments, Incidents, Medications, Body Map, Shift Notes
                // Recipe: rounded-xl, bg slate-800/60, padding 4px. ACTIVE: near-black (#0A0A0A) pill with white text.
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B).copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val tabs = listOf(
                            Triple(0, "Care Plans", Icons.Default.Description),
                            Triple(1, "Assessments", Icons.Default.Assessment),
                            Triple(2, "Incidents", Icons.Default.Warning),
                            Triple(3, "Medications", Icons.Default.Medication),
                            Triple(4, "Body Map", Icons.Default.Accessibility),
                            Triple(5, "Shift Notes", Icons.Default.EventNote)
                        )

                        tabs.forEach { (index, title, icon) ->
                            val isSelected = detailSegmentTab == index
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = if (isSelected) Color(0xFF0A0A0A) else Color.Transparent,
                                modifier = Modifier
                                    .clickable { detailSegmentTab = index }
                                    .testTag("tab_$title")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else BaseTextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = title,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else BaseTextMuted
                                    )
                                }
                            }
                        }
                    }
                }

                // Tab Content
                when (detailSegmentTab) {
                    0 -> {
                        // CARE PLANS TAB
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Care Plans",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BaseTextHeading
                            )

                            // Action buttons: "Generate AI Care Plan" (AI gradient) & "Manual plan" (secondary)
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = { repository.generateAiCarePlanDraft(activeClient.id) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                    modifier = Modifier
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(AiGradient)
                                        .testTag("generate_ai_care_plan_button")
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Generate AI Care Plan", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                }

                                OutlinedButton(
                                    onClick = { repository.generateAiCarePlanDraft(activeClient.id) },
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BaseBorderInput),
                                    modifier = Modifier.height(44.dp).testTag("manual_plan_button")
                                ) {
                                    Text("Manual plan", fontSize = 14.sp, color = Color.White)
                                }
                            }
                        }

                        // Plan Cards (Title with AI chip, "Draft · V1" chip at right, body text formatted as clean bullets)
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            clientPlans.forEach { plan ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = BaseSurface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BaseBorder),
                                    modifier = Modifier.fillMaxWidth().testTag("plan_card_${plan.versionNumber}")
                                ) {
                                    Column(modifier = Modifier.padding(20.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = plan.title,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = BaseTextHeading
                                                )
                                                if (plan.isAiDraft) {
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    AiChip(text = "AI Draft")
                                                }
                                            }

                                            // "Draft · V1" or "Approved · V1" chip
                                            NeutralPill(text = "${plan.status.name.lowercase().capitalize()} · V${plan.versionNumber}")
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(text = plan.summary, fontSize = 14.sp, color = BaseTextBody)

                                        Spacer(modifier = Modifier.height(14.dp))
                                        Divider(color = BaseBorder, thickness = 1.dp)
                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Rendered as real bulleted list per intentional fixes!
                                        Text("Key Care Directives:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = BaseTextMuted)
                                        Spacer(modifier = Modifier.height(6.dp))

                                        BulletedTextItem("Nutrition & Hydration: ${plan.nutritionHydrationPlan}")
                                        BulletedTextItem("Mobility & Moving: ${plan.mobilityMovingHandlingPlan}")
                                        BulletedTextItem("Medication Directives: ${plan.medicationSupportPlan}")
                                        BulletedTextItem("Emergency Contingency: ${plan.emergencyContingency}")
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // ASSESSMENTS TAB
                        Text("Clinical Assessments", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = BaseSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BaseBorder),
                            modifier = Modifier.fillMaxWidth().padding(20.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("NEWS2 Clinical Score: 0 (Normal / Stable)", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseEmerald300)
                                Text("Waterlow Pressure Ulcer Assessment: 8 (Low Risk)", fontSize = 14.sp, color = BaseTextBody)
                            }
                        }
                    }

                    2 -> {
                        // INCIDENTS TAB
                        Text("Logged Incidents (${clientIncidents.size})", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                        if (clientIncidents.isEmpty()) {
                            Text("No assessments recorded.", fontSize = 14.sp, color = BaseTextFaint, modifier = Modifier.padding(16.dp))
                        } else {
                            clientIncidents.forEach { inc ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = BaseSurface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BaseBorder),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(inc.category.displayName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                                            StatusPill(text = inc.severity.displayName, statusColor = Color(inc.severity.colorHex))
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(inc.description, fontSize = 13.sp, color = BaseTextBody)
                                    }
                                }
                            }
                        }
                    }

                    3 -> {
                        // MEDICATIONS TAB
                        Text("Active eMAR Medication Regimen (${clientMeds.size})", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                        clientMeds.forEach { med ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = BaseSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BaseBorder),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(med.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                                        Text("${med.dose} • ${med.scheduledTimes.joinToString(", ")} • ${med.route}", fontSize = 12.sp, color = BaseTextMuted)
                                    }
                                    NeutralPill(text = "Stock: ${med.currentStock}")
                                }
                            }
                        }
                    }

                    4 -> {
                        // BODY MAP TAB
                        Text("Interactive Body Map & Skin Integrity", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                        BodyMapViewer(
                            clientId = activeClient.id,
                            marks = clientMarks,
                            onAddMark = { repository.addBodyMapMark(it) }
                        )
                    }

                    5 -> {
                        // SHIFT NOTES TAB
                        Text("Recent Shift Notes & Handover Entries", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                        clientVisits.forEach { v ->
                            if (!v.dailyNotes.isNullOrBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = BaseSurface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BaseBorder),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("${v.startTime} - ${v.visitType} (${v.date})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = BaseEmerald300)
                                        Text(v.dailyNotes, fontSize = 13.sp, color = BaseTextBody)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // CLIENTS LIST VIEW
            val filteredClients = clients.filter {
                it.fullName.contains(searchQuery, ignoreCase = true) ||
                it.address.contains(searchQuery, ignoreCase = true)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .testTag("clients_list_screen"),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // H1 "Clients" with subtitle and "+ New Client" Button
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "Clients",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = BaseTextHeading
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Manage client profiles, care plans and clinical records.",
                                fontSize = 14.sp,
                                color = BaseTextMuted
                            )
                        }

                        // Primary "+ New Client" button (emerald-500, white text, rounded-xl, 44px height, px 20px)
                        Button(
                            onClick = { showNewClientModal = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                            modifier = Modifier
                                .height(44.dp)
                                .testTag("add_new_client_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("New Client", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                    }
                }

                // Search Input (max-w-md 448px, height 44px, rounded-lg, bg #1B2335, 1px slate-700 border)
                item {
                    Box(modifier = Modifier.widthIn(max = 448.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search clients by name...", color = BaseTextFaint, fontSize = 14.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BaseTextMuted, modifier = Modifier.size(18.dp)) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = BaseModalInput,
                                unfocusedContainerColor = BaseModalInput,
                                focusedBorderColor = BaseEmerald500,
                                unfocusedBorderColor = BaseBorderInput,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("clients_search_input")
                        )
                    }
                }

                // 3-Column Grid of Client Cards (1 column on mobile)
                item {
                    if (isDesktop) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            filteredClients.chunked(3).forEach { rowClients ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    rowClients.forEach { client ->
                                        ClientCardItem(
                                            client = client,
                                            modifier = Modifier.weight(1f),
                                            onClick = { onSelectClient(client.id) }
                                        )
                                    }
                                    // Filler if row has less than 3
                                    repeat(3 - rowClients.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            filteredClients.forEach { client ->
                                ClientCardItem(
                                    client = client,
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = { onSelectClient(client.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // "New Client Profile" Modal
    // Recipe: max-w-lg (512px), bg #0F172A, rounded-2xl (16dp), border 1px #171C2E, padding 24px;
    // overlay black/70; title 20px semibold; X close button; 2-column field grid; Cancel / Create client.
    if (showNewClientModal) {
        AlertDialog(
            onDismissRequest = { showNewClientModal = false },
            containerColor = BaseModalBg,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.widthIn(max = 512.dp).border(1.dp, BaseBorder, RoundedCornerShape(16.dp)),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "New Client Profile", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)
                    IconButton(onClick = { showNewClientModal = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = BaseTextMuted)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Full name
                    ModalInputField(label = "Full Name", value = newFullName, onValueChange = { newFullName = it })

                    // Date of birth + Gender (2 columns)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ModalInputField(label = "Date of Birth (dd/mm/yyyy)", value = newDob, onValueChange = { newDob = it }, modifier = Modifier.weight(1f))
                        ModalInputField(label = "Gender", value = newGender, onValueChange = { newGender = it }, modifier = Modifier.weight(1f))
                    }

                    // Care type + NHS number (2 columns)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ModalInputField(label = "Care Type", value = newCareType.displayName, onValueChange = {}, modifier = Modifier.weight(1f))
                        ModalInputField(label = "NHS Number", value = newNhsNumber, onValueChange = { newNhsNumber = it }, modifier = Modifier.weight(1f))
                    }

                    // Address
                    ModalInputField(label = "Address & Postcode", value = newAddress, onValueChange = { newAddress = it })

                    // Phone + Emergency Contact (2 columns)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ModalInputField(label = "Phone", value = newPhone, onValueChange = { newPhone = it }, modifier = Modifier.weight(1f))
                        ModalInputField(label = "Emergency Contact", value = newEmergencyContact, onValueChange = { newEmergencyContact = it }, modifier = Modifier.weight(1f))
                    }

                    // Clinical history (textarea)
                    Column {
                        Text("Clinical History", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = newClinicalHistory,
                            onValueChange = { newClinicalHistory = it },
                            placeholder = { Text("Relevant medical history, diagnoses, allergies...", color = BaseTextFaint, fontSize = 13.sp) },
                            minLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = BaseModalInput,
                                unfocusedContainerColor = BaseModalInput,
                                focusedBorderColor = BaseEmerald500,
                                unfocusedBorderColor = BaseBorderInput,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("modal_clinical_history_input")
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newFullName.isNotBlank()) {
                            val newClient = ClientProfile(
                                id = "client-" + java.util.UUID.randomUUID().toString().take(6),
                                organisationId = "org-allcarex-01",
                                branchId = "branch-wst",
                                fullName = newFullName,
                                preferredName = newFullName.split(" ").firstOrNull() ?: newFullName,
                                dateOfBirth = newDob.ifBlank { "01/01/1945" },
                                nhsNumber = newNhsNumber.ifBlank { "999 123 4567" },
                                address = newAddress.ifBlank { "10 Downing Street" },
                                postcode = "SW1A 2AA",
                                phone = newPhone.ifBlank { "020 7946 0000" },
                                keySafeCode = "1234",
                                gpSurgery = "Westminster Medical Surgery",
                                gpName = "Dr. M. Vance",
                                nextOfKinName = newEmergencyContact.ifBlank { "Next of Kin" },
                                nextOfKinRelationship = "Family",
                                nextOfKinPhone = "07700 900111",
                                fundingType = FundingType.PRIVATE_SELF_FUNDED,
                                careType = newCareType,
                                riskLevel = RiskLevel.MEDIUM,
                                allergies = emptyList(),
                                consentGiven = true,
                                dnacprRespectStatus = false,
                                mentalCapacityStatus = MentalCapacityStatus.CAPACITY_CONFIRMED,
                                notesSummary = newClinicalHistory
                            )
                            repository.createClient(newClient)
                            showNewClientModal = false
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                    modifier = Modifier.height(44.dp).testTag("modal_create_client_button")
                ) {
                    Text("Create client", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showNewClientModal = false },
                    modifier = Modifier.height(44.dp)
                ) {
                    Text("Cancel", fontSize = 14.sp, color = BaseTextMuted)
                }
            }
        )
    }
}

@Composable
private fun ClientCardItem(
    client: ClientProfile,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // Recipe: 48px brand-gradient initial tile, name 18px semibold white, care type in slate-400,
    // risk pill at top-right, MapPin + address (14px slate-400), Phone + number (14px slate-400).
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = BaseSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BaseBorder),
        modifier = modifier
            .clickable { onClick() }
            .testTag("client_card_${client.id}")
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    val initials = client.fullName.split(" ")
                        .filter { it.isNotBlank() }
                        .take(2)
                        .map { it.first().uppercase() }
                        .joinToString("")

                    // 48px brand-gradient initial tile
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BrandGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials.ifBlank { "AC" },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = client.fullName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BaseTextHeading
                        )
                        Text(
                            text = client.careType.displayName,
                            fontSize = 14.sp,
                            color = BaseTextMuted
                        )
                    }
                }

                // Risk pill at top-right
                RiskBadge(risk = client.riskLevel)
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = BaseBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // MapPin + address row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = null, tint = BaseTextMuted, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${client.address}, ${client.postcode}",
                    fontSize = 14.sp,
                    color = BaseTextMuted,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Phone + number row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, contentDescription = null, tint = BaseTextMuted, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = client.phone,
                    fontSize = 14.sp,
                    color = BaseTextMuted
                )
            }
        }
    }
}

@Composable
private fun BulletedTextItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text("• ", fontSize = 14.sp, color = BaseEmerald400, fontWeight = FontWeight.Bold)
        Text(text = text, fontSize = 14.sp, color = BaseTextBody)
    }
}

@Composable
private fun ModalInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextHeading)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BaseModalInput,
                unfocusedContainerColor = BaseModalInput,
                focusedBorderColor = BaseEmerald500,
                unfocusedBorderColor = BaseBorderInput,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        )
    }
}
