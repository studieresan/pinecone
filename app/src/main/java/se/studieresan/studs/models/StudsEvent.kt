package se.studieresan.studs.models

data class StudsEvent(
        val id: String = "",
        val companyName: String  = "",
        val privateDescription: String  = "",
        val publicDescription: String  = "",
        val date: String = "",
        val beforeSurveys: List<String> = listOf(),
        val afterSurveys: List<String> = listOf(),
        val location: String = "",
        val pictures: List<String> = listOf()
)

