package com.example.jasminassociates.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Inventory(

    @SerialName("inventory_id") val inventoryID: Int = 0,
)