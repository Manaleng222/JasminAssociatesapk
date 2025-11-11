package com.example.jasminassociates.data.repository



import com.jasminassociates.models.Project
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProjectRepository @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    suspend fun insertProject(project: Project): ApiResult<Project> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("projects").insert(project)
            ApiResult.Success(project)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun updateProject(project: Project): ApiResult<Project> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("projects").update(project) {
                filter { eq("project_id", project.projectID) }
            }
            ApiResult.Success(project)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun deleteProject(projectId: Int): ApiResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("projects").delete {
                filter { eq("project_id", projectId) }
            }
            ApiResult.Success(true)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun getProjectById(projectId: Int): ApiResult<Project> = withContext(Dispatchers.IO) {
        try {
            val project = supabaseClient.postgrest.from("projects")
                .select {
                    filter { eq("project_id", projectId) }
                }
                .decodeSingle<Project>()
            ApiResult.Success(project)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    fun getAllProjects(): Flow<List<Project>> = flow {
        try {
            val projects = supabaseClient.postgrest.from("projects")
                .select()
                .decodeList<Project>()
            emit(projects)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getProjectsByClient(clientId: Int): Flow<List<Project>> = flow {
        try {
            val projects = supabaseClient.postgrest.from("projects")
                .select {
                    filter { eq("client_id", clientId) }
                }
                .decodeList<Project>()
            emit(projects)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getProjectsByManager(managerId: Int): Flow<List<Project>> = flow {
        try {
            val projects = supabaseClient.postgrest.from("projects")
                .select {
                    filter { eq("project_manager_id", managerId) }
                }
                .decodeList<Project>()
            emit(projects)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getProjectsByStatus(status: String): Flow<List<Project>> = flow {
        try {
            val projects = supabaseClient.postgrest.from("projects")
                .select {
                    filter { eq("status", status) }
                }
                .decodeList<Project>()
            emit(projects)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}