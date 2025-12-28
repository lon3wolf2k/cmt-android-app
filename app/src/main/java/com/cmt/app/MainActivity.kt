package com.cmt.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.cmt.app.navigation.NavRoutes
import com.cmt.app.ui.screen.EventScreen
import com.cmt.app.ui.screen.HomeScreen
import com.cmt.app.ui.screen.MapScreen
import com.cmt.app.ui.screen.RaceDetailsScreen
import com.cmt.app.ui.screen.RacesScreen
import com.cmt.app.ui.theme.CmtTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CmtAppContent()
        }
    }
}

@Composable
private fun CmtAppContent() {
    val navController = rememberNavController()
    CmtTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = NavRoutes.Home
            ) {
                composable(NavRoutes.Home) {
                    HomeScreen(
                        onOpenEvent = { eventId -> navController.navigate(NavRoutes.event(eventId)) },
                        onOpenRaces = { eventId -> navController.navigate(NavRoutes.races(eventId)) }
                    )
                }
                composable(
                    route = NavRoutes.Event,
                    arguments = listOf(navArgument("eventId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val eventId = backStackEntry.arguments?.getString("eventId").orEmpty()
                    EventScreen(
                        eventId = eventId,
                        onBack = { navController.popBackStack() },
                        onOpenRaces = { navController.navigate(NavRoutes.races(eventId)) }
                    )
                }
                composable(
                    route = NavRoutes.Races,
                    arguments = listOf(navArgument("eventId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val eventId = backStackEntry.arguments?.getString("eventId").orEmpty()
                    RacesScreen(
                        eventId = eventId,
                        onBack = { navController.popBackStack() },
                        onOpenRace = { raceId -> navController.navigate(NavRoutes.raceDetails(raceId)) }
                    )
                }
                composable(
                    route = NavRoutes.RaceDetails,
                    arguments = listOf(navArgument("raceId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val raceId = backStackEntry.arguments?.getString("raceId").orEmpty()
                    RaceDetailsScreen(
                        raceId = raceId,
                        onBack = { navController.popBackStack() },
                        onOpenMap = { navController.navigate(NavRoutes.map(raceId)) }
                    )
                }
                composable(
                    route = NavRoutes.Map,
                    arguments = listOf(navArgument("raceId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val raceId = backStackEntry.arguments?.getString("raceId").orEmpty()
                    MapScreen(
                        raceId = raceId,
                        onBack = { navController.popBackStack() },
                        onGoHome = {
                            navController.navigate(NavRoutes.Home) {
                                popUpTo(NavRoutes.Home) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    }
}
