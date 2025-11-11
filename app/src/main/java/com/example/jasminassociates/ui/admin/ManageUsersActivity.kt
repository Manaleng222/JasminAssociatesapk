package com.example.jasminassociates.ui.admin



import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jasminassociates.R
import com.example.jasminassociates.models.User
import com.example.jasminassociates.ui.adapters.UsersAdapter
import com.example.jasminassociates.viewmodels.admin.ManageUsersViewModel

class ManageUsersActivity : AppCompatActivity() {

    private lateinit var viewModel: ManageUsersViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UsersAdapter
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_users)

       // val userService = ServiceLocator.getUserService()
        //viewModel = ManageUsersViewModel(userService)
        setupViews()
        setupRecyclerView()
        observeViewModel()
        loadUsers()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)

        findViewById<Button>(R.id.dashboardButton).setOnClickListener {
            onDashboardClicked()
        }

        findViewById<Button>(R.id.addUserButton).setOnClickListener {
            onAddUserClicked()
        }

        findViewById<Button>(R.id.menuButton).setOnClickListener {
            onMenuClicked()
        }
    }

    private fun setupRecyclerView() {
        adapter = UsersAdapter(
            onEditClick = { user ->
                onEditUserClicked(user)
            },
            onDeleteClick = { user ->
                onDeleteUserClicked(user)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.users.observe(this) { users ->
            adapter.submitList(users)
        }

        viewModel.isLoading.observe(this) { loading ->
            progressBar.visibility = if (loading) ProgressBar.VISIBLE else ProgressBar.GONE
        }
    }

    private fun loadUsers() {
        viewModel.loadUsers()
    }

    private fun onDashboardClicked() {
        startActivity(Intent(this, AdminDashboardActivity::class.java))
        finish()
    }

    private fun onAddUserClicked() {
        startActivity(Intent(this, AddEditUserActivity::class.java))
    }

    private fun onEditUserClicked(user: User) {
        val intent = Intent(this, AddEditUserActivity::class.java).apply {
            putExtra("userId", user.userID)
        }
        startActivity(intent)
    }

    private fun onDeleteUserClicked(user: User) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete ${user.firstName} ${user.lastName}?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteUser(user.userID)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun onMenuClicked() {
        Toast.makeText(this, "Menu clicked", Toast.LENGTH_SHORT).show()
    }
}