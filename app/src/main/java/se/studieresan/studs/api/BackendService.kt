package se.studieresan.studs.api

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import se.studieresan.studs.models.StudsEvent

interface BackendService {
    @POST("login")
    fun login(@Body user: User): Completable

    @GET("graphql?query=$eventQuery")
    fun fetchEvents(): Single<Events>
}

data class User(val email: String, val password: String)

data class Events(val data: AllEvents)
data class AllEvents(val allEvents: List<StudsEvent>)

const val eventQuery =
    """query {
        allEvents {
            id
            companyName
            schedule
            privateDescription
            publicDescription
            date
            beforeSurveys
            afterSurveys
            location
            pictures
            published
        }
    }"""
