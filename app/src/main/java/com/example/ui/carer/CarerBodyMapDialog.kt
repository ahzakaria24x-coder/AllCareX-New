package com.example.ui.carer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CareRepository
import com.example.model.BodyMapMark
import com.example.ui.clients.BodyMapViewer
import com.example.ui.theme.CareMintTeal

@Composable
fun CarerBodyMapDialog(
    clientId: String,
    repository: CareRepository,
    onDismiss: () -> Unit
) {
    val bodyMapMarks by repository.bodyMapMarks.collectAsState()
    val clientMarks = bodyMapMarks.filter { it.clientId == clientId }
    val clients by repository.clients.collectAsState()
    val client = clients.find { it.id == clientId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Accessibility, contentDescription = null, tint = CareMintTeal)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Body Map: ${client?.fullName ?: "Client"}", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Tap front or back body figure to log bruises, rashes, pressure areas, or dressings:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                BodyMapViewer(
                    clientId = clientId,
                    marks = clientMarks,
                    onAddMark = { newMark ->
                        repository.addBodyMapMark(newMark)
                    },
                    modifier = Modifier.fillMaxWidth().testTag("carer_body_map_viewer")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CareMintTeal),
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text("Done", color = androidx.compose.ui.graphics.Color(0xFF0A1128), fontWeight = FontWeight.Bold)
            }
        }
    )
}
