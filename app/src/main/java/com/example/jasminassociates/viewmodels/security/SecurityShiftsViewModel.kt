package com.example.jasminassociates.viewmodels.security

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jasminassociates.models.ShiftDisplay
import kotlinx.coroutines.launch

class SecurityShiftsViewModel : ViewModel() {

    private val _shifts = MutableLiveData<List<ShiftDisplay>>()
    val shifts: LiveData<List<ShiftDisplay>> = _shifts

    private val _totalShifts = MutableLiveData<Int>()
    val totalShifts: LiveData<Int> = _totalShifts

    private val _completedShifts = MutableLiveData<Int>()
    val completedShifts: LiveData<Int> = _completedShifts

    private val _totalHours = MutableLiveData<Double>()
    val totalHours: LiveData<Double> = _totalHours

    fun loadShifts() {
        viewModelScope.launch {
            // Use the primary constructor with named parameters
            val dummyShifts = listOf(
                ShiftDisplay(
                    shiftId = 1,
                    securityPersonnelName = "John Doe",
                    projectName = "Security Assignment",
                    shiftDetails = "2024-01-15 | 08:00 - 16:00",
                    paymentDetails = "Rate: $150.00/hr | Total: $1200.00",
                    status = "Scheduled",
                    statusColor = "Blue"
                )
            )
            _shifts.value = dummyShifts
            _totalShifts.value = 1
            _completedShifts.value = 0
            _totalHours.value = 8.0
        }
    }

    fun clockIn(shiftId: Int) {
        // Implementation for clock in
    }

    fun clockOut(shiftId: Int) {
        // Implementation for clock out
    }

    fun exportShifts(): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            result.value = true
        }
        return result
    }
}