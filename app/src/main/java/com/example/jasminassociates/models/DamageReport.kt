package com.example.jasminassociates.models

import com.jasminassociates.models.Project
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Contextual
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class DamageReport(
    @SerialName("report_id") val reportID: Int = 0,
    @SerialName("project_id") val projectID: Int,
    @SerialName("reported_by") val reportedBy: Int,
    @SerialName("report_date") @Contextual val reportDate: LocalDateTime = LocalDateTime.now(),
    @SerialName("damage_description") val damageDescription: String = "",
    @SerialName("image_paths") val imagePaths: String? = null,
    @SerialName("severity_level") val severityLevel: String? = null,
    @SerialName("estimated_repair_cost")  val estimatedRepairCost: BigDecimal,
    @SerialName("actual_repair_cost") val actualRepairCost: BigDecimal? = null,
    @SerialName("status") val status: String = "Reported",
    @SerialName("assigned_to") val assignedTo: Int? = null,
    @SerialName("completion_date")  val completionDate: LocalDateTime? = null,
    @SerialName("project") val project: Project? = null,
    @SerialName("reporter") val reporter: User? = null,
    @SerialName("assigned_worker") val assignedWorker: User? = null
)