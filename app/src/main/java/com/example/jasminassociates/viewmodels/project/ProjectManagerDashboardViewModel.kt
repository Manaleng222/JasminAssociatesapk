package com.example.jasminassociates.viewmodels.project

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jasminassociates.services.DashboardService
import com.example.jasminassociates.services.ProjectService
import com.example.jasminassociates.services.ProjectTaskService
import com.example.jasminassociates.services.UserService
import com.jasminassociates.models.Project
import kotlinx.coroutines.launch

class ProjectManagerDashboardViewModel(
    private val projectService: ProjectService,
    private val userService: UserService,
    private val taskService: ProjectTaskService,
    private val dashboardService: DashboardService
) : ViewModel() {

    private val _welcomeMessage = MutableLiveData<String>()
    val welcomeMessage: LiveData<String> = _welcomeMessage

    private val _myProjectsCount = MutableLiveData<Int>()
    val myProjectsCount: LiveData<Int> = _myProjectsCount

    private val _activeTasksCount = MutableLiveData<Int>()
    val activeTasksCount: LiveData<Int> = _activeTasksCount

    private val _teamMembersCount = MutableLiveData<Int>()
    val teamMembersCount: LiveData<Int> = _teamMembersCount

    private val _overdueTasksCount = MutableLiveData<Int>()
    val overdueTasksCount: LiveData<Int> = _overdueTasksCount

    private val _recentProjects = MutableLiveData<List<ProjectDisplay>>()
    val recentProjects: LiveData<List<ProjectDisplay>> = _recentProjects

    private val _upcomingDeadlines = MutableLiveData<List<com.example.jasminassociates.models.ProjectTask>>()
    val upcomingDeadlines: LiveData<List<com.example.jasminassociates.models.ProjectTask>> = _upcomingDeadlines

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private var currentUserId: Int = 1 // In real app, get from authentication/session

    init {
        _welcomeMessage.value = "Welcome, Project Manager!"
        _myProjectsCount.value = 0
        _activeTasksCount.value = 0
        _teamMembersCount.value = 0
        _overdueTasksCount.value = 0
        _recentProjects.value = emptyList()
        _upcomingDeadlines.value = emptyList()
        _isLoading.value = false
        _errorMessage.value = ""

        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            try {
                // Load projects managed by current user
                val myProjects = projectService.getProjectsByManager(currentUserId)
                _myProjectsCount.value = myProjects.size

                // Load team members (construction workers and security personnel)
                val constructionWorkers = userService.getUsersByRole("ConstructionWorker")
                val securityPersonnel = userService.getUsersByRole("SecurityPersonnel")
                _teamMembersCount.value = constructionWorkers.size + securityPersonnel.size

                // Load tasks across all managed projects
                val allTasks = mutableListOf<com.example.jasminassociates.models.ProjectTask>()
                myProjects.forEach { project ->
                    val projectTasks = taskService.getTasksByProject(project.projectID)
                    allTasks.addAll(projectTasks)
                }

                // Calculate active tasks (not completed)
                _activeTasksCount.value = allTasks.count { it.status != "Completed" }

                // Calculate overdue tasks
                _overdueTasksCount.value = allTasks.count { task ->
                    task.dueDate != null &&
                            task.dueDate!!.isBefore(java.time.LocalDateTime.now()) &&
                            task.status != "Completed"
                }

                // Load recent projects for display
                val recentProjectsList = myProjects.take(3).map { project ->
                    val projectTasks = taskService.getTasksByProject(project.projectID)
                    val totalTasks = projectTasks.size
                    val completedTasks = projectTasks.count { it.status == "Completed" }
                    val progress = if (totalTasks > 0) (completedTasks * 100 / totalTasks) else 0

                    ProjectDisplay(
                        projectId = project.projectID,
                        projectName = project.projectName ?: "Unnamed Project",
                        status = project.status,
                        statusColor = getStatusColor(project.status),
                        progress = progress,
                        totalTasks = totalTasks,
                        completedTasks = completedTasks
                    )
                }
                _recentProjects.value = recentProjectsList

                // Load upcoming deadlines
                val upcomingDeadlinesList = dashboardService.getUpcomingDeadlines(7)
                _upcomingDeadlines.value = upcomingDeadlinesList

                // Update welcome message with user name if available
                updateWelcomeMessage()

            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Failed to load dashboard data: ${e.message}"
                // Set fallback data
                setFallbackData()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun updateWelcomeMessage() {
        try {
            val currentUser = userService.getUserById(currentUserId)
            currentUser?.let { user ->
                _welcomeMessage.value = "Welcome, ${user.firstName}!"
            }
        } catch (e: Exception) {
            // Keep default welcome message if user fetch fails
        }
    }

    private fun setFallbackData() {
        // Set some reasonable fallback values
        _myProjectsCount.value = _myProjectsCount.value ?: 0
        _activeTasksCount.value = _activeTasksCount.value ?: 0
        _teamMembersCount.value = _teamMembersCount.value ?: 0
        _overdueTasksCount.value = _overdueTasksCount.value ?: 0

        // Set fallback recent projects if none loaded
        if (_recentProjects.value.isNullOrEmpty()) {
            _recentProjects.value = listOf(
                ProjectDisplay(
                    projectId = 1,
                    projectName = "Office Building Construction",
                    status = "InProgress",
                    statusColor = "#FF9800", // Orange
                    progress = 65,
                    totalTasks = 20,
                    completedTasks = 13
                ),
                ProjectDisplay(
                    projectId = 2,
                    projectName = "Security System Installation",
                    status = "Planning",
                    statusColor = "#2196F3", // Blue
                    progress = 20,
                    totalTasks = 15,
                    completedTasks = 3
                ),
                ProjectDisplay(
                    projectId = 3,
                    projectName = "Residential Renovation",
                    status = "Completed",
                    statusColor = "#4CAF50", // Green
                    progress = 100,
                    totalTasks = 10,
                    completedTasks = 10
                )
            )
        }
    }

    private fun getStatusColor(status: String): String {
        return when (status) {
            "Completed" -> "#4CAF50" // Green
            "InProgress" -> "#FF9800" // Orange
            "Planning" -> "#2196F3" // Blue
            "OnHold" -> "#9E9E9E" // Gray
            "Cancelled" -> "#F44336" // Red
            else -> "#000000" // Black
        }
    }

    fun refresh() {
        loadDashboardData()
    }

    fun setCurrentUserId(userId: Int) {
        currentUserId = userId
        loadDashboardData()
    }

    fun getProjectStats(projectId: Int): ProjectStats {
        // This would typically fetch from a stats service
        return ProjectStats(
            totalTasks = 0,
            completedTasks = 0,
            budgetUsed = 0.0,
            timelineProgress = 0
        )
    }

    data class ProjectStats(
        val totalTasks: Int,
        val completedTasks: Int,
        val budgetUsed: Double,
        val timelineProgress: Int
    )

    fun clearError() {
        _errorMessage.value = ""
    }
    data class ProjectDisplay(
        val projectId: Int = 0,
        val projectName: String = "",
        val status: String = "",
        val statusColor: String = "#000000", // Black
        val progress: Int = 0,
        val totalTasks: Int = 0,
        val completedTasks: Int = 0
    ) {
        val progressText: String
            get() = "$progress% Complete"

        val tasksText: String
            get() = "$completedTasks/$totalTasks tasks"
    }
}

