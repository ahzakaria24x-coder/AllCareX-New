package com.example.model

import java.util.UUID

enum class UserRole(val displayName: String, val badgeColor: Long) {
    OWNER_ADMIN("Owner / Admin", 0xFF6366F1),
    REGISTERED_MANAGER("Registered Manager", 0xFF00D09C),
    COORDINATOR("Care Coordinator", 0xFF0284C7),
    SENIOR_CARER("Senior Carer / Lead", 0xFF8B5CF6),
    CARE_WORKER("Care Worker", 0xFF10B981),
    NURSE("Registered Nurse", 0xFFEC4899),
    HR_RECRUITER("HR / Recruiter", 0xFFF59E0B),
    FINANCE("Finance Manager", 0xFF14B8A6),
    FAMILY_MEMBER("Family Member (Portal)", 0xFF3B82F6),
    EXTERNAL_AUDITOR("External Auditor (Read-Only)", 0xFF64748B);

    fun isOfficeRole(): Boolean = this in listOf(
        OWNER_ADMIN, REGISTERED_MANAGER, COORDINATOR, HR_RECRUITER, FINANCE, EXTERNAL_AUDITOR
    )

    fun canApproveCarePlan(): Boolean = this in listOf(
        OWNER_ADMIN, REGISTERED_MANAGER, SENIOR_CARER, NURSE
    )

    fun canAdministerControlledDrugs(): Boolean = this in listOf(
        NURSE, SENIOR_CARER, REGISTERED_MANAGER, CARE_WORKER
    )

    fun canAssignVisits(): Boolean = this in listOf(
        OWNER_ADMIN, REGISTERED_MANAGER, COORDINATOR
    )

    fun canManageWorkforce(): Boolean = this in listOf(
        OWNER_ADMIN, REGISTERED_MANAGER, HR_RECRUITER
    )
}

data class AppUser(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val branchId: String,
    val organisationId: String,
    val avatarInitials: String = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
)

data class Branch(
    val id: String,
    val name: String,
    val code: String,
    val address: String,
    val phone: String,
    val activeClientsCount: Int,
    val activeStaffCount: Int
)

data class OrganisationSettings(
    val id: String = "org-allcarex-01",
    val name: String = "All Care X UK",
    val registrationNumber: String = "1-102938475",
    val regulator: RegulatorType = RegulatorType.CQC_ENGLAND,
    val primaryColorHex: Long = 0xFF00D09C,
    val darkNavyHex: Long = 0xFF0E1726,
    val emergencyContact: String = "0800 242 7399",
    val retentionPeriodYears: Int = 8
)

enum class RegulatorType(val displayName: String, val region: String) {
    CQC_ENGLAND("CQC (Care Quality Commission)", "England"),
    CARE_INSPECTORATE_SCOTLAND("Care Inspectorate", "Scotland"),
    CIW_WALES("Care Inspectorate Wales (CIW)", "Wales"),
    RQIA_NI("RQIA", "Northern Ireland")
}

enum class FundingType(val displayName: String) {
    LOCAL_AUTHORITY("Local Authority Funded"),
    PRIVATE_SELF_FUNDED("Private / Self-Funded"),
    NHS_CHC("NHS Continuing Healthcare (CHC)"),
    DIRECT_PAYMENTS("Direct Payments")
}

enum class CareType(val displayName: String) {
    DOMICILIARY("Domiciliary / Home Care"),
    SUPPORTED_LIVING("Supported Living"),
    RESIDENTIAL_NURSING("Residential / Nursing Home")
}

enum class RiskLevel(val displayName: String, val colorHex: Long) {
    LOW("Low Risk", 0xFF10B981),
    MEDIUM("Medium Risk", 0xFFF59E0B),
    HIGH("High Risk", 0xFFEF4444),
    CRITICAL("Critical Risk", 0xFFDC2626)
}

enum class MentalCapacityStatus(val displayName: String) {
    CAPACITY_CONFIRMED("Mental Capacity Confirmed"),
    BEST_INTERESTS("Best Interests Decision (MCA 2005)"),
    DOLS_AUTHORISED("DoLS Authorised / Court of Protection")
}

data class ClientProfile(
    val id: String,
    val organisationId: String,
    val branchId: String,
    val fullName: String,
    val preferredName: String,
    val dateOfBirth: String, // dd/MM/yyyy
    val nhsNumber: String, // e.g. 485 777 3456
    val address: String,
    val postcode: String,
    val phone: String,
    val keySafeCode: String,
    val gpSurgery: String,
    val gpName: String,
    val nextOfKinName: String,
    val nextOfKinRelationship: String,
    val nextOfKinPhone: String,
    val fundingType: FundingType,
    val careType: CareType,
    val riskLevel: RiskLevel,
    val allergies: List<String>,
    val consentGiven: Boolean,
    val dnacprRespectStatus: Boolean, // Resuscitation / ReSPECT flag
    val mentalCapacityStatus: MentalCapacityStatus,
    val requiredCarersCount: Int = 1, // 1 or 2 carers
    val preferredCarerIds: List<String> = emptyList(),
    val notesSummary: String = ""
)

enum class CarePlanStatus {
    DRAFT,
    REVIEWED,
    APPROVED
}

