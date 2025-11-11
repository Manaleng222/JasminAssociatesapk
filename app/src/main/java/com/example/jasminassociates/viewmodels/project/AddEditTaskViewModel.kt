package com.example.jasminassociates.viewmodels.project



import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jasminassociates.models.ProjectTask
import com.example.jasminassociates.models.User
import com.example.jasminassociates.services.ProjectTaskService
import com.example.jasminassociates.services.UserService
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate

class AddEditTaskViewModel(
    private val taskService: ProjectTaskService,
    private val userService: UserService
) : ViewModel() {

    private val _taskId = MutableLiveData<Int>()
    val taskId: LiveData<Int> = _taskId

    private val _projectId = MutableLiveData<Int>()
    val projectId: LiveData<Int> = _projectId

    private val _taskName = MutableLiveData<String>()
    val taskName: LiveData<String> = _taskName

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    private val _startDate = MutableLiveData<LocalDate>()
    val startDate: LiveData<LocalDate> = _startDate

    private val _dueDate = MutableLiveData<LocalDate>()
    val dueDate: LiveData<LocalDate> = _dueDate

    private val _selectedPriority = MutableLiveData<String>()
    val selectedPriority: LiveData<String> = _selectedPriority

    private val _estimatedHours = MutableLiveData<BigDecimal>()
    val estimatedHours: LiveData<BigDecimal> = _estimatedHours

    private val _selectedTeamMember = MutableLiveData<User?>()
    val selectedTeamMember: LiveData<User?> = _selectedTeamMember

    private val _validationMessage = MutableLiveData<String>()
    val validationMessage: LiveData<String> = _validationMessage

    private val _teamMembers = MutableLiveData<List<User>>()
    val teamMembers: LiveData<List<User>> = _teamMembers

    private val _saveResult = MutableLiveData<Result<Boolean>?>()
    val saveResult: LiveData<Result<Boolean>?> = _saveResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _priorityLevels = MutableLiveData<List<String>>()
    val priorityLevels: LiveData<List<String>> = _priorityLevels

    private val _pageTitle = MutableLiveData<String>()
    val pageTitle: LiveData<String> = _pageTitle

    init {
        _taskId.value = 0
        _projectId.value = 0
        _taskName.value = ""
        _description.value = ""
        _startDate.value = LocalDate.now()
        _dueDate.value = LocalDate.now().plusDays(7)
        _selectedPriority.value = "Medium"
        _estimatedHours.value = BigDecimal.ZERO
        _validationMessage.value = ""
        _pageTitle.value = "Add New Task"
        _priorityLevels.value = listOf("Low", "Medium", "High", "Critical")
    }

    fun setTaskId(taskId: Int) {
        _taskId.value = taskId
        if (taskId > 0) {
            _pageTitle.value = "Edit Task"
        }
    }

    fun setProjectId(projectId: Int) {
        _projectId.value = projectId
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load team members
                val teamMembersList = userService.getTeamMembers()
                _teamMembers.value = teamMembersList

                // If editing, load existing task
                val currentTaskId = _taskId.value ?: 0
                if (currentTaskId > 0) {
                    loadTask(currentTaskId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadTask(taskId: Int) {
        try {
            val task = taskService.getTaskById(taskId)
            if (task != null) {
                _taskName.value = task.taskName ?: ""
                _description.value = task.description ?: ""
                _startDate.value = task.startDate?.toLocalDate() ?: LocalDate.now()
                _dueDate.value = task.dueDate?.toLocalDate() ?: LocalDate.now().plusDays(7)
                _selectedPriority.value = task.priority
                _estimatedHours.value = task.estimatedHours

                if (task.assignedTo != null) {
                    val teamMembersList = _teamMembers.value ?: emptyList()
                    _selectedTeamMember.value = teamMembersList.firstOrNull { it.userID == task.assignedTo }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateTaskName(name: String) {
        _taskName.value = name
        clearValidation()
    }

    fun updateDescription(desc: String) {
        _description.value = desc
    }

    fun updateStartDate(date: LocalDate) {
        _startDate.value = date
        // Update DueDate minimum
        val currentDueDate = _dueDate.value
        if (currentDueDate != null && currentDueDate.isBefore(date)) {
            _dueDate.value = date.plusDays(1)
        }
    }

    fun updateDueDate(date: LocalDate) {
        _dueDate.value = date
        clearValidation()
    }

    fun updateSelectedPriority(priority: String) {
        _selectedPriority.value = priority
    }

    fun updateEstimatedHours(hours: Double) {
        _estimatedHours.value = hours.toBigDecimal()
        clearValidation()
    }

    fun updateSelectedTeamMember(member: User?) {
        _selectedTeamMember.value = member
    }

    fun saveTask() {
        viewModelScope.launch {
            try {
                if (!validateForm()) {
                    _saveResult.value = Result.failure(Exception(_validationMessage.value))
                    return@launch
                }

                val task = ProjectTask(
                    taskID = _taskId.value ?: 0,
                    projectID = _projectId.value ?: 0,
                    taskName = _taskName.value ?: "",
                    description = _description.value ?: "",
                    assignedTo = _selectedTeamMember.value?.userID,
                    startDate = _startDate.value?.atStartOfDay(),
                    dueDate = _dueDate.value?.atStartOfDay(),
                    priority = _selectedPriority.value ?: "Medium",
                    estimatedHours = _estimatedHours.value ?: BigDecimal.ZERO,
                    status = "NotStarted",
                    actualHours = BigDecimal.ZERO,
                    project = null,
                    assignedUser = null,
                    equipmentAssignments = emptyList()
                )

                val result = if (_taskId.value ?: 0 > 0) {
                    taskService.updateTask(task)
                } else {
                    taskService.createTask(task)
                }

                _saveResult.value = Result.success(result)
            } catch (e: Exception) {
                e.printStackTrace()
                _validationMessage.value = "Error: ${e.message}"
                _saveResult.value = Result.failure(e)
            }
        }
    }

    private fun validateForm(): Boolean {
        val currentTaskName = _taskName.value
        val currentStartDate = _startDate.value
        val currentDueDate = _dueDate.value

        if (currentTaskName.isNullOrBlank()) {
            _validationMessage.value = "Task name is required."
            return false
        }

        if (currentDueDate != null && currentStartDate != null && currentDueDate.isBefore(currentStartDate)) {
            _validationMessage.value = "Due date cannot be before start date."
            return false
        }

        if (currentDueDate != null && currentDueDate.isBefore(LocalDate.now())) {
            _validationMessage.value = "Due date cannot be in the past."
            return false
        }

        _validationMessage.value = ""
        return true
    }

    private fun clearValidation() {
        if (_validationMessage.value?.isNotEmpty() == true) {
            _validationMessage.value = ""
        }
    }

    fun clearSaveResult() {
        _saveResult.value = null
    }
}