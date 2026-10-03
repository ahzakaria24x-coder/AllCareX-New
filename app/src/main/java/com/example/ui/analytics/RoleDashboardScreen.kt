package com.example.ui.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CareRepository
import com.example.ui.common.AiChip
import com.example.ui.common.NeutralPill
import com.example.ui.common.StatCard
import com.example.ui.common.StatusPill
import com.example.ui.theme.*

@Composable
fun RoleDashboardScreen(
    repository: CareRepository
) {
    val clients by repository.clients.collectAsState()
    val visits by repository.visits.collectAsState()
    val incidents by repository.incidents.collectAsState()
    val staffList by repository.staffList.collectAsState()
    val branches by repository.branches.collectAsState()

    var selectedRole by remember { mutableStateOf(AnalyticsRole.REGISTERED_MANAGER) }
    var selectedBranch by remember { mutableStateOf("All Branches") }
    var selectedDateRange by remember { mutableStateOf(AnalyticsDateRange.THIS_MONTH) }
    var selectedCareType by remember { mutableStateOf("All Services") }

    // Drill down dialog state
    var drillDownTitle by remember { mutableStateOf<String?>(null) }
    var drillDownDescription by remember { mutableStateOf("") }
    var drillDownRecords by remember { mutableStateOf<List<DrillDownRecord>>(emptyList()) }

    // Dropdowns open state
    var branchDropdownOpen by remember { mutableStateOf(false) }
    var dateDropdownOpen by remember { mutableStateOf(false) }
    var careTypeDropdownOpen by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(BasePageBg)) {
        val isDesktop = maxWidth >= 1024.dp
        val contentPadding = if (isDesktop) 32.dp else 16.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .testTag("role_dashboard_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Header Row: Title & Subtitle
            item {
                Column {
                    Text(
                        text = "${selectedRole.displayName} Analytics",
                        fontSize = if (isDesktop) 30.sp else 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = BaseTextHeading
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Cross-service operational, clinical, workforce and financial intelligence.",
                        fontSize = 14.sp,
                        color = BaseTextMuted
                    )
                }
            }

            // 1. Role Selector Tabs
            item {
                val hScroll = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(hScroll)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF161C2D))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AnalyticsRole.values().forEach { role ->
                        val isSelected = selectedRole == role
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) BaseEmerald500 else Color.Transparent)
                                .clickable { selectedRole = role }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = role.displayName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Color.White else BaseTextMuted
                            )
                        }
                    }
                }
            }

            // Filters Row: Branch, Date Range, Care Type
            item {
                val filterScroll = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(filterScroll),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Branch filter
                    Box {
                        OutlinedButton(
                            onClick = { branchDropdownOpen = true },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = BaseSurface)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = BaseEmerald300, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(selectedBranch, fontSize = 12.sp, color = BaseTextHeading)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = BaseTextMuted)
                        }

                        DropdownMenu(
                            expanded = branchDropdownOpen,
                            onDismissRequest = { branchDropdownOpen = false },
                            modifier = Modifier.background(BaseModalBg)
                        ) {
                            (listOf("All Branches") + branches.map { it.name }).forEach { b ->
                                DropdownMenuItem(
                                    text = { Text(b, color = BaseTextHeading) },
                                    onClick = {
                                        selectedBranch = b
                                        branchDropdownOpen = false
                                    }
                                )
                            }
                        }
                    }

                    // Date range filter
                    Box {
                        OutlinedButton(
                            onClick = { dateDropdownOpen = true },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = BaseSurface)
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = StatusInfoText, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(selectedDateRange.displayName, fontSize = 12.sp, color = BaseTextHeading)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = BaseTextMuted)
                        }

                        DropdownMenu(
                            expanded = dateDropdownOpen,
                            onDismissRequest = { dateDropdownOpen = false },
                            modifier = Modifier.background(BaseModalBg)
                        ) {
                            AnalyticsDateRange.values().forEach { d ->
                                DropdownMenuItem(
                                    text = { Text(d.displayName, color = BaseTextHeading) },
                                    onClick = {
                                        selectedDateRange = d
                                        dateDropdownOpen = false
                                    }
                                )
                            }
                        }
                    }

                    // Care type filter
                    Box {
                        OutlinedButton(
                            onClick = { careTypeDropdownOpen = true },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = BaseSurface)
                        ) {
                            Icon(Icons.Default.MedicalServices, contentDescription = null, tint = StatusVioletText, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(selectedCareType, fontSize = 12.sp, color = BaseTextHeading)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = BaseTextMuted)
                        }

                        DropdownMenu(
                            expanded = careTypeDropdownOpen,
                            onDismissRequest = { careTypeDropdownOpen = false },
                            modifier = Modifier.background(BaseModalBg)
                        ) {
                            listOf("All Services", "Domiciliary", "Supported Living", "Residential / Nursing").forEach { ct ->
                                DropdownMenuItem(
                                    text = { Text(ct, color = BaseTextHeading) },
                                    onClick = {
                                        selectedCareType = ct
                                        careTypeDropdownOpen = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Stat Cards tailored to the selected role
            item {
                when (selectedRole) {
                    AnalyticsRole.REGISTERED_MANAGER -> {
                        FourStatRow(
                            isDesktop = isDesktop,
                            c1 = StatCardData("CQC Readiness", "91%", "5/5 Quality Domains Good", Icons.Default.VerifiedUser, BaseEmerald300) {
                                drillDownTitle = "CQC Compliance Scores"
                                drillDownDescription = "Key Question Quality Statement breakdown"
                                drillDownRecords = listOf(
                                    DrillDownRecord("cqc-1", "Safe Domain Evidence", "Safeguarding, Medicines, Infection Control", "Quality", "90%", "03/10/2026", "Pass"),
                                    DrillDownRecord("cqc-2", "Effective Domain Evidence", "Person-centred care plans, NEWS2 obs", "Quality", "92%", "03/10/2026", "Pass"),
                                    DrillDownRecord("cqc-3", "Caring Domain Evidence", "Service user feedback, dignity reviews", "Quality", "92%", "03/10/2026", "Pass"),
                                    DrillDownRecord("cqc-4", "Responsive Domain Evidence", "Complaints & rapid care adjustments", "Quality", "92%", "03/10/2026", "Pass"),
                                    DrillDownRecord("cqc-5", "Well-led Domain Evidence", "Supervisions, governance & audits", "Quality", "91%", "03/10/2026", "Pass")
                                )
                            },
                            c2 = StatCardData("Critical Incidents", "2", "1 CQC Reg 18 Notifiable", Icons.Default.Warning, StatusCritText) {
                                drillDownTitle = "Active Safeguarding & Clinical Incidents"
                                drillDownDescription = "Investigating incidents requiring manager sign-off"
                                drillDownRecords = incidents.map {
                                    DrillDownRecord(it.id, it.clientName, it.description, it.category.displayName, it.severity.name, it.incidentDate, it.investigationStatus)
                                }
                            },
                            c3 = StatCardData("Audit Score Avg", "91.2%", "Last audit: 15/09/2026", Icons.Default.FactCheck, BaseEmerald300) {
                                drillDownTitle = "Clinical & Quality Audit Log"
                                drillDownDescription = "Internal governance and spot check completions"
                                drillDownRecords = listOf(
                                    DrillDownRecord("aud-1", "Q3 Medication Audit", "Lead: Aisha Khan (RN)", "eMAR", "94%", "15/09/2026", "Completed"),
                                    DrillDownRecord("aud-2", "Infection Control Spot Check", "Lead: Rachel Stein", "Clinical", "88%", "10/08/2026", "Completed"),
                                    DrillDownRecord("aud-3", "Safeguarding Annual Review", "Lead: Rachel Stein", "Governance", "91%", "20/07/2026", "Completed")
                                )
                            },
                            c4 = StatCardData("Training Compliant", "89%", "5 modules expired", Icons.Default.School, StatusMedText) {
                                drillDownTitle = "Staff Mandatory Training Compliance"
                                drillDownDescription = "Staff with expired or approaching renewal certifications"
                                drillDownRecords = staffList.map {
                                    DrillDownRecord(it.id, it.name, "${it.role.displayName} • Training matrix", "Workforce", "${it.compliancePercentage}%", "03/10/2026", if (it.compliancePercentage >= 90) "Normal" else "Warning")
                                }
                            }
                        )
                    }

                    AnalyticsRole.COORDINATOR -> {
                        FourStatRow(
                            isDesktop = isDesktop,
                            c1 = StatCardData("Delivered vs Sched", "412 / 420", "98.1% visit fulfillment", Icons.Default.CalendarToday, BaseEmerald300) {
                                drillDownTitle = "Visits Scheduled vs Delivered"
                                drillDownDescription = "Completed and scheduled visits breakdown"
                                drillDownRecords = visits.map {
                                    DrillDownRecord(it.id, it.clientName, "Carer: ${it.carerName ?: "Unassigned"} • ${it.startTime}-${it.endTime}", "Visits", it.date, it.status.displayName, if (it.status.displayName == "Completed") "Completed" else "On Track")
                                }
                            },
                            c2 = StatCardData("Late / Missed", "1.8% / 0.4%", "Traffic (62%), Emergency (38%)", Icons.Default.AccessTime, StatusMedText) {
                                drillDownTitle = "Late & Missed Visits Audit"
                                drillDownDescription = "Visits flagged running >15m late or missed"
                                drillDownRecords = listOf(
                                    DrillDownRecord("v-late-1", "Naim (09:00 Run)", "Carer delayed in A64 traffic gridlock", "Operations", "+18m Late", "02/10/2026", "Late"),
                                    DrillDownRecord("v-miss-1", "Margaret Holloway", "Client at unscheduled GP emergency appointment", "Operations", "Missed (Client Away)", "28/09/2026", "Missed")
                                )
                            },
                            c3 = StatCardData("Continuity Rate", "84.2%", "Target: ≥80% primary carers", Icons.Default.Group, BaseEmerald300) {
                                drillDownTitle = "Care Continuity by Client"
                                drillDownDescription = "Percentage of runs delivered by client's top 2 regular carers"
                                drillDownRecords = listOf(
                                    DrillDownRecord("cont-1", "Doris Penrose", "Primary: Aisha Khan & Tom Brennan", "Continuity", "88%", "Sep 2026", "Pass"),
                                    DrillDownRecord("cont-2", "Margaret Holloway", "Primary: Tom Brennan & Daniel Okafor", "Continuity", "82%", "Sep 2026", "Pass"),
                                    DrillDownRecord("cont-3", "Geoffrey Whitfield", "Primary: Lucia Marques", "Continuity", "86%", "Sep 2026", "Pass")
                                )
                            },
                            c4 = StatCardData("Unallocated Hours", "14.5h", "Next 7 days coverage", Icons.Default.EventBusy, StatusCritText) {
                                drillDownTitle = "Unallocated Care Hours Pipeline"
                                drillDownDescription = "Scheduled visits requiring staff assignment"
                                drillDownRecords = listOf(
                                    DrillDownRecord("unalloc-1", "Naim • Morning Call (09:00)", "York Central • 1.0 hour", "Rota", "Unallocated", "04/10/2026", "Warning"),
                                    DrillDownRecord("unalloc-2", "Geoffrey Whitfield • Tea (17:00)", "Harrogate • 0.75 hour", "Rota", "Unallocated", "04/10/2026", "Warning")
                                )
                            }
                        )
                    }

                    AnalyticsRole.FINANCE -> {
                        FourStatRow(
                            isDesktop = isDesktop,
                            c1 = StatCardData("Gross Revenue", "£142,800", "+8.4% vs previous month", Icons.Default.AccountBalanceWallet, BaseEmerald300) {
                                drillDownTitle = "Invoiced Revenue by Service"
                                drillDownDescription = "Total care delivered income across funding streams"
                                drillDownRecords = listOf(
                                    DrillDownRecord("rev-1", "Local Authority Framework (Camden)", "2,140 care hours @ £24.50/hr", "Finance", "£52,430", "Sep 2026", "Completed"),
                                    DrillDownRecord("rev-2", "Private Self-Funded Clients", "1,850 care hours @ £32.00/hr", "Finance", "£59,200", "Sep 2026", "Completed"),
                                    DrillDownRecord("rev-3", "NHS Continuing Healthcare (CHC)", "880 care hours @ £35.40/hr", "Finance", "£31,170", "Sep 2026", "Completed")
                                )
                            },
                            c2 = StatCardData("Direct Carer Cost", "£98,400", "Payroll + NI + Mileage", Icons.Default.Payments, BaseTextHeading) {
                                drillDownTitle = "Direct Care Delivery Costs"
                                drillDownDescription = "Staff wages, employer pension, and travel reimbursement"
                                drillDownRecords = listOf(
                                    DrillDownRecord("cost-1", "Carer Base Hourly Pay", "4,870 hours logged across branches", "Payroll", "£73,050", "Sep 2026", "Completed"),
                                    DrillDownRecord("cost-2", "Employer NI & Pension", "Statutory workforce contributions", "Tax", "£15,850", "Sep 2026", "Completed"),
                                    DrillDownRecord("cost-3", "Mileage & Travel Allowance", "9,500 billable km @ 45p/mile", "Expenses", "£9,500", "Sep 2026", "Completed")
                                )
                            },
                            c3 = StatCardData("Gross Margin", "31.1%", "£44,400 surplus margin", Icons.Default.TrendingUp, BaseEmerald300) {
                                drillDownTitle = "Branch Margin Performance"
                                drillDownDescription = "Gross margin after direct staff costs by branch"
                                drillDownRecords = listOf(
                                    DrillDownRecord("bm-1", "Camden & Islington Branch", "Revenue £64,000 | Cost £42,000", "Margin", "34.4%", "Sep 2026", "Pass"),
                                    DrillDownRecord("bm-2", "York & North Yorkshire Branch", "Revenue £48,000 | Cost £34,000", "Margin", "29.2%", "Sep 2026", "Pass"),
                                    DrillDownRecord("bm-3", "Bradford & Airedale Branch", "Revenue £30,800 | Cost £22,400", "Margin", "27.3%", "Sep 2026", "Pass")
                                )
                            },
                            c4 = StatCardData("Overdue Invoices", "£4,250", "2 invoices >30 days past due", Icons.Default.ReceiptLong, StatusCritText) {
                                drillDownTitle = "Aged Debtors & Overdue Invoices"
                                drillDownDescription = "Invoices unpaid past credit term dates"
                                drillDownRecords = listOf(
                                    DrillDownRecord("inv-101", "North Yorkshire County Council", "Invoice #INV-2026-081 (Due 15/09/2026)", "Invoicing", "£2,850.00", "48d Overdue", "Critical"),
                                    DrillDownRecord("inv-102", "Private Client: Margaret Holloway (Estate)", "Invoice #INV-2026-088 (Due 20/09/2026)", "Invoicing", "£1,400.00", "34d Overdue", "Warning")
                                )
                            }
                        )
                    }

                    AnalyticsRole.HR -> {
                        FourStatRow(
                            isDesktop = isDesktop,
                            c1 = StatCardData("DBS & Training", "91.4%", "Compliance baseline index", Icons.Default.Badge, BaseEmerald300) {
                                drillDownTitle = "Workforce Compliance Status"
                                drillDownDescription = "Full DBS Enhanced and 12-Module mandatory training index"
                                drillDownRecords = staffList.map {
                                    DrillDownRecord(it.id, it.name, "${it.role.displayName} • DBS: Clear", "Compliance", "${it.compliancePercentage}%", "03/10/2026", if (it.compliancePercentage >= 90) "Pass" else "Warning")
                                }
                            },
                            c2 = StatCardData("Staff Turnover", "14.2%", "UK care sector benchmark: 28%", Icons.Default.SwapHoriz, BaseEmerald300) {
                                drillDownTitle = "Staff Retention & Turnover Rate"
                                drillDownDescription = "Leavers and new starters annualized rate"
                                drillDownRecords = listOf(
                                    DrillDownRecord("turn-1", "Annualized Leaver Rate", "2 leavers over trailing 12 months", "HR", "14.2%", "Trailing 12M", "Pass"),
                                    DrillDownRecord("turn-2", "Average Carer Tenure", "Staff retention average", "HR", "2.8 Years", "Trailing 12M", "Pass")
                                )
                            },
                            c3 = StatCardData("Sickness Rate", "2.8%", "Lost hours (Target: <4.0%)", Icons.Default.Sick, BaseEmerald300) {
                                drillDownTitle = "Sickness & Absence Hours Log"
                                drillDownDescription = "Unscheduled absence and sick pay claims"
                                drillDownRecords = listOf(
                                    DrillDownRecord("sick-1", "Daniel Okafor", "2 shifts lost (flu / medical)", "Absence", "16.0h", "22/09/2026", "Normal"),
                                    DrillDownRecord("sick-2", "Lucia Marques", "1 shift lost (migraine)", "Absence", "8.0h", "14/09/2026", "Normal")
                                )
                            },
                            c4 = StatCardData("Supervision Due", "88%", "4 supervisions due in 14d", Icons.Default.SupervisedUserCircle, StatusMedText) {
                                drillDownTitle = "1-to-1 Supervisions & Appraisals Schedule"
                                drillDownDescription = "CQC Regulation 18 staff governance and competency checks"
                                drillDownRecords = listOf(
                                    DrillDownRecord("sup-1", "Daniel Okafor (Care Worker)", "Supervision Lead: Rachel Stein", "Supervision", "Due in 6d", "09/10/2026", "Warning"),
                                    DrillDownRecord("sup-2", "Lucia Marques (Care Worker)", "Supervision Lead: Tom Brennan", "Supervision", "Due in 11d", "14/10/2026", "Warning"),
                                    DrillDownRecord("sup-3", "Tom Brennan (Senior Carer)", "Supervision Lead: Eleanor Vance", "Supervision", "Due in 18d", "21/10/2026", "Normal")
                                )
                            }
                        )
                    }

                    AnalyticsRole.DIRECTOR -> {
                        FourStatRow(
                            isDesktop = isDesktop,
                            c1 = StatCardData("Total EBITDA", "£28,600", "20.0% operating margin", Icons.Default.MonetizationOn, BaseEmerald300) {
                                drillDownTitle = "Consolidated Executive EBITDA"
                                drillDownDescription = "Operating profit before depreciation across all registered branches"
                                drillDownRecords = listOf(
                                    DrillDownRecord("eb-1", "Camden Service Hub", "Revenue £64k | Operating Cost £49k", "Executive", "£15,000", "Sep 2026", "Pass"),
                                    DrillDownRecord("eb-2", "Yorkshire Service Hub", "Revenue £78.8k | Operating Cost £65.2k", "Executive", "£13,600", "Sep 2026", "Pass")
                                )
                            },
                            c2 = StatCardData("Active Client Capacity", "86.4%", "48 current / 55 cap", Icons.Default.PeopleAlt, BaseEmerald300) {
                                drillDownTitle = "Service Capacity & Client Inflow"
                                drillDownDescription = "Safe caseload ratios vs Registered Manager capacity"
                                drillDownRecords = listOf(
                                    DrillDownRecord("cap-1", "Camden Domiciliary", "24 Active Packages / 28 Max", "Capacity", "85.7%", "Sep 2026", "Normal"),
                                    DrillDownRecord("cap-2", "York Supported Living", "18 Active Packages / 20 Max", "Capacity", "90.0%", "Sep 2026", "Normal"),
                                    DrillDownRecord("cap-3", "Bradford Outreach", "6 Active Packages / 7 Max", "Capacity", "85.7%", "Sep 2026", "Normal")
                                )
                            },
                            c3 = StatCardData("CQC Audit Status", "Outstanding Ready", "91% quality statement average", Icons.Default.Stars, BaseEmerald300) {
                                drillDownTitle = "CQC Single Assessment Framework Audit Status"
                                drillDownDescription = "Readiness benchmark across all 34 quality statements"
                                drillDownRecords = listOf(
                                    DrillDownRecord("cqc-gov-1", "Safe & Safeguarding", "Zero unresolved statutory breaches", "Audit", "90%", "03/10/2026", "Pass"),
                                    DrillDownRecord("cqc-gov-2", "Clinical Effectiveness", "Digital eMAR & Waterlow tracking", "Audit", "92%", "03/10/2026", "Pass"),
                                    DrillDownRecord("cqc-gov-3", "Leadership & Governance", "Duty of Candour & closed audit loops", "Audit", "91%", "03/10/2026", "Pass")
                                )
                            },
                            c4 = StatCardData("Agency Dependency", "3.2%", "Target: <5.0% of hours", Icons.Default.PersonOff, BaseEmerald300) {
                                drillDownTitle = "Agency Staff Dependency & Cost"
                                drillDownDescription = "Proportion of care visits delivered by temporary agency workers"
                                drillDownRecords = listOf(
                                    DrillDownRecord("ag-1", "Pulse Healthcare Agency", "Covered 4 weekend shifts (night standby)", "Agency", "£1,420 (3.2%)", "Sep 2026", "Pass")
                                )
                            }
                        )
                    }
                }
            }

            // 2. Operational Analytics Section (Clickable with AI Insights)
            item {
                Text(
                    text = "Operational Analytics & Visit Delivery",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BaseTextHeading
                )
            }

            item {
                if (isDesktop) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        VisitsScheduledVsDeliveredChart(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                drillDownTitle = "Visits Scheduled vs Delivered (Last 6 Weeks)"
                                drillDownDescription = "Actual visit completion logs vs planned roster"
                                drillDownRecords = listOf(
                                    DrillDownRecord("rec-v1", "Week 36 (01 Sep - 07 Sep)", "Scheduled: 410 | Delivered: 405 (5 Cancelled by Family)", "Rota", "98.8%", "07/09/2026", "Completed"),
                                    DrillDownRecord("rec-v2", "Week 37 (08 Sep - 14 Sep)", "Scheduled: 415 | Delivered: 410 (5 Cancelled)", "Rota", "98.8%", "14/09/2026", "Completed"),
                                    DrillDownRecord("rec-v3", "Week 38 (15 Sep - 21 Sep)", "Scheduled: 420 | Delivered: 418 (2 Rescheduled)", "Rota", "99.5%", "21/09/2026", "Completed"),
                                    DrillDownRecord("rec-v4", "Week 39 (22 Sep - 28 Sep)", "Scheduled: 420 | Delivered: 412 (8 Late/Omitted)", "Rota", "98.1%", "28/09/2026", "Warning")
                                )
                            }
                        )

                        LateMissedReasonsChart(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                drillDownTitle = "Late & Missed Visit Reasons Log"
                                drillDownDescription = "Root causes captured by field GPS check-ins"
                                drillDownRecords = listOf(
                                    DrillDownRecord("lm-1", "Urban Traffic & Road Closures", "62% of delays. Peak times: 08:00-09:30 on A1/A64 routes", "Late Reason", "18 Visits", "Sep 2026", "Warning"),
                                    DrillDownRecord("lm-2", "Clinical Emergency / 999 Paramedic Wait", "22% of delays. Carer stayed with client pending ambulance", "Late Reason", "6 Visits", "Sep 2026", "Completed"),
                                    DrillDownRecord("lm-3", "Key-Safe Battery / Access Lockout", "10% of delays. Backup physical key retrieved from branch", "Late Reason", "3 Visits", "Sep 2026", "Warning"),
                                    DrillDownRecord("lm-4", "Short-Notice Sickness Handover", "6% of delays. Emergency cover dispatched within 25 mins", "Late Reason", "2 Visits", "Sep 2026", "Normal")
                                )
                            }
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        VisitsScheduledVsDeliveredChart(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                drillDownTitle = "Visits Scheduled vs Delivered (Last 6 Weeks)"
                                drillDownDescription = "Actual visit completion logs vs planned roster"
                                drillDownRecords = listOf(
                                    DrillDownRecord("rec-v1", "Week 36 (01 Sep - 07 Sep)", "Scheduled: 410 | Delivered: 405", "Rota", "98.8%", "07/09/2026", "Completed"),
                                    DrillDownRecord("rec-v2", "Week 39 (22 Sep - 28 Sep)", "Scheduled: 420 | Delivered: 412", "Rota", "98.1%", "28/09/2026", "Warning")
                                )
                            }
                        )
                        LateMissedReasonsChart(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                drillDownTitle = "Late & Missed Visit Reasons Log"
                                drillDownDescription = "Root causes captured by field GPS check-ins"
                                drillDownRecords = listOf(
                                    DrillDownRecord("lm-1", "Urban Traffic & Road Closures", "62% of delays", "Late Reason", "18 Visits", "Sep 2026", "Warning"),
                                    DrillDownRecord("lm-2", "Clinical Emergency / 999 Paramedic Wait", "22% of delays", "Late Reason", "6 Visits", "Sep 2026", "Completed")
                                )
                            }
                        )
                    }
                }
            }

            // 3. Clinical & Safety Analytics Section (Clickable with AI Insights)
            item {
                Text(
                    text = "Clinical & Safeguarding Intelligence",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BaseTextHeading
                )
            }

            item {
                if (isDesktop) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        IncidentsTimeOfDayChart(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                drillDownTitle = "Incident Time-of-Day Distribution"
                                drillDownDescription = "Clinical incidents grouped by 4-hour shift windows"
                                drillDownRecords = listOf(
                                    DrillDownRecord("tod-1", "Morning (07:00 - 11:00)", "Doris Penrose (Breathlessness) & Margaret Holloway (Fall)", "Clinical", "2 Incidents (67%)", "08:30 / 09:20", "Critical"),
                                    DrillDownRecord("tod-2", "Midday (11:00 - 15:00)", "No recorded incidents in review window", "Clinical", "0 Incidents", "11:00 - 15:00", "Normal"),
                                    DrillDownRecord("tod-3", "Evening (15:00 - 20:00)", "Geoffrey Whitfield (Medication Inhaler Omission)", "eMAR", "1 Incident (33%)", "18:00", "Warning"),
                                    DrillDownRecord("tod-4", "Night (20:00 - 07:00)", "No night falls or clinical escalations", "Clinical", "0 Incidents", "20:00 - 07:00", "Normal")
                                )
                            }
                        )

                        FallsTrendChart(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                drillDownTitle = "Falls Incident Timeline & Prevention Audit"
                                drillDownDescription = "Post-fall neuro observations and physiotherapy reviews"
                                drillDownRecords = listOf(
                                    DrillDownRecord("fall-1", "Margaret Holloway (Home Care)", "Found on floor by bathroom. No fracture. Physio review requested.", "Fall", "Moderate", "29/08/2026", "Investigating"),
                                    DrillDownRecord("fall-2", "Arthur Pendelton (Residential)", "Slip on wet kitchen tiles. Minor bruising to knee. Ice applied.", "Fall", "Minor", "12/08/2026", "Closed")
                                )
                            }
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        IncidentsTimeOfDayChart(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                drillDownTitle = "Incident Time-of-Day Distribution"
                                drillDownDescription = "Clinical incidents grouped by shift windows"
                                drillDownRecords = listOf(
                                    DrillDownRecord("tod-1", "Morning (07:00 - 11:00)", "Doris Penrose & Margaret Holloway", "Clinical", "2 Incidents", "Morning", "Critical")
                                )
                            }
                        )
                        FallsTrendChart(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                drillDownTitle = "Falls Incident Timeline"
                                drillDownDescription = "Post-fall reviews and observations"
                                drillDownRecords = listOf(
                                    DrillDownRecord("fall-1", "Margaret Holloway", "Bathroom slip. Physio referral sent.", "Fall", "Moderate", "29/08/2026", "Investigating")
                                )
                            }
                        )
                    }
                }
            }

            // 4. Workforce & Finance Analytics Section
            item {
                Text(
                    text = "Workforce Compliance & Financial Margins",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BaseTextHeading
                )
            }

            item {
                if (isDesktop) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        TrainingExpiryPipelineChart(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                drillDownTitle = "DBS & Training Expiry Forecast Pipeline"
                                drillDownDescription = "Upcoming certification renewals by 30-day buckets"
                                drillDownRecords = listOf(
                                    DrillDownRecord("exp-1", "Daniel Okafor (Moving & Handling)", "Expired 53 days ago (09/08/2026) • Practical course booked", "Training", "Expired", "09/08/2026", "Critical"),
                                    DrillDownRecord("exp-2", "Tom Brennan (First Aid at Work)", "Expires in 103 days (12/01/2027) • Provider confirmed", "Training", "Current", "12/01/2027", "Normal"),
                                    DrillDownRecord("exp-3", "Aisha Khan (Safeguarding Level 3)", "Expires in 123 days (01/02/2027) • E-learning completed", "Training", "Current", "01/02/2027", "Normal")
                                )
                            }
                        )

                        BranchMarginFinancialChart(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                drillDownTitle = "Branch Financial Contribution Margin"
                                drillDownDescription = "Revenue vs Direct Carer Payroll breakdown"
                                drillDownRecords = listOf(
                                    DrillDownRecord("fin-1", "Camden & Islington Service Hub", "Revenue: £64,000 | Wages & Exp: £42,000 | Margin: £22,000", "Branch P&L", "34.4% Margin", "Sep 2026", "Pass"),
                                    DrillDownRecord("fin-2", "York & North Yorkshire Hub", "Revenue: £48,000 | Wages & Exp: £34,000 | Margin: £14,000", "Branch P&L", "29.2% Margin", "Sep 2026", "Pass"),
                                    DrillDownRecord("fin-3", "Bradford Supported Living Hub", "Revenue: £30,800 | Wages & Exp: £22,400 | Margin: £8,400", "Branch P&L", "27.3% Margin", "Sep 2026", "Pass")
                                )
                            }
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        TrainingExpiryPipelineChart(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                drillDownTitle = "DBS & Training Expiry Pipeline"
                                drillDownDescription = "Upcoming certification renewals"
                                drillDownRecords = listOf(
                                    DrillDownRecord("exp-1", "Daniel Okafor (Moving & Handling)", "Expired 53d ago", "Training", "Expired", "09/08/2026", "Critical")
                                )
                            }
                        )
                        BranchMarginFinancialChart(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                drillDownTitle = "Branch Contribution Margin"
                                drillDownDescription = "Revenue vs Direct Carer Payroll"
                                drillDownRecords = listOf(
                                    DrillDownRecord("fin-1", "Camden & Islington", "Revenue £64k | Margin £22k", "P&L", "34.4%", "Sep 2026", "Pass")
                                )
                            }
                        )
                    }
                }
            }
        }

        // Drill down dialog overlay
        drillDownTitle?.let { title ->
            ChartDrillDownDialog(
                title = title,
                metricDescription = drillDownDescription,
                records = drillDownRecords,
                onDismiss = { drillDownTitle = null }
            )
        }
    }
}

