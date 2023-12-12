package com.example.fittrack

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.fittrack.data.LocationEntity
import com.example.fittrack.data.MyApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


val locationUpdates = mutableListOf<Location>()

@Composable
fun RecordScreen() {
    val context = LocalContext.current
    var locationState by remember { mutableStateOf<Location?>(null) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    var sessionId by remember { mutableIntStateOf(0) }
    var timerStarted by remember { mutableStateOf(false) }
    var timeCount by remember { mutableLongStateOf(0L) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (hasLocationPermission) {
            getLocationUpdates(context, sessionId, false) { location ->
                locationState = location
            }
        }
    }

    LaunchedEffect(key1 = true) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        } else {
            hasLocationPermission = true
            getLocationUpdates(context, sessionId, false) { location ->
                locationState = location
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MapViewComposable(
            locationState = remember { mutableStateOf(locationState) },
            modifier = Modifier.padding(top = 65.dp)
        )

        Column(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            locationState?.let { location ->
                Text(text = "Latitude: ${location.latitude}, Longitude: ${location.longitude}")
                Spacer(modifier = Modifier.height(8.dp))
            } ?: Text(text = "Waiting for location...")

            Spacer(modifier = Modifier.height(8.dp))

            // Circular Record Button
            Button(
                onClick = {
                    timerStarted = !timerStarted
                    if (timerStarted) {
                        sessionId = (System.currentTimeMillis() / 1000).toInt()
                        locationUpdates.clear()
                        getLocationUpdates(context, sessionId, true) { location ->
                            locationState = location
                            locationUpdates.add(location)
                        }
                    } else {
                        val totalDistance = calculateTotalDistance(locationUpdates.map { location ->
                            LocationEntity(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                timestamp = location.time,
                                sessionId = sessionId
                            )
                        })
                        val sessionData = collectSessionData(timeCount, totalDistance)
                        CoroutineScope(Dispatchers.IO).launch {
                            MyApp.database?.sessionDao()?.insertSession(sessionData)
                        }
                    }
                },
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                content = {
                    Text(
                        text = if (timerStarted) "Stop" else "Start",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = formatDuration(timeCount))
        }
    }

    LaunchedEffect(key1 = timerStarted) {
        while (timerStarted) {
            delay(1000)
            timeCount++
        }
    }
}

fun getLocationUpdates(context: Context, sessionId: Int, startUpdates: Boolean, onLocationReceived: (Location) -> Unit) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val listener = LocationListener { location ->
        onLocationReceived(location)

        if (startUpdates) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    MyApp.database?.locationDao()?.insertLocation(
                        LocationEntity(
                            sessionId = sessionId,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    Log.d("LocationUpdate", "Location inserted: Lat: ${location.latitude}, Long: ${location.longitude}")
                } catch (e: Exception) {
                    Log.e("LocationUpdate", "Error inserting location into database", e)
                }
            }
        }
    }

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, listener)
    } else if (!startUpdates) {
        locationManager.removeUpdates(listener)
    }
}

