package com.example.wibercustomer.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.wibercustomer.databinding.ActivityProfileBinding
import com.example.wibercustomer.viewmodels.ProfileViewModel

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileBinding
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        profileViewModel.nameText.observe(this){
            binding.nameInputLayout.editText?.setText(it)
        }

        profileViewModel.phoneNumberText.observe(this){
            binding.phoneNumberInputLayout.editText?.setText(it)
        }

        profileViewModel.defaultAddressText.observe(this){
            binding.addressInputLayout.editText?.setText(it)
        }

        profileViewModel.newPasswordText.observe(this){
            binding.newPasswordInputLayout.editText?.setText(it)
        }

    }
}