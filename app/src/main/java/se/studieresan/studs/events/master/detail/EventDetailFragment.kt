package se.studieresan.studs.events.master.detail

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_event_detail.*
import se.studieresan.studs.R
import se.studieresan.studs.StudsApplication
import se.studieresan.studs.models.StudsEvent
import java.text.SimpleDateFormat
import java.util.*

class EventDetailFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            val eventId = arguments.getString(EVENT_ID_KEY)
            val eventSource = (activity.application as StudsApplication).eventSource

            eventSource.getEventById(eventId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(::render)
        }
//        val db = FirebaseFirestore.getInstance()
//
//        db.collection("checkins")
//                .whereEqualTo("eventId", eventId)
//                .get()
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        for (document in task.result) {
//                            Log.d("TEST", document.id + " => " + document.data)
//                        }
//                    } else {
//                        Log.w("TEST", "Error getting documents.", task.exception)
//                    }
//                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event_detail, container, false)
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

    fun render(event: StudsEvent) {
        if (event.date != null) {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
            val formatter = SimpleDateFormat("MMM dd, YYYY", Locale.ENGLISH)
            val calendar = GregorianCalendar()
            calendar.time = format.parse(event.date)

            event_date.text = formatter.format(calendar.time)
        } else {
            event_date.text = "No date specified."
        }

        val description = event.privateDescription
        if (description != null &&
                description.isNotBlank() &&
                description.isNotEmpty()) {
            event_description.text = event.privateDescription
        }
        event_location.text = event.location ?: "No date specified."
        event_company_name.text = event.companyName
    }

}
