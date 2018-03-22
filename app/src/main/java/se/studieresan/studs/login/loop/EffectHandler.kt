package se.studieresan.studs.login.loop

import android.content.ContentValues.TAG
import android.util.Log
import com.spotify.mobius.Connection
import com.spotify.mobius.functions.Consumer
import io.reactivex.disposables.CompositeDisposable
import se.studieresan.studs.api.BackendService
import se.studieresan.studs.api.User


val TAG = "LoginEffectHandler"
val ERROR = "Login Failed. Check your username/password or try again later."

fun effectHandler(backendService: BackendService): (Consumer<Event>) -> Connection<Effect>  = {
    eventConsumer ->

    object : Connection<Effect> {

        val disposables: CompositeDisposable = CompositeDisposable()

        override fun accept(effect: Effect) {
            val dispatch = eventConsumer::accept
            when (effect) {
                is RemoteLogin -> {
                    val disposable = backendService
                            .login(User(effect.name, effect.password))
                            .subscribe(
                                    {
                                        dispatch(LoggedIn(Success))
                                    },
                                    { throwable ->
                                        Log.e(TAG, throwable.message)
                                        dispatch(LoggedIn(Error(ERROR)))
                                    })
                    disposables.add(disposable)
                }

            }
        }

        override fun dispose() {
            disposables.clear()
        }
    }
}
