package com.example.jasminassociates.data.repository // Add package declaration

import com.example.jasminassociates.models.ProjectTask
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val supabaseClient: SupabaseClient
) {

    // Add this missing method
    fun getAllTasks(): Flow<List<ProjectTask>> = flow {
        try {
            val tasks = supabaseClient.postgrest.from("project_tasks")
                .select()
                .decodeList<ProjectTask>()
            emit(tasks)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun insertProjectTask(task: ProjectTask): ApiResult<ProjectTask> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("project_tasks").insert(task)
            ApiResult.Success(task)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun updateProjectTask(task: ProjectTask): ApiResult<ProjectTask> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("project_tasks").update(task) {
                filter { eq("task_id", task.taskID) }
            }
            ApiResult.Success(task)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun deleteProjectTask(taskId: Int): ApiResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("project_tasks").delete {
                filter { eq("task_id", taskId) }
            }
            ApiResult.Success(true)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun getTaskById(taskId: Int): ApiResult<ProjectTask> = withContext(Dispatchers.IO) {
        try {
            val task = supabaseClient.postgrest.from("project_tasks")
                .select {
                    filter { eq("task_id", taskId) }
                }
                .decodeSingle<ProjectTask>()
            ApiResult.Success(task)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    fun getTasksByProject(projectId: Int): Flow<List<ProjectTask>> = flow {
        try {
            val tasks = supabaseClient.postgrest.from("project_tasks")
                .select {
                    filter { eq("project_id", projectId) }
                }
                .decodeList<ProjectTask>()
            emit(tasks)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    fun getTasksByAssignee(userId: Int): Flow<List<ProjectTask>> = flow {
        try {
            val tasks = supabaseClient.postgrest.from("project_tasks")
                .select {
                    filter { eq("assigned_to", userId) }
                }
                .decodeList<ProjectTask>()
            emit(tasks)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    fun getTasksByStatus(status: String): Flow<List<ProjectTask>> = flow {
        try {
            val tasks = supabaseClient.postgrest.from("project_tasks")
                .select {
                    filter { eq("status", status) }
                }
                .decodeList<ProjectTask>()
            emit(tasks)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}