package com.example.jasminassociates.viewmodels.project

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jasminassociates.models.Project
import com.example.jasminassociates.models.User
import com.example.jasminassociates.services.ProjectService
import com.example.jasminassociates.services.UserService
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class AddEditProjectViewModel(
    private val projectService: ProjectService,
    private val userService: UserService
) : ViewModel() {

    private val _project = MutableLiveData<Project>()
    val project: LiveData<Project> = _project

    private val _selectedClient = MutableLiveData<User?>()
    val selectedClient: LiveData<User?> = _selectedClient

    private val _selectedProjectManager = MutableLiveData<User?>()
    val selectedProjectManager: LiveData<User?> = _selectedProjectManager

    private val _clients = MutableLiveData<List<User>>()
    val clients: LiveData<List<User>> = _clients

    private val _projectManagers = MutableLiveData<List<User>>()
    val projectManagers: LiveData<List<User>> = _projectManagers

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _saveResult = MutableLiveData<Result<Boolean>?>()
    val saveResult: LiveData<Result<Boolean>> = _saveResult as LiveData<Result<Boolean>>

    val projectTypes = listOf("Residential", "Commercial", "Renovation", "Maintenance", "SecurityInstallation")
    val statusOptions = listOf("Planning", "InProgress", "OnHold", "Completed", "Cancelled")

    private val _pageTitle = MutableLiveData<String>()
    val pageTitle: LiveData<String> = _pageTitle

    private var editingProject: Project? = null

    init {
        initializeNewProject()
        loadData()
    }

    fun initializeWithProject(project: Project) {
        editingProject = project
        _project.value = project.copy()
        _pageTitle.value = "Edit Project"

        // Set selected client and project manager based on IDs
        viewModelScope.launch {
            val clientsList = _clients.value ?: emptyList()
            val projectManagersList = _projectManagers.value ?: emptyList()

            _selectedClient.value = clientsList.firstOrNull { it.userID == project.clientID }
            _selectedProjectManager.value = projectManagersList.firstOrNull { it.userID == project.projectManagerID }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeNewProject() {
        val newProject = Project(
            projectID = 0,
            projectName = "",
            description = "",
            projectType = "Residential",
            clientID = 0,
            projectManagerID = 0,
            startDate = LocalDateTime.now(),
            endDate = null,
            estimatedBudget = BigDecimal.ZERO,
            actualCost = BigDecimal.ZERO,
            status = "Planning",
            location = "",
            createdDate = LocalDateTime.now(),
            client = null,
            projectManager = null,
            tasks = emptyList(),
            invoices = emptyList(),
            messages = emptyList(),
            documents = emptyList(),
            damageReports = emptyList(),
            equipmentRequests = emptyList(),
            equipmentAssignments = emptyList(),
            securityShifts = emptyList()
        )
        _project.value = newProject
        _pageTitle.value = "Add New Project"
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load clients and project managers
                val users = userService.getAllUsers()

                val clientsList = users.filter { it.role == "Client" }
                val projectManagersList = users.filter { it.role == "ProjectManager" }

                _clients.value = clientsList
                _projectManagers.value = projectManagersList

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProjectName(name: String) {
        _project.value = _project.value?.copy(projectName = name)
    }

    fun updateDescription(description: String) {
        _project.value = _project.value?.copy(description = description)
    }

    fun updateProjectType(type: String) {
        _project.value = _project.value?.copy(projectType = type)
    }

    fun updateStartDate(date: LocalDate) {
        _project.value = _project.value?.copy(startDate = date)
    }

    fun updateEndDate(date: LocalDate) {
        _project.value = _project.value?.copy(endDate = date)
    }

    fun updateEstimatedBudget(budget: Double) {
        _project.value = _project.value?.copy(estimatedBudget = budget.toBigDecimal())
    }

    fun updateActualCost(cost: Double) {
        _project.value = _project.value?.copy(actualCost = cost.toBigDecimal())
    }

    fun updateStatus(status: String) {
        _project.value = _project.value?.copy(status = status)
    }

    fun updateLocation(location: String) {
        _project.value = _project.value?.copy(location = location)
    }

    fun setSelectedClient(client: User?) {
        _selectedClient.value = client
        _project.value = _project.value?.copy(clientID = client?.userID ?: 0)
    }

    fun setSelectedProjectManager(manager: User?) {
        _selectedProjectManager.value = manager
        _project.value = _project.value?.copy(projectManagerID = manager?.userID ?: 0)
    }

    fun saveProject() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentProject = _project.value ?: return@launch

                // Validate required fields
                if (currentProject.projectName?.isBlank() == true ||
                    currentProject.clientID == 0 ||
                    currentProject.projectManagerID == 0) {
                    _saveResult.value = Result.failure(Exception("Please fill all required fields"))
                    return@launch
                }

                // Set client and project manager IDs
                val projectWithIds = currentProject.copy(
                    clientID = _selectedClient.value?.userID ?: 0,
                    projectManagerID = _selectedProjectManager.value?.userID ?: 0
                )

                val result = if (projectWithIds.projectID == 0) {
                    projectService.createProject(projectWithIds)
                } else {
                    projectService.updateProject(projectWithIds)
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

    fun clearSaveResult() {
        _saveResult.value = null
    }
}