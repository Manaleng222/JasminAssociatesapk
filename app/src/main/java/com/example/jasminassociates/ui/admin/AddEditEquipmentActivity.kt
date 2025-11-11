package com.example.jasminassociates.ui.admin



import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.jasminassociates.R

class AddEditEquipmentActivity : AppCompatActivity() {

    private lateinit var equipmentNameEditText: EditText
    private lateinit var equipmentTypeSpinner: Spinner
    private lateinit var serialNumberEditText: EditText
    private lateinit var statusSpinner: Spinner
    private lateinit var locationEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var purchaseDatePicker: DatePicker
    private lateinit var purchaseCostEditText: EditText
    private lateinit var currentValueEditText: EditText
    private lateinit var hourlyRentalRateEditText: EditText
    private lateinit var maintenanceIntervalEditText: EditText
    private lateinit var lastMaintenanceDatePicker: DatePicker
    private lateinit var nextMaintenanceDatePicker: DatePicker
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_equipment)

        setupViews()
    }

    private fun setupViews() {
        equipmentNameEditText = findViewById(R.id.equipmentNameEditText)
        equipmentTypeSpinner = findViewById(R.id.equipmentTypeSpinner)
        serialNumberEditText = findViewById(R.id.serialNumberEditText)
        statusSpinner = findViewById(R.id.statusSpinner)
        locationEditText = findViewById(R.id.locationEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        purchaseDatePicker = findViewById(R.id.purchaseDatePicker)
        purchaseCostEditText = findViewById(R.id.purchaseCostEditText)
        currentValueEditText = findViewById(R.id.currentValueEditText)
        hourlyRentalRateEditText = findViewById(R.id.hourlyRentalRateEditText)
        maintenanceIntervalEditText = findViewById(R.id.maintenanceIntervalEditText)
        lastMaintenanceDatePicker = findViewById(R.id.lastMaintenanceDatePicker)
        nextMaintenanceDatePicker = findViewById(R.id.nextMaintenanceDatePicker)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)

        // Setup spinners
        setupSpinner(equipmentTypeSpinner, arrayOf("Heavy Machinery", "Tools", "Vehicles", "Safety Equipment", "Other"))
        setupSpinner(statusSpinner, arrayOf("Available", "In Use", "Under Maintenance", "Retired"))

        saveButton.setOnClickListener { onSaveClicked() }
        cancelButton.setOnClickListener { onCancelClicked() }

        findViewById<Button>(R.id.dashboardButton).setOnClickListener {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
            finish()
        }
    }

    private fun setupSpinner(spinner: Spinner, items: Array<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun onSaveClicked() {
        // Validate required fields
        if (equipmentNameEditText.text.toString().trim().isEmpty()) {
            equipmentNameEditText.error = "Equipment name is required"
            return
        }

        if (equipmentTypeSpinner.selectedItemPosition == 0) {
            Toast.makeText(this, "Please select equipment type", Toast.LENGTH_SHORT).show()
            return
        }

        if (statusSpinner.selectedItemPosition == 0) {
            Toast.makeText(this, "Please select status", Toast.LENGTH_SHORT).show()
            return
        }

        // Collect data
        val equipmentName = equipmentNameEditText.text.toString().trim()
        val equipmentType = equipmentTypeSpinner.selectedItem.toString()
        val serialNumber = serialNumberEditText.text.toString().trim()
        val status = statusSpinner.selectedItem.toString()
        val location = locationEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()

        // Parse numeric values with null safety
        val purchaseCost = purchaseCostEditText.text.toString().toDoubleOrNull() ?: 0.0
        val currentValue = currentValueEditText.text.toString().toDoubleOrNull() ?: 0.0
        val hourlyRentalRate = hourlyRentalRateEditText.text.toString().toDoubleOrNull() ?: 0.0
        val maintenanceInterval = maintenanceIntervalEditText.text.toString().toIntOrNull() ?: 0

        // Handle dates
        val purchaseDate = getDateFromDatePicker(purchaseDatePicker)
        val lastMaintenanceDate = getDateFromDatePicker(lastMaintenanceDatePicker)
        val nextMaintenanceDate = getDateFromDatePicker(nextMaintenanceDatePicker)

        // Here you would typically save to database or send to API
        saveEquipmentData(
            equipmentName,
            equipmentType,
            serialNumber,
            status,
            location,
            description,
            purchaseCost,
            currentValue,
            hourlyRentalRate,
            maintenanceInterval,
            purchaseDate,
            lastMaintenanceDate,
            nextMaintenanceDate
        )
    }

    private fun getDateFromDatePicker(datePicker: DatePicker): String {
        val day = datePicker.dayOfMonth
        val month = datePicker.month + 1 // Month is 0-based
        val year = datePicker.year
        return String.format("%04d-%02d-%02d", year, month, day)
    }

    private fun saveEquipmentData(
        equipmentName: String,
        equipmentType: String,
        serialNumber: String,
        status: String,
        location: String,
        description: String,
        purchaseCost: Double,
        currentValue: Double,
        hourlyRentalRate: Double,
        maintenanceInterval: Int,
        purchaseDate: String,
        lastMaintenanceDate: String,
        nextMaintenanceDate: String
    ) {
        // TODO: Implement your data saving logic here
        // This could be saving to Room database, making API call, etc.

        // For now, just show a success message
        Toast.makeText(this, "Equipment saved successfully!", Toast.LENGTH_SHORT).show()

        // Optionally finish the activity
        finish()
    }

    private fun onCancelClicked() {
        finish()
    }
}