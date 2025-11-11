package com.example.jasminassociates.models

import com.jasminassociates.models.Project
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Contextual
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class ProjectTask(
    @SerialName("task_id") val taskID: Int = 0,
    @SerialName("project_id") val projectID: Int,
    @SerialName("task_name") val taskName: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("assigned_to") val assignedTo: Int? = null,
    @SerialName("start_date") @Contextual val startDate: LocalDateTime? = null,
    @SerialName("due_date") @Contextual val dueDate: LocalDateTime? = null,
    @SerialName("completed_date") @Contextual val completedDate: LocalDateTime? = null,
    @SerialName("status") val status: String = "NotStarted",
    @SerialName("priority") val priority: String = "Medium",
    @SerialName("estimated_hours") @Contextual val estimatedHours: BigDecimal = BigDecimal.ZERO,
    @SerialName("actual_hours") @Contextual val actualHours: BigDecimal = BigDecimal.ZERO,
    @SerialName("project") val project: Project? = null,
    @SerialName("assigned_user") val assignedUser: User? = null,
    @SerialName("equipment_assignments") val equipmentAssignments: List<EquipmentAssignment>? = null
)