package com.example.jasminassociates.services

import com.example.jasminassociates.data.repository.SecurityShiftRepository
import com.example.jasminassociates.data.repository.getOrNull
import com.example.jasminassociates.data.repository.isSuccess
import com.example.jasminassociates.models.SecurityShift
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.Duration
import javax.inject.Inject

class SecurityShiftService @Inject constructor(
    private val shiftRepository: SecurityShiftRepository,
    private val locationService: LocationService
) {

    suspend fun getAllShifts(): List<SecurityShift> {
        return shiftRepository.getAllShifts().first()
    }

    suspend fun getShiftsByStatus(status: String): List<SecurityShift> {
        return shiftRepository.getShiftsByStatus(status).first()
    }

    suspend fun getLiveShifts(): List<SecurityShift> {
        return getShiftsByStatus("ClockedIn")
    }

    suspend fun createShift(shift: SecurityShift): Boolean {
        // Validate the shift object
        if (shift.securityPersonnelID <= 0) {
            return false
        }

        if (shift.hourlyRate <= java.math.BigDecimal.ZERO) {
            return false
        }

        val shiftWithDefaults = if (shift.location.isNullOrEmpty()) {
            shift.copy(location = "Main Site")
        } else {
            shift
        }

        return shiftRepository.insertSecurityShift(shiftWithDefaults).isSuccess
    }

    suspend fun updateShift(shift: SecurityShift): Boolean {
        return shiftRepository.updateSecurityShift(shift).isSuccess
    }

    suspend fun deleteShift(shiftId: Int): Boolean {
        return shiftRepository.deleteSecurityShift(shiftId).isSuccess
    }

    suspend fun getShiftById(shiftId: Int): SecurityShift? {
        return try {
            val result = shiftRepository.getShiftById(shiftId)
            if (result.isSuccess) {
                result.getOrNull()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getShiftsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): List<SecurityShift> {
        return shiftRepository.getShiftsByDateRange(
            startDate.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE),
            endDate.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)
        ).first()
    }

    suspend fun getCurrentShift(userId: Int): SecurityShift? {
        val shifts = getShiftsByUser(userId)
        return shifts.find { it.status == "ClockedIn" }
    }

    suspend fun getShiftsByUser(userId: Int): List<SecurityShift> {
        return shiftRepository.getShiftsBySecurityPersonnel(userId).first()
    }

    // Enhanced Location Methods
    suspend fun clockInWithCurrentLocation(shiftId: Int): ClockInResult {
        return try {
            val location = locationService.getCurrentLocationWithRetry()
            if (location != null) {
                val success = clockInWithLocation(
                    shiftId,
                    location.latitude,
                    location.longitude,
                    location.address
                )
                if (success) {
                    ClockInResult.Success(
                        shiftId = shiftId,
                        location = location,
                        timestamp = LocalDateTime.now()
                    )
                } else {
                    ClockInResult.Failure("Failed to update shift with location data")
                }
            } else {
                ClockInResult.Failure("Unable to retrieve current location")
            }
        } catch (e: Exception) {
            ClockInResult.Failure("Error during clock-in: ${e.message}")
        }
    }

    suspend fun clockOutWithCurrentLocation(shiftId: Int): ClockOutResult {
        return try {
            val location = locationService.getCurrentLocationWithRetry()
            if (location != null) {
                val success = clockOutWithLocation(
                    shiftId,
                    location.latitude,
                    location.longitude,
                    location.address
                )
                if (success) {
                    val shift = getShiftById(shiftId)
                    ClockOutResult.Success(
                        shiftId = shiftId,
                        location = location,
                        timestamp = LocalDateTime.now(),
                        totalHours = shift?.totalHours?.toDouble() ?: 0.0,
                        totalPay = shift?.totalPay?.toDouble() ?: 0.0
                    )
                } else {
                    ClockOutResult.Failure("Failed to update shift with location data")
                }
            } else {
                ClockOutResult.Failure("Unable to retrieve current location")
            }
        } catch (e: Exception) {
            ClockOutResult.Failure("Error during clock-out: ${e.message}")
        }
    }

    suspend fun clockInWithLocation(shiftId: Int, latitude: String, longitude: String, location: String): Boolean {
        val shift = getShiftById(shiftId)
        if (shift != null) {
            val updatedShift = shift.copy(
                actualClockIn = LocalDateTime.now(),
                clockInLatitude = latitude,
                clockInLongitude = longitude,
                clockInLocation = location,
                status = "ClockedIn"
            )
            return shiftRepository.updateSecurityShift(updatedShift).isSuccess
        }
        return false
    }

    suspend fun clockOutWithLocation(shiftId: Int, latitude: String, longitude: String, location: String): Boolean {
        val shift = getShiftById(shiftId)
        if (shift != null && shift.actualClockIn != null) {
            val actualClockOut = LocalDateTime.now()
            val timeWorked = Duration.between(shift.actualClockIn, actualClockOut)
            val totalHours = java.math.BigDecimal.valueOf(timeWorked.toHours().toDouble())
            val totalPay = totalHours * shift.hourlyRate

            val updatedShift = shift.copy(
                actualClockOut = actualClockOut,
                clockOutLatitude = latitude,
                clockOutLongitude = longitude,
                clockOutLocation = location,
                status = "Completed",
                totalHours = totalHours,
                totalPay = totalPay
            )
            return shiftRepository.updateSecurityShift(updatedShift).isSuccess
        }
        return false
    }

    // Data Classes
    sealed class ClockInResult {
        data class Success(
            val shiftId: Int,
            val location: LocationService.LocationResult,
            val timestamp: LocalDateTime
        ) : ClockInResult()

        data class Failure(val error: String) : ClockInResult()
    }

    sealed class ClockOutResult {
        data class Success(
            val shiftId: Int,
            val location: LocationService.LocationResult,
            val timestamp: LocalDateTime,
            val totalHours: Double,
            val totalPay: Double
        ) : ClockOutResult()

        data class Failure(val error: String) : ClockOutResult()
    }

    // Simplified version for basic functionality
    data class LocationValidationResult(
        val isValid: Boolean,
        val currentLocation: LocationService.LocationResult? = null,
        val targetLocation: LocationService.LocationResult? = null,
        val distance: Float? = null,
        val maxAllowedDistance: Float = 100f,
        val error: String? = null
    )

    data class ShiftLocationInfo(
        val shiftId: Int,
        val clockInLocation: String? = null,
        val clockInCoordinates: String? = null,
        val clockOutLocation: String? = null,
        val clockOutCoordinates: String? = null,
        val hasLocationData: Boolean = false,
        val clockInTime: LocalDateTime? = null,
        val clockOutTime: LocalDateTime? = null
    )
}