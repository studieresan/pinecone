package se.studieresan.studs.api

import android.content.Context
import android.preference.PreferenceManager
import okhttp3.Interceptor
import okhttp3.Response


class ReceivedCookiesInterceptor(val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            val cookies = mutableSetOf<String>()

            originalResponse.headers("Set-Cookie").forEach {
                cookies.add(it)
            }

            PreferenceManager
                    .getDefaultSharedPreferences(context)
                    .edit()
                    .putStringSet(COOKIES, cookies)
                    .commit()
        }

        return originalResponse
    }

}
