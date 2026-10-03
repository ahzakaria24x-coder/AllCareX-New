package com.example.ui.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.model.ClientInvoice
import com.example.model.InvoiceLineItem
import com.example.model.InvoiceStatus
import com.example.ui.theme.CareMintTeal
import com.example.ui.theme.CareMintTealContainer

@Composable
fun ClientInvoicingView(
    repository: CareRepository
) {
    val invoices by repository.invoices.collectAsState()
    val fundingRules by repository.fundingRules.collectAsState()

    var selectedInvoiceForPayment by remember { mutableStateOf<ClientInvoice?>(null) }
    var selectedInvoiceForCreditNote by remember { mutableStateOf<ClientInvoice?>(null) }
    var selectedInvoiceForDispute by remember { mutableStateOf<Pair<ClientInvoice, InvoiceLineItem>?>(null) }
    var showPdfSuccessModal by remember { mutableStateOf<ClientInvoice?>(null) }
    var showAccountingExportSuccess by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Banner & Accounting Export Placeholder
        item {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = CareMintTealContainer.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Delivered-Visit Invoicing & Split Funding", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                            Text("Invoices generated strictly from verified GPS delivered visits. VAT-exempt under VATA 1994 Sch 9 Group 7.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                        }

                        Button(
                            onClick = { showAccountingExportSuccess = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp).testTag("export_accounting_button")
                        ) {
                            Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, tint = Color(0xFF0A1128), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Xero / Sage CSV", color = Color(0xFF0A1128), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (showAccountingExportSuccess) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = "✓ Exported: Account mapping ready for Xero, Sage 50, and QuickBooks UK. API connector endpoint configured: /api/v1/integrations/accounting",
                                fontSize = 10.sp,
                                color = CareMintTeal,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Client Invoices (${invoices.size})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(invoices) { invoice ->
            DetailedInvoiceCard(
                invoice = invoice,
                onApprove = { repository.approveInvoice(invoice.id) },
                onDispatch = { repository.dispatchInvoice(invoice.id) },
                onRecordPayment = { selectedInvoiceForPayment = invoice },
                onIssueCreditNote = { selectedInvoiceForCreditNote = invoice },
                onDisputeLine = { line -> selectedInvoiceForDispute = Pair(invoice, line) },
                onViewPdf = { showPdfSuccessModal = invoice }
            )
        }
    }

    // Modal: Record Payment in Ledger
    if (selectedInvoiceForPayment != null) {
        val inv = selectedInvoiceForPayment!!
        var payAmount by remember { mutableStateOf("${inv.outstandingBalance}") }
        var payMethod by remember { mutableStateOf("Local Authority Remittance (BACS)") }
        var payRef by remember { mutableStateOf("BACS-" + System.currentTimeMillis().toString().takeLast(6)) }

        AlertDialog(
            onDismissRequest = { selectedInvoiceForPayment = null },
            title = { Text("Record Payment in Ledger", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(text = "Invoice: ${inv.invoiceNumber} (${inv.clientName})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Outstanding Balance: £${"%.2f".format(inv.outstandingBalance)}", fontSize = 12.sp, color = CareMintTeal)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = payAmount,
                        onValueChange = { payAmount = it },
                        label = { Text("Payment Amount (£)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = payMethod,
                        onValueChange = { payMethod = it },
                        label = { Text("Payment Method (BACS / Direct Debit / Card)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = payRef,
                        onValueChange = { payRef = it },
                        label = { Text("Transaction Reference") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = payAmount.toDoubleOrNull() ?: inv.outstandingBalance
                        repository.recordInvoicePayment(inv.id, amount, payMethod, payRef)
                        selectedInvoiceForPayment = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                    modifier = Modifier.testTag("confirm_record_payment_button")
                ) {
                    Text("Save to Ledger", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedInvoiceForPayment = null }) { Text("Cancel") }
            }
        )
    }

    // Modal: Issue Credit Note
    if (selectedInvoiceForCreditNote != null) {
        val inv = selectedInvoiceForCreditNote!!
        var creditAmount by remember { mutableStateOf("21.00") }
        var creditReason by remember { mutableStateOf("Cancelled hospital admission call credit") }

        AlertDialog(
            onDismissRequest = { selectedInvoiceForCreditNote = null },
            title = { Text("Issue Credit Note", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(text = "Invoice: ${inv.invoiceNumber} (${inv.clientName})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = creditAmount,
                        onValueChange = { creditAmount = it },
                        label = { Text("Credit Amount (£)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = creditReason,
                        onValueChange = { creditReason = it },
                        label = { Text("Credit Reason") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = creditAmount.toDoubleOrNull() ?: 0.0
                        repository.issueCreditNote(inv.id, amount, creditReason)
                        selectedInvoiceForCreditNote = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                    modifier = Modifier.testTag("confirm_credit_note_button")
                ) {
                    Text("Issue Credit Note", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedInvoiceForCreditNote = null }) { Text("Cancel") }
            }
        )
    }

    // Modal: Flag Invoice Line Disputed
    if (selectedInvoiceForDispute != null) {
        val (inv, line) = selectedInvoiceForDispute!!
        var reasonInput by remember { mutableStateOf("Family disputes 15 min call length on ${line.date}") }

        AlertDialog(
            onDismissRequest = { selectedInvoiceForDispute = null },
            title = { Text("Dispute Invoice Line Item", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(text = "Line: ${line.description} (£${"%.2f".format(line.totalAmount)})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reasonInput,
                        onValueChange = { reasonInput = it },
                        label = { Text("Reason for Dispute") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.flagInvoiceLineDisputed(inv.id, line.id, reasonInput)
                        selectedInvoiceForDispute = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Flag Disputed", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedInvoiceForDispute = null }) { Text("Cancel") }
            }
        )
    }

    // Modal: PDF Output Preview
    if (showPdfSuccessModal != null) {
        val inv = showPdfSuccessModal!!
        AlertDialog(
            onDismissRequest = { showPdfSuccessModal = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFEF4444))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Invoice PDF & Evidence Generated", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(text = "INVOICE: ${inv.invoiceNumber}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = "Billed to: ${inv.clientName} (Gross: £${"%.2f".format(inv.grossTotal)})", fontSize = 12.sp)
                    Text(text = "Period: ${inv.billingPeriod} • ${inv.totalVisitsDelivered} Visits Delivered", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CareMintTealContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("✓ Attached: Itemised GPS visit time log", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
                            Text("✓ Included: Local Authority split schedule (£${"%.2f".format(inv.splits.localAuthorityTotal)})", fontSize = 10.sp)
                            Text("✓ Included: Statutory VAT Exemption Notice", fontSize = 10.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPdfSuccessModal = null },
                    colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal)
                ) {
                    Text("Close", color = Color(0xFF0A1128), fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun DetailedInvoiceCard(
    invoice: ClientInvoice,
    onApprove: () -> Unit,
    onDispatch: () -> Unit,
    onRecordPayment: () -> Unit,
    onIssueCreditNote: () -> Unit,
    onDisputeLine: (InvoiceLineItem) -> Unit,
    onViewPdf: () -> Unit
) {
    var expandedLines by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(text = invoice.invoiceNumber, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = CareMintTeal)
                    Text(text = "Client: ${invoice.clientName}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Period: ${invoice.billingPeriod} • ${invoice.totalVisitsDelivered} Visits (${invoice.totalHoursDelivered}h)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(invoice.status.colorHex).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = invoice.status.displayName,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(invoice.status.colorHex),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            // Split Funding Breakdown
            Text("SPLIT FUNDING APPORTIONMENT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CareMintTeal)
            Spacer(modifier = Modifier.height(4.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Local Authority", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("£${"%.2f".format(invoice.splits.localAuthorityTotal)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("NHS CHC", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("£${"%.2f".format(invoice.splits.nhsChcTotal)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Private Client", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("£${"%.2f".format(invoice.splits.privateClientTotal)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Outstanding", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("£${"%.2f".format(invoice.outstandingBalance)}", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = if (invoice.outstandingBalance > 0) Color(0xFFF59E0B) else Color(0xFF10B981))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Traceability: Expandable Delivered Visit Line Items
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedLines = !expandedLines },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Trace to Source Visits (${invoice.lines.size} Lines)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CareMintTeal
                )
                Icon(
                    imageVector = if (expandedLines) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = CareMintTeal,
                    modifier = Modifier.size(18.dp)
                )
            }

            if (expandedLines) {
                Spacer(modifier = Modifier.height(6.dp))
                invoice.lines.forEach { line ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "${line.date}: ${line.description}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "Delivered: ${line.deliveredMinutes}m (Billed: ${line.billedMinutes}m) @ £${line.hourlyRate}/h",
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (line.isDisputed) {
                                    Text(text = "⚠ Disputed: ${line.disputeReason}", fontSize = 9.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "£${"%.2f".format(line.totalAmount)}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                if (!line.isDisputed) {
                                    Text(
                                        text = "Dispute",
                                        fontSize = 9.sp,
                                        color = Color(0xFFF59E0B),
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.clickable { onDisputeLine(line) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row (Approval, Dispatch, PDF, Payment, Credit Note)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onViewPdf,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp).testTag("pdf_invoice_${invoice.id}")
                ) {
                    Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("PDF", fontSize = 10.sp)
                }

                OutlinedButton(
                    onClick = onRecordPayment,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp).testTag("record_payment_${invoice.id}")
                ) {
                    Text("+ Payment", fontSize = 10.sp)
                }

                OutlinedButton(
                    onClick = onIssueCreditNote,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp).testTag("credit_note_${invoice.id}")
                ) {
                    Text("+ Credit", fontSize = 10.sp)
                }

                Spacer(modifier = Modifier.weight(1f))

                if (invoice.status == InvoiceStatus.DRAFT) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp).testTag("approve_invoice_${invoice.id}")
                    ) {
                        Text("Approve", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (invoice.status == InvoiceStatus.APPROVED) {
                    Button(
                        onClick = onDispatch,
                        colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp).testTag("send_invoice_${invoice.id}")
                    ) {
                        Text("Dispatch", color = Color(0xFF0A1128), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
