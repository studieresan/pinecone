package se.studieresan.studs.events.master.loop

import com.spotify.mobius.Connection
import com.spotify.mobius.functions.Consumer
import io.reactivex.Observable
import se.studieresan.studs.api.EventSource
import se.studieresan.studs.models.StudsEvent

val TAG = "EventsEffectHandler"

class EffectHandler(eventSource: EventSource) {

    val events: Observable<List<StudsEvent>> by lazy {
        eventSource.fetchEvents().cache()
    }

    fun effectHandler(): (eventConsumer: Consumer<Event>)
        -> Connection<Effect> = { eventConsumer ->
        object : Connection<Effect> {

            override fun accept(effect: Effect) {
                val dispatch = eventConsumer::accept
                when (effect) {
                    is FetchEvents -> {
                        events.take(1)
                                .subscribe { dispatch(EventsLoaded(it)) }
                    }
                }
            }

            override fun dispose() = Unit // Nothing to clean
        }
    }

}

