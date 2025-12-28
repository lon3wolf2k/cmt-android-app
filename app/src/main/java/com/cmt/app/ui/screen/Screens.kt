package com.cmt.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

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
    val mapView = remember {
        MapView(context).apply {
            setMultiTouchControls(true)
            controller.setZoom(14.0)
            controller.setCenter(GeoPoint(0.0, 0.0))
        }
    }

    DisposableEffect(mapView) {
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onDetach()
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
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            factory = { mapView }
        )
        Button(onClick = onGoHome, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Home")
        }
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Race")
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}
