package se.studieresan.studs

import android.app.Application
import android.preference.PreferenceManager
import com.google.firebase.FirebaseApp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import se.studieresan.studs.api.*


class StudsApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }

    private val retrofit by lazy {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(AddCookiesInterceptor(this))
                .addInterceptor(ReceivedCookiesInterceptor(this))
                .build()

        Retrofit.Builder()
                .baseUrl("https://studs-overlord.appspot.com")
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    val eventSource: EventSource by lazy {
        EventSourceImpl(backendService)
    }

    val userSource: UserSource by lazy {
        UserSourceImpl(backendService, PreferenceManager.getDefaultSharedPreferences(this))
    }

    val backendService by lazy {
        retrofit.create(BackendService::class.java)
    }

}
