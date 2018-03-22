package se.studieresan.studs.api

import io.reactivex.Observable
import se.studieresan.studs.models.StudsEvent

class EventSource(val service: BackendService) {
    private var events: List<StudsEvent>? = null

    fun fetchEvents(): Observable<List<StudsEvent>> {
        if (events != null) {
            return Observable.just(events)
        } else {
            return Observable.create { observer ->
                service.fetchEvents()
                        .subscribe(
                                {
                                    events = it.data.allEvents
                                    observer.onNext(it.data.allEvents)
                                },
                                { throwable ->
                                    observer.onError(throwable)
                                })
            }
        }
    }

}
