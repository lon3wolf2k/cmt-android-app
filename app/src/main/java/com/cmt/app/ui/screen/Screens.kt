package com.cmt.app.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.cmt.app.data.downloadGpxIfMissing
import com.cmt.app.data.fileExistsAndHasSize
import com.cmt.app.data.getGpxUrlForRace
import com.cmt.app.data.parseGpxTrackPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.tileprovider.modules.GpsMyLocationProvider
import org.osmdroid.tileprovider.modules.OfflineTileProvider
import org.osmdroid.tileprovider.modules.SimpleRegisterReceiver
import org.osmdroid.tileprovider.tilesource.FileBasedTileSource
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MyLocationNewOverlay
import org.osmdroid.views.overlay.Polyline
import java.io.File

@Composable
fun HomeScreen(
    onOpenEvent: (String) -> Unit,
    onOpenRaces: (String) -> Unit
) {
    val sampleEventId = "event-1"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Home", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Welcome to CMT. Choose where to go next.", style = MaterialTheme.typography.bodyLarge)
        Button(onClick = { onOpenEvent(sampleEventId) }) {
            Text("Open Event $sampleEventId")
        }
        Button(onClick = { onOpenRaces(sampleEventId) }) {
            Text("View Races for $sampleEventId")
        }
    }
}

@Composable
fun EventScreen(
    eventId: String,
    onBack: () -> Unit,
    onOpenRaces: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Event", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "Showing details for event: $eventId",
            style = MaterialTheme.typography.bodyLarge
        )
        Button(onClick = onOpenRaces) {
            Text("Go to Races")
        }
        Button(onClick = onBack) {
            Text("Back")
        }
    }
}

@Composable
fun RacesScreen(
    eventId: String,
    onBack: () -> Unit,
    onOpenRace: (String) -> Unit
) {
    val sampleRaceId = "race-101"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Races", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "Races for event: $eventId",
            style = MaterialTheme.typography.bodyLarge
        )
        Button(onClick = { onOpenRace(sampleRaceId) }) {
            Text("Open Race $sampleRaceId")
        }
        Button(onClick = onBack) {
            Text("Back to Event")
        }
    }
}

@Composable
fun RaceDetailsScreen(
    raceId: String,
    onBack: () -> Unit,
    onOpenMap: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Race Details", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "Details for race: $raceId",
            style = MaterialTheme.typography.bodyLarge
        )
        Button(onClick = onOpenMap) {
            Text("View Map")
        }
        Button(onClick = onBack) {
            Text("Back to Races")
        }
    }
}

@Composable
fun MapScreen(
    raceId: String,
    onBack: () -> Unit,
    onGoHome: () -> Unit
) {
    val context = LocalContext.current
    val hasFineLocationPermission = {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    val mbtilesFile = remember {
        context.getExternalFilesDir(null)?.let { baseDir ->
            File(baseDir, "maps/corfu.mbtiles")
        }
    }
    val registerReceiver = remember { SimpleRegisterReceiver(context) }
    val routeCacheFile = remember(raceId) {
        File(context.cacheDir, "routes/$raceId.gpx")
    }
    var routePoints by remember { mutableStateOf<List<GeoPoint>>(emptyList()) }
    val polyline = remember {
        Polyline().apply {
            outlinePaint.color = Color.RED
            outlinePaint.strokeWidth = 6f
        }
    }
    val mapView = remember {
        MapView(context).apply {
            setMultiTouchControls(true)
            setUseDataConnection(false)
            controller.setZoom(11.0)
            controller.setCenter(GeoPoint(39.6243, 19.9217))
        }
    }
    val locationOverlay = remember {
        MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
    }
    var hasLocationPermission by remember { mutableStateOf(hasFineLocationPermission()) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
        if (!granted) {
            Log.w("MapScreen", "Location permission denied")
        }
    }

    fun enableLocationOverlay() {
        if (!mapView.overlays.contains(locationOverlay)) {
            mapView.overlays.add(locationOverlay)
        }
        locationOverlay.enableMyLocation()
    }

    fun disableLocationOverlay() {
        locationOverlay.disableFollowLocation()
        locationOverlay.disableMyLocation()
        mapView.overlays.remove(locationOverlay)
    }

    DisposableEffect(mapView) {
        mapView.overlays.add(polyline)
        mapView.onResume()
        onDispose {
            disableLocationOverlay()
            mapView.overlays.remove(polyline)
            mapView.onPause()
            mapView.onDetach()
        }
    }

    LaunchedEffect(mbtilesFile) {
        if (mbtilesFile != null && fileExistsAndHasSize(mbtilesFile)) {
            try {
                val provider = OfflineTileProvider(registerReceiver, arrayOf(mbtilesFile))
                mapView.setTileProvider(provider)
                val tileSourceName = provider.tileSources.firstOrNull()
                val tileSource = tileSourceName?.let { FileBasedTileSource.getSource(it) }
                if (tileSource != null) {
                    mapView.setTileSource(tileSource)
                }
                mapView.setUseDataConnection(false)
                mapView.controller.setZoom(11.0)
                mapView.controller.setCenter(GeoPoint(39.6243, 19.9217))
                mapView.invalidate()
            } catch (e: Exception) {
                Log.e("MapScreen", "Failed to load offline tiles: ${e.message}", e)
            }
        } else {
            Log.i("MapScreen", "Offline MBTiles not available yet; using default tiles")
        }
    }

    LaunchedEffect(raceId) {
        try {
            val url = getGpxUrlForRace(raceId)
            downloadGpxIfMissing(url, routeCacheFile)
            val points = withContext(Dispatchers.IO) { parseGpxTrackPoints(routeCacheFile) }
            routePoints = points
            polyline.setPoints(points)
            mapView.invalidate()
        } catch (e: Exception) {
            Log.e("MapScreen", "Error loading GPX for race $raceId: ${e.message}", e)
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            enableLocationOverlay()
        } else {
            disableLocationOverlay()
        }
    }

    fun centerOnUser() {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }
        val current = locationOverlay.myLocation
        if (current != null) {
            mapView.controller.animateTo(current, 15.0, 1000L)
        } else {
            locationOverlay.enableMyLocation()
            locationOverlay.runOnFirstFix {
                val fix = locationOverlay.myLocation
                if (fix != null) {
                    mapView.post {
                        mapView.controller.animateTo(fix, 15.0, 1000L)
                    }
                } else {
                    mapView.post {
                        Log.w("MapScreen", "Location fix unavailable")
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Race Map", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "Map for race: $raceId",
            style = MaterialTheme.typography.bodyLarge
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            AndroidView(
                modifier = Modifier.matchParentSize(),
                factory = { mapView }
            )
            FloatingActionButton(
                onClick = { centerOnUser() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
            ) {
                Text("Center on me")
            }
        }
        Button(
            onClick = {
                if (routePoints.isNotEmpty()) {
                    val bbox: BoundingBox = BoundingBox.fromGeoPointsSafe(routePoints)
                    mapView.zoomToBoundingBox(bbox, true, 50)
                }
            },
            enabled = routePoints.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Zoom to Route")
        }
        Button(onClick = onGoHome, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Home")
        }
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Race")
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}
