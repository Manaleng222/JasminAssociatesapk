package com.example.jasminassociates.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jasminassociates.models.EquipmentRequest
import com.example.jasminassociates.services.EquipmentRequestService
import com.example.jasminassociates.services.EquipmentService
import com.example.jasminassociates.services.ProjectService
import com.jasminassociates.models.Equipment
import com.jasminassociates.models.Project
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class CreateEquipmentRequestViewModel(
    private val requestService: EquipmentRequestService,
    private val equipmentService: EquipmentService,
    private val projectService: ProjectService,
    private val preselectedEquipmentId: Int = 0
) : ViewModel() {

    private val _request = MutableLiveData<EquipmentRequest>()
    val request: LiveData<EquipmentRequest> = _request

    private val _selectedProject = MutableLiveData<Project?>()
    val selectedProject: LiveData<Project?> = _selectedProject

    private val _preselectedEquipment = MutableLiveData<Equipment?>()
    val preselectedEquipment: LiveData<Equipment?> = _preselectedEquipment

    private val _projects = MutableLiveData<List<Project>>()
    val projects: LiveData<List<Project>> = _projects

    private val _validationMessage = MutableLiveData<String>()
    val validationMessage: LiveData<String> = _validationMessage

    private val _submitResult = MutableLiveData<Boolean>()
    val submitResult: LiveData<Boolean> = _submitResult

    val equipmentTypes = listOf("Construction", "Security", "Office", "Vehicle")

    val hasPreselectedEquipment: LiveData<Boolean> = MutableLiveData<Boolean>().apply {
        value = false
    }

    val hasValidationMessage: LiveData<Boolean> = MutableLiveData<Boolean>().apply {
        value = false
    }

    init {
        _request.value = EquipmentRequest(
            requestID = 0,
            projectID = 0,
            requestedBy = 0,
            equipmentType = "",
            description = "",
            quantity = 1,
            requestDate = LocalDateTime.now(),
            requiredFrom = null,
            requiredUntil = null,
            status = "Pending",
            adminNotes = "",
            approvedBy = null,
            approvedDate = null,
            assignedEquipmentID = null,
            project = null,
            requester = null,
            approver = null,
            assignedEquipment = null
        )
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            loadProjects()
            if (preselectedEquipmentId > 0) {
                loadPreselectedEquipment(preselectedEquipmentId)
            }
        }
    }

    private suspend fun loadProjects() {
        val projectsList = projectService.getAllProjects()
        _projects.value = projectsList
    }

    private suspend fun loadPreselectedEquipment(equipmentId: Int) {
        val equipment = equipmentService.getEquipmentById(equipmentId)
        _preselectedEquipment.value = equipment
        (hasPreselectedEquipment as MutableLiveData).value = equipment != null

        if (equipment != null) {
            _request.value = _request.value?.copy(
                equipmentType = equipment.equipmentType ?: "",
                description = "Request for ${equipment.equipmentName} - ${equipment.description}"
            )
        }
    }

    fun updateEquipmentType(type: String) {
        _request.value = _request.value?.copy(equipmentType = type)
    }

    fun updateDescription(description: String) {
        _request.value = _request.value?.copy(description = description)
    }

    fun updateQuantity(quantity: Int) {
        _request.value = _request.value?.copy(quantity = quantity)
    }

    fun updateRequiredFrom(date: LocalDateTime?) {
        _request.value = _request.value?.copy(requiredFrom = date)
    }

    fun updateRequiredUntil(date: LocalDateTime?) {
        _request.value = _request.value?.copy(requiredUntil = date)
    }

    fun updateAdminNotes(notes: String) {
        _request.value = _request.value?.copy(adminNotes = notes)
    }

    fun setSelectedProject(project: Project?) {
        _selectedProject.value = project
        _request.value = _request.value?.copy(projectID = project?.projectID ?: 0)
    }

    fun submitRequest() {
        viewModelScope.launch {
            if (!validateRequest()) {
                _submitResult.value = false
                return@launch
            }

            try {
                val currentRequest = _request.value ?: return@launch
                // Set the current user as requester (replace with actual user ID)
                val requestWithUser = currentRequest.copy(requestedBy = 1)

                val success = requestService.createRequest(requestWithUser)
                _validationMessage.value = if (success) "" else "Failed to submit request. Please try again."
                _submitResult.value = success
            } catch (e: Exception) {
                _validationMessage.value = "Error submitting request: ${e.message}"
                _submitResult.value = false
            }
        }
    }

    private fun validateRequest(): Boolean {
        val currentRequest = _request.value
        _validationMessage.value = ""

        if (currentRequest?.projectID == 0) {
            _validationMessage.value = "Please select a project."
            return false
        }

        if (currentRequest?.equipmentType.isNullOrBlank()) {
            _validationMessage.value = "Please select an equipment type."
            return false
        }

        if (currentRequest?.description.isNullOrBlank()) {
            _validationMessage.value = "Please provide a description for the request."
            return false
        }

        if (currentRequest?.quantity ?: 0 <= 0) {
            _validationMessage.value = "Quantity must be at least 1."
            return false
        }

        return true
    }
}