package com.example.jasminassociates.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jasminassociates.R
import com.example.jasminassociates.models.SecurityShift
import java.time.format.DateTimeFormatter

class TodaysShiftsAdapter : ListAdapter<SecurityShift, TodaysShiftsAdapter.ShiftViewHolder>(ShiftDiffCallback()) {

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShiftViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todays_shift, parent, false)
        return ShiftViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShiftViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ShiftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timeRange: TextView = itemView.findViewById(R.id.timeRange)
        private val location: TextView = itemView.findViewById(R.id.location)
        private val status: TextView = itemView.findViewById(R.id.status)

        fun bind(shift: SecurityShift) {
            // Format LocalTime to String for display
            val startTime = shift.scheduledStartTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            val endTime = shift.scheduledEndTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            timeRange.text = "$startTime - $endTime"

            location.text = shift.location ?: "Main Site"

            status.text = shift.status
            // Use a default color for now - you can map status to colors as needed
            val colorRes = when (shift.status) {
                "Completed" -> R.color.green
                "ClockedIn" -> R.color.blue
                "Scheduled" -> R.color.orange
                else -> android.R.color.darker_gray
            }
            status.setTextColor(ContextCompat.getColor(itemView.context, colorRes))
        }
    }

    class ShiftDiffCallback : DiffUtil.ItemCallback<SecurityShift>() {
        override fun areItemsTheSame(oldItem: SecurityShift, newItem: SecurityShift): Boolean {
            return oldItem.shiftID == newItem.shiftID
        }

        override fun areContentsTheSame(oldItem: SecurityShift, newItem: SecurityShift): Boolean {
            return oldItem == newItem
        }
    }
}