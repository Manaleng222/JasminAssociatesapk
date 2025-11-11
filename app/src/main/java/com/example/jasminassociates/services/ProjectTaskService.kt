package com.example.jasminassociates.services





import com.example.jasminassociates.data.repository.TaskRepository
import com.example.jasminassociates.data.repository.getOrNull
import com.example.jasminassociates.data.repository.isSuccess
import com.example.jasminassociates.models.ProjectTask
import kotlinx.coroutines.flow.first
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.inject.Inject

class ProjectTaskService @Inject constructor(
    private val taskRepository: TaskRepository
) {

    suspend fun getAllTasks(): List<ProjectTask> {
        return taskRepository.getAllTasks().first()
    }

    suspend fun getTasksByProject(projectId: Int): List<ProjectTask> {
        return taskRepository.getTasksByProject(projectId).first()
    }

    suspend fun getTasksByUser(userId: Int): List<ProjectTask> {
        return taskRepository.getTasksByAssignee(userId).first()
    }

    suspend fun getTaskById(id: Int): ProjectTask? {
        val result = taskRepository.getTaskById(id)
        return if (result.isSuccess) {
            result.getOrNull()
        } else {
            null
        }
    }

    suspend fun createTask(task: ProjectTask): Boolean {
        return taskRepository.insertProjectTask(task).isSuccess
    }

    suspend fun updateTask(task: ProjectTask): Boolean {
        return taskRepository.updateProjectTask(task).isSuccess
    }

    suspend fun deleteTask(id: Int): Boolean {
        return taskRepository.deleteProjectTask(id).isSuccess
    }

    suspend fun updateTaskStatus(taskId: Int, status: String): Boolean {
        val task = getTaskById(taskId)
        return if (task != null) {
            val updatedTask = if (status == "Completed") {
                task.copy(
                    status = status,
                    completedDate = LocalDateTime.now()
                )
            } else {
                task.copy(status = status)
            }
            taskRepository.updateProjectTask(updatedTask).isSuccess
        } else {
            false
        }
    }

    suspend fun getOverdueTasks(): List<ProjectTask> {
        val tasks = getAllTasks()
        val now = LocalDateTime.now()
        return tasks.filter {
            it.dueDate != null &&
                    it.dueDate!!.isBefore(now) &&
                    it.status != "Completed"
        }
    }

    suspend fun getTotalEstimatedHours(projectId: Int): BigDecimal {
        val tasks = getTasksByProject(projectId)
        return tasks.sumOf { it.estimatedHours }
    }

    suspend fun getTotalActualHours(projectId: Int): BigDecimal {
        val tasks = getTasksByProject(projectId)
        return tasks.sumOf { it.actualHours }
    }
}