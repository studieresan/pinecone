package se.studieresan.studs.models

import se.studieresan.studs.DATE_FORMAT
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

data class StudsEvent(
        val id: String,
        val companyName: String  = "",
        val privateDescription: String? = null,
        val publicDescription: String?  = null,
        val date: String? = null,
        val beforeSurveys: List<String> = listOf(),
        val afterSurveys: List<String> = listOf(),
        val location: String? = null,
        val pictures: List<String> = listOf()
): Serializable {

    companion object {
        val format = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)
    }

    // GSON parsing causing problems, required this work-around
    private var parsedDate: Date? = null
    fun eventStart(): Date {
        if (parsedDate == null) parsedDate = format.parse(date)
        return parsedDate!!
    }

}