data class CarePlanVersion(
    val id: String = UUID.randomUUID().toString(),
    val clientId: String,
    val versionNumber: Int,
    val status: CarePlanStatus,
    val title: String,
    val summary: String,
    val personalOutcomes: List<String>,
    val nutritionHydrationPlan: String,
    val mobilityMovingHandlingPlan: String,
    val medicationSupportPlan: String,
    val communicationNeeds: String,
    val emergencyContingency: String,
    val isAiDraft: Boolean = false,
    val createdBy: String,
    val createdDate: String, // dd/MM/yyyy
    val approvedBy: String? = null,
    val approvedDate: String? = null,
    val nextReviewDate: String // dd/MM/yyyy
)

data class News2Assessment(
    val id: String = UUID.randomUUID().toString(),
    val clientId: String,
    val assessedDate: String,
    val respirationRate: Int,
    val oxygenSaturation: Int,
    val systolicBp: Int,
    val pulseRate: Int,
    val consciousnessAcvpu: String, // Alert, CVPU
    val temperature: Float,
    val totalScore: Int,
    val riskBand: String, // Low, Low-Med, Med, High
    val notes: String
)

data class WaterlowAssessment(
    val id: String = UUID.randomUUID().toString(),
    val clientId: String,
    val assessedDate: String,
    val buildWeight: Int,
    val skinType: Int,
    val sexAge: Int,
    val continence: Int,
    val mobility: Int,
    val appetite: Int,
    val specialRisks: Int,
    val totalScore: Int,
    val riskCategory: String // Low (10+), High (15+), Very High (20+)
)

data class BodyMapMark(
    val id: String = UUID.randomUUID().toString(),
    val clientId: String,
    val visitId: String? = null,
    val isFront: Boolean,
    val xNormalized: Float, // 0.0 to 1.0
    val yNormalized: Float, // 0.0 to 1.0
    val markType: String, // Bruise, Pressure Sore, Rash, Skin Tear, Wound
    val severity: String, // Mild, Moderate, Severe
    val description: String,
    val dateLogged: String,
    val recordedBy: String
)

enum class VisitStatus(val displayName: String, val colorHex: Long) {
    SCHEDULED("Scheduled", 0xFF64748B),
    EN_ROUTE("En Route", 0xFF0284C7),
    CHECKED_IN("In Progress", 0xFF10B981),
    COMPLETED("Completed", 0xFF059669),
    RUNNING_LATE("Running Late", 0xFFF59E0B),
    MISSED("Missed / Alert", 0xFFEF4444)
}

data class VisitTask(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String, // Personal Care, Medication, Meal Prep, Mobility, Fluid Check
    val isMandatory: Boolean = true,
    var isCompleted: Boolean = false,
    var notes: String = "",
    val exceptionReason: String? = null // Reason if mandatory task could not be completed
)

data class Visit(
    val id: String,
    val organisationId: String,
    val branchId: String,
    val clientId: String,
    val clientName: String,
    val clientAddress: String,
    val clientPostcode: String,
    val carerId: String?,
    val carerName: String?,
    val secondCarerId: String? = null,
    val secondCarerName: String? = null,
    val date: String, // dd/MM/yyyy
    val startTime: String, // HH:mm (24h)
    val endTime: String, // HH:mm (24h)
    val visitType: String, // Morning, Lunch, Tea, Bed, Waking Night
    val status: VisitStatus,
    val tasks: List<VisitTask>,
    val checkInTime: String? = null,
    val checkOutTime: String? = null,
    val checkInGps: String? = null, // e.g. "51.5074° N, 0.1278° W (Verified ±8m)"
    val checkOutGps: String? = null,
    val dailyNotes: String? = null,
    val fluidIntakeMl: Int? = null,
    val moodObservation: String? = null,
    val travelTimeMinutes: Int = 15,
    val requiredSkills: List<String> = listOf("Moving & Handling", "Medication Admin"),
    val isEmergencyAlertRaised: Boolean = false,
    val alerts: List<String> = emptyList(), // Red banner alerts e.g. "Care-plan change: Mobilises with frame", "Medication change: Warfarin 3mg", "Risk flag: High fall risk"
    val keySafeCode: String? = null,
    val allergies: List<String> = emptyList(),
    val dnacprStatus: Boolean = false,
    val clientPreferences: String? = null,
    val nextVisitNotes: String? = null,
    val photoEvidenceUris: List<String> = emptyList(),
    val checkInOverrideReason: String? = null, // Manual reason if GPS fails
    val isLateNotified: Boolean = false,
    val lateDelayMinutes: Int = 0
) {
    val scheduledStartTime: String get() = startTime
    val scheduledEndTime: String get() = endTime
    val address: String get() = clientAddress
    val postCode: String get() = clientPostcode
    val careType: CareType get() = CareType.DOMICILIARY
}

enum class AdministrationOutcome(val code: String, val displayName: String, val colorHex: Long) {
    ADMINISTERED("G", "Administered (Given)", 0xFF10B981),
    GIVEN("G", "Administered (Given)", 0xFF10B981),
    REFUSED("R", "Refused by Client", 0xFFEF4444),
    MISSED("M", "Missed / Unavailable", 0xFFDC2626),
    NOT_REQUIRED("NR", "Not Required (PRN)", 0xFF64748B),
    OMITTED_HOSPITAL("H", "Omitted (Hospital/Clinical)", 0xFFF59E0B),
    SLEEPING("S", "Asleep / Inadvisable", 0xFF8B5CF6)
}

