package com.example.jasminassociates.ui.adapters



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jasminassociates.R
import com.example.jasminassociates.models.SecurityShift
import java.time.format.DateTimeFormatter

class AllShiftsAdapter(
    private var shifts: List<SecurityShift>,
    private val onDeleteClick: (SecurityShift) -> Unit
) : RecyclerView.Adapter<AllShiftsAdapter.ShiftViewHolder>() {

    class ShiftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val personnelNameTextView: TextView = itemView.findViewById(R.id.personnelNameTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShiftViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_all_shift, parent, false)
        return ShiftViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShiftViewHolder, position: Int) {
        val shift = shifts[position]
        holder.personnelNameTextView.text = shift.securityPersonnel?.firstName ?: "Unknown"
        holder.dateTextView.text = shift.shiftDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        holder.timeTextView.text = "${shift.scheduledStartTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${shift.scheduledEndTime.format(DateTimeFormatter.ofPattern("HH:mm"))}"
        holder.statusTextView.text = shift.status

        holder.deleteButton.setOnClickListener {
            onDeleteClick(shift)
        }
    }

    override fun getItemCount(): Int = shifts.size

    fun updateShifts(newShifts: List<SecurityShift>) {
        shifts = newShifts
        notifyDataSetChanged()
    }
}