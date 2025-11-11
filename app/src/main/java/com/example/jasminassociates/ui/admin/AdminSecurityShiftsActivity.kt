package com.example.jasminassociates.ui.admin

import com.example.jasminassociates.ui.adapters.AllShiftsAdapter
import com.example.jasminassociates.ui.adapters.LiveShiftsAdapter
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jasminassociates.R
import com.example.jasminassociates.models.SecurityShift
import com.example.jasminassociates.viewmodels.admin.AdminSecurityShiftsViewModel
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

class AdminSecurityShiftsActivity : AppCompatActivity() {

    private lateinit var viewModel: AdminSecurityShiftsViewModel
    private lateinit var securityPersonnelSpinner: Spinner
    private lateinit var projectSpinner: Spinner
    private lateinit var shiftDatePicker: DatePicker
    private lateinit var startTimePicker: TimePicker
    private lateinit var endTimePicker: TimePicker
    private lateinit var hourlyRateEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var createShiftButton: Button
    private lateinit var refreshButton: Button
    private lateinit var liveShiftsRecyclerView: RecyclerView
    private lateinit var allShiftsRecyclerView: RecyclerView
    private lateinit var liveShiftsAdapter: LiveShiftsAdapter
    private lateinit var allShiftsAdapter: AllShiftsAdapter
    private lateinit var validationMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_security_shifts)

        // Initialize ViewModel with ViewModelProvider
        viewModel = ViewModelProvider(this).get(AdminSecurityShiftsViewModel::class.java)

        setupViews()
        setupRecyclerViews()
        setupObservers()
        loadData()
    }

    private fun setupViews() {
        securityPersonnelSpinner = findViewById(R.id.securityPersonnelSpinner)
        projectSpinner = findViewById(R.id.projectSpinner)
        shiftDatePicker = findViewById(R.id.shiftDatePicker)
        startTimePicker = findViewById(R.id.startTimePicker)
        endTimePicker = findViewById(R.id.endTimePicker)
        hourlyRateEditText = findViewById(R.id.hourlyRateEditText)
        locationEditText = findViewById(R.id.locationEditText)
        createShiftButton = findViewById(R.id.createShiftButton)
        refreshButton = findViewById(R.id.refreshButton)
        validationMessage = findViewById(R.id.validationMessage)

        // Set minimum date to today
        val calendar = Calendar.getInstance()
        shiftDatePicker.minDate = calendar.timeInMillis

        createShiftButton.setOnClickListener { onCreateShiftClicked() }
        refreshButton.setOnClickListener { onRefreshClicked() }

        findViewById<Button>(R.id.dashboardButton).setOnClickListener {
            onDashboardClicked()
        }
    }

    private fun setupRecyclerViews() {
        liveShiftsRecyclerView = findViewById(R.id.liveShiftsRecyclerView)
        allShiftsRecyclerView = findViewById(R.id.allShiftsRecyclerView)

        liveShiftsAdapter = LiveShiftsAdapter(emptyList())
        allShiftsAdapter = AllShiftsAdapter(emptyList()) { shift -> onDeleteShiftClicked(shift) }

        liveShiftsRecyclerView.layoutManager = LinearLayoutManager(this)
        liveShiftsRecyclerView.adapter = liveShiftsAdapter

        allShiftsRecyclerView.layoutManager = LinearLayoutManager(this)
        allShiftsRecyclerView.adapter = allShiftsAdapter
    }

    private fun setupObservers() {
        viewModel.securityPersonnel.observe(this) { personnel ->
            val personnelNames = personnel.map {
                "${it.firstName ?: ""} ${it.lastName ?: ""}".trim().ifEmpty { "Unknown Security" }
            }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, personnelNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            securityPersonnelSpinner.adapter = adapter
        }

        viewModel.projects.observe(this) { projects ->
            val projectNames = projects.map { it.projectName ?: "Unknown Project" }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, projectNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            projectSpinner.adapter = adapter
        }

        viewModel.liveShifts.observe(this) { shifts ->
            liveShiftsAdapter.updateShifts(shifts)
        }

        viewModel.allShifts.observe(this) { shifts ->
            allShiftsAdapter.updateShifts(shifts)
        }

        viewModel.createShiftResult.observe(this) { result ->
            createShiftButton.isEnabled = true
            createShiftButton.text = "Create Shift"

            result?.fold(
                onSuccess = { success ->
                    if (success) {
                        Toast.makeText(this, "Shift created successfully!", Toast.LENGTH_SHORT).show()
                        clearForm()
                    } else {
                        Toast.makeText(this, "Failed to create shift", Toast.LENGTH_LONG).show()
                    }
                },
                onFailure = { exception ->
                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            )
        }

        viewModel.isLoading.observe(this) { isLoading ->
            createShiftButton.isEnabled = !isLoading
            refreshButton.isEnabled = !isLoading
        }
    }

    private fun loadData() {
        viewModel.loadData()
    }

    private fun onCreateShiftClicked() {
        validationMessage.visibility = TextView.GONE

        val selectedPersonnelPosition = securityPersonnelSpinner.selectedItemPosition
        val securityPersonnelList = viewModel.securityPersonnel.value
        val selectedPersonnel = if (securityPersonnelList != null && selectedPersonnelPosition >= 0 &&
            selectedPersonnelPosition < securityPersonnelList.size) {
            securityPersonnelList[selectedPersonnelPosition]
        } else {
            null
        }

        val hourlyRate = hourlyRateEditText.text.toString().toDoubleOrNull()

        // Validation
        if (selectedPersonnel == null) {
            showValidationMessage("Please select security personnel")
            return
        }

        if (hourlyRate == null || hourlyRate <= 0) {
            showValidationMessage("Please enter a valid hourly rate")
            return
        }

        val startTime = getTimeFromPicker(startTimePicker)
        val endTime = getTimeFromPicker(endTimePicker)

        if (startTime >= endTime) {
            showValidationMessage("Start time must be before end time")
            return
        }

        createShiftButton.isEnabled = false
        createShiftButton.text = "Creating..."

        // Get selected project
        val selectedProjectPosition = projectSpinner.selectedItemPosition
        val projectList = viewModel.projects.value
        val selectedProject = if (projectList != null && selectedProjectPosition >= 0 &&
            selectedProjectPosition < projectList.size) {
            projectList[selectedProjectPosition]
        } else {
            null
        }

        // Create shift data
        val shiftDate = getDateFromPicker(shiftDatePicker)
        val location = locationEditText.text.toString().trim()

        // Create SecurityShift object with correct parameter names
        val securityShift = SecurityShift(
            securityPersonnelID = selectedPersonnel.userID,
            shiftDate = LocalDateTime.of(shiftDate, LocalTime.MIDNIGHT),
            scheduledStartTime = startTime,
            scheduledEndTime = endTime,
            hourlyRate = BigDecimal.valueOf(hourlyRate),
            location = location.ifEmpty { "Main Site" }
        )

        viewModel.createShift(securityShift)
    }

    private fun onDeleteShiftClicked(shift: SecurityShift) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this shift?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteShift(shift.shiftID)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun onRefreshClicked() {
        loadData()
        Toast.makeText(this, "Data refreshed", Toast.LENGTH_SHORT).show()
    }

    private fun onDashboardClicked() {
        startActivity(Intent(this, AdminDashboardActivity::class.java))
        finish()
    }

    private fun showValidationMessage(message: String) {
        validationMessage.text = message
        validationMessage.visibility = TextView.VISIBLE
    }

    private fun getTimeFromPicker(timePicker: TimePicker): LocalTime {
        return LocalTime.of(timePicker.hour, timePicker.minute)
    }

    private fun getDateFromPicker(datePicker: DatePicker): LocalDate {
        return LocalDate.of(datePicker.year, datePicker.month + 1, datePicker.dayOfMonth)
    }

    private fun clearForm() {
        hourlyRateEditText.text.clear()
        locationEditText.text.clear()
        // Reset spinners to first position
        if (securityPersonnelSpinner.adapter != null && securityPersonnelSpinner.adapter.count > 0) {
            securityPersonnelSpinner.setSelection(0)
        }
        if (projectSpinner.adapter != null && projectSpinner.adapter.count > 0) {
            projectSpinner.setSelection(0)
        }
    }
}