data class Medication(
    val id: String,
    val organisationId: String,
    val branchId: String,
    val clientId: String,
    val name: String,
    val dose: String,
    val route: String, // Oral, Topical, Inhaled, Sublingual, Eye Drops
    val frequencyDescription: String,
    val scheduledTimes: List<String>, // e.g. ["08:00", "13:00", "18:00"]
    val isPrn: Boolean = false, // Pro Re Nata (As Required)
    val prnProtocol: String? = null, // e.g. "Take 1-2 tablets for pain. Max 8 tablets in 24h. Min 4h gap."
    val isControlledDrug: Boolean = false, // Schedule 2/3 requires double signature!
    val prescriber: String,
    val startDate: String,
    val stopDate: String? = null,
    val currentStock: Int,
    val warningThresholdStock: Int = 10,
    val specialInstructions: String = "",
    val nhsDmdCode: String = "dmd-991203"
)

data class MedicationAdministrationRecord(
    val id: String = UUID.randomUUID().toString(),
    val medicationId: String,
    val medicationName: String,
    val clientId: String,
    val scheduledDate: String, // dd/MM/yyyy
    val scheduledTime: String, // HH:mm
    val actualTime: String? = null,
    val outcome: AdministrationOutcome,
    val reasonCodeDescription: String? = null,
    val administeredByCarerId: String,
    val administeredByCarerName: String,
    val witnessCarerId: String? = null, // for controlled drugs double sign
    val witnessCarerName: String? = null,
    val notes: String = "",
    val doseGiven: String
)

enum class IncidentCategory(val displayName: String) {
    FALL("Slip, Trip or Fall"),
    MEDICATION_ERROR("Medication Administration Error"),
    CLINICAL_DETERIORATION("Acute Clinical Deterioration"),
    SAFEGUARDING_CONCERN("Safeguarding Vulnerable Adult"),
    ABSCONDING("Unexplained Absence / Missing Person"),
    PHYSICAL_VERBAL_AGGRESSION("Challenging Behaviour / Aggression"),
    PRESSURE_INJURY("New Pressure Sore / Skin Damage"),
    COMPLAINT("Client / Family Complaint")
}

enum class IncidentSeverity(val displayName: String, val colorHex: Long) {
    MINOR("Minor (No injury)", 0xFF10B981),
    LOW("Low", 0xFF10B981),
    MODERATE("Moderate (First aid / GP contact)", 0xFFF59E0B),
    MEDIUM("Medium", 0xFFF59E0B),
    SEVERE("Severe (A&E / Paramedics)", 0xFFEF4444),
    HIGH("High", 0xFFEF4444),
    CRITICAL("Critical (Life-threatening / Major safeguarding)", 0xFF7F1D1D)
}

data class IncidentReport(
    val id: String = UUID.randomUUID().toString(),
    val organisationId: String = "org-allcarex-01",
    val branchId: String = "branch-cmd",
    val clientId: String,
    val clientName: String = "",
    val reportedByStaffId: String,
    val reportedByStaffName: String,
    val incidentDate: String = "03/10/2026", // dd/MM/yyyy
    val incidentTime: String = "10:00", // HH:mm
    val category: IncidentCategory,
    val severity: IncidentSeverity,
    val location: String = "Client Home",
    val description: String,
    val immediateActionsTaken: String = "",
    val paramedicsCalled: Boolean = false,
    val isCqcNotifiable: Boolean = false, // Reg 18 Notification
    val isLocalAuthoritySafeguardingReferral: Boolean = false,
    val safeguardingReferenceNumber: String? = null,
    val dutyOfCandourCompleted: Boolean = false, // Informs next of kin in writing
    val dutyOfCandourNotes: String? = null,
    val investigationStatus: String = "Actioned", // Open, Under Investigation, Actioned, Closed
    val lessonsLearned: String? = null
) {
    val timestamp: String get() = "$incidentDate $incidentTime"
    val status: String get() = investigationStatus
    val immediateActionTaken: String get() = immediateActionsTaken
}

typealias IncidentRecord = IncidentReport

enum class ContractType(val displayName: String) {
    ZERO_HOUR("Zero-Hour Flexible"),
    GUARANTEED_HOURS("Guaranteed Hours"),
    FULL_TIME("Full-Time Permanent (37.5h)"),
    PART_TIME("Part-Time (20h)")
}

data class StaffProfile(
    val id: String,
    val organisationId: String = "org-allcarex-01",
    val branchId: String = "branch-cmd",
    val fullName: String,
    val email: String,
    val phone: String,
    val role: UserRole,
    val contractType: ContractType = ContractType.FULL_TIME,
    val rightToWorkVerified: Boolean = true,
    val rightToWorkExpiry: String = "01/01/2028", // dd/MM/yyyy
    val dbsCertificateNumber: String = "DBS-99120",
    val dbsEnhancedStatus: Boolean = true,
    val dbsIssueDate: String = "01/01/2024",
    val dbsExpiryDate: String = "01/01/2027",
    val dbsUpdateServiceRegistered: Boolean = true,
    val careCertificateCompleted: Boolean = true,
    val compliancePercentage: Int = 100,
    val lastSupervisionDate: String = "01/08/2026",
    val nextSupervisionDueDate: String = "01/11/2026",
    val annualAppraisalDueDate: String = "01/10/2027",
    val skills: List<String> = listOf("Moving & Handling", "Medication Admin"),
    val hoursWorkedThisWeek: Float = 37.5f,
    val isAvailable: Boolean = true
) {
    val name: String get() = fullName
    val isOnLeave: Boolean get() = !isAvailable

    fun hasExpiredChecks(): Boolean {
        // Simple flag logic for demo
        return compliancePercentage < 75
    }
}

