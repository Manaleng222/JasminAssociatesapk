package com.example.jasminassociates.viewmodels.admin

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jasminassociates.services.SecurityShiftService

import com.example.jasminassociates.viewmodels.security.ShiftDisplay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime

class SecurityShiftsViewModel(
    private val securityShiftService: SecurityShiftService
) : ViewModel() {

    private val _shifts = MutableLiveData<List<ShiftDisplay>>()
    val shifts: LiveData<List<ShiftDisplay>> = _shifts

    private val _totalShifts = MutableLiveData<Int>()
    val totalShifts: LiveData<Int> = _totalShifts

    private val _completedShifts = MutableLiveData<Int>()
    val completedShifts: LiveData<Int> = _completedShifts

    private val _totalHours = MutableLiveData<Double>()
    val totalHours: LiveData<Double> = _totalHours

    private val _clockInResult = MutableLiveData<Boolean>()
    val clockInResult: LiveData<Boolean> = _clockInResult

    private val _clockOutResult = MutableLiveData<Boolean>()
    val clockOutResult: LiveData<Boolean> = _clockOutResult

    init {
        loadShifts()
    }

    fun loadShifts() {
        viewModelScope.launch {
            try {
                val shiftsList = securityShiftService.getAllShifts()
                val shiftDisplays = shiftsList.map { ShiftDisplay(it) }
                _shifts.value = shiftDisplays

                _totalShifts.value = shiftsList.size
                _completedShifts.value = shiftsList.count { it.status == "Completed" }
                _totalHours.value = shiftsList
                    .filter { it.totalHours > 0.0.toBigDecimal() }
                    .sumOf { it.totalHours.toDouble() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun clockIn(shiftId: Int) {
        viewModelScope.launch {
            try {
                val shift = securityShiftService.getShiftById(shiftId)
                if (shift != null) {
                    shift.actualClockIn = LocalDateTime.now()
                    shift.status = "ClockedIn"
                    val result = securityShiftService.updateShift(shift)
                    _clockInResult.value = result
                    if (result) loadShifts()
                } else {
                    _clockInResult.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _clockInResult.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun clockOut(shiftId: Int) {
        viewModelScope.launch {
            try {
                val shift = securityShiftService.getShiftById(shiftId)
                if (shift != null && shift.actualClockIn != null) {
                    shift.actualClockOut = LocalDateTime.now()
                    val hoursWorked = Duration.between(shift.actualClockIn, shift.actualClockOut).toHours()
                    shift.totalHours = hoursWorked.toBigDecimal()
                    shift.totalPay = shift.totalHours * shift.hourlyRate
                    shift.status = "Completed"
                    val result = securityShiftService.updateShift(shift)
                    _clockOutResult.value = result
                    if (result) loadShifts()
                } else {
                    _clockOutResult.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _clockOutResult.value = false
            }
        }
    }
}