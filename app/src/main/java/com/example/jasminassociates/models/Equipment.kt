package com.jasminassociates.models

import com.example.jasminassociates.models.EquipmentAssignment
import com.example.jasminassociates.models.EquipmentRequest
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Contextual
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class Equipment(
    @SerialName("equipment_id") val equipmentID: Int = 0,
    @SerialName("equipment_name") val equipmentName: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("equipment_type") val equipmentType: String? = null,
    @SerialName("serial_number") val serialNumber: String? = null,
    @SerialName("purchase_date") @Contextual val purchaseDate: LocalDateTime? = null,
    @SerialName("purchase_cost") @Contextual val purchaseCost: BigDecimal,
    @SerialName("current_value") @Contextual val currentValue: BigDecimal,
    @SerialName("status") val status: String = "Available",
    @SerialName("hourly_rental_rate") @Contextual val hourlyRentalRate: BigDecimal = BigDecimal.ZERO,
    @SerialName("maintenance_interval") val maintenanceInterval: Int? = null,
    @SerialName("last_maintenance_date") @Contextual val lastMaintenanceDate: LocalDateTime? = null,
    @SerialName("next_maintenance_date") @Contextual val nextMaintenanceDate: LocalDateTime? = null,
    @SerialName("location") val location: String? = null,
    @SerialName("equipment_assignments") val equipmentAssignments: List<EquipmentAssignment>? = null,
    @SerialName("equipment_requests") val equipmentRequests: List<EquipmentRequest>? = null
)
