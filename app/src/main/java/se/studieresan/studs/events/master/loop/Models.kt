package se.studieresan.studs.events.master.loop

import se.studieresan.studs.models.StudsEvent

// Model
data class EventsModel(
        val events: List<StudsEvent> = listOf(),
        val loading: Boolean = true
)

// Events
sealed class Event
data class EventsLoaded(val events: List<StudsEvent>): Event()

// Effects
sealed class Effect
object FetchEvents: Effect()
