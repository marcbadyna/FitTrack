package com.example.fittrack

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fittrack.data.RunData


@Composable
fun RunCard(runData: RunData, onDelete: (RunData) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Confirm Deletion") },
            text = { Text("Are you sure you want to delete this run?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onDelete(runData)
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Default.DirectionsRun,
                    contentDescription = "Run",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = runData.date,
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }
                CustomDropdownMenu(expanded, onDismiss = { expanded = false }) {
                    CustomDropdownMenuItem("Delete") {
                        showDialog = true
                        expanded = false
                    }
                    CustomDropdownMenuItem("Cancel") {
                        expanded = false
                    }
                }
            }
            RunDetails(runData)
        }
    }
}

@Composable
fun CustomDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    if (expanded) {
        Column {
            content()
        }
        DisposableEffect(Unit) {
            onDispose { onDismiss() }
        }
    }
}

@Composable
fun CustomDropdownMenuItem(text: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(text)
    }
}


@Composable
fun RunDetails(runData: RunData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        // Left side for Distance
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
            Text(
                text = String.format("%.2f", runData.distance),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Miles",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Middle column for Time and Pace
        Column(modifier = Modifier.weight(1.2f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.time),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(30.dp)) // Space between label and value
                Text(
                    text = runData.duration,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.height(4.dp)) // Space between Time and Pace rows
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.pace),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(30.dp)) // Space between label and value
                Text(
                    text = runData.pace,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Right side spacer
        Spacer(modifier = Modifier.weight(1f))
    }
}














