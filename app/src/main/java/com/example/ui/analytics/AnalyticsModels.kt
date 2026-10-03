package com.example.ui.analytics

import com.example.model.UserRole
import java.util.UUID

enum class AnalyticsRole(val displayName: String, val role: UserRole) {
    REGISTERED_MANAGER("Registered Manager", UserRole.REGISTERED_MANAGER),
    COORDINATOR("Coordinator", UserRole.COORDINATOR),
    FINANCE("Finance & Billing", UserRole.FINANCE),
    HR("HR & Workforce", UserRole.HR_RECRUITER),
    DIRECTOR("Director / Board", UserRole.OWNER_ADMIN)
}

enum class AnalyticsDateRange(val displayName: String) {
    TODAY("Today"),
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month"),
    LAST_30_DAYS("Last 30 Days"),
    Q3_2026("Q3 2026"),
    YTD("Year to Date (2026)")
}

data class AlertThresholdRule(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val metricKey: String,
    val condition: String, // "GREATER_THAN", "LESS_THAN"
    val thresholdValue: Double,
    val currentValue: Double,
    val unit: String,
    val notifyRoles: List<String>,
    val isBreached: Boolean,
    val isEnabled: Boolean = true,
    val aiRecommendation: String
)

data class DrillDownRecord(
    val id: String,
    val primaryText: String,
    val secondaryText: String,
    val category: String,
    val valueBadge: String,
    val timestamp: String,
    val status: String
)

data class SavedReportTemplate(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val dataset: String,
    val scheduleFrequency: String, // "Daily 07:00", "Weekly Mon 08:00", "Monthly 1st", "Manual"
    val lastGenerated: String,
    val recipientRoles: List<String>,
    val format: String = "PDF + Excel"
)
