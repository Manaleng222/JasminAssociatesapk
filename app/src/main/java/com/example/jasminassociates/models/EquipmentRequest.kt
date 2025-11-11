package com.example.jasminassociates.models

import com.jasminassociates.models.Equipment
import com.jasminassociates.models.Project
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Contextual
import java.time.LocalDateTime

@Serializable
data class EquipmentRequest(
    @SerialName("request_id") val requestID: Int = 0,
    @SerialName("project_id") val projectID: Int,
    @SerialName("requested_by") val requestedBy: Int,
    @SerialName("equipment_type") val equipmentType: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("quantity") val quantity: Int = 1,
    @SerialName("request_date") @Contextual val requestDate: LocalDateTime = LocalDateTime.now(),
    @SerialName("required_from") @Contextual val requiredFrom: LocalDateTime? = null,
    @SerialName("required_until") @Contextual val requiredUntil: LocalDateTime? = null,
    @SerialName("status") var status: String = "Pending",
    @SerialName("admin_notes") val adminNotes: String = "",
    @SerialName("approved_by") val approvedBy: Int? = null,
    @SerialName("approved_date") @Contextual val approvedDate: LocalDateTime? = null,
    @SerialName("assigned_equipment_id") val assignedEquipmentID: Int? = null,
    @SerialName("project") val project: Project? = null,
    @SerialName("requester") val requester: User? = null,
    @SerialName("approver") val approver: User? = null,
    @SerialName("assigned_equipment") val assignedEquipment: Equipment? = null
)