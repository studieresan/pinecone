package se.studieresan.studs.api

import android.content.Context
import android.preference.PreferenceManager
import okhttp3.Interceptor
import okhttp3.Response

val COOKIES = "cookies"
class AddCookiesInterceptor(val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain
                .request()
                .newBuilder()
        val cookies = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getStringSet(COOKIES, mutableSetOf())
        cookies.forEach { cookie ->
            builder.addHeader("Cookie", cookie)
        }
        builder.addHeader("Content-Type", "application/graphql")

        return chain.proceed(builder.build())
    }

}
