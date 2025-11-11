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
import com.example.jasminassociates.ui.adapters.RecentProjectsAdapter.*
import com.example.jasminassociates.viewmodels.project.ProjectManagerDashboardViewModel


class RecentProjectsAdapter : ListAdapter<ProjectManagerDashboardViewModel.ProjectDisplay, ProjectViewHolder>(ProjectDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_project, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = getItem(position)
        holder.bind(project)
    }

    class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val projectName: TextView = itemView.findViewById(R.id.projectName)
        private val projectStatus: TextView = itemView.findViewById(R.id.projectStatus)
        private val projectProgress: TextView = itemView.findViewById(R.id.projectProgress)
        private val tasksCount: TextView = itemView.findViewById(R.id.tasksCount)

        fun bind(project: ProjectManagerDashboardViewModel.ProjectDisplay) {
            projectName.text = project.projectName
            projectStatus.text = project.status
            projectProgress.text = project.progressText
            tasksCount.text = project.tasksText

            // Set status color
            try {
                val color = android.graphics.Color.parseColor(project.statusColor)
                projectStatus.setTextColor(color)
            } catch (e: Exception) {
                // Use default color if parsing fails
                projectStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
            }
        }
    }

    class ProjectDiffCallback : DiffUtil.ItemCallback<ProjectManagerDashboardViewModel.ProjectDisplay>() {
        override fun areItemsTheSame(oldItem: ProjectManagerDashboardViewModel.ProjectDisplay, newItem: ProjectManagerDashboardViewModel.ProjectDisplay): Boolean {
            return oldItem.projectId == newItem.projectId
        }

        override fun areContentsTheSame(oldItem: ProjectManagerDashboardViewModel.ProjectDisplay, newItem: ProjectManagerDashboardViewModel.ProjectDisplay): Boolean {
            return oldItem == newItem
        }
    }
}