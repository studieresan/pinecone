package se.studieresan.studs.events.master

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import se.studieresan.studs.R
import se.studieresan.studs.isOnSameDayAs
import se.studieresan.studs.models.StudsEvent
import se.studieresan.studs.show
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

val PreviousEventTitle = 0
val PreviousEvent = 1
val FutureEvent = 2
val FutureEventTitle = 3

class EventAdapter: RecyclerView.Adapter<ViewHolder>() {

    val monthFormatter = SimpleDateFormat("MMM", Locale.ENGLISH)
    val calendar = Calendar.getInstance()
    var events: List<StudsEvent> by Delegates.observable(listOf()) { _, _, _ ->
        val today = Date()
        val todaysCalendar = Calendar.getInstance()
        todaysCalendar.time = today
        val (past, future) = events.partition {
            val eventTime = it.eventStart()
            calendar.time = eventTime

            eventTime < today && !calendar.isOnSameDayAs(todaysCalendar)
        }
        futureEvents = future.sortedBy { it.eventStart() }
        previousEvents = past.sortedBy { it.eventStart() }.reversed()
        notifyDataSetChanged()
    }
    var previousEvents: List<StudsEvent> = listOf()
    var futureEvents: List<StudsEvent> = listOf()
    var listInteractionListener: OnListInteraction? = null

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
            val holder = TitleViewHolder(inflate(R.layout.list_item_event_header))
            holder.logoutButton.setOnClickListener{
                listInteractionListener?.onLogout()
            }
            holder
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

        calendar.time = event.eventStart()

        holder.dateNumber.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
        holder.dateMonth.text = monthFormatter.format(calendar.time)

        holder.view.setOnClickListener {
            listInteractionListener?.onEventSelected(event)
        }
    }

    private fun onBindTitle(holder: TitleViewHolder, type: Int) {
        val title = if (type == PreviousEventTitle) "Previous Events" else "Upcoming Events"
        holder.logoutButton.show(type == FutureEventTitle)
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
        val logoutButton: TextView = view.findViewById(R.id.logout_button)
    }

    interface OnListInteraction {
        fun onEventSelected(event: StudsEvent)
        fun onLogout()
    }
}
