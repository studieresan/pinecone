package se.studieresan.studs.events.detail.loop

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.spotify.mobius.Connectable
import com.spotify.mobius.Connection
import com.spotify.mobius.disposables.CompositeDisposable
import com.spotify.mobius.functions.Consumer
import se.studieresan.studs.FIREBASE_DATE_FORMAT
import se.studieresan.studs.api.EventSource
import se.studieresan.studs.api.UserSource
import se.studieresan.studs.models.StudsUser
import java.text.SimpleDateFormat
import java.util.*


class EffectHandler(
        private val eventSource: EventSource,
        private val userSource: UserSource,
        private val reference: CollectionReference,
        private val context: Context
): Connectable<Effect, Event> {

    private var disposables: CompositeDisposable? = null

    override fun connect(output: Consumer<Event>): Connection<Effect> {
        // TODO handle properly
        var isDisposed = false

        fun checkIn(user: StudsUser, checkedInById: String, eventId: String, output: Consumer<Event>) {
            val date = timeFormatter.format(Date())
            val checkin = mapOf<String, String>(
                    "checkedInAt" to date,
                    "checkedInById" to checkedInById,
                    "eventId" to eventId,
                    "userId" to user.id
            )

            output.accept(UserLoading(user.id))
            reference.add(checkin).addOnCompleteListener {
                if (!isDisposed) output.accept(UserLoaded(user.id))
            }
        }

        fun checkOut(userId: String, checkInId: String, output: Consumer<Event>) {
            output.accept(UserLoading(userId))
            reference.document(checkInId).delete()
                    .addOnCompleteListener {
                        if (!isDisposed) output.accept(UserLoaded(userId))
                    }
                    .addOnFailureListener {
                        Log.d("Error!", "error: $it")
                    }
        }

        return object: Connection<Effect> {
            override fun accept(effect: Effect) =
                    when (effect) {
                        is LoadEvent -> loadEvent(output, effect.eventId)
                        is LoadUsers -> loadUsers(output)
                        is CheckIn ->
                            checkIn(effect.user, effect.checkedInBy, effect.eventId, output)
                        is CheckOut ->
                            checkOut(effect.userId, effect.checkInId, output)
                        is CallNumber -> callNumber(effect.telephoneNumber)
                        is GetLocation -> getLocation(effect.address, output)
                    }

            override fun dispose() {
                // TODO, handle properly
                isDisposed = true
            }
        }
    }

    fun getLocation(address: String, output: Consumer<Event>) {
        if (Geocoder.isPresent()) {
            val geocoder = Geocoder(context)
            geocoder.locationFromAddress(address)?.let { location ->
                val position = Position(location.latitude, location.longitude)
                output.accept(LocationLoaded(position))
            }
        } else {
            // TODO, find another way :(
            throw IllegalStateException("Could not find geocoder")
        }
    }


    fun loadUsers(eventConsumer: Consumer<Event>) {
        userSource.fetchUsers()
                .subscribe(
                        { users ->
                            eventConsumer.accept(UsersLoaded(users))
                        },
                        {
                            Log.d("EventDetailEffects", "Did not have permission to fetch all users.")
                            Log.d("EventDetailEffects", "$it")
                        }
                )
    }

    fun loadEvent(eventConsumer: Consumer<Event>, eventId: String) {
        eventSource.getEventById(eventId)
                .take(1)
                .subscribe { event ->
                    eventConsumer.accept(EventLoaded(event))
                }
    }

    val timeFormatter = SimpleDateFormat(FIREBASE_DATE_FORMAT, Locale.ENGLISH)

    fun callNumber(number: String) {
        TODO("Implement")
    }

}

private fun Geocoder.locationFromAddress(address: String): Address? =
        getFromLocationName("Stockholm, $address", 1)
                .firstOrNull()
