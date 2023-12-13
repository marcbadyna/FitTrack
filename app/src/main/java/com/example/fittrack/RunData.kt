package com.example.fittrack

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.fittrack.data.LocationEntity
import com.example.fittrack.data.MyApp
import com.example.fittrack.data.RunData
import com.example.fittrack.data.SessionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun getCurrentDate(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}

fun collectSessionData(timeCount: Long, distance: Double): SessionEntity {
    return SessionEntity(
        date = getCurrentDate(),
        duration = timeCount,
        distance = distance
    )
}

fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371e3
    val phi1 = lat1 * Math.PI / 180
    val phi2 = lat2 * Math.PI / 180
    val deltaPhi = (lat2 - lat1) * Math.PI / 180
    val deltaLambda = (lon2 - lon1) * Math.PI / 180

    val a = sin(deltaPhi / 2) * sin(deltaPhi / 2) +
            cos(phi1) * cos(phi2) *
            sin(deltaLambda / 2) * sin(deltaLambda / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return r * c * 0.000621371
}

fun calculateTotalDistance(locations: List<LocationEntity>): Double {
    var totalDistance = 0.0
    for (i in 0 until locations.size - 1) {
        totalDistance += haversineDistance(
            locations[i].latitude,
            locations[i].longitude,
            locations[i + 1].latitude,
            locations[i + 1].longitude
        )
    }
    return totalDistance
}

fun formatDuration(durationInSeconds: Long): String {
    val hours = java.util.concurrent.TimeUnit.SECONDS.toHours(durationInSeconds)
    val minutes = java.util.concurrent.TimeUnit.SECONDS.toMinutes(durationInSeconds) % 60
    val seconds = durationInSeconds % 60

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

fun calculatePace(distance: Double, durationInSeconds: Long): String {
    if (distance <= 0) {
        return "N/A"
    }
    val durationInMinutes = durationInSeconds / 60.0
    val pace = durationInMinutes / distance

    val paceMinutes = pace.toInt()
    val paceSeconds = ((pace - paceMinutes) * 60).toInt()
    return String.format("%d:%02d /mi", paceMinutes, paceSeconds)
}

@Composable
fun generateRunData(): List<RunData> {
    val runDataList = remember { mutableStateOf<List<RunData>>(listOf()) }

    LaunchedEffect(key1 = "load_run_data") {
        CoroutineScope(Dispatchers.IO).launch {
            val sessionEntities = MyApp.database?.sessionDao()?.getAllSessions() ?: listOf()
            val updatedRunData = sessionEntities.map { entity ->
                RunData(
                    sessionId = entity.id,
                    date = entity.date,
                    distance = entity.distance,
                    duration = formatDuration(entity.duration),
                    pace = calculatePace(entity.distance, entity.duration)
                )
            }
            runDataList.value = updatedRunData
        }
    }

    return runDataList.value
}

fun parseDuration(duration: String): Long {
    val parts = duration.split(":").map { it.toInt() }
    return when (parts.size) {
        3 -> (parts[0] * 3600L) + (parts[1] * 60L) + parts[2]
        2 -> (parts[0] * 60L) + parts[1]
        1 -> parts[0].toLong()
        else -> 0L
    }
}

fun cleanupOrphanedLocations() {
    CoroutineScope(Dispatchers.IO).launch {
        Log.d("HomeScreen", "Cleaning up orphaned locations")
        MyApp.database?.locationDao()?.deleteOrphanedLocations()
    }
}





