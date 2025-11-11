package com.example.jasminassociates.services


import com.example.jasminassociates.data.repository.EquipmentAssignmentRepository
import com.example.jasminassociates.data.repository.EquipmentRepository
import com.example.jasminassociates.data.repository.EquipmentRequestRepository
import com.example.jasminassociates.data.repository.getOrNull
import com.example.jasminassociates.data.repository.isSuccess
import com.example.jasminassociates.models.EquipmentAssignment
import com.example.jasminassociates.models.EquipmentRequest
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import javax.inject.Inject

class EquipmentRequestService @Inject constructor(
    private val equipmentRequestRepository: EquipmentRequestRepository,
    private val equipmentRepository: EquipmentRepository,
    private val equipmentAssignmentRepository: EquipmentAssignmentRepository

) {

    suspend fun getAllRequests(): List<EquipmentRequest> {
        return equipmentRequestRepository.getAllRequests().first()
    }

    suspend fun getRequestsByUser(userId: Int): List<EquipmentRequest> {
        return equipmentRequestRepository.getRequestsByRequester(userId).first()
    }

    suspend fun getPendingRequests(): List<EquipmentRequest> {
        return equipmentRequestRepository.getRequestsByStatus("Pending").first()
    }

    suspend fun getRequestsByProject(projectId: Int): List<EquipmentRequest> {
        return equipmentRequestRepository.getRequestsByProject(projectId).first()
    }

    suspend fun getRequestById(id: Int): EquipmentRequest? {
        return try {
            val result = equipmentRequestRepository.getRequestById(id)
            if (result.isSuccess) {
                result.getOrNull()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createRequest(request: EquipmentRequest): Boolean {
        return equipmentRequestRepository.insertEquipmentRequest(request).isSuccess
    }

    suspend fun updateRequest(request: EquipmentRequest): Boolean {
        return equipmentRequestRepository.updateEquipmentRequest(request).isSuccess
    }

    suspend fun approveRequest(requestId: Int, approvedBy: Int, notes: String? = null): Boolean {
        val request = getRequestById(requestId)
        if (request != null) {
            val updatedRequest = request.copy(
                status = "Approved",
                approvedBy = approvedBy,
                approvedDate = LocalDateTime.now(),
                adminNotes = notes ?: request.adminNotes
            )
            return equipmentRequestRepository.updateEquipmentRequest(updatedRequest).isSuccess
        }
        return false
    }

    suspend fun rejectRequest(requestId: Int, rejectedBy: Int, notes: String): Boolean {
        val request = getRequestById(requestId)
        if (request != null) {
            val updatedRequest = request.copy(
                status = "Rejected",
                approvedBy = rejectedBy,
                approvedDate = LocalDateTime.now(),
                adminNotes = notes
            )
            return equipmentRequestRepository.updateEquipmentRequest(updatedRequest).isSuccess
        }
        return false
    }

    suspend fun assignEquipment(requestId: Int, equipmentId: Int): Boolean {
        val request = getRequestById(requestId)

        // Get equipment using the repository method
        val equipmentResult = equipmentRepository.getEquipmentById(equipmentId)

        if (request != null && equipmentResult.isSuccess) {
            val equipment = equipmentResult.getOrNull()
            if (equipment != null && equipment.status == "Available") {
                // Update request
                val updatedRequest = request.copy(
                    assignedEquipmentID = equipmentId,
                    status = "Fulfilled"
                )
                val requestUpdated = equipmentRequestRepository.updateEquipmentRequest(updatedRequest).isSuccess

                // Update equipment status
                val updatedEquipment = equipment.copy(status = "InUse")
                val equipmentUpdated = equipmentRepository.updateEquipment(updatedEquipment).isSuccess

                // Create equipment assignment
                val assignment = EquipmentAssignment(
                    equipmentID = equipmentId,
                    projectID = request.projectID,
                    assignedDate = LocalDateTime.now(),
                    expectedReturnDate = request.requiredUntil,
                    assignedBy = request.approvedBy ?: 1 // Default to admin if not set
                )
                val assignmentCreated = equipmentAssignmentRepository.insertEquipmentAssignment(assignment).isSuccess

                return requestUpdated && equipmentUpdated && assignmentCreated
            }
        }
        return false
    }

    suspend fun getPendingRequestCount(): Int {
        return getPendingRequests().size
    }

    suspend fun getRequestStats(): EquipmentRequestStats {
        val requests = getAllRequests()
        return EquipmentRequestStats(
            totalRequests = requests.size,
            pendingRequests = requests.count { it.status == "Pending" },
            approvedRequests = requests.count { it.status == "Approved" },
            rejectedRequests = requests.count { it.status == "Rejected" },
            fulfilledRequests = requests.count { it.status == "Fulfilled" }
        )
    }

    data class EquipmentRequestStats(
        val totalRequests: Int = 0,
        val pendingRequests: Int = 0,
        val approvedRequests: Int = 0,
        val rejectedRequests: Int = 0,
        val fulfilledRequests: Int = 0
    )
}