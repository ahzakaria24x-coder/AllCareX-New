package com.example.data

import com.example.model.*
import java.util.UUID

object DemoDataProvider {

    val defaultBranches = listOf(
        Branch(
            id = "branch-wst",
            name = "Westminster & Central London",
            code = "WST-01",
            address = "42 Victoria Street, Westminster, SW1H 0EU",
            phone = "020 7946 0192",
            activeClientsCount = 4,
            activeStaffCount = 5
        ),
        Branch(
            id = "branch-cmd",
            name = "Camden & Islington",
            code = "CMD-02",
            address = "18 Camden High St, London NW1 0JH",
            phone = "020 7946 0541",
            activeClientsCount = 2,
            activeStaffCount = 4
        ),
        Branch(
            id = "branch-knc",
            name = "Kensington & Chelsea",
            code = "KNC-03",
            address = "95 King's Road, Chelsea, SW3 4NX",
            phone = "020 7946 0883",
            activeClientsCount = 2,
            activeStaffCount = 3
        )
    )

    val defaultClients = listOf(
        ClientProfile(
            id = "client-01",
            organisationId = "org-allcarex-01",
            branchId = "branch-wst",
            fullName = "Arthur Pendelton",
            preferredName = "Arthur",
            dateOfBirth = "14/05/1942",
            nhsNumber = "485 777 3456",
            address = "12 St. James Court, Flat 4, London",
            postcode = "SW1A 1AA",
            phone = "020 7946 0991",
            keySafeCode = "4821",
            gpSurgery = "St James Health Centre",
            gpName = "Dr. Helen Vance",
            nextOfKinName = "Clara Pendelton",
            nextOfKinRelationship = "Daughter (Power of Attorney - Health & Welfare)",
            nextOfKinPhone = "07700 900123",
            fundingType = FundingType.LOCAL_AUTHORITY,
            careType = CareType.DOMICILIARY,
            riskLevel = RiskLevel.HIGH,
            allergies = listOf("Penicillin", "Sulfa drugs"),
            consentGiven = true,
            dnacprRespectStatus = true,
            mentalCapacityStatus = MentalCapacityStatus.BEST_INTERESTS,
            requiredCarersCount = 1,
            preferredCarerIds = listOf("staff-04", "staff-05"),
            notesSummary = "Vascular dementia. Requires prompting for meals and steadying aid with Zimmer frame."
        ),
        ClientProfile(
            id = "client-02",
            organisationId = "org-allcarex-01",
            branchId = "branch-cmd",
            fullName = "Margaret 'Maggie' Brown",
            preferredName = "Maggie",
            dateOfBirth = "03/11/1948",
            nhsNumber = "612 884 1923",
            address = "45 Park Way, Camden, London",
            postcode = "NW1 7PN",
            phone = "020 7946 0418",
            keySafeCode = "1973",
            gpSurgery = "Camden Mews Medical Practice",
            gpName = "Dr. Michael Hughes",
            nextOfKinName = "Peter Brown",
            nextOfKinRelationship = "Son",
            nextOfKinPhone = "07700 900456",
            fundingType = FundingType.PRIVATE_SELF_FUNDED,
            careType = CareType.SUPPORTED_LIVING,
            riskLevel = RiskLevel.MEDIUM,
            allergies = listOf("Latex"),
            consentGiven = true,
            dnacprRespectStatus = false,
            mentalCapacityStatus = MentalCapacityStatus.CAPACITY_CONFIRMED,
            requiredCarersCount = 1,
            preferredCarerIds = listOf("staff-04"),
            notesSummary = "Parkinson's Disease. Freezing episodes in morning. Takes Levodopa on strict schedule."
        ),
        ClientProfile(
            id = "client-03",
            organisationId = "org-allcarex-01",
            branchId = "branch-knc",
            fullName = "Tariq Al-Mansoor",
            preferredName = "Tariq",
            dateOfBirth = "22/08/1984",
            nhsNumber = "309 441 5567",
            address = "88 Cromwell Gardens, Apt 2, Kensington",
            postcode = "SW7 4HE",
            phone = "020 7946 0772",
            keySafeCode = "9901",
            gpSurgery = "Brompton Medical Group",
            gpName = "Dr. Sarah Thornton",
            nextOfKinName = "Layla Al-Mansoor",
            nextOfKinRelationship = "Sister",
            nextOfKinPhone = "07700 900789",
            fundingType = FundingType.NHS_CHC,
            careType = CareType.SUPPORTED_LIVING,
            riskLevel = RiskLevel.MEDIUM,
            allergies = emptyList(),
            consentGiven = true,
            dnacprRespectStatus = false,
            mentalCapacityStatus = MentalCapacityStatus.CAPACITY_CONFIRMED,
            requiredCarersCount = 1,
            preferredCarerIds = listOf("staff-08"),
            notesSummary = "Acquired Brain Injury rehab. Active physiotherapy plan. Independent with prompting."
        ),
        ClientProfile(
            id = "client-04",
            organisationId = "org-allcarex-01",
            branchId = "branch-wst",
            fullName = "Evelyn Rose Davies",
            preferredName = "Evelyn",
            dateOfBirth = "19/02/1935",
            nhsNumber = "554 123 9980",
            address = "Room 14, Rose Gardens Suite, Victoria",
            postcode = "SW1E 6AX",
            phone = "020 7946 0225",
            keySafeCode = "Nurse Call Station",
            gpSurgery = "Westminster Cathedral Surgery",
            gpName = "Dr. Julian Blake",
            nextOfKinName = "Simon Davies",
            nextOfKinRelationship = "Son",
            nextOfKinPhone = "07700 900334",
            fundingType = FundingType.NHS_CHC,
            careType = CareType.RESIDENTIAL_NURSING,
            riskLevel = RiskLevel.CRITICAL,
            allergies = listOf("Co-trimoxazole", "Cephalosporins"),
            consentGiven = true,
            dnacprRespectStatus = true,
            mentalCapacityStatus = MentalCapacityStatus.DOLS_AUTHORISED,
            requiredCarersCount = 2,
            preferredCarerIds = listOf("staff-06", "staff-03"),
            notesSummary = "Stroke recovery, high risk of aspiration, pressure relief turn chart q2h, hoist transfer only."
        ),
        ClientProfile(
            id = "client-05",
            organisationId = "org-allcarex-01",
            branchId = "branch-cmd",
            fullName = "Liam O'Connor",
            preferredName = "Liam",
            dateOfBirth = "10/06/1997",
            nhsNumber = "782 990 1234",
            address = "7 Chalcot Crescent, Flat B, Camden",
            postcode = "NW1 8TR",
            phone = "020 7946 0889",
            keySafeCode = "3310",
            gpSurgery = "Regent's Park Practice",
            gpName = "Dr. Anita Patel",
            nextOfKinName = "Maureen O'Connor",
            nextOfKinRelationship = "Mother",
            nextOfKinPhone = "07700 900890",
            fundingType = FundingType.DIRECT_PAYMENTS,
            careType = CareType.SUPPORTED_LIVING,
            riskLevel = RiskLevel.LOW,
            allergies = listOf("Peanuts", "Tree Nuts"),
            consentGiven = true,
            dnacprRespectStatus = false,
            mentalCapacityStatus = MentalCapacityStatus.CAPACITY_CONFIRMED,
            requiredCarersCount = 1,
            preferredCarerIds = listOf("staff-11"),
            notesSummary = "Autism spectrum. Highly visual schedule. Support with budgeting and meal preparation."
        ),
        ClientProfile(
            id = "client-06",
            organisationId = "org-allcarex-01",
            branchId = "branch-knc",
            fullName = "Dorothy 'Dot' Higgins",
            preferredName = "Dot",
            dateOfBirth = "30/09/1940",
            nhsNumber = "419 632 7810",
            address = "21 Cheyne Walk, Flat 3, Chelsea",
            postcode = "SW3 5HL",
            phone = "020 7946 0612",
            keySafeCode = "7721",
            gpSurgery = "Chelsea Green Health Centre",
            gpName = "Dr. Jeremy Clarkson-Reid",
            nextOfKinName = "Susan Higgins",
            nextOfKinRelationship = "Niece",
            nextOfKinPhone = "07700 900551",
            fundingType = FundingType.LOCAL_AUTHORITY,
            careType = CareType.DOMICILIARY,
            riskLevel = RiskLevel.HIGH,
            allergies = listOf("Aspirin", "Ibuprofen"),
            consentGiven = true,
            dnacprRespectStatus = true,
            mentalCapacityStatus = MentalCapacityStatus.CAPACITY_CONFIRMED,
            requiredCarersCount = 1,
            preferredCarerIds = listOf("staff-05"),
            notesSummary = "Congestive heart failure, severe bilateral oedema, falls sensor pendant worn."
        ),
        ClientProfile(
            id = "client-07",
            organisationId = "org-allcarex-01",
            branchId = "branch-wst",
            fullName = "Harpreet Kaur Singh",
            preferredName = "Harpreet",
            dateOfBirth = "12/04/1953",
            nhsNumber = "882 109 4321",
            address = "15 Belgrave Square, Mews 2, Westminster",
            postcode = "SW1X 8PG",
            phone = "020 7946 0341",
            keySafeCode = "8204",
            gpSurgery = "Belgravia Health Surgery",
            gpName = "Dr. Rajeev Sethi",
            nextOfKinName = "Amarjit Singh",
            nextOfKinRelationship = "Husband",
            nextOfKinPhone = "07700 900662",
            fundingType = FundingType.PRIVATE_SELF_FUNDED,
            careType = CareType.DOMICILIARY,
            riskLevel = RiskLevel.MEDIUM,
            allergies = listOf("Codeine phosphate"),
            consentGiven = true,
            dnacprRespectStatus = false,
            mentalCapacityStatus = MentalCapacityStatus.CAPACITY_CONFIRMED,
            requiredCarersCount = 1,
            preferredCarerIds = listOf("staff-04", "staff-06"),
            notesSummary = "Type 2 diabetes. Daily blood glucose monitoring. Left heel ulcer dressing review."
        ),
        ClientProfile(
            id = "client-08",
            organisationId = "org-allcarex-01",
            branchId = "branch-cmd",
            fullName = "George Henderson",
            preferredName = "George",
            dateOfBirth = "08/12/1946",
            nhsNumber = "901 324 8765",
            address = "52 Chalk Farm Road, Camden",
            postcode = "NW1 8AN",
            phone = "020 7946 0119",
            keySafeCode = "6190",
            gpSurgery = "Primrose Hill Health Clinic",
            gpName = "Dr. Chloe Adams",
            nextOfKinName = "Tom Henderson",
            nextOfKinRelationship = "Brother",
            nextOfKinPhone = "07700 900118",
            fundingType = FundingType.LOCAL_AUTHORITY,
            careType = CareType.DOMICILIARY,
            riskLevel = RiskLevel.MEDIUM,
            allergies = emptyList(),
            consentGiven = true,
            dnacprRespectStatus = true,
            mentalCapacityStatus = MentalCapacityStatus.CAPACITY_CONFIRMED,
            requiredCarersCount = 1,
            preferredCarerIds = listOf("staff-04"),
            notesSummary = "Severe COPD. Home oxygen concentrator at 2L/min. Shortness of breath on exertion."
        )
    )

    val defaultStaff = listOf(
        StaffProfile(
            id = "staff-01",
            organisationId = "org-allcarex-01",
            branchId = "branch-wst",
            fullName = "Eleanor Vance",
            email = "e.vance@allcarex.co.uk",
            phone = "07700 901001",
            role = UserRole.REGISTERED_MANAGER,
            contractType = ContractType.FULL_TIME,
            rightToWorkVerified = true,
            rightToWorkExpiry = "14/08/2028",
            dbsCertificateNumber = "001882910394",
            dbsEnhancedStatus = true,
            dbsIssueDate = "10/01/2024",
            dbsExpiryDate = "10/01/2027",
            dbsUpdateServiceRegistered = true,
            careCertificateCompleted = true,
            compliancePercentage = 100,
            lastSupervisionDate = "12/08/2026",
            nextSupervisionDueDate = "12/11/2026",
            annualAppraisalDueDate = "10/01/2027",
            skills = listOf("Leadership", "CQC Compliance", "Safeguarding Level 3", "Moving & Handling Train-the-Trainer"),
            hoursWorkedThisWeek = 37.5f
        ),
        StaffProfile(
            id = "staff-02",
            organisationId = "org-allcarex-01",
            branchId = "branch-wst",
            fullName = "Marcus Thorne",
            email = "m.thorne@allcarex.co.uk",
            phone = "07700 901002",
            role = UserRole.COORDINATOR,
            contractType = ContractType.FULL_TIME,
            rightToWorkVerified = true,
            rightToWorkExpiry = "19/03/2029",
            dbsCertificateNumber = "001992019481",
            dbsEnhancedStatus = true,
            dbsIssueDate = "05/02/2024",
            dbsExpiryDate = "05/02/2027",
            dbsUpdateServiceRegistered = true,
            careCertificateCompleted = true,
            compliancePercentage = 95,
            lastSupervisionDate = "01/09/2026",
            nextSupervisionDueDate = "01/12/2026",
            annualAppraisalDueDate = "05/02/2027",
            skills = listOf("Rota Scheduling", "Skills Matching", "Conflict Resolution", "Emergency Response"),
            hoursWorkedThisWeek = 38.0f
        ),
        StaffProfile(
            id = "staff-03",
            organisationId = "org-allcarex-01",
            branchId = "branch-wst",
            fullName = "Sarah Jenkins",
            email = "s.jenkins@allcarex.co.uk",
            phone = "07700 901003",
            role = UserRole.SENIOR_CARER,
            contractType = ContractType.FULL_TIME,
            rightToWorkVerified = true,
            rightToWorkExpiry = "30/09/2028",
            dbsCertificateNumber = "002019485122",
            dbsEnhancedStatus = true,
            dbsIssueDate = "18/03/2024",
            dbsExpiryDate = "18/03/2027",
            dbsUpdateServiceRegistered = true,
            careCertificateCompleted = true,
            compliancePercentage = 98,
            lastSupervisionDate = "15/07/2026",
            nextSupervisionDueDate = "15/10/2026",
            annualAppraisalDueDate = "18/03/2027",
            skills = listOf("Complex Dementia", "Palliative Care", "Controlled Drugs", "NEWS2 Scoring", "Waterlow"),
            hoursWorkedThisWeek = 35.0f
        ),
        StaffProfile(
            id = "staff-04",
            organisationId = "org-allcarex-01",
            branchId = "branch-cmd",
            fullName = "David Okonkwo",
            email = "d.okonkwo@allcarex.co.uk",
            phone = "07700 901004",
            role = UserRole.CARE_WORKER,
            contractType = ContractType.GUARANTEED_HOURS,
            rightToWorkVerified = true,
            rightToWorkExpiry = "22/11/2027",
            dbsCertificateNumber = "002184920194",
            dbsEnhancedStatus = true,
            dbsIssueDate = "20/04/2024",
            dbsExpiryDate = "20/04/2027",
            dbsUpdateServiceRegistered = true,
            careCertificateCompleted = true,
            compliancePercentage = 92,
            lastSupervisionDate = "20/08/2026",
            nextSupervisionDueDate = "20/11/2026",
            annualAppraisalDueDate = "20/04/2027",
            skills = listOf("Moving & Handling", "Medication Administration", "Infection Control", "First Aid"),
            hoursWorkedThisWeek = 32.5f
        ),
        StaffProfile(
            id = "staff-05",
            organisationId = "org-allcarex-01",
            branchId = "branch-wst",
            fullName = "Chloe Bennett",
            email = "c.bennett@allcarex.co.uk",
            phone = "07700 901005",
            role = UserRole.CARE_WORKER,
            contractType = ContractType.FULL_TIME,
            rightToWorkVerified = true,
            rightToWorkExpiry = "10/05/2028",
            dbsCertificateNumber = "002271894012",
            dbsEnhancedStatus = true,
            dbsIssueDate = "15/06/2024",
            dbsExpiryDate = "15/06/2027",
            dbsUpdateServiceRegistered = true,
            careCertificateCompleted = true,
            compliancePercentage = 90,
            lastSupervisionDate = "10/08/2026",
            nextSupervisionDueDate = "10/11/2026",
            annualAppraisalDueDate = "15/06/2027",
            skills = listOf("Moving & Handling", "Catheter Care", "eMAR", "Dementia Care"),
            hoursWorkedThisWeek = 36.0f
        ),
        StaffProfile(
            id = "staff-06",
            organisationId = "org-allcarex-01",
            branchId = "branch-wst",
            fullName = "Priya Patel",
            email = "p.patel@allcarex.co.uk",
            phone = "07700 901006",
            role = UserRole.NURSE,
            contractType = ContractType.FULL_TIME,
            rightToWorkVerified = true,
            rightToWorkExpiry = "15/01/2029",
            dbsCertificateNumber = "002349018423",
            dbsEnhancedStatus = true,
            dbsIssueDate = "01/02/2024",
            dbsExpiryDate = "01/02/2027",
            dbsUpdateServiceRegistered = true,
            careCertificateCompleted = true,
            compliancePercentage = 100,
            lastSupervisionDate = "05/09/2026",
            nextSupervisionDueDate = "05/12/2026",
            annualAppraisalDueDate = "01/02/2027",
            skills = listOf("NMC Registered", "Wound Care & Dressing", "Tracheostomy", "Peg Feeding", "Palliative Syringe Driver"),
            hoursWorkedThisWeek = 37.5f
        ),
        StaffProfile(
            id = "staff-07",
            organisationId = "org-allcarex-01",
            branchId = "branch-cmd",
            fullName = "James Wilson",
            email = "j.wilson@allcarex.co.uk",
            phone = "07700 901007",
            role = UserRole.CARE_WORKER,
            contractType = ContractType.ZERO_HOUR,
            rightToWorkVerified = true,
            rightToWorkExpiry = "01/10/2026",
            dbsCertificateNumber = "001748291034",
            dbsEnhancedStatus = true,
            dbsIssueDate = "15/08/2023",
            dbsExpiryDate = "15/08/2026", // Expired!
            dbsUpdateServiceRegistered = false,
            careCertificateCompleted = false,
            compliancePercentage = 64, // Traffic Light: RED
            lastSupervisionDate = "10/04/2026",
            nextSupervisionDueDate = "10/07/2026",
            annualAppraisalDueDate = "15/08/2026",
            skills = listOf("Basic Personal Care"),
            hoursWorkedThisWeek = 14.0f,
            isAvailable = false // Blocked due to expired compliance!
        ),
        StaffProfile(
            id = "staff-08",
            organisationId = "org-allcarex-01",
            branchId = "branch-knc",
            fullName = "Fatimah Al-Sayed",
            email = "f.alsayed@allcarex.co.uk",
            phone = "07700 901008",
            role = UserRole.SENIOR_CARER,
            contractType = ContractType.FULL_TIME,
            rightToWorkVerified = true,
            rightToWorkExpiry = "11/04/2029",
            dbsCertificateNumber = "002490184729",
            dbsEnhancedStatus = true,
            dbsIssueDate = "12/03/2024",
            dbsExpiryDate = "12/03/2027",
            dbsUpdateServiceRegistered = true,
            careCertificateCompleted = true,
            compliancePercentage = 96,
            lastSupervisionDate = "18/08/2026",
            nextSupervisionDueDate = "18/11/2026",
            annualAppraisalDueDate = "12/03/2027",
            skills = listOf("Brain Injury Support", "Epilepsy Buccal Midazolam", "Stoma Care", "MCA & DoLS"),
            hoursWorkedThisWeek = 37.0f
        ),
        StaffProfile(
            id = "staff-09",
            organisationId = "org-allcarex-01",
            branchId = "branch-wst",
            fullName = "Oliver Smith",
            email = "o.smith@allcarex.co.uk",
            phone = "07700 901009",
            role = UserRole.HR_RECRUITER,
            contractType = ContractType.FULL_TIME,
            rightToWorkVerified = true,
            rightToWorkExpiry = "05/12/2029",
            dbsCertificateNumber = "002591028472",
            dbsEnhancedStatus = true,
            dbsIssueDate = "22/02/2024",
            dbsExpiryDate = "22/02/2027",
            dbsUpdateServiceRegistered = true,
            careCertificateCompleted = true,
            compliancePercentage = 100,
            lastSupervisionDate = "01/09/2026",
            nextSupervisionDueDate = "01/12/2026",
            annualAppraisalDueDate = "22/02/2027",
            skills = listOf("Safer Recruitment", "Right to Work Checks", "DBS Barred List", "Staff Appraisals"),
            hoursWorkedThisWeek = 37.5f
        ),
        StaffProfile(
            id = "staff-10",
            organisationId = "org-allcarex-01",
            branchId = "branch-wst",
            fullName = "Hannah Croft",
            email = "h.croft@allcarex.co.uk",
            phone = "07700 901010",
            role = UserRole.FINANCE,
            contractType = ContractType.FULL_TIME,
            rightToWorkVerified = true,
            rightToWorkExpiry = "14/06/2029",
            dbsCertificateNumber = "002619482019",
            dbsEnhancedStatus = false,
            dbsIssueDate = "09/01/2024",
            dbsExpiryDate = "09/01/2027",
            dbsUpdateServiceRegistered = true,
            careCertificateCompleted = false,
            compliancePercentage = 95,
            lastSupervisionDate = "15/08/2026",
            nextSupervisionDueDate = "15/11/2026",
            annualAppraisalDueDate = "09/01/2027",
            skills = listOf("Invoicing", "Local Authority Split Billing", "Payroll Reconciliation", "Mileage Audits"),
            hoursWorkedThisWeek = 37.5f
        ),
        StaffProfile(
            id = "staff-11",
            organisationId = "org-allcarex-01",
            branchId = "branch-cmd",
            fullName = "Robert Evans",
            email = "r.evans@allcarex.co.uk",
            phone = "07700 901011",
            role = UserRole.CARE_WORKER,
            contractType = ContractType.PART_TIME,
            rightToWorkVerified = true,
            rightToWorkExpiry = "18/09/2028",
            dbsCertificateNumber = "002789104821",
            dbsEnhancedStatus = true,
            dbsIssueDate = "04/05/2024",
            dbsExpiryDate = "04/05/2027",
            dbsUpdateServiceRegistered = true,
            careCertificateCompleted = true,
            compliancePercentage = 88,
            lastSupervisionDate = "12/07/2026",
            nextSupervisionDueDate = "12/10/2026",
            annualAppraisalDueDate = "04/05/2027",
            skills = listOf("Autism & PBS", "Positive Communication", "Meal Prep", "Community Outings"),
            hoursWorkedThisWeek = 20.0f
        ),
        StaffProfile(
            id = "staff-12",
            organisationId = "org-allcarex-01",
            branchId = "branch-wst",
            fullName = "Dr. Alistair Finch",
            email = "a.finch@cqc-auditors.co.uk",
            phone = "07700 901012",
            role = UserRole.EXTERNAL_AUDITOR,
            contractType = ContractType.PART_TIME,
            rightToWorkVerified = true,
            rightToWorkExpiry = "30/12/2027",
            dbsCertificateNumber = "002891048192",
            dbsEnhancedStatus = true,
            dbsIssueDate = "11/11/2024",
            dbsExpiryDate = "11/11/2027",
            dbsUpdateServiceRegistered = true,
            careCertificateCompleted = true,
            compliancePercentage = 100,
            lastSupervisionDate = "01/01/2026",
            nextSupervisionDueDate = "01/01/2027",
            annualAppraisalDueDate = "11/11/2027",
            skills = listOf("CQC Single Assessment Framework", "Clinical Governance", "Statutory Audit"),
            hoursWorkedThisWeek = 10.0f
        )
    )

    fun createInitialCarePlans(): List<CarePlanVersion> {
        return listOf(
            CarePlanVersion(
                id = "cp-01",
                clientId = "client-01",
                versionNumber = 3,
                status = CarePlanStatus.APPROVED,
                title = "Person-Centred Independence & Mobility Plan",
                summary = "Support Arthur with morning routine, personal hygiene, breakfast preparation, and reassurance due to vascular memory decline. Maintain dignified independence with gentle prompts.",
                personalOutcomes = listOf(
                    "Remain safely in own home with familiar surroundings",
                    "Maintain mobility with Zimmer frame without falling",
                    "Enjoy listening to Classic FM with morning tea"
                ),
                nutritionHydrationPlan = "Encourage 1.5L fluid daily. Prefers Earl Grey tea with 1 sugar. Diabetic-friendly breakfast porridge.",
                mobilityMovingHandlingPlan = "1-carer assistance. Always ensure Zimmer frame within reach before standing. Non-slip socks or fitted slippers mandatory.",
                medicationSupportPlan = "Level 2 medication support: Carer to prompt and observe ingestion from blister pack, record on eMAR.",
                communicationNeeds = "Speak clearly facing Arthur. Give one simple choice at a time. Reassure if confused about time.",
                emergencyContingency = "Call daughter Clara first (07700 900123). If uncontactable or fall occurs with head injury, dial 999.",
                isAiDraft = false,
                createdBy = "Eleanor Vance (Registered Manager)",
                createdDate = "10/08/2026",
                approvedBy = "Eleanor Vance",
                approvedDate = "12/08/2026",
                nextReviewDate = "12/11/2026"
            ),
            CarePlanVersion(
                id = "cp-04",
                clientId = "client-04",
                versionNumber = 4,
                status = CarePlanStatus.APPROVED,
                title = "Complex Nursing & Post-Stroke Care Plan",
                summary = "Comprehensive 24/7 care package. Two carers required for all manual handling and hoist transfers. Rigorous skin breakdown prevention and aspiration pneumonia vigilance.",
                personalOutcomes = listOf(
                    "Prevent skin integrity breakdown and pressure ulcers",
                    "Safe nutrition via modified IDDSI Level 4 pureed diet",
                    "Maintain comfort, dignified interaction, and family contact"
                ),
                nutritionHydrationPlan = "IDDSI Level 4 pureed foods and Level 3 mildly thick fluids. Upright 90-degree seating for all feeds and 30 mins after.",
                mobilityMovingHandlingPlan = "Strictly 2-carer transfer using Invacare Birdie Hoist with medium clip sling. Re-position in bed every 2 hours.",
                medicationSupportPlan = "Level 3 administration by Registered Nurse or Medication Competent Senior Carer. Crushed meds where approved by pharmacist.",
                communicationNeeds = "Dysphasia. Uses pictorial communication board and thumbs up/down gesture.",
                emergencyContingency = "Active ReSPECT form. Do not attempt CPR. Palliate symptoms, contact GP and next of kin Simon Davies immediately.",
                isAiDraft = false,
                createdBy = "Priya Patel (Registered Nurse)",
                createdDate = "01/09/2026",
                approvedBy = "Eleanor Vance (Registered Manager)",
                approvedDate = "03/09/2026",
                nextReviewDate = "03/12/2026"
            )
        )
    }

    fun createInitialMedications(): List<Medication> {
        return listOf(
            Medication(
                id = "med-01",
                organisationId = "org-allcarex-01",
                branchId = "branch-wst",
                clientId = "client-01",
                name = "Ramipril",
                dose = "5mg",
                route = "Oral (Tablet)",
                frequencyDescription = "Once daily in morning",
                scheduledTimes = listOf("08:00"),
                prescriber = "Dr. Helen Vance (St James Surgery)",
                startDate = "15/01/2025",
                currentStock = 24,
                warningThresholdStock = 7,
                specialInstructions = "Monitor blood pressure if dizzy."
            ),
            Medication(
                id = "med-02",
                organisationId = "org-allcarex-01",
                branchId = "branch-wst",
                clientId = "client-01",
                name = "Donepezil Hydrochloride",
                dose = "10mg",
                route = "Oral (Tablet)",
                frequencyDescription = "Once daily at bedtime",
                scheduledTimes = listOf("20:00"),
                prescriber = "Dr. Helen Vance",
                startDate = "20/03/2025",
                currentStock = 18,
                warningThresholdStock = 7,
                specialInstructions = "Take with water before sleep."
            ),
            Medication(
                id = "med-03",
                organisationId = "org-allcarex-01",
                branchId = "branch-wst",
                clientId = "client-01",
                name = "Paracetamol (PRN)",
                dose = "500mg (1-2 tablets)",
                route = "Oral",
                frequencyDescription = "As required for mild pain/pyrexia",
                scheduledTimes = listOf("PRN"),
                isPrn = true,
                prnProtocol = "1 or 2 tablets every 4 to 6 hours as needed. Maximum 8 tablets (4000mg) in 24 hours. Check blister pack history before giving.",
                prescriber = "Dr. Helen Vance",
                startDate = "01/01/2025",
                currentStock = 32,
                warningThresholdStock = 10,
                specialInstructions = "Record reason for giving in daily MAR notes."
            ),
            Medication(
                id = "med-04",
                organisationId = "org-allcarex-01",
                branchId = "branch-cmd",
                clientId = "client-02",
                name = "Co-careldopa (Sinemet Plus)",
                dose = "25mg/100mg",
                route = "Oral (Tablet)",
                frequencyDescription = "Three times daily at exact times",
                scheduledTimes = listOf("08:00", "13:00", "18:00"),
                prescriber = "Dr. Michael Hughes (Camden Practice)",
                startDate = "10/02/2024",
                currentStock = 45,
                warningThresholdStock = 12,
                specialInstructions = "CRITICAL TIMING: Must be taken 30 minutes before food to prevent freezing."
            ),
            Medication(
                id = "med-05",
                organisationId = "org-allcarex-01",
                branchId = "branch-wst",
                clientId = "client-04",
                name = "Morphine Sulfate Oral Solution (Oramorph)",
                dose = "5mg / 2.5ml",
                route = "Oral Liquid",
                frequencyDescription = "Every 4 hours PRN for breakthrough pain",
                scheduledTimes = listOf("PRN"),
                isPrn = true,
                isControlledDrug = true, // Schedule 2 Controlled Drug - requires double signature!
                prnProtocol = "2.5ml (5mg) orally for breakthrough palliative pain. Maximum 30mg in 24 hours. Double signature required on eMAR.",
                prescriber = "Dr. Julian Blake (Palliative Consultant)",
                startDate = "20/08/2026",
                currentStock = 85, // ml
                warningThresholdStock = 30,
                specialInstructions = "CONTROLLED DRUG: Count balance before and after administering. Both carers must verify dose."
            ),
            Medication(
                id = "med-06",
                organisationId = "org-allcarex-01",
                branchId = "branch-wst",
                clientId = "client-04",
                name = "Atorvastatin",
                dose = "40mg",
                route = "Oral (Tablet)",
                frequencyDescription = "Once daily at night",
                scheduledTimes = listOf("20:00"),
                prescriber = "Dr. Julian Blake",
                startDate = "01/06/2025",
                currentStock = 5, // LOW STOCK TRIGGER
                warningThresholdStock = 7,
                specialInstructions = "LOW STOCK WARNING: Reorder prescription immediately from GP surgery."
            )
        )
    }

    fun createInitialVisits(): List<Visit> {
        return listOf(
            Visit(
                id = "visit-01",
                organisationId = "org-allcarex-01",
                branchId = "branch-wst",
                clientId = "client-01",
                clientName = "Arthur Pendelton",
                clientAddress = "12 St. James Court, Flat 4",
                clientPostcode = "SW1A 1AA",
                carerId = "staff-04",
                carerName = "David Okonkwo",
                date = "03/10/2026",
                startTime = "08:00",
                endTime = "08:45",
                visitType = "Morning Call (Personal Care & Breakfast)",
                status = VisitStatus.SCHEDULED,
                tasks = listOf(
                    VisitTask(title = "Assist with washing & dressing", category = "Personal Care", isMandatory = true),
                    VisitTask(title = "Morning medication prompt (Ramipril 5mg)", category = "Medication", isMandatory = true),
                    VisitTask(title = "Prepare porridge and Earl Grey tea", category = "Meal Prep", isMandatory = true),
                    VisitTask(title = "Ensure Zimmer frame is accessible", category = "Mobility", isMandatory = true),
                    VisitTask(title = "Record fluid intake (goal 300ml)", category = "Fluid Check", isMandatory = true)
                ),
                travelTimeMinutes = 12,
                requiredSkills = listOf("Moving & Handling", "Medication Admin", "Dementia Awareness"),
                alerts = listOf(
                    "CARE PLAN CHANGE: Mobilises with Zimmer frame (1x carer assist required)",
                    "MEDICATION CHANGE: Ramipril dosage confirmed 5mg daily",
                    "RISK FLAG: High fall risk history (recent living room slip)"
                ),
                keySafeCode = "#4920",
                allergies = listOf("Penicillin", "Latex"),
                dnacprStatus = true,
                clientPreferences = "Prefers Earl Grey with 1 sugar before washing; radio tuned to Classic FM.",
                nextVisitNotes = "Check milk level in fridge on lunch call."
            ),
            Visit(
                id = "visit-02",
                organisationId = "org-allcarex-01",
                branchId = "branch-cmd",
                clientId = "client-02",
                clientName = "Margaret 'Maggie' Brown",
                clientAddress = "45 Park Way, Camden",
                clientPostcode = "NW1 7PN",
                carerId = "staff-05",
                carerName = "Chloe Bennett",
                date = "03/10/2026",
                startTime = "08:00",
                endTime = "08:30",
                visitType = "Morning Call & Parkinson's Med",
                status = VisitStatus.SCHEDULED,
                tasks = listOf(
                    VisitTask(title = "Administer Sinemet Plus at 08:00 sharp", category = "Medication", isMandatory = true),
                    VisitTask(title = "Assist out of bed & support steadying", category = "Mobility", isMandatory = true),
                    VisitTask(title = "Breakfast toast & fresh orange juice", category = "Meal Prep", isMandatory = false)
                ),
                travelTimeMinutes = 15,
                requiredSkills = listOf("Parkinson's Support", "Medication Admin"),
                alerts = listOf("TIMED MED: Sinemet Plus must be taken with water at 08:00 sharp"),
                keySafeCode = "#3310",
                allergies = listOf("Aspirin"),
                dnacprStatus = false,
                clientPreferences = "Likes blinds opened fully upon entry; slippers placed by bed."
            ),
            Visit(
                id = "visit-03",
                organisationId = "org-allcarex-01",
                branchId = "branch-wst",
                clientId = "client-04",
                clientName = "Evelyn Rose Davies",
                clientAddress = "Room 14, Rose Gardens Suite, Victoria",
                clientPostcode = "SW1E 6AX",
                carerId = "staff-06",
                carerName = "Priya Patel (Nurse)",
                secondCarerId = "staff-03",
                secondCarerName = "Sarah Jenkins",
                date = "03/10/2026",
                startTime = "09:00",
                endTime = "10:00",
                visitType = "Double-Up Nursing Care & Repositioning",
                status = VisitStatus.SCHEDULED,
                tasks = listOf(
                    VisitTask(title = "Two-carer hoist transfer to comfy armchair", category = "Mobility", isMandatory = true),
                    VisitTask(title = "Sacrum and heel skin integrity inspection", category = "Skin Care", isMandatory = true),
                    VisitTask(title = "IDDSI pureed breakfast feed & hydration", category = "Nutrition", isMandatory = true),
                    VisitTask(title = "Oral hygiene and moisture gel", category = "Personal Care", isMandatory = true)
                ),
                travelTimeMinutes = 10,
                requiredSkills = listOf("Hoist Operations", "Double-Handed", "Clinical Assessment"),
                alerts = listOf("DOUBLE-UP: Must wait for second carer (Sarah Jenkins) before hoist transfer"),
                keySafeCode = "#1092",
                allergies = listOf("Co-amoxiclav", "Sulfa drugs"),
                dnacprStatus = true,
                clientPreferences = "Gentle head repositioning every 30 minutes. Lavender moisturiser."
            ),
            Visit(
                id = "visit-04",
                organisationId = "org-allcarex-01",
                branchId = "branch-wst",
                clientId = "client-01",
                clientName = "Arthur Pendelton",
                clientAddress = "12 St. James Court, Flat 4",
                clientPostcode = "SW1A 1AA",
                carerId = "staff-04",
                carerName = "David Okonkwo",
                date = "03/10/2026",
                startTime = "12:30",
                endTime = "13:15",
                visitType = "Lunch Call (Meal & Hydration)",
                status = VisitStatus.SCHEDULED,
                tasks = listOf(
                    VisitTask(title = "Heat tomato soup and prepare ham sandwich", category = "Meal Prep", isMandatory = true),
                    VisitTask(title = "Offer warm tea and record fluid (target 250ml)", category = "Fluid Check", isMandatory = true),
                    VisitTask(title = "Prompt midday Paracetamol 500mg", category = "Medication", isMandatory = true),
                    VisitTask(title = "Toilet assistance & check skin hygiene", category = "Personal Care", isMandatory = true)
                ),
                travelTimeMinutes = 12,
                alerts = listOf("HYDRATION ALERT: Daily target minimum 1,200ml - monitor intake carefully"),
                keySafeCode = "#4920",
                allergies = listOf("Penicillin", "Latex"),
                dnacprStatus = true,
                clientPreferences = "Sandwich cut into quarters with crusts on. Tea lukewarm."
            ),
            Visit(
                id = "visit-05",
                organisationId = "org-allcarex-01",
                branchId = "branch-knc",
                clientId = "client-06",
                clientName = "Dorothy 'Dot' Higgins",
                clientAddress = "21 Cheyne Walk, Flat 3",
                clientPostcode = "SW3 5HL",
                carerId = null, // UNASSIGNED VISIT ALERT!
                carerName = null,
                date = "03/10/2026",
                startTime = "11:00",
                endTime = "11:45",
                visitType = "Mid-Morning Welfare & Oedema Check",
                status = VisitStatus.SCHEDULED,
                tasks = listOf(
                    VisitTask(title = "Check lower limb oedema elevation", category = "Clinical Check", isMandatory = true),
                    VisitTask(title = "Prepare light snack and hydration", category = "Meal Prep", isMandatory = true),
                    VisitTask(title = "Empty commode and sanitize", category = "Hygiene", isMandatory = true)
                ),
                travelTimeMinutes = 15
            ),
            Visit(
                id = "visit-06",
                organisationId = "org-allcarex-01",
                branchId = "branch-wst",
                clientId = "client-07",
                clientName = "Harpreet Kaur Singh",
                clientAddress = "15 Belgrave Square, Mews 2, Westminster",
                clientPostcode = "SW1X 8PG",
                carerId = "staff-04",
                carerName = "David Okonkwo",
                date = "03/10/2026",
                startTime = "16:00",
                endTime = "16:45",
                visitType = "Tea Call & Diabetes Check",
                status = VisitStatus.SCHEDULED,
                tasks = listOf(
                    VisitTask(title = "Blood glucose check record in eMAR", category = "Clinical Check", isMandatory = true),
                    VisitTask(title = "Prepare light dhal and roti meal", category = "Meal Prep", isMandatory = true),
                    VisitTask(title = "Inspect left heel ulcer dressing (ensure dry)", category = "Skin Care", isMandatory = true),
                    VisitTask(title = "Offer cardamom tea with fluid record (200ml)", category = "Fluid Check", isMandatory = true)
                ),
                travelTimeMinutes = 14,
                alerts = listOf("CLINICAL ALERT: Left heel dressing must remain dry and intact"),
                keySafeCode = "#8204",
                allergies = listOf("Codeine phosphate"),
                dnacprStatus = false,
                clientPreferences = "Prefers shoes removed at the front porch. Traditional spiced tea."
            ),
            Visit(
                id = "visit-07",
                organisationId = "org-allcarex-01",
                branchId = "branch-wst",
                clientId = "client-01",
                clientName = "Arthur Pendelton",
                clientAddress = "12 St. James Court, Flat 4",
                clientPostcode = "SW1A 1AA",
                carerId = "staff-04",
                carerName = "David Okonkwo",
                date = "03/10/2026",
                startTime = "20:00",
                endTime = "20:45",
                visitType = "Bed Call (Night Routine & Lockup)",
                status = VisitStatus.SCHEDULED,
                tasks = listOf(
                    VisitTask(title = "Assist into nightwear and wash face/hands", category = "Personal Care", isMandatory = true),
                    VisitTask(title = "Administer night medication (Atorvastatin 20mg)", category = "Medication", isMandatory = true),
                    VisitTask(title = "Ensure glass of water on bedside coaster", category = "Hydration", isMandatory = true),
                    VisitTask(title = "Ensure emergency call pendant is around neck", category = "Safety", isMandatory = true),
                    VisitTask(title = "Double lock front door and secure key in safe", category = "Security", isMandatory = true)
                ),
                travelTimeMinutes = 10,
                alerts = listOf("SECURITY: Check front door deadbolt locked before leaving"),
                keySafeCode = "#4920",
                allergies = listOf("Penicillin", "Latex"),
                dnacprStatus = true,
                clientPreferences = "Night light left switched on in hallway; bedroom door ajar."
            ),
            // Tomorrow's visits (Next 48 Hours Cache)
            Visit(
                id = "visit-08",
                organisationId = "org-allcarex-01",
                branchId = "branch-wst",
                clientId = "client-01",
                clientName = "Arthur Pendelton",
                clientAddress = "12 St. James Court, Flat 4",
                clientPostcode = "SW1A 1AA",
                carerId = "staff-04",
                carerName = "David Okonkwo",
                date = "04/10/2026",
                startTime = "08:00",
                endTime = "08:45",
                visitType = "Morning Call (Personal Care & Breakfast)",
                status = VisitStatus.SCHEDULED,
                tasks = listOf(
                    VisitTask(title = "Assist with washing & dressing", category = "Personal Care", isMandatory = true),
                    VisitTask(title = "Morning medication prompt (Ramipril 5mg)", category = "Medication", isMandatory = true),
                    VisitTask(title = "Breakfast & fluid record", category = "Meal Prep", isMandatory = true)
                ),
                travelTimeMinutes = 12,
                alerts = listOf("CACHED FOR OFFLINE USE (48h Rota Active)"),
                keySafeCode = "#4920",
                allergies = listOf("Penicillin", "Latex"),
                dnacprStatus = true
            ),
            Visit(
                id = "visit-09",
                organisationId = "org-allcarex-01",
                branchId = "branch-wst",
                clientId = "client-07",
                clientName = "Harpreet Kaur Singh",
                clientAddress = "15 Belgrave Square, Mews 2",
                clientPostcode = "SW1X 8PG",
                carerId = "staff-04",
                carerName = "David Okonkwo",
                date = "04/10/2026",
                startTime = "12:30",
                endTime = "13:15",
                visitType = "Lunch Call & Blood Sugar Review",
                status = VisitStatus.SCHEDULED,
                tasks = listOf(
                    VisitTask(title = "Blood glucose check record", category = "Clinical Check", isMandatory = true),
                    VisitTask(title = "Prepare warm lunch", category = "Meal Prep", isMandatory = true)
                ),
                travelTimeMinutes = 14,
                keySafeCode = "#8204"
            )
        )
    }

    fun createInitialIncidents(): List<IncidentReport> {
        return listOf(
            IncidentReport(
                id = "inc-01",
                organisationId = "org-allcarex-01",
                branchId = "branch-wst",
                clientId = "client-01",
                clientName = "Arthur Pendelton",
                reportedByStaffId = "staff-04",
                reportedByStaffName = "David Okonkwo",
                incidentDate = "28/09/2026",
                incidentTime = "08:15",
                category = IncidentCategory.FALL,
                severity = IncidentSeverity.MODERATE,
                location = "Living room carpet near bookshelf",
                description = "Arthur slipped while attempting to retrieve a photo album without using his Zimmer frame. Found seated on floor against armchair. No loss of consciousness.",
                immediateActionsTaken = "Full 10-point physical check. No head strike. Left forearm minor skin tear dressed with sterile mepilex. Assisted up using Mangar Elk lifting cushion. GP and daughter notified.",
                paramedicsCalled = false,
                isCqcNotifiable = false,
                isLocalAuthoritySafeguardingReferral = false,
                dutyOfCandourCompleted = true,
                dutyOfCandourNotes = "Daughter Clara contacted at 08:30 via phone. Incident log copy emailed.",
                investigationStatus = "Closed",
                lessonsLearned = "Arthur's photo albums moved from top shelf to coffee table at waist height to prevent overreaching."
            ),
            IncidentReport(
                id = "inc-02",
                organisationId = "org-allcarex-01",
                branchId = "branch-wst",
                clientId = "client-04",
                clientName = "Evelyn Rose Davies",
                reportedByStaffId = "staff-06",
                reportedByStaffName = "Priya Patel",
                incidentDate = "01/10/2026",
                incidentTime = "14:20",
                category = IncidentCategory.PRESSURE_INJURY,
                severity = IncidentSeverity.SEVERE,
                location = "Bed - Sacral region",
                description = "During turn and hygiene routine, noted new Category 2 pressure damage on right lower sacrum (approx 1.5cm x 1cm blistered skin break).",
                immediateActionsTaken = "Cavilon barrier film applied followed by foam dressing. Tissue Viability Nurse (TVN) urgent referral submitted. Air-flow dynamic mattress pressure checked and calibrated.",
                paramedicsCalled = false,
                isCqcNotifiable = true, // CQC Reg 18 notification
                isLocalAuthoritySafeguardingReferral = true,
                safeguardingReferenceNumber = "WST-SG-2026-0914",
                dutyOfCandourCompleted = true,
                dutyOfCandourNotes = "Son Simon met in person and written duty of candour letter provided.",
                investigationStatus = "Under Investigation",
                lessonsLearned = "Turn chart interval reduced from 3 hours to strictly 2 hours. Repositioning alarm enabled."
            )
        )
    }

    fun createInitialTraining(): List<TrainingRecord> {
        val modules = listOf(
            "Moving & Handling (Practical)",
            "Safeguarding Adults (Level 2/3)",
            "Infection Prevention & Control",
            "Medication Competency & eMAR",
            "First Aid & Basic Life Support",
            "Fire Safety & Evacuation",
            "Food Hygiene & Safety",
            "Oliver McGowan LD & Autism Training",
            "Mental Capacity Act (MCA) & DoLS"
        )

        val list = mutableListOf<TrainingRecord>()
        // staff-04 (David)
        modules.forEachIndexed { i, mod ->
            list.add(
                TrainingRecord(
                    staffId = "staff-04",
                    moduleName = mod,
                    isMandatory = true,
                    completedDate = "15/04/2026",
                    expiryDate = "15/04/2027",
                    scorePercentage = 95 - (i * 2),
                    certificateRef = "CERT-UK-994$i",
                    isExpired = false
                )
            )
        }
        // staff-07 (James Wilson - Expired records)
        list.add(
            TrainingRecord(
                staffId = "staff-07",
                moduleName = "Moving & Handling (Practical)",
                isMandatory = true,
                completedDate = "10/08/2024",
                expiryDate = "10/08/2025",
                scorePercentage = 80,
                certificateRef = "CERT-EX-101",
                isExpired = true
            )
        )
        list.add(
            TrainingRecord(
                staffId = "staff-07",
                moduleName = "Medication Competency & eMAR",
                isMandatory = true,
                completedDate = "12/08/2024",
                expiryDate = "12/08/2025",
                scorePercentage = 75,
                certificateRef = "CERT-EX-102",
                isExpired = true
            )
        )
        return list
    }

    fun createInitialBodyMap(): List<BodyMapMark> {
        return listOf(
            BodyMapMark(
                id = "bm-01",
                clientId = "client-01",
                isFront = true,
                xNormalized = 0.28f, // left forearm
                yNormalized = 0.45f,
                markType = "Skin Tear",
                severity = "Mild",
                description = "2cm superficial tear from bookshelf slip on 28/09. Clean, edges healing under silicone dressing.",
                dateLogged = "28/09/2026",
                recordedBy = "David Okonkwo"
            ),
            BodyMapMark(
                id = "bm-02",
                clientId = "client-04",
                isFront = false,
                xNormalized = 0.50f, // sacrum
                yNormalized = 0.54f,
                markType = "Pressure Sore",
                severity = "Severe",
                description = "Category 2 sacral pressure break, 1.5cm x 1cm. TVN referral active.",
                dateLogged = "01/10/2026",
                recordedBy = "Priya Patel (RN)"
            )
        )
    }

    fun createInitialVacancies(): List<Vacancy> {
        return listOf(
            Vacancy(
                id = "vac-01",
                title = "Senior Domiciliary Care Worker (Full-Time)",
                branchId = "branch-wst",
                roleType = UserRole.SENIOR_CARER,
                contractType = ContractType.FULL_TIME,
                hourlyRate = 14.50,
                description = "Lead morning care runs in Westminster, mentor new care workers, and administer medications.",
                isOpen = true,
                applicantsCount = 4
            ),
            Vacancy(
                id = "vac-02",
                title = "Supported Living Support Worker",
                branchId = "branch-cmd",
                roleType = UserRole.CARE_WORKER,
                contractType = ContractType.GUARANTEED_HOURS,
                hourlyRate = 13.00,
                description = "Support neurodiverse young adults with community participation and independent living skills in Camden.",
                isOpen = true,
                applicantsCount = 6
            ),
            Vacancy(
                id = "vac-03",
                title = "Clinical Lead Registered Nurse",
                branchId = "branch-wst",
                roleType = UserRole.NURSE,
                contractType = ContractType.FULL_TIME,
                hourlyRate = 22.50,
                description = "Complex nursing caseload, PEG feeding oversight, tracheostomy care, and medication audits.",
                isOpen = true,
                applicantsCount = 2
            )
        )
    }

    fun createInitialCandidates(): List<Candidate> {
        return listOf(
            Candidate(
                id = "cand-01",
                vacancyId = "vac-01",
                fullName = "Amara Nwosu",
                email = "amara.nwosu@ukcare-applicant.co.uk",
                phone = "07700 902114",
                appliedDate = "22/09/2026",
                stage = CandidateStage.OFFER_ACCEPTED,
                interviewScore = InterviewScore(5, 4, 5, 5, "Compassionate values, exemplary knowledge of dignity in care.", "Strong Yes"),
                dbsTracking = DbsTracking("E-BULK-992140", "Certificate Dispatched", "002991048291", true),
                rightToWork = RightToWorkCheck(true, "UK Biometric Residence Permit / Share Code Verified", "31/12/2028"),
                references = ReferenceChecks("St Thomas Home Care", "Verified", "Camden Council Care Team", "Verified"),
                onboarding = OnboardingChecklist(contractSigned = true, uniformIssued = true, shadowShiftsBooked = true, idBadgePrinted = true, mandatoryTrainingEnrolled = true),
                offerHourlyRate = 14.50,
                cvSummary = "4 years experience in South London domiciliary care. NVQ Level 3 in Health and Social Care."
            ),
            Candidate(
                id = "cand-02",
                vacancyId = "vac-02",
                fullName = "Jordan Taylor",
                email = "jordan.taylor@careconnect.co.uk",
                phone = "07700 902881",
                appliedDate = "26/09/2026",
                stage = CandidateStage.CHECKS_PENDING,
                interviewScore = InterviewScore(4, 4, 4, 4, "Strong communication with autism spectrum young adults.", "Proceed with Offer"),
                dbsTracking = DbsTracking("E-BULK-994821", "Barred List Clear - Dispatched", null, false),
                rightToWork = RightToWorkCheck(true, "British Citizen Passport Valid", "10/08/2033"),
                references = ReferenceChecks("Islington Community Action", "Verified", "University Tutor", "Received"),
                onboarding = OnboardingChecklist(contractSigned = true, uniformIssued = false, shadowShiftsBooked = false, idBadgePrinted = false, mandatoryTrainingEnrolled = true),
                offerHourlyRate = 13.00,
                cvSummary = "PBS (Positive Behaviour Support) trained. 2 years experience supporting neurodiverse individuals."
            ),
            Candidate(
                id = "cand-03",
                vacancyId = "vac-01",
                fullName = "Grace O'Malley",
                email = "grace.omalley@caremail.co.uk",
                phone = "07700 903445",
                appliedDate = "28/09/2026",
                stage = CandidateStage.INTERVIEWED,
                interviewScore = InterviewScore(4, 3, 4, 4, "Good interpersonal skills. Needs moving & handling practical refresher.", "Proceed with Offer"),
                dbsTracking = DbsTracking(),
                offerHourlyRate = 14.00,
                cvSummary = "Experienced carer returning to practice. Previous senior carer in Manchester."
            )
        )
    }

    fun createInitialPayRules(): PayRulesConfig {
        return PayRulesConfig(
            organisationId = "org-allcarex-01",
            baseHourlyRate = 13.50,
            weekendUpliftPerHour = 2.00,
            nightUpliftPerHour = 2.50,
            bankHolidayMultiplier = 1.5,
            travelTimeRatePerHour = 12.00,
            mileageRatePerMile = 0.45,
            sleepInFlatRate = 55.00,
            wakingNightHourlyRate = 16.50,
            trainingHourlyRate = 12.00,
            holidayAccrualPercentage = 12.07,
            nationalLivingWageRate = 11.44
        )
    }

    fun createInitialTimesheets(): List<TimesheetEntry> {
        return listOf(
            TimesheetEntry(
                staffId = "staff-04",
                staffName = "David Okonkwo",
                entryType = EntryType.VISIT,
                visitId = "visit-01",
                clientName = "Arthur Pendelton",
                date = "03/10/2026",
                scheduledHours = 0.75,
                actualDeliveredHours = 0.87, // 52 mins vs 45 mins
                differenceMinutes = 7,
                hourlyPayRate = 13.50,
                grossPay = 11.75,
                travelTimeMinutes = 15,
                travelPay = 3.00,
                mileageMiles = 3.2,
                mileagePay = 1.44, // @ £0.45/mile
                holidayPayAccrued = 1.42, // 12.07%
                isDisputed = false,
                isNlwCompliant = true
            ),
            TimesheetEntry(
                staffId = "staff-05",
                staffName = "Chloe Bennett",
                entryType = EntryType.VISIT,
                visitId = "visit-02",
                clientName = "Margaret 'Maggie' Brown",
                date = "03/10/2026",
                scheduledHours = 0.50,
                actualDeliveredHours = 0.50,
                differenceMinutes = 0,
                hourlyPayRate = 13.50,
                grossPay = 6.75,
                travelTimeMinutes = 18,
                travelPay = 3.60,
                mileageMiles = 4.0,
                mileagePay = 1.80,
                holidayPayAccrued = 0.81,
                isDisputed = false,
                isNlwCompliant = true
            ),
            TimesheetEntry(
                staffId = "staff-06",
                staffName = "Priya Patel (RN)",
                entryType = EntryType.VISIT,
                visitId = "visit-03",
                clientName = "Evelyn Rose Davies",
                date = "03/10/2026",
                scheduledHours = 1.00,
                actualDeliveredHours = 1.08,
                differenceMinutes = 5,
                hourlyPayRate = 22.00,
                grossPay = 23.76,
                travelTimeMinutes = 15,
                travelPay = 5.50,
                mileageMiles = 2.5,
                mileagePay = 1.13,
                holidayPayAccrued = 2.87,
                isDisputed = false,
                isNlwCompliant = true
            ),
            TimesheetEntry(
                staffId = "staff-04",
                staffName = "David Okonkwo",
                entryType = EntryType.TRAINING,
                visitId = null,
                clientName = "Oliver McGowan LD & Autism Workshop",
                date = "02/10/2026",
                scheduledHours = 3.00,
                actualDeliveredHours = 3.00,
                differenceMinutes = 0,
                hourlyPayRate = 12.00,
                grossPay = 36.00,
                travelTimeMinutes = 0,
                travelPay = 0.0,
                mileageMiles = 0.0,
                mileagePay = 0.0,
                holidayPayAccrued = 4.35,
                isDisputed = false,
                isNlwCompliant = true
            ),
            TimesheetEntry(
                staffId = "staff-11",
                staffName = "Robert Evans",
                entryType = EntryType.SLEEP_IN,
                visitId = null,
                clientName = "Chalcot Supported Living (Liam O'Connor)",
                date = "01/10/2026",
                scheduledHours = 8.00,
                actualDeliveredHours = 8.00,
                differenceMinutes = 0,
                hourlyPayRate = 6.88, // Flat sleep-in rate £55 / 8h
                grossPay = 55.00,
                travelTimeMinutes = 20,
                travelPay = 4.00,
                mileageMiles = 3.0,
                mileagePay = 1.35,
                holidayPayAccrued = 6.64,
                isDisputed = false,
                isNlwCompliant = true,
                complianceFlags = listOf("Sleep-in flat rate compliant with Mencap Supreme Court ruling")
            ),
            TimesheetEntry(
                staffId = "staff-07",
                staffName = "James Wilson",
                entryType = EntryType.VISIT,
                visitId = null,
                clientName = "George Henderson",
                date = "29/09/2026",
                scheduledHours = 1.00,
                actualDeliveredHours = 1.00,
                differenceMinutes = 0,
                hourlyPayRate = 11.20, // Below statutory NLW (£11.44)!
                grossPay = 11.20,
                travelTimeMinutes = 45, // Unpaid travel time
                travelPay = 0.0,
                mileageMiles = 6.0,
                mileagePay = 0.0,
                holidayPayAccrued = 1.35,
                isDisputed = true,
                disputeReason = "Unpaid 45-minute travel between Camden and Islington calls reduced effective rate below NLW.",
                isNlwCompliant = false,
                nlwShortfall = 0.24,
                shortfallReason = "Hourly pay £11.20 is below UK National Living Wage of £11.44/hr. Long unpaid travel time (45 mins) must be reimbursed.",
                complianceFlags = listOf("NLW BREACH WARNING: £0.24/hr shortfall")
            )
        )
    }

    fun createInitialHolidays(): List<HolidayRecord> {
        return listOf(
            HolidayRecord(
                staffId = "staff-04",
                staffName = "David Okonkwo",
                startDate = "14/10/2026",
                endDate = "16/10/2026",
                hoursRequested = 15.0,
                status = "Pending",
                hasRotaClash = true,
                notes = "Rota clash: 3 scheduled morning visits assigned to Arthur Pendelton. Cover carer required."
            ),
            HolidayRecord(
                staffId = "staff-05",
                staffName = "Chloe Bennett",
                startDate = "20/09/2026",
                endDate = "22/09/2026",
                hoursRequested = 16.0,
                status = "Approved",
                hasRotaClash = false,
                notes = "Cover provided by Fatimah Al-Sayed."
            )
        )
    }

    fun createInitialHolidayBalances(): List<StaffHolidayBalance> {
        return listOf(
            StaffHolidayBalance("staff-04", "David Okonkwo", 240.0, 28.97, 14.0, 14.97),
            StaffHolidayBalance("staff-05", "Chloe Bennett", 260.0, 31.38, 20.0, 11.38),
            StaffHolidayBalance("staff-06", "Priya Patel", 300.0, 36.21, 15.0, 21.21),
            StaffHolidayBalance("staff-11", "Robert Evans", 160.0, 19.31, 0.0, 19.31)
        )
    }

    fun createInitialSickness(): List<SicknessRecord> {
        return listOf(
            SicknessRecord(
                staffId = "staff-07",
                staffName = "James Wilson",
                startDate = "24/09/2026",
                endDate = "29/09/2026",
                qualifyingDays = 5,
                isSspEligible = true,
                sspNotes = "Qualifies for Statutory Sick Pay (5 days absence). Rate: £116.75/week pro-rata.",
                fitNoteRef = "MED3-77291-UK (Acute Viral Respiratory Infection)",
                returnToWorkCompleted = true,
                returnToWorkNotes = "Fit to resume duty without restrictions. Completed with Eleanor Vance."
            )
        )
    }

    fun createInitialPayRuns(): List<PayRun> {
        return listOf(
            PayRun(
                id = "pr-2026-w40",
                periodName = "October 2026 - Week 40 (Active)",
                startDate = "28/09/2026",
                endDate = "04/10/2026",
                totalStaffCount = 6,
                totalDeliveredHours = 14.45,
                grossPayTotal = 144.46,
                travelPayTotal = 16.10,
                mileagePayTotal = 5.72,
                holidayAccruedTotal = 17.44,
                totalPayrollCost = 166.28,
                status = PayRunStatus.MANAGER_REVIEW,
                exportedFormat = null,
                approvedBy = "Eleanor Vance (Registered Manager)",
                lockedAt = null,
                adjustments = listOf(
                    PayAdjustment(
                        staffId = "staff-07",
                        staffName = "James Wilson",
                        periodRef = "Week 39",
                        amount = 12.50,
                        reason = "NLW travel time back-pay reimbursement for 29/09 shift",
                        authorisedBy = "Hannah Croft (Finance)"
                    )
                )
            ),
            PayRun(
                id = "pr-2026-w39",
                periodName = "September 2026 - Week 39 (Closed)",
                startDate = "21/09/2026",
                endDate = "27/09/2026",
                totalStaffCount = 10,
                totalDeliveredHours = 345.00,
                grossPayTotal = 4657.50,
                travelPayTotal = 414.00,
                mileagePayTotal = 152.10,
                holidayAccruedTotal = 562.16,
                totalPayrollCost = 5223.60,
                status = PayRunStatus.LOCKED,
                exportedFormat = "Xero UK Payroll CSV",
                approvedBy = "Hannah Croft (Finance)",
                lockedAt = "28/09/2026 17:00",
                adjustments = emptyList()
            )
        )
    }

    fun createInitialFundingRules(): List<ClientFundingRule> {
        return listOf(
            ClientFundingRule(
                clientId = "client-01",
                primarySource = FundingType.LOCAL_AUTHORITY,
                councilHourlyRate = 24.00,
                nhsChcHourlyRate = 0.0,
                privateHourlyRate = 28.00,
                councilSplitPercent = 85.0,
                privateSplitPercent = 15.0,
                nhsSplitPercent = 0.0,
                purchaseOrderNumber = "PO-WST-2026-0914",
                roundingRule = "Exact Minutes",
                billCommissionedOrActual = "Actual Delivered"
            ),
            ClientFundingRule(
                clientId = "client-04",
                primarySource = FundingType.NHS_CHC,
                councilHourlyRate = 0.0,
                nhsChcHourlyRate = 40.00,
                privateHourlyRate = 0.0,
                councilSplitPercent = 0.0,
                privateSplitPercent = 0.0,
                nhsSplitPercent = 100.0,
                purchaseOrderNumber = "PO-NHS-CHC-7721",
                roundingRule = "Exact Minutes",
                billCommissionedOrActual = "Commissioned"
            ),
            ClientFundingRule(
                clientId = "client-02",
                primarySource = FundingType.PRIVATE_SELF_FUNDED,
                councilHourlyRate = 0.0,
                nhsChcHourlyRate = 0.0,
                privateHourlyRate = 30.00,
                councilSplitPercent = 0.0,
                privateSplitPercent = 100.0,
                nhsSplitPercent = 0.0,
                purchaseOrderNumber = "PRIV-DIR-002",
                roundingRule = "Round Up 15 Mins",
                billCommissionedOrActual = "Actual Delivered"
            )
        )
    }

    fun createInitialInvoices(): List<ClientInvoice> {
        return listOf(
            ClientInvoice(
                id = "inv-2026-001",
                invoiceNumber = "ACX-INV-1092",
                clientId = "client-01",
                clientName = "Arthur Pendelton",
                billingPeriod = "24/09/2026 - 30/09/2026",
                totalVisitsDelivered = 21,
                totalHoursDelivered = 15.75,
                grossTotal = 441.00, // @ £28/hr
                splits = InvoiceSplit(
                    localAuthorityRate = 24.00,
                    localAuthorityTotal = 378.00,
                    nhsChcRate = 0.0,
                    nhsChcTotal = 0.0,
                    privateClientRate = 4.00,
                    privateClientTotal = 63.00
                ),
                status = InvoiceStatus.APPROVED,
                generatedDate = "01/10/2026",
                dueDate = "15/10/2026",
                outstandingBalance = 63.00, // Council paid 378, private balance remaining
                agingBucket = "0-30 days",
                carerWageCostTotal = 212.63, // 15.75h @ £13.50
                payments = listOf(
                    InvoicePayment(
                        invoiceId = "inv-2026-001",
                        paymentDate = "02/10/2026",
                        amount = 378.00,
                        paymentMethod = "Local Authority Remittance (BACS)",
                        reference = "WST-REMIT-99120"
                    )
                ),
                lines = listOf(
                    InvoiceLineItem(
                        visitId = "visit-01",
                        date = "24/09/2026",
                        description = "Morning Personal Care & Medication Call",
                        scheduledMinutes = 45,
                        deliveredMinutes = 48,
                        billedMinutes = 45,
                        hourlyRate = 28.00,
                        totalAmount = 21.00,
                        laPortion = 18.00,
                        nhsPortion = 0.0,
                        privatePortion = 3.00
                    ),
                    InvoiceLineItem(
                        visitId = "visit-04",
                        date = "24/09/2026",
                        description = "Lunch Welfare Call & Meal Prep",
                        scheduledMinutes = 45,
                        deliveredMinutes = 45,
                        billedMinutes = 45,
                        hourlyRate = 28.00,
                        totalAmount = 21.00,
                        laPortion = 18.00,
                        nhsPortion = 0.0,
                        privatePortion = 3.00
                    )
                )
            ),
            ClientInvoice(
                id = "inv-2026-002",
                invoiceNumber = "ACX-INV-1093",
                clientId = "client-04",
                clientName = "Evelyn Rose Davies",
                billingPeriod = "24/09/2026 - 30/09/2026",
                totalVisitsDelivered = 28,
                totalHoursDelivered = 42.00,
                grossTotal = 1680.00, // Double-up nursing @ £40/hr
                splits = InvoiceSplit(
                    localAuthorityRate = 0.0,
                    localAuthorityTotal = 0.0,
                    nhsChcRate = 40.00,
                    nhsChcTotal = 1680.00,
                    privateClientRate = 0.0,
                    privateClientTotal = 0.0
                ),
                status = InvoiceStatus.SENT,
                generatedDate = "01/10/2026",
                dueDate = "28/10/2026",
                outstandingBalance = 1680.00,
                agingBucket = "0-30 days",
                carerWageCostTotal = 882.00,
                lines = listOf(
                    InvoiceLineItem(
                        visitId = "visit-03",
                        date = "24/09/2026",
                        description = "Complex Double-Up Nursing Care & Hoist Repositioning",
                        scheduledMinutes = 60,
                        deliveredMinutes = 65,
                        billedMinutes = 60,
                        hourlyRate = 40.00,
                        totalAmount = 40.00,
                        laPortion = 0.0,
                        nhsPortion = 40.0,
                        privatePortion = 0.0
                    )
                )
            ),
            ClientInvoice(
                id = "inv-2026-003",
                invoiceNumber = "ACX-INV-1094",
                clientId = "client-02",
                clientName = "Margaret 'Maggie' Brown",
                billingPeriod = "01/09/2026 - 07/09/2026",
                totalVisitsDelivered = 14,
                totalHoursDelivered = 10.50,
                grossTotal = 315.00, // @ £30/hr private
                splits = InvoiceSplit(
                    localAuthorityRate = 0.0,
                    localAuthorityTotal = 0.0,
                    nhsChcRate = 0.0,
                    nhsChcTotal = 0.0,
                    privateClientRate = 30.00,
                    privateClientTotal = 315.00
                ),
                status = InvoiceStatus.OVERDUE,
                generatedDate = "08/09/2026",
                dueDate = "22/09/2026",
                outstandingBalance = 315.00,
                agingBucket = "61+ days",
                carerWageCostTotal = 141.75
            )
        )
    }

    fun createInitialPolicies(): List<PolicyDocument> {
        return listOf(
            PolicyDocument(
                id = "pol-01",
                title = "Safe Handling & Administration of Medication (eMAR)",
                category = "Clinical Governance",
                version = "v4.2",
                lastReviewedDate = "15/08/2026",
                nextReviewDate = "15/08/2027",
                approvedBy = "Eleanor Vance (Registered Manager)",
                staffAcknowledgedCount = 11,
                totalStaffCount = 12,
                summaryContent = "Defines 6 rights of medication administration, Controlled Drug Schedule 2/3 double signature protocol, and zero-blame error reporting."
            ),
            PolicyDocument(
                id = "pol-02",
                title = "Safeguarding Adults at Risk & Duty of Candour",
                category = "Safeguarding",
                version = "v3.1",
                lastReviewedDate = "01/09/2026",
                nextReviewDate = "01/09/2027",
                approvedBy = "Eleanor Vance",
                staffAcknowledgedCount = 12,
                totalStaffCount = 12,
                summaryContent = "Mandatory reporting timelines for Section 42 enquiries, CQC Regulation 18 notifications, and statutory openness with relatives."
            ),
            PolicyDocument(
                id = "pol-03",
                title = "Lone Worker Safety & Real-Time Monitoring",
                category = "Health & Safety",
                version = "v2.0",
                lastReviewedDate = "10/05/2026",
                nextReviewDate = "10/05/2027",
                approvedBy = "Marcus Thorne (Coordinator)",
                staffAcknowledgedCount = 10,
                totalStaffCount = 12,
                summaryContent = "GPS verified check-in/out requirements, SOS alarm escalation procedure, and emergency on-call response within 10 minutes."
            )
        )
    }

    fun createInitialAuditTemplates(): List<AuditTemplate> {
        return listOf(
            AuditTemplate(
                id = "aud-01",
                title = "Monthly eMAR & Medication Records Audit",
                frequency = "Monthly",
                lastConductedDate = "25/09/2026",
                auditorName = "Sarah Jenkins (Senior Carer)",
                overallRagStatus = "GREEN",
                actionItems = listOf(
                    AuditActionItem(
                        auditTemplateId = "aud-01",
                        title = "Atorvastatin Low Stock Reorder",
                        issueIdentified = "Stock fell below 7-day threshold without automated repeat slip.",
                        smartActionRequired = "Ensure repeat prescription requested at 10-day buffer.",
                        ownerName = "David Okonkwo",
                        targetDate = "05/10/2026",
                        status = "In Progress"
                    )
                )
            ),
            AuditTemplate(
                id = "aud-02",
                title = "Infection Prevention & Control (IPC) Spot Check",
                frequency = "Bi-Monthly",
                lastConductedDate = "18/09/2026",
                auditorName = "Priya Patel (RN)",
                overallRagStatus = "GREEN",
                actionItems = emptyList()
            ),
            AuditTemplate(
                id = "aud-03",
                title = "Care Plan & Risk Assessment Quarterly Review",
                frequency = "Quarterly",
                lastConductedDate = "15/09/2026",
                auditorName = "Eleanor Vance",
                overallRagStatus = "AMBER",
                actionItems = listOf(
                    AuditActionItem(
                        auditTemplateId = "aud-03",
                        title = "Update Arthur Pendelton Falls Assessment",
                        issueIdentified = "Recent slip on 28/09 requires formal revision in Waterlow & Falls protocol.",
                        smartActionRequired = "Complete revised falls risk score and verify sensor mat placement.",
                        ownerName = "Marcus Thorne",
                        targetDate = "04/10/2026",
                        status = "Open"
                    )
                )
            )
        )
    }

    fun createInitialComplaints(): List<ComplaintCompliment> {
        return listOf(
            ComplaintCompliment(
                id = "cc-01",
                isCompliment = true,
                clientName = "Arthur Pendelton (Daughter Clara)",
                dateLogged = "29/09/2026",
                category = "Dignity & Care",
                description = "Clara expressed heartfelt gratitude for David's patient morning assistance following Arthur's slight disorientation.",
                dutyOfCandourApplies = false,
                dutyOfCandourSatisfied = true,
                resolutionDate = "29/09/2026",
                lessonsLearned = "Shared in monthly team commendations."
            ),
            ComplaintCompliment(
                id = "cc-02",
                isCompliment = false,
                clientName = "Margaret 'Maggie' Brown",
                dateLogged = "20/09/2026",
                category = "Punctuality",
                description = "Morning visit arrived 25 minutes after scheduled Sinemet Plus window due to heavy central London traffic.",
                dutyOfCandourApplies = true,
                dutyOfCandourSatisfied = true,
                resolutionDate = "22/09/2026",
                lessonsLearned = "Route buffer increased by 15 mins for all morning Parkinson's medication rounds."
            )
        )
    }

    fun createInitialCqcThemes(): List<CqcAssessmentTheme> {
        return listOf(
            CqcAssessmentTheme(
                id = "cqc-safe-01",
                keyQuestion = "Safe",
                qualityStatementTitle = "Learning culture",
                statementDescription = "We have a proactive and positive culture of safety based on openness and honesty, in which concerns about safety are listened to.",
                currentScore = 3, // Good
                evidenceNotes = "All clinical incidents and medication discrepancies logged with immediate actions, root cause analysis, and Duty of Candour letters.",
                lastUpdated = "01/10/2026"
            ),
            CqcAssessmentTheme(
                id = "cqc-safe-02",
                keyQuestion = "Safe",
                qualityStatementTitle = "Safe systems, pathways and transitions",
                statementDescription = "We work with people and our partners to establish and maintain safe systems of care.",
                currentScore = 4, // Outstanding
                evidenceNotes = "Real-time electronic eMAR with Schedule 2 Controlled Drug double signatures and GPS verified visit call monitoring.",
                lastUpdated = "28/09/2026"
            ),
            CqcAssessmentTheme(
                id = "cqc-eff-01",
                keyQuestion = "Effective",
                qualityStatementTitle = "Assessing needs",
                statementDescription = "We maximise the effectiveness of people’s care and treatment by assessing and reviewing their health, care, wellbeing and communication needs with them.",
                currentScore = 3, // Good
                evidenceNotes = "Standardised NEWS2, Waterlow pressure ulcer scores, and person-centred care plans versioned with named senior approval.",
                lastUpdated = "02/10/2026"
            ),
            CqcAssessmentTheme(
                id = "cqc-caring-01",
                keyQuestion = "Caring",
                qualityStatementTitle = "Treating people through kindness, empathy and compassion",
                statementDescription = "We treat people with kindness, empathy and compassion and we respect their privacy and dignity.",
                currentScore = 4, // Outstanding
                evidenceNotes = "Family portal with transparent daily care logs, nutrition tracking, and direct coordinator messaging.",
                lastUpdated = "30/09/2026"
            ),
            CqcAssessmentTheme(
                id = "cqc-well-01",
                keyQuestion = "Well-Led",
                qualityStatementTitle = "Governance, management and sustainability",
                statementDescription = "We have clear responsibilities, roles, systems of accountability and good governance.",
                currentScore = 3, // Good
                evidenceNotes = "Immutable audit log on all client records, multi-tenant branch segregation, and automated 48-hour WTD and DBS expiry rostering blocks.",
                lastUpdated = "03/10/2026"
            )
        )
    }

    // ==================== DYNAMIC REGULATOR FRAMEWORKS & EVIDENCE ====================

    fun createInitialFrameworks(): List<AssessmentFramework> {
        val safeStatements = listOf(
            QualityStatementOrKloe(
                id = "stmt-safe-01",
                keyQuestionId = "SAFE",
                code = "SAFE-01",
                title = "Learning culture",
                description = "Proactive safety culture based on openness, honesty, learning from safety events, near-misses and whistleblowing.",
                currentSelfAssessmentScore = 3,
                selfAssessmentNarrative = "Zero-blame reporting is embedded. All medication near-misses and falls trigger clinical investigation within 24h with statutory Duty of Candour letters.",
                linkedEvidenceIds = listOf("ev-01", "ev-02")
            ),
            QualityStatementOrKloe(
                id = "stmt-safe-02",
                keyQuestionId = "SAFE",
                code = "SAFE-02",
                title = "Safeguarding",
                description = "Understanding what constitutes abuse, zero tolerance, timely Section 42 local authority referrals and trauma-informed support.",
                currentSelfAssessmentScore = 4,
                selfAssessmentNarrative = "100% of staff completed Care Certificate Standard 10 & Level 3 Safeguarding. Multi-agency liaison with Westminster & Camden Safeguarding Boards.",
                linkedEvidenceIds = listOf("ev-03")
            ),
            QualityStatementOrKloe(
                id = "stmt-safe-03",
                keyQuestionId = "SAFE",
                code = "SAFE-03",
                title = "Safe environments & lone working",
                description = "Physical premises risk assessments, lone worker monitoring, real-time GPS safety check-ins and emergency SOS response.",
                currentSelfAssessmentScore = 4,
                selfAssessmentNarrative = "Carer Mobile app includes lone worker status with 10-minute escalation and geofenced check-in/out ±4m accuracy.",
                linkedEvidenceIds = listOf("ev-04")
            ),
            QualityStatementOrKloe(
                id = "stmt-safe-04",
                keyQuestionId = "SAFE",
                code = "SAFE-04",
                title = "Medicines optimisation (eMAR)",
                description = "Safe prescribing, administration, storage, disposal, controlled drugs management and audit trails.",
                currentSelfAssessmentScore = 3,
                selfAssessmentNarrative = "Digital eMAR with Schedule 2 Controlled Drug dual verification. Monthly internal audit score 92.0%. 7-day low-stock automatic reorder buffer.",
                linkedEvidenceIds = listOf("ev-05")
            )
        )

        val effectiveStatements = listOf(
            QualityStatementOrKloe(
                id = "stmt-eff-01",
                keyQuestionId = "EFFECTIVE",
                code = "EFF-01",
                title = "Assessing needs & clinical tools",
                description = "Holistic assessments using recognised evidence-based clinical tools (NEWS2, MUST, Waterlow, Abbey Pain, Falls risk).",
                currentSelfAssessmentScore = 3,
                selfAssessmentNarrative = "All 8 active clients have version-controlled assessments. 100% of high-risk clients have bi-weekly Waterlow and MUST clinical reviews.",
                linkedEvidenceIds = listOf("ev-06")
            ),
            QualityStatementOrKloe(
                id = "stmt-eff-02",
                keyQuestionId = "EFFECTIVE",
                code = "EFF-02",
                title = "Delivering evidence-based care & treatment",
                description = "Care adheres to NICE guidelines, current national clinical standards and person-centred care planning.",
                currentSelfAssessmentScore = 4,
                selfAssessmentNarrative = "AI draft care plan feature strictly requires named senior approval and never overwrites approved clinical plans.",
                linkedEvidenceIds = listOf("ev-07")
            )
        )

        val caringStatements = listOf(
            QualityStatementOrKloe(
                id = "stmt-car-01",
                keyQuestionId = "CARING",
                code = "CAR-01",
                title = "Kindness, compassion and dignity",
                description = "People are treated with genuine respect, warmth, empathy and their dignity and privacy are upheld at all times.",
                currentSelfAssessmentScore = 4,
                selfAssessmentNarrative = "Client satisfaction survey achieved 94.2% rating. Zero complaints regarding dignity or attitude over 12 months.",
                linkedEvidenceIds = listOf("ev-08")
            ),
            QualityStatementOrKloe(
                id = "stmt-car-02",
                keyQuestionId = "CARING",
                code = "CAR-02",
                title = "Treating people as individuals & independence",
                description = "Personal history, cultural preferences, gender identity, religious needs and choices are respected.",
                currentSelfAssessmentScore = 3,
                selfAssessmentNarrative = "Care plans document life history, preferred names, routines, dietary preferences, and hobbies.",
                linkedEvidenceIds = listOf("ev-09")
            )
        )

        val responsiveStatements = listOf(
            QualityStatementOrKloe(
                id = "stmt-resp-01",
                keyQuestionId = "RESPONSIVE",
                code = "RESP-01",
                title = "Person-centred care & continuity",
                description = "Continuity of care with regular carers, responsive scheduling, and prompt adjustment when needs change.",
                currentSelfAssessmentScore = 3,
                selfAssessmentNarrative = "Care rota algorithm prioritises regular carers (88% carer continuity achieved). Running late notifications inform clients within 5 mins.",
                linkedEvidenceIds = listOf("ev-10")
            ),
            QualityStatementOrKloe(
                id = "stmt-resp-02",
                keyQuestionId = "RESPONSIVE",
                code = "RESP-02",
                title = "Listening to and involving people",
                description = "Proactive collection of compliments, concerns, complaints with transparent feedback and 'You said, we did' actions.",
                currentSelfAssessmentScore = 4,
                selfAssessmentNarrative = "Active 'You Said, We Did' board in place. Family Portal provides real-time hydration and visit updates.",
                linkedEvidenceIds = listOf("ev-11")
            )
        )

        val wellLedStatements = listOf(
            QualityStatementOrKloe(
                id = "stmt-well-01",
                keyQuestionId = "WELL_LED",
                code = "WELL-01",
                title = "Shared direction, culture & governance",
                description = "Clear leadership, registered manager oversight, monthly clinical governance committees, risk register and audit rigor.",
                currentSelfAssessmentScore = 4,
                selfAssessmentNarrative = "Monthly Clinical Governance Committee with published minutes. Dynamic Risk Register scored (Likelihood x Impact). Immutable audit logs.",
                linkedEvidenceIds = listOf("ev-12")
            ),
            QualityStatementOrKloe(
                id = "stmt-well-02",
                keyQuestionId = "WELL_LED",
                code = "WELL-02",
                title = "Capable, compassionate leaders & workforce",
                description = "Fit and proper persons, safe recruitment, DBS Update Service, 100% supervision schedule, training matrix compliance.",
                currentSelfAssessmentScore = 3,
                selfAssessmentNarrative = "Rostering engine blocks non-compliant staff (e.g. expired DBS or mandatory training <75%). 100% Right-to-Work verified.",
                linkedEvidenceIds = listOf("ev-13")
            )
        )

        val cqcKeyQuestions = listOf(
            KeyQuestion("SAFE", "Safe", "Shield", "People are protected from abuse, avoidable harm and neglect.", 1, safeStatements),
            KeyQuestion("EFFECTIVE", "Effective", "CheckCircle", "Care and treatment achieves good outcomes and promotes quality of life.", 2, effectiveStatements),
            KeyQuestion("CARING", "Caring", "Favorite", "Staff treat people with kindness, empathy, respect and dignity.", 3, caringStatements),
            KeyQuestion("RESPONSIVE", "Responsive", "AccessTime", "Services are organised so they meet people's changing individual needs.", 4, responsiveStatements),
            KeyQuestion("WELL_LED", "Well-led", "Apartment", "Leadership, management and governance assure high quality person-centred care.", 5, wellLedStatements)
        )

        return listOf(
            AssessmentFramework(
                id = "fw-cqc-saf-2024",
                name = "CQC Single Assessment Framework (Current)",
                regulatorName = "Care Quality Commission (CQC)",
                version = "v2.4",
                effectiveDate = "01/01/2024",
                isActive = true,
                isArchived = false,
                keyQuestions = cqcKeyQuestions
            ),
            AssessmentFramework(
                id = "fw-cqc-kloe-2026",
                name = "CQC KLOE Evolution Framework (2026+ Transition)",
                regulatorName = "Care Quality Commission (CQC)",
                version = "v3.0 Draft",
                effectiveDate = "01/01/2027",
                isActive = false,
                isArchived = false,
                keyQuestions = cqcKeyQuestions.map { kq ->
                    kq.copy(
                        name = "${kq.name} (KLOE Focus)",
                        statements = kq.statements.map { it.copy(title = "KLOE: ${it.title}") }
                    )
                }
            ),
            AssessmentFramework(
                id = "fw-ciw-wales",
                name = "Care Inspectorate Wales (CIW) Framework",
                regulatorName = "Care Inspectorate Wales (CIW)",
                version = "v1.2",
                effectiveDate = "01/04/2025",
                isActive = false,
                isArchived = false,
                keyQuestions = listOf(
                    KeyQuestion("WELL_BEING", "Well-being", "Favorite", "People feel safe and protected, and achieve their personal outcomes.", 1, safeStatements),
                    KeyQuestion("CARE_SUPPORT", "Care & Support", "CheckCircle", "People receive the right care at the right time.", 2, effectiveStatements),
                    KeyQuestion("LEADERSHIP_MGMT", "Leadership & Management", "Apartment", "The service is well run with robust governance.", 3, wellLedStatements)
                )
            ),
            AssessmentFramework(
                id = "fw-empty-template",
                name = "Blank Regulator Framework Template",
                regulatorName = "Custom / Other Regulator",
                version = "v1.0 Template",
                effectiveDate = "01/10/2026",
                isActive = false,
                isArchived = false,
                keyQuestions = listOf(
                    KeyQuestion("CUSTOM-1", "Custom Domain 1", "Shield", "Define key regulatory questions and criteria.", 1, emptyList())
                )
            )
        )
    }

    fun createInitialEvidenceItems(): List<EvidenceItem> {
        return listOf(
            EvidenceItem(
                id = "ev-01",
                statementId = "stmt-safe-01",
                title = "Duty of Candour & Incident Log Audit",
                sourceType = EvidenceSourceType.AUTO_PLATFORM,
                description = "100% of notifiable safety events logged with root cause analysis and letters sent to families within 48 hours.",
                dateRecorded = "01/10/2026",
                freshnessRating = "Fresh",
                metricValue = "100% Duty of Candour compliance"
            ),
            EvidenceItem(
                id = "ev-02",
                statementId = "stmt-safe-01",
                title = "Staff Medication Competency Assessments",
                sourceType = EvidenceSourceType.AUDIT_RESULT,
                description = "All 6 active carers passed annual observational moving & handling and medication competency checks.",
                dateRecorded = "28/09/2026",
                freshnessRating = "Fresh",
                metricValue = "12/12 Carers Assessed"
            ),
            EvidenceItem(
                id = "ev-03",
                statementId = "stmt-safe-02",
                title = "Enhanced DBS & Barred List Verification",
                sourceType = EvidenceSourceType.AUTO_PLATFORM,
                description = "Automated compliance check flags DBS renewals 60 days ahead. James Wilson flagged and blocked from rostering.",
                dateRecorded = "02/10/2026",
                freshnessRating = "Fresh",
                metricValue = "100% Active Carers Verified"
            ),
            EvidenceItem(
                id = "ev-04",
                statementId = "stmt-safe-03",
                title = "Lone Worker SOS Escalation Log",
                sourceType = EvidenceSourceType.AUTO_PLATFORM,
                description = "Zero unanswered panic alarms. Real-time GPS tracking ±4m precision with offline sync buffering.",
                dateRecorded = "03/10/2026",
                freshnessRating = "Fresh",
                metricValue = "0 Unresolved Alarms"
            ),
            EvidenceItem(
                id = "ev-05",
                statementId = "stmt-safe-04",
                title = "Monthly eMAR Medication Audit",
                sourceType = EvidenceSourceType.AUDIT_RESULT,
                description = "Overall audit score 92.0%. Action item logged for Atorvastatin repeat prescription buffer.",
                dateRecorded = "25/09/2026",
                freshnessRating = "Fresh",
                metricValue = "92.0% Score (Green RAG)"
            ),
            EvidenceItem(
                id = "ev-06",
                statementId = "stmt-eff-01",
                title = "Clinical Tool Tracking (NEWS2, MUST, Waterlow)",
                sourceType = EvidenceSourceType.AUTO_PLATFORM,
                description = "Digital calculations with instant clinical escalation prompts for scores >= 3.",
                dateRecorded = "02/10/2026",
                freshnessRating = "Fresh",
                metricValue = "100% In-Date Assessments"
            ),
            EvidenceItem(
                id = "ev-07",
                statementId = "stmt-eff-02",
                title = "Person-Centred Care Plan Review Rates",
                sourceType = EvidenceSourceType.AUTO_PLATFORM,
                description = "96.4% of care plans reviewed within statutory 30-day window with named registered manager sign-off.",
                dateRecorded = "30/09/2026",
                freshnessRating = "Fresh",
                metricValue = "96.4% Review Rate"
            ),
            EvidenceItem(
                id = "ev-08",
                statementId = "stmt-car-01",
                title = "Annual Client & Relative Satisfaction Survey",
                sourceType = EvidenceSourceType.SURVEY_FEEDBACK,
                description = "45 responses received. 94.2% overall satisfaction. High praise for staff empathy and punctuality.",
                dateRecorded = "15/09/2026",
                freshnessRating = "Fresh",
                metricValue = "94.2% Satisfaction"
            ),
            EvidenceItem(
                id = "ev-09",
                statementId = "stmt-car-02",
                title = "Cultural Preference & Diet Plans",
                sourceType = EvidenceSourceType.MANUAL_UPLOAD,
                description = "Halal, Kosher, and pureed dietary preferences logged in all resident dietary passports.",
                dateRecorded = "20/09/2026",
                freshnessRating = "Fresh",
                metricValue = "100% Passports Complete"
            ),
            EvidenceItem(
                id = "ev-10",
                statementId = "stmt-resp-01",
                title = "Care Continuity Tracking",
                sourceType = EvidenceSourceType.AUTO_PLATFORM,
                description = "Continuity of care algorithm achieves 88.5% regular carer allocation across all domiciliary visits.",
                dateRecorded = "02/10/2026",
                freshnessRating = "Fresh",
                metricValue = "88.5% Carer Continuity"
            ),
            EvidenceItem(
                id = "ev-11",
                statementId = "stmt-resp-02",
                title = "You Said, We Did Action Tracker",
                sourceType = EvidenceSourceType.MANUAL_UPLOAD,
                description = "Implemented tea-call timing adjustments and wider tea variety following family council meeting.",
                dateRecorded = "24/09/2026",
                freshnessRating = "Fresh",
                metricValue = "4 Actions Completed"
            ),
            EvidenceItem(
                id = "ev-12",
                statementId = "stmt-well-01",
                title = "Clinical Governance Committee Minutes",
                sourceType = EvidenceSourceType.MANUAL_UPLOAD,
                description = "Quarterly committee review covering clinical audit trends, falls root causes, and safeguarding.",
                dateRecorded = "28/09/2026",
                freshnessRating = "Fresh",
                metricValue = "Quorum Met / 6 Actions"
            ),
            EvidenceItem(
                id = "ev-13",
                statementId = "stmt-well-02",
                title = "Staff Training Matrix & Care Certificate",
                sourceType = EvidenceSourceType.AUTO_PLATFORM,
                description = "15 UK mandatory training modules tracked. Automated warning flags modules expiring in 30 days.",
                dateRecorded = "01/10/2026",
                freshnessRating = "Fresh",
                metricValue = "91.8% Workforce Compliance"
            ),
            // Evidence Gap Example
            EvidenceItem(
                id = "ev-gap-01",
                statementId = "stmt-safe-03",
                title = "GAP: Fire Marshal Refresher for Night Staff",
                sourceType = EvidenceSourceType.AUTO_PLATFORM,
                description = "2 waking-night staff members require updated practical fire extinguisher evacuation drill.",
                dateRecorded = "01/10/2026",
                freshnessRating = "Needs Review",
                metricValue = "2 Staff Overdue",
                isGap = true,
                gapRemediationPlan = "Booked on London Fire Safety Training course on 14/10/2026."
            )
        )
    }

    fun createInitialSurveys(): List<SatisfactionSurveyResult> {
        return listOf(
            SatisfactionSurveyResult(
                respondentGroup = "Clients",
                title = "Annual Client Wellbeing & Dignity Survey 2026",
                surveyPeriod = "August - September 2026",
                respondentsCount = 28,
                overallSatisfactionPercent = 94.2,
                keyFindings = listOf(
                    "98% feel safe and respected by their regular carers",
                    "92% report carers arrive within the promised visit window",
                    "86% say carers know their individual personal preferences"
                ),
                actionsTriggered = listOf(
                    "Expand weekend activity choices in residential care",
                    "Provide larger-font daily care plans for visually impaired clients"
                )
            ),
            SatisfactionSurveyResult(
                respondentGroup = "Family Members",
                title = "Family & Next-of-Kin Portal Feedback",
                surveyPeriod = "Q3 2026",
                respondentsCount = 35,
                overallSatisfactionPercent = 92.0,
                keyFindings = listOf(
                    "96% love the real-time daily visit notes and fluid intake tracking",
                    "88% feel care coordinators communicate promptly when routines change"
                ),
                actionsTriggered = listOf(
                    "Added photo upload capability for community social outings",
                    "Established monthly virtual coffee morning with Registered Manager"
                )
            ),
            SatisfactionSurveyResult(
                respondentGroup = "Staff",
                title = "Workforce Wellbeing & Supervision Pulse",
                surveyPeriod = "September 2026",
                respondentsCount = 18,
                overallSatisfactionPercent = 89.5,
                keyFindings = listOf(
                    "94% feel supported by senior carers and out-of-hours on-call",
                    "90% appreciate paid travel time and HMRC mileage reimbursements",
                    "82% feel lone worker SOS gives confidence in the community"
                ),
                actionsTriggered = listOf(
                    "Increased travel buffer on busy Central London bus routes",
                    "Supplied winter weatherproof coats and rechargeable torches"
                )
            )
        )
    }

    fun createInitialRegulatorNotifications(): List<RegulatorNotification> {
        return listOf(
            RegulatorNotification(
                notificationType = "CQC Statutory Notification (Notice of Serious Injury / Reg 18)",
                clientOrStaffName = "Arthur Pendelton",
                submissionDate = "29/09/2026",
                submissionReference = "CQC-NOTIF-2026-09821",
                statutoryDeadline = "Within 24 hours of incident",
                status = "Acknowledged by CQC",
                summaryDetails = "Notification of minor slip and hospital check. CT head scan clear, discharged back to home care with sensor mat in place."
            ),
            RegulatorNotification(
                notificationType = "Local Authority Safeguarding Section 42 Enquiry Referral",
                clientOrStaffName = "George Henderson",
                submissionDate = "27/09/2026",
                submissionReference = "WST-SAFEGUARD-44019",
                statutoryDeadline = "Same day notification",
                status = "Under Review by Local Authority",
                summaryDetails = "Allegation of financial pressure by distant acquaintance. Bank card secured, adult social care lead assigned."
            ),
            RegulatorNotification(
                notificationType = "CQC Notification: Application for Deprivation of Liberty (DoLS)",
                clientOrStaffName = "Evelyn Rose Davies",
                submissionDate = "15/09/2026",
                submissionReference = "DOLS-CQC-2026-7714",
                statutoryDeadline = "Statutory annual renewal",
                status = "Approved / Closed",
                summaryDetails = "Standard DoLS authorization granted by supervisory body for dementia wandering protection."
            )
        )
    }

    fun createInitialMeetings(): List<GovernanceMeeting> {
        return listOf(
            GovernanceMeeting(
                meetingType = "Clinical Governance & Quality Committee",
                date = "28/09/2026",
                chairName = "Eleanor Vance (Registered Manager)",
                attendees = listOf("Eleanor Vance (RM)", "Priya Patel (Clinical Lead RN)", "Marcus Thorne (Coordinator)", "Sarah Jenkins (Senior Carer)"),
                minutesSummary = "Reviewed Q3 clinical audits. eMAR adherence 94.2%. Zero grade 3/4 pressure ulcers. Falls risk reassessments completed.",
                keyDecisions = listOf(
                    "Mandate dual signatures on all Schedule 2 liquid narcotics",
                    "Introduce bi-weekly Waterlow skin integrity checks for high-risk clients",
                    "Purchase 2 additional high-spec pressure-relieving air mattresses"
                )
            ),
            GovernanceMeeting(
                meetingType = "Safeguarding & Whistleblowing Review",
                date = "15/09/2026",
                chairName = "Eleanor Vance",
                attendees = listOf("Eleanor Vance", "Hannah Croft (Finance)", "Marcus Thorne"),
                minutesSummary = "Quarterly review of Duty of Candour records, Section 42 referrals and whistleblower hotline test.",
                keyDecisions = listOf(
                    "Hotline test confirmed functional with immediate SMS to Registered Manager",
                    "Refresher training for 3 newly appointed carers on financial abuse signs"
                )
            )
        )
    }

    fun createInitialRiskRegister(): List<RiskRegisterItem> {
        return listOf(
            RiskRegisterItem(
                riskCategory = "Staffing & Recruitment",
                description = "Carer shortage or sudden winter illness surge affecting visit punctuality.",
                likelihood = 3,
                impact = 4,
                mitigations = "On-call senior carer standby rota, pre-approved bank agency staff, active pipeline with 3 vetted candidates.",
                riskOwner = "Marcus Thorne",
                reviewDate = "15/10/2026"
            ),
            RiskRegisterItem(
                riskCategory = "Clinical Care",
                description = "Medication administration error with high-risk anticoagulant or insulin.",
                likelihood = 2,
                impact = 5,
                mitigations = "Mandatory double sign-off on eMAR, automated missed-dose alert at 45 mins, 6-monthly competency checks.",
                riskOwner = "Priya Patel (RN)",
                reviewDate = "01/11/2026"
            ),
            RiskRegisterItem(
                riskCategory = "Information Governance & GDPR",
                description = "Data breach or loss of mobile device containing care plans in transit.",
                likelihood = 2,
                impact = 4,
                mitigations = "PIN security timeout lock, MDM remote wipe enabled on all phones, zero local storage of unencrypted PII.",
                riskOwner = "Eleanor Vance",
                reviewDate = "01/12/2026"
            ),
            RiskRegisterItem(
                riskCategory = "Health & Safety / Environmental",
                description = "Severe winter weather / snow causing inability to access rural clients.",
                likelihood = 2,
                impact = 4,
                mitigations = "4x4 vehicle availability contract, prioritization matrix (Tier 1 vital calls flagged in Rota).",
                riskOwner = "Marcus Thorne",
                reviewDate = "01/11/2026"
            )
        )
    }

    fun createInitialBcp(): BusinessContinuityPlan {
        return BusinessContinuityPlan(
            id = "bcp-01",
            title = "All Care X Business Continuity & Disaster Recovery Plan",
            version = "v3.2",
            lastTestedDate = "15/09/2026",
            keyRisksCovered = listOf(
                "Severe Weather / Transport Strike",
                "IT Cloud System Outage / Cyber Incident",
                "Epidemic / Surge Staff Absence",
                "Loss of Branch Office Utilities / Telephony"
            ),
            emergencyContactTree = listOf(
                EmergencyContact("Gold Commander (Registered Manager)", "Eleanor Vance", "07700 900111", "020 7946 0111", 1),
                EmergencyContact("Silver Operational Lead (Coordination)", "Marcus Thorne", "07700 900222", "020 7946 0222", 2),
                EmergencyContact("Clinical Escalation Lead (Registered Nurse)", "Priya Patel", "07700 900333", "020 7946 0333", 2),
                EmergencyContact("Finance & Emergency Supplier Authority", "Hannah Croft", "07700 900444", "020 7946 0444", 3)
            )
        )
    }

    fun createInitialYouSaidWeDid(): List<YouSaidWeDidItem> {
        return listOf(
            YouSaidWeDidItem(
                feedbackSource = "Family Forum (August 2026)",
                youSaid = "Relatives requested earlier evening meal visits for Arthur and Maggie so they can watch the evening news relaxed.",
                weDid = "Adjusted rota scheduling windows from 18:30 to 17:30 with full client agreement and carer continuity.",
                dateImplemented = "01/09/2026"
            ),
            YouSaidWeDidItem(
                feedbackSource = "Carer Voice Council (July 2026)",
                youSaid = "Carers reported travel times between Camden and Islington calls were tight during evening rush hour.",
                weDid = "Added mandatory 15-minute travel buffers in rota engine and increased paid travel allowance.",
                dateImplemented = "15/08/2026"
            ),
            YouSaidWeDidItem(
                feedbackSource = "Client Feedback Survey (June 2026)",
                youSaid = "Clients asked for consistent carers who know their specific tea preferences (e.g. Earl Grey with oat milk).",
                weDid = "Introduced 'My Perfect Cup of Tea & Snacks' section prominently displayed on carer mobile visit header.",
                dateImplemented = "01/07/2026"
            )
        )
    }

    fun createInitialInspectionDaySession(): InspectionDaySession {
        return InspectionDaySession(
            id = "ins-session-cqc-01",
            auditorName = "Lead Inspector Sarah Crawford",
            regulatorName = "Care Quality Commission (CQC Adult Social Care)",
            accessExpiresAt = "03/10/2026 18:00 (Today)",
            isSessionActive = true,
            accessLog = listOf(
                AuditorAccessLogEntry("03/10/2026 09:15", "Sarah Crawford", "Policy Library", "Safe Handling of Medication v4.2"),
                AuditorAccessLogEntry("03/10/2026 09:40", "Sarah Crawford", "Clinical Audits", "Monthly eMAR Medication Audit 25/09/2026"),
                AuditorAccessLogEntry("03/10/2026 10:10", "Sarah Crawford", "Staff Compliance", "DBS & Training Matrix (12 Staff)"),
                AuditorAccessLogEntry("03/10/2026 10:45", "Sarah Crawford", "Duty of Candour", "Incident Report INC-2026-081")
            )
        )
    }

    fun createInitialStaffDocuments(): List<StaffDocument> {
        return listOf(
            StaffDocument(
                title = "Contract of Employment (Permanent F/T)",
                type = "Contract",
                issueDate = "15/01/2025",
                status = "Verified",
                fileRef = "DOC-CONT-2025-04"
            ),
            StaffDocument(
                title = "Enhanced DBS Certificate (Adult Barred List)",
                type = "DBS Certificate",
                issueDate = "10/01/2025",
                expiryDate = "10/01/2028",
                status = "Verified",
                fileRef = "DBS-ENH-001928374"
            ),
            StaffDocument(
                title = "UK Right-to-Work Home Office Verification",
                type = "Right-to-Work",
                issueDate = "12/01/2025",
                expiryDate = "Continuous / Indefinite",
                status = "Verified",
                fileRef = "HO-RTW-W99410"
            ),
            StaffDocument(
                title = "The Care Certificate (15 Standards Met)",
                type = "Care Certificate",
                issueDate = "28/04/2025",
                status = "Verified",
                fileRef = "CC-UK-2025-771"
            ),
            StaffDocument(
                title = "Staff Photographic ID & Key Fob Authorization",
                type = "Photo ID",
                issueDate = "15/01/2025",
                expiryDate = "15/01/2027",
                status = "Verified",
                fileRef = "ID-ALLCAREX-04"
            )
        )
    }

    fun createInitialOfficeMessages(): List<OfficeMessage> {
        return listOf(
            OfficeMessage(
                senderName = "Sarah Jenkins",
                senderRole = "Care Coordinator",
                subject = "Arthur Pendelton - New Zimmer Frame",
                messageText = "Hi David, Arthur's physio delivered his new Zimmer frame this morning. Please ensure he uses it for all transfers and document in daily notes.",
                timestamp = "03/10/2026 07:30",
                isUrgent = true,
                isRead = false
            ),
            OfficeMessage(
                senderName = "Operations Desk",
                senderRole = "Branch Admin",
                subject = "Fresh PPE Supplies Ready at Branch",
                messageText = "New boxes of nitrile gloves, aprons, and FFP3 masks are available for pickup at the Westminster hub reception desk.",
                timestamp = "02/10/2026 16:45",
                isUrgent = false,
                isRead = true
            ),
            OfficeMessage(
                senderName = "Marcus Vance",
                senderRole = "Registered Manager",
                subject = "Monthly Carer Voice Council - Thursday 3pm",
                messageText = "Reminder that our monthly carer feedback forum is this Thursday. Paid attendance with tea & pastries provided.",
                timestamp = "01/10/2026 11:20",
                isUrgent = false,
                isRead = true
            )
        )
    }

    fun createInitialManagerNotifications(): List<ManagerNotification> {
        return listOf(
            ManagerNotification(
                staffId = "staff-04",
                staffName = "David Okonkwo",
                category = "Mileage Query",
                details = "Queried 3.5 miles unpaid detour between Camden and Westminster due to road closure on Marylebone Rd.",
                timestamp = "02/10/2026 18:20",
                status = "Acknowledged"
            )
        )
    }

    fun createInitialHandoverDraft(): HandoverDraft {
        return HandoverDraft(
            date = "03/10/2026",
            shiftType = "Morning & Lunch Run (Westminster)",
            carerId = "staff-04",
            carerName = "David Okonkwo",
            visitsCount = 4,
            totalFluidMl = 750,
            tasksSummary = "14/14 planned tasks completed; 0 medication refusals; 1 minor observation logged.",
            aiGeneratedSummary = "Arthur settled well after breakfast and lunch; consumed 550ml total fluid and mobilised safely with Zimmer frame. Harpreet attended for tea call; heel dressing dry and intact. Margaret had morning Sinemet on time. All key-safe codes secured.",
            carerEditedNotes = "Arthur mentioned he is feeling slightly tired today; ensure water glass is refilled on bed call and check front door lock.",
            redFlags = listOf("Arthur: High fall risk vigilance", "Harpreet: Maintain left heel ulcer elevation"),
            isSubmitted = false
        )
    }
}


