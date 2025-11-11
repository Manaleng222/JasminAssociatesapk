package com.example.jasminassociates.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jasminassociates.services.EquipmentService
import com.jasminassociates.models.Equipment
import kotlinx.coroutines.launch

class EquipmentViewModel(
    private val equipmentService: EquipmentService
) : ViewModel() {

    private val _equipment = MutableLiveData<List<Equipment>>()
    val equipment: LiveData<List<Equipment>> = _equipment

    private val _selectedType = MutableLiveData<String>()
    val selectedType: LiveData<String> = _selectedType

    private val _selectedStatus = MutableLiveData<String>()
    val selectedStatus: LiveData<String> = _selectedStatus

    val equipmentTypes = listOf("All", "Construction", "Security", "Office", "Vehicle")
    val statusOptions = listOf("All", "Available", "InUse", "UnderMaintenance", "Retired")

    init {
        _selectedType.value = "All"
        _selectedStatus.value = "All"
        loadEquipment()
    }

    fun loadEquipment() {
        viewModelScope.launch {
            try {
                val equipmentList = equipmentService.getAllEquipment()
                _equipment.value = filterEquipment(equipmentList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setSelectedType(type: String) {
        _selectedType.value = type
        filterEquipment()
    }

    fun setSelectedStatus(status: String) {
        _selectedStatus.value = status
        filterEquipment()
    }

    private fun filterEquipment() {
        viewModelScope.launch {
            try {
                val equipmentList = equipmentService.getAllEquipment()
                _equipment.value = filterEquipment(equipmentList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun filterEquipment(equipmentList: List<Equipment>): List<Equipment> {
        return equipmentList.filter { equipment ->
            val typeMatch = _selectedType.value == "All" || equipment.equipmentType == _selectedType.value
            val statusMatch = _selectedStatus.value == "All" || equipment.status == _selectedStatus.value
            typeMatch && statusMatch
        }
    }

    fun refresh() {
        loadEquipment()
    }
}