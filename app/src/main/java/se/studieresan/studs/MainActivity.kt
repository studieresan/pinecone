package se.studieresan.studs

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import se.studieresan.studs.events.master.loop.EffectHandler


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    val handleEffects by lazy {
        val eventSource = (application as StudsApplication).eventSource
        val effectHandler = EffectHandler(eventSource)
        effectHandler.effectHandler()
    }
}
