package com.example.jasminassociates.ui.projectmanager


import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jasminassociates.R
import com.example.jasminassociates.ui.adapters.RecentProjectsAdapter
import com.example.jasminassociates.viewmodels.project.ProjectManagerDashboardViewModel



class ProjectManagerDashboardActivity : AppCompatActivity() {

    private lateinit var viewModel: ProjectManagerDashboardViewModel
    private lateinit var welcomeMessage: TextView
    private lateinit var myProjectsCount: TextView
    private lateinit var activeTasksCount: TextView
    private lateinit var teamMembersCount: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecentProjectsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_manager_dashboard)

        //viewModel = ProjectManagerDashboardViewModel()
        setupViews()
        setupRecyclerView()
        observeViewModel()
        loadDashboardData()
    }

    private fun setupViews() {
        welcomeMessage = findViewById(R.id.welcomeMessage)
        myProjectsCount = findViewById(R.id.myProjectsCount)
        activeTasksCount = findViewById(R.id.activeTasksCount)
        teamMembersCount = findViewById(R.id.teamMembersCount)
        recyclerView = findViewById(R.id.recyclerView)

        findViewById<Button>(R.id.myProjectsButton).setOnClickListener {
            onMyProjectsClicked()
        }

        findViewById<Button>(R.id.manageTasksButton).setOnClickListener {
            onManageTasksClicked()
        }

        findViewById<Button>(R.id.equipmentRequestButton).setOnClickListener {
            onEquipmentRequestClicked()
        }

        findViewById<Button>(R.id.createEquipmentRequestButton).setOnClickListener {
            onCreateEquipmentRequestClicked()
        }
    }

    private fun setupRecyclerView() {
        adapter = RecentProjectsAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.welcomeMessage.observe(this) { message ->
            welcomeMessage.text = message
        }

        viewModel.myProjectsCount.observe(this) { count ->
            myProjectsCount.text = count.toString()
        }

        viewModel.activeTasksCount.observe(this) { count ->
            activeTasksCount.text = count.toString()
        }

        viewModel.teamMembersCount.observe(this) { count ->
            teamMembersCount.text = count.toString()
        }

        viewModel.recentProjects.observe(this) { projects ->
            adapter.submitList(projects)
        }
    }

    private fun loadDashboardData() {
        viewModel.loadDashboardData()
    }

    private fun onMyProjectsClicked() {
        val intent = Intent(this, ManageProjectsActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onManageTasksClicked() {
        val intent = Intent(this, ProjectTasksActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onEquipmentRequestClicked() {
        val intent = Intent(this, EquipmentRequestsActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onCreateEquipmentRequestClicked() {
        val intent = Intent(this, CreateEquipmentRequestActivity::class.java)
        startActivity(intent)
        finish()
    }
}