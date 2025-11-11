package com.example.jasminassociates.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jasminassociates.R
import com.example.jasminassociates.databinding.ItemEquipmentRequestBinding
import com.example.jasminassociates.models.EquipmentRequest

class EquipmentRequestsAdapter(
    private val onItemClick: (EquipmentRequest) -> Unit
) : ListAdapter<EquipmentRequest, EquipmentRequestsAdapter.RequestViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val binding = ItemEquipmentRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RequestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = getItem(position)
        holder.bind(request)
    }

    inner class RequestViewHolder(private val binding: ItemEquipmentRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(request: EquipmentRequest) {
            binding.tvEquipmentType.text = request.equipmentType
            binding.tvProjectName.text = request.project?.projectName ?: "No Project"
            binding.tvRequestDate.text = itemView.context.getString(R.string.requested_date_format, request.requestDate.toLocalDate())
            binding.tvStatus.text = request.status

            // Set status color using ColorInt extension
            val statusColor = when (request.status) {
                "Pending" -> android.graphics.Color.parseColor("#FFC107") // Amber
                "Approved" -> android.graphics.Color.parseColor("#28A745") // Green
                "Fulfilled" -> android.graphics.Color.parseColor("#17A2B8") // Blue
                "Rejected" -> android.graphics.Color.parseColor("#DC3545") // Red
                else -> android.graphics.Color.GRAY
            }
            binding.tvStatus.setTextColor(statusColor)

            binding.root.setOnClickListener {
                onItemClick(request)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<EquipmentRequest>() {
        override fun areItemsTheSame(oldItem: EquipmentRequest, newItem: EquipmentRequest): Boolean {
            return oldItem.requestID == newItem.requestID
        }

        override fun areContentsTheSame(oldItem: EquipmentRequest, newItem: EquipmentRequest): Boolean {
            return oldItem == newItem
        }
    }
}