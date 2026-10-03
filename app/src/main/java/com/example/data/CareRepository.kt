package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class CareRepository {

    // Current Active User & Mode
    private val _currentUser = MutableStateFlow(
        AppUser(
            id = "staff-01",
            name = "Eleanor Vance",
            email = "e.vance@allcarex.co.uk",
            role = UserRole.REGISTERED_MANAGER,
            branchId = "branch-wst",
            organisationId = "org-allcarex-01"
        )
    )
    val currentUser: StateFlow<AppUser> = _currentUser.asStateFlow()

    private val _isCarerMobileMode = MutableStateFlow(false)
    val isCarerMobileMode: StateFlow<Boolean> = _isCarerMobileMode.asStateFlow()

    private val _currentBranchId = MutableStateFlow<String?>("branch-wst")
    val currentBranchId: StateFlow<String?> = _currentBranchId.asStateFlow()

    private val _organisationSettings = MutableStateFlow(OrganisationSettings())
    val organisationSettings: StateFlow<OrganisationSettings> = _organisationSettings.asStateFlow()

    // State collections
    private val _branches = MutableStateFlow(DemoDataProvider.defaultBranches)
    val branches: StateFlow<List<Branch>> = _branches.asStateFlow()

    private val _clients = MutableStateFlow(DemoDataProvider.defaultClients)
    val clients: StateFlow<List<ClientProfile>> = _clients.asStateFlow()

    private val _carePlans = MutableStateFlow(DemoDataProvider.createInitialCarePlans())
    val carePlans: StateFlow<List<CarePlanVersion>> = _carePlans.asStateFlow()

    private val _visits = MutableStateFlow(DemoDataProvider.createInitialVisits())
    val visits: StateFlow<List<Visit>> = _visits.asStateFlow()

    private val _medications = MutableStateFlow(DemoDataProvider.createInitialMedications())
    val medications: StateFlow<List<Medication>> = _medications.asStateFlow()

    private val _marHistory = MutableStateFlow<List<MedicationAdministrationRecord>>(emptyList())
    val marHistory: StateFlow<List<MedicationAdministrationRecord>> = _marHistory.asStateFlow()

    private val _incidents = MutableStateFlow(DemoDataProvider.createInitialIncidents())
    val incidents: StateFlow<List<IncidentReport>> = _incidents.asStateFlow()

    private val _staffList = MutableStateFlow(DemoDataProvider.defaultStaff)
    val staffList: StateFlow<List<StaffProfile>> = _staffList.asStateFlow()

    private val _trainingList = MutableStateFlow(DemoDataProvider.createInitialTraining())
    val trainingList: StateFlow<List<TrainingRecord>> = _trainingList.asStateFlow()

    private val _bodyMapMarks = MutableStateFlow(DemoDataProvider.createInitialBodyMap())
    val bodyMapMarks: StateFlow<List<BodyMapMark>> = _bodyMapMarks.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditRecord>>(listOf(
        AuditRecord(
            timestamp = "03/10/2026 07:45",
            performedBy = "Eleanor Vance (Registered Manager)",
            userRole = UserRole.REGISTERED_MANAGER,
            actionType = "SYSTEM_STARTUP",
            targetRecord = "AuditLog",
            branchId = "branch-wst",
            notes = "System session initialised for Westminster branch."
        )
    ))
    val auditLogs: StateFlow<List<AuditRecord>> = _auditLogs.asStateFlow()

    private val _offlineQueue = MutableStateFlow<List<QueuedOfflineAction>>(emptyList())
    val offlineQueue: StateFlow<List<QueuedOfflineAction>> = _offlineQueue.asStateFlow()

    private val _familyMessages = MutableStateFlow(listOf(
        FamilyMessage(
            clientId = "client-01",
            senderName = "Clara Pendelton",
            senderRole = "Family (Daughter)",
            timestamp = "02/10/2026 18:30",
            messageText = "Hello team, dad sounded cheerful today! Did he drink plenty of fluids this afternoon?"
        ),
        FamilyMessage(
            clientId = "client-01",
            senderName = "Marcus Thorne",
            senderRole = "Care Coordinator",
            timestamp = "02/10/2026 19:10",
            messageText = "Good evening Clara, yes! David recorded 450ml of tea and squash during tea call. He is settled comfortably for the night."
        )
    ))
    val familyMessages: StateFlow<List<FamilyMessage>> = _familyMessages.asStateFlow()

    // Recruitment State
    private val _vacancies = MutableStateFlow(DemoDataProvider.createInitialVacancies())
    val vacancies: StateFlow<List<Vacancy>> = _vacancies.asStateFlow()

    private val _candidates = MutableStateFlow(DemoDataProvider.createInitialCandidates())
    val candidates: StateFlow<List<Candidate>> = _candidates.asStateFlow()

    // Payroll & Invoicing State
    private val _payRules = MutableStateFlow(DemoDataProvider.createInitialPayRules())
    val payRules: StateFlow<PayRulesConfig> = _payRules.asStateFlow()

    private val _payRuns = MutableStateFlow(DemoDataProvider.createInitialPayRuns())
    val payRuns: StateFlow<List<PayRun>> = _payRuns.asStateFlow()

    private val _timesheets = MutableStateFlow(DemoDataProvider.createInitialTimesheets())
    val timesheets: StateFlow<List<TimesheetEntry>> = _timesheets.asStateFlow()

    private val _holidays = MutableStateFlow(DemoDataProvider.createInitialHolidays())
    val holidays: StateFlow<List<HolidayRecord>> = _holidays.asStateFlow()

    private val _holidayBalances = MutableStateFlow(DemoDataProvider.createInitialHolidayBalances())
    val holidayBalances: StateFlow<List<StaffHolidayBalance>> = _holidayBalances.asStateFlow()

    private val _sicknessRecords = MutableStateFlow(DemoDataProvider.createInitialSickness())
    val sicknessRecords: StateFlow<List<SicknessRecord>> = _sicknessRecords.asStateFlow()

    private val _fundingRules = MutableStateFlow(DemoDataProvider.createInitialFundingRules())
    val fundingRules: StateFlow<List<ClientFundingRule>> = _fundingRules.asStateFlow()

    private val _invoices = MutableStateFlow(DemoDataProvider.createInitialInvoices())
    val invoices: StateFlow<List<ClientInvoice>> = _invoices.asStateFlow()

    // Quality & Compliance State
    private val _frameworks = MutableStateFlow(DemoDataProvider.createInitialFrameworks())
    val frameworks: StateFlow<List<AssessmentFramework>> = _frameworks.asStateFlow()

    private val _evidenceItems = MutableStateFlow(DemoDataProvider.createInitialEvidenceItems())
    val evidenceItems: StateFlow<List<EvidenceItem>> = _evidenceItems.asStateFlow()

    private val _policies = MutableStateFlow(DemoDataProvider.createInitialPolicies())
    val policies: StateFlow<List<PolicyDocument>> = _policies.asStateFlow()

    private val _auditTemplates = MutableStateFlow(DemoDataProvider.createInitialAuditTemplates())
    val auditTemplates: StateFlow<List<AuditTemplate>> = _auditTemplates.asStateFlow()

    private val _complaintsCompliments = MutableStateFlow(DemoDataProvider.createInitialComplaints())
    val complaintsCompliments: StateFlow<List<ComplaintCompliment>> = _complaintsCompliments.asStateFlow()

    private val _satisfactionSurveys = MutableStateFlow(DemoDataProvider.createInitialSurveys())
    val satisfactionSurveys: StateFlow<List<SatisfactionSurveyResult>> = _satisfactionSurveys.asStateFlow()

    private val _regulatorNotifications = MutableStateFlow(DemoDataProvider.createInitialRegulatorNotifications())
    val regulatorNotifications: StateFlow<List<RegulatorNotification>> = _regulatorNotifications.asStateFlow()

    private val _governanceMeetings = MutableStateFlow(DemoDataProvider.createInitialMeetings())
    val governanceMeetings: StateFlow<List<GovernanceMeeting>> = _governanceMeetings.asStateFlow()

    private val _riskRegister = MutableStateFlow(DemoDataProvider.createInitialRiskRegister())
    val riskRegister: StateFlow<List<RiskRegisterItem>> = _riskRegister.asStateFlow()

    private val _businessContinuityPlan = MutableStateFlow(DemoDataProvider.createInitialBcp())
    val businessContinuityPlan: StateFlow<BusinessContinuityPlan> = _businessContinuityPlan.asStateFlow()

    private val _youSaidWeDid = MutableStateFlow(DemoDataProvider.createInitialYouSaidWeDid())
    val youSaidWeDid: StateFlow<List<YouSaidWeDidItem>> = _youSaidWeDid.asStateFlow()

    private val _inspectionSession = MutableStateFlow(DemoDataProvider.createInitialInspectionDaySession())
    val inspectionSession: StateFlow<InspectionDaySession> = _inspectionSession.asStateFlow()

    private val _cqcThemes = MutableStateFlow(DemoDataProvider.createInitialCqcThemes())
    val cqcThemes: StateFlow<List<CqcAssessmentTheme>> = _cqcThemes.asStateFlow()

    // Field-Staff Carer Mobile State
    private val _loneWorkerSafetyState = MutableStateFlow(LoneWorkerSafetyState())
    val loneWorkerSafetyState: StateFlow<LoneWorkerSafetyState> = _loneWorkerSafetyState.asStateFlow()

    private val _handovers = MutableStateFlow(listOf(DemoDataProvider.createInitialHandoverDraft()))
    val handovers: StateFlow<List<HandoverDraft>> = _handovers.asStateFlow()

    private val _staffDocuments = MutableStateFlow(DemoDataProvider.createInitialStaffDocuments())
    val staffDocuments: StateFlow<List<StaffDocument>> = _staffDocuments.asStateFlow()

    private val _officeMessages = MutableStateFlow(DemoDataProvider.createInitialOfficeMessages())
    val officeMessages: StateFlow<List<OfficeMessage>> = _officeMessages.asStateFlow()

    private val _managerNotifications = MutableStateFlow(DemoDataProvider.createInitialManagerNotifications())
    val managerNotifications: StateFlow<List<ManagerNotification>> = _managerNotifications.asStateFlow()

    private val _isOfflineSimulated = MutableStateFlow(false)
    val isOfflineSimulated: StateFlow<Boolean> = _isOfflineSimulated.asStateFlow()

    // Session Security & Inactivity Timeout
    private val _isSessionLocked = MutableStateFlow(false)
    val isSessionLocked: StateFlow<Boolean> = _isSessionLocked.asStateFlow()

    private fun getCurrentUkTimestamp(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK)
        return sdf.format(Date())
    }

    // Role & Navigation
    fun switchUserRole(newRole: UserRole) {
        val current = _currentUser.value
        val newName = when (newRole) {
            UserRole.REGISTERED_MANAGER -> "Eleanor Vance"
            UserRole.COORDINATOR -> "Marcus Thorne"
            UserRole.SENIOR_CARER -> "Sarah Jenkins"
            UserRole.CARE_WORKER -> "David Okonkwo"
            UserRole.NURSE -> "Priya Patel"
            UserRole.HR_RECRUITER -> "Oliver Smith"
            UserRole.FINANCE -> "Hannah Croft"
            UserRole.FAMILY_MEMBER -> "Clara Pendelton (Relative)"
            UserRole.EXTERNAL_AUDITOR -> "Dr. Alistair Finch (Auditor)"
            UserRole.OWNER_ADMIN -> "Arthur Vance (Director)"
        }
        val targetBranch = if (newRole == UserRole.CARE_WORKER) "branch-cmd" else current.branchId
        _currentUser.value = current.copy(
            role = newRole,
            name = newName,
            branchId = targetBranch
        )
        if (!newRole.isOfficeRole()) {
            _isCarerMobileMode.value = true
        }
        logAudit("ROLE_SWITCH", "User switched active persona to ${newRole.displayName}")
    }

    fun toggleCarerMobileMode(forceMobile: Boolean? = null) {
        _isCarerMobileMode.value = forceMobile ?: !_isCarerMobileMode.value
    }

    fun setBranchFilter(branchId: String?) {
        _currentBranchId.value = branchId
    }

    // Audit Logging
    fun logAudit(actionType: String, notes: String, targetRecord: String = "Record") {
        val user = _currentUser.value
        val entry = AuditRecord(
            timestamp = getCurrentUkTimestamp(),
            performedBy = user.name,
            userRole = user.role,
            actionType = actionType,
            targetRecord = targetRecord,
            branchId = user.branchId,
            notes = notes
        )
        _auditLogs.value = listOf(entry) + _auditLogs.value
    }

    // Client Service
    fun getClients(branchFilter: String? = _currentBranchId.value): List<ClientProfile> {
        val all = _clients.value
        return if (branchFilter == null) all else all.filter { it.branchId == branchFilter }
    }

    fun getClientById(clientId: String): ClientProfile? {
        logAudit("VIEW_CLIENT", "Viewed detailed profile of client $clientId", clientId)
        return _clients.value.find { it.id == clientId }
    }

    fun createClient(client: ClientProfile) {
        _clients.value = listOf(client) + _clients.value
        logAudit("CREATE_CLIENT", "Created new client profile for ${client.fullName}", client.id)
    }

    fun updateClient(updated: ClientProfile) {
        _clients.value = _clients.value.map { if (it.id == updated.id) updated else it }
        logAudit("UPDATE_CLIENT", "Updated client details for ${updated.fullName}", updated.id)
    }

    // Care Plan Service (AI Draft & Versioning)
    fun getCarePlansForClient(clientId: String): List<CarePlanVersion> {
        return _carePlans.value.filter { it.clientId == clientId }.sortedByDescending { it.versionNumber }
    }

    fun generateAiCarePlanDraft(clientId: String): CarePlanVersion {
        val client = getClientById(clientId) ?: return _carePlans.value.first()
        val existingPlans = getCarePlansForClient(clientId)
        val nextVer = (existingPlans.maxOfOrNull { it.versionNumber } ?: 0) + 1

        val aiDraft = CarePlanVersion(
            clientId = clientId,
            versionNumber = nextVer,
            status = CarePlanStatus.DRAFT,
            title = "[AI Generated Draft] Person-Centred Re-ablement & Care Plan v$nextVer",
            summary = "Clinical AI recommendation based on ${client.fullName}'s profile (${client.careType.displayName}, Risk: ${client.riskLevel.displayName}). Identifies key mobility support, dignity preservation, and timely hydration prompting.",
            personalOutcomes = listOf(
                "Maintain maximum functional independence at home with 1-carer safety vigilance",
                "Ensure hydration baseline of 1.5L daily to prevent urinary infections and dizziness",
                "Support dignity and confidence during personal morning hygiene routines"
            ),
            nutritionHydrationPlan = "Encourage fluid intake with morning and afternoon drinks. Diabetic/regular diet according to preference. Monitor swallowing and posture.",
            mobilityMovingHandlingPlan = "Use Zimmer frame/walking aid consistently. Clear rugs and trip hazards along walking paths. 1 carer standby assist.",
            medicationSupportPlan = "Prompt and observe administration at prescribed times via eMAR. Alert office if refused.",
            communicationNeeds = "Clear, calm communication in quiet environment. Allow processing time.",
            emergencyContingency = "Immediate escalation to Registered Manager & Next of Kin (${client.nextOfKinName}: ${client.nextOfKinPhone}) if uncharacteristic confusion or fall occurs.",
            isAiDraft = true,
            createdBy = "All Care X Gemini Assistant (Draft Only)",
            createdDate = getCurrentUkTimestamp().split(" ").first(),
            nextReviewDate = "03/01/2027"
        )

        _carePlans.value = listOf(aiDraft) + _carePlans.value
        logAudit("GENERATE_AI_DRAFT", "Generated AI care plan draft v$nextVer for client ${client.fullName}. Strictly labelled AI and set to Draft.", aiDraft.id)
        return aiDraft
    }

    fun approveCarePlan(planId: String, approverName: String) {
        val now = getCurrentUkTimestamp().split(" ").first()
        _carePlans.value = _carePlans.value.map { plan ->
            if (plan.id == planId) {
                plan.copy(
                    status = CarePlanStatus.APPROVED,
                    approvedBy = approverName,
                    approvedDate = now
                )
            } else plan
        }
        logAudit("APPROVE_CARE_PLAN", "Approved care plan $planId by $approverName", planId)
    }

    // Visits Service
    fun getVisits(date: String = "03/10/2026", branchFilter: String? = _currentBranchId.value): List<Visit> {
        val list = _visits.value.filter { it.date == date }
        return if (branchFilter == null) list else list.filter { it.branchId == branchFilter }
    }

    fun getTodayVisitsForCarer(carerId: String): List<Visit> {
        return _visits.value.filter { it.carerId == carerId || it.secondCarerId == carerId }
    }

    fun checkInVisit(
        visitId: String,
        gpsCoordinates: String = "51.5074° N, 0.1278° W (Verified ±6m)",
        isManualOverride: Boolean = false,
        overrideReason: String? = null
    ) {
        val timeNow = getCurrentUkTimestamp().split(" ").last()
        _visits.value = _visits.value.map { v ->
            if (v.id == visitId) {
                v.copy(
                    status = VisitStatus.CHECKED_IN,
                    checkInTime = timeNow,
                    checkInGps = if (isManualOverride) "Manual Override: ${overrideReason ?: "GPS unavailable"}" else gpsCoordinates,
                    checkInOverrideReason = overrideReason
                )
            } else v
        }
        val auditMsg = if (isManualOverride) {
            "Checked in to visit $visitId at $timeNow via manual override reason: '$overrideReason'"
        } else {
            "Checked in to visit $visitId at $timeNow with verified GPS $gpsCoordinates"
        }
        logAudit("VISIT_CHECK_IN", auditMsg, visitId)
        queueOfflineSync("VISIT_CHECK_IN", "Visit $visitId checked in at $timeNow")
    }

    fun checkOutVisit(
        visitId: String,
        dailyNotes: String = "",
        fluidMl: Int? = null,
        mood: String? = null,
        gpsCoordinates: String = "51.5074° N, 0.1278° W (Verified ±5m)"
    ) {
        checkOutVisitExtended(visitId, dailyNotes, fluidMl, mood, null, emptyList(), gpsCoordinates)
    }

    fun assignVisitToCarer(visitId: String, carerId: String) {
        val staff = _staffList.value.find { it.id == carerId }
        _visits.value = _visits.value.map { v ->
            if (v.id == visitId) {
                v.copy(carerId = staff?.id, carerName = staff?.fullName)
            } else v
        }
        logAudit("ASSIGN_CARER", "Assigned carer ${staff?.fullName ?: "Unassigned"} to visit $visitId", visitId)
    }

    fun addVisit(visit: Visit) {
        _visits.value = listOf(visit) + _visits.value
        logAudit("ADD_VISIT", "Scheduled new visit for ${visit.clientName} at ${visit.date}", visit.id)
    }

    fun addMedication(medication: Medication) {
        _medications.value = listOf(medication) + _medications.value
        logAudit("ADD_MEDICATION", "Added medication ${medication.name} for client ${medication.clientId}", medication.id)
    }

    fun addStaff(staff: StaffProfile) {
        _staffList.value = listOf(staff) + _staffList.value
        logAudit("ADD_STAFF", "Added staff member ${staff.fullName} (${staff.role.displayName})", staff.id)
    }

    fun recordMedicationAdministration(record: MedicationAdministrationRecord) {
        _marHistory.value = listOf(record) + _marHistory.value
        if (record.outcome == AdministrationOutcome.ADMINISTERED || record.outcome == AdministrationOutcome.GIVEN) {
            _medications.value = _medications.value.map { m ->
                if (m.id == record.medicationId && m.currentStock > 0) {
                    m.copy(currentStock = m.currentStock - 1)
                } else m
            }
        }
        logAudit("EMAR_SIGN", "Administered ${record.medicationName} (${record.doseGiven}). Outcome: ${record.outcome.displayName}", record.medicationId)
    }

    fun submitManagerNotification(notification: ManagerNotification) {
        _managerNotifications.value = listOf(notification) + _managerNotifications.value
        logAudit("NOTIFY_MANAGER", "Submitted staff notice: ${notification.subject}", notification.id)
    }

    fun checkOutVisitExtended(
        visitId: String,
        dailyNotes: String,
        fluidMl: Int?,
        mood: String?,
        nextVisitNotes: String?,
        photoUris: List<String> = emptyList(),
        gpsCoordinates: String = "51.5074° N, 0.1278° W (Verified ±5m)"
    ) {
        val timeNow = getCurrentUkTimestamp().split(" ").last()
        _visits.value = _visits.value.map { v ->
            if (v.id == visitId) {
                v.copy(
                    status = VisitStatus.COMPLETED,
                    checkOutTime = timeNow,
                    checkOutGps = gpsCoordinates,
                    dailyNotes = dailyNotes,
                    fluidIntakeMl = fluidMl,
                    moodObservation = mood,
                    nextVisitNotes = nextVisitNotes,
                    photoEvidenceUris = photoUris
                )
            } else v
        }
        logAudit("VISIT_CHECK_OUT", "Completed visit $visitId. Notes, fluid (${fluidMl ?: 0}ml), and next-visit handover recorded.", visitId)
        queueOfflineSync("VISIT_CHECK_OUT", "Visit $visitId completed at $timeNow. Fluid: ${fluidMl ?: 0}ml")
    }

    fun toggleVisitTask(visitId: String, taskId: String) {
        _visits.value = _visits.value.map { v ->
            if (v.id == visitId) {
                val updatedTasks = v.tasks.map { t ->
                    if (t.id == taskId) t.copy(isCompleted = !t.isCompleted) else t
                }
                v.copy(tasks = updatedTasks)
            } else v
        }
    }

    fun setTaskExceptionReason(visitId: String, taskId: String, reason: String) {
        _visits.value = _visits.value.map { v ->
            if (v.id == visitId) {
                val updatedTasks = v.tasks.map { t ->
                    if (t.id == taskId) t.copy(exceptionReason = reason, isCompleted = false) else t
                }
                v.copy(tasks = updatedTasks)
            } else v
        }
        logAudit("TASK_EXCEPTION", "Recorded exception for task $taskId on visit $visitId: $reason", visitId)
        queueOfflineSync("TASK_EXCEPTION", "Task exception recorded on $visitId: $reason")
    }

    fun markRunningLate(visitId: String, delayMinutes: Int) {
        markRunningLateWithNotification(visitId, delayMinutes, "Traffic delay between calls", notifyFamily = true)
    }

    fun markRunningLateWithNotification(visitId: String, delayMinutes: Int, reason: String, notifyFamily: Boolean = true) {
        _visits.value = _visits.value.map { v ->
            if (v.id == visitId) {
                v.copy(
                    status = VisitStatus.RUNNING_LATE,
                    isLateNotified = true,
                    lateDelayMinutes = delayMinutes
                )
            } else v
        }
        val familyNotice = if (notifyFamily) " (Family / Next of Kin notified via SMS)" else ""
        logAudit("RUNNING_LATE_ALERT", "Carer marked running late by $delayMinutes mins ($reason) for visit $visitId$familyNotice", visitId)
        queueOfflineSync("RUNNING_LATE_ALERT", "Visit $visitId running late +${delayMinutes}m: $reason")
    }

    fun triggerLoneWorkerSos(visitId: String) {
        _visits.value = _visits.value.map { v ->
            if (v.id == visitId) {
                v.copy(isEmergencyAlertRaised = true)
            } else v
        }
        _loneWorkerSafetyState.value = _loneWorkerSafetyState.value.copy(
            isSosTriggered = true,
            isEscalated = true
        )
        logAudit("LONE_WORKER_SOS", "EMERGENCY: Lone worker SOS triggered on visit $visitId! Office and On-Call Manager notified immediately.", visitId)
        queueOfflineSync("LONE_WORKER_SOS", "CRITICAL SOS triggered for visit $visitId")
    }

    fun triggerLoneWorkerPanic(coords: String = "51.5074° N, 0.1278° W (Westminster)") {
        _loneWorkerSafetyState.value = _loneWorkerSafetyState.value.copy(
            isSosTriggered = true,
            isEscalated = true,
            lastGpsCoords = coords
        )
        logAudit("PANIC_BUTTON", "EMERGENCY PANIC: Field carer activated 'I Need Help' panic button at $coords", "SAFETY")
        queueOfflineSync("PANIC_BUTTON", "Carer panic button pressed at $coords")
    }

    fun cancelLoneWorkerPanic() {
        _loneWorkerSafetyState.value = _loneWorkerSafetyState.value.copy(
            isSosTriggered = false,
            isEscalated = false,
            remainingSeconds = 2700
        )
        logAudit("CANCEL_PANIC", "Carer safety panic alert stood down. All safe.", "SAFETY")
    }

    fun resetLoneWorkerTimer() {
        val timeNow = getCurrentUkTimestamp().split(" ").last()
        _loneWorkerSafetyState.value = _loneWorkerSafetyState.value.copy(
            remainingSeconds = 2700,
            lastCheckInTime = timeNow,
            isEscalated = false
        )
        logAudit("LONE_WORKER_CHECK_IN", "Carer checked in safely. Timer reset to 45 mins at $timeNow", "SAFETY")
    }

    fun generateAiHandoverDraft(carerId: String, carerName: String): HandoverDraft {
        val todayVisits = _visits.value.filter { it.carerId == carerId }
        val completedCount = todayVisits.count { it.status == VisitStatus.COMPLETED }
        val totalFluid = todayVisits.sumOf { it.fluidIntakeMl ?: 0 }
        val dateNow = getCurrentUkTimestamp().split(" ").first()

        val clientsServed = todayVisits.map { it.clientName }.distinct().joinToString(", ")
        val aiSummary = "Delivered $completedCount of ${todayVisits.size} visits today across $clientsServed. Total fluid documented: ${totalFluid}ml. All scheduled medications administered. Care plans adhered to with active mobility support."

        val draft = HandoverDraft(
            date = dateNow,
            shiftType = "Daily Shift Run",
            carerId = carerId,
            carerName = carerName,
            visitsCount = todayVisits.size,
            totalFluidMl = totalFluid,
            tasksSummary = "$completedCount completed out of ${todayVisits.size} visits",
            aiGeneratedSummary = aiSummary,
            carerEditedNotes = "All clients settled comfortably. Please check Arthur Pendelton's hydration and door locks on next round.",
            redFlags = listOf("Arthur: Fall risk caution", "Harpreet: Heel dressing check"),
            isSubmitted = false
        )
        _handovers.value = listOf(draft) + _handovers.value
        return draft
    }

    fun saveAndSignHandover(handoverId: String, editedNotes: String) {
        val timeNow = getCurrentUkTimestamp()
        _handovers.value = _handovers.value.map { h ->
            if (h.id == handoverId) {
                h.copy(
                    carerEditedNotes = editedNotes,
                    isSubmitted = true,
                    submittedAt = timeNow
                )
            } else h
        }
        logAudit("SUBMIT_HANDOVER", "Carer signed off end-of-shift handover $handoverId", handoverId)
        queueOfflineSync("SUBMIT_HANDOVER", "Shift handover signed off at $timeNow")
    }

    fun sendManagerNotification(staffId: String, staffName: String, category: String, details: String) {
        val newNotif = ManagerNotification(
            staffId = staffId,
            staffName = staffName,
            category = category,
            details = details,
            timestamp = getCurrentUkTimestamp(),
            status = "Sent"
        )
        _managerNotifications.value = listOf(newNotif) + _managerNotifications.value
        logAudit("NOTIFY_MANAGER", "Carer $staffName submitted $category notification to office: $details", staffId)
        queueOfflineSync("NOTIFY_MANAGER", "$category notification: $details")
    }

    fun markMessageRead(messageId: String) {
        _officeMessages.value = _officeMessages.value.map { m ->
            if (m.id == messageId) m.copy(isRead = true) else m
        }
    }

    fun toggleOfflineSimulation(isOffline: Boolean) {
        _isOfflineSimulated.value = isOffline
    }

    fun createVisit(visit: Visit) {
        _visits.value = _visits.value + visit
        logAudit("CREATE_VISIT", "Created visit for ${visit.clientName} at ${visit.startTime}", visit.id)
    }

    // Medication & eMAR Service
    fun getMedicationsForClient(clientId: String): List<Medication> {
        return _medications.value.filter { it.clientId == clientId }
    }

    fun recordMedicationAdministration(
        medicationId: String,
        clientId: String,
        outcome: AdministrationOutcome,
        doseGiven: String,
        carerName: String,
        reasonCode: String? = null,
        witnessCarerName: String? = null,
        notes: String = ""
    ) {
        val med = _medications.value.find { it.id == medicationId } ?: return
        val record = MedicationAdministrationRecord(
            medicationId = medicationId,
            medicationName = med.name,
            clientId = clientId,
            scheduledDate = getCurrentUkTimestamp().split(" ").first(),
            scheduledTime = getCurrentUkTimestamp().split(" ").last(),
            actualTime = getCurrentUkTimestamp().split(" ").last(),
            outcome = outcome,
            reasonCodeDescription = reasonCode,
            administeredByCarerId = _currentUser.value.id,
            administeredByCarerName = carerName,
            witnessCarerName = witnessCarerName,
            notes = notes,
            doseGiven = doseGiven
        )
        _marHistory.value = listOf(record) + _marHistory.value

        // Decrement stock if administered
        if (outcome == AdministrationOutcome.ADMINISTERED) {
            _medications.value = _medications.value.map { m ->
                if (m.id == medicationId && m.currentStock > 0) {
                    m.copy(currentStock = m.currentStock - 1)
                } else m
            }
        }

        val auditDesc = if (med.isControlledDrug) {
            "Administered Controlled Drug ${med.name} ($doseGiven). Witnessed & double-signed by $witnessCarerName. Outcome: ${outcome.displayName}."
        } else {
            "Recorded eMAR outcome for ${med.name} ($doseGiven): ${outcome.displayName}. Reason: ${reasonCode ?: "N/A"}."
        }
        logAudit("EMAR_SIGN", auditDesc, medicationId)
        queueOfflineSync("EMAR_RECORD", "eMAR for ${med.name}: ${outcome.code}")
    }

    // Incidents Service
    fun logIncident(incident: IncidentReport) {
        _incidents.value = listOf(incident) + _incidents.value
        val alertInfo = if (incident.isCqcNotifiable) "[CQC REG 18 NOTIFIABLE] " else ""
        logAudit("LOG_INCIDENT", "$alertInfo New incident logged for ${incident.clientName}: ${incident.category.displayName} (${incident.severity.displayName})", incident.id)
    }

    fun submitIncident(incident: IncidentReport) = logIncident(incident)

    // Workforce & Compliance
    fun getStaff(): List<StaffProfile> {
        val branchFilter = _currentBranchId.value
        val list = _staffList.value
        return if (branchFilter == null) list else list.filter { it.branchId == branchFilter }
    }

    fun canRosterCarer(staffId: String): Pair<Boolean, String> {
        val staff = _staffList.value.find { it.id == staffId } ?: return false to "Staff not found"
        if (staff.compliancePercentage < 75) {
            return false to "Rostering blocked: Staff compliance is below 75% (${staff.compliancePercentage}%). DBS or mandatory training expired!"
        }
        if (staff.hoursWorkedThisWeek >= 48f) {
            return false to "Working Time Directive Warning: Exceeds 48 hours weekly limit."
        }
        return true to "Eligible to roster"
    }

    // Training Matrix
    fun getTrainingForStaff(staffId: String): List<TrainingRecord> {
        return _trainingList.value.filter { it.staffId == staffId }
    }

    // Body Map
    fun getBodyMapMarks(clientId: String): List<BodyMapMark> {
        return _bodyMapMarks.value.filter { it.clientId == clientId }
    }

    fun addBodyMapMark(mark: BodyMapMark) {
        _bodyMapMarks.value = listOf(mark) + _bodyMapMarks.value
        logAudit("BODY_MAP_MARK", "Added body map mark (${mark.markType} - ${mark.severity}) for client ${mark.clientId}", mark.id)
    }

    // Family Portal Messaging
    fun sendFamilyMessage(clientId: String, text: String) {
        val user = _currentUser.value
        val msg = FamilyMessage(
            clientId = clientId,
            senderName = user.name,
            senderRole = user.role.displayName,
            timestamp = getCurrentUkTimestamp(),
            messageText = text
        )
        _familyMessages.value = _familyMessages.value + msg
        logAudit("FAMILY_MESSAGE", "Sent secure message to family of client $clientId", clientId)
    }

    // Offline Sync
    private fun queueOfflineSync(actionType: String, summary: String) {
        val item = QueuedOfflineAction(
            timestamp = getCurrentUkTimestamp(),
            actionType = actionType,
            payloadSummary = summary,
            isSynced = false
        )
        _offlineQueue.value = listOf(item) + _offlineQueue.value
    }

    fun syncAllOfflineActions() {
        val count = _offlineQueue.value.size
        _offlineQueue.value = emptyList()
        logAudit("OFFLINE_SYNC", "Synchronised $count pending offline care records to London Cloud repository.")
    }

    // Settings
    fun updateOrganisationSettings(updated: OrganisationSettings) {
        _organisationSettings.value = updated
        logAudit("UPDATE_SETTINGS", "Updated organisation white-label brand and regulator settings.")
    }

    // ==================== RECRUITMENT SERVICE ====================
    fun updateCandidateStage(candidateId: String, newStage: CandidateStage) {
        _candidates.value = _candidates.value.map { c ->
            if (c.id == candidateId) c.copy(stage = newStage) else c
        }
        logAudit("CANDIDATE_STAGE_UPDATE", "Moved candidate $candidateId to stage ${newStage.displayName}", candidateId)
    }

    fun scoreCandidateInterview(candidateId: String, score: InterviewScore) {
        _candidates.value = _candidates.value.map { c ->
            if (c.id == candidateId) c.copy(interviewScore = score, stage = CandidateStage.CHECKS_PENDING) else c
        }
        logAudit("INTERVIEW_SCORED", "Scored interview for candidate $candidateId (Score: ${score.totalScore}/20, Rec: ${score.recommendation})", candidateId)
    }

    fun toggleOnboardingItem(candidateId: String, itemKey: String) {
        _candidates.value = _candidates.value.map { c ->
            if (c.id == candidateId) {
                val ob = c.onboarding
                val updatedOb = when (itemKey) {
                    "contract" -> ob.copy(contractSigned = !ob.contractSigned)
                    "uniform" -> ob.copy(uniformIssued = !ob.uniformIssued)
                    "shadow" -> ob.copy(shadowShiftsBooked = !ob.shadowShiftsBooked)
                    "idBadge" -> ob.copy(idBadgePrinted = !ob.idBadgePrinted)
                    "training" -> ob.copy(mandatoryTrainingEnrolled = !ob.mandatoryTrainingEnrolled)
                    else -> ob
                }
                c.copy(onboarding = updatedOb)
            } else c
        }
    }

    fun convertCandidateToStaffProfile(candidateId: String): StaffProfile? {
        val candidate = _candidates.value.find { it.id == candidateId } ?: return null
        val vacancy = _vacancies.value.find { it.id == candidate.vacancyId }
        val role = vacancy?.roleType ?: UserRole.CARE_WORKER
        val contract = vacancy?.contractType ?: ContractType.GUARANTEED_HOURS

        val newStaff = StaffProfile(
            id = "staff-" + UUID.randomUUID().toString().take(6),
            organisationId = "org-allcarex-01",
            branchId = vacancy?.branchId ?: "branch-wst",
            fullName = candidate.fullName,
            email = candidate.email,
            phone = candidate.phone,
            role = role,
            contractType = contract,
            rightToWorkVerified = candidate.rightToWork.verified,
            rightToWorkExpiry = candidate.rightToWork.expiryDate,
            dbsCertificateNumber = candidate.dbsTracking.certificateNumber ?: "002991048291",
            dbsEnhancedStatus = true,
            dbsIssueDate = getCurrentUkTimestamp().split(" ").first(),
            dbsExpiryDate = "03/10/2029",
            dbsUpdateServiceRegistered = true,
            careCertificateCompleted = false,
            compliancePercentage = 95,
            lastSupervisionDate = getCurrentUkTimestamp().split(" ").first(),
            nextSupervisionDueDate = "03/01/2027",
            annualAppraisalDueDate = "03/10/2027",
            skills = listOf("Moving & Handling", "Person-Centred Care", "eMAR", "Safeguarding"),
            hoursWorkedThisWeek = 0f,
            isAvailable = true
        )

        _staffList.value = _staffList.value + newStaff
        _candidates.value = _candidates.value.map { c ->
            if (c.id == candidateId) c.copy(stage = CandidateStage.ONBOARDED) else c
        }

        logAudit("CONVERT_TO_STAFF", "Successfully onboarded candidate ${candidate.fullName} and created live staff profile ${newStaff.id} (${newStaff.role.displayName})", newStaff.id)
        return newStaff
    }

    // ==================== PAYROLL & INVOICING SERVICE ====================
    fun generateTimesheetsFromDeliveredVisits() {
        val completed = _visits.value.filter { it.status == VisitStatus.COMPLETED || it.checkInTime != null }
        val entries = completed.map { v ->
            val staff = _staffList.value.find { it.id == v.carerId }
            val rate = 13.50 // standard UK carer base rate
            val hours = 0.75
            val gross = hours * rate
            val travelPay = (v.travelTimeMinutes / 60.0) * rate
            val mileagePay = 2.5 * 0.45
            val holidayAccrual = gross * 0.1207

            TimesheetEntry(
                staffId = v.carerId ?: "staff-04",
                staffName = v.carerName ?: "Assigned Staff",
                visitId = v.id,
                clientName = v.clientName,
                date = v.date,
                scheduledHours = 0.75,
                actualDeliveredHours = hours,
                hourlyPayRate = rate,
                grossPay = Math.round(gross * 100.0) / 100.0,
                travelTimeMinutes = v.travelTimeMinutes,
                travelPay = Math.round(travelPay * 100.0) / 100.0,
                mileageMiles = 2.5,
                mileagePay = Math.round(mileagePay * 100.0) / 100.0,
                holidayPayAccrued = Math.round(holidayAccrual * 100.0) / 100.0,
                isNlwCompliant = rate >= 11.44,
                complianceFlags = emptyList()
            )
        }
        if (entries.isNotEmpty()) {
            _timesheets.value = entries
            logAudit("GENERATE_TIMESHEETS", "Generated ${entries.size} verified GPS visit timesheet entries with NLW and holiday accrual.")
        }
    }

    fun updatePayRules(newConfig: PayRulesConfig) {
        _payRules.value = newConfig
        logAudit("UPDATE_PAY_RULES", "Updated organisation pay rules: Base £${newConfig.baseHourlyRate}/h, NLW £${newConfig.nationalLivingWageRate}/h, Mileage £${newConfig.mileageRatePerMile}/mi")
    }

    fun addManualTimesheetEntry(
        staffId: String,
        staffName: String,
        entryType: EntryType,
        clientOrEventName: String,
        date: String,
        hours: Double,
        hourlyRate: Double
    ) {
        val gross = Math.round(hours * hourlyRate * 100.0) / 100.0
        val holidayAccrual = Math.round(gross * (_payRules.value.holidayAccrualPercentage / 100.0) * 100.0) / 100.0
        val newEntry = TimesheetEntry(
            staffId = staffId,
            staffName = staffName,
            entryType = entryType,
            clientName = clientOrEventName,
            date = date,
            scheduledHours = hours,
            actualDeliveredHours = hours,
            differenceMinutes = 0,
            hourlyPayRate = hourlyRate,
            grossPay = gross,
            holidayPayAccrued = holidayAccrual,
            isNlwCompliant = hourlyRate >= _payRules.value.nationalLivingWageRate
        )
        _timesheets.value = listOf(newEntry) + _timesheets.value
        logAudit("ADD_MANUAL_TIMESHEET", "Added manual timesheet entry for $staffName: ${entryType.displayName} ($hours hrs @ £$hourlyRate/h)")
    }

    fun raiseTimesheetDispute(timesheetId: String, reason: String) {
        _timesheets.value = _timesheets.value.map { entry ->
            if (entry.id == timesheetId) {
                entry.copy(isDisputed = true, disputeReason = reason)
            } else entry
        }
        logAudit("TIMESHEET_DISPUTE_RAISED", "Timesheet dispute raised for entry $timesheetId: $reason", timesheetId)
    }

    fun advancePayRun(payRunId: String) {
        _payRuns.value = _payRuns.value.map { run ->
            if (run.id == payRunId) {
                val nextStatus = when (run.status) {
                    PayRunStatus.DRAFT -> PayRunStatus.MANAGER_REVIEW
                    PayRunStatus.MANAGER_REVIEW -> PayRunStatus.APPROVED
                    PayRunStatus.APPROVED -> PayRunStatus.EXPORTED
                    PayRunStatus.EXPORTED -> PayRunStatus.LOCKED
                    PayRunStatus.LOCKED -> PayRunStatus.LOCKED
                }
                val lockedTime = if (nextStatus == PayRunStatus.LOCKED) getCurrentUkTimestamp() else run.lockedAt
                run.copy(
                    status = nextStatus,
                    lockedAt = lockedTime,
                    approvedBy = if (nextStatus == PayRunStatus.APPROVED) _currentUser.value.name else run.approvedBy
                )
            } else run
        }
        logAudit("ADVANCE_PAYRUN", "Advanced pay-run status for $payRunId", payRunId)
    }

    fun addPayAdjustment(payRunId: String, staffId: String, staffName: String, amount: Double, reason: String) {
        val adjustment = PayAdjustment(
            staffId = staffId,
            staffName = staffName,
            periodRef = "Adjustment",
            amount = amount,
            reason = reason,
            authorisedBy = _currentUser.value.name
        )
        _payRuns.value = _payRuns.value.map { run ->
            if (run.id == payRunId) {
                run.copy(
                    adjustments = run.adjustments + adjustment,
                    totalPayrollCost = run.totalPayrollCost + amount
                )
            } else run
        }
        logAudit("ADD_PAY_ADJUSTMENT", "Added pay adjustment of £$amount for $staffName: $reason", payRunId)
    }

    fun requestHoliday(staffId: String, staffName: String, startDate: String, endDate: String, hours: Double, notes: String) {
        // Check for clash with existing visits
        val hasClash = _visits.value.any { it.carerId == staffId && it.status == VisitStatus.SCHEDULED }
        val newHoliday = HolidayRecord(
            staffId = staffId,
            staffName = staffName,
            startDate = startDate,
            endDate = endDate,
            hoursRequested = hours,
            status = "Pending",
            hasRotaClash = hasClash,
            notes = if (hasClash) "Rota clash: Active scheduled visits require cover. $notes" else notes
        )
        _holidays.value = listOf(newHoliday) + _holidays.value
        logAudit("HOLIDAY_REQUESTED", "Holiday requested by $staffName for $startDate - $endDate ($hours hrs)", newHoliday.id)
    }

    fun approveHoliday(holidayId: String) {
        _holidays.value = _holidays.value.map { h ->
            if (h.id == holidayId) h.copy(status = "Approved") else h
        }
        // Deduct from staff balance
        val holiday = _holidays.value.find { it.id == holidayId }
        if (holiday != null) {
            _holidayBalances.value = _holidayBalances.value.map { bal ->
                if (bal.staffId == holiday.staffId) {
                    bal.copy(
                        takenHours = bal.takenHours + holiday.hoursRequested,
                        remainingBalanceHours = Math.max(0.0, bal.remainingBalanceHours - holiday.hoursRequested)
                    )
                } else bal
            }
        }
        logAudit("HOLIDAY_APPROVED", "Holiday $holidayId approved by ${_currentUser.value.name}", holidayId)
    }

    fun logSickness(record: SicknessRecord) {
        _sicknessRecords.value = listOf(record) + _sicknessRecords.value
        logAudit("SICKNESS_LOGGED", "Logged sickness for ${record.staffName}: ${record.startDate} - ${record.endDate} (SSP Eligible: ${record.isSspEligible})", record.id)
    }

    fun flagInvoiceLineDisputed(invoiceId: String, lineId: String, reason: String) {
        _invoices.value = _invoices.value.map { inv ->
            if (inv.id == invoiceId) {
                val updatedLines = inv.lines.map { l ->
                    if (l.id == lineId) l.copy(isDisputed = true, disputeReason = reason) else l
                }
                inv.copy(lines = updatedLines)
            } else inv
        }
        logAudit("INVOICE_LINE_DISPUTED", "Invoice $invoiceId line $lineId flagged as disputed: $reason", invoiceId)
    }

    fun recordInvoicePayment(invoiceId: String, amount: Double, method: String, reference: String) {
        _invoices.value = _invoices.value.map { inv ->
            if (inv.id == invoiceId) {
                val newPayment = InvoicePayment(
                    invoiceId = invoiceId,
                    paymentDate = getCurrentUkTimestamp().split(" ").first(),
                    amount = amount,
                    paymentMethod = method,
                    reference = reference
                )
                val newOutstanding = Math.max(0.0, inv.outstandingBalance - amount)
                val newStatus = if (newOutstanding <= 0.0) InvoiceStatus.PAID else InvoiceStatus.PART_PAID
                inv.copy(
                    payments = inv.payments + newPayment,
                    outstandingBalance = newOutstanding,
                    status = newStatus
                )
            } else inv
        }
        logAudit("RECORD_PAYMENT", "Recorded payment of £$amount on invoice $invoiceId via $method (Ref: $reference)", invoiceId)
    }

    fun issueCreditNote(invoiceId: String, amount: Double, reason: String) {
        _invoices.value = _invoices.value.map { inv ->
            if (inv.id == invoiceId) {
                val newCreditNote = CreditNote(
                    invoiceId = invoiceId,
                    creditNoteNumber = "CN-" + UUID.randomUUID().toString().take(6).uppercase(),
                    date = getCurrentUkTimestamp().split(" ").first(),
                    amount = amount,
                    reason = reason
                )
                val newOutstanding = Math.max(0.0, inv.outstandingBalance - amount)
                inv.copy(
                    creditNotes = inv.creditNotes + newCreditNote,
                    outstandingBalance = newOutstanding
                )
            } else inv
        }
        logAudit("ISSUE_CREDIT_NOTE", "Issued credit note of £$amount on invoice $invoiceId: $reason", invoiceId)
    }

    fun approveInvoice(invoiceId: String) {
        _invoices.value = _invoices.value.map { inv ->
            if (inv.id == invoiceId) inv.copy(status = InvoiceStatus.APPROVED) else inv
        }
        logAudit("APPROVE_INVOICE", "Invoice $invoiceId approved by Finance Manager.", invoiceId)
    }

    fun dispatchInvoice(invoiceId: String) {
        _invoices.value = _invoices.value.map { inv ->
            if (inv.id == invoiceId) inv.copy(status = InvoiceStatus.SENT) else inv
        }
        logAudit("SEND_INVOICE", "Dispatched invoice $invoiceId to payer (Local Authority / NHS CHC / Private).", invoiceId)
    }

    // ==================== QUALITY, GOVERNANCE & FRAMEWORK SERVICE ====================

    fun setActiveFramework(frameworkId: String) {
        _frameworks.value = _frameworks.value.map { fw ->
            fw.copy(isActive = fw.id == frameworkId)
        }
        logAudit("SET_ACTIVE_FRAMEWORK", "Switched active regulatory assessment framework to $frameworkId", frameworkId)
    }

    fun addOrUpdateFramework(framework: AssessmentFramework) {
        val exists = _frameworks.value.any { it.id == framework.id }
        _frameworks.value = if (exists) {
            _frameworks.value.map { if (it.id == framework.id) framework else it }
        } else {
            _frameworks.value + framework
        }
        logAudit("SAVE_REGULATOR_FRAMEWORK", "Saved regulatory framework ${framework.name} (v${framework.version})", framework.id)
    }

    fun addKeyQuestionToFramework(frameworkId: String, keyQuestion: KeyQuestion) {
        _frameworks.value = _frameworks.value.map { fw ->
            if (fw.id == frameworkId) {
                fw.copy(keyQuestions = fw.keyQuestions + keyQuestion)
            } else fw
        }
        logAudit("ADD_KEY_QUESTION", "Added key question ${keyQuestion.name} to framework $frameworkId")
    }

    fun addOrUpdateStatement(frameworkId: String, keyQuestionId: String, statement: QualityStatementOrKloe) {
        _frameworks.value = _frameworks.value.map { fw ->
            if (fw.id == frameworkId) {
                val updatedKqs = fw.keyQuestions.map { kq ->
                    if (kq.id == keyQuestionId) {
                        val exists = kq.statements.any { it.id == statement.id }
                        val newStmts = if (exists) {
                            kq.statements.map { if (it.id == statement.id) statement else it }
                        } else {
                            kq.statements + statement
                        }
                        kq.copy(statements = newStmts)
                    } else kq
                }
                fw.copy(keyQuestions = updatedKqs)
            } else fw
        }
        logAudit("UPDATE_STATEMENT", "Updated statement ${statement.code}: ${statement.title} in framework $frameworkId")
    }

    fun updateSelfAssessmentScore(frameworkId: String, statementId: String, score: Int, narrative: String) {
        _frameworks.value = _frameworks.value.map { fw ->
            if (fw.id == frameworkId) {
                val updatedKqs = fw.keyQuestions.map { kq ->
                    val updatedStmts = kq.statements.map { stmt ->
                        if (stmt.id == statementId) {
                            stmt.copy(
                                currentSelfAssessmentScore = score,
                                selfAssessmentNarrative = narrative,
                                selfAssessmentDate = getCurrentUkTimestamp().split(" ").first()
                            )
                        } else stmt
                    }
                    kq.copy(statements = updatedStmts)
                }
                fw.copy(keyQuestions = updatedKqs)
            } else fw
        }
        logAudit("SELF_ASSESSMENT_SCORE", "Updated self-assessment for statement $statementId to score $score/4", statementId)
    }

    fun addEvidenceItem(item: EvidenceItem) {
        _evidenceItems.value = listOf(item) + _evidenceItems.value
        logAudit("ADD_EVIDENCE", "Added evidence item '${item.title}' linked to ${item.statementId}")
    }

    fun addPolicy(policy: PolicyDocument) {
        _policies.value = listOf(policy) + _policies.value
        logAudit("CREATE_POLICY", "Published new policy '${policy.title}' (v${policy.version})", policy.id)
    }

    fun acknowledgePolicy(policyId: String) {
        _policies.value = _policies.value.map { pol ->
            if (pol.id == policyId && pol.staffAcknowledgedCount < pol.totalStaffCount) {
                pol.copy(staffAcknowledgedCount = pol.staffAcknowledgedCount + 1)
            } else pol
        }
        logAudit("POLICY_ACKNOWLEDGE", "Staff member acknowledged policy $policyId", policyId)
    }

    fun addAuditTemplate(template: AuditTemplate) {
        _auditTemplates.value = listOf(template) + _auditTemplates.value
        logAudit("CREATE_AUDIT_TEMPLATE", "Created audit template '${template.title}' (${template.frequency})", template.id)
    }

    fun updateAuditActionStatus(actionId: String, newStatus: String, notes: String? = null) {
        _auditTemplates.value = _auditTemplates.value.map { aud ->
            val updatedActions = aud.actionItems.map { act ->
                if (act.id == actionId) {
                    act.copy(status = newStatus, completionNotes = notes ?: act.completionNotes)
                } else act
            }
            aud.copy(actionItems = updatedActions)
        }
        logAudit("UPDATE_AUDIT_ACTION", "Updated audit action item $actionId to $newStatus", actionId)
    }

    fun addComplaintOrCompliment(item: ComplaintCompliment) {
        _complaintsCompliments.value = listOf(item) + _complaintsCompliments.value
        val label = if (item.isCompliment) "Compliment" else "Complaint"
        logAudit("LOG_FEEDBACK", "Logged new $label for ${item.clientName} (Duty of Candour: ${item.dutyOfCandourApplies})", item.id)
    }

    fun addRegulatorNotification(notif: RegulatorNotification) {
        _regulatorNotifications.value = listOf(notif) + _regulatorNotifications.value
        logAudit("STATUTORY_NOTIFICATION", "Submitted statutory notification '${notif.notificationType}' (Ref: ${notif.submissionReference})", notif.id)
    }

    fun addRiskRegisterItem(risk: RiskRegisterItem) {
        _riskRegister.value = listOf(risk) + _riskRegister.value
        logAudit("ADD_RISK", "Added risk register entry: ${risk.description} (Score: ${risk.riskScore})", risk.id)
    }

    fun addGovernanceMeeting(meeting: GovernanceMeeting) {
        _governanceMeetings.value = listOf(meeting) + _governanceMeetings.value
        logAudit("GOVERNANCE_MEETING", "Recorded minutes for ${meeting.meetingType} held on ${meeting.date}", meeting.id)
    }

    fun addYouSaidWeDidItem(item: YouSaidWeDidItem) {
        _youSaidWeDid.value = listOf(item) + _youSaidWeDid.value
        logAudit("YOU_SAID_WE_DID", "Added You Said, We Did item: '${item.youSaid.take(40)}...'", item.id)
    }

    fun toggleInspectionSession(active: Boolean) {
        _inspectionSession.value = _inspectionSession.value.copy(isSessionActive = active)
        val action = if (active) "ACTIVATED" else "DEACTIVATED"
        logAudit("INSPECTION_MODE", "Inspection-Day Auditor Mode $action by Registered Manager.")
    }

    fun logAuditorAccess(section: String, recordRef: String) {
        val entry = AuditorAccessLogEntry(
            timestamp = getCurrentUkTimestamp(),
            auditorName = _inspectionSession.value.auditorName,
            sectionAccessed = section,
            recordReference = recordRef
        )
        _inspectionSession.value = _inspectionSession.value.copy(
            accessLog = listOf(entry) + _inspectionSession.value.accessLog
        )
        logAudit("AUDITOR_ACCESS", "Inspection Auditor viewed $section ($recordRef)")
    }

    fun updateCqcThemeScore(themeId: String, newScore: Int, evidenceNotes: String) {
        _cqcThemes.value = _cqcThemes.value.map { t ->
            if (t.id == themeId) {
                t.copy(
                    currentScore = newScore,
                    evidenceNotes = evidenceNotes,
                    lastUpdated = getCurrentUkTimestamp().split(" ").first()
                )
            } else t
        }
        logAudit("UPDATE_CQC_FRAMEWORK", "Updated CQC self-assessment quality statement $themeId to score $newScore", themeId)
    }

    // ==================== SECURITY & SESSION INACTIVITY ====================
    fun lockSession() {
        _isSessionLocked.value = true
        logAudit("SESSION_LOCK", "User session locked due to inactivity or manual lock.")
    }

    fun unlockSessionWithPin(pin: String): Boolean {
        // Standard security PIN for demo testing: 1234
        if (pin == "1234" || pin == "0000") {
            _isSessionLocked.value = false
            logAudit("SESSION_UNLOCK", "User session unlocked with verified PIN.")
            return true
        }
        return false
    }
}
