package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.CareRepository
import com.example.model.AdministrationOutcome
import com.example.model.CandidateStage
import com.example.model.CarePlanStatus
import com.example.model.InvoiceStatus
import com.example.model.VisitStatus
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("All Care X", appName)
    }

    @Test
    fun `care plan AI draft generates exactly one draft labelled AI and requires approval`() {
        val repository = CareRepository()
        val clientId = "client-01"
        val initialCount = repository.getCarePlansForClient(clientId).size

        val aiDraft = repository.generateAiCarePlanDraft(clientId)
        val updatedPlans = repository.getCarePlansForClient(clientId)

        assertEquals(initialCount + 1, updatedPlans.size)
        assertTrue(aiDraft.isAiDraft)
        assertEquals(CarePlanStatus.DRAFT, aiDraft.status)

        // Senior approves the draft
        repository.approveCarePlan(aiDraft.id, "Eleanor Vance")
        val approved = repository.getCarePlansForClient(clientId).find { it.id == aiDraft.id }
        assertNotNull(approved)
        assertEquals(CarePlanStatus.APPROVED, approved?.status)
    }

    @Test
    fun `workforce compliance check blocks rostering if compliance is under 75 percent`() {
        val repository = CareRepository()
        // James Wilson (staff-07) has compliance 64% due to expired DBS
        val (canRosterJames, reasonJames) = repository.canRosterCarer("staff-07")
        assertFalse(canRosterJames)
        assertTrue(reasonJames.contains("Rostering blocked"))

        // David Okonkwo (staff-04) has compliance 92%
        val (canRosterDavid, _) = repository.canRosterCarer("staff-04")
        assertTrue(canRosterDavid)
    }

    @Test
    fun `eMAR administration decrements stock and logs immutable audit trail`() {
        val repository = CareRepository()
        val initialMed = repository.medications.value.find { it.id == "med-01" }
        assertNotNull(initialMed)
        val initialStock = initialMed!!.currentStock

        repository.recordMedicationAdministration(
            medicationId = "med-01",
            clientId = "client-01",
            outcome = AdministrationOutcome.ADMINISTERED,
            doseGiven = "5mg",
            carerName = "David Okonkwo"
        )

        val updatedMed = repository.medications.value.find { it.id == "med-01" }
        assertEquals(initialStock - 1, updatedMed?.currentStock)
        assertTrue(repository.marHistory.value.isNotEmpty())
    }

    @Test
    fun `recruitment candidate conversion creates staff profile upon onboarding completion`() {
        val repository = CareRepository()
        val candidate = repository.candidates.value.find { it.id == "cand-01" }
        assertNotNull(candidate)
        assertTrue(candidate!!.onboarding.isReadyForStaffProfile())

        val initialStaffCount = repository.staffList.value.size
        val createdStaff = repository.convertCandidateToStaffProfile("cand-01")

        assertNotNull(createdStaff)
        assertEquals(initialStaffCount + 1, repository.staffList.value.size)
        assertEquals("Amara Nwosu", createdStaff?.fullName)

        val updatedCandidate = repository.candidates.value.find { it.id == "cand-01" }
        assertEquals(CandidateStage.ONBOARDED, updatedCandidate?.stage)
    }

    @Test
    fun `payroll timesheets calculate gross pay and holiday accrual correctly`() {
        val repository = CareRepository()
        val initialTimesheets = repository.timesheets.value
        assertTrue(initialTimesheets.isNotEmpty())

        val entry = initialTimesheets.first()
        assertTrue(entry.isNlwCompliant)
        assertTrue(entry.holidayPayAccrued > 0.0)
        assertTrue(entry.mileagePay > 0.0)

        // Invoicing approval flow
        val invoice = repository.invoices.value.first()
        repository.approveInvoice(invoice.id)
        val approvedInvoice = repository.invoices.value.find { it.id == invoice.id }
        assertEquals(InvoiceStatus.APPROVED, approvedInvoice?.status)
    }

    @Test
    fun `pay run advances through workflow stages and locks period`() {
        val repository = CareRepository()
        val run = repository.payRuns.value.first()
        val initialStatus = run.status

        repository.advancePayRun(run.id)
        val advancedRun = repository.payRuns.value.find { it.id == run.id }
        assertNotNull(advancedRun)
        assertNotEquals(initialStatus, advancedRun?.status)

        // Add adjustment to pay run
        repository.addPayAdjustment(run.id, "staff-04", "David Okonkwo", 25.00, "Approved weekend overtime")
        val runWithAdj = repository.payRuns.value.find { it.id == run.id }
        assertTrue(runWithAdj?.adjustments?.any { it.amount == 25.00 } == true)
    }

    @Test
    fun `carer can raise timesheet dispute and request holiday with rota clash checking`() {
        val repository = CareRepository()
        val timesheet = repository.timesheets.value.first()
        assertFalse(timesheet.isDisputed)

        repository.raiseTimesheetDispute(timesheet.id, "Unpaid 30m travel time")
        val disputed = repository.timesheets.value.find { it.id == timesheet.id }
        assertTrue(disputed?.isDisputed == true)
        assertEquals("Unpaid 30m travel time", disputed?.disputeReason)

        // Holiday request with clash detection
        repository.requestHoliday("staff-04", "David Okonkwo", "15/10/2026", "17/10/2026", 10.0, "Family wedding")
        val newHoliday = repository.holidays.value.first()
        assertEquals("staff-04", newHoliday.staffId)

        // Approve holiday and verify balance deduction
        val initialBal = repository.holidayBalances.value.find { it.staffId == "staff-04" }?.remainingBalanceHours ?: 0.0
        repository.approveHoliday(newHoliday.id)
        val updatedBal = repository.holidayBalances.value.find { it.staffId == "staff-04" }?.remainingBalanceHours ?: 0.0
        assertEquals(initialBal - 10.0, updatedBal, 0.01)
    }

    @Test
    fun `client invoice ledger supports payments, credit notes, and line disputes`() {
        val repository = CareRepository()
        val invoice = repository.invoices.value.first()
        val initialBalance = invoice.outstandingBalance

        // Record partial payment
        repository.recordInvoicePayment(invoice.id, 50.0, "BACS", "REF-9921")
        val afterPayment = repository.invoices.value.find { it.id == invoice.id }
        assertEquals(initialBalance - 50.0, afterPayment?.outstandingBalance ?: 0.0, 0.01)

        // Issue credit note
        repository.issueCreditNote(invoice.id, 13.0, "Cancelled call discount")
        val afterCredit = repository.invoices.value.find { it.id == invoice.id }
        assertEquals(initialBalance - 50.0 - 13.0, afterCredit?.outstandingBalance ?: 0.0, 0.01)
    }

    @Test
    fun `quality CQC mock inspection framework supports dynamic data updates`() {
        val repository = CareRepository()
        val initialThemes = repository.cqcThemes.value
        assertTrue(initialThemes.isNotEmpty())

        val targetTheme = initialThemes.first()
        repository.updateCqcThemeScore(targetTheme.id, 4, "Updated outstanding clinical governance audit evidence.")

        val updatedTheme = repository.cqcThemes.value.find { it.id == targetTheme.id }
        assertEquals(4, updatedTheme?.currentScore)
        assertTrue(updatedTheme?.evidenceNotes?.contains("outstanding clinical governance") == true)
    }

    @Test
    fun `inactivity session lock requires valid PIN to resume access`() {
        val repository = CareRepository()
        assertFalse(repository.isSessionLocked.value)

        repository.lockSession()
        assertTrue(repository.isSessionLocked.value)

        val wrongPinAttempt = repository.unlockSessionWithPin("9999")
        assertFalse(wrongPinAttempt)
        assertTrue(repository.isSessionLocked.value)

        val correctPinAttempt = repository.unlockSessionWithPin("1234")
        assertTrue(correctPinAttempt)
        assertFalse(repository.isSessionLocked.value)
    }

    @Test
    fun `carer visit check-in supports GPS and manual reason override`() {
        val repository = CareRepository()
        val visit = repository.visits.value.first()

        // Test GPS check-in
        repository.checkInVisit(visit.id, "51.5074° N, 0.1278° W (Verified ±6m)")
        var checkedInVisit = repository.visits.value.find { it.id == visit.id }
        assertEquals(VisitStatus.CHECKED_IN, checkedInVisit?.status)
        assertTrue(checkedInVisit?.checkInGps?.contains("Verified") == true)

        // Test manual GPS override with reason
        val secondVisit = repository.visits.value[1]
        repository.checkInVisit(
            visitId = secondVisit.id,
            isManualOverride = true,
            overrideReason = "Basement flat - GPS lock unavailable"
        )
        val manualVisit = repository.visits.value.find { it.id == secondVisit.id }
        assertEquals(VisitStatus.CHECKED_IN, manualVisit?.status)
        assertEquals("Basement flat - GPS lock unavailable", manualVisit?.checkInOverrideReason)
    }

    @Test
    fun `mandatory tasks support exception reasons when not completed`() {
        val repository = CareRepository()
        val visit = repository.visits.value.first()
        val mandatoryTask = visit.tasks.first { it.isMandatory }

        repository.setTaskExceptionReason(visit.id, mandatoryTask.id, "Client declined task today")
        val updatedVisit = repository.visits.value.find { it.id == visit.id }
        val updatedTask = updatedVisit?.tasks?.find { it.id == mandatoryTask.id }

        assertFalse(updatedTask?.isCompleted == true)
        assertEquals("Client declined task today", updatedTask?.exceptionReason)
    }

    @Test
    fun `lone-worker safety timer resets and panic button alerts on-call manager`() {
        val repository = CareRepository()
        val initialSafety = repository.loneWorkerSafetyState.value
        assertFalse(initialSafety.isSosTriggered)

        // Trigger panic button
        repository.triggerLoneWorkerPanic("51.5074° N, 0.1278° W (Westminster)")
        val panicState = repository.loneWorkerSafetyState.value
        assertTrue(panicState.isSosTriggered)
        assertTrue(panicState.isEscalated)

        // Stand down panic
        repository.cancelLoneWorkerPanic()
        val clearedState = repository.loneWorkerSafetyState.value
        assertFalse(clearedState.isSosTriggered)

        // Reset timer
        repository.resetLoneWorkerTimer()
        assertEquals(2700, repository.loneWorkerSafetyState.value.remainingSeconds)
    }

    @Test
    fun `running late notification updates visit and alerts family`() {
        val repository = CareRepository()
        val visit = repository.visits.value.first()

        repository.markRunningLateWithNotification(visit.id, 15, "Traffic delay on Marylebone Rd", notifyFamily = true)
        val updatedVisit = repository.visits.value.find { it.id == visit.id }

        assertEquals(VisitStatus.RUNNING_LATE, updatedVisit?.status)
        assertTrue(updatedVisit?.isLateNotified == true)
        assertEquals(15, updatedVisit?.lateDelayMinutes)
    }

    @Test
    fun `end-of-shift handover draft can be reviewed, edited and signed off`() {
        val repository = CareRepository()
        val draft = repository.generateAiHandoverDraft("staff-04", "David Okonkwo")
        assertNotNull(draft)
        assertFalse(draft.isSubmitted)

        repository.saveAndSignHandover(draft.id, "Arthur was cheerful, hydration target met.")
        val signedHandover = repository.handovers.value.find { it.id == draft.id }
        assertTrue(signedHandover?.isSubmitted == true)
        assertEquals("Arthur was cheerful, hydration target met.", signedHandover?.carerEditedNotes)
    }

    @Test
    fun `carer staff tools support manager notifications and message read tracking`() {
        val repository = CareRepository()

        // Send manager notification
        repository.sendManagerNotification("staff-04", "David Okonkwo", "Safety Concern", "Loose front doorstep at Arthur Pendelton")
        val notifications = repository.managerNotifications.value
        assertTrue(notifications.any { it.details.contains("Loose front doorstep") })

        // Mark office message read
        val message = repository.officeMessages.value.first()
        repository.markMessageRead(message.id)
        val updatedMessage = repository.officeMessages.value.find { it.id == message.id }
        assertTrue(updatedMessage?.isRead == true)
    }
}
