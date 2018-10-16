package se.studieresan.studs.events.master.loop

import android.content.SharedPreferences
import com.spotify.mobius.Connection
import com.spotify.mobius.functions.Consumer
import io.reactivex.Observable
import se.studieresan.studs.COOKIES
import se.studieresan.studs.LOGGED_IN
import se.studieresan.studs.api.EventSource
import se.studieresan.studs.models.StudsEvent

val TAG = "EventsEffectHandler"

class EffectHandler(eventSource: EventSource, val sharedPreferences: SharedPreferences) {

    val events: Observable<List<StudsEvent>> by lazy {
        eventSource.fetchEvents().cache()
    }

    fun effectHandler(): (eventConsumer: Consumer<Event>) -> Connection<Effect> = {
        eventConsumer ->
        object : Connection<Effect> {

            override fun accept(effect: Effect) {
                when (effect) {
                    is FetchEvents -> fetchEvents(eventConsumer)
                    is TriggerLogout -> logout(eventConsumer, sharedPreferences)
                }
            }

            override fun dispose() = Unit // Nothing to clean
        }
    }

    fun fetchEvents(eventConsumer: Consumer<Event>) =
        events.take(1)
                .subscribe { eventConsumer.accept(EventsLoaded(it)) }

    fun logout(eventConsumer: Consumer<Event>, sharedPreferences: SharedPreferences) {
        sharedPreferences
                .edit()
                .putBoolean(LOGGED_IN, false)
                .commit()
        sharedPreferences
                .edit()
                .remove(COOKIES)
                .commit()
        eventConsumer.accept(LoggedOut)
    }

}

