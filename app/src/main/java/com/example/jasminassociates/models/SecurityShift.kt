package com.example.jasminassociates.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Contextual
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalTime

@Serializable
data class SecurityShift(
    @SerialName("shift_id") val shiftID: Int = 0,
    @SerialName("security_personnel_id") val securityPersonnelID: Int,
    @SerialName("shift_date") @Contextual val shiftDate: LocalDateTime,
    @SerialName("scheduled_start_time")  val scheduledStartTime: LocalTime,
    @SerialName("scheduled_end_time")  val scheduledEndTime: LocalTime,
    @SerialName("actual_clock_in")  var actualClockIn: LocalDateTime? = null,
    @SerialName("actual_clock_out")  var actualClockOut: LocalDateTime? = null,
    @SerialName("hourly_rate")  val hourlyRate: BigDecimal,
    @SerialName("total_hours")  var totalHours: BigDecimal = BigDecimal.ZERO,
    @SerialName("total_pay")  var totalPay: BigDecimal = BigDecimal.ZERO,
    @SerialName("status") var status: String = "Scheduled",
    @SerialName("location") val location: String? = null,
    @SerialName("notes") val notes: String? = null,
    @SerialName("clock_in_latitude") val clockInLatitude: String? = null,
    @SerialName("clock_in_longitude") val clockInLongitude: String? = null,
    @SerialName("clock_in_location") val clockInLocation: String? = null,
    @SerialName("clock_out_latitude") val clockOutLatitude: String? = null,
    @SerialName("clock_out_longitude") val clockOutLongitude: String? = null,
    @SerialName("clock_out_location") val clockOutLocation: String? = null,
    @SerialName("security_personnel") val securityPersonnel: User? = null
)