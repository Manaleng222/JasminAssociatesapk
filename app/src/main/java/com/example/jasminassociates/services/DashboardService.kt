package com.example.jasminassociates.services

import com.example.jasminassociates.data.repository.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DashboardService @Inject constructor(
    private val userRepository: UserRepository,
    private val projectRepository: ProjectRepository,
    private val invoiceRepository: InvoiceRepository,
    private val taskRepository: TaskRepository,
    private val equipmentRepository: EquipmentRepository
) {

    suspend fun getDashboardStats(): DashboardStats {
        val users = userRepository.getAllUsers().first()
        val projects = projectRepository.getAllProjects().first()
        //val invoices = invoiceRepository.getAllInvoices().first()
        val tasks = taskRepository.getAllTasks().first()
        val equipment = equipmentRepository.getAllEquipment().first()

        return DashboardStats(
            totalUsers = users.size,
            activeProjects = projects.count { it.status == "Active" },
            totalSecurityPersonnel = users.count { it.role == "SecurityPersonnel" },
            totalConstructionWorkers = users.count { it.role == "ConstructionWorker" },
           // pendingInvoices = invoices.count { it.status == "Pending" },
            overdueTasks = tasks.count { it.dueDate?.isBefore(java.time.LocalDateTime.now()) == true },
            equipmentDueForMaintenance = equipment.count {
                it.nextMaintenanceDate?.isBefore(java.time.LocalDateTime.now().plusDays(7)) == true
            }
        )
    }

    suspend fun getRecentProjects(count: Int = 5): List<com.jasminassociates.models.Project> {
        val projects = projectRepository.getAllProjects().first()
        return projects.sortedByDescending { it.createdDate }.take(count)
    }

    suspend fun getUpcomingDeadlines(days: Int = 7): List<com.example.jasminassociates.models.ProjectTask> {
        val tasks = taskRepository.getAllTasks().first()
        val cutoffDate = java.time.LocalDateTime.now().plusDays(days.toLong())
        return tasks.filter {
            it.dueDate != null &&
                    it.dueDate!!.isBefore(cutoffDate) &&
                    it.status != "Completed"
        }.sortedBy { it.dueDate }
    }



    data class DashboardStats(
        val totalUsers: Int = 0,
        val activeProjects: Int = 0,
        val totalSecurityPersonnel: Int = 0,
        val totalConstructionWorkers: Int = 0,
        val pendingInvoices: Int = 0,
        val overdueTasks: Int = 0,
        val equipmentDueForMaintenance: Int = 0
    )

    data class FinancialSummary(
        val totalRevenue: Double = 0.0,
        val outstandingInvoices: Double = 0.0,
        val totalEquipmentValue: Double = 0.0
    )
}