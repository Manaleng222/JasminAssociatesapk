package com.example.jasminassociates.services


import com.example.jasminassociates.data.repository.EquipmentRepository
import com.example.jasminassociates.data.repository.getOrNull
import com.example.jasminassociates.data.repository.isSuccess
import com.jasminassociates.models.Equipment

import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import javax.inject.Inject

class EquipmentService @Inject constructor(
    private val equipmentRepository: EquipmentRepository
) {

    suspend fun getAllEquipment(): List<Equipment> {
        return equipmentRepository.getAllEquipment().first()
    }

    suspend fun getEquipmentById(id: Int): Equipment? {
        return equipmentRepository.getEquipmentById(id).getOrNull()
    }

    suspend fun createEquipment(equipment: Equipment): Boolean {
        return equipmentRepository.insertEquipment(equipment).isSuccess
    }

    suspend fun updateEquipment(equipment: Equipment): Boolean {
        return equipmentRepository.updateEquipment(equipment).isSuccess
    }

    suspend fun deleteEquipment(id: Int): Boolean {
        return equipmentRepository.deleteEquipment(id).isSuccess
    }

    suspend fun getEquipmentByType(equipmentType: String): List<Equipment> {
        return equipmentRepository.getEquipmentByType(equipmentType).first()
    }

    suspend fun getAvailableEquipment(): List<Equipment> {
        return equipmentRepository.getEquipmentByStatus("Available").first()
    }

    suspend fun getEquipmentNeedingMaintenance(): List<Equipment> {
        val equipment = getAllEquipment()
        val now = LocalDateTime.now()
        return equipment.filter {
            it.nextMaintenanceDate?.isBefore(now.plusDays(7)) == true
        }
    }

    suspend fun getTotalEquipmentValue(): Double {
        val equipment = getAllEquipment()
        return equipment.sumOf { it.currentValue.toDouble() }
    }
}