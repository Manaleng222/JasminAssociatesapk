package com.example.jasminassociates.ui.projectmanager

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jasminassociates.R
import com.example.jasminassociates.data.repository.TaskRepository
import com.example.jasminassociates.data.repository.UserRepository
import com.example.jasminassociates.models.User
import com.example.jasminassociates.services.ProjectTaskService
import com.example.jasminassociates.services.UserService
import com.example.jasminassociates.viewmodels.project.AddEditTaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class AddEditTaskActivity : AppCompatActivity() {

    @Inject
    lateinit var taskRepository: TaskRepository

    @Inject
    lateinit var userRepository: UserRepository

    private lateinit var viewModel: AddEditTaskViewModel
    private lateinit var taskNameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var startDateEditText: EditText
    private lateinit var dueDateEditText: EditText
    private lateinit var prioritySpinner: Spinner
    private lateinit var estimatedHoursEditText: EditText
    private lateinit var teamMemberSpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var dashboardButton: Button
    private lateinit var pageTitle: TextView
    private lateinit var validationMessage: TextView

    private var taskId: Int = 0
    private var projectId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_task)

        // Get taskId and projectId from intent
        taskId = intent.getIntExtra("taskId", 0)
        projectId = intent.getIntExtra("projectId", 0)

        // Initialize services with injected repositories
        val taskService = ProjectTaskService(taskRepository)
        val userService = UserService(userRepository)

        // Initialize ViewModel with ViewModelFactory
        viewModel = ViewModelProvider(this, AddEditTaskViewModelFactory(taskService, userService))[AddEditTaskViewModel::class.java]

        setupViews()
        setupObservers()
        setupListeners()

        // Set task and project IDs
        viewModel.setTaskId(taskId)
        viewModel.setProjectId(projectId)
        viewModel.loadData()
    }

    private fun setupViews() {
        // Fix: Use the correct IDs from XML
        taskNameEditText = findViewById(R.id.etTaskName)
        descriptionEditText = findViewById(R.id.etDescription)
        startDateEditText = findViewById(R.id.etStartDate)
        dueDateEditText = findViewById(R.id.etDueDate)
        prioritySpinner = findViewById(R.id.spPriority)
        estimatedHoursEditText = findViewById(R.id.etEstimatedHours)
        teamMemberSpinner = findViewById(R.id.spTeamMembers)
        saveButton = findViewById(R.id.btnSaveTask)
        cancelButton = findViewById(R.id.btnCancel)
        dashboardButton = findViewById(R.id.btnDashboard)
        pageTitle = findViewById(R.id.tvPageTitle)
        validationMessage = findViewById(R.id.tvValidationMessage)

        // Setup priority spinner
        val priorityAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOf("Low", "Medium", "High", "Critical")
        )
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        prioritySpinner.adapter = priorityAdapter
    }

    private fun setupObservers() {
        // Observe task name
        viewModel.taskName.observe(this) { name ->
            if (taskNameEditText.text.toString() != name) {
                taskNameEditText.setText(name)
            }
        }

        // Observe description
        viewModel.description.observe(this) { desc ->
            if (descriptionEditText.text.toString() != desc) {
                descriptionEditText.setText(desc)
            }
        }

        // Observe start date
        viewModel.startDate.observe(this) { date ->
            val formattedDate = date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
            startDateEditText.setText(formattedDate)
        }

        // Observe due date
        viewModel.dueDate.observe(this) { date ->
            val formattedDate = date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
            dueDateEditText.setText(formattedDate)
        }

        // Observe priority
        viewModel.selectedPriority.observe(this) { priority ->
            val priorities = viewModel.priorityLevels.value ?: listOf("Low", "Medium", "High", "Critical")
            val position = priorities.indexOf(priority)
            if (position >= 0 && prioritySpinner.selectedItemPosition != position) {
                prioritySpinner.setSelection(position)
            }
        }

        // Observe estimated hours
        viewModel.estimatedHours.observe(this) { hours ->
            val hoursText = if (hours > BigDecimal.ZERO) hours.toString() else ""
            if (estimatedHoursEditText.text.toString() != hoursText) {
                estimatedHoursEditText.setText(hoursText)
            }
        }

        // Observe team members
        viewModel.teamMembers.observe(this) { teamMembers ->
            setupTeamMemberSpinner(teamMembers)
        }

        // Observe selected team member
        viewModel.selectedTeamMember.observe(this) { selectedMember ->
            val teamMembers = viewModel.teamMembers.value ?: emptyList()
            val position = teamMembers.indexOfFirst { it.userID == selectedMember?.userID }
            if (position >= 0 && teamMemberSpinner.selectedItemPosition != position + 1) {
                teamMemberSpinner.setSelection(position + 1)
            }
        }

        // Observe validation messages
        viewModel.validationMessage.observe(this) { message ->
            validationMessage.text = message
            validationMessage.visibility = if (message.isNotEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        }

        // Observe page title
        viewModel.pageTitle.observe(this) { title ->
            pageTitle.text = title
        }

        // Observe save result
        viewModel.saveResult.observe(this) { result ->
            result?.onSuccess { success ->
                if (success) {
                    Toast.makeText(this, "Task saved successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to save task", Toast.LENGTH_SHORT).show()
                }
            }?.onFailure { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
            viewModel.clearSaveResult()
        }
    }

    private fun setupListeners() {
        // Task name listener
        taskNameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.updateTaskName(taskNameEditText.text.toString())
            }
        }

        // Description listener
        descriptionEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.updateDescription(descriptionEditText.text.toString())
            }
        }

        // Start date listener - changed from Button to EditText
        startDateEditText.setOnClickListener {
            showDatePicker { selectedDate ->
                viewModel.updateStartDate(selectedDate)
            }
        }

        // Due date listener - changed from Button to EditText
        dueDateEditText.setOnClickListener {
            showDatePicker { selectedDate ->
                viewModel.updateDueDate(selectedDate)
            }
        }

        // Priority spinner listener
        prioritySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedPriority = parent?.getItemAtPosition(position) as String
                viewModel.updateSelectedPriority(selectedPriority)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Estimated hours listener
        estimatedHoursEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                try {
                    val hours = estimatedHoursEditText.text.toString().toDouble()
                    viewModel.updateEstimatedHours(hours)
                } catch (e: NumberFormatException) {
                    viewModel.updateEstimatedHours(0.0)
                }
            }
        }

        // Team member spinner listener
        teamMemberSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                if (position > 0) {
                    val teamMembers = viewModel.teamMembers.value ?: emptyList()
                    if (position - 1 < teamMembers.size) {
                        val selectedMember = teamMembers[position - 1]
                        viewModel.updateSelectedTeamMember(selectedMember)
                    }
                } else {
                    viewModel.updateSelectedTeamMember(null)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Save button listener
        saveButton.setOnClickListener {
            // Update all fields before saving
            viewModel.updateTaskName(taskNameEditText.text.toString())
            viewModel.updateDescription(descriptionEditText.text.toString())
            try {
                val hours = estimatedHoursEditText.text.toString().toDouble()
                viewModel.updateEstimatedHours(hours)
            } catch (e: NumberFormatException) {
                viewModel.updateEstimatedHours(0.0)
            }
            viewModel.saveTask()
        }

        // Cancel button listener
        cancelButton.setOnClickListener {
            finish()
        }

        // Dashboard button listener
        dashboardButton.setOnClickListener {
            finish()
        }
    }

    private fun setupTeamMemberSpinner(teamMembers: List<User>) {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            mutableListOf<String>().apply {
                add("Select Team Member")
                addAll(teamMembers.map { "${it.firstName} ${it.lastName}" })
            }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        teamMemberSpinner.adapter = adapter
    }

    private fun showDatePicker(onDateSelected: (LocalDate) -> Unit) {
        val currentDate = viewModel.startDate.value ?: LocalDate.now()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                onDateSelected(selectedDate)
            },
            currentDate.year,
            currentDate.monthValue - 1,
            currentDate.dayOfMonth
        )
        datePicker.show()
    }
    // ViewModel Factory for AddEditTaskViewModel
    class AddEditTaskViewModelFactory(
        private val taskService: ProjectTaskService,
        private val userService: UserService
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddEditTaskViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddEditTaskViewModel(taskService, userService) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

