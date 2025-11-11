package com.example.jasminassociates.viewmodels.security

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jasminassociates.models.SecurityShift
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class SecurityGuardDashboardViewModel : ViewModel() {

    private val _welcomeMessage = MutableLiveData<String>()
    val welcomeMessage: LiveData<String> = _welcomeMessage

    private val _todaysShiftsCount = MutableLiveData<Int>()
    val todaysShiftsCount: LiveData<Int> = _todaysShiftsCount

    private val _weeklyHours = MutableLiveData<Double>()
    val weeklyHours: LiveData<Double> = _weeklyHours

    private val _totalEarnings = MutableLiveData<Double>()
    val totalEarnings: LiveData<Double> = _totalEarnings

    private val _clockStatus = MutableLiveData<String>()
    val clockStatus: LiveData<String> = _clockStatus

    private val _clockStatusColor = MutableLiveData<Int>() // Changed to Int for color resources
    val clockStatusColor: LiveData<Int> = _clockStatusColor

    private val _currentShiftInfo = MutableLiveData<String>()
    val currentShiftInfo: LiveData<String> = _currentShiftInfo

    private val _canClockIn = MutableLiveData<Boolean>()
    val canClockIn: LiveData<Boolean> = _canClockIn

    private val _canClockOut = MutableLiveData<Boolean>()
    val canClockOut: LiveData<Boolean> = _canClockOut

    private val _todaysShifts = MutableLiveData<List<SecurityShift>>() // Use SecurityShift directly
    val todaysShifts: LiveData<List<SecurityShift>> = _todaysShifts

    private val _isGettingLocation = MutableLiveData<Boolean>()
    val isGettingLocation: LiveData<Boolean> = _isGettingLocation

    private val _clockInResult = MutableLiveData<Result<Boolean>?>()
    val clockInResult: LiveData<Result<Boolean>?> = _clockInResult

    private val _clockOutResult = MutableLiveData<Result<Boolean>?>()
    val clockOutResult: LiveData<Result<Boolean>?> = _clockOutResult

    private var currentUserId: Int = 0

    init {
        _welcomeMessage.value = "Welcome, Security Guard!"
        _todaysShiftsCount.value = 0
        _weeklyHours.value = 0.0
        _totalEarnings.value = 0.0
        _clockStatus.value = "Not Clocked In"
        _clockStatusColor.value = android.R.color.darker_gray
        _currentShiftInfo.value = ""
        _canClockIn.value = true
        _canClockOut.value = false
        _isGettingLocation.value = false

        loadDashboardData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadDashboardData() {
        viewModelScope.launch {
            try {
                // For demo, use a test user ID
                currentUserId = 1

                // For now, use dummy data since we don't have real services
                _todaysShiftsCount.value = 1
                _weeklyHours.value = 8.0
                _totalEarnings.value = 1200.0

                // Create dummy shifts for display
                val dummyShifts = listOf(
                    SecurityShift(
                        shiftID = 1,
                        securityPersonnelID = currentUserId,
                        shiftDate = LocalDateTime.now(), // Added required parameter
                        scheduledStartTime = LocalTime.of(8, 0), // Fixed: Use LocalTime instead of String
                        scheduledEndTime = LocalTime.of(16, 0), // Fixed: Use LocalTime instead of String
                        hourlyRate = BigDecimal("150.00"), // Added required parameter
                        location = "Main Site",
                        status = "Scheduled"
                    )
                )
                _todaysShifts.value = dummyShifts

                // Check current clock status
                checkClockStatus()

                // Update welcome message
                _welcomeMessage.value = "Welcome, Security User!"

            } catch (e: Exception) {
                e.printStackTrace()
                // Set default values
                _todaysShiftsCount.value = 0
                _weeklyHours.value = 0.0
                _totalEarnings.value = 0.0
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun checkClockStatus() {
        try {
            // For now, simulate not clocked in
            _clockStatus.value = "NOT CLOCKED IN"
            _clockStatusColor.value = android.R.color.holo_red_light
            _currentShiftInfo.value = "Ready to clock in"
            _canClockIn.value = true
            _canClockOut.value = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun clockIn() {
        viewModelScope.launch {
            try {
                _isGettingLocation.value = true

                // Simulate clock in process
                _clockStatus.value = "CLOCKED IN"
                _clockStatusColor.value = android.R.color.holo_green_light
                _currentShiftInfo.value = "Clocked in at: ${LocalTime.now()}"
                _canClockIn.value = false
                _canClockOut.value = true

                _clockInResult.value = Result.success(true)
            } catch (e: Exception) {
                e.printStackTrace()
                _clockInResult.value = Result.failure(e)
            } finally {
                _isGettingLocation.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun clockOut() {
        viewModelScope.launch {
            try {
                _isGettingLocation.value = true

                // Simulate clock out process
                _clockStatus.value = "NOT CLOCKED IN"
                _clockStatusColor.value = android.R.color.holo_red_light
                _currentShiftInfo.value = "Ready to clock in"
                _canClockIn.value = true
                _canClockOut.value = false

                _clockOutResult.value = Result.success(true)
            } catch (e: Exception) {
                e.printStackTrace()
                _clockOutResult.value = Result.failure(e)
            } finally {
                _isGettingLocation.value = false
            }
        }
    }

    fun clearClockResults() {
        _clockInResult.value = null
        _clockOutResult.value = null
    }
}