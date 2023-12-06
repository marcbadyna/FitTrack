package com.example.fittrack

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fittrack.data.MyApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CalendarScreen(refreshTrigger: MutableState<Boolean>) {
    val totalDistance = remember { mutableDoubleStateOf(0.0) }
    val totalTime = remember { mutableLongStateOf(0L) }
    val numberOfRuns = remember { mutableIntStateOf(0) }
    val averagePace = remember { mutableStateOf("0:00 min/mile") }

    LaunchedEffect(refreshTrigger.value) {
        CoroutineScope(Dispatchers.IO).launch {
            val sessions = MyApp.database?.sessionDao()?.getAllSessions() ?: listOf()
            val totalDist = sessions.sumOf { it.distance }
            val totalDur = sessions.sumOf { it.duration }
            val runCount = sessions.size
            val avgPace = if (runCount > 0) {
                val totalPaceMinutes = sessions.sumOf { durationToMinutes(it.duration) }
                calculatePace(totalDist, (totalPaceMinutes / runCount).toLong())
            } else "N/A"

            withContext(Dispatchers.Main) {
                totalDistance.doubleValue = totalDist
                totalTime.longValue = totalDur
                numberOfRuns.intValue = runCount
                averagePace.value = avgPace
            }
        }
    }

    Column(modifier = Modifier.padding(80.dp)) {
        StatisticCard(label = "Total Distance", value = "${String.format("%.2f", totalDistance.doubleValue)} miles")
        StatisticCard(label = "Total Time", value = formatDuration(totalTime.longValue))
        StatisticCard(label = "Number of Runs", value = "${numberOfRuns.intValue}")
        StatisticCard(label = "Average Pace", value = averagePace.value)
    }
}

@Composable
fun StatisticCard(label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            Text(text = value, style = MaterialTheme.typography.bodySmall)
        }
    }
}

fun durationToMinutes(durationInSeconds: Long): Double {
    return durationInSeconds / 60.0
}



