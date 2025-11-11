package com.example.jasminassociates.data.repository



import com.example.jasminassociates.models.DamageReport
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DamageReportRepository @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    suspend fun insertDamageReport(report: DamageReport): ApiResult<DamageReport> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("damage_reports").insert(report)
            ApiResult.Success(report)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun updateDamageReport(report: DamageReport): ApiResult<DamageReport> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("damage_reports").update(report) {
                filter { eq("report_id", report.reportID) }
            }
            ApiResult.Success(report)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun deleteDamageReport(reportId: Int): ApiResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("damage_reports").delete {
                filter { eq("report_id", reportId) }
            }
            ApiResult.Success(true)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun getReportById(reportId: Int): ApiResult<DamageReport> = withContext(Dispatchers.IO) {
        try {
            val report = supabaseClient.postgrest.from("damage_reports")
                .select {
                    filter { eq("report_id", reportId) }
                }
                .decodeSingle<DamageReport>()
            ApiResult.Success(report)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun getDamageReportsByProject(projectId: Int): Flow<List<DamageReport>> = flow {
        try {
            val reports = supabaseClient.postgrest.from("damage_reports")
                .select {
                    filter { eq("project_id", projectId) }
                }
                .decodeList<DamageReport>()
            emit(reports)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getDamageReportsByReporter(userId: Int): Flow<List<DamageReport>> = flow {
        try {
            val reports = supabaseClient.postgrest.from("damage_reports")
                .select {
                    filter { eq("reported_by", userId) }
                }
                .decodeList<DamageReport>()
            emit(reports)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getDamageReportsByAssignee(userId: Int): Flow<List<DamageReport>> = flow {
        try {
            val reports = supabaseClient.postgrest.from("damage_reports")
                .select {
                    filter { eq("assigned_to", userId) }
                }
                .decodeList<DamageReport>()
            emit(reports)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getDamageReportsByStatus(status: String): Flow<List<DamageReport>> = flow {
        try {
            val reports = supabaseClient.postgrest.from("damage_reports")
                .select {
                    filter { eq("status", status) }
                }
                .decodeList<DamageReport>()
            emit(reports)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}