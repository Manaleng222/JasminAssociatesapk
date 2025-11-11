package com.example.jasminassociates.ui.projectmanager

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jasminassociates.R
import com.example.jasminassociates.ui.adapters.TasksAdapter
import com.example.jasminassociates.data.repository.TaskRepository
import com.example.jasminassociates.models.ProjectTask
import com.example.jasminassociates.services.ProjectTaskService
import com.example.jasminassociates.viewmodels.ProjectTasksViewModel
import javax.inject.Inject


class ProjectTasksActivity : AppCompatActivity() {

    @Inject
    lateinit var taskRepository: TaskRepository

    private lateinit var viewModel: ProjectTasksViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TasksAdapter
    private lateinit var projectNameText: TextView
    private lateinit var summaryText: TextView
    private var projectId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_tasks)

        projectId = intent.getIntExtra("projectId", 0)

        // Create service with injected repository
        val taskService = ProjectTaskService(taskRepository)
        viewModel = ViewModelProvider(this, ProjectTasksViewModelFactory(taskService, projectId))[ProjectTasksViewModel::class.java]

        setupViews()
        setupRecyclerView()
        observeViewModel()
        loadTasks()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerView)
        projectNameText = findViewById(R.id.projectName)
        summaryText = findViewById(R.id.summaryText)

        findViewById<Button>(R.id.dashboardButton).setOnClickListener {
            onDashboardClicked()
        }

        findViewById<Button>(R.id.addTaskButton).setOnClickListener {
            onAddTaskClicked()
        }
    }

    private fun setupRecyclerView() {
        adapter = TasksAdapter(
            onEditClick = { task: ProjectTask ->
                onEditTaskClicked(task)
            },
            onUpdateStatusClick = { task: ProjectTask ->
                onUpdateStatusClicked(task)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.tasks.observe(this) { tasks ->
            adapter.submitList(tasks)
        }

        viewModel.projectName.observe(this) { name ->
            projectNameText.text = name
        }

        viewModel.taskSummary.observe(this) { summary ->
            summaryText.text = summary
        }
    }

    private fun loadTasks() {
        viewModel.loadTasks()
    }

    private fun onAddTaskClicked() {
        val intent = Intent(this, AddEditTaskActivity::class.java).apply {
            putExtra("projectId", projectId)
        }
        startActivity(intent)
    }

    private fun onEditTaskClicked(task: ProjectTask) {
        val intent = Intent(this, AddEditTaskActivity::class.java).apply {
            putExtra("taskId", task.taskID)
            putExtra("projectId", projectId)
        }
        startActivity(intent)
    }

    private fun onUpdateStatusClicked(task: ProjectTask) {
        showStatusUpdateDialog(task)
    }

    private fun showStatusUpdateDialog(task: ProjectTask) {
        val statuses = arrayOf("NotStarted", "InProgress", "Completed", "OnHold")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Update Task Status")
            .setItems(statuses) { _, which -> // Fixed: Remove unused 'dialog' parameter
                val newStatus = statuses[which]
                viewModel.updateTaskStatus(task.taskID, newStatus)
            }
            .show()
    }

    private fun onDashboardClicked() {
        val intent = Intent(this, ProjectManagerDashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
    // ViewModel Factory for proper ViewModel creation
    class ProjectTasksViewModelFactory(
        private val taskService: ProjectTaskService,
        private val projectId: Int
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProjectTasksViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ProjectTasksViewModel(taskService, projectId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

