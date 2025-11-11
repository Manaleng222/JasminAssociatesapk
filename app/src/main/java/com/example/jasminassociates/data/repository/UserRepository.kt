package com.example.jasminassociates.data.repository

import com.example.jasminassociates.models.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    suspend fun insertUser(user: User): ApiResult<User> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("users").insert(user)
            ApiResult.Success(user)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun updateUser(user: User): ApiResult<User> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("users").update(user) {
                filter { eq("user_id", user.userID) }
            }
            ApiResult.Success(user)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun deleteUser(userId: Int): ApiResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("users").delete {
                filter { eq("user_id", userId) }
            }
            ApiResult.Success(true)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun getUserById(userId: Int): ApiResult<User> = withContext(Dispatchers.IO) {
        try {
            val user = supabaseClient.postgrest.from("users")
                .select {
                    filter { eq("user_id", userId) }
                }
                .decodeSingle<User>()
            ApiResult.Success(user)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    fun getAllUsers(): Flow<List<User>> = flow {
        try {
            val users = supabaseClient.postgrest.from("users")
                .select()
                .decodeList<User>()
            emit(users)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getUsersByRole(role: String): Flow<List<User>> = flow {
        try {
            val users = supabaseClient.postgrest.from("users")
                .select {
                    filter { eq("role", role) }
                }
                .decodeList<User>()
            emit(users)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getActiveUsers(): Flow<List<User>> = flow {
        try {
            val users = supabaseClient.postgrest.from("users")
                .select {
                    filter { eq("is_active", true) }
                }
                .decodeList<User>()
            emit(users)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}