// ==================== REUSABLE STATS ROW ====================
data class StatCardData(
    val title: String,
    val value: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
private fun FourStatRow(
    isDesktop: Boolean,
    c1: StatCardData,
    c2: StatCardData,
    c3: StatCardData,
    c4: StatCardData
) {
    if (isDesktop) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(c1.title, c1.value, c1.subtitle, c1.icon, c1.color, modifier = Modifier.weight(1f), onClick = c1.onClick)
            StatCard(c2.title, c2.value, c2.subtitle, c2.icon, c2.color, modifier = Modifier.weight(1f), onClick = c2.onClick)
            StatCard(c3.title, c3.value, c3.subtitle, c3.icon, c3.color, modifier = Modifier.weight(1f), onClick = c3.onClick)
            StatCard(c4.title, c4.value, c4.subtitle, c4.icon, c4.color, modifier = Modifier.weight(1f), onClick = c4.onClick)
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(c1.title, c1.value, c1.subtitle, c1.icon, c1.color, modifier = Modifier.weight(1f), onClick = c1.onClick)
                StatCard(c2.title, c2.value, c2.subtitle, c2.icon, c2.color, modifier = Modifier.weight(1f), onClick = c2.onClick)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(c3.title, c3.value, c3.subtitle, c3.icon, c3.color, modifier = Modifier.weight(1f), onClick = c3.onClick)
                StatCard(c4.title, c4.value, c4.subtitle, c4.icon, c4.color, modifier = Modifier.weight(1f), onClick = c4.onClick)
            }
        }
    }
}

