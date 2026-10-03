package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CareRepository
import com.example.model.UserRole
import com.example.ui.analytics.AnalyticsScreen
import com.example.ui.briefing.DailyBriefingScreen
import com.example.ui.carer.CarerMobileScreen
import com.example.ui.clients.ClientsScreen
import com.example.ui.compliance.ComplianceGovernanceScreen
import com.example.ui.cqc.CqcInspectionsScreen
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.emar.EmarMedicationsScreen
import com.example.ui.family.FamilyInboxScreen
import com.example.ui.incidents.IncidentsListScreen
import com.example.ui.manager.NotifyLineManagerScreen
import com.example.ui.payroll.PayrollFinanceScreen
import com.example.ui.reports.ComplianceReportingScreen
import com.example.ui.rota.RotaVisitsScreen
import com.example.ui.theme.*
import com.example.ui.training.StaffTrainingScreen
import com.example.ui.workforce.WorkforceStaffScreen

class MainActivity : ComponentActivity() {

    private val repository = CareRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainCareApp(repository = repository)
            }
        }
    }
}

enum class NavigationScreen(val title: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    CLIENTS("Clients", Icons.Default.Group),
    ROTA("Rota & Visits", Icons.Default.CalendarToday),
    EMAR("eMAR", Icons.Default.Medication),
    INCIDENTS("Incidents", Icons.Default.Warning),
    FAMILY_INBOX("Family Inbox", Icons.Default.ChatBubble),
    WORKFORCE("Workforce", Icons.Default.Badge),
    TRAINING("Training", Icons.Default.School),
    PAYROLL("Payroll", Icons.Default.AccountBalanceWallet),
    COMPLIANCE("Compliance", Icons.Default.VerifiedUser),
    CQC_INSPECTIONS("CQC Inspections", Icons.Default.FactCheck),
    REPORTS("Reports", Icons.Default.Assessment),
    ANALYTICS("Analytics", Icons.Default.BarChart),
    NOTIFY_MANAGER("Notify Manager", Icons.Default.Campaign),
    DAILY_BRIEFING("Daily Briefing", Icons.Default.WbSunny)
}

