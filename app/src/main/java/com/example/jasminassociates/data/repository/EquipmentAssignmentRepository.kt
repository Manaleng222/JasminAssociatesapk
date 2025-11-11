package com.example.jasminassociates.data.repository

import com.example.jasminassociates.models.EquipmentAssignment
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.PostgrestFilterBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EquipmentAssignmentRepository @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    suspend fun insertEquipmentAssignment(assignment: EquipmentAssignment): ApiResult<EquipmentAssignment> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("equipment_assignments").insert(assignment)
            ApiResult.Success(assignment)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun updateEquipmentAssignment(assignment: EquipmentAssignment): ApiResult<EquipmentAssignment> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("equipment_assignments").update(assignment) {
                filter { eq("assignment_id", assignment.assignmentID) }
            }
            ApiResult.Success(assignment)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun deleteEquipmentAssignment(assignmentId: Int): ApiResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("equipment_assignments").delete {
                filter { eq("assignment_id", assignmentId) }
            }
            ApiResult.Success(true)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun getAssignmentById(assignmentId: Int): ApiResult<EquipmentAssignment> = withContext(Dispatchers.IO) {
        try {
            val assignment = supabaseClient.postgrest.from("equipment_assignments")
                .select {
                    filter { eq("assignment_id", assignmentId) }
                }
                .decodeSingle<EquipmentAssignment>()
            ApiResult.Success(assignment)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun getAssignmentsByEquipment(equipmentId: Int): Flow<List<EquipmentAssignment>> = flow {
        try {
            val assignments = supabaseClient.postgrest.from("equipment_assignments")
                .select {
                    filter { eq("equipment_id", equipmentId) }
                }
                .decodeList<EquipmentAssignment>()
            emit(assignments)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getAssignmentsByProject(projectId: Int): Flow<List<EquipmentAssignment>> = flow {
        try {
            val assignments = supabaseClient.postgrest.from("equipment_assignments")
                .select {
                    filter { eq("project_id", projectId) }
                }
                .decodeList<EquipmentAssignment>()
            emit(assignments)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getAssignmentsByTask(taskId: Int): Flow<List<EquipmentAssignment>> = flow {
        try {
            val assignments = supabaseClient.postgrest.from("equipment_assignments")
                .select {
                    filter { eq("task_id", taskId) }
                }
                .decodeList<EquipmentAssignment>()
            emit(assignments)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getAssignmentsByAssigner(assignerId: Int): Flow<List<EquipmentAssignment>> = flow {
        try {
            val assignments = supabaseClient.postgrest.from("equipment_assignments")
                .select {
                    filter { eq("assigned_by", assignerId) }
                }
                .decodeList<EquipmentAssignment>()
            emit(assignments)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getActiveAssignments(): Flow<List<EquipmentAssignment>> = flow {
        try {
            val assignments = supabaseClient.postgrest.from("equipment_assignments")
                .select {
                    filter { is_("actual_return_date", null) }
                }
                .decodeList<EquipmentAssignment>()
            emit(assignments)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    private fun PostgrestFilterBuilder.is_(string: String, nothing: Nothing?) {}

    suspend fun getOverdueAssignments(): Flow<List<EquipmentAssignment>> = flow {
        try {
            val assignments = supabaseClient.postgrest.from("equipment_assignments")
                .select {
                    filter { is_("actual_return_date", null) }
                    filter { lt("expected_return_date", "now()") }
                }
                .decodeList<EquipmentAssignment>()
            emit(assignments)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}