package com.example.fittrack

import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@Composable
fun MapViewComposable(locationState: State<Location?>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    var googleMap: GoogleMap? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        mapView.onCreate(null)
        mapView.onStart()
        mapView.onResume()

        onDispose {
            mapView.onStop()
            mapView.onDestroy()
        }
    }
    AndroidView(
        { mapView },
        modifier = modifier
            .fillMaxWidth()
            .height(500.dp)
    ) { mapView ->
        mapView.getMapAsync { map ->
            googleMap = map
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            updateMapWithLocation(map, locationState.value)
        }
    }
    LaunchedEffect(locationState.value) {
        if (locationState.value != null) {
            updateMapWithLocation(googleMap, locationState.value)
        } else {
            Log.d("MapViewComposable", "Waiting for location update...")
        }
    }
}

private fun updateMapWithLocation(map: GoogleMap?, location: Location?) {
    if (map == null) {
        Log.d("MapViewComposable", "Google Map instance is null.")
        return
    }
    location?.let {
        val currentLocation = LatLng(it.latitude, it.longitude)
        Log.d("MapViewComposable", "Location updated: Lat: ${it.latitude}, Long: ${it.longitude}")
        map.apply {
            clear()
            addMarker(MarkerOptions().position(currentLocation).title("Your Location"))
            moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
        }
    } ?: Log.d("MapViewComposable", "Location is null.")
}






