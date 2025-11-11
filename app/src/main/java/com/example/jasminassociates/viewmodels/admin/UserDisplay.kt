package com.example.jasminassociates.viewmodels.admin

import com.example.jasminassociates.models.User

data class UserDisplay(
    val userId: Int = 0,
    val fullName: String = "",
    val role: String = "",
    val email: String = "",
    val status: String = "Active",
    val statusColor: String = "#4CAF50" // Green color for active
) {
    constructor(user: User) : this(
        userId = user.userID,
        fullName = "${user.firstName} ${user.lastName}",
        role = user.role,
        email = user.email,
        status = if (user.isActive) "Active" else "Inactive",
        statusColor = if (user.isActive) "#4CAF50" else "#F44336" // Green/Red
    )
}