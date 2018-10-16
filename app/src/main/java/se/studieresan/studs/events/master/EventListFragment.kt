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
import se.studieresan.studs.events.detail.EventDetailFragment
import se.studieresan.studs.events.detail.MapsActivity
import se.studieresan.studs.events.master.loop.*
import se.studieresan.studs.login.LoginActivity
import se.studieresan.studs.models.StudsEvent
import se.studieresan.studs.show

class EventListFragment : Fragment(), EventAdapter.OnListInteraction {

    private val loopFactory by lazy {
        Mobius.loop(::update, (activity as MainActivity).handleEffects)
                .logger(AndroidLogger.tag("EventLoop"))
                .init { First.first(EventsModel(), setOf(FetchEvents)) }
    }
    private val controller by lazy {
        MobiusAndroid.controller(loopFactory, EventsModel())
    }
    private val adapter = EventAdapter()
    private var dispatch: ((Event) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_event, container, false)

        controller.connect(::connectViews)

        view.recyclerview.adapter = adapter
        view.recyclerview.layoutManager = LinearLayoutManager(context)
        view.recyclerview.setHasFixedSize(true)

        controller.start()
        return view
    }

    private fun connectViews(eventConsumer: Consumer<Event>): Connection<EventsModel> {
        dispatch = eventConsumer::accept
        return object : Connection<EventsModel> {
            override fun accept(model: EventsModel) = render(model)

            override fun dispose() {
                adapter.listInteractionListener = null
            }
        }
    }

    private fun render(model: EventsModel) {
        progress.show(model.loading)
        if (adapter.events != model.events) adapter.events = model.events
        if (model.loggedOut) logOut()
    }

    override fun onStart() {
        super.onStart()
        adapter.listInteractionListener = this
    }

    override fun onResume() {
        super.onResume()
        render(controller.model)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        controller.stop()
        controller.disconnect()
    }

    override fun onEventSelected(event: StudsEvent) {
        val intent = Intent(context, MapsActivity::class.java)
        intent.putExtra(EventDetailFragment.EVENT_ID_KEY, event.id)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
    }

    override fun onLogout() {
        dispatch?.invoke(Logout)
    }

    fun logOut() {
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        activity.finish()
    }
}

