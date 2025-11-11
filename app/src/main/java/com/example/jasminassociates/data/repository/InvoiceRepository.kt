package com.example.jasminassociates.data.repository



import com.example.jasminassociates.models.Invoice
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InvoiceRepository @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    suspend fun insertInvoice(invoice: Invoice): ApiResult<Invoice> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("invoices").insert(invoice)
            ApiResult.Success(invoice)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun updateInvoice(invoice: Invoice): ApiResult<Invoice> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("invoices").update(invoice) {
                filter { eq("invoice_id", invoice.invoiceID) }
            }
            ApiResult.Success(invoice)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun deleteInvoice(invoiceId: Int): ApiResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest.from("invoices").delete {
                filter { eq("invoice_id", invoiceId) }
            }
            ApiResult.Success(true)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    suspend fun getInvoiceById(invoiceId: Int): ApiResult<Invoice> = withContext(Dispatchers.IO) {
        try {
            val invoice = supabaseClient.postgrest.from("invoices")
                .select {
                    filter { eq("invoice_id", invoiceId) }
                }
                .decodeSingle<Invoice>()
            ApiResult.Success(invoice)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    fun getAllInvoices(): Flow<List<Invoice>> = flow {
        try {
            val invoices = supabaseClient.postgrest.from("invoices")
                .select()
                .decodeList<Invoice>()
            emit(invoices)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getInvoicesByProject(projectId: Int): Flow<List<Invoice>> = flow {
        try {
            val invoices = supabaseClient.postgrest.from("invoices")
                .select {
                    filter { eq("project_id", projectId) }
                }
                .decodeList<Invoice>()
            emit(invoices)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getInvoicesByClient(clientId: Int): Flow<List<Invoice>> = flow {
        try {
            val invoices = supabaseClient.postgrest.from("invoices")
                .select {
                    filter { eq("client_id", clientId) }
                }
                .decodeList<Invoice>()
            emit(invoices)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getInvoicesByStatus(status: String): Flow<List<Invoice>> = flow {
        try {
            val invoices = supabaseClient.postgrest.from("invoices")
                .select {
                    filter { eq("status", status) }
                }
                .decodeList<Invoice>()
            emit(invoices)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getInvoicesByCreator(creatorId: Int): Flow<List<Invoice>> = flow {
        try {
            val invoices = supabaseClient.postgrest.from("invoices")
                .select {
                    filter { eq("created_by", creatorId) }
                }
                .decodeList<Invoice>()
            emit(invoices)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}