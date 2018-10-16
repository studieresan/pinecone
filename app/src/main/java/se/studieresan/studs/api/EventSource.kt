package se.studieresan.studs.api

import io.reactivex.Observable
import se.studieresan.studs.models.StudsEvent

interface EventSource {

    fun fetchEvents(): Observable<List<StudsEvent>>

    fun getEventById(id: String): Observable<StudsEvent>

}

class EventSourceImpl(private val service: BackendService): EventSource {
    private var events: List<StudsEvent>? = null

    override fun fetchEvents(): Observable<List<StudsEvent>> =
        if (events != null) {
            Observable.just(events)
        } else {
            service.fetchEvents()
                    .map {
                        events = it.data.allEvents
                        it.data.allEvents
                    }
                    .toObservable()
        }

    override fun getEventById(id: String) = fetchEvents()
            .flatMap { Observable.fromIterable(it) }
            .filter  { it.id == id }

}
