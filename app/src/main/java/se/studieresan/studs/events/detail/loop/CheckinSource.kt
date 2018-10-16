package se.studieresan.studs.events.detail.loop

import com.google.firebase.firestore.CollectionReference
import com.spotify.mobius.EventSource
import com.spotify.mobius.disposables.Disposable
import com.spotify.mobius.functions.Consumer
import se.studieresan.studs.DATE_FORMAT
import se.studieresan.studs.FIREBASE_DATE_FORMAT
import se.studieresan.studs.models.CheckIn
import java.text.SimpleDateFormat
import java.util.*

class CheckinSource(
        val eventId: String,
        val collectionReference: CollectionReference
): EventSource<Event> {

    val format = SimpleDateFormat(FIREBASE_DATE_FORMAT, Locale.ENGLISH)
    val format2 = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.ENGLISH)
    override fun subscribe(eventConsumer: Consumer<Event>?): Disposable {

        eventConsumer?.accept(CheckinLoading)
        val listener = collectionReference
                .whereEqualTo("eventId", eventId)
                .addSnapshotListener { snapshot, exception ->
                    val users = snapshot
                            .map {
                                val date = try {
                                    format.parse(it["checkedInAt"] as String)
                                } catch (_: Exception) {
                                    format2.parse(it["checkedInAt"] as String)
                                }
                                CheckIn(
                                        id = it.id,
                                        userId = it["userId"] as String,
                                        checkedInById = it["checkedInById"] as String,
                                        date = date,
                                        time = timeFormatter.format(date)
                                )
                            }

                    eventConsumer?.accept(CheckinsLoaded)
                    eventConsumer?.accept(UsersCheckedIn(users))
                }

        // The snapshot listener will not trigger if noone is actually checked in.
        // This listener will check if any data exists at all.
        collectionReference
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener {
//                    eventConsumer?.accept(CheckinsLoaded)
                }


        return Disposable {
            listener.remove()
        }
    }
}
