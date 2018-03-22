package se.studieresan.studs.events.master

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import se.studieresan.studs.R
import se.studieresan.studs.models.StudsEvent
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

val PreviousEventTitle = 0
val PreviousEvent = 1
val FutureEvent = 2
val FutureEventTitle = 3

class EventAdapter: RecyclerView.Adapter<ViewHolder>() {

    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
    val monthFormatter = SimpleDateFormat("MMM", Locale.ENGLISH)
    val calendar = GregorianCalendar()
    var events: List<StudsEvent> by Delegates.observable(listOf()) { _, _, _ ->
        notifyDataSetChanged()
        val today = Date()
        val (past, future) = events.partition { format.parse(it.date) < today }
        futureEvents = future
        previousEvents = past
    }
    var previousEvents: List<StudsEvent> = listOf()
    var futureEvents: List<StudsEvent> = listOf()
    var eventSelectedListener: OnEventSelected? = null

    override fun getItemViewType(position: Int): Int =
        when (position) {
            0 -> FutureEventTitle
            in 1..futureEvents.size -> FutureEvent
            futureEvents.size + 1 -> PreviousEventTitle
            else -> PreviousEvent
        }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        fun inflate(view: Int): View =
            LayoutInflater
                    .from(parent!!.context)
                    .inflate(view, parent, false)
        return if (viewType == PreviousEventTitle || viewType == FutureEventTitle) {
            TitleViewHolder(inflate(R.layout.list_item_event_header))
        } else {
            EventViewHolder(inflate(R.layout.list_item_event))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val type = getItemViewType(position)
        when (holder) {
            is TitleViewHolder -> onBindTitle(holder, type)
            is EventViewHolder -> onBindEvent(holder, position, type)
        }
    }

    private fun onBindEvent(holder: EventViewHolder, position: Int, type: Int) {
        val event = if (type == FutureEvent) {
            futureEvents[position - 1]
        } else {
            previousEvents[position - 2 - futureEvents.size]
        }

        holder.name.text = event.companyName
        holder.location.text = event.location

        val date = format.parse(event.date)
        calendar.time = date

        holder.dateNumber.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
        holder.dateMonth.text = monthFormatter.format(calendar.time)

        holder.view.setOnClickListener {
            eventSelectedListener?.onEventSelected(event)
        }
    }

    private fun onBindTitle(holder: TitleViewHolder, type: Int) {
        val title = if (type == PreviousEventTitle) "Previous Events" else "Upcoming Events"
        holder.textView.text = title
    }


    override fun getItemCount() =
            if (events.isNotEmpty()) {
                events.size + 2
            } else {
                0
            }

    class EventViewHolder(view: View): ViewHolder(view) {
        val view: View = view.findViewById(R.id.card)
        val name: TextView = view.findViewById(R.id.company_name_textview)
        val location: TextView = view.findViewById(R.id.event_location_textview)
        val dateNumber : TextView = view.findViewById(R.id.event_date_textview)
        val dateMonth: TextView = view.findViewById(R.id.event_month_textview)
    }

    class TitleViewHolder(view: View): ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.header_textview)
    }

    interface OnEventSelected {
        fun onEventSelected(event: StudsEvent)
    }
}

