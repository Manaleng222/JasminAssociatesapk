package com.example.jasminassociates

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.jasminassociates.models.User
import com.example.jasminassociates.services.AuthService
import com.example.jasminassociates.ui.auth.LoginActivity
import com.example.jasminassociates.ui.admin.AdminDashboardActivity
import com.example.jasminassociates.ui.projectmanager.ManageProjectsActivity
import com.example.jasminassociates.ui.security.SecurityShiftsActivity
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Check if user is already logged in
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus() {
        // For now, we'll always show login screen
        // In a real app, you would check SharedPreferences or secure storage for login status
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    // This method can be called from LoginActivity after successful authentication
    fun navigateToRoleBasedDashboard(user: User) {
        val intent = when (user.role) {
            "Admin" -> Intent(this, AdminDashboardActivity::class.java)
            "ProjectManager" -> Intent(this, ManageProjectsActivity::class.java)
            "SecurityPersonnel" -> Intent(this, SecurityShiftsActivity::class.java)
            else -> Intent(this, LoginActivity::class.java) // Fallback to login
        }

        intent.putExtra("USER_DATA", user)
        startActivity(intent)
        finish()
    }
}