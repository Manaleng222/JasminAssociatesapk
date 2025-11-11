package com.example.jasminassociates.services


import com.example.jasminassociates.data.repository.ApiResult
import com.example.jasminassociates.data.repository.UserRepository
import com.example.jasminassociates.data.repository.isSuccess
import com.example.jasminassociates.models.User
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import javax.inject.Inject

class AuthService @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend fun authenticate(email: String, password: String): User? {
        // Default admin credentials
        if (email == "admin@jasmin.com" && password == "admin123") {
            return createOrGetDefaultAdmin()
        }

        // Project Manager credentials
        if (email == "pm101003@jasmin.com" && password == "PM101003") {
            return createOrGetProjectManager()
        }

        // Security Guard credentials
        if (email == "sg101001@jasmin.com" && password == "SG101001") {
            return createOrGetSecurityGuard()
        }

        val users = userRepository.getAllUsers().first()
        val user = users.find { it.email == email && it.isActive && it.password == password }

        if (user != null) {
            // Update last login
            val updatedUser = user.copy(lastLogin = LocalDateTime.now())
            userRepository.updateUser(updatedUser)
            return updatedUser
        }

        return null
    }

    private suspend fun createOrGetDefaultAdmin(): User? {
        val users = userRepository.getAllUsers().first()
        var admin = users.find { it.email == "admin@jasmin.com" && it.role == "Admin" }

        if (admin != null) {
            val updatedAdmin = admin.copy(lastLogin = LocalDateTime.now())
            userRepository.updateUser(updatedAdmin)
            return updatedAdmin
        }

        admin = User(
            firstName = "System",
            lastName = "Administrator",
            email = "admin@jasmin.com",
            password = "admin123",
            phoneNumber = "123-456-7890",
            role = "Admin",
            isActive = true,
            createdDate = LocalDateTime.now(),
            lastLogin = LocalDateTime.now()
        )
        return when (val result = userRepository.insertUser(admin)) {
            is ApiResult.Success -> result.data
            is ApiResult.Failure -> null
        }
    }

    private suspend fun createOrGetProjectManager(): User? {
        val users = userRepository.getAllUsers().first()
        var pm = users.find { it.email == "pm101003@jasmin.com" && it.role == "ProjectManager" }

        if (pm != null) {
            val updatedPm = pm.copy(lastLogin = LocalDateTime.now())
            userRepository.updateUser(updatedPm)
            return updatedPm
        }

        pm = User(
            firstName = "Project",
            lastName = "Manager",
            email = "pm101003@jasmin.com",
            password = "PM101003",
            phoneNumber = "123-456-7891",
            role = "ProjectManager",
            isActive = true,
            createdDate = LocalDateTime.now(),
            lastLogin = LocalDateTime.now()
        )
        return when (val result = userRepository.insertUser(pm)) {
            is ApiResult.Success -> result.data
            is ApiResult.Failure -> null
        }
    }

    private suspend fun createOrGetSecurityGuard(): User? {
        val users = userRepository.getAllUsers().first()
        var sg = users.find { it.email == "sg101001@jasmin.com" && it.role == "SecurityPersonnel" }

        if (sg != null) {
            val updatedSg = sg.copy(lastLogin = LocalDateTime.now())
            userRepository.updateUser(updatedSg)
            return updatedSg
        }

        sg = User(
            firstName = "Security",
            lastName = "Guard",
            email = "sg101001@jasmin.com",
            password = "SG101001",
            phoneNumber = "123-456-7892",
            role = "SecurityPersonnel",
            hourlyRate = java.math.BigDecimal("18.50"),
            isActive = true,
            createdDate = LocalDateTime.now(),
            lastLogin = LocalDateTime.now()
        )
        return when (val result = userRepository.insertUser(sg)) {
            is ApiResult.Success -> result.data
            is ApiResult.Failure -> null
        }
    }

    suspend fun changePassword(userId: Int, currentPassword: String, newPassword: String): Boolean {
        return when (val result = userRepository.getUserById(userId)) {
            is ApiResult.Success -> {
                val user = result.data
                if (user.password == currentPassword) {
                    val updatedUser = user.copy(password = newPassword)
                    userRepository.updateUser(updatedUser).isSuccess
                } else {
                    false
                }
            }
            is ApiResult.Failure -> false
        }
    }

    suspend fun resetPassword(email: String, newPassword: String): Boolean {
        val users = userRepository.getAllUsers().first()
        val user = users.find { it.email == email && it.isActive }

        if (user != null) {
            val updatedUser = user.copy(password = newPassword)
            return userRepository.updateUser(updatedUser).isSuccess
        }
        return false
    }

    suspend fun isEmailUnique(email: String, excludeUserId: Int? = null): Boolean {
        val users = userRepository.getAllUsers().first()
        return users.none { it.email == email && it.userID != excludeUserId }
    }
}