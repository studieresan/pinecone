package se.studieresan.studs.events.master.detail

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import se.studieresan.studs.R



class EventDetailFragment : Fragment() {

    private var eventId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            eventId = arguments.getString(EVENT_ID_KEY)
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

    override fun onStop() {
        super.onStop()
        Log.d("MAIN", "Hello Stop")
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
}