@Composable
fun MainCareApp(repository: CareRepository) {
    val currentUser by repository.currentUser.collectAsState()
    val isCarerMode by repository.isCarerMobileMode.collectAsState()
    val isSessionLocked by repository.isSessionLocked.collectAsState()

    var currentScreen by remember { mutableStateOf(NavigationScreen.DASHBOARD) }
    var selectedClientIdForDetail by remember { mutableStateOf<String?>(null) }
    var isMobileDrawerOpen by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showSignOutDialog by remember { mutableStateOf(false) }

    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(BasePageBg)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        val isDesktop = maxWidth >= 1024.dp

        if (isCarerMode) {
            // Field Carer Mode
            CarerMobileScreen(
                repository = repository,
                onNavigateToEmar = { clientId ->
                    selectedClientIdForDetail = clientId
                    currentScreen = NavigationScreen.EMAR
                    repository.toggleCarerMobileMode(false)
                },
                onNavigateToIncidents = {
                    currentScreen = NavigationScreen.INCIDENTS
                    repository.toggleCarerMobileMode(false)
                }
            )
        } else {
            // Base44 Exact Application Shell
            Row(modifier = Modifier.fillMaxSize()) {
                // DESKTOP FIXED SIDEBAR (260px wide)
                if (isDesktop) {
                    DesktopSidebar(
                        currentScreen = currentScreen,
                        onSelectScreen = { screen ->
                            selectedClientIdForDetail = null
                            currentScreen = screen
                        },
                        onSignOut = { showSignOutDialog = true }
                    )
                }

                // MAIN CONTENT COLUMN
                Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    // 64px HEADER
                    AppHeaderBar(
                        isDesktop = isDesktop,
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        onMenuClick = { isMobileDrawerOpen = true },
                        onAvatarClick = { showSignOutDialog = true }
                    )

                    // SCREEN CONTENT
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(BasePageBg)
                    ) {
                        when (currentScreen) {
                            NavigationScreen.DASHBOARD -> DashboardScreen(
                                repository = repository,
                                onNavigateToClients = { currentScreen = NavigationScreen.CLIENTS },
                                onNavigateToRota = { currentScreen = NavigationScreen.ROTA },
                                onNavigateToEmar = { currentScreen = NavigationScreen.EMAR },
                                onNavigateToIncidents = { currentScreen = NavigationScreen.INCIDENTS },
                                onNavigateToWorkforce = { currentScreen = NavigationScreen.WORKFORCE },
                                onNavigateToCarerMode = { repository.toggleCarerMobileMode(true) },
                                onNavigateToCqc = { currentScreen = NavigationScreen.CQC_INSPECTIONS }
                            )
                            NavigationScreen.CLIENTS -> ClientsScreen(
                                repository = repository,
                                selectedClientId = selectedClientIdForDetail,
                                onSelectClient = { selectedClientIdForDetail = it }
                            )
                            NavigationScreen.ROTA -> RotaVisitsScreen(
                                repository = repository
                            )
                            NavigationScreen.EMAR -> EmarMedicationsScreen(
                                repository = repository,
                                initialClientId = selectedClientIdForDetail
                            )
                            NavigationScreen.INCIDENTS -> IncidentsListScreen(
                                repository = repository
                            )
                            NavigationScreen.FAMILY_INBOX -> FamilyInboxScreen(
                                repository = repository
                            )
                            NavigationScreen.WORKFORCE -> WorkforceStaffScreen(
                                repository = repository
                            )
                            NavigationScreen.TRAINING -> StaffTrainingScreen(
                                repository = repository
                            )
                            NavigationScreen.PAYROLL -> PayrollFinanceScreen(
                                repository = repository
                            )
                            NavigationScreen.COMPLIANCE -> ComplianceGovernanceScreen(
                                repository = repository
                            )
                            NavigationScreen.CQC_INSPECTIONS -> CqcInspectionsScreen(
                                repository = repository
                            )
                            NavigationScreen.REPORTS -> ComplianceReportingScreen(
                                repository = repository
                            )
                            NavigationScreen.ANALYTICS -> AnalyticsScreen(
                                repository = repository
                            )
                            NavigationScreen.NOTIFY_MANAGER -> NotifyLineManagerScreen(
                                repository = repository
                            )
                            NavigationScreen.DAILY_BRIEFING -> DailyBriefingScreen(
                                repository = repository
                            )
                        }
                    }
                }
            }

            // MOBILE SLIDE-OVER DRAWER (below 1024px)
            if (!isDesktop && isMobileDrawerOpen) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable { isMobileDrawerOpen = false }
                        .testTag("mobile_drawer_overlay")
                ) {
                    Box(
                        modifier = Modifier
                            .width(280.dp)
                            .fillMaxHeight()
                            .background(BaseModalBg)
                            .border(1.dp, BaseBorder)
                            .clickable(enabled = false) {} // absorb click
                    ) {
                        MobileDrawerContent(
                            currentScreen = currentScreen,
                            onSelectScreen = { screen ->
                                selectedClientIdForDetail = null
                                currentScreen = screen
                                isMobileDrawerOpen = false
                            },
                            onSignOut = {
                                isMobileDrawerOpen = false
                                showSignOutDialog = true
                            }
                        )
                    }
                }
            }
        }

        // SIGN OUT / SWITCH PERSONA MODAL
        if (showSignOutDialog) {
            AlertDialog(
                onDismissRequest = { showSignOutDialog = false },
                containerColor = BaseModalBg,
                shape = RoundedCornerShape(16.dp),
                title = {
                    Text("User Profile & Role", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Active user: ${currentUser.name} (${currentUser.role.displayName})", fontSize = 13.sp, color = BaseTextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Switch role persona:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = BaseTextHeading)

                        listOf(
                            UserRole.REGISTERED_MANAGER to "Registered Manager (Office)",
                            UserRole.COORDINATOR to "Coordinator (Rota)",
                            UserRole.CARE_WORKER to "Care Worker (Carer Mobile Mode)",
                            UserRole.NURSE to "Registered Nurse (eMAR)",
                            UserRole.FAMILY_MEMBER to "Family Member (Family Portal)"
                        ).forEach { (role, label) ->
                            Button(
                                onClick = {
                                    repository.switchUserRole(role)
                                    showSignOutDialog = false
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentUser.role == role) BaseEmerald500 else BaseModalInput
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier.fillMaxWidth().height(36.dp)
                            ) {
                                Text(label, fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSignOutDialog = false }) {
                        Text("Close", color = BaseEmerald300)
                    }
                }
            )
        }

        // INACTIVITY SESSION TIMEOUT LOCK OVERLAY
        if (isSessionLocked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF020617).copy(alpha = 0.96f))
                    .testTag("inactivity_lock_overlay"),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = BaseModalBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BaseBorder),
                    tonalElevation = 10.dp,
                    modifier = Modifier.widthIn(max = 360.dp).padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(StatusCritBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = "Locked", tint = StatusCritText, modifier = Modifier.size(28.dp))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("All Care X Security Lock", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                        Text(
                            "Session locked due to inactivity. Enter PIN (1234) to unlock.",
                            fontSize = 12.sp,
                            color = BaseTextMuted,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = pinInput,
                            onValueChange = {
                                pinInput = it
                                pinError = false
                            },
                            label = { Text("4-Digit PIN (Default: 1234)") },
                            isError = pinError,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = BaseModalInput,
                                unfocusedContainerColor = BaseModalInput,
                                focusedTextColor = BaseTextHeading,
                                unfocusedTextColor = BaseTextHeading,
                                focusedBorderColor = BaseEmerald500,
                                unfocusedBorderColor = Color(0xFF334155)
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("pin_unlock_input")
                        )

                        if (pinError) {
                            Text("Incorrect PIN. Try 1234.", fontSize = 11.sp, color = StatusCritText, modifier = Modifier.padding(top = 4.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val ok = repository.unlockSessionWithPin(pinInput)
                                if (ok) {
                                    pinInput = ""
                                    pinError = false
                                } else {
                                    pinError = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BaseEmerald500),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        ) {
                            Text("Unlock Session", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DesktopSidebar(
    currentScreen: NavigationScreen,
    onSelectScreen: (NavigationScreen) -> Unit,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(260.dp)
            .fillMaxHeight()
            .background(BaseSurface)
            .border(1.dp, BaseBorder)
    ) {
        // Top 64px Block: Logo tile + All Care X / CARE PLATFORM + bottom border
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .border(1.dp, BaseBorder)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Logo",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "All Care X",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = "CARE PLATFORM",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = BaseTextMuted,
                    letterSpacing = 1.6.sp
                )
            }
        }

        // Nav List: Scrollable, space-y-1, px-3
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            NavigationScreen.values().forEach { screen ->
                val isActive = currentScreen == screen
                SidebarNavItem(
                    screen = screen,
                    isActive = isActive,
                    onClick = { onSelectScreen(screen) }
                )
            }
        }

        // Pinned Footer: Sign Out with top border
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(1.dp, BaseBorder)
                .clickable { onSignOut() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Logout, contentDescription = "Sign out", tint = BaseTextMuted, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text("Sign out", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextMuted)
        }
    }
}

