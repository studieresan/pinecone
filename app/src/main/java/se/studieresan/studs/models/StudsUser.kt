package se.studieresan.studs.models

import java.io.Serializable

enum class Permissions(val asString: String) {
    EventPermission("events_permission")
}

data class StudsUser(
        val id: String = "",
        val profile: Profile = Profile(),
        val permissions: List<String> = emptyList()
): Serializable

data class Profile(
        val email: String = "",
        val firstName: String = "",
        val lastName: String = "",
        val phone: String = "",
        val picture: String = ""
): Serializable