// ==================== CUSTOM CLICKABLE CHART WIDGETS WITH AI INSIGHTS ====================

@Composable
private fun VisitsScheduledVsDeliveredChart(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BaseSurface)
            .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = BaseEmerald300, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Visits Scheduled vs Delivered", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
            }
            Text("Tap to drill down →", fontSize = 11.sp, color = BaseEmerald300, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // AI Insight Line
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AiChipBg)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AiGradientStart, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "AI Operational Insight: 98.1% fulfillment rate. 8 cancelled visits concentrated in Week 39 due to planned hospital discharge delays.",
                fontSize = 11.sp,
                color = AiChipText,
                lineHeight = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chart Bars
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val weeks = listOf(
                "W36 (Sep 01-07)" to (410 to 405),
                "W37 (Sep 08-14)" to (415 to 410),
                "W38 (Sep 15-21)" to (420 to 418),
                "W39 (Sep 22-28)" to (420 to 412)
            )

            weeks.forEach { (weekLabel, pair) ->
                val (sched, deliv) = pair
                val ratio = deliv.toFloat() / sched.toFloat()
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(weekLabel, fontSize = 12.sp, color = BaseTextMuted)
                        Text("$deliv / $sched (${String.format("%.1f", ratio * 100)}%)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = BaseEmerald300)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { ratio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = BaseEmerald500,
                        trackColor = Color(0xFF1E283F)
                    )
                }
            }
        }
    }
}

