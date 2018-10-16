package se.studieresan.studs.events.detail.loop

import com.spotify.mobius.Next
import se.studieresan.studs.next

fun update(model: EventDetailModel, event: Event): Next<EventDetailModel, Effect> =
        when (event) {
            is EventLoaded -> next(model = model.copy(event = event.event))

            is UsersCheckedIn ->
                next(model = model.copy(checkInUsers = event.users))

            is MapLoaded -> next(model = model.copy(map = event.map))

            is UsersLoaded -> next(model = model.copy(users = event.users))

            is TriggerCheckIn ->
                next(effects = setOf(CheckIn(event.user, event.checkedInBy, event.eventId)))

            is TriggerCheckOut -> next(effects = setOf(CheckOut(event.userId, event.checkInId)))

            is TriggerCall -> next(effects = setOf(CallNumber(event.telephoneNumber)))

            is UserLoading ->
                next(model = model.copy(loadingUsers = model.loadingUsers + (event.userId to true)))

            is UserLoaded ->
                next(model = model.copy(loadingUsers = model.loadingUsers + (event.userId to false)))

            ToggleUserListType ->
                next(model = model.copy(showingAllUsers = !model.showingAllUsers))

            is TriggerGetLocation -> next(effects = setOf(GetLocation(event.address)))

            is LocationLoaded -> next(model = model.copy(location = event.location))

            is CheckinLoading -> next(model = model.copy(loadingCheckins = true))

            is CheckinsLoaded -> next(model = model.copy(loadingCheckins = false))
        }
