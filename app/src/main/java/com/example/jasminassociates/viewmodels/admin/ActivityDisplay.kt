package com.example.jasminassociates.viewmodels.admin

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

data class ActivityDisplay @RequiresApi(Build.VERSION_CODES.O) constructor(
    val userId: Int = 0,
    val activityType: String? = null,
    val userName: String? = null,
    val description: String? = null,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: String? = null,
    val statusColor: String? = null // Using String instead of Color for Android
)