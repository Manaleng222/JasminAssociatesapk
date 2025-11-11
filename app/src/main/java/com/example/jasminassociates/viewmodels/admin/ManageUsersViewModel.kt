package com.example.jasminassociates.viewmodels.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jasminassociates.models.User
import com.example.jasminassociates.services.UserService
import kotlinx.coroutines.launch

class ManageUsersViewModel(
    private val userService: UserService
) : ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val _filteredUsers = MutableLiveData<List<User>>()
    val filteredUsers: LiveData<List<User>> = _filteredUsers

    private val _selectedRole = MutableLiveData<String>()
    val selectedRole: LiveData<String> = _selectedRole

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _deleteResult = MutableLiveData<Result<Boolean>?>()
    val deleteResult: LiveData<Result<Boolean>> = _deleteResult as LiveData<Result<Boolean>>

    val roles = listOf("All", "Admin", "ProjectManager", "ConstructionWorker", "SecurityPersonnel", "Client", "Supplier")

    init {
        _selectedRole.value = "All"
        _searchQuery.value = ""
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val usersList = userService.getAllUsers()
                _users.value = usersList
                applyFilters()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setSelectedRole(role: String) {
        _selectedRole.value = role
        applyFilters()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    private fun applyFilters() {
        val users = _users.value ?: return
        val roleFilter = _selectedRole.value
        val query = _searchQuery.value?.lowercase() ?: ""

        val filtered = users.filter { user ->
            val matchesRole = roleFilter == "All" || user.role == roleFilter
            val matchesSearch = query.isEmpty() ||
                    user.firstName.lowercase().contains(query) ||
                    user.lastName.lowercase().contains(query) ||
                    user.email.lowercase().contains(query) ||
                    user.role.lowercase().contains(query)
            matchesRole && matchesSearch
        }

        _filteredUsers.value = filtered
    }

    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            try {
                val result = userService.deleteUser(userId)
                if (result) {
                    loadUsers() // Refresh the list
                    _deleteResult.value = Result.success(true)
                } else {
                    _deleteResult.value = Result.failure(Exception("Failed to delete user"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _deleteResult.value = Result.failure(e)
            }
        }
    }

    fun clearDeleteResult() {
        _deleteResult.value = null
    }

    fun refresh() {
        loadUsers()
    }
}