package com.example.jasminassociates.ui.auth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.jasminassociates.R
import com.example.jasminassociates.services.AuthService
import com.example.jasminassociates.ui.admin.AdminDashboardActivity
import com.example.jasminassociates.ui.projectmanager.ProjectManagerDashboardActivity
import com.example.jasminassociates.ui.security.SecurityGuardDashboardActivity
import com.example.jasminassociates.viewmodels.LoginViewModel
import com.example.jasminassociates.viewmodels.LoginViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var rememberMeCheckBox: CheckBox
    private lateinit var loginButton: Button
    private lateinit var errorTextView: TextView
    private lateinit var forgotPasswordTextView: TextView
    private lateinit var rememberMeTextView: TextView
    private lateinit var progressBar: ProgressBar

    @Inject
    lateinit var authService: AuthService

    // Secure shared preferences for storing credentials
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize encrypted shared preferences
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        sharedPreferences = EncryptedSharedPreferences.create(
            "secure_prefs",
            masterKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        // Initialize ViewModel with AuthService
        viewModel = ViewModelProvider(this, LoginViewModelFactory(authService, sharedPreferences))
            .get(LoginViewModel::class.java)

        setupViews()
        setupObservers()
        loadRememberedCredentials()
    }

    private fun setupViews() {
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox)
        loginButton = findViewById(R.id.loginButton)
        errorTextView = findViewById(R.id.errorTextView)
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView)
        rememberMeTextView = findViewById(R.id.rememberMeTextView)
        progressBar = findViewById(android.R.id.progress) // Use system progress bar or add one to your layout

        // Set up click listeners
        loginButton.setOnClickListener {
            onLoginClicked()
        }

        forgotPasswordTextView.setOnClickListener {
            onForgotPasswordClicked()
        }

        rememberMeTextView.setOnClickListener {
            onRememberMeClicked()
        }
    }

    private fun setupObservers() {
        viewModel.loginResult.observe(this) { result ->
            result?.let {
                if (it.isSuccess) {
                    val user = it.getOrNull()
                    if (user != null) {
                        onLoginCompleted(user)
                    } else {
                        showError("Login failed: User not found")
                    }
                } else {
                    showError("Login failed: ${it.exceptionOrNull()?.message ?: "Unknown error"}")
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            loginButton.isEnabled = !isLoading
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) {
                loginButton.text = "Logging in..."
            } else {
                loginButton.text = "Login"
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            if (error.isNotEmpty()) {
                showError(error)
            }
        }
    }

    private fun onLoginClicked() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()
        val rememberMe = rememberMeCheckBox.isChecked

        // Basic validation
        if (TextUtils.isEmpty(email)) {
            showError("Please enter your email")
            return
        }

        if (TextUtils.isEmpty(password)) {
            showError("Please enter your password")
            return
        }

        viewModel.login(email, password, rememberMe)
    }

    private fun onLoginCompleted(user: com.example.jasminassociates.models.User) {
        // Store user role in shared preferences
        sharedPreferences.edit().putString("user_role", user.role).apply()

        // Navigate based on user role
        val destinationIntent = when (user.role.lowercase()) {
            "admin" -> Intent(this, AdminDashboardActivity::class.java)
            "projectmanager" -> Intent(this, ProjectManagerDashboardActivity::class.java)
            "securitypersonnel" -> Intent(this, SecurityGuardDashboardActivity::class.java)
            else -> Intent(this, AdminDashboardActivity::class.java) // Default to admin
        }

       // destinationIntent.putExtra("USER_DATA", user)
        startActivity(destinationIntent)
        finish() // Close login activity
    }

    private fun onRememberMeClicked() {
        rememberMeCheckBox.isChecked = !rememberMeCheckBox.isChecked
    }

    private fun onForgotPasswordClicked() {
        Toast.makeText(this, "Please contact system administrator to reset your password.", Toast.LENGTH_LONG).show()
    }

    private fun loadRememberedCredentials() {
        // Load remembered email if exists
        val savedEmail = sharedPreferences.getString("remembered_email", "")
        if (!savedEmail.isNullOrEmpty()) {
            emailEditText.setText(savedEmail)
            rememberMeCheckBox.isChecked = true
        }
    }

    private fun showError(message: String) {
        errorTextView.text = message
        errorTextView.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        // Clear password field for security (but keep email if remember me is checked)
        if (!rememberMeCheckBox.isChecked) {
            passwordEditText.text.clear()
        }
    }
}