package com.example.jasminassociates.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Contextual
import java.math.BigDecimal

@Serializable
data class InvoiceItem(
    @SerialName("item_id") val itemID: Int = 0,
    @SerialName("invoice_id") val invoiceID: Int,
    @SerialName("item_type") val itemType: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("quantity") @Contextual val quantity: BigDecimal = BigDecimal.ONE,
    @SerialName("unit_price") @Contextual val unitPrice: BigDecimal,
    @SerialName("total_price") @Contextual val totalPrice: BigDecimal,
    @SerialName("invoice") val invoice: Invoice? = null
)