@Composable
private fun LateMissedReasonsChart(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BaseSurface)
            .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, contentDescription = null, tint = StatusMedText, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Late & Missed Visit Reasons", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
            }
            Text("Tap to drill down →", fontSize = 11.sp, color = StatusMedText, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // AI Insight Line
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AiChipBg)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AiGradientStart, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "AI Rota Insight: 62% of lateness caused by morning A64 traffic congestion. Adjusting carer travel allowance by +10m on runs 3 & 4 will eliminate delays.",
                fontSize = 11.sp,
                color = AiChipText,
                lineHeight = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val reasons = listOf(
                "Urban Traffic / Congestion" to (0.62f to "62% (18 visits)"),
                "Clinical Emergency on Previous Visit" to (0.22f to "22% (6 visits)"),
                "Key-Safe / Access Difficulty" to (0.10f to "10% (3 visits)"),
                "Unplanned Staff Sickness Cover" to (0.06f to "6% (2 visits)")
            )

            reasons.forEach { (reason, pair) ->
                val (pct, label) = pair
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(reason, fontSize = 12.sp, color = BaseTextMuted, maxLines = 1)
                        Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = StatusMedText)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { pct },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = StatusMedText,
                        trackColor = Color(0xFF1E283F)
                    )
                }
            }
        }
    }
}

