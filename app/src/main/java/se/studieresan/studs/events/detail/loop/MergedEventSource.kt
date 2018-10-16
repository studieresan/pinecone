package se.studieresan.studs.events.detail.loop

import com.spotify.mobius.EventSource
import com.spotify.mobius.disposables.CompositeDisposable
import com.spotify.mobius.disposables.Disposable
import com.spotify.mobius.functions.Consumer

class MergedEventSource(
        vararg val sources: EventSource<Event>
): EventSource<Event> {

    override fun subscribe(eventConsumer: Consumer<Event>?): Disposable {
        val disposables = sources
                .map { it.subscribe(eventConsumer) }
                .toTypedArray()
        return CompositeDisposable.from(*disposables)
    }

}
