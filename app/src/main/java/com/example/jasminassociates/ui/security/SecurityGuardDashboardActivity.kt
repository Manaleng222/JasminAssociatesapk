package com.example.jasminassociates.ui.security

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jasminassociates.R
import com.example.jasminassociates.models.SecurityShift
import com.example.jasminassociates.ui.adapters.TodaysShiftsAdapter
import com.example.jasminassociates.viewmodels.security.SecurityGuardDashboardViewModel

class SecurityGuardDashboardActivity : AppCompatActivity() {

    private lateinit var viewModel: SecurityGuardDashboardViewModel
    private lateinit var welcomeMessage: TextView
    private lateinit var todaysShiftsCount: TextView
    private lateinit var weeklyHours: TextView
    private lateinit var totalEarnings: TextView
    private lateinit var clockStatus: TextView
    private lateinit var currentShiftInfo: TextView
    private lateinit var clockInButton: Button
    private lateinit var clockOutButton: Button
    private lateinit var locationProgress: ProgressBar
    private lateinit var locationText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TodaysShiftsAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_guard_dashboard)

        // For now, create with empty constructor - in real app, use dependency injection
        viewModel = SecurityGuardDashboardViewModel()
        setupViews()
        setupRecyclerView()
        observeViewModel()
        loadDashboardData()
    }

    private fun setupViews() {
        welcomeMessage = findViewById(R.id.welcomeMessage)
        todaysShiftsCount = findViewById(R.id.todaysShiftsCount)
        weeklyHours = findViewById(R.id.weeklyHours)
        totalEarnings = findViewById(R.id.totalEarnings)
        clockStatus = findViewById(R.id.clockStatus)
        currentShiftInfo = findViewById(R.id.currentShiftInfo)
        clockInButton = findViewById(R.id.clockInButton)
        clockOutButton = findViewById(R.id.clockOutButton)
        locationProgress = findViewById(R.id.locationProgress)
        locationText = findViewById(R.id.locationText)
        recyclerView = findViewById(R.id.recyclerView)

        // Action buttons
        findViewById<Button>(R.id.myShiftsButton).setOnClickListener {
            onMyShiftsClicked()
        }

        findViewById<Button>(R.id.scheduleButton).setOnClickListener {
            onScheduleClicked()
        }

        findViewById<Button>(R.id.timeSheetButton).setOnClickListener {
            onTimeSheetClicked()
        }

        findViewById<Button>(R.id.reportsButton).setOnClickListener {
            onReportsClicked()
        }

        // Clock in/out buttons
        clockInButton.setOnClickListener {
            onClockInClicked()
        }

        clockOutButton.setOnClickListener {
            onClockOutClicked()
        }
    }

    private fun setupRecyclerView() {
        adapter = TodaysShiftsAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeViewModel() {
        viewModel.welcomeMessage.observe(this) { message ->
            welcomeMessage.text = message
        }

        viewModel.todaysShiftsCount.observe(this) { count ->
            todaysShiftsCount.text = count.toString()
        }

        viewModel.weeklyHours.observe(this) { hours ->
            weeklyHours.text = hours.toString()
        }

        viewModel.totalEarnings.observe(this) { earnings ->
            totalEarnings.text = "R${String.format("%.2f", earnings)}"
        }

        viewModel.clockStatus.observe(this) { status ->
            clockStatus.text = status
        }

        viewModel.currentShiftInfo.observe(this) { info ->
            currentShiftInfo.text = info
        }

        viewModel.clockStatusColor.observe(this) { colorRes ->
            val color = ContextCompat.getColor(this, android.R.color.transparent) // Fixed null parameter
            findViewById<CardView>(R.id.clockStatusCard).setCardBackgroundColor(color)
        }

        viewModel.isGettingLocation.observe(this) { isLoading ->
            locationProgress.visibility = if (isLoading) ProgressBar.VISIBLE else ProgressBar.GONE
            locationText.visibility = if (isLoading) TextView.VISIBLE else TextView.GONE
            clockInButton.visibility = if (isLoading) Button.GONE else Button.VISIBLE
            clockOutButton.visibility = if (isLoading) Button.GONE else Button.VISIBLE
        }

        viewModel.canClockIn.observe(this) { canClockIn ->
            clockInButton.visibility = if (canClockIn) Button.VISIBLE else Button.GONE
        }

        viewModel.canClockOut.observe(this) { canClockOut ->
            clockOutButton.visibility = if (canClockOut) Button.VISIBLE else Button.GONE
        }

        viewModel.todaysShifts.observe(this) { shifts ->
            adapter.submitList(shifts)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadDashboardData() {
        viewModel.loadDashboardData()
    }

    private fun onClockInClicked() {
        viewModel.clockIn()
    }

    private fun onClockOutClicked() {
        viewModel.clockOut()
    }

    private fun onMyShiftsClicked() {
        val intent = Intent(this, SecurityShiftsActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onScheduleClicked() {
        Toast.makeText(this, "View your upcoming schedule", Toast.LENGTH_SHORT).show()
    }

    private fun onTimeSheetClicked() {
        Toast.makeText(this, "View your time sheet and hours", Toast.LENGTH_SHORT).show()
    }

    private fun onReportsClicked() {
        Toast.makeText(this, "Generate security reports", Toast.LENGTH_SHORT).show()
    }
}