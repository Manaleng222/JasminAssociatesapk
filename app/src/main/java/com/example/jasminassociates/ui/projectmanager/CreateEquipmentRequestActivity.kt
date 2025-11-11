package com.example.jasminassociates.ui.projectmanager

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.jasminassociates.R
import com.example.jasminassociates.databinding.ActivityCreateEquipmentRequestBinding
import com.example.jasminassociates.viewmodels.CreateEquipmentRequestViewModel

class CreateEquipmentRequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateEquipmentRequestBinding
    private lateinit var viewModel: CreateEquipmentRequestViewModel
    private var preselectedEquipmentId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEquipmentRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preselectedEquipmentId = intent.getIntExtra("preselected_equipment_id", 0)

        viewModel = ViewModelProvider(this).get(CreateEquipmentRequestViewModel::class.java)

        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.new_equipment_request)

        // Setup equipment types AutoCompleteTextView
        val equipmentTypesAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, viewModel.equipmentTypes)
        binding.spinnerEquipmentType.setAdapter(equipmentTypesAdapter)

        binding.btnSubmit.setOnClickListener { submitRequest() }
        binding.btnCancel.setOnClickListener { finish() }
        binding.btnDashboard.setOnClickListener { navigateToDashboard() }
    }

    private fun setupObservers() {
        viewModel.projects.observe(this) { projects ->
            val projectNames = projects.map { it.projectName }
            val projectAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, projectNames)
            binding.spinnerProject.setAdapter(projectAdapter)
        }

        viewModel.preselectedEquipment.observe(this) { equipment ->
            equipment?.let {
                binding.layoutPreselectedEquipment.visibility = android.view.View.VISIBLE
                binding.tvEquipmentName.text = it.equipmentName
                binding.tvEquipmentType.text = it.equipmentType
                binding.tvEquipmentStatus.text = it.status
            }
        }

        viewModel.submitResult.observe(this) { success ->
            if (success) {
                showSuccess(getString(R.string.saved_successfully))
                finish()
            } else {
                val message = viewModel.validationMessage.value
                showError(message ?: getString(R.string.save_failed))
            }
        }

        viewModel.validationMessage.observe(this) { message ->
            binding.tvValidationMessage.text = message
            binding.tvValidationMessage.visibility = if (message.isNotEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        }
    }

    private fun submitRequest() {
        val projectName = binding.spinnerProject.text.toString()
        val equipmentType = binding.spinnerEquipmentType.text.toString()
        val description = binding.etDescription.text.toString()
        val quantity = binding.etQuantity.text.toString().toIntOrNull() ?: 1

        // Find the project by name
        val projects = viewModel.projects.value
        val selectedProject = projects?.find { it.projectName == projectName }
        viewModel.setSelectedProject(selectedProject)

        viewModel.updateEquipmentType(equipmentType)
        viewModel.updateDescription(description)
        viewModel.updateQuantity(quantity)

        viewModel.submitRequest()
    }

    private fun navigateToDashboard() {
        startActivity(android.content.Intent(this, ProjectManagerDashboardActivity::class.java))
        finish()
    }

    private fun showSuccess(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}