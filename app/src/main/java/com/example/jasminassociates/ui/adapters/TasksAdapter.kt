package com.example.jasminassociates.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jasminassociates.R
import com.example.jasminassociates.models.ProjectTask

class TasksAdapter(
    private val onEditClick: (ProjectTask) -> Unit,
    private val onUpdateStatusClick: (ProjectTask) -> Unit
) : ListAdapter<ProjectTask, TasksAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskName: TextView = itemView.findViewById(R.id.taskName)
        private val taskDescription: TextView = itemView.findViewById(R.id.taskDescription)
        private val taskAssignee: TextView = itemView.findViewById(R.id.taskAssignee)
        private val taskDueDate: TextView = itemView.findViewById(R.id.taskDueDate)
        private val taskStatus: TextView = itemView.findViewById(R.id.taskStatus)
        private val editButton: Button = itemView.findViewById(R.id.editButton)
        private val updateStatusButton: Button = itemView.findViewById(R.id.updateStatusButton)

        fun bind(task: ProjectTask) {
            taskName.text = task.taskName ?: "Unnamed Task"
            taskDescription.text = task.description ?: "No description"
            taskAssignee.text = "Assignee: ${task.assignedTo ?: "Unassigned"}"
            taskDueDate.text = "Due: ${task.dueDate?.toString() ?: "No due date"}"
            taskStatus.text = task.status

            // Set status color
            val statusColor = when (task.status) {
                "Completed" -> R.color.green
                "InProgress" -> R.color.orange
                "Pending" -> R.color.blue
                "Overdue" -> R.color.red
                else -> R.color.black
            }
            taskStatus.setTextColor(ContextCompat.getColor(itemView.context, statusColor))

            editButton.setOnClickListener {
                onEditClick(task)
            }

            updateStatusButton.setOnClickListener {
                onUpdateStatusClick(task)
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<ProjectTask>() {
        override fun areItemsTheSame(oldItem: ProjectTask, newItem: ProjectTask): Boolean {
            return oldItem.taskID== newItem.taskID
        }

        override fun areContentsTheSame(oldItem: ProjectTask, newItem: ProjectTask): Boolean {
            return oldItem == newItem
        }
    }
}