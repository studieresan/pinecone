package se.studieresan.studs.models

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
)

