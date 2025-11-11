package com.example.jasminassociates.ui.adapters



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jasminassociates.R
import com.example.jasminassociates.models.SecurityShift
import java.time.format.DateTimeFormatter

class LiveShiftsAdapter(private var shifts: List<SecurityShift>) : RecyclerView.Adapter<LiveShiftsAdapter.ShiftViewHolder>() {

    class ShiftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val personnelNameTextView: TextView = itemView.findViewById(R.id.personnelNameTextView)
        val projectTextView: TextView = itemView.findViewById(R.id.projectTextView)
        val clockInTimeTextView: TextView = itemView.findViewById(R.id.clockInTimeTextView)
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShiftViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_live_shift, parent, false)
        return ShiftViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShiftViewHolder, position: Int) {
        val shift = shifts[position]
        holder.personnelNameTextView.text = shift.securityPersonnel?.firstName ?: "Unknown"
        holder.projectTextView.text = "Project ID: ${shift.securityPersonnelID}"
        holder.clockInTimeTextView.text = shift.actualClockIn?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "Not clocked in"
        holder.locationTextView.text = shift.location ?: "Main Site"
    }

    override fun getItemCount(): Int = shifts.size

    fun updateShifts(newShifts: List<SecurityShift>) {
        shifts = newShifts
        notifyDataSetChanged()
    }
}