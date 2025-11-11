package com.jasminassociates.models

import com.example.jasminassociates.models.DamageReport
import com.example.jasminassociates.models.DocumentAttachment
import com.example.jasminassociates.models.EquipmentAssignment
import com.example.jasminassociates.models.EquipmentRequest
import com.example.jasminassociates.models.Invoice
import com.example.jasminassociates.models.ProjectMessage
import com.example.jasminassociates.models.ProjectTask
import com.example.jasminassociates.models.SecurityShift
import com.example.jasminassociates.models.User
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Contextual
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class Project(
    @SerialName("project_id") val projectID: Int = 0,
    @SerialName("project_name") val projectName: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("project_type") val projectType: String? = null,
    @SerialName("client_id") val clientID: Int,
    @SerialName("project_manager_id") val projectManagerID: Int,
    @SerialName("start_date") @Contextual val startDate: LocalDateTime,
    @SerialName("end_date") @Contextual val endDate: LocalDateTime? = null,
    @SerialName("estimated_budget") @Contextual val estimatedBudget: BigDecimal,
    @SerialName("actual_cost") @Contextual val actualCost: BigDecimal = BigDecimal.ZERO,
    @SerialName("status") val status: String = "Planning",
    @SerialName("location") val location: String? = null,
    @SerialName("created_date") @Contextual val createdDate: LocalDateTime = LocalDateTime.now(),
    @SerialName("client") val client: User? = null,
    @SerialName("project_manager") val projectManager: User? = null,
    @SerialName("tasks") val tasks: List<ProjectTask>? = null,
    @SerialName("invoices") val invoices: List<Invoice>? = null,
    @SerialName("messages") val messages: List<ProjectMessage>? = null,
    @SerialName("documents") val documents: List<DocumentAttachment>? = null,
    @SerialName("damage_reports") val damageReports: List<DamageReport>? = null,
    @SerialName("equipment_requests") val equipmentRequests: List<EquipmentRequest>? = null,
    @SerialName("equipment_assignments") val equipmentAssignments: List<EquipmentAssignment>? = null,
    @SerialName("security_shifts") val securityShifts: List<SecurityShift>? = null
)