data class TrainingRecord(
    val id: String = UUID.randomUUID().toString(),
    val staffId: String,
    val moduleName: String,
    val isMandatory: Boolean = true,
    val completedDate: String,
    val expiryDate: String,
    val scorePercentage: Int,
    val certificateRef: String,
    val isExpired: Boolean
)

data class AuditRecord(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: String,
    val performedBy: String,
    val userRole: UserRole,
    val actionType: String, // VIEW_CLIENT, EDIT_CARE_PLAN, ADMINISTER_MED, GENERATE_AI_DRAFT, INCIDENT_LOGGED
    val targetRecord: String,
    val branchId: String,
    val notes: String,
    val organisationId: String = "org-allcarex-01",
    val ipAddress: String = "10.0.4.12 (TLS 1.3)",
    val hashSha256: String = "",
    val previousHash: String = ""
)

data class QueuedOfflineAction(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: String,
    val actionType: String, // VISIT_CHECK_IN, VISIT_CHECK_OUT, MAR_SIGN, DAILY_NOTE, INCIDENT
    val payloadSummary: String,
    val isSynced: Boolean = false
)

data class FamilyMessage(
    val id: String = UUID.randomUUID().toString(),
    val clientId: String,
    val senderName: String,
    val senderRole: String, // "Family (Daughter)" or "Care Coordinator"
    val timestamp: String,
    val messageText: String
)

// ==================== RECRUITMENT MODELS ====================
enum class CandidateStage(val displayName: String, val colorHex: Long) {
    APPLIED("Applied", 0xFF64748B),
    SHORTLISTED("Shortlisted", 0xFF0284C7),
    INTERVIEWED("Interviewed", 0xFF8B5CF6),
    CHECKS_PENDING("Vetting & DBS Checks", 0xFFF59E0B),
    OFFER_MADE("Offer Letter Sent", 0xFF06B6D4),
    OFFER_ACCEPTED("Offer Accepted", 0xFF10B981),
    ONBOARDED("Onboarded & Staff Profile Created", 0xFF00D09C),
    REJECTED("Rejected", 0xFFEF4444)
}

data class Vacancy(
    val id: String,
    val title: String,
    val branchId: String,
    val roleType: UserRole,
    val contractType: ContractType,
    val hourlyRate: Double,
    val description: String,
    val isOpen: Boolean = true,
    val applicantsCount: Int = 0
)

data class InterviewScore(
    val valuesScore: Int = 4, // 1 to 5
    val movingHandlingScore: Int = 4,
    val communicationScore: Int = 5,
    val scenarioScore: Int = 4,
    val interviewerNotes: String = "",
    val recommendation: String = "Proceed with Offer" // Strong Yes, Proceed with Offer, Hold, Reject
) {
    val totalScore: Int get() = valuesScore + movingHandlingScore + communicationScore + scenarioScore
}

data class DbsTracking(
    val applicationRef: String = "E-BULK-" + UUID.randomUUID().toString().take(8).uppercase(),
    val stage: String = "Submitted to DBS", // Draft, Submitted to DBS, Barred List Clear, Certificate Dispatched
    val certificateNumber: String? = null,
    val isComplete: Boolean = false
)

data class RightToWorkCheck(
    val verified: Boolean = true,
    val shareCodeOrDoc: String = "UK Passport / Home Office Share Code Verified",
    val expiryDate: String = "31/12/2029"
)

data class ReferenceChecks(
    val referee1Name: String,
    val referee1Status: String = "Verified", // Requested, Received, Verified
    val referee2Name: String,
    val referee2Status: String = "Verified"
)

data class OnboardingChecklist(
    var contractSigned: Boolean = false,
    var uniformIssued: Boolean = false,
    var shadowShiftsBooked: Boolean = false,
    var idBadgePrinted: Boolean = false,
    var mandatoryTrainingEnrolled: Boolean = false
) {
    fun isReadyForStaffProfile(): Boolean = contractSigned && uniformIssued && shadowShiftsBooked && idBadgePrinted && mandatoryTrainingEnrolled
    fun completedCount(): Int = listOf(contractSigned, uniformIssued, shadowShiftsBooked, idBadgePrinted, mandatoryTrainingEnrolled).count { it }
}

data class Candidate(
    val id: String,
    val vacancyId: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val appliedDate: String,
    var stage: CandidateStage,
    var interviewScore: InterviewScore = InterviewScore(),
    var dbsTracking: DbsTracking = DbsTracking(),
    var rightToWork: RightToWorkCheck = RightToWorkCheck(),
    var references: ReferenceChecks = ReferenceChecks("Previous Care Home Mgr", "Verified", "Clinical Supervisor", "Verified"),
    var onboarding: OnboardingChecklist = OnboardingChecklist(),
    val offerHourlyRate: Double = 13.50,
    val cvSummary: String = "3 years domiciliary experience in London. NVQ Level 2 in Health & Social Care."
)

// ==================== PAYROLL & INVOICING MODELS ====================

enum class EntryType(val displayName: String) {
    VISIT("Delivered Client Visit"),
    TRAINING("Mandatory Training / Workshop"),
    SHADOWING("Shadow Shift with Senior"),
    OFFICE_HOURS("Coordination & Office Admin"),
    SLEEP_IN("Overnight Sleep-In Duty")
}

