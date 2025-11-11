package com.example.jasminassociates.viewmodels.admin




import android.os.Build
import androidx.annotation.RequiresApi
import com.example.jasminassociates.services.ReportService
import java.time.LocalDateTime

data class ActivitiesReport @RequiresApi(Build.VERSION_CODES.O) constructor(
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val activities: List<ReportService.ActivityDisplay> = emptyList(),
    val generatedBy: String = "",
    val generatedAt: LocalDateTime = LocalDateTime.now()
)