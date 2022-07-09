package com.example.wibercustomer.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
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

        var toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Home"
        var drawerLayout = binding.drawerLayout
        var navigationView = binding.navView
        var actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.openNavDrawer,
            R.string.closeNavDrawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        if (historyViewModel.historyList.value!!.isEmpty())
            historyViewModel.initHistoryData()

        binding.historyRecycleview.layoutManager = LinearLayoutManager(this)

        val listUser = historyViewModel.historyList.value

        historyAdapter = HistoryAdapter(listUser!!)

        binding.historyRecycleview.adapter = historyAdapter

    }
}