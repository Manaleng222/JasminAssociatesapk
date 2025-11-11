package com.example.jasminassociates.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jasminassociates.models.EquipmentRequest
import com.example.jasminassociates.services.EquipmentRequestService
import kotlinx.coroutines.launch

class EquipmentRequestsViewModel(
    private val requestService: EquipmentRequestService
) : ViewModel() {

    private val _requests = MutableLiveData<List<EquipmentRequest>>()
    val requests: LiveData<List<EquipmentRequest>> = _requests

    private val _selectedStatus = MutableLiveData<String>()
    val selectedStatus: LiveData<String> = _selectedStatus

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _totalRequests = MutableLiveData<Int>()
    val totalRequests: LiveData<Int> = _totalRequests

    private val _pendingRequests = MutableLiveData<Int>()
    val pendingRequests: LiveData<Int> = _pendingRequests

    private val _approvedRequests = MutableLiveData<Int>()
    val approvedRequests: LiveData<Int> = _approvedRequests

    private val _fulfilledRequests = MutableLiveData<Int>()
    val fulfilledRequests: LiveData<Int> = _fulfilledRequests

    val statusFilters = listOf("All", "Pending", "Approved", "Rejected", "Fulfilled")

    init {
        _selectedStatus.value = "All"
        _isRefreshing.value = false
        _totalRequests.value = 0
        _pendingRequests.value = 0
        _approvedRequests.value = 0
        _fulfilledRequests.value = 0

        loadRequests()
    }

    fun setSelectedStatus(status: String) {
        _selectedStatus.value = status
        loadRequests()
    }

    fun loadRequests() {
        viewModelScope.launch {
            try {
                _isRefreshing.value = true

                val allRequests = requestService.getAllRequests()
                val filteredRequests = if (_selectedStatus.value == "All") {
                    allRequests
                } else {
                    allRequests.filter { it.status == _selectedStatus.value }
                }

                _requests.value = filteredRequests
                updateStats(allRequests)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun updateStats(requests: List<EquipmentRequest>) {
        _totalRequests.value = requests.size
        _pendingRequests.value = requests.count { it.status == "Pending" }
        _approvedRequests.value = requests.count { it.status == "Approved" }
        _fulfilledRequests.value = requests.count { it.status == "Fulfilled" }
    }

    fun refresh() {
        loadRequests()
    }
}