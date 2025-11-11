package com.jasminassociates.models

import com.example.jasminassociates.models.SecurityShift

data class ShiftDisplay(
    val shiftId: Int = 0,
    val securityPersonnelName: String = "",
    val projectName: String = "",
    val shiftDetails: String = "",
    val paymentDetails: String = "",
    val status: String = "",
    val statusColor: String = ""
) {
    constructor(shift: SecurityShift) : this(
        shiftId = shift.shiftID,
        securityPersonnelName = "${shift.securityPersonnel?.firstName} ${shift.securityPersonnel?.lastName}",
        projectName = "General Assignment",
        shiftDetails = "${shift.shiftDate} | ${shift.scheduledStartTime} - ${shift.scheduledEndTime}",
        paymentDetails = "Rate: $${shift.hourlyRate}/hr | Total: $${shift.totalPay}",
        status = shift.status,
        statusColor = when (shift.status) {
            "Completed" -> "Green"
            "ClockedIn" -> "Orange"
            "Scheduled" -> "Blue"
            else -> "Gray"
        }
    )
}