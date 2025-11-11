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
import com.jasminassociates.models.Project
import java.text.SimpleDateFormat
import java.util.*

class ProjectsAdapter(
    private val onEditClick: (Project) -> Unit,
    private val onViewTasksClick: (Project) -> Unit
) : ListAdapter<Project, ProjectsAdapter.ProjectViewHolder>(ProjectDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_project, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val projectName: TextView = itemView.findViewById(R.id.projectName)
        private val clientName: TextView = itemView.findViewById(R.id.clientName)
        private val status: TextView = itemView.findViewById(R.id.status)
        private val startDate: TextView = itemView.findViewById(R.id.startDate)
        private val editButton: Button = itemView.findViewById(R.id.editButton)
        private val tasksButton: Button = itemView.findViewById(R.id.tasksButton)

        fun bind(project: Project) {
            projectName.text = project.projectName
            clientName.text = project.client?.firstName ?: "No Client"
            status.text = project.status
            startDate.text = "Start: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(project.startDate)}"

            editButton.setOnClickListener { onEditClick(project) }
            tasksButton.setOnClickListener { onViewTasksClick(project) }
        }
    }

    class ProjectDiffCallback : DiffUtil.ItemCallback<Project>() {
        override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean {
            return oldItem.projectID == newItem.projectID
        }

        override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean {
            return oldItem == newItem
        }
    }
}
