package se.studieresan.studs.events.master.loop

import com.spotify.mobius.Next
import se.studieresan.studs.next

fun update(model: EventsModel, event: Event): Next<EventsModel, Effect> =
        when (event) {
            is EventsLoaded ->
                next(model = model.copy(events = event.events, loading = false))
            is Logout -> next(effects = setOf(TriggerLogout))
            is LoggedOut -> next(model = model.copy(loggedOut = true))
        }

