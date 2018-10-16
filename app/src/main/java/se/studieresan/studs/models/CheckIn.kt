package se.studieresan.studs.models

import java.io.Serializable
import java.util.*

data class CheckIn(
        val id: String,
        val userId: String,
        val date: Date,
        val time: String,
        val checkedInById: String
): Serializable