@Composable
private fun IncidentsTimeOfDayChart(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BaseSurface)
            .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = StatusCritText, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Incidents by Shift Window", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
            }
            Text("Tap to drill down →", fontSize = 11.sp, color = StatusCritText, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // AI Insight Line
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AiChipBg)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AiGradientStart, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "AI Clinical Insight: 67% of clinical incidents occur between 07:00-11:00 during high-exertion morning transfers. Recommend 2-carer protocol review for high Waterlow clients.",
                fontSize = 11.sp,
                color = AiChipText,
                lineHeight = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                for (i in 1..3) {
                    val y = height * (i / 4f)
                    drawLine(color = Color(0xFF1E283F), start = Offset(0f, y), end = Offset(width, y), strokeWidth = 1.dp.toPx())
                }

                val barWidth = width * 0.16f
                val heights = listOf(0.75f, 0.05f, 0.35f, 0.05f) // Morning, Midday, Evening, Night
                val colors = listOf(StatusCritText, BaseTextMuted, StatusMedText, BaseTextFaint)
                val xs = listOf(width * 0.15f, width * 0.38f, width * 0.62f, width * 0.85f)

                xs.forEachIndexed { idx, xCenter ->
                    val bH = height * heights[idx]
                    drawRoundRect(
                        color = colors[idx],
                        topLeft = Offset(xCenter - barWidth / 2f, height - bH),
                        size = Size(barWidth, bH),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text("Morning (2)", fontSize = 11.sp, color = BaseTextHeading, fontWeight = FontWeight.Bold)
            Text("Midday (0)", fontSize = 11.sp, color = BaseTextMuted)
            Text("Evening (1)", fontSize = 11.sp, color = BaseTextHeading)
            Text("Night (0)", fontSize = 11.sp, color = BaseTextMuted)
        }
    }
}

