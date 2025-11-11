package com.example.jasminassociates.services

import com.example.jasminassociates.data.repository.UserRepository
import com.example.jasminassociates.data.repository.getOrNull
import com.example.jasminassociates.data.repository.isSuccess
import com.example.jasminassociates.models.User
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserService @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend fun getAllUsers(): List<User> {
        return userRepository.getAllUsers().first()
    }

    suspend fun getUserById(id: Int): User? {
        return try {
            val result = userRepository.getUserById(id)
            if (result.isSuccess) {
                result.getOrNull()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createUser(user: User): Boolean {
        return userRepository.insertUser(user).isSuccess
    }

    suspend fun updateUser(user: User): Boolean {
        return userRepository.updateUser(user).isSuccess
    }

    suspend fun deleteUser(id: Int): Boolean {
        val user = getUserById(id)
        if (user != null) {
            val updatedUser = user.copy(isActive = false)
            return userRepository.updateUser(updatedUser).isSuccess
        }
        return false
    }

    suspend fun getUsersByRole(role: String): List<User> {
        return userRepository.getUsersByRole(role).first()
    }

    suspend fun getTeamMembers(): List<User> {
        val constructionWorkers = getUsersByRole("ConstructionWorker")
        val securityPersonnel = getUsersByRole("SecurityPersonnel")
        return constructionWorkers + securityPersonnel
    }

    suspend fun getActiveUsers(): List<User> {
        val users = getAllUsers()
        return users.filter { it.isActive }
    }
}