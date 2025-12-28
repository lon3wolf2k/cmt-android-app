package com.cmt.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(onOpenEvent: (String) -> Unit, onBack: () -> Unit) {
    ScreenTemplate(
        title = "Home",
        description = "Welcome to CMT. Choose where to go next.",
        primaryLabel = "Open Sample Event",
        onPrimaryClick = { onOpenEvent("event-1") },
        secondaryLabel = "Back",
        onSecondaryClick = onBack
    )
}

@Composable
fun EventScreen(eventId: String, onBack: () -> Unit, onViewRaces: () -> Unit) {
    ScreenTemplate(
        title = "Event $eventId",
        description = "Details for event $eventId. Navigate to races.",
        primaryLabel = "View Races",
        onPrimaryClick = onViewRaces,
        secondaryLabel = "Back",
        onSecondaryClick = onBack
    )
}

@Composable
fun RacesScreen(eventId: String, onBack: () -> Unit, onOpenRace: (String) -> Unit) {
    ScreenTemplate(
        title = "Races for $eventId",
        description = "Pick a race to see more details.",
        primaryLabel = "Open Race 1",
        onPrimaryClick = { onOpenRace("race-1") },
        secondaryLabel = "Back",
        onSecondaryClick = onBack
    )
}

@Composable
fun RaceDetailsScreen(raceId: String, onBack: () -> Unit, onViewMap: () -> Unit) {
    ScreenTemplate(
        title = "Race $raceId",
        description = "Placeholder race details for $raceId.",
        primaryLabel = "View Map",
        onPrimaryClick = onViewMap,
        secondaryLabel = "Back",
        onSecondaryClick = onBack
    )
}

@Composable
fun MapScreen(raceId: String, onBack: () -> Unit, onGoHome: () -> Unit) {
    ScreenTemplate(
        title = "Map for $raceId",
        description = "Map placeholder for race $raceId.",
        primaryLabel = "Back to Race",
        onPrimaryClick = onBack,
        secondaryLabel = "Back to Home",
        onSecondaryClick = onGoHome
    )
}

@Composable
private fun ScreenTemplate(
    title: String,
    description: String,
    primaryLabel: String,
    onPrimaryClick: () -> Unit,
    secondaryLabel: String,
    onSecondaryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onPrimaryClick, modifier = Modifier.fillMaxWidth()) {
            Text(primaryLabel)
        }
        Button(onClick = onSecondaryClick, modifier = Modifier.fillMaxWidth()) {
            Text(secondaryLabel)
        }
    }
}