@Composable
private fun FallsTrendChart(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BaseSurface)
            .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TrendingDown, contentDescription = null, tint = StatusHighText, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Falls Incident Trend (Weekly)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
            }
            Text("Tap to drill down →", fontSize = 11.sp, color = StatusHighText, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // AI Insight Line
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AiChipBg)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AiGradientStart, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "AI Safety Insight: Falls decreased by 50% following the installation of sensor floor mats for Margaret Holloway and footwear hazard review.",
                fontSize = 11.sp,
                color = AiChipText,
                lineHeight = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Line canvas
        Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val points = listOf(
                    0.05f to 0.35f, // W34: 2
                    0.25f to 0.70f, // W35: 1
                    0.45f to 0.20f, // W36: 3
                    0.65f to 0.70f, // W37: 1
                    0.85f to 0.90f  // W38: 0
                )

                for (i in 1..3) {
                    val y = height * (i / 4f)
                    drawLine(color = Color(0xFF1E283F), start = Offset(0f, y), end = Offset(width, y), strokeWidth = 1.dp.toPx())
                }

                val path = Path()
                points.forEachIndexed { index, (xRatio, yRatio) ->
                    val x = xRatio * width
                    val y = yRatio * height
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path = path, color = StatusHighText, style = Stroke(width = 3.dp.toPx()))

                points.forEach { (xRatio, yRatio) ->
                    drawCircle(color = StatusHighText, radius = 5.dp.toPx(), center = Offset(xRatio * width, yRatio * height))
                    drawCircle(color = Color.White, radius = 2.dp.toPx(), center = Offset(xRatio * width, yRatio * height))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("W34 (2)", "W35 (1)", "W36 (3)", "W37 (1)", "W38 (0)").forEach { label ->
                Text(label, fontSize = 11.sp, color = BaseTextMuted)
            }
        }
    }
}

