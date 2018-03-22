package se.studieresan.studs.events.master.loop

import com.spotify.mobius.Next
import com.spotify.mobius.Next.next

fun update(model: EventsModel, event: Event): Next<EventsModel, Effect> =
        when (event) {
            is EventsLoaded ->
                next(model.copy(events = event.events, loading = false))
        }

