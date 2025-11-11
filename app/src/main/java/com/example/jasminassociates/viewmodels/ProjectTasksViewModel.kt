package com.example.jasminassociates.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jasminassociates.models.ProjectTask
import com.example.jasminassociates.services.ProjectTaskService
import kotlinx.coroutines.launch

class ProjectTasksViewModel(
    private val taskService: ProjectTaskService,
    private val projectId: Int
) : ViewModel() {

    private val _tasks = MutableLiveData<List<ProjectTask>>()
    val tasks: LiveData<List<ProjectTask>> = _tasks

    private val _projectName = MutableLiveData<String>()
    val projectName: LiveData<String> = _projectName

    private val _taskSummary = MutableLiveData<String>()
    val taskSummary: LiveData<String> = _taskSummary

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        _projectName.value = ""
        _taskSummary.value = ""
        _isLoading.value = false
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val tasksList = taskService.getTasksByProject(projectId)
                _tasks.value = tasksList
                updateTaskSummary(tasksList)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateTaskStatus(taskId: Int, status: String) {
        viewModelScope.launch {
            try {
                val success = taskService.updateTaskStatus(taskId, status)
                if (success) {
                    loadTasks() // Refresh the list
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateTaskSummary(tasks: List<ProjectTask>) {
        val totalTasks = tasks.size
        val completedTasks = tasks.count { it.status == "Completed" }
        val inProgressTasks = tasks.count { it.status == "InProgress" }
        val notStartedTasks = tasks.count { it.status == "NotStarted" }

        _taskSummary.value = "Total: $totalTasks | Completed: $completedTasks | In Progress: $inProgressTasks | Not Started: $notStartedTasks"
    }

    fun setProjectName(name: String) {
        _projectName.value = name
    }

    fun refresh() {
        loadTasks()
    }
}