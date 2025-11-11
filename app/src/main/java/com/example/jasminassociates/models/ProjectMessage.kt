package com.example.jasminassociates.models

import com.jasminassociates.models.Project
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Contextual
import java.time.LocalDateTime

@Serializable
data class ProjectMessage(
    @SerialName("message_id") val messageID: Int = 0,
    @SerialName("project_id") val projectID: Int,
    @SerialName("sender_id") val senderID: Int,
    @SerialName("message_text") val messageText: String? = null,
    @SerialName("sent_date") @Contextual val sentDate: LocalDateTime = LocalDateTime.now(),
    @SerialName("parent_message_id") val parentMessageID: Int? = null,
    @SerialName("project") val project: Project? = null,
    @SerialName("sender") val sender: User? = null,
    @SerialName("parent_message") val parentMessage: ProjectMessage? = null,
    @SerialName("replies") val replies: List<ProjectMessage>? = null
)