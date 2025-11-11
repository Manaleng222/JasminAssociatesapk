package com.example.jasminassociates.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.jasminassociates.R
import com.example.jasminassociates.viewmodels.project.AddEditProjectViewModel
import java.time.LocalDate

class AddEditProjectActivity : AppCompatActivity() {

    private lateinit var viewModel: AddEditProjectViewModel
    private lateinit var projectNameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var projectTypeSpinner: Spinner
    private lateinit var clientSpinner: Spinner
    private lateinit var projectManagerSpinner: Spinner
    private lateinit var startDatePicker: DatePicker
    private lateinit var endDatePicker: DatePicker
    private lateinit var estimatedBudgetEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var statusSpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_project)

        viewModel = ViewModelProvider(this).get(AddEditProjectViewModel::class.java)
        setupViews()
        setupObservers()
        // loadData() is called automatically in ViewModel init
    }

    private fun setupViews() {
        projectNameEditText = findViewById(R.id.projectNameEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        projectTypeSpinner = findViewById(R.id.projectTypeSpinner)
        clientSpinner = findViewById(R.id.clientSpinner)
        projectManagerSpinner = findViewById(R.id.projectManagerSpinner)
        startDatePicker = findViewById(R.id.startDatePicker)
        endDatePicker = findViewById(R.id.endDatePicker)
        estimatedBudgetEditText = findViewById(R.id.estimatedBudgetEditText)
        locationEditText = findViewById(R.id.locationEditText)
        statusSpinner = findViewById(R.id.statusSpinner)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)
        progressBar = findViewById(R.id.progressBar)

        // Setup spinners - use the actual project types and status options from ViewModel
        setupSpinner(projectTypeSpinner, viewModel.projectTypes.toTypedArray())
        setupSpinner(statusSpinner, viewModel.statusOptions.toTypedArray())

        saveButton.setOnClickListener { onSaveClicked() }
        cancelButton.setOnClickListener { onCancelClicked() }

        findViewById<Button>(R.id.dashboardButton).setOnClickListener {
            onDashboardClicked()
        }
    }

    private fun setupSpinner(spinner: Spinner, items: Array<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.clients.observe(this) { clients ->
            val clientNames = clients.map { it.firstName ?: "Unknown Client" }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, clientNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            clientSpinner.adapter = adapter

            // Set selected client if editing
            viewModel.selectedClient.value?.let { client ->
                val index = clients.indexOfFirst { it.userID == client.userID }
                if (index != -1) clientSpinner.setSelection(index)
            }
        }

        viewModel.projectManagers.observe(this) { managers ->
            val managerNames = managers.map { it.firstName ?: "Unknown Manager" }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, managerNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            projectManagerSpinner.adapter = adapter

            // Set selected project manager if editing
            viewModel.selectedProjectManager.value?.let { manager ->
                val index = managers.indexOfFirst { it.userID == manager.userID }
                if (index != -1) projectManagerSpinner.setSelection(index)
            }
        }

        viewModel.project.observe(this) { project ->
            projectNameEditText.setText(project.projectName)
            descriptionEditText.setText(project.description)
            locationEditText.setText(project.location)
            estimatedBudgetEditText.setText(project.estimatedBudget?.toString() ?: "0.0")

            // Set spinner selections
            val projectTypeIndex = viewModel.projectTypes.indexOf(project.projectType)
            if (projectTypeIndex != -1) projectTypeSpinner.setSelection(projectTypeIndex)

            val statusIndex = viewModel.statusOptions.indexOf(project.status)
            if (statusIndex != -1) statusSpinner.setSelection(statusIndex)

            // Set dates
            project.startDate?.let { date ->
                val localDate = if (date is java.sql.Timestamp) {
                    date.toLocalDate()
                } else {
                    date as? LocalDate ?: LocalDate.now()
                }
                startDatePicker.updateDate(localDate.year, localDate.monthValue - 1, localDate.dayOfMonth)
            }

            project.endDate?.let { date ->
                val localDate = if (date is java.sql.Timestamp) {
                    date.toLocalDate()
                } else {
                    date as? LocalDate ?: LocalDate.now()
                }
                endDatePicker.updateDate(localDate.year, localDate.monthValue - 1, localDate.dayOfMonth)
            }
        }

        viewModel.saveResult.observe(this) { result ->
            progressBar.visibility = ProgressBar.GONE
            saveButton.isEnabled = true

            result?.fold(
                onSuccess = {
                    Toast.makeText(this, "Project saved successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                },
                onFailure = { exception ->
                    Toast.makeText(this, "Failed to save project: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            )
        }

        viewModel.isLoading.observe(this) { isLoading ->
            saveButton.isEnabled = !isLoading
            cancelButton.isEnabled = !isLoading
            progressBar.visibility = if (isLoading) ProgressBar.VISIBLE else ProgressBar.GONE
        }

        viewModel.pageTitle.observe(this) { title ->
            this.title = title
        }
    }

    private fun onSaveClicked() {
        val projectName = projectNameEditText.text.toString()

        // Basic validation
        if (projectName.isBlank()) {
            Toast.makeText(this, "Please enter project name", Toast.LENGTH_SHORT).show()
            return
        }

        // Update ViewModel with form data
        viewModel.updateProjectName(projectName)
        viewModel.updateDescription(descriptionEditText.text.toString())
        viewModel.updateProjectType(projectTypeSpinner.selectedItem.toString())
        viewModel.updateLocation(locationEditText.text.toString())
        viewModel.updateStatus(statusSpinner.selectedItem.toString())

        // Update budget
        estimatedBudgetEditText.text.toString().toDoubleOrNull()?.let {
            viewModel.updateEstimatedBudget(it)
        }

        // Update dates
        val startDate = getDateFromPicker(startDatePicker)
        viewModel.updateStartDate(startDate)

        val endDate = getDateFromPicker(endDatePicker)
        viewModel.updateEndDate(endDate)

        // Update selected client and project manager
        val selectedClientIndex = clientSpinner.selectedItemPosition
        val clients = viewModel.clients.value
        if (clients != null && selectedClientIndex >= 0 && selectedClientIndex < clients.size) {
            viewModel.setSelectedClient(clients[selectedClientIndex])
        }

        val selectedManagerIndex = projectManagerSpinner.selectedItemPosition
        val managers = viewModel.projectManagers.value
        if (managers != null && selectedManagerIndex >= 0 && selectedManagerIndex < managers.size) {
            viewModel.setSelectedProjectManager(managers[selectedManagerIndex])
        }

        progressBar.visibility = ProgressBar.VISIBLE
        saveButton.isEnabled = false
        viewModel.saveProject()
    }

    private fun onCancelClicked() {
        finish()
    }

    private fun onDashboardClicked() {
        startActivity(Intent(this, AdminDashboardActivity::class.java))
        finish()
    }

    private fun getDateFromPicker(datePicker: DatePicker): LocalDate {
        return LocalDate.of(datePicker.year, datePicker.month + 1, datePicker.dayOfMonth)
    }
}