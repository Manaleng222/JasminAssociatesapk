package com.example.jasminassociates.services


import com.example.jasminassociates.data.repository.ApiResult
import com.example.jasminassociates.data.repository.InvoiceRepository
import com.example.jasminassociates.data.repository.isSuccess
import com.example.jasminassociates.models.Invoice
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.util.Random
import javax.inject.Inject

class InvoiceService @Inject constructor(
    private val invoiceRepository: InvoiceRepository
) {

    suspend fun getAllInvoices(): List<Invoice> {
        return invoiceRepository.getAllInvoices().first()
    }

    suspend fun getInvoiceById(id: Int): Invoice? {
        return when (val result = invoiceRepository.getInvoiceById(id)) {
            is ApiResult.Success -> result.data
            is ApiResult.Failure -> null
        }
    }

    suspend fun createInvoice(invoice: Invoice): Boolean {
        val invoiceWithNumber = if (invoice.invoiceNumber.isNullOrEmpty()) {
            invoice.copy(invoiceNumber = generateInvoiceNumber())
        } else {
            invoice
        }
        return invoiceRepository.insertInvoice(invoiceWithNumber).isSuccess
    }

    suspend fun updateInvoice(invoice: Invoice): Boolean {
        return invoiceRepository.updateInvoice(invoice).isSuccess
    }

    suspend fun deleteInvoice(id: Int): Boolean {
        return invoiceRepository.deleteInvoice(id).isSuccess
    }

    suspend fun getInvoicesByProject(projectId: Int): List<Invoice> {
        return invoiceRepository.getInvoicesByProject(projectId).first()
    }

    suspend fun getInvoicesByStatus(status: String): List<Invoice> {
        return invoiceRepository.getInvoicesByStatus(status).first()
    }

    suspend fun getOverdueInvoices(): List<Invoice> {
        val invoices = getAllInvoices()
        val now = LocalDateTime.now()
        return invoices.filter {
            it.status == "Pending" && it.dueDate.isBefore(now)
        }
    }

    suspend fun markInvoiceAsPaid(invoiceId: Int): Boolean {
        val invoice = getInvoiceById(invoiceId)
        if (invoice != null) {
            val updatedInvoice = invoice.copy(
                status = "Paid",
                paymentDate = LocalDateTime.now()
            )
            return invoiceRepository.updateInvoice(updatedInvoice).isSuccess
        }
        return false
    }

    suspend fun getTotalOutstandingInvoices(): Double {
        val invoices = getAllInvoices()
        return invoices
            .filter { it.status == "Pending" || it.status == "Overdue" }
            .sumOf { it.totalAmount.toDouble() }
    }

    suspend fun getTotalPaidInvoices(): Double {
        val invoices = getAllInvoices()
        return invoices
            .filter { it.status == "Paid" }
            .sumOf { it.totalAmount.toDouble() }
    }

    private fun generateInvoiceNumber(): String {
        val timestamp = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        val random = Random().nextInt(9000) + 1000
        return "INV-$timestamp-$random"
    }
}