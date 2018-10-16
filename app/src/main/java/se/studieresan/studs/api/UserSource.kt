package se.studieresan.studs.api

import android.content.SharedPreferences
import io.reactivex.Single
import se.studieresan.studs.EMAIL
import se.studieresan.studs.models.Permissions
import se.studieresan.studs.models.StudsUser

interface UserSource {

    fun fetchUsers(): Single<List<StudsUser>>

    fun getLoggedInUser(users: List<StudsUser>): StudsUser?

}

class UserSourceImpl(
        private val backendService: BackendService,
        private val preferences: SharedPreferences
): UserSource {

    var users: List<StudsUser>? = null

    override fun fetchUsers(): Single<List<StudsUser>> =
            if (users != null) {
                Single.just(users)
            } else {
                backendService
                        .fetchUser()
                        .map { it.data.user }
                        .flatMap { user ->
                            val hasPermission = user.permissions
                                    .any { it == Permissions.EventPermission.asString }
                            if (hasPermission) {
                                backendService.fetchUsers()
                                        .map {
                                            users = it.data.studsUsers
                                            it.data.studsUsers
                                        }
                            } else {
                                Single.error(Throwable("Failed to fetch users, missing permission"))
                            }
                        }
            }

    override fun getLoggedInUser(users: List<StudsUser>): StudsUser? {
        val email = preferences.getString(EMAIL, "none")
        return users.find { it.profile.email == email }
    }
}
