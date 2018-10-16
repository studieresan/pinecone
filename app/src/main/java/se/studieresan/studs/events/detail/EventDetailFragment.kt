package se.studieresan.studs.events.detail

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_event_detail.*
import kotlinx.android.synthetic.main.fragment_event_detail.view.*
import se.studieresan.studs.R
import se.studieresan.studs.events.detail.loop.EventDetailModel
import se.studieresan.studs.show
import java.text.SimpleDateFormat
import java.util.*

class EventDetailFragment : Fragment() {

    val adapter by lazy {
        EventDetailAdapter(activity as MapsActivity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_event_detail, container, false)
        v.user_recyclerview.adapter = adapter
        v.user_recyclerview.layoutManager = object: LinearLayoutManager(context) {
            override fun canScrollVertically() = false
        }
        v.user_recyclerview.setHasFixedSize(false)
        v.list_toggle.setOnCheckedChangeListener { _, _ ->
            (activity as MapsActivity).toggleUserListType()
        }

        return v
    }

    companion object {
        public val EVENT_ID_KEY = "event_id_key"

        fun newInstance(eventId: String): EventDetailFragment {
            val fragment = EventDetailFragment()
            val args = Bundle()
            args.putString(EVENT_ID_KEY, eventId)
            fragment.arguments = args
            return fragment
        }
    }

    var modelCache: EventDetailModel? = null
    fun render(model: EventDetailModel) {
        if (model == modelCache) return
        modelCache = model

        val event = model.event ?: return

        if (event.date != null) {
            val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
            val calendar = Calendar.getInstance()
            calendar.time = event.eventStart()

            event_date.text = formatter.format(calendar.time)
        } else {
            event_date.text = "No date specified."
        }

        val description = event.privateDescription
        if (description != null &&
                description.isNotBlank() &&
                description.isNotEmpty()) {
            event_description.text = event.privateDescription
        } else {
            event_description.text = "No Description"
        }

        event_location.text = event.location ?: "No date specified."
        event_company_name.text = event.companyName

        if (model.users.isNotEmpty()) {
            checkin_status_group.show(true)
            adapter.setUsers(
                    model.users,
                    model.checkInUsers,
                    model.loadingUsers,
                    model.showingAllUsers,
                    model.loadingCheckins
            )
        }
    }

}
