package se.studieresan.studs.events.master

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.spotify.mobius.Connection
import com.spotify.mobius.First
import com.spotify.mobius.Mobius
import com.spotify.mobius.android.AndroidLogger
import com.spotify.mobius.android.MobiusAndroid
import com.spotify.mobius.functions.Consumer
import kotlinx.android.synthetic.main.fragment_event.*
import kotlinx.android.synthetic.main.fragment_event.view.*
import se.studieresan.studs.MainActivity
import se.studieresan.studs.R
import se.studieresan.studs.events.master.detail.EventDetailFragment
import se.studieresan.studs.events.master.detail.MapsActivity
import se.studieresan.studs.events.master.loop.Event
import se.studieresan.studs.events.master.loop.EventsModel
import se.studieresan.studs.events.master.loop.FetchEvents
import se.studieresan.studs.events.master.loop.update
import se.studieresan.studs.models.StudsEvent
import se.studieresan.studs.show

class EventListFragment : Fragment(), EventAdapter.OnEventSelected {

    private val loopFactory by lazy {
        Mobius.loop(::update, (activity as MainActivity).handleEffects)
                .logger(AndroidLogger.tag("EventLoop"))
                .init { First.first(EventsModel(), setOf(FetchEvents)) }
    }
    private val controller by lazy {
        MobiusAndroid.controller(loopFactory, EventsModel())
    }
    private val adapter = EventAdapter()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_event, container, false)

        controller.connect(::connectViews)

        view.recyclerview.adapter = adapter
        view.recyclerview.layoutManager = object: LinearLayoutManager(context) {
            override fun canScrollVertically() = false
        }

        controller.start()
        return view
    }

    private fun connectViews(eventConsumer: Consumer<Event>): Connection<EventsModel> {
        return object : Connection<EventsModel> {
            override fun accept(model: EventsModel) = render(model)

            override fun dispose() {
                adapter.eventSelectedListener = null
            }
        }
    }


    private fun render(model: EventsModel) {
        progress.show(model.loading)
        if (adapter.events != model.events) adapter.events = model.events
    }

    override fun onStart() {
        super.onStart()
        adapter.eventSelectedListener = this
    }

    override fun onDestroyView() {
        super.onDestroyView()
        controller.stop()
        controller.disconnect()
    }

    override fun onEventSelected(event: StudsEvent) {
        val intent = Intent(context, MapsActivity::class.java)
        intent.putExtra(EventDetailFragment.EVENT_ID_KEY, event.id)
//        startActivity(intent)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
    }
}