@Composable
private fun TrainingExpiryPipelineChart(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BaseSurface)
            .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.School, contentDescription = null, tint = StatusVioletText, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("DBS & Training Expiry Pipeline", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
            }
            Text("Tap to drill down →", fontSize = 11.sp, color = StatusVioletText, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // AI Insight Line
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AiChipBg)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AiGradientStart, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "AI HR Insight: 1 staff member (Daniel Okafor) has expired Moving & Handling module. Practical refresher booked for 12/10/2026.",
                fontSize = 11.sp,
                color = AiChipText,
                lineHeight = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            data class ExpiryItem(val label: String, val ratio: Float, val color: Color, val text: String)
            val buckets = listOf(
                ExpiryItem("Already Expired", 0.25f, StatusCritText, "1 module (Daniel O.)"),
                ExpiryItem("Expiring in ≤30 Days", 0.00f, StatusMedText, "0 modules"),
                ExpiryItem("Expiring in 31 - 60 Days", 0.15f, StatusInfoText, "1 module (Moving & Handling)"),
                ExpiryItem("Expiring in 61 - 90 Days", 0.40f, BaseEmerald300, "3 modules (First Aid & Safe)")
            )

            buckets.forEach { item ->
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(item.label, fontSize = 12.sp, color = BaseTextMuted)
                        Text(item.text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = item.color)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { item.ratio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = item.color,
                        trackColor = Color(0xFF1E283F)
                    )
                }
            }
        }
    }
}

