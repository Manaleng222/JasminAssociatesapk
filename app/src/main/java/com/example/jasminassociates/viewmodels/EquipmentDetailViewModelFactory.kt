package com.example.jasminassociates.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jasminassociates.services.EquipmentRequestService
import com.example.jasminassociates.services.EquipmentService

class EquipmentDetailViewModelFactory(
    private val equipmentService: EquipmentService,
    private val requestService: EquipmentRequestService,
    private val equipmentId: Int,
    private val requestId: Int = 0
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EquipmentDetailViewModel::class.java)) {
            return EquipmentDetailViewModel(
                equipmentService = equipmentService,
                requestService = requestService,
                equipmentId = equipmentId,
                requestId = requestId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}