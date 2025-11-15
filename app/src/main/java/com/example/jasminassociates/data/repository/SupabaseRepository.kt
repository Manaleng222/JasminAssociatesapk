package com.example.jasminassociates.data.repository



import android.util.Log
import com.example.jasminassociates.models.*
import com.jasminassociates.models.Equipment
import com.jasminassociates.models.Project
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class SupabaseRepository {
    private val client: SupabaseClient = createSupabaseClient(
       

    // User Operations
    suspend fun insertUser(user: User): ApiResult<User> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("users").insert(user)
            ApiResult.Success(user)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun updateUser(user: User): ApiResult<User> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("users").update(user) {
                filter { eq("user_id", user.userID) }
            }
            ApiResult.Success(user)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun deleteUser(userId: Int): ApiResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("users").delete {
                filter { eq("user_id", userId) }
            }
            ApiResult.Success(true)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun getUserById(userId: Int): ApiResult<User> = withContext(Dispatchers.IO) {
        try {
            val user = client.postgrest.from("users")
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
            val users = client.postgrest.from("users")
                .select()
                .decodeList<User>()
            emit(users)
        } catch (e: Exception) {
            Log.e("SupabaseRepository", "Error fetching users: ${e.message}")
            emit(emptyList())
        }
    }
    // Project Operations
    suspend fun insertProject(project: Project): ApiResult<Project> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("projects").insert(project)
            ApiResult.Success(project)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun updateProject(project: Project): ApiResult<Project> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("projects").update(project) {
                filter { eq("project_id", project.projectID) }
            }
            ApiResult.Success(project)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun deleteProject(projectId: Int): ApiResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("projects").delete {
                filter { eq("project_id", projectId) }
            }
            ApiResult.Success(true)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun getAllProjects(): Flow<List<Project>> = flow {
        try {
            val projects = client.postgrest.from("projects")
                .select()
                .decodeList<Project>()
            emit(projects)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Damage Report Operations
    suspend fun insertDamageReport(report: DamageReport): ApiResult<DamageReport> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("damage_reports").insert(report)
            ApiResult.Success(report)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun updateDamageReport(report: DamageReport): ApiResult<DamageReport> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("damage_reports").update(report) {
                filter { eq("report_id", report.reportID) }
            }
            ApiResult.Success(report)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun deleteDamageReport(reportId: Int): ApiResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("damage_reports").delete {
                filter { eq("report_id", reportId) }
            }
            ApiResult.Success(true)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun getDamageReportsByProject(projectId: Int): Flow<List<DamageReport>> = flow {
        try {
            val reports = client.postgrest.from("damage_reports")
                .select {
                    filter { eq("project_id", projectId) }
                }
                .decodeList<DamageReport>()
            emit(reports)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Equipment Operations
    suspend fun insertEquipment(equipment: Equipment): ApiResult<Equipment> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("equipment").insert(equipment)
            ApiResult.Success(equipment)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun updateEquipment(equipment: Equipment): ApiResult<Equipment> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("equipment").update(equipment) {
                filter { eq("equipment_id", equipment.equipmentID) }
            }
            ApiResult.Success(equipment)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun deleteEquipment(equipmentId: Int): ApiResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("equipment").delete {
                filter { eq("equipment_id", equipmentId) }
            }
            ApiResult.Success(true)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun getAllEquipment(): Flow<List<Equipment>> = flow {
        try {
            val equipment = client.postgrest.from("equipment")
                .select()
                .decodeList<Equipment>()
            emit(equipment)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Equipment Request Operations
    suspend fun insertEquipmentRequest(request: EquipmentRequest): ApiResult<EquipmentRequest> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("equipment_requests").insert(request)
            ApiResult.Success(request)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun updateEquipmentRequest(request: EquipmentRequest): ApiResult<EquipmentRequest> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("equipment_requests").update(request) {
                filter { eq("request_id", request.requestID) }
            }
            ApiResult.Success(request)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun deleteEquipmentRequest(requestId: Int): ApiResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("equipment_requests").delete {
                filter { eq("request_id", requestId) }
            }
            ApiResult.Success(true)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    // Project Task Operations
    suspend fun insertProjectTask(task: ProjectTask): ApiResult<ProjectTask> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("project_tasks").insert(task)
            ApiResult.Success(task)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun updateProjectTask(task: ProjectTask): ApiResult<ProjectTask> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("project_tasks").update(task) {
                filter { eq("task_id", task.taskID) }
            }
            ApiResult.Success(task)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun deleteProjectTask(taskId: Int): ApiResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("project_tasks").delete {
                filter { eq("task_id", taskId) }
            }
            ApiResult.Success(true)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    // Invoice Operations
    suspend fun insertInvoice(invoice: Invoice): ApiResult<Invoice> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("invoices").insert(invoice)
            ApiResult.Success(invoice)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun updateInvoice(invoice: Invoice): ApiResult<Invoice> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("invoices").update(invoice) {
                filter { eq("invoice_id", invoice.invoiceID) }
            }
            ApiResult.Success(invoice)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun deleteInvoice(invoiceId: Int): ApiResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("invoices").delete {
                filter { eq("invoice_id", invoiceId) }
            }
            ApiResult.Success(true)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    // Security Shift Operations
    suspend fun insertSecurityShift(shift: SecurityShift): ApiResult<SecurityShift> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("security_shifts").insert(shift)
            ApiResult.Success(shift)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun updateSecurityShift(shift: SecurityShift): ApiResult<SecurityShift> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("security_shifts").update(shift) {
                filter { eq("shift_id", shift.shiftID) }
            }
            ApiResult.Success(shift)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun deleteSecurityShift(shiftId: Int): ApiResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("security_shifts").delete {
                filter { eq("shift_id", shiftId) }
            }
            ApiResult.Success(true)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    // Document Attachment Operations
    suspend fun insertDocumentAttachment(document: DocumentAttachment): ApiResult<DocumentAttachment> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("document_attachments").insert(document)
            ApiResult.Success(document)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun deleteDocumentAttachment(documentId: Int): ApiResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("document_attachments").delete {
                filter { eq("document_id", documentId) }
            }
            ApiResult.Success(true)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    // Project Message Operations
    suspend fun insertProjectMessage(message: ProjectMessage): ApiResult<ProjectMessage> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("project_messages").insert(message)
            ApiResult.Success(message)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun deleteProjectMessage(messageId: Int): ApiResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            client.postgrest.from("project_messages").delete {
                filter { eq("message_id", messageId) }
            }
            ApiResult.Success(true)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    // Stats and Analytics
    suspend fun getProjectStats(projectId: Int): ProjectStats = withContext(Dispatchers.IO) {
        try {
            val tasks = client.postgrest.from("project_tasks")
                .select {
                    filter { eq("project_id", projectId) }
                }
                .decodeList<ProjectTask>()

            val damageReports = client.postgrest.from("damage_reports")
                .select {
                    filter { eq("project_id", projectId) }
                }
                .decodeList<DamageReport>()

            val equipmentRequests = client.postgrest.from("equipment_requests")
                .select {
                    filter { eq("project_id", projectId) }
                }
                .decodeList<EquipmentRequest>()

            ProjectStats(
                totalTasks = tasks.size,
                completedTasks = tasks.count { it.status == "Completed" },
                pendingTasks = tasks.count { it.status != "Completed" },
                totalDamageReports = damageReports.size,
                openDamageReports = damageReports.count { it.status == "Reported" },
                totalEquipmentRequests = equipmentRequests.size,
                pendingEquipmentRequests = equipmentRequests.count { it.status == "Pending" }
            )
        } catch (e: Exception) {
            Log.e("SupabaseRepository", "Error fetching project stats: ${e.message}")
            ProjectStats()
        }


    }

    data class ProjectStats(
        val totalTasks: Int = 0,
        val completedTasks: Int = 0,
        val pendingTasks: Int = 0,
        val totalDamageReports: Int = 0,
        val openDamageReports: Int = 0,
        val totalEquipmentRequests: Int = 0,
        val pendingEquipmentRequests: Int = 0
    )
}
