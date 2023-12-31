package com.example.fittrack

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import com.example.fittrack.data.MyApp
import com.example.fittrack.data.RunData
import com.example.fittrack.data.SessionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen() {
    val runs = remember { mutableStateOf<List<RunData>>(listOf()) }
    val coroutineScope = rememberCoroutineScope()
    val refreshTrigger = remember { mutableStateOf(false) }

    GenerateRunDataEffect(runs)

    val onDelete: (RunData) -> Unit = { runData ->
        coroutineScope.launch(Dispatchers.IO) {
            try {
                MyApp.database?.sessionDao()?.deleteSession(
                    SessionEntity(
                        id = runData.sessionId,
                        date = runData.date,
                        distance = runData.distance,
                        duration = parseDuration(runData.duration)
                    )
                )

                MyApp.database?.locationDao()?.deleteLocationsBySession(runData.sessionId)

                cleanupOrphanedLocations()

                val updatedRunData = MyApp.database?.sessionDao()?.getAllSessions()?.map { entity ->
                    RunData(
                        sessionId = entity.id,
                        date = entity.date,
                        distance = entity.distance,
                        duration = formatDuration(entity.duration),
                        pace = calculatePace(entity.distance, entity.duration)
                    )
                } ?: listOf()

                withContext(Dispatchers.Main) {
                    runs.value = updatedRunData
                    refreshTrigger.value = !refreshTrigger.value
                }
            } catch (e: Exception) {
                Log.e("HomeScreen", "Error deleting run: ${e.message}")
                withContext(Dispatchers.Main) {
                }
            }
        }
    }


    LazyColumn(
        contentPadding = PaddingValues(
            top = 80.dp,
            bottom = 80.dp
        )
    ) {
        items(runs.value) { run ->
            RunCard(run, onDelete)
        }
    }
}

@Composable
fun GenerateRunDataEffect(runs: MutableState<List<RunData>>) {
    val runData = generateRunData()
    LaunchedEffect(runData) {
        runs.value = runData
    }
}

