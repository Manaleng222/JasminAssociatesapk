package com.example.jasminassociates.models

import com.jasminassociates.models.Project
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Contextual
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class User(
    @SerialName("user_id") val userID: Int = 0,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("phone_number") val phoneNumber: String,
    @SerialName("role") val role: String,
    @SerialName("hourly_rate") @Contextual val hourlyRate: BigDecimal? = null,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_date") @Contextual val createdDate: LocalDateTime = LocalDateTime.now(),
    @SerialName("last_login") @Contextual val lastLogin: LocalDateTime? = null,
    @SerialName("client_projects") val clientProjects: List<Project> = emptyList(),
    @SerialName("managed_projects") val managedProjects: List<Project> = emptyList(),
    @SerialName("security_shifts") val securityShifts: List<SecurityShift> = emptyList(),
    @SerialName("reported_damage_reports") val reportedDamageReports: List<DamageReport> = emptyList(),
    @SerialName("assigned_damage_reports") val assignedDamageReports: List<DamageReport> = emptyList(),
    @SerialName("assigned_tasks") val assignedTasks: List<ProjectTask> = emptyList(),
    @SerialName("equipment_requests") val equipmentRequests: List<EquipmentRequest> = emptyList(),
    @SerialName("created_invoices") val createdInvoices: List<Invoice> = emptyList()
)