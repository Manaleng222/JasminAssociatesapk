package com.example.jasminassociates.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jasminassociates.data.repository.ProjectRepository
import com.jasminassociates.models.Project
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ManageProjectsViewModel(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _projects = MutableLiveData<List<Project>>()
    val projects: LiveData<List<Project>> = _projects

    private val _selectedStatus = MutableLiveData<String>()
    val selectedStatus: LiveData<String> = _selectedStatus

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _projectCount = MutableLiveData<Int>()
    val projectCount: LiveData<Int> = _projectCount

    val statusFilters = listOf("All", "Planning", "InProgress", "OnHold", "Completed", "Cancelled")

    init {
        _selectedStatus.value = "All"
        _isLoading.value = false
        loadProjects()
    }

    fun loadProjects() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                projectRepository.getAllProjects().collect { projectsList ->
                    _projects.value = filterProjects(projectsList)
                    _projectCount.value = projectsList.size
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _projects.value = emptyList()
                _projectCount.value = 0
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setSelectedStatus(status: String) {
        _selectedStatus.value = status
        filterProjects()
    }

    fun filterProjects(status: String) {
        _selectedStatus.value = status
        filterProjects()
    }

    private fun filterProjects() {
        viewModelScope.launch {
            try {
                projectRepository.getAllProjects().collect { projectsList ->
                    val filteredProjects = filterProjects(projectsList)
                    _projects.value = filteredProjects
                    _projectCount.value = filteredProjects.size
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun filterProjects(projects: List<Project>): List<Project> {
        return if (_selectedStatus.value == "All") {
            projects
        } else {
            projects.filter { it.status == _selectedStatus.value }
        }
    }

    fun refresh() {
        loadProjects()
    }
}