data class PayRulesConfig(
    val organisationId: String = "org-allcarex-01",
    val baseHourlyRate: Double = 13.50,
    val weekendUpliftPerHour: Double = 2.00,
    val nightUpliftPerHour: Double = 2.50,
    val bankHolidayMultiplier: Double = 1.5,
    val travelTimeRatePerHour: Double = 12.00,
    val mileageRatePerMile: Double = 0.45, // HMRC standard UK rate
    val sleepInFlatRate: Double = 55.00,
    val wakingNightHourlyRate: Double = 16.50,
    val trainingHourlyRate: Double = 12.00,
    val holidayAccrualPercentage: Double = 12.07, // UK irregular hours statutory accrual
    val nationalLivingWageRate: Double = 11.44 // Current UK statutory NLW for 21+
)

data class TimesheetEntry(
    val id: String = UUID.randomUUID().toString(),
    val staffId: String,
    val staffName: String,
    val entryType: EntryType = EntryType.VISIT,
    val visitId: String? = null,
    val clientName: String,
    val date: String,
    val scheduledHours: Double,
    val actualDeliveredHours: Double,
    val differenceMinutes: Int = 0, // e.g. +7m
    val hourlyPayRate: Double,
    val grossPay: Double,
    val travelTimeMinutes: Int = 0,
    val travelPay: Double = 0.0,
    val mileageMiles: Double = 0.0,
    val mileagePay: Double = 0.0, // @ £0.45/mile
    val holidayPayAccrued: Double = 0.0, // @ 12.07%
    val isDisputed: Boolean = false,
    val disputeReason: String? = null,
    val isNlwCompliant: Boolean = true,
    val nlwShortfall: Double = 0.0,
    val shortfallReason: String? = null,
    val complianceFlags: List<String> = emptyList()
) {
    val totalPayable: Double get() = grossPay + travelPay + mileagePay
    val effectiveHourlyRate: Double
        get() {
            val totalTimeHours = actualDeliveredHours + (travelTimeMinutes / 60.0)
            return if (totalTimeHours > 0) totalPayable / totalTimeHours else hourlyPayRate
        }
}

enum class PayRunStatus(val displayName: String, val colorHex: Long) {
    DRAFT("Draft", 0xFF64748B),
    MANAGER_REVIEW("Manager Review", 0xFF0284C7),
    APPROVED("Approved by Finance", 0xFF10B981),
    EXPORTED("Exported to Payroll", 0xFF00D09C),
    LOCKED("Locked / Closed", 0xFF6366F1)
}

data class PayAdjustment(
    val id: String = UUID.randomUUID().toString(),
    val staffId: String,
    val staffName: String,
    val periodRef: String,
    val amount: Double,
    val reason: String,
    val authorisedBy: String
)

data class PayRun(
    val id: String = "pr-" + UUID.randomUUID().toString().take(6),
    val periodName: String, // e.g. "October 2026 - Week 40"
    val startDate: String,
    val endDate: String,
    val totalStaffCount: Int,
    val totalDeliveredHours: Double,
    val grossPayTotal: Double,
    val travelPayTotal: Double,
    val mileagePayTotal: Double,
    val holidayAccruedTotal: Double,
    val totalPayrollCost: Double,
    var status: PayRunStatus = PayRunStatus.DRAFT,
    val exportedFormat: String? = null,
    val approvedBy: String? = null,
    val lockedAt: String? = null,
    val adjustments: List<PayAdjustment> = emptyList()
)

data class HolidayRecord(
    val id: String = UUID.randomUUID().toString(),
    val staffId: String,
    val staffName: String,
    val startDate: String,
    val endDate: String,
    val hoursRequested: Double,
    var status: String = "Approved", // Pending, Approved, Rejected
    val hasRotaClash: Boolean = false,
    val notes: String = ""
)

data class StaffHolidayBalance(
    val staffId: String,
    val staffName: String,
    val totalWorkedHours: Double,
    val accruedHours: Double, // 12.07%
    val takenHours: Double,
    val remainingBalanceHours: Double
)

data class SicknessRecord(
    val id: String = UUID.randomUUID().toString(),
    val staffId: String,
    val staffName: String,
    val startDate: String,
    val endDate: String,
    val qualifyingDays: Int,
    val isSspEligible: Boolean = true, // SSP statutory flag
    val sspNotes: String = "Qualifies for Statutory Sick Pay (>4 consecutive days)",
    val fitNoteRef: String? = null,
    val returnToWorkCompleted: Boolean = true,
    val returnToWorkNotes: String? = "Return to work interview completed with registered manager."
)

data class ClientFundingRule(
    val clientId: String,
    val primarySource: FundingType,
    val councilHourlyRate: Double = 24.00,
    val nhsChcHourlyRate: Double = 38.00,
    val privateHourlyRate: Double = 28.00,
    val councilSplitPercent: Double = 80.0,
    val privateSplitPercent: Double = 20.0,
    val nhsSplitPercent: Double = 0.0,
    val purchaseOrderNumber: String = "PO-LBH-2026-994",
    val roundingRule: String = "Exact Minutes",
    val billCommissionedOrActual: String = "Actual Delivered"
)

data class InvoiceLineItem(
    val id: String = UUID.randomUUID().toString(),
    val visitId: String? = null,
    val date: String,
    val description: String,
    val scheduledMinutes: Int,
    val deliveredMinutes: Int,
    val billedMinutes: Int,
    val hourlyRate: Double,
    val totalAmount: Double,
    val laPortion: Double,
    val nhsPortion: Double,
    val privatePortion: Double,
    val isDisputed: Boolean = false,
    val disputeReason: String? = null
)

data class InvoicePayment(
    val id: String = UUID.randomUUID().toString(),
    val invoiceId: String,
    val paymentDate: String,
    val amount: Double,
    val paymentMethod: String, // BACS Transfer, Direct Debit, Card, Local Authority Remittance
    val reference: String
)

