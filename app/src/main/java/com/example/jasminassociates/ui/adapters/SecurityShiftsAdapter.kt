package com.example.jasminassociates.ui.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jasminassociates.R
import com.jasminassociates.models.ShiftDisplay

class SecurityShiftsAdapter(
    private val onEditClick: (ShiftDisplay) -> Unit,
    private val onClockInOutClick: (ShiftDisplay) -> Unit
) : ListAdapter<ShiftDisplay, SecurityShiftsAdapter.ShiftViewHolder>(ShiftDisplayDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShiftViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_security_shift, parent, false)
        return ShiftViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShiftViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ShiftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val securityPersonnelName: TextView = itemView.findViewById(R.id.securityPersonnelName)
        private val projectName: TextView = itemView.findViewById(R.id.projectName)
        private val shiftDetails: TextView = itemView.findViewById(R.id.shiftDetails)
        private val paymentDetails: TextView = itemView.findViewById(R.id.paymentDetails)
        private val editButton: Button = itemView.findViewById(R.id.editButton)
        private val clockInOutButton: Button = itemView.findViewById(R.id.clockInOutButton)

        fun bind(shift: ShiftDisplay) {
            securityPersonnelName.text = shift.securityPersonnelName
            projectName.text = shift.projectName
            shiftDetails.text = shift.shiftDetails
            paymentDetails.text = shift.paymentDetails

            editButton.setOnClickListener { onEditClick(shift) }
            clockInOutButton.setOnClickListener { onClockInOutClick(shift) }
        }
    }
    class ShiftDisplayDiffCallback : DiffUtil.ItemCallback<ShiftDisplay>() {
        override fun areItemsTheSame(oldItem: ShiftDisplay, newItem: ShiftDisplay): Boolean {
            return oldItem.shiftId == newItem.shiftId
        }

        override fun areContentsTheSame(oldItem: ShiftDisplay, newItem: ShiftDisplay): Boolean {
            return oldItem == newItem
        }
    }
}

