package com.example.jasminassociates.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jasminassociates.services.EquipmentService
import com.jasminassociates.models.Equipment
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class AddEditEquipmentViewModel(
    private val equipmentService: EquipmentService,
    private val equipmentId: Int = 0
) : ViewModel() {

    private val _equipment = MutableLiveData<Equipment?>()
    val equipment: LiveData<Equipment?> = _equipment

    private val _validationMessage = MutableLiveData<String>()
    val validationMessage: LiveData<String> = _validationMessage

    private val _saveMessage = MutableLiveData<String>()
    val saveMessage: LiveData<String> = _saveMessage

    private val _saveResult = MutableLiveData<Boolean>()
    val saveResult: LiveData<Boolean> = _saveResult

    val equipmentTypes = listOf("Construction", "Security", "Office", "Vehicle")
    val statusOptions = listOf("Available", "InUse", "UnderMaintenance", "Retired")

    val pageTitle: LiveData<String> = MutableLiveData<String>().apply {
        value = if (equipmentId == 0) "Add New Equipment" else "Edit Equipment"
    }

    val saveButtonText: LiveData<String> = MutableLiveData<String>().apply {
        value = if (equipmentId == 0) "Add Equipment" else "Update Equipment"
    }

    val hasValidationMessage: LiveData<Boolean> = MutableLiveData<Boolean>().apply {
        value = false
    }

    init {
        _equipment.value = Equipment(
            equipmentID = 0,
            equipmentName = "",
            description = "",
            equipmentType = "",
            serialNumber = "",
            purchaseDate = LocalDateTime.now(),
            purchaseCost = BigDecimal.ZERO,
            currentValue = BigDecimal.ZERO,
            status = "Available",
            hourlyRentalRate = BigDecimal.ZERO,
            maintenanceInterval = 30,
            lastMaintenanceDate = null,
            nextMaintenanceDate = null,
            location = "",
            equipmentAssignments = emptyList(),
            equipmentRequests = emptyList()
        )
        loadEquipmentData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadEquipmentData() {
        viewModelScope.launch {
            if (equipmentId > 0) {
                val existingEquipment = equipmentService.getEquipmentById(equipmentId)
                if (existingEquipment != null) {
                    _equipment.value = existingEquipment
                }
            } else {
                // Set default values for new equipment
                _equipment.value = _equipment.value?.copy(
                    status = "Available",
                    purchaseDate = LocalDateTime.now(),
                    maintenanceInterval = 30,
                    hourlyRentalRate = BigDecimal.ZERO,
                    purchaseCost = BigDecimal.ZERO,
                    currentValue = BigDecimal.ZERO
                )
            }
        }
    }

    fun updateEquipmentName(name: String) {
        _equipment.value = _equipment.value?.copy(equipmentName = name)
    }

    fun updateDescription(description: String) {
        _equipment.value = _equipment.value?.copy(description = description)
    }

    fun updateEquipmentType(type: String) {
        _equipment.value = _equipment.value?.copy(equipmentType = type)
    }

    fun updateSerialNumber(serialNumber: String) {
        _equipment.value = _equipment.value?.copy(serialNumber = serialNumber)
    }

    fun updatePurchaseDate(date: LocalDateTime) {
        _equipment.value = _equipment.value?.copy(purchaseDate = date)
    }

    fun updatePurchaseCost(cost: Double) {
        _equipment.value = _equipment.value?.copy(purchaseCost = cost.toBigDecimal())
    }

    fun updateCurrentValue(value: Double) {
        _equipment.value = _equipment.value?.copy(currentValue = value.toBigDecimal())
    }

    fun updateStatus(status: String) {
        _equipment.value = _equipment.value?.copy(status = status)
    }

    fun updateHourlyRentalRate(rate: Double) {
        _equipment.value = _equipment.value?.copy(hourlyRentalRate = rate.toBigDecimal())
    }

    fun updateMaintenanceInterval(interval: Int) {
        _equipment.value = _equipment.value?.copy(maintenanceInterval = interval)
    }

    fun updateLocation(location: String) {
        _equipment.value = _equipment.value?.copy(location = location)
    }

    fun saveEquipment() {
        viewModelScope.launch {
            if (!validateEquipment()) {
                _saveResult.value = false
                return@launch
            }

            try {
                val currentEquipment = _equipment.value ?: return@launch
                val success = if (equipmentId == 0) {
                    equipmentService.createEquipment(currentEquipment)
                } else {
                    equipmentService.updateEquipment(currentEquipment)
                }

                _saveMessage.value = if (success) {
                    if (equipmentId == 0) "Equipment added successfully!" else "Equipment updated successfully!"
                } else {
                    if (equipmentId == 0) "Failed to add equipment." else "Failed to update equipment."
                }

                _saveResult.value = success
            } catch (e: Exception) {
                _saveMessage.value = "Error saving equipment: ${e.message}"
                _saveResult.value = false
            }
        }
    }

    private fun validateEquipment(): Boolean {
        val currentEquipment = _equipment.value
        _validationMessage.value = ""

        if (currentEquipment?.equipmentName.isNullOrBlank()) {
            _validationMessage.value = "Equipment name is required."
            return false
        }

        if (currentEquipment?.equipmentType.isNullOrBlank()) {
            _validationMessage.value = "Equipment type is required."
            return false
        }

        if (currentEquipment?.status.isNullOrBlank()) {
            _validationMessage.value = "Status is required."
            return false
        }

        if (currentEquipment?.purchaseCost?.compareTo(BigDecimal.ZERO) == -1) {
            _validationMessage.value = "Purchase cost cannot be negative."
            return false
        }

        if (currentEquipment?.currentValue?.compareTo(BigDecimal.ZERO) == -1) {
            _validationMessage.value = "Current value cannot be negative."
            return false
        }

        if (currentEquipment?.hourlyRentalRate?.compareTo(BigDecimal.ZERO) == -1) {
            _validationMessage.value = "Hourly rental rate cannot be negative."
            return false
        }

        return true
    }
}