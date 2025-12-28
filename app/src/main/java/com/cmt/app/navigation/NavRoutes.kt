package com.cmt.app.navigation

object NavRoutes {
    const val Home = "home"
    const val Event = "event/{eventId}"
    const val Races = "races/{eventId}"
    const val RaceDetails = "race/{raceId}"
    const val Map = "map/{raceId}"

    fun event(eventId: String) = "event/$eventId"
    fun races(eventId: String) = "races/$eventId"
    fun raceDetails(raceId: String) = "race/$raceId"
    fun map(raceId: String) = "map/$raceId"
}
