package com.example.jasminassociates.ui.projectmanager

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jasminassociates.EquipmentDetailActivity
import com.example.jasminassociates.R
import com.example.jasminassociates.databinding.ActivityEquipmentRequestsBinding
import com.example.jasminassociates.models.EquipmentRequest
import com.example.jasminassociates.ui.adapters.EquipmentRequestsAdapter
import com.example.jasminassociates.viewmodels.EquipmentRequestsViewModel

class EquipmentRequestsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEquipmentRequestsBinding
    private lateinit var viewModel: EquipmentRequestsViewModel
    private lateinit var adapter: EquipmentRequestsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEquipmentRequestsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // FIX 1: Use bracket syntax instead of .get()
        viewModel = ViewModelProvider(this)[EquipmentRequestsViewModel::class.java]

        setupUI()
        setupObservers()
        setupRecyclerView()
        loadRequests()
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.equipment_requests)

        // Setup status filter spinner
        val statusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, viewModel.statusFilters)

        // FIX 2: Direct assignment without .also
        //binding.spinnerStatusFilter.adapter = statusAdapter

        binding.btnNewRequest.setOnClickListener { createNewRequest() }
        binding.btnDashboard.setOnClickListener { navigateToDashboard() }
        binding.swipeRefreshLayout.setOnRefreshListener { refreshRequests() }
    }

    private fun setupObservers() {
        viewModel.requests.observe(this) { requests ->
            adapter.submitList(requests)
            binding.tvEmptyState.visibility = if (requests.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        }

        viewModel.totalRequests.observe(this) { total ->
            binding.tvTotalRequests.text = total.toString()
        }

        viewModel.pendingRequests.observe(this) { pending ->
            binding.tvPendingRequests.text = pending.toString()
        }

        viewModel.approvedRequests.observe(this) { approved ->
            binding.tvApprovedRequests.text = approved.toString()
        }

        viewModel.fulfilledRequests.observe(this) { fulfilled ->
            binding.tvFulfilledRequests.text = fulfilled.toString()
        }

        viewModel.isRefreshing.observe(this) { refreshing ->
            binding.swipeRefreshLayout.isRefreshing = refreshing
        }
    }

    private fun setupRecyclerView() {
        adapter = EquipmentRequestsAdapter { request ->
            showRequestDetails(request)
        }

        binding.rvRequests.apply {
            layoutManager = LinearLayoutManager(this@EquipmentRequestsActivity)
            adapter = this@EquipmentRequestsActivity.adapter
        }
    }

    private fun loadRequests() {
        viewModel.loadRequests()
    }

    private fun refreshRequests() {
        viewModel.refresh()
    }

    private fun createNewRequest() {
        startActivity(android.content.Intent(this, CreateEquipmentRequestActivity::class.java))
    }

    private fun showRequestDetails(request: EquipmentRequest) {
        val intent = android.content.Intent(this, EquipmentDetailActivity::class.java).apply {
            putExtra("equipment_id", request.assignedEquipmentID ?: 0)
            putExtra("request_id", request.requestID)
        }
        startActivity(intent)
    }

    private fun navigateToDashboard() {
        startActivity(android.content.Intent(this, ProjectManagerDashboardActivity::class.java))
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}