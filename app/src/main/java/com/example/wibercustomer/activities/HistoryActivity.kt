package com.example.wibercustomer.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wibercustomer.activities.SigninActivity.Companion.authCustomerTokenFromSignIn
import com.example.wibercustomer.activities.SigninActivity.Companion.phoneNumberLoginFromSignIn
import com.example.wibercustomer.adapters.HistoryAdapter
import com.example.wibercustomer.databinding.ActivityHistoryBinding
import com.example.wibercustomer.viewmodels.HistoryViewModel

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        historyViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.historyRecyclerview.layoutManager = LinearLayoutManager(this)

        historyViewModel.totalBalance.observe(this){
            binding.totalBalance.text = it.toInt().toString() + " VND"
        }

        historyViewModel.historyList.observe(this){
            historyAdapter = HistoryAdapter(it)

            binding.historyRecyclerview.adapter = historyAdapter
        }

        historyViewModel.initHistoryData(phoneNumberLoginFromSignIn, authCustomerTokenFromSignIn)
        val historyStatusObserver = Observer<String> { status ->
            when(status) {
                "Done" -> {
                    val listUser = historyViewModel.historyList.value

                }
                else -> {

                }
            }
        }
    }
}