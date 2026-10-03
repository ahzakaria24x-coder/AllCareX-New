package com.example.ui.carer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.model.Visit
import com.example.ui.theme.CareMintTeal
import com.example.ui.theme.CareMintTealContainer

@Composable
fun CarerMobileScreen(
    repository: CareRepository,
    onNavigateToEmar: (String) -> Unit = {},
    onNavigateToIncidents: () -> Unit = {}
) {
    // Bottom Tab Bar selection: 0: Today, 1: Clients, 2: Messages, 3: Me
    var activeBottomTab by remember { mutableStateOf(0) }

    // Accessibility & Shift Preferences
    var isHighContrast by remember { mutableStateOf(false) }
    var isLargeText by remember { mutableStateOf(false) }
    var isNightTheme by remember { mutableStateOf(false) }

    // Dialog & Flow States
    var activeVisitInFlow by remember { mutableStateOf<Visit?>(null) }
    var showHandoverDialog by remember { mutableStateOf(false) }
    var showNotifyManagerDialog by remember { mutableStateOf(false) }
    var showIncidentDialogForVisit by remember { mutableStateOf<Visit?>(null) }
    var showBodyMapForClientId by remember { mutableStateOf<String?>(null) }

    val safetyState by repository.loneWorkerSafetyState.collectAsState()
    val isOfflineSimulated by repository.isOfflineSimulated.collectAsState()
    val offlineQueue by repository.offlineQueue.collectAsState()

    // Container theme colors for Night Mode / High Contrast
    val backgroundColor = when {
        isHighContrast -> Color(0xFF000000)
        isNightTheme -> Color(0xFF080D1A)
        else -> MaterialTheme.colorScheme.background
    }

    val surfaceColor = when {
        isHighContrast -> Color(0xFF111111)
        isNightTheme -> Color(0xFF0F172A)
        else -> MaterialTheme.colorScheme.surface
    }

    val primaryTextColor = if (isHighContrast) Color.White else MaterialTheme.colorScheme.onSurface
    val textScale = if (isLargeText) 1.2f else 1.0f

    Surface(
        color = backgroundColor,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header: Top Utility Bar (Lone Worker Status, Signal, Accessibility Toggles)
            Surface(
                color = surfaceColor,
                tonalElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("carer_mobile_header")
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                    // Top Status Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Signal & Offline Status
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                repository.toggleOfflineSimulation(!isOfflineSimulated)
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (isOfflineSimulated) Color(0xFFF59E0B) else Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isOfflineSimulated) "OFFLINE (Simulated)" else "ONLINE • 4G",
                                fontSize = (10 * textScale).sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOfflineSimulated) Color(0xFFF59E0B) else CareMintTeal
                            )
                        }

                        // Accessibility Switches Row: High Contrast, Large Text, Night Mode (min 48dp touch target)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Night Theme Toggle
                            IconButton(
                                onClick = { isNightTheme = !isNightTheme },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isNightTheme) CareMintTealContainer else Color.Transparent)
                                    .testTag("toggle_night_mode")
                            ) {
                                Icon(
                                    imageVector = if (isNightTheme) Icons.Default.Nightlight else Icons.Default.WbSunny,
                                    contentDescription = "Night Shift Mode",
                                    tint = if (isNightTheme) CareMintTeal else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Large Text Toggle
                            IconButton(
                                onClick = { isLargeText = !isLargeText },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isLargeText) CareMintTealContainer else Color.Transparent)
                                    .testTag("toggle_large_text")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatSize,
                                    contentDescription = "Large Text Mode",
                                    tint = if (isLargeText) CareMintTeal else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // High Contrast Toggle
                            IconButton(
                                onClick = { isHighContrast = !isHighContrast },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isHighContrast) CareMintTealContainer else Color.Transparent)
                                    .testTag("toggle_high_contrast")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Contrast,
                                    contentDescription = "High Contrast Mode",
                                    tint = if (isHighContrast) CareMintTeal else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Lone-Worker Safety Banner with Countdown Timer & Check-In
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (safetyState.isSosTriggered) Color(0xFFEF4444).copy(alpha = 0.2f) else CareMintTealContainer.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (safetyState.isSosTriggered) Color(0xFFEF4444) else CareMintTeal.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("lone_worker_safety_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (safetyState.isSosTriggered) Icons.Default.Warning else Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = if (safetyState.isSosTriggered) Color(0xFFEF4444) else CareMintTeal,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = if (safetyState.isSosTriggered) "EMERGENCY PANIC BROADCAST ACTIVE" else "Lone Worker Timer: 42m remaining",
                                        fontSize = (11 * textScale).sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (safetyState.isSosTriggered) Color(0xFFEF4444) else CareMintTeal
                                    )
                                    Text(
                                        text = "On-call manager: ${safetyState.escalationContactName}",
                                        fontSize = (9 * textScale).sp,
                                        color = primaryTextColor
                                    )
                                }
                            }

                            if (safetyState.isSosTriggered) {
                                TextButton(
                                    onClick = { repository.cancelLoneWorkerPanic() },
                                    modifier = Modifier.heightIn(min = 40.dp)
                                ) {
                                    Text("Stand Down", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = (10 * textScale).sp)
                                }
                            } else {
                                Button(
                                    onClick = { repository.resetLoneWorkerTimer() },
                                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .heightIn(min = 40.dp)
                                        .testTag("reset_safety_timer_button")
                                ) {
                                    Text("Check In", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold, fontSize = (10 * textScale).sp)
                                }
                            }
                        }
                    }
                }
            }

            // Main Body Content Container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                when (activeBottomTab) {
                    0 -> CarerTodayTimelineView(
                        repository = repository,
                        isHighContrast = isHighContrast,
                        isLargeText = isLargeText,
                        onStartVisit = { visit -> activeVisitInFlow = visit },
                        onOpenVisitFlow = { visit -> activeVisitInFlow = visit },
                        onOpenHandover = { showHandoverDialog = true },
                        onOpenIncident = { visit -> showIncidentDialogForVisit = visit },
                        onOpenBodyMap = { clientId -> showBodyMapForClientId = clientId }
                    )
                    1 -> CarerClientsView(
                        repository = repository,
                        isHighContrast = isHighContrast,
                        isLargeText = isLargeText,
                        onOpenEmar = onNavigateToEmar,
                        onOpenBodyMap = { clientId -> showBodyMapForClientId = clientId }
                    )
                    2 -> CarerMessagesView(
                        repository = repository,
                        isHighContrast = isHighContrast,
                        isLargeText = isLargeText,
                        onOpenNotifyManager = { showNotifyManagerDialog = true }
                    )
                    3 -> CarerStaffToolsView(
                        repository = repository,
                        isHighContrast = isHighContrast,
                        isLargeText = isLargeText,
                        onOpenNotifyManager = { showNotifyManagerDialog = true }
                    )
                }
            }

            // Bottom Tab Bar: Today, Clients, Messages, Me (Min 48px Touch Targets)
            NavigationBar(
                containerColor = surfaceColor,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .testTag("carer_bottom_tab_bar")
            ) {
                NavigationBarItem(
                    selected = activeBottomTab == 0,
                    onClick = { activeBottomTab = 0 },
                    icon = { Icon(Icons.Default.Today, contentDescription = "Today") },
                    label = { Text("Today", fontSize = (10 * textScale).sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0A1128),
                        selectedTextColor = CareMintTeal,
                        indicatorColor = CareMintTeal
                    ),
                    modifier = Modifier.testTag("tab_today")
                )

                NavigationBarItem(
                    selected = activeBottomTab == 1,
                    onClick = { activeBottomTab = 1 },
                    icon = { Icon(Icons.Default.Group, contentDescription = "Clients") },
                    label = { Text("Clients", fontSize = (10 * textScale).sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0A1128),
                        selectedTextColor = CareMintTeal,
                        indicatorColor = CareMintTeal
                    ),
                    modifier = Modifier.testTag("tab_clients")
                )

                NavigationBarItem(
                    selected = activeBottomTab == 2,
                    onClick = { activeBottomTab = 2 },
                    icon = { Icon(Icons.Default.Email, contentDescription = "Messages") },
                    label = { Text("Messages", fontSize = (10 * textScale).sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0A1128),
                        selectedTextColor = CareMintTeal,
                        indicatorColor = CareMintTeal
                    ),
                    modifier = Modifier.testTag("tab_messages")
                )

                NavigationBarItem(
                    selected = activeBottomTab == 3,
                    onClick = { activeBottomTab = 3 },
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Me") },
                    label = { Text("Me", fontSize = (10 * textScale).sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0A1128),
                        selectedTextColor = CareMintTeal,
                        indicatorColor = CareMintTeal
                    ),
                    modifier = Modifier.testTag("tab_me")
                )
            }
        }
    }

    // Modal: 6-Stage Visit Flow
    activeVisitInFlow?.let { visit ->
        CarerVisitFlowDialog(
            visit = visit,
            repository = repository,
            isHighContrast = isHighContrast,
            isLargeText = isLargeText,
            onDismiss = { activeVisitInFlow = null },
            onOpenEmar = { clientId ->
                activeVisitInFlow = null
                onNavigateToEmar(clientId)
            },
            onOpenIncident = { v ->
                showIncidentDialogForVisit = v
            },
            onOpenBodyMap = { cid ->
                showBodyMapForClientId = cid
            }
        )
    }

    // Modal: End-of-Shift Handover Summary
    if (showHandoverDialog) {
        CarerHandoverDialog(
            repository = repository,
            onDismiss = { showHandoverDialog = false }
        )
    }

    // Modal: Notify My Manager
    if (showNotifyManagerDialog) {
        CarerNotifyManagerDialog(
            repository = repository,
            onDismiss = { showNotifyManagerDialog = false }
        )
    }

    // Modal: Quick Incident Report
    if (showIncidentDialogForVisit != null) {
        CarerQuickIncidentDialog(
            visit = showIncidentDialogForVisit,
            repository = repository,
            onDismiss = { showIncidentDialogForVisit = null }
        )
    }

    // Modal: Body Map Viewer
    showBodyMapForClientId?.let { cid ->
        CarerBodyMapDialog(
            clientId = cid,
            repository = repository,
            onDismiss = { showBodyMapForClientId = null }
        )
    }
}
