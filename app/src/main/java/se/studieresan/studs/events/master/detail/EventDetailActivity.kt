package se.studieresan.studs.events.master.detail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import se.studieresan.studs.R

class EventDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        val eventId = intent.getStringExtra(EventDetailFragment.EVENT_ID_KEY)

        supportFragmentManager
                .beginTransaction()
                .add(
                        R.id.fragment_holder,
                        EventDetailFragment.newInstance(eventId = eventId)
                )
                .commit()
    }

}
