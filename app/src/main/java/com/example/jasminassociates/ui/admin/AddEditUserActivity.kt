package com.example.jasminassociates.ui.admin



import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.jasminassociates.R
import com.example.jasminassociates.models.User
import com.jasminassociates.viewmodels.admin.AddEditUserViewModel

class AddEditUserActivity : AppCompatActivity() {

    private lateinit var viewModel: AddEditUserViewModel
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var roleSpinner: Spinner
    private lateinit var hourlyRateEditText: EditText
    private lateinit var generatedEmailTextView: TextView
    private lateinit var generatedPasswordTextView: TextView
    private lateinit var copyEmailButton: Button
    private lateinit var regeneratePasswordButton: Button
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var dashboardButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_user)

        viewModel = ViewModelProvider(this).get(AddEditUserViewModel::class.java)

        val userId = intent.getIntExtra("userId", 0)
        if (userId > 0) {
            viewModel.initializeWithUserFromId(userId)
        }

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        firstNameEditText = findViewById(R.id.firstNameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        roleSpinner = findViewById(R.id.roleSpinner)
        hourlyRateEditText = findViewById(R.id.hourlyRateEditText)
        generatedEmailTextView = findViewById(R.id.generatedEmailTextView)
        generatedPasswordTextView = findViewById(R.id.generatedPasswordTextView)
        copyEmailButton = findViewById(R.id.copyEmailButton)
        regeneratePasswordButton = findViewById(R.id.regeneratePasswordButton)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)
        dashboardButton = findViewById(R.id.dashboardButton)

        // Setup role spinner
        val roles = viewModel.roles.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = adapter

        roleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedRole = parent?.getItemAtPosition(position) as? String
                selectedRole?.let { role ->
                    viewModel.updateRole(role)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        copyEmailButton.setOnClickListener { onCopyEmailClicked() }
        regeneratePasswordButton.setOnClickListener { onRegeneratePasswordClicked() }
        saveButton.setOnClickListener { onSaveClicked() }
        cancelButton.setOnClickListener { onCancelClicked() }
        dashboardButton.setOnClickListener { onDashboardClicked() }

        findViewById<Button>(R.id.backButton).setOnClickListener { finish() }
    }

    private fun setupObservers() {
        viewModel.user.observe(this) { user ->
            user?.let {
                firstNameEditText.setText(it.firstName)
                lastNameEditText.setText(it.lastName)
                phoneEditText.setText(it.phoneNumber)
                hourlyRateEditText.setText(it.hourlyRate?.toString() ?: "")

                val rolePosition = viewModel.roles.indexOf(it.role)
                if (rolePosition >= 0) {
                    roleSpinner.setSelection(rolePosition)
                }
            }
        }

        viewModel.generatedEmail.observe(this) { email ->
            generatedEmailTextView.text = email
        }

        viewModel.generatedPassword.observe(this) { password ->
            generatedPasswordTextView.text = password
        }

        viewModel.saveResult.observe(this) { result ->
            result?.fold(
                onSuccess = { success ->
                    if (success) {
                        showSuccessMessage("User saved successfully!")
                        viewModel.generatedPassword.value?.let { password ->
                            copyToClipboard(password)
                            showPasswordCopiedDialog(password)
                        }
                        finish()
                    } else {
                        showErrorMessage("Failed to save user")
                    }
                    saveButton.isEnabled = true
                    saveButton.text = "Save User"
                },
                onFailure = { exception ->
                    showErrorMessage(exception.message ?: "Failed to save user")
                    saveButton.isEnabled = true
                    saveButton.text = "Save User"
                }
            )
        }

        viewModel.isLoading.observe(this) { isLoading ->
            saveButton.isEnabled = !isLoading
            saveButton.text = if (isLoading) "Saving..." else "Save User"
        }
    }

    private fun onCopyEmailClicked() {
        val email = viewModel.generatedEmail.value
        if (!email.isNullOrEmpty()) {
            copyToClipboard(email)
            Toast.makeText(this, "Email copied to clipboard!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onRegeneratePasswordClicked() {
        viewModel.regeneratePassword()
        Toast.makeText(this, "New password generated", Toast.LENGTH_SHORT).show()
    }

    private fun onSaveClicked() {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val phoneNumber = phoneEditText.text.toString().trim()
        val role = roleSpinner.selectedItem.toString()
        val hourlyRate = hourlyRateEditText.text.toString().toDoubleOrNull() ?: 0.0

        // Basic validation
        if (firstName.isEmpty() || lastName.isEmpty()) {
            showErrorMessage("Please enter first and last name")
            return
        }

        // Update ViewModel with form data
        viewModel.updateFirstName(firstName)
        viewModel.updateLastName(lastName)
        viewModel.updatePhoneNumber(phoneNumber)
        viewModel.updateRole(role)
        viewModel.updateHourlyRate(hourlyRate)

        saveButton.isEnabled = false
        saveButton.text = "Saving..."
        viewModel.saveUser()
    }

    private fun onCancelClicked() {
        AlertDialog.Builder(this)
            .setTitle("Confirm Cancel")
            .setMessage("Are you sure you want to cancel? Any unsaved changes will be lost.")
            .setPositiveButton("Yes") { _, _ -> finish() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun onDashboardClicked() {
        startActivity(Intent(this, AdminDashboardActivity::class.java))
        finish()
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Generated Password", text)
        clipboard.setPrimaryClip(clip)
    }

    private fun showPasswordCopiedDialog(password: String) {
        AlertDialog.Builder(this)
            .setTitle("Success")
            .setMessage("User saved successfully!\n\nGenerated Password: $password\n(This password has been copied to clipboard)")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showSuccessMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}