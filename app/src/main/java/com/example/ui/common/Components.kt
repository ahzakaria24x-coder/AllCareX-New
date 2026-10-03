package com.example.ui.common

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppUser
import com.example.model.Branch
import com.example.model.RiskLevel
import com.example.model.UserRole
import com.example.ui.theme.*

@Composable
fun AppHeader(
    currentUser: AppUser,
    isCarerMode: Boolean,
    branches: List<Branch>,
    selectedBranchId: String?,
    pendingSyncCount: Int,
    onRoleChange: (UserRole) -> Unit,
    onBranchChange: (String?) -> Unit,
    onToggleCarerMode: () -> Unit,
    onSyncClick: () -> Unit
) {
    var showRoleMenu by remember { mutableStateOf(false) }
    var showBranchMenu by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Logo & Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CareMintTeal),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = "All Care X Logo",
                            tint = Color(0xFF0A1128),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ALL CARE",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "X",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = CareMintTeal
                            )
                        }
                        Text(
                            text = if (isCarerMode) "CARER MOBILE WORKFLOW" else "UK CARE MANAGEMENT PLATFORM",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = CareMintTeal,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                // Controls: Mode Switcher & Persona Switcher
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Carer Mode Toggle Button
                    FilterChip(
                        selected = isCarerMode,
                        onClick = onToggleCarerMode,
                        label = {
                            Text(
                                text = if (isCarerMode) "Carer App" else "Office View",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isCarerMode) Icons.Default.PhoneAndroid else Icons.Default.DesktopWindows,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CareMintTealContainer,
                            selectedLabelColor = CareMintTeal,
                            selectedLeadingIconColor = CareMintTeal
                        ),
                        modifier = Modifier
                            .testTag("mode_toggle_button")
                            .height(34.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Persona / Role Picker Pill
                    Box {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clickable { showRoleMenu = true }
                                .testTag("role_picker_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(Color(currentUser.role.badgeColor)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = currentUser.avatarInitials,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = currentUser.role.displayName.take(14) + if (currentUser.role.displayName.length > 14) "..." else "",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Switch Role",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showRoleMenu,
                            onDismissRequest = { showRoleMenu = false }
                        ) {
                            Text(
                                text = "SWITCH USER PERSONA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            Divider()
                            UserRole.values().forEach { role ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(role.badgeColor))
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = role.displayName,
                                                fontSize = 12.sp,
                                                fontWeight = if (currentUser.role == role) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    },
                                    onClick = {
                                        onRoleChange(role)
                                        showRoleMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Sub-row: Branch Switcher & Offline Sync Banner
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Branch Selector
                Box {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier
                            .clickable { showBranchMenu = true }
                            .testTag("branch_selector")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = CareMintTeal,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val currentBranchName = branches.find { it.id == selectedBranchId }?.name ?: "All London Branches (3)"
                            Text(
                                text = currentBranchName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showBranchMenu,
                        onDismissRequest = { showBranchMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Branches (London)", fontSize = 12.sp) },
                            onClick = {
                                onBranchChange(null)
                                showBranchMenu = false
                            }
                        )
                        Divider()
                        branches.forEach { b ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(b.name, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        Text("${b.code} • ${b.activeClientsCount} Clients", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                },
                                onClick = {
                                    onBranchChange(b.id)
                                    showBranchMenu = false
                                }
                            )
                        }
                    }
                }

                // Pending Offline Sync Indicator
                if (pendingSyncCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B)),
                        modifier = Modifier
                            .clickable { onSyncClick() }
                            .testTag("offline_sync_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Sync",
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$pendingSyncCount Pending Sync",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF59E0B)
                            )
                        }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(CareMintTeal)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Cloud Synced (London)",
                            fontSize = 10.sp,
                            color = CareMintTeal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    isAlert: Boolean = false,
    onClick: () -> Unit = {}
) {
    // Exact recipe: rounded-2xl (16dp), border 1px #171C2E, background #0A1022, padding 20dp
    // Label 12px uppercase tracking-wider semibold slate-400; number 36px bold white; caption 14px slate-500;
    // Icon tile 40px rounded-xl with tinted background and -300 coloured icon at top-right.
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = BaseSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BaseBorder),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = title.uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    color = BaseTextMuted,
                    modifier = Modifier.weight(1f)
                )

                // 40px rounded-xl icon tile with tinted background and -300 colored icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = BaseTextHeading
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = BaseTextFaint,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun StatusPill(
    text: String = "",
    statusColor: Color = Color.Unspecified,
    textColor: Color = Color.Unspecified,
    status: String = text,
    color: Color = Color.Unspecified,
    modifier: Modifier = Modifier
) {
    val displayText = if (status.isNotEmpty()) status else text
    val targetColor = when {
        color != Color.Unspecified -> color
        statusColor != Color.Unspecified -> statusColor
        else -> BaseEmerald300
    }
    val finalTextColor = if (textColor != Color.Unspecified) textColor else targetColor

    Surface(
        shape = RoundedCornerShape(999.dp),
        color = targetColor.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, targetColor.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Text(
            text = displayText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = finalTextColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun NeutralPill(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color(0xFF161C2D),
        modifier = modifier
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = BaseTextBody,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun AiChip(
    text: String = "AI Draft",
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = AiChipBg,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = AiChipText,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = AiChipText
            )
        }
    }
}

@Composable
fun RiskBadge(risk: RiskLevel) {
    val (statusColor, textColor) = when (risk) {
        RiskLevel.LOW -> StatusLowText to StatusLowText
        RiskLevel.MEDIUM -> StatusMedText to StatusMedText
        RiskLevel.HIGH -> StatusHighText to StatusHighText
        RiskLevel.CRITICAL -> StatusCritText to StatusCritText
    }
    StatusPill(text = risk.displayName, statusColor = statusColor, textColor = textColor)
}
