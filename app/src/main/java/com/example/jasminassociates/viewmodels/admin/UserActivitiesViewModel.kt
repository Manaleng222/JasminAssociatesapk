package com.example.jasminassociates.viewmodels.admin

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jasminassociates.models.User
import com.example.jasminassociates.services.SecurityShiftService
import com.example.jasminassociates.services.UserService
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class UserActivitiesViewModel(
    private val userService: UserService,
    private val securityShiftService: SecurityShiftService
) : ViewModel() {

    private val _users = MutableLiveData<List<UserDisplay>>()
    val users: LiveData<List<UserDisplay>> = _users

    private val _allUsers = MutableLiveData<List<User>>()
    val allUsers: LiveData<List<User>> = _allUsers

    private val _activities = MutableLiveData<List<ActivityDisplay>>()
    val activities: LiveData<List<ActivityDisplay>> = _activities

    private val _selectedUser = MutableLiveData<UserDisplay?>()
    val selectedUser: LiveData<UserDisplay?> = _selectedUser

    private val _selectedUserDetails = MutableLiveData<User?>()
    val selectedUserDetails: LiveData<User?> = _selectedUserDetails

    private val _selectedRole = MutableLiveData<String>()
    val selectedRole: LiveData<String> = _selectedRole

    private val _startDate = MutableLiveData<LocalDateTime>()
    val startDate: LiveData<LocalDateTime> = _startDate

    private val _endDate = MutableLiveData<LocalDateTime>()
    val endDate: LiveData<LocalDateTime> = _endDate

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _showUserTable = MutableLiveData<Boolean>()
    val showUserTable: LiveData<Boolean> = _showUserTable

    val roles = listOf(
        "All", "SecurityPersonnel", "ConstructionWorker", "ProjectManager", "Client", "Admin", "Supplier"
    )

    init {
        _selectedRole.value = "All"
        _startDate.value = LocalDateTime.now().minusDays(7)
        _endDate.value = LocalDateTime.now()
        _showUserTable.value = true

        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val usersList = userService.getAllUsers()

                // Load users for dropdown
                val userDisplays = usersList.map { UserDisplay(it) }
                _users.value = userDisplays

                // Load all users for table
                _allUsers.value = usersList

                loadActivities()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setSelectedUser(user: UserDisplay?) {
        _selectedUser.value = user
        viewModelScope.launch {
            if (user != null) {
                loadSelectedUserDetails(user.userId)
                loadActivities()
            } else {
                _selectedUserDetails.value = null
            }
        }
    }

    fun setSelectedRole(role: String) {
        _selectedRole.value = role
        loadActivities()
    }

    fun setStartDate(date: LocalDateTime) {
        _startDate.value = date
        loadActivities()
    }

    fun setEndDate(date: LocalDateTime) {
        _endDate.value = date
        loadActivities()
    }

    fun setShowUserTable(show: Boolean) {
        _showUserTable.value = show
        if (show) {
            _selectedUser.value = null
            _selectedUserDetails.value = null
        }
    }

    private suspend fun loadSelectedUserDetails(userId: Int) {
        try {
            val userDetails = userService.getUserById(userId)
            _selectedUserDetails.value = userDetails
        } catch (e: Exception) {
            e.printStackTrace()
            _selectedUserDetails.value = null
        }
    }

    private fun loadActivities() {
        viewModelScope.launch {
            try {
                val activitiesList = mutableListOf<ActivityDisplay>()
                val startDate = _startDate.value
                val endDate = _endDate.value
                val selectedUser = _selectedUser.value
                val selectedRole = _selectedRole.value

                if (startDate == null || endDate == null) return@launch

                if (selectedUser != null) {
                    // Load activities for specific user
                    val shifts = securityShiftService.getShiftsByUser(selectedUser.userId)
                    val filteredShifts = shifts.filter {
                        it.shiftDate.isAfter(startDate) && it.shiftDate.isBefore(endDate)
                    }

                    filteredShifts.forEach { shift ->
                        activitiesList.add(
                            ActivityDisplay(
                                userId = selectedUser.userId,
                                activityType = "Security Shift",
                                userName = "${selectedUser.fullName}",
                                description = "Shift at ${shift.location} | Hours: ${shift.totalHours}",
                                timestamp = shift.shiftDate,
                                status = shift.status,
                                statusColor = if (shift.status == "Completed") "#4CAF50" else "#FF9800"
                            )
                        )
                    }
                } else {
                    // Load all activities filtered by role
                    val allShifts = securityShiftService.getAllShifts()
                    val filteredShifts = allShifts.filter {
                        it.shiftDate.isAfter(startDate) && it.shiftDate.isBefore(endDate)
                    }

                    filteredShifts.forEach { shift ->
                        // In a real app, you would load the user details for each shift
                        if (selectedRole == "All" || selectedRole == "SecurityPersonnel") {
                            activitiesList.add(
                                ActivityDisplay(
                                    userId = shift.securityPersonnelID,
                                    activityType = "Security Shift",
                                    userName = "Security Personnel #${shift.securityPersonnelID}",
                                    description = "Shift at ${shift.location} | Hours: ${shift.totalHours}",
                                    timestamp = shift.shiftDate,
                                    status = shift.status,
                                    statusColor = if (shift.status == "Completed") "#4CAF50" else "#FF9800"
                                )
                            )
                        }
                    }
                }

                _activities.value = activitiesList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshUsers() {
        viewModelScope.launch {
            try {
                val usersList = userService.getAllUsers()
                _allUsers.value = usersList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshActivities() {
        loadActivities()
    }
    // Add these to the UserActivitiesViewModel class file
    data class UserDisplay(
        val userId: Int,
        val fullName: String,
        val role: String,
        val email: String
    ) {
        constructor(user: com.example.jasminassociates.models.User) : this(
            userId = user.userID,
            fullName = "${user.firstName} ${user.lastName}".trim(),
            role = user.role,
            email = user.email ?: "No email"
        )

        override fun toString(): String = fullName
    }

    data class ActivityDisplay(
        val userId: Int,
        val activityType: String,
        val userName: String,
        val description: String,
        val timestamp: java.time.LocalDateTime,
        val status: String,
        val statusColor: String
    )
}