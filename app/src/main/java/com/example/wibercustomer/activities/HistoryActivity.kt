package com.example.wibercustomer.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wibercustomer.R
import com.example.wibercustomer.adapters.HistoryAdapter
import com.example.wibercustomer.databinding.ActivityHistoryBinding
import com.example.wibercustomer.databinding.ActivityProfileBinding
import com.example.wibercustomer.models.History
import com.example.wibercustomer.viewmodels.HistoryViewModel

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHistoryBinding
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        historyViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)

        binding.historyRecycleview.layoutManager = LinearLayoutManager(this)

        val listUser = historyViewModel.historyList.value

        historyAdapter = HistoryAdapter(listUser!!)

        binding.historyRecycleview.adapter = historyAdapter

    }
}