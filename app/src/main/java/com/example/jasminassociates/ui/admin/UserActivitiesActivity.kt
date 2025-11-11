package com.example.jasminassociates.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jasminassociates.R
import com.example.jasminassociates.ui.adapters.ActivitiesAdapter
import com.example.jasminassociates.viewmodels.admin.UserActivitiesViewModel


class UserActivitiesActivity : AppCompatActivity() {

    private lateinit var viewModel: UserActivitiesViewModel
    private lateinit var activitiesButton: Button
    private lateinit var usersButton: Button
    private lateinit var userTableScrollView: ScrollView
    private lateinit var activitiesLayout: LinearLayout
    private lateinit var userSpinner: Spinner
    private lateinit var selectedUserLayout: LinearLayout
    private lateinit var activitiesRecyclerView: RecyclerView
    private lateinit var refreshButton: Button
    private lateinit var reportButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var activitiesAdapter: ActivitiesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_activities)

        // Initialize ViewModel with ViewModelProvider
        viewModel = ViewModelProvider(this)[UserActivitiesViewModel::class.java]

        setupViews()
        setupRecyclerViews()
        setupObservers()
        loadData()
    }

    private fun setupViews() {
        activitiesButton = findViewById(R.id.activitiesButton)
        usersButton = findViewById(R.id.usersButton)
        userTableScrollView = findViewById(R.id.userTableScrollView)
        activitiesLayout = findViewById(R.id.activitiesLayout)
        userSpinner = findViewById(R.id.userSpinner)
        selectedUserLayout = findViewById(R.id.selectedUserLayout)
        activitiesRecyclerView = findViewById(R.id.activitiesRecyclerView)
        refreshButton = findViewById(R.id.refreshButton)
        reportButton = findViewById(R.id.reportButton)
        progressBar = findViewById(R.id.progressBar)

        activitiesButton.setOnClickListener { onShowActivitiesClicked() }
        usersButton.setOnClickListener { onShowUsersClicked() }
        refreshButton.setOnClickListener { onRefreshClicked() }
        reportButton.setOnClickListener { onGenerateReportClicked() }

        findViewById<Button>(R.id.menuButton)?.setOnClickListener {
            onMenuClicked()
        }

        findViewById<Button>(R.id.dashboardButton)?.setOnClickListener {
            onDashboardClicked()
        }

        userSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedUser = parent?.getItemAtPosition(position)
                if (selectedUser is UserActivitiesViewModel.UserDisplay) {
                    viewModel.setSelectedUser(selectedUser)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupRecyclerViews() {
        activitiesAdapter = ActivitiesAdapter(emptyList())
        activitiesRecyclerView.layoutManager = LinearLayoutManager(this)
        activitiesRecyclerView.adapter = activitiesAdapter
    }

    private fun setupObservers() {
        viewModel.showUserTable.observe(this) { showTable ->
            userTableScrollView.visibility = if (showTable) ScrollView.VISIBLE else ScrollView.GONE
            activitiesLayout.visibility = if (showTable) LinearLayout.GONE else LinearLayout.VISIBLE

            // Update button colors
            updateButtonColors(showTable)
        }

        viewModel.users.observe(this) { users ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, users)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            userSpinner.adapter = adapter
        }

        viewModel.allUsers.observe(this) { users ->
            // For table view, we'll populate the table layout
            populateUserTable(users)
        }

        viewModel.selectedUserDetails.observe(this) { user ->
            selectedUserLayout.visibility = if (user != null) LinearLayout.VISIBLE else LinearLayout.GONE
            if (user != null) {
                updateSelectedUserDetails(user)
            }
        }

        viewModel.activities.observe(this) { activities ->
            activitiesAdapter.updateActivities(activities ?: emptyList())
        }

        viewModel.isLoading.observe(this) { loading ->
            progressBar.visibility = if (loading) ProgressBar.VISIBLE else ProgressBar.GONE
        }
    }

    private fun loadData() {
        viewModel.loadData()
    }

    private fun onShowActivitiesClicked() {
        viewModel.setShowUserTable(false)
    }

    private fun onShowUsersClicked() {
        viewModel.setShowUserTable(true)
    }

    private fun onRefreshClicked() {
        if (viewModel.showUserTable.value == true) {
            viewModel.refreshUsers()
            Toast.makeText(this, "User data refreshed successfully", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.loadData()
            Toast.makeText(this, "Activities data refreshed successfully", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onGenerateReportClicked() {
        // Simple report generation - in a real app, this would create a PDF or CSV
        val activities = viewModel.activities.value
        if (activities != null && activities.isNotEmpty()) {
            val reportSummary = "Activities Report\nTotal Activities: ${activities.size}\nGenerated: ${java.util.Date()}"
            Toast.makeText(this, reportSummary, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "No activities to generate report", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onMenuClicked() {
        Toast.makeText(this, "Menu clicked", Toast.LENGTH_SHORT).show()
    }

    private fun onDashboardClicked() {
        startActivity(Intent(this, AdminDashboardActivity::class.java))
        finish()
    }

    private fun updateButtonColors(showTable: Boolean) {
        val activeColor = "#007ACC"
        val inactiveColor = "#6c757d"

        usersButton.setBackgroundColor(android.graphics.Color.parseColor(if (showTable) activeColor else inactiveColor))
        activitiesButton.setBackgroundColor(android.graphics.Color.parseColor(if (!showTable) activeColor else inactiveColor))
    }

    private fun populateUserTable(users: List<com.example.jasminassociates.models.User>) {
        // Find or create the table layout
        val userTableLayout = findViewById<TableLayout>(R.id.userTableLayout)
        userTableLayout?.removeAllViews()

        if (users.isEmpty()) return

        // Add header row
        val headerRow = TableRow(this)
        arrayOf("ID", "Name", "Role", "Email", "Status").forEach { header ->
            val textView = TextView(this).apply {
                text = header
                setTextColor(android.graphics.Color.WHITE)
                setBackgroundColor(android.graphics.Color.DKGRAY)
                setPadding(16, 16, 16, 16)
                textSize = 14f
            }
            headerRow.addView(textView)
        }
        userTableLayout?.addView(headerRow)

        // Add user rows
        users.forEach { user ->
            val userRow = TableRow(this)
            arrayOf(
                user.userID.toString(),
                "${user.firstName} ${user.lastName}",
                user.role,
                user.email ?: "N/A",
                if (user.isActive) "Active" else "Inactive"
            ).forEach { data ->
                val textView = TextView(this).apply {
                    text = data
                    setPadding(16, 16, 16, 16)
                    textSize = 12f
                }
                userRow.addView(textView)
            }
            userTableLayout?.addView(userRow)
        }
    }

    private fun updateSelectedUserDetails(user: com.example.jasminassociates.models.User) {
        // Create or update user details views
        val userNameTextView = findViewById<TextView?>(R.id.selectedUserNameTextView)
            ?: TextView(this).apply { id = R.id.selectedUserNameTextView }
        val userRoleTextView = findViewById<TextView?>(R.id.selectedUserRoleTextView)
            ?: TextView(this).apply { id = R.id.selectedUserRoleTextView }
        val userEmailTextView = findViewById<TextView?>(R.id.selectedUserEmailTextView)
            ?: TextView(this).apply { id = R.id.selectedUserEmailTextView }
        val userStatusTextView = findViewById<TextView?>(R.id.selectedUserStatusTextView)
            ?: TextView(this).apply { id = R.id.selectedUserStatusTextView }

        userNameTextView.text = "Name: ${user.firstName} ${user.lastName}"
        userRoleTextView.text = "Role: ${user.role}"
        userEmailTextView.text = "Email: ${user.email ?: "N/A"}"
        userStatusTextView.text = "Status: ${if (user.isActive) "Active" else "Inactive"}"

        // Add views to layout if they don't exist
        if (findViewById<TextView?>(R.id.selectedUserNameTextView) == null) {
            selectedUserLayout.addView(userNameTextView)
            selectedUserLayout.addView(userRoleTextView)
            selectedUserLayout.addView(userEmailTextView)
            selectedUserLayout.addView(userStatusTextView)
        }
    }
}