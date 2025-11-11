package com.example.jasminassociates.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jasminassociates.services.EquipmentRequestService

// Add this if you need a custom ViewModel factory
class EquipmentRequestsViewModelFactory(
    private val equipmentRequestService: EquipmentRequestService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EquipmentRequestsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EquipmentRequestsViewModel(equipmentRequestService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}