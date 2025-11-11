package com.example.jasminassociates.viewmodels.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jasminassociates.services.ProjectTaskService
import com.example.jasminassociates.services.UserService

class AddEditTaskViewModelFactory(
    private val taskService: ProjectTaskService,
    private val userService: UserService
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEditTaskViewModel::class.java)) {
            return AddEditTaskViewModel(taskService, userService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}