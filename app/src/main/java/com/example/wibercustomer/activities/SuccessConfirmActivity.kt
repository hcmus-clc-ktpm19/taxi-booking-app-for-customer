package com.example.wibercustomer.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.wibercustomer.R
import com.example.wibercustomer.databinding.ActivitySuccessConfirmationBinding
import com.example.wibercustomer.viewmodels.SuccessConfirmViewModel

class SuccessConfirmActivity : AppCompatActivity() {
    private lateinit var successConfirmViewModel: SuccessConfirmViewModel
    private lateinit var binding: ActivitySuccessConfirmationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        successConfirmViewModel = ViewModelProvider(this)[SuccessConfirmViewModel::class.java]

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_close -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

    }
}