data class CreditNote(
    val id: String = UUID.randomUUID().toString(),
    val invoiceId: String,
    val creditNoteNumber: String,
    val date: String,
    val amount: Double,
    val reason: String
)

data class InvoiceSplit(
    val localAuthorityRate: Double, // £ per hour
    val localAuthorityTotal: Double,
    val nhsChcRate: Double,
    val nhsChcTotal: Double,
    val privateClientRate: Double,
    val privateClientTotal: Double
)

enum class InvoiceStatus(val displayName: String, val colorHex: Long) {
    DRAFT("Draft", 0xFF64748B),
    APPROVED("Approved by Coordinator", 0xFF0284C7),
    SENT("Dispatched to Payer", 0xFF00D09C),
    PART_PAID("Part-Paid", 0xFFF59E0B),
    PAID("Paid in Full", 0xFF10B981),
    OVERDUE("Overdue (60+ Days)", 0xFFEF4444)
}

data class ClientInvoice(
    val id: String,
    val invoiceNumber: String,
    val clientId: String,
    val clientName: String,
    val billingPeriod: String, // e.g. "24/09/2026 - 30/09/2026"
    val totalVisitsDelivered: Int,
    val totalHoursDelivered: Double,
    val grossTotal: Double,
    val splits: InvoiceSplit,
    var status: InvoiceStatus = InvoiceStatus.APPROVED,
    val generatedDate: String,
    val dueDate: String,
    val lines: List<InvoiceLineItem> = emptyList(),
    val payments: List<InvoicePayment> = emptyList(),
    val creditNotes: List<CreditNote> = emptyList(),
    val outstandingBalance: Double = grossTotal,
    val agingBucket: String = "0-30 days", // 0-30 days, 31-60 days, 61+ days
    val carerWageCostTotal: Double = grossTotal * 0.52, // for margin calculation
    val vatExemptionNotice: String = "Exempt from UK VAT under VATA 1994, Schedule 9, Group 7 (Health and Welfare Services)"
) {
    val grossMarginPounds: Double get() = grossTotal - carerWageCostTotal
    val grossMarginPercent: Double get() = if (grossTotal > 0) (grossMarginPounds / grossTotal) * 100.0 else 0.0
}

// ==================== QUALITY, GOVERNANCE & COMPLIANCE MODELS ====================

data class PolicyVersionEntry(
    val version: String,
    val changeDate: String,
    val changeSummary: String,
    val author: String
)

data class PolicyDocument(
    val id: String,
    val title: String,
    val category: String, // Clinical Governance, Human Resources, Health & Safety, Safeguarding
    val version: String,
    val owner: String = "Eleanor Vance (Registered Manager)",
    val lastReviewedDate: String,
    val nextReviewDate: String,
    val isOverdue: Boolean = false,
    val approvedBy: String,
    val staffAcknowledgedCount: Int,
    val totalStaffCount: Int = 12,
    val summaryContent: String,
    val linkedStatementCodes: List<String> = listOf("SAFE-01", "WELL-01"),
    val versionHistory: List<PolicyVersionEntry> = emptyList()
)

data class AuditCheckItem(
    val id: String = UUID.randomUUID().toString(),
    val category: String,
    val question: String,
    var passed: Boolean = true,
    var notes: String = "",
    val riskIfFailed: String = "Regulatory non-compliance"
)

data class AuditActionItem(
    val id: String = UUID.randomUUID().toString(),
    val auditTemplateId: String,
    val title: String,
    val issueIdentified: String,
    val smartActionRequired: String,
    val ownerName: String,
    val targetDate: String,
    var status: String = "In Progress", // Open, In Progress, Resolved, Overdue
    val completionNotes: String? = null
)

enum class AuditType(val displayName: String) {
    MEDICATION("Medication Administration & Controlled Drugs"),
    CARE_PLAN("Care Plans & Person-Centred Reviews"),
    INFECTION_CONTROL("Infection Prevention & Control (IPC)"),
    HEALTH_SAFETY("Health, Safety & Environmental Risk"),
    HANDOVER("Shift Handover & Call Monitoring"),
    MEALTIME_DINING("Nutrition, Hydration & MUST Monitoring"),
    FINANCE("Client Cash, BACS & Billing Accuracy")
}

data class AuditTemplate(
    val id: String,
    val title: String,
    val auditType: AuditType = AuditType.MEDICATION,
    val frequency: String, // Monthly, Quarterly, Bi-Annual, Weekly
    val lastConductedDate: String,
    val nextDueDate: String = "15/10/2026",
    val auditorName: String,
    val overallRagStatus: String, // GREEN, AMBER, RED
    val overallScorePercent: Double = 92.0,
    val checkItems: List<AuditCheckItem> = emptyList(),
    val actionItems: List<AuditActionItem> = emptyList(),
    val historicalScores: List<Pair<String, Double>> = listOf("Aug" to 88.0, "Sep" to 90.0, "Oct" to 92.0)
)

data class ComplaintCompliment(
    val id: String = UUID.randomUUID().toString(),
    val isCompliment: Boolean,
    val clientName: String,
    val dateLogged: String,
    val category: String, // Punctuality, Dignity/Respect, Communication, Medication, Staff Attitude
    val description: String,
    val investigationDetails: String? = null,
    val outcomeNotes: String? = null,
    val responseLetterSentDate: String? = null,
    val dutyOfCandourApplies: Boolean = false,
    var dutyOfCandourSatisfied: Boolean = false,
    val dutyOfCandourLetterRef: String? = null,
    var status: String = "Resolved", // Open, Under Investigation, Resolved
    val satisfactionRating: Int? = 5,
    val resolutionDate: String? = null,
    val lessonsLearned: String? = null
)

