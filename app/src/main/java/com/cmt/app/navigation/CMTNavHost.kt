package com.cmt.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cmt.app.ui.screens.EventScreen
import com.cmt.app.ui.screens.HomeScreen
import com.cmt.app.ui.screens.MapScreen
import com.cmt.app.ui.screens.RaceDetailsScreen
import com.cmt.app.ui.screens.RacesScreen

@Composable
fun CMTNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onOpenEvent = { navController.navigate(eventRoute(it)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.EVENT,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId").orEmpty()
            EventScreen(
                eventId = eventId,
                onBack = { navController.popBackStack() },
                onViewRaces = { navController.navigate(racesRoute(eventId)) }
            )
        }
        composable(
            route = Routes.RACES,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId").orEmpty()
            RacesScreen(
                eventId = eventId,
                onBack = { navController.popBackStack() },
                onOpenRace = { raceId -> navController.navigate(raceRoute(raceId)) }
            )
        }
        composable(
            route = Routes.RACE,
            arguments = listOf(navArgument("raceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val raceId = backStackEntry.arguments?.getString("raceId").orEmpty()
            RaceDetailsScreen(
                raceId = raceId,
                onBack = { navController.popBackStack() },
                onViewMap = { navController.navigate(mapRoute(raceId)) }
            )
        }
        composable(
            route = Routes.MAP,
            arguments = listOf(navArgument("raceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val raceId = backStackEntry.arguments?.getString("raceId").orEmpty()
            MapScreen(
                raceId = raceId,
                onBack = { navController.popBackStack() },
                onGoHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
