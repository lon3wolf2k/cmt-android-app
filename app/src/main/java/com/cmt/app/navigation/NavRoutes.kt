package com.cmt.app.navigation

object Routes {
    const val HOME = "home"
    const val EVENT = "event/{eventId}"
    const val RACES = "races/{eventId}"
    const val RACE = "race/{raceId}"
    const val MAP = "map/{raceId}"
}

fun eventRoute(eventId: String) = "event/$eventId"
fun racesRoute(eventId: String) = "races/$eventId"
fun raceRoute(raceId: String) = "race/$raceId"
fun mapRoute(raceId: String) = "map/$raceId"
