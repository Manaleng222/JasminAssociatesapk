package com.example.jasminassociates.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jasminassociates.R
import com.example.jasminassociates.viewmodels.admin.UserActivitiesViewModel
import java.time.format.DateTimeFormatter

class ActivitiesAdapter(private var activities: List<UserActivitiesViewModel.ActivityDisplay>) :
    RecyclerView.Adapter<ActivitiesAdapter.ActivityViewHolder>() {

    class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val activityTypeTextView: TextView = itemView.findViewById(R.id.activityTypeTextView)
        val userNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val timestampTextView: TextView = itemView.findViewById(R.id.timestampTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_activity, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = activities[position]
        holder.activityTypeTextView.text = activity.activityType
        holder.userNameTextView.text = activity.userName
        holder.descriptionTextView.text = activity.description
        holder.timestampTextView.text = activity.timestamp.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
        holder.statusTextView.text = activity.status
        holder.statusTextView.setTextColor(Color.parseColor(activity.statusColor))
    }

    override fun getItemCount(): Int = activities.size

    fun updateActivities(newActivities: List<UserActivitiesViewModel.ActivityDisplay>) {
        activities = newActivities
        notifyDataSetChanged()
    }
}