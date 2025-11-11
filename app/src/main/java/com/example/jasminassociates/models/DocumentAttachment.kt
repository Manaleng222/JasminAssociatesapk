package com.example.jasminassociates.models

import com.jasminassociates.models.Project
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Contextual
import java.time.LocalDateTime

@Serializable
data class DocumentAttachment(
    @SerialName("document_id") val documentID: Int = 0,
    @SerialName("project_id") val projectID: Int,
    @SerialName("file_name") val fileName: String? = null,
    @SerialName("file_path") val filePath: String? = null,
    @SerialName("file_type") val fileType: String? = null,
    @SerialName("uploaded_by") val uploadedBy: Int,
    @SerialName("upload_date") @Contextual val uploadDate: LocalDateTime = LocalDateTime.now(),
    @SerialName("description") val description: String? = null,
    @SerialName("project") val project: Project? = null,
    @SerialName("uploader") val uploader: User? = null
)