@Composable
private fun MobileDrawerContent(
    currentScreen: NavigationScreen,
    onSelectScreen: (NavigationScreen) -> Unit,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BaseModalBg)
    ) {
        // Logo Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .border(1.dp, BaseBorder)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Logo",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("All Care X", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                Text("CARE PLATFORM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BaseTextMuted, letterSpacing = 1.6.sp)
            }
        }

        // Nav List
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            NavigationScreen.values().forEach { screen ->
                val isActive = currentScreen == screen
                SidebarNavItem(
                    screen = screen,
                    isActive = isActive,
                    onClick = { onSelectScreen(screen) }
                )
            }
        }

        // Pinned Footer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(1.dp, BaseBorder)
                .clickable { onSignOut() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Logout, contentDescription = "Sign out", tint = BaseTextMuted, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text("Sign out", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BaseTextMuted)
        }
    }
}

@Composable
private fun SidebarNavItem(
    screen: NavigationScreen,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isActive) BaseNavActiveBg else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = screen.icon,
            contentDescription = screen.title,
            tint = if (isActive) BaseEmerald300 else BaseTextMuted,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = screen.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isActive) BaseEmerald300 else BaseTextMuted
        )
    }
}

@Composable
private fun AppHeaderBar(
    isDesktop: Boolean,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onMenuClick: () -> Unit,
    onAvatarClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(BasePageBg)
            .border(1.dp, BaseBorderHeader)
            .padding(horizontal = if (isDesktop) 24.dp else 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (isDesktop) {
            // Left Search Input (max-w-md, 448px, height 40px, bg #0F1322, 1px border)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search clients, staff, records...", color = BaseTextFaint, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BaseTextFaint, modifier = Modifier.size(18.dp)) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BaseRaisedSurface,
                    unfocusedContainerColor = BaseRaisedSurface,
                    focusedTextColor = BaseTextHeading,
                    unfocusedTextColor = BaseTextHeading,
                    focusedBorderColor = BaseEmerald500,
                    unfocusedBorderColor = BaseBorder
                ),
                modifier = Modifier
                    .widthIn(max = 448.dp)
                    .height(46.dp)
            )
        } else {
            // Mobile Hamburger menu button
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.testTag("mobile_menu_button")
            ) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = BaseTextMuted, modifier = Modifier.size(24.dp))
            }
        }

        // Right Notification Bell & Avatar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable { /* Notifications */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = BaseTextMuted, modifier = Modifier.size(20.dp))
                // 8px rose dot at top right
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(StatusCritText)
                        .align(Alignment.TopEnd)
                )
            }

            // 36px circular avatar with brand gradient and white bold initials "AC"
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BrandGradient)
                    .clickable { onAvatarClick() }
                    .testTag("header_avatar"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AC",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
