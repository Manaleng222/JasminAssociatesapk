package com.example.jasminassociates.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jasminassociates.R
import com.example.jasminassociates.ui.adapters.RecentUsersAdapter
import com.example.jasminassociates.ui.projectmanager.ManageProjectsActivity
import com.example.jasminassociates.viewmodels.admin.AdminDashboardViewModel
import com.jasminassociates.models.Project

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var viewModel: AdminDashboardViewModel

    // TextViews for displaying stats
    private lateinit var totalUsersTextView: TextView
    private lateinit var activeProjectsTextView: TextView
    private lateinit var securityStaffTextView: TextView
    private lateinit var constructionWorkersTextView: TextView
    private lateinit var overdueTasksTextView: TextView
    private lateinit var equipmentMaintenanceTextView: TextView

    private lateinit var refreshButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        // Initialize ViewModel with ViewModelProvider
        viewModel = ViewModelProvider(this).get(AdminDashboardViewModel::class.java)

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        // Initialize TextViews
        totalUsersTextView = findViewById(R.id.totalUsersTextView)
        activeProjectsTextView = findViewById(R.id.activeProjectsTextView)
        securityStaffTextView = findViewById(R.id.securityStaffTextView)
        constructionWorkersTextView = findViewById(R.id.constructionWorkersTextView)
        overdueTasksTextView = findViewById(R.id.overdueTasksTextView)
        equipmentMaintenanceTextView = findViewById(R.id.equipmentMaintenanceTextView)

        refreshButton = findViewById(R.id.refreshButton)
        progressBar = findViewById(R.id.progressBar)

        // Setup RecyclerView for recent users
        val recentUsersRecyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val recentUsersAdapter = RecentUsersAdapter(emptyList())
        recentUsersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@AdminDashboardActivity)
            adapter = recentUsersAdapter
        }

        // Setup button click listeners
        refreshButton.setOnClickListener {
            viewModel.refresh()
        }

        findViewById<Button>(R.id.manageUsersButton).setOnClickListener {
            startActivity(Intent(this, ManageUsersActivity::class.java))
        }

        findViewById<Button>(R.id.manageProjectsButton).setOnClickListener {
            startActivity(Intent(this, ManageProjectsActivity::class.java))
        }

        // Setup other buttons
        findViewById<Button>(R.id.viewActivitiesButton)?.setOnClickListener {
            Toast.makeText(this, "View Activities feature coming soon", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.manageEquipmentButton)?.setOnClickListener {
            Toast.makeText(this, "Manage Equipment feature coming soon", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.securityShiftsButton)?.setOnClickListener {
            Toast.makeText(this, "Security Shifts feature coming soon", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.viewReportsButton)?.setOnClickListener {
            Toast.makeText(this, "Reports feature coming soon", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.menuButton)?.setOnClickListener {
            Toast.makeText(this, "Menu feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        viewModel.dashboardStats.observe(this) { stats ->
            stats?.let {
                totalUsersTextView.text = it.totalUsers.toString()
                activeProjectsTextView.text = it.activeProjects.toString()
                securityStaffTextView.text = it.totalSecurityPersonnel.toString()
                constructionWorkersTextView.text = it.totalConstructionWorkers.toString()
                overdueTasksTextView.text = it.overdueTasks.toString()
                equipmentMaintenanceTextView.text = it.equipmentDueForMaintenance.toString()
            }
        }

        viewModel.recentProjects.observe(this) { projects ->
            projects?.let {
                updateRecentProjectsView(it)
            }
        }

        viewModel.recentUsers.observe(this) { users ->
            users?.let {
                val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
                (recyclerView.adapter as? RecentUsersAdapter)?.updateUsers(it)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) ProgressBar.VISIBLE else ProgressBar.GONE
            refreshButton.isEnabled = !isLoading
        }
    }

    private fun updateRecentProjectsView(projects: List<Project>) {
        val projectsContainer = findViewById<LinearLayout?>(R.id.recentProjectsContainer)
        projectsContainer?.removeAllViews()

        projects.forEach { project ->
            val projectView = layoutInflater.inflate(R.layout.item_recent_project, projectsContainer, false)

            // Fix: Use the correct IDs from your XML layout
            val projectNameTextView = projectView.findViewById<TextView>(R.id.projectName)
            val projectStatusTextView = projectView.findViewById<TextView>(R.id.projectStatus)
            val projectProgressTextView = projectView.findViewById<TextView>(R.id.projectProgress)
            val tasksCountTextView = projectView.findViewById<TextView>(R.id.tasksCount)

            projectNameTextView.text = project.projectName ?: "Unnamed Project"
            projectStatusTextView.text = "Status: ${project.status ?: "Unknown"}"

            // Set default values for progress and tasks count
            projectProgressTextView.text = "Progress: 0%"
            tasksCountTextView.text = "Tasks: 0"

            projectsContainer?.addView(projectView)
        }
    }
}