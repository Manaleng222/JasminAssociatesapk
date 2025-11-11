package com.example.jasminassociates.ui.projectmanager

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jasminassociates.R
import com.example.jasminassociates.data.repository.ProjectRepository
import com.example.jasminassociates.ui.adapters.ProjectsAdapter
import com.example.jasminassociates.ui.admin.AddEditProjectActivity
import com.example.jasminassociates.viewmodels.ManageProjectsViewModel
import com.jasminassociates.models.Project

import javax.inject.Inject


class ManageProjectsActivity : AppCompatActivity() {

    @Inject
    lateinit var projectRepository: ProjectRepository

    private lateinit var viewModel: ManageProjectsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProjectsAdapter
    private lateinit var statusFilterSpinner: Spinner
    private lateinit var summaryText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_projects)

        // Initialize ViewModel with ProjectRepository
        viewModel = ViewModelProvider(this, ManageProjectsViewModelFactory(projectRepository))[ManageProjectsViewModel::class.java]

        setupViews()
        setupRecyclerView()
        observeViewModel()
        loadProjects()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerView)
        statusFilterSpinner = findViewById(R.id.statusFilterSpinner)
        summaryText = findViewById(R.id.summaryText)

        findViewById<Button>(R.id.dashboardButton).setOnClickListener {
            onDashboardClicked()
        }

        findViewById<Button>(R.id.addProjectButton).setOnClickListener {
            onAddProjectClicked()
        }

        // Setup spinner - use the same filters as defined in ViewModel
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, viewModel.statusFilters)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusFilterSpinner.adapter = spinnerAdapter

        statusFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                onStatusFilterChanged()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupRecyclerView() {
        adapter = ProjectsAdapter(
            onEditClick = { project ->
                onEditProjectClicked(project)
            },
            onViewTasksClick = { project ->
                onViewTasksClicked(project)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.projects.observe(this) { projects ->
            adapter.submitList(projects)
        }

        viewModel.projectCount.observe(this) { count ->
            summaryText.text = "Total Projects: ${count ?: 0}"
        }
    }

    private fun loadProjects() {
        viewModel.loadProjects()
    }

    private fun onAddProjectClicked() {
        val intent = Intent(this, AddEditProjectActivity::class.java)
        startActivity(intent)
    }

    private fun onEditProjectClicked(project: Project) {
        val intent = Intent(this, AddEditProjectActivity::class.java).apply {
            putExtra("projectId", project.projectID)
        }
        startActivity(intent)
    }

    private fun onViewTasksClicked(project: Project) {
        val intent = Intent(this, ProjectTasksActivity::class.java).apply {
            putExtra("projectId", project.projectID)
        }
        startActivity(intent)
    }

    private fun onStatusFilterChanged() {
        val selectedStatus = statusFilterSpinner.selectedItem as String
        viewModel.filterProjects(selectedStatus)
    }

    private fun onDashboardClicked() {
        val intent = Intent(this, ProjectManagerDashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
    // ViewModel Factory for proper ViewModel creation
    class ManageProjectsViewModelFactory(
        private val projectRepository: ProjectRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ManageProjectsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ManageProjectsViewModel(projectRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

