package com.example.jasminassociates.data.repository



import com.example.jasminassociates.models.SecurityShift
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SecurityShiftRepository @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    suspend fun getAllShifts(): Flow<List<SecurityShift>> = flow {
        try {
            val shifts = supabaseClient.postgrest.from("security_shifts")
                .select()
                .decodeList<SecurityShift>()
            emit(shifts)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    suspend fun insertSecurityShift(shift: SecurityShift): ApiResult<SecurityShift> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("security_shifts").insert(shift)
            ApiResult.Success(shift)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun updateSecurityShift(shift: SecurityShift): ApiResult<SecurityShift> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("security_shifts").update(shift) {
                filter { eq("shift_id", shift.shiftID) }
            }
            ApiResult.Success(shift)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun deleteSecurityShift(shiftId: Int): ApiResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("security_shifts").delete {
                filter { eq("shift_id", shiftId) }
            }
            ApiResult.Success(true)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun getShiftById(shiftId: Int): ApiResult<SecurityShift> = withContext(Dispatchers.IO) {
        try {
            val shift = supabaseClient.postgrest.from("security_shifts")
                .select {
                    filter { eq("shift_id", shiftId) }
                }
                .decodeSingle<SecurityShift>()
            ApiResult.Success(shift)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun getShiftsBySecurityPersonnel(userId: Int): Flow<List<SecurityShift>> = flow {
        try {
            val shifts = supabaseClient.postgrest.from("security_shifts")
                .select {
                    filter { eq("security_personnel_id", userId) }
                }
                .decodeList<SecurityShift>()
            emit(shifts)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getShiftsByStatus(status: String): Flow<List<SecurityShift>> = flow {
        try {
            val shifts = supabaseClient.postgrest.from("security_shifts")
                .select {
                    filter { eq("status", status) }
                }
                .decodeList<SecurityShift>()
            emit(shifts)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getShiftsByDateRange(startDate: String, endDate: String): Flow<List<SecurityShift>> = flow {
        try {
            val shifts = supabaseClient.postgrest.from("security_shifts")
                .select {
                    filter { gte("shift_date", startDate) }
                    filter { lte("shift_date", endDate) }
                }
                .decodeList<SecurityShift>()
            emit(shifts)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}