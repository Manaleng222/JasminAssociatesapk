package com.example.jasminassociates.data.repository





import com.jasminassociates.models.Equipment
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EquipmentRepository @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    suspend fun insertEquipment(equipment: Equipment): ApiResult<Equipment> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("equipment").insert(equipment)
            ApiResult.Success(equipment)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun updateEquipment(equipment: Equipment): ApiResult<Equipment> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("equipment").update(equipment) {
                filter { eq("equipment_id", equipment.equipmentID) }
            }
            ApiResult.Success(equipment)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun deleteEquipment(equipmentId: Int): ApiResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("equipment").delete {
                filter { eq("equipment_id", equipmentId) }
            }
            ApiResult.Success(true)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun getEquipmentById(equipmentId: Int): ApiResult<Equipment> = withContext(Dispatchers.IO) {
        try {
            val equipment = supabaseClient.postgrest.from("equipment")
                .select {
                    filter { eq("equipment_id", equipmentId) }
                }
                .decodeSingle<Equipment>()
            ApiResult.Success(equipment)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    fun getAllEquipment(): Flow<List<Equipment>> = flow {
        try {
            val equipment = supabaseClient.postgrest.from("equipment")
                .select()
                .decodeList<Equipment>()
            emit(equipment)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getEquipmentByStatus(status: String): Flow<List<Equipment>> = flow {
        try {
            val equipment = supabaseClient.postgrest.from("equipment")
                .select {
                    filter { eq("status", status) }
                }
                .decodeList<Equipment>()
            emit(equipment)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getEquipmentByType(type: String): Flow<List<Equipment>> = flow {
        try {
            val equipment = supabaseClient.postgrest.from("equipment")
                .select {
                    filter { eq("equipment_type", type) }
                }
                .decodeList<Equipment>()
            emit(equipment)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}