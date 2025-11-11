package com.example.jasminassociates.viewmodels.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jasminassociates.models.ProjectTask
import com.example.jasminassociates.services.DashboardService
import com.example.jasminassociates.services.ProjectService
import com.example.jasminassociates.services.UserService
import com.jasminassociates.models.Project
import kotlinx.coroutines.launch

class AdminDashboardViewModel(
    private val dashboardService: DashboardService,
    private val userService: UserService,
    private val projectService: ProjectService
) : ViewModel() {

    private val _dashboardStats = MutableLiveData<DashboardService.DashboardStats>()
    val dashboardStats: LiveData<DashboardService.DashboardStats> = _dashboardStats

    private val _financialSummary = MutableLiveData<DashboardService.FinancialSummary>()
    val financialSummary: LiveData<DashboardService.FinancialSummary> = _financialSummary

    private val _recentProjects = MutableLiveData<List<Project>>()
    val recentProjects: LiveData<List<Project>> = _recentProjects

    private val _upcomingDeadlines = MutableLiveData<List<ProjectTask>>()
    val upcomingDeadlines: LiveData<List<ProjectTask>> = _upcomingDeadlines

    private val _recentUsers = MutableLiveData<List<UserDisplay>>()
    val recentUsers: LiveData<List<UserDisplay>> = _recentUsers

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load dashboard stats
                val stats = dashboardService.getDashboardStats()
                _dashboardStats.value = stats

                // Load financial summary
                val financial = dashboardService.getFinancialSummary()
                _financialSummary.value = financial

                // Load recent projects
                val projects = dashboardService.getRecentProjects()
                _recentProjects.value = projects

                // Load upcoming deadlines
                val deadlines = dashboardService.getUpcomingDeadlines()
                _upcomingDeadlines.value = deadlines

                // Load recent users
                val users = userService.getAllUsers()
                val recentUserDisplays = users.take(5).map { UserDisplay(it) }
                _recentUsers.value = recentUserDisplays

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        loadDashboardData()
    }
}