package com.example.fittrack

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fittrack.data.RunData


@Composable
fun RunCard(runData: RunData, onDelete: (RunData) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Confirm Deletion") },
            text = { Text("Are you sure you want to delete this run?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onDelete(runData)
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
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
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = runData.date,
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { showDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            RunDetails(runData)
        }
    }
}

@Composable
fun RunDetails(runData: RunData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Row {
            Text(
                text = stringResource(R.string.distance),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "${String.format("%.2f", runData.distance)} miles",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Row {
            Text(
                text = stringResource(R.string.time),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = runData.duration,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Row {
            Text(
                text = stringResource(R.string.pace),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = runData.pace,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}




