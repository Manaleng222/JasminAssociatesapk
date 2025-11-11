package com.example.jasminassociates.services


import com.example.jasminassociates.data.repository.ApiResult
import com.example.jasminassociates.data.repository.ProjectRepository
import com.example.jasminassociates.data.repository.isSuccess

import com.jasminassociates.models.Project
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ProjectService @Inject constructor(
    private val projectRepository: ProjectRepository
) {

    suspend fun getAllProjects(): List<Project> {
        return projectRepository.getAllProjects().first()
    }

    suspend fun getProjectById(id: Int): Project? {
        return when (val result = projectRepository.getProjectById(id)) {
            is ApiResult.Success -> result.data
            is ApiResult.Failure -> null
        }
    }

    suspend fun createProject(project: Project): Boolean {
        return projectRepository.insertProject(project).isSuccess
    }

    suspend fun updateProject(project: Project): Boolean {
        return projectRepository.updateProject(project).isSuccess
    }

    suspend fun deleteProject(id: Int): Boolean {
        return projectRepository.deleteProject(id).isSuccess
    }

    suspend fun getProjectsByStatus(status: String): List<Project> {
        return projectRepository.getProjectsByStatus(status).first()
    }

    suspend fun getProjectsByManager(managerId: Int): List<Project> {
        return projectRepository.getProjectsByManager(managerId).first()
    }

    suspend fun getProjectTotalCost(projectId: Int): Double {
        val project = getProjectById(projectId)
        return project?.actualCost?.toDouble() ?: 0.0
    }
}