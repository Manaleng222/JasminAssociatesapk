package com.example.jasminassociates.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AnalyticsRepository @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    suspend fun getProjectStats(projectId: Int): ProjectStats = withContext(Dispatchers.IO) {
        try {
            val tasks = supabaseClient.postgrest.from("project_tasks")
                .select {
                    filter { eq("project_id", projectId) }
                }
                .decodeList<com.example.jasminassociates.models.ProjectTask>()

            val damageReports = supabaseClient.postgrest.from("damage_reports")
                .select {
                    filter { eq("project_id", projectId) }
                }
                .decodeList<com.example.jasminassociates.models.DamageReport>()

            val equipmentRequests = supabaseClient.postgrest.from("equipment_requests")
                .select {
                    filter { eq("project_id", projectId) }
                }
                .decodeList<com.example.jasminassociates.models.EquipmentRequest>()

            ProjectStats(
                totalTasks = tasks.size,
                completedTasks = tasks.count { it.status == "Completed" },
                pendingTasks = tasks.count { it.status != "Completed" },
                totalDamageReports = damageReports.size,
                openDamageReports = damageReports.count { it.status == "Reported" },
                totalEquipmentRequests = equipmentRequests.size,
                pendingEquipmentRequests = equipmentRequests.count { it.status == "Pending" }
            )
        } catch (e: Exception) {
            ProjectStats()
        }
    }

    data class ProjectStats(
        val totalTasks: Int = 0,
        val completedTasks: Int = 0,
        val pendingTasks: Int = 0,
        val totalDamageReports: Int = 0,
        val openDamageReports: Int = 0,
        val totalEquipmentRequests: Int = 0,
        val pendingEquipmentRequests: Int = 0
    )
}