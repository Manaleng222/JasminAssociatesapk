package com.jasminassociates.viewmodels.admin

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jasminassociates.models.User
import com.example.jasminassociates.services.UserService
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
class AddEditUserViewModel(
    private val userService: UserService
) : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _generatedPassword = MutableLiveData<String>()
    val generatedPassword: LiveData<String> = _generatedPassword

    private val _generatedEmail = MutableLiveData<String>()
    val generatedEmail: LiveData<String> = _generatedEmail

    private val _pageTitle = MutableLiveData<String>()
    val pageTitle: LiveData<String> = _pageTitle

    private val _showPasswordFields = MutableLiveData<Boolean>()
    val showPasswordFields: LiveData<Boolean> = _showPasswordFields

    private val _saveResult = MutableLiveData<Result<Boolean>?>()
    val saveResult: LiveData<Result<Boolean>> = _saveResult as LiveData<Result<Boolean>>

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val roles = listOf(
        "Admin", "ProjectManager", "ConstructionWorker", "SecurityPersonnel", "Client", "Supplier"
    )

    private var editingUser: User? = null

    init {
        initializeNewUser()
    }

    fun initializeWithUser(user: User) {
        editingUser = user
        _user.value = user.copy()
        _pageTitle.value = "Edit User"
        _generatedEmail.value = user.email
        _generatedPassword.value = user.password
        _showPasswordFields.value = false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeNewUser() {
        val newUser = User(
            userID = 0,
            firstName = "",
            lastName = "",
            email = "",
            password = "",
            phoneNumber = "",
            role = "",
            hourlyRate = null,
            isActive = true,
            createdDate = LocalDateTime.now(),
            lastLogin = null,
            clientProjects = emptyList(),
            managedProjects = emptyList(),
            securityShifts = emptyList(),
            reportedDamageReports = emptyList(),
            assignedDamageReports = emptyList(),
            assignedTasks = emptyList(),
            equipmentRequests = emptyList(),
            createdInvoices = emptyList()
        )
        _user.value = newUser
        _pageTitle.value = "Add New User"
        _showPasswordFields.value = false
        generatePassword()
    }

    fun updateFirstName(firstName: String) {
        _user.value = _user.value?.copy(firstName = firstName)
    }

    fun updateLastName(lastName: String) {
        _user.value = _user.value?.copy(lastName = lastName)
    }

    fun updateEmail(email: String) {
        _user.value = _user.value?.copy(email = email)
    }

    fun updatePhoneNumber(phoneNumber: String) {
        _user.value = _user.value?.copy(phoneNumber = phoneNumber)
    }

    fun updateRole(role: String) {
        _user.value = _user.value?.copy(role = role)
        if (editingUser == null) {
            generateEmailBasedOnRole(role)
        }
    }

    fun updateHourlyRate(hourlyRate: Double) {
        _user.value = _user.value?.copy(hourlyRate = hourlyRate.toBigDecimal())
    }

    fun updateIsActive(isActive: Boolean) {
        _user.value = _user.value?.copy(isActive = isActive)
    }

    private fun generatePassword() {
        val lowercase = "abcdefghijklmnopqrstuvwxyz"
        val uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val numbers = "0123456789"
        val special = "!@#$%^&*"
        val allChars = lowercase + uppercase + numbers + special

        val password = StringBuilder(12)

        // Ensure at least one of each character type
        password.append(lowercase[Random.nextInt(lowercase.length)])
        password.append(uppercase[Random.nextInt(uppercase.length)])
        password.append(numbers[Random.nextInt(numbers.length)])
        password.append(special[Random.nextInt(special.length)])

        // Fill remaining characters
        for (i in 4 until 12) {
            password.append(allChars[Random.nextInt(allChars.length)])
        }

        // Shuffle the password
        val passwordArray = password.toString().toCharArray()
        for (i in passwordArray.size - 1 downTo 1) {
            val j = Random.nextInt(i + 1)
            val temp = passwordArray[i]
            passwordArray[i] = passwordArray[j]
            passwordArray[j] = temp
        }

        _generatedPassword.value = String(passwordArray)
        _user.value = _user.value?.copy(password = _generatedPassword.value ?: "")
    }

    private fun generateEmailBasedOnRole(role: String) {
        val prefix = when (role.lowercase()) {
            "projectmanager" -> "PM"
            "constructionworker" -> "CW"
            "securitypersonnel" -> "SG"
            "admin" -> "AD"
            "client" -> "CL"
            "supplier" -> "SP"
            else -> "USER"
        }
        val randomId = Random.nextInt(100000, 999999).toString()
        val email = "$prefix$randomId@jasmin.com"
        _generatedEmail.value = email
        _user.value = _user.value?.copy(email = email)
    }

    fun togglePasswordFields() {
        _showPasswordFields.value = !(_showPasswordFields.value ?: false)
        if (_showPasswordFields.value == false) {
            // Reset to generated password when hiding fields
            _user.value = _user.value?.copy(password = _generatedPassword.value ?: "")
        }
    }

    fun saveUser() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUser = _user.value ?: return@launch

                // Validate required fields
                if (currentUser.firstName.isBlank() ||
                    currentUser.lastName.isBlank() ||
                    currentUser.email.isBlank() ||
                    currentUser.role.isBlank()) {
                    _saveResult.value = Result.failure(Exception("Please fill all required fields"))
                    return@launch
                }

                val result = if (editingUser == null) {
                    // New user - use generated password
                    val userWithPassword = currentUser.copy(password = _generatedPassword.value ?: "")
                    userService.createUser(userWithPassword)
                } else {
                    // Existing user - keep existing password unless changed
                    userService.updateUser(currentUser)
                }

                _saveResult.value = Result.success(result)
            } catch (e: Exception) {
                e.printStackTrace()
                _saveResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun regeneratePassword() {
        generatePassword()
    }

    fun clearSaveResult() {
        _saveResult.value = null
    }
    fun initializeWithUserFromId(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = userService.getUserById(userId)
                user?.let { initializeWithUser(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}