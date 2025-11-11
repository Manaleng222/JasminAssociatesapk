package com.example.jasminassociates.viewmodels.admin

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jasminassociates.models.Project
import com.example.jasminassociates.models.SecurityShift
import com.example.jasminassociates.models.User
import com.example.jasminassociates.services.ProjectService
import com.example.jasminassociates.services.SecurityShiftService
import com.example.jasminassociates.services.UserService
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
class AdminSecurityShiftsViewModel(
    private val securityShiftService: SecurityShiftService,
    private val userService: UserService,
    private val projectService: ProjectService
) : ViewModel() {

    private val _securityPersonnel = MutableLiveData<List<User>>()
    val securityPersonnel: LiveData<List<User>> = _securityPersonnel

    private val _projects = MutableLiveData<List<Project>>()
    val projects: LiveData<List<Project>> = _projects

    private val _allShifts = MutableLiveData<List<SecurityShift>>()
    val allShifts: LiveData<List<SecurityShift>> = _allShifts

    private val _liveShifts = MutableLiveData<List<SecurityShift>>()
    val liveShifts: LiveData<List<SecurityShift>> = _liveShifts

    private val _selectedSecurityPersonnel = MutableLiveData<User?>()
    val selectedSecurityPersonnel: LiveData<User?> = _selectedSecurityPersonnel

    private val _selectedProject = MutableLiveData<Project?>()
    val selectedProject: LiveData<Project?> = _selectedProject

    private val _shiftDate = MutableLiveData<LocalDate>()
    val shiftDate: LiveData<LocalDate> = _shiftDate

    private val _startTime = MutableLiveData<LocalTime>()
    val startTime: LiveData<LocalTime> = _startTime

    private val _endTime = MutableLiveData<LocalTime>()
    val endTime: LiveData<LocalTime> = _endTime

    private val _hourlyRate = MutableLiveData<BigDecimal>()
    val hourlyRate: LiveData<BigDecimal> = _hourlyRate

    private val _location = MutableLiveData<String>()
    val location: LiveData<String> = _location

    private val _createShiftResult = MutableLiveData<Result<Boolean>?>()
    val createShiftResult: LiveData<Result<Boolean>> = _createShiftResult as LiveData<Result<Boolean>>

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        _shiftDate.value = LocalDate.now()
        _startTime.value = LocalTime.of(8, 0)
        _endTime.value = LocalTime.of(16, 0)
        _hourlyRate.value = BigDecimal.ZERO
        _location.value = ""

        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load security personnel
                val securityUsers = userService.getUsersByRole("SecurityPersonnel")
                _securityPersonnel.value = securityUsers

                // Load projects
                val projectList = projectService.getAllProjects()
                _projects.value = projectList

                // Load shifts
                loadAllShifts()
                loadLiveShifts()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadAllShifts() {
        val shifts = securityShiftService.getAllShifts()
        _allShifts.value = shifts.sortedByDescending { it.shiftDate }
    }

    private suspend fun loadLiveShifts() {
        val shifts = securityShiftService.getShiftsByStatus("ClockedIn")
        _liveShifts.value = shifts
    }

    fun setSelectedSecurityPersonnel(user: User?) {
        _selectedSecurityPersonnel.value = user
        user?.hourlyRate?.let { rate ->
            _hourlyRate.value = rate
        } ?: run {
            _hourlyRate.value = BigDecimal("18.50") // Default rate
        }
    }

    fun setSelectedProject(project: Project?) {
        _selectedProject.value = project
    }

    fun setShiftDate(date: LocalDate) {
        _shiftDate.value = date
    }

    fun setStartTime(time: LocalTime) {
        _startTime.value = time
    }

    fun setEndTime(time: LocalTime) {
        _endTime.value = time
    }

    fun setHourlyRate(rate: BigDecimal) {
        _hourlyRate.value = rate
    }

    fun setLocation(location: String) {
        _location.value = location
    }

    fun createShift() {
        viewModelScope.launch {
            try {
                val securityPersonnel = _selectedSecurityPersonnel.value
                val shiftDate = _shiftDate.value
                val startTime = _startTime.value
                val endTime = _endTime.value
                val hourlyRate = _hourlyRate.value

                if (securityPersonnel == null || shiftDate == null ||
                    startTime == null || endTime == null || hourlyRate == null) {
                    _createShiftResult.value = Result.failure(Exception("Please fill all required fields"))
                    return@launch
                }

                if (hourlyRate <= BigDecimal.ZERO) {
                    _createShiftResult.value = Result.failure(Exception("Hourly rate must be greater than 0"))
                    return@launch
                }

                if (startTime >= endTime) {
                    _createShiftResult.value = Result.failure(Exception("End time must be after start time"))
                    return@launch
                }

                val newShift = SecurityShift(
                    securityPersonnelID = securityPersonnel.userID,
                    shiftDate = LocalDateTime.of(shiftDate, LocalTime.MIDNIGHT),
                    scheduledStartTime = startTime,
                    scheduledEndTime = endTime,
                    hourlyRate = hourlyRate,
                    location = _location.value ?: "Main Site",
                    status = "Scheduled",
                    totalHours = BigDecimal.ZERO,
                    totalPay = BigDecimal.ZERO
                )

                val result = securityShiftService.createShift(newShift)

                if (result) {
                    // Reset form
                    _selectedSecurityPersonnel.value = null
                    _selectedProject.value = null
                    _hourlyRate.value = BigDecimal.ZERO
                    _location.value = ""
                    _shiftDate.value = LocalDate.now()
                    _startTime.value = LocalTime.of(8, 0)
                    _endTime.value = LocalTime.of(16, 0)

                    // Reload data
                    loadData()
                }

                _createShiftResult.value = Result.success(result)
            } catch (e: Exception) {
                e.printStackTrace()
                _createShiftResult.value = Result.failure(e)
            }
        }
    }

    fun deleteShift(shiftId: Int) {
        viewModelScope.launch {
            try {
                val result = securityShiftService.deleteShift(shiftId)
                if (result) {
                    loadData()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshLiveShifts() {
        viewModelScope.launch {
            loadLiveShifts()
        }
    }

    fun clearCreateShiftResult() {
        _createShiftResult.value = null
    }
    // In AdminSecurityShiftsViewModel.kt
    fun createShift(securityShift: SecurityShift) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = securityShiftService.createShift(securityShift)
                _createShiftResult.value = Result.success(result)
            } catch (e: Exception) {
                _createShiftResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}