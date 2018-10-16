package se.studieresan.studs.api

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import se.studieresan.studs.models.StudsEvent
import se.studieresan.studs.models.StudsUser

interface BackendService {
    @POST("login")
    fun login(@Body user: User): Completable

    @GET("graphql?query=$eventQuery")
    fun fetchEvents(): Single<Events>

    @GET("graphql?query=$userQuery")
    fun fetchUser(): Single<StudsUserWrapper>

    @GET("graphql?query=$allUsersQuery")
    fun fetchUsers(): Single<Users>
}


data class Events(val data: AllEvents)
data class AllEvents(val allEvents: List<StudsEvent>)

data class User(val email: String, val password: String)
data class StudsUserWrapper(val data: StudsInnerUserWrapper)
data class StudsInnerUserWrapper(val user: StudsUser)
data class Users(val data: AllUsers)
data class AllUsers(val studsUsers: List<StudsUser>)

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

const val userFields =
        """
          email
          firstName
          lastName
          phone
          picture
        """

const val userQuery =
        """query {
             user {
               id
               profile {
                 $userFields
               }
               permissions
             }
           }"""

const val allUsersQuery =
        """
        query {
          studsUsers: users(memberType: studs_member) {
            id
            profile { ${userFields} }
          }
        }
        """
