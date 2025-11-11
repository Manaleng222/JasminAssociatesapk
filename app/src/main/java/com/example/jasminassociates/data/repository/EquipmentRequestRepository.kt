package com.example.jasminassociates.data.repository


import com.example.jasminassociates.models.EquipmentRequest
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EquipmentRequestRepository @Inject constructor(
    private val supabaseClient: SupabaseClient
) {

    // Add this missing method
    fun getAllRequests(): Flow<List<EquipmentRequest>> = flow {
        try {
            val requests = supabaseClient.postgrest.from("equipment_requests")
                .select()
                .decodeList<EquipmentRequest>()
            emit(requests)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun insertEquipmentRequest(request: EquipmentRequest): ApiResult<EquipmentRequest> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("equipment_requests").insert(request)
            ApiResult.Success(request)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun updateEquipmentRequest(request: EquipmentRequest): ApiResult<EquipmentRequest> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("equipment_requests").update(request) {
                filter { eq("request_id", request.requestID) }
            }
            ApiResult.Success(request)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun deleteEquipmentRequest(requestId: Int): ApiResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("equipment_requests").delete {
                filter { eq("request_id", requestId) }
            }
            ApiResult.Success(true)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun getRequestById(requestId: Int): ApiResult<EquipmentRequest> = withContext(Dispatchers.IO) {
        try {
            val request = supabaseClient.postgrest.from("equipment_requests")
                .select {
                    filter { eq("request_id", requestId) }
                }
                .decodeSingle<EquipmentRequest>()
            ApiResult.Success(request)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    fun getRequestsByProject(projectId: Int): Flow<List<EquipmentRequest>> = flow {
        try {
            val requests = supabaseClient.postgrest.from("equipment_requests")
                .select {
                    filter { eq("project_id", projectId) }
                }
                .decodeList<EquipmentRequest>()
            emit(requests)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    fun getRequestsByRequester(userId: Int): Flow<List<EquipmentRequest>> = flow {
        try {
            val requests = supabaseClient.postgrest.from("equipment_requests")
                .select {
                    filter { eq("requested_by", userId) }
                }
                .decodeList<EquipmentRequest>()
            emit(requests)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    fun getRequestsByStatus(status: String): Flow<List<EquipmentRequest>> = flow {
        try {
            val requests = supabaseClient.postgrest.from("equipment_requests")
                .select {
                    filter { eq("status", status) }
                }
                .decodeList<EquipmentRequest>()
            emit(requests)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}