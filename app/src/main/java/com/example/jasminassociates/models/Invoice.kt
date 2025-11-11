package com.example.jasminassociates.models

import com.jasminassociates.models.Project
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Contextual
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class Invoice(
    @SerialName("invoice_id") val invoiceID: Int = 0,
    @SerialName("project_id") val projectID: Int,
    @SerialName("invoice_number") val invoiceNumber: String? = null,
    @SerialName("invoice_date") @Contextual val invoiceDate: LocalDateTime = LocalDateTime.now(),
    @SerialName("due_date") @Contextual val dueDate: LocalDateTime,
    @SerialName("client_id") val clientID: Int,
    @SerialName("total_amount") @Contextual val totalAmount: BigDecimal,
    @SerialName("tax_amount") @Contextual val taxAmount: BigDecimal = BigDecimal.ZERO,
    @SerialName("status") val status: String = "Draft",
    @SerialName("created_by") val createdBy: Int,
    @SerialName("payment_date") @Contextual val paymentDate: LocalDateTime? = null,
    @SerialName("notes") val notes: String? = null,
    @SerialName("project") val project: Project? = null,
    @SerialName("client") val client: User? = null,
    @SerialName("creator") val creator: User? = null,
    @SerialName("invoice_items") val invoiceItems: List<InvoiceItem>? = null
)