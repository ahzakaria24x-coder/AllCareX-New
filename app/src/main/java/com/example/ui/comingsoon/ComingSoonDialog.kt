package com.example.ui.comingsoon

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CareMintTeal
import com.example.ui.theme.CareMintTealContainer

@Composable
fun ComingSoonDialog(
    moduleTitle: String,
    onDismiss: () -> Unit
) {
    val moduleDetails = when (moduleTitle) {
        "Recruitment" -> Triple(
            "Recruitment & Applicant Tracking (Phase 2)",
            "End-to-end safer recruitment pipeline built for UK care standards.",
            listOf(
                "Job vacancies posting & candidate scoring matrix",
                "Automated Right-to-Work share code checks & DBS barred list integration",
                "Structured reference requests & interview evaluation scoring",
                "Conditional offer letters with digital e-signature and onboarding checklist"
            )
        )
        "Payroll & Invoicing" -> Triple(
            "Payroll & Client Invoicing (Phase 2)",
            "Automated billing and pay calculations driven by verified GPS visit timestamps.",
            listOf(
                "Timesheets auto-generated from real visit check-in & check-out times",
                "National Living Wage (NLW) and travel-time compliance audits",
                "Holiday pay accrual (12.07% statutory) & HMRC mileage allowance (£0.45/mile)",
                "Split-funding invoicing: Local Authority, NHS CHC, and private co-payments",
                "Payroll export pre-formatted for Xero, Sage, and QuickBooks"
            )
        )
        "Quality & CQC Compliance" -> Triple(
            "Quality & CQC Compliance Framework (Phase 3)",
            "Continuous clinical governance and dynamic inspection readiness.",
            listOf(
                "Editable CQC Single Assessment Framework & Quality Statements data model",
                "Adaptable to Care Inspectorate Scotland, CIW Wales, and RQIA Northern Ireland",
                "Policy library with staff version acknowledgements & audit log",
                "Mock inspection self-assessment and CQC evidence-pack ZIP export",
                "Complaints & compliments duty of candour resolution tracking"
            )
        )
        else -> Triple(
            "Advanced Analytics & Integrations (Phase 3)",
            "Cross-system healthcare interoperability and executive BI.",
            listOf(
                "Direct NHS dm+d and GP Connect electronic lookup integration",
                "Incident trends, medication error root cause, and turnover forecasting",
                "Continuity of care index analytics per service user",
                "Automated SMS family notifications and push alert engine"
            )
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CareMintTealContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Construction, contentDescription = null, tint = CareMintTeal, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = moduleDetails.first, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF59E0B).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "ARCHITECTURE PLANNED & DATA MODEL DESIGNED",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFF59E0B),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(text = moduleDetails.second, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)

                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Core Capabilities Designed:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CareMintTeal)

                Spacer(modifier = Modifier.height(6.dp))
                moduleDetails.third.forEach { feature ->
                    Row(
                        modifier = Modifier.padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("• ", color = CareMintTeal, fontWeight = FontWeight.Bold)
                        Text(text = feature, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                modifier = Modifier.testTag("dismiss_coming_soon")
            ) {
                Text("Close Preview", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
            }
        }
    )
}