data class SatisfactionSurveyResult(
    val id: String = UUID.randomUUID().toString(),
    val respondentGroup: String, // Clients, Family Members, Staff
    val title: String,
    val surveyPeriod: String, // e.g. "Q3 2026 Annual Survey"
    val respondentsCount: Int,
    val overallSatisfactionPercent: Double,
    val keyFindings: List<String>,
    val actionsTriggered: List<String>
)

data class RegulatorNotification(
    val id: String = UUID.randomUUID().toString(),
    val incidentId: String? = null,
    val notificationType: String, // CQC Serious Injury, CQC Deprivation of Liberty, LA Safeguarding s42 Referral, RIDDOR
    val clientOrStaffName: String,
    val submissionDate: String,
    val submissionReference: String,
    val statutoryDeadline: String,
    var status: String = "Acknowledged by CQC", // Draft, Submitted, Acknowledged by CQC, Closed
    val summaryDetails: String
)

data class GovernanceMeeting(
    val id: String = UUID.randomUUID().toString(),
    val meetingType: String, // Clinical Governance, RM Operations Review, Safeguarding Board
    val date: String,
    val chairName: String,
    val attendees: List<String>,
    val minutesSummary: String,
    val keyDecisions: List<String>
)

data class RiskRegisterItem(
    val id: String = UUID.randomUUID().toString(),
    val riskCategory: String, // Clinical Care, Staffing, Information Governance, Health & Safety, Financial
    val description: String,
    val likelihood: Int, // 1 to 5
    val impact: Int, // 1 to 5
    val mitigations: String,
    val riskOwner: String,
    val reviewDate: String
) {
    val riskScore: Int get() = likelihood * impact
    val riskLevel: String get() = when {
        riskScore >= 15 -> "HIGH (RED)"
        riskScore >= 8 -> "MEDIUM (AMBER)"
        else -> "LOW (GREEN)"
    }
}

data class EmergencyContact(
    val role: String,
    val name: String,
    val phone: String,
    val alternatePhone: String,
    val escalationTier: Int
)

data class BusinessContinuityPlan(
    val id: String = "bcp-01",
    val title: String = "All Care X Business Continuity & Disaster Recovery Plan",
    val version: String = "v3.2",
    val lastTestedDate: String = "15/09/2026",
    val keyRisksCovered: List<String> = listOf("Severe Weather / Snow Flooding", "IT System Outage / Cyber Incident", "Fuel Shortage / Transport Disruption", "Flu / Pandemic Surge Staff Shortage"),
    val emergencyContactTree: List<EmergencyContact> = emptyList()
)

data class YouSaidWeDidItem(
    val id: String = UUID.randomUUID().toString(),
    val feedbackSource: String, // Family Forum, Carer Council, Client Survey
    val youSaid: String,
    val weDid: String,
    val dateImplemented: String
)

// Dynamic & Versionable Regulator Assessment Framework Models
enum class EvidenceSourceType(val displayName: String) {
    AUTO_PLATFORM("Platform Live Metric"),
    MANUAL_UPLOAD("Manual Document Upload"),
    AUDIT_RESULT("Internal Audit Outcome"),
    INCIDENT_REVIEW("Incident / Safeguarding Investigation"),
    POLICY_LINK("Active Policy Reference"),
    SURVEY_FEEDBACK("Satisfaction Survey Finding")
}

data class EvidenceItem(
    val id: String = UUID.randomUUID().toString(),
    val statementId: String,
    val title: String,
    val sourceType: EvidenceSourceType = EvidenceSourceType.AUTO_PLATFORM,
    val description: String,
    val dateRecorded: String,
    val freshnessRating: String = "Fresh", // Fresh, Needs Review, Stale
    val metricValue: String? = null,
    val isGap: Boolean = false,
    val gapRemediationPlan: String? = null
)

data class QualityStatementOrKloe(
    val id: String,
    val keyQuestionId: String, // SAFE, EFFECTIVE, CARING, RESPONSIVE, WELL_LED
    val code: String, // e.g. "SAFE-01", "S1"
    val title: String, // e.g. "Learning culture", "Safe environments"
    val description: String,
    val evidenceCategories: List<String> = listOf("People's experience", "Feedback from staff & leaders", "Processes", "Outcomes"),
    val ratingCharacteristics: Map<Int, String> = mapOf(
        1 to "Inadequate: Systemic failures in learning; incidents uninvestigated.",
        2 to "Requires Improvement: Inconsistent incident reporting and delayed lessons learned.",
        3 to "Good: Proactive reporting culture with transparent duty of candour.",
        4 to "Outstanding: Sector-leading safety innovation with zero blame and rapid learning."
    ),
    var currentSelfAssessmentScore: Int = 3, // 1 to 4
    var selfAssessmentNarrative: String = "",
    var selfAssessmentDate: String = "01/10/2026",
    val linkedEvidenceIds: List<String> = emptyList(),
    val openActionIds: List<String> = emptyList()
)

