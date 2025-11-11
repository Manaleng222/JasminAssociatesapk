package com.example.jasminassociates.models

import com.jasminassociates.models.Equipment
import com.jasminassociates.models.Project
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Contextual
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class EquipmentAssignment(
    @SerialName("assignment_id") val assignmentID: Int = 0,
    @SerialName("equipment_id") val equipmentID: Int,
    @SerialName("project_id") val projectID: Int,
    @SerialName("task_id") val taskID: Int? = null,
    @SerialName("assigned_date") @Contextual val assignedDate: LocalDateTime = LocalDateTime.now(),
    @SerialName("expected_return_date") @Contextual val expectedReturnDate: LocalDateTime? = null,
    @SerialName("actual_return_date") @Contextual val actualReturnDate: LocalDateTime? = null,
    @SerialName("rental_cost") @Contextual val rentalCost: BigDecimal = BigDecimal.ZERO,
    @SerialName("assigned_by") val assignedBy: Int,
    @SerialName("equipment") val equipment: Equipment? = null,
    @SerialName("project") val project: Project? = null,
    @SerialName("task") val task: ProjectTask? = null,
    @SerialName("assigner") val assigner: User? = null
)