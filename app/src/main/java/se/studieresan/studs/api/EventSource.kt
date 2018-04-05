package se.studieresan.studs.api

import android.util.Log
import io.reactivex.Observable
import se.studieresan.studs.models.StudsEvent

class EventSource(val service: BackendService) {
    private var events: List<StudsEvent>? = null

    fun fetchEvents(): Observable<List<StudsEvent>> =
        if (events != null) {
            Observable.just(events)
        } else {
            Observable.create { observer ->
                service.fetchEvents()
                        .subscribe(
                                {
                                    events = it.data.allEvents
                                    observer.onNext(it.data.allEvents)
                                },
                                { throwable ->
                                    Log.d("EventSource", "$throwable")
                                    observer.onError(throwable)
                                })
            }
        }

    fun getEventById(id: String) = fetchEvents()
            .flatMap { Observable.fromIterable(it) }
            .filter  { it.id == id }

}
