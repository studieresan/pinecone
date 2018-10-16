package se.studieresan.studs.events.detail

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import se.studieresan.studs.R
import se.studieresan.studs.models.CheckIn
import se.studieresan.studs.models.StudsUser
import se.studieresan.studs.show

class EventDetailAdapter(
        private val listener: OnItemPressedListener
): RecyclerView.Adapter<EventDetailAdapter.ViewHolder>() {

    private var users: List<StudsUser> = emptyList()
    private var checkedInUsers: List<CheckIn> = emptyList()
    private var usersLoading: Map<String, Boolean> = emptyMap()
    private var loadingCheckins: Boolean = true

    fun setUsers(
            newUsers: List<StudsUser>,
            newCheckins: List<CheckIn>,
            newLoading: Map<String, Boolean>,
            showAll: Boolean,
            loadingCheckins: Boolean
    ) {
        if (users.isNotEmpty()) {
            val old = users
            val sorted = newUsers.sortedBy { it.profile.firstName }
            val new =
                    if (showAll) sorted
                    else sorted
                            .filter { user -> newCheckins.find { it.userId == user.id } == null }

            val diff = DiffUtil.calculateDiff(object: DiffUtil.Callback() {
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                    old[oldItemPosition].id == new[newItemPosition].id

                override fun getOldListSize(): Int = old.size

                override fun getNewListSize(): Int = new.size

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldUser = old[oldItemPosition]
                    val newUser = new[newItemPosition]
                    val oldCheckIn = checkedInUsers.find { it.userId == oldUser.id }
                    val newCheckIn = newCheckins.find { it.userId == newUser.id }

                    return oldUser == newUser &&
                            (usersLoading.getOrElse(oldUser.id, { false }) ==
                            newLoading.getOrElse(newUser.id, { false })) &&
                            newCheckIn == oldCheckIn
                }
            })
            checkedInUsers = newCheckins
            usersLoading = newLoading
            users = new
            diff.dispatchUpdatesTo(this)
        } else {
            checkedInUsers = newCheckins
            usersLoading = newLoading
            users = newUsers.sortedBy { it.profile.firstName }
            notifyDataSetChanged()
        }
        if (this.loadingCheckins && !loadingCheckins) {
            notifyDataSetChanged()
            this.loadingCheckins = loadingCheckins
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater
                .from(parent!!.context)
                .inflate(R.layout.list_item_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.name.text = "${user.profile.firstName} ${user.profile.lastName}"

        holder.callButton.setOnClickListener {
            listener.callUser(user.profile.phone)
        }

        val isLoading = usersLoading.getOrElse(user.id, { false })
        if (isLoading || loadingCheckins) {
            holder.progress.show(true)
            holder.checkInButton.show(false)
        } else {
            holder.checkInButton.show(true)
            holder.progress.show(false)
            val checkIn = checkedInUsers.find { it.userId == user.id }
            if (checkIn != null) {
                val checkedInBy = users.find { checkIn.checkedInById == it.id }?.profile
                holder.checkInButton.text = "${checkedInBy?.firstName} ${checkedInBy?.lastName?.firstOrNull()} @ ${checkIn?.time}"
                holder.checkInButton.setOnClickListener {
                    listener.checkOutUser(user.id, checkIn)
                    holder.checkInButton.setOnClickListener(null)
                }
            } else {
                holder.checkInButton.text = "Check in"
                holder.checkInButton.setOnClickListener {
                    listener.checkInUser(user)
                    holder.checkInButton.setOnClickListener(null)
                }
            }
        }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name_textview)
        val checkInButton: Button = view.findViewById(R.id.checkin_button)
        val callButton: View = view.findViewById(R.id.call_button)
        val progress: View = view.findViewById(R.id.progress)
    }

    interface OnItemPressedListener {
        fun checkInUser(user: StudsUser)
        fun checkOutUser(userId: String, checkIn: CheckIn)
        fun callUser(telephoneNumer: String)
    }

}