@Composable
private fun BranchMarginFinancialChart(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BaseSurface)
            .border(1.dp, BaseBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = BaseEmerald300, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Revenue vs Direct Carer Cost", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
            }
            Text("Tap to drill down →", fontSize = 11.sp, color = BaseEmerald300, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // AI Insight Line
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AiChipBg)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AiGradientStart, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "AI Finance Insight: Camden branch generates top margin (34.4%) due to dense urban route clusters; York rural mileage reduces margin to 29.2%.",
                fontSize = 11.sp,
                color = AiChipText,
                lineHeight = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val branchesPnl = listOf(
                Triple("Camden & Islington", "Rev: £64.0k | Cost: £42.0k", 0.65f to "34.4% Margin"),
                Triple("York & North Yorkshire", "Rev: £48.0k | Cost: £34.0k", 0.70f to "29.2% Margin"),
                Triple("Bradford & Airedale", "Rev: £30.8k | Cost: £22.4k", 0.72f to "27.3% Margin")
            )

            branchesPnl.forEach { (branchName, numbers, pair) ->
                val (ratio, marginText) = pair
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(branchName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BaseTextHeading)
                        Text(marginText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BaseEmerald300)
                    }
                    Text(numbers, fontSize = 11.sp, color = BaseTextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { 1f - ratio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = BaseEmerald500,
                        trackColor = Color(0xFF1E283F)
                    )
                }
            }
        }
    }
}