data class KeyQuestion(
    val id: String, // SAFE, EFFECTIVE, CARING, RESPONSIVE, WELL_LED
    val name: String, // Safe, Effective, Caring, Responsive, Well-led
    val iconName: String = "Shield",
    val description: String,
    val order: Int,
    val statements: List<QualityStatementOrKloe> = emptyList()
) {
    val averageScore: Double
        get() = if (statements.isNotEmpty()) statements.map { it.currentSelfAssessmentScore }.average() else 3.0
    val ratingLabel: String
        get() = when {
            averageScore >= 3.5 -> "Outstanding"
            averageScore >= 2.5 -> "Good"
            averageScore >= 1.5 -> "Requires Improvement"
            else -> "Inadequate"
        }
}

data class AssessmentFramework(
    val id: String,
    val name: String, // e.g. "CQC Single Assessment Framework (34 Statements)"
    val regulatorName: String, // CQC, CIW, Care Inspectorate, RQIA
    val version: String, // e.g. "v2.1"
    val effectiveDate: String,
    var isActive: Boolean = true,
    var isArchived: Boolean = false,
    val keyQuestions: List<KeyQuestion> = emptyList()
)

data class AuditorAccessLogEntry(
    val timestamp: String,
    val auditorName: String,
    val sectionAccessed: String,
    val recordReference: String
)

data class InspectionDaySession(
    val id: String = "ins-session-" + UUID.randomUUID().toString().take(6),
    val auditorName: String = "Lead CQC Inspector (London Adult Social Care Directorate)",
    val regulatorName: String = "Care Quality Commission (CQC)",
    val accessExpiresAt: String = "03/10/2026 18:00 (Today)",
    var isSessionActive: Boolean = true,
    val accessLog: List<AuditorAccessLogEntry> = emptyList()
)

data class CqcAssessmentTheme(
    val id: String,
    val keyQuestion: String,
    val qualityStatementTitle: String,
    val statementDescription: String,
    var currentScore: Int,
    var evidenceNotes: String,
    var lastUpdated: String
)

data class StaffDocument(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val type: String, // "Contract", "DBS Certificate", "Right-to-Work", "Care Certificate", "Photo ID"
    val issueDate: String,
    val expiryDate: String? = null,
    val status: String = "Verified", // "Verified", "Expiring Soon", "Pending Review"
    val fileRef: String
)

data class OfficeMessage(
    val id: String = UUID.randomUUID().toString(),
    val senderName: String,
    val senderRole: String,
    val subject: String,
    val messageText: String,
    val timestamp: String,
    val isUrgent: Boolean = false,
    val isRead: Boolean = false
)

data class ManagerNotification(
    val id: String = UUID.randomUUID().toString(),
    val staffId: String = "staff-01",
    val staffName: String = "David Okonkwo",
    val category: String = "General", // "Unavailability", "Safety Concern", "Client Concern", "Mileage Query", "General"
    val details: String = "",
    val timestamp: String = "01/10/2026 09:00",
    val status: String = "Sent", // "Sent", "Acknowledged", "Resolved"
    val type: String = category,
    val subject: String = category,
    val message: String = details,
    val shiftDate: String? = null
)

data class HandoverDraft(
    val id: String = UUID.randomUUID().toString(),
    val date: String,
    val shiftType: String, // "Morning & Lunch Run", "Evening & Bed Run"
    val carerId: String,
    val carerName: String,
    val visitsCount: Int,
    val totalFluidMl: Int,
    val tasksSummary: String,
    val aiGeneratedSummary: String,
    val carerEditedNotes: String = "",
    val redFlags: List<String> = emptyList(),
    val isSubmitted: Boolean = false,
    val submittedAt: String? = null
)

data class LoneWorkerSafetyState(
    val isTimerActive: Boolean = true,
    val remainingSeconds: Int = 2700, // 45 minutes countdown
    val totalSeconds: Int = 2700,
    val lastCheckInTime: String = "08:00",
    val escalationContactName: String = "Sarah Jenkins (On-Call Coordinator)",
    val escalationContactPhone: String = "07700 900332",
    val isEscalated: Boolean = false,
    val isSosTriggered: Boolean = false,
    val lastGpsCoords: String = "51.5074° N, 0.1278° W (Westminster)"
)

// ==================== SECURITY & AUDIT HARDENING MODELS ====================
data class SecurityIssue(
    val id: String,
    val issue: String,
    val severity: String, // CRITICAL, HIGH, MEDIUM, LOW
    val where: String,
    val fix: String,
    val status: String // FIXED, MITIGATED, RECOMMENDED
)

data class PasswordValidationResult(
    val isValid: Boolean,
    val score: Int, // 0 to 100
    val errors: List<String>
)

data class FileValidationResult(
    val isValid: Boolean,
    val fileName: String,
    val mimeType: String,
    val sizeKb: Long,
    val scanStatus: String, // CLEAN, QUARANTINED, PENDING_SCAN
    val errorMessage: String? = null
)

data class SarExportBundle(
    val clientId: String,
    val clientName: String,
    val exportedAt: String,
    val exportedBy: String,
    val personalData: Map<String, String>,
    val carePlanCount: Int,
    val visitCount: Int,
    val medCount: Int,
    val auditLogCount: Int,
    val jsonExportSnippet: String
)

data class SecurityTestResult(
    val testId: String,
    val testName: String,
    val passed: Boolean,
    val category: String, // AUTH, AUTHORISATION, AUDIT, SECRETS, INPUT, DATA_PROTECTION
    val details: String,
    val timestamp: String
)

data class AuditIntegrityResult(
    val isChainValid: Boolean,
    val totalRecordsChecked: Int,
    val lastVerifiedTimestamp: String,
    val rootHash: String,
    val details: String
)
