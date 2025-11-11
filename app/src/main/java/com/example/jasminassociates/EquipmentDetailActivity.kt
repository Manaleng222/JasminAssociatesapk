package com.example.jasminassociates

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.example.jasminassociates.databinding.ActivityEquipmentDetailBinding
import com.example.jasminassociates.ui.admin.AddEditEquipmentActivity
import com.example.jasminassociates.ui.projectmanager.CreateEquipmentRequestActivity
import com.example.jasminassociates.viewmodels.EquipmentDetailViewModel
import com.jasminassociates.models.Equipment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class EquipmentDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEquipmentDetailBinding
    private lateinit var viewModel: EquipmentDetailViewModel
    private lateinit var btnEdit: Button
    private lateinit var btnRequest: Button
    private lateinit var layoutAdminActions: LinearLayout
    private lateinit var cardCurrentAssignment: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEquipmentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val equipmentId = intent.getIntExtra("equipmentId", -1)
        val requestId = intent.getIntExtra("requestId", 0)

        // Hilt will inject the ViewModel automatically
        viewModel = ViewModelProvider(this)[EquipmentDetailViewModel::class.java]

        // Set the equipment data
        viewModel.setEquipmentData(equipmentId, requestId)

        initializeViews()
        setupClickListeners()
        setupObservers()
    }

    private fun initializeViews() {
        btnEdit = findViewById(R.id.btnEdit)
        btnRequest = findViewById(R.id.btnRequest)
        layoutAdminActions = findViewById(R.id.layoutAdminActions)
        cardCurrentAssignment = findViewById(R.id.cardCurrentAssignment)
    }

    private fun setupClickListeners() {
        btnEdit.setOnClickListener {
            // Check admin status directly from ViewModel
            if (viewModel.isAdmin.value == true) {
                viewModel.equipment.value?.let { equipment ->
                    val intent = Intent(this, AddEditEquipmentActivity::class.java)
                    intent.putExtra("equipmentId", equipment.equipmentID)
                    startActivity(intent)
                }
            }
        }

        btnRequest.setOnClickListener {
            // Check project manager status directly from ViewModel
            if (viewModel.isProjectManager.value == true) {
                viewModel.equipment.value?.let { equipment ->
                    val intent = Intent(this, CreateEquipmentRequestActivity::class.java)
                    intent.putExtra("equipmentId", equipment.equipmentID)
                    startActivity(intent)
                }
            }
        }

        findViewById<Button>(R.id.btnMaintenanceHistory).setOnClickListener {
            Toast.makeText(this, "Maintenance history feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnAssignmentHistory).setOnClickListener {
            Toast.makeText(this, "Assignment history feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        viewModel.equipment.observe(this) { equipment ->
            equipment?.let { updateEquipmentUI(it) }
        }

        viewModel.isAdmin.observe(this) { isAdmin ->
            btnEdit.visibility = if (isAdmin == true) View.VISIBLE else View.GONE
            layoutAdminActions.visibility = if (isAdmin == true) View.VISIBLE else View.GONE
        }

        viewModel.isProjectManager.observe(this) { isProjectManager ->
            btnRequest.visibility = if (isProjectManager == true) View.VISIBLE else View.GONE
        }

        viewModel.hasCurrentAssignment.observe(this) { hasAssignment ->
            cardCurrentAssignment.visibility = if (hasAssignment == true) View.VISIBLE else View.GONE
        }

        viewModel.currentAssignment.observe(this) { assignment ->
            assignment?.let {
                findViewById<TextView>(R.id.tvProjectName).text = assignment.project?.projectName ?: "Unknown Project"
                findViewById<TextView>(R.id.tvAssignedDate).text =
                    "Assigned: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(assignment.assignedDate)}"
            }
        }
    }

    private fun updateEquipmentUI(equipment: Equipment) {
        findViewById<TextView>(R.id.tvEquipmentName).text = equipment.equipmentName
        findViewById<TextView>(R.id.tvEquipmentType).text = equipment.equipmentType
        findViewById<TextView>(R.id.tvSerialNumber).text = equipment.serialNumber
        findViewById<TextView>(R.id.tvLocation).text = equipment.location
        findViewById<TextView>(R.id.tvDescription).text = equipment.description ?: "No description"

        equipment.purchaseDate?.let {
            findViewById<TextView>(R.id.tvPurchaseDate).text =
                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it)
        }

        findViewById<TextView>(R.id.tvCurrentValue).text = String.format("$%.2f", equipment.currentValue)
        findViewById<TextView>(R.id.tvRentalRate).text = String.format("$%.2f/hr", equipment.hourlyRentalRate)

        equipment.lastMaintenanceDate?.let {
            findViewById<TextView>(R.id.tvLastMaintenance).text =
                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it)
        }

        equipment.nextMaintenanceDate?.let {
            findViewById<TextView>(R.id.tvNextMaintenance).text =
                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it)
        }

        // Update status - ensure status is not null
        val status = equipment.status ?: "Unknown"
        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        tvStatus.text = status
        tvStatus.background = getStatusBackground(status)

        // Update icon
        findViewById<TextView>(R.id.tvEquipmentIcon).text = getEquipmentIcon(equipment.equipmentType)
    }

    private fun getStatusBackground(status: String): Drawable {
        val color = when (status.lowercase()) {
            "available" -> Color.GREEN
            "in use" -> Color.BLUE
            "maintenance" -> Color.YELLOW
            "broken" -> Color.RED
            else -> Color.GRAY
        }
        val drawable = GradientDrawable()
        drawable.cornerRadius = 8f
        drawable.setColor(color)
        return drawable
    }

    private fun getEquipmentIcon(equipmentType: String?): String {
        return when (equipmentType?.lowercase()) {
            "excavator" -> "🚜"
            "crane" -> "🏗️"
            "bulldozer" -> "🚜"
            "truck" -> "🚛"
            else -> "🔧"
        }
    }
}