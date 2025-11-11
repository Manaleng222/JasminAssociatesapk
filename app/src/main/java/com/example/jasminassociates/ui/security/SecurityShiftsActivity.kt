package com.example.jasminassociates.ui.security

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jasminassociates.R
import com.example.jasminassociates.ui.adapters.SecurityShiftsAdapter
import com.example.jasminassociates.viewmodels.security.SecurityShiftsViewModel
import com.jasminassociates.models.ShiftDisplay

class SecurityShiftsActivity : AppCompatActivity() {

    private lateinit var viewModel: SecurityShiftsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SecurityShiftsAdapter
    private lateinit var totalShifts: TextView
    private lateinit var completedShifts: TextView
    private lateinit var totalHours: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_shifts)

        viewModel = SecurityShiftsViewModel()
        setupViews()
        setupRecyclerView()
        observeViewModel()
        loadShifts()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerView)
        totalShifts = findViewById(R.id.totalShifts)
        completedShifts = findViewById(R.id.completedShifts)
        totalHours = findViewById(R.id.totalHours)

        findViewById<Button>(R.id.dashboardButton).setOnClickListener {
            onDashboardClicked()
        }

        findViewById<Button>(R.id.exportShiftsButton).setOnClickListener {
            onExportShiftsClicked()
        }
    }

    private fun setupRecyclerView() {
        adapter = SecurityShiftsAdapter(
            onEditClick = { shift ->
                onEditShiftClicked(shift)
            },
            onClockInOutClick = { shift ->
                onClockInOutClicked(shift)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.shifts.observe(this) { shifts ->
            adapter.submitList(shifts)
        }

        viewModel.totalShifts.observe(this) { count ->
            totalShifts.text = "Total Shifts: $count"
        }

        viewModel.completedShifts.observe(this) { count ->
            completedShifts.text = "Completed: $count"
        }

        viewModel.totalHours.observe(this) { hours ->
            totalHours.text = "Total Hours: $hours"
        }
    }

    private fun loadShifts() {
        viewModel.loadShifts()
    }

    private fun onEditShiftClicked(shift: ShiftDisplay) {
        Toast.makeText(this, "Edit shift: ${shift.shiftId}", Toast.LENGTH_SHORT).show()
    }

    private fun onClockInOutClicked(shift: ShiftDisplay) {
        val options = arrayOf("Clock In", "Clock Out")

        AlertDialog.Builder(this)
            .setTitle("Clock Action")
            .setItems(options) { _, which -> // Fixed unused parameter
                when (which) {
                    0 -> viewModel.clockIn(shift.shiftId)
                    1 -> viewModel.clockOut(shift.shiftId)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun onExportShiftsClicked() {
        // Simplified export function
        viewModel.exportShifts().observe(this, Observer { success: Boolean? -> // Fixed type inference
            if (success == true) {
                Toast.makeText(this, "Shifts exported successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to export shifts", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onDashboardClicked() {
        val intent = Intent(this, SecurityGuardDashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}