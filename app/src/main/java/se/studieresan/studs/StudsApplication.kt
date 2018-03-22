package se.studieresan.studs

import android.app.Application
import com.google.firebase.FirebaseApp
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import se.studieresan.studs.api.AddCookiesInterceptor
import se.studieresan.studs.api.BackendService
import se.studieresan.studs.api.EventSource
import se.studieresan.studs.api.ReceivedCookiesInterceptor

class StudsApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }

    private val retrofit by lazy {
        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AddCookiesInterceptor(this))
                .addInterceptor(ReceivedCookiesInterceptor(this))
                .build()

        Retrofit.Builder()
                .baseUrl("https://studs18-overlord.herokuapp.com/")
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    val eventSource by lazy {
        EventSource(backendService)
    }

    val backendService by lazy {
        retrofit.create(BackendService::class.java)
    }

}
