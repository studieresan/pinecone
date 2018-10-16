package se.studieresan.studs.events.detail.loop

import com.google.android.gms.maps.GoogleMap
import se.studieresan.studs.models.CheckIn
import se.studieresan.studs.models.StudsEvent
import se.studieresan.studs.models.StudsUser
import java.io.Serializable

data class EventDetailModel(
        val checkinOpen: Boolean = false,
        val event: StudsEvent? = null,
        val map: GoogleMap? = null,
        val checkInUsers: List<CheckIn> = (emptyList()),
        val users: List<StudsUser> = emptyList(),
        val loadingUsers: Map<String, Boolean> = emptyMap(),
        val showingAllUsers: Boolean = true,
        val location: Position = Position(59.3293, 18.0686),
        val loadingCheckins: Boolean = false
): Serializable

data class Position(
        val latitude: Double,
        val longitude: Double
): Serializable // google's LatLng is not serializable

sealed class Event
object ToggleUserListType: Event()
data class EventLoaded(val event: StudsEvent): Event()
object CheckinLoading: Event()
object CheckinsLoaded: Event()
data class MapLoaded(val map: GoogleMap): Event()
data class UsersCheckedIn(val users: List<CheckIn>): Event()
data class UsersLoaded(val users: List<StudsUser>): Event()
data class TriggerCheckIn(val user: StudsUser, val checkedInBy: String, val eventId: String): Event()
data class TriggerCheckOut(val userId: String, val checkInId: String): Event()
data class TriggerCall(val telephoneNumber: String): Event()
data class TriggerGetLocation(val address: String): Event()
data class LocationLoaded(val location: Position): Event()
data class UserLoading(val userId: String): Event()
data class UserLoaded(val userId: String): Event()

sealed class Effect
data class LoadEvent(val eventId: String): Effect()
object LoadUsers: Effect()
data class CheckIn(val user: StudsUser, val checkedInBy: String, val eventId: String): Effect()
data class CheckOut(val userId: String, val checkInId: String): Effect()
data class CallNumber(val telephoneNumber: String): Effect()
data class GetLocation(val address: String): Effect()
