package com.example.jasminassociates.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.jasminassociates.models.EquipmentAssignment
import com.example.jasminassociates.services.EquipmentRequestService
import com.example.jasminassociates.services.EquipmentService
import com.jasminassociates.models.Equipment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EquipmentDetailViewModel @Inject constructor(
    private val equipmentService: EquipmentService,
    private val requestService: EquipmentRequestService
) : ViewModel() {

    private val _equipment = MutableLiveData<Equipment?>()
    val equipment: LiveData<Equipment?> = _equipment

    private val _currentAssignment = MutableLiveData<EquipmentAssignment?>()
    val currentAssignment: LiveData<EquipmentAssignment?> = _currentAssignment

    private val _isAdmin = MutableLiveData<Boolean>()
    val isAdmin: LiveData<Boolean> = _isAdmin

    private val _isProjectManager = MutableLiveData<Boolean>()
    val isProjectManager: LiveData<Boolean> = _isProjectManager

    private val _hasCurrentAssignment = MutableLiveData<Boolean>()
    val hasCurrentAssignment: LiveData<Boolean> = _hasCurrentAssignment

    private var equipmentId: Int = -1
    private var requestId: Int = 0

    fun setEquipmentData(equipmentId: Int, requestId: Int = 0) {
        this.equipmentId = equipmentId
        this.requestId = requestId
        loadEquipmentDetails()
    }

    init {
        _isAdmin.value = true // Replace with actual role checking logic
        _isProjectManager.value = true // Replace with actual role checking logic
        _hasCurrentAssignment.value = false
    }

    fun loadEquipmentDetails() {
        viewModelScope.launch {
            if (equipmentId > 0) {
                val equipmentDetails = equipmentService.getEquipmentById(equipmentId)
                _equipment.value = equipmentDetails

                // Load current assignment - this would need to be implemented based on your data structure
                // For now, setting to null since equipmentAssignments might not exist in Equipment model
                _currentAssignment.value = null
                _hasCurrentAssignment.value = false
            }
        }
    }
}