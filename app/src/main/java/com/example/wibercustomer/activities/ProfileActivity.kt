package com.example.wibercustomer.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.wibercustomer.activities.SigninActivity.Companion.authCustomerTokenFromSignIn
import com.example.wibercustomer.activities.SigninActivity.Companion.phoneNumberLoginFromSignIn
import com.example.wibercustomer.api.AuthService
import com.example.wibercustomer.api.CustomerService
import com.example.wibercustomer.databinding.ActivityProfileBinding
import com.example.wibercustomer.models.CustomerInfo
import com.example.wibercustomer.models.roleEnum
import com.example.wibercustomer.viewmodels.ProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dmax.dialog.SpotsDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var alertDialog : AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        alertDialog = SpotsDialog.Builder().setContext(this)
            .setCancelable(false)
            .setMessage("Uploading")
            .build()

        val nameLayout = binding.nameInputLayout
        val newPasswordLayout = binding.newPasswordInputLayout

        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        profileViewModel.nameText.observe(this){
            nameLayout.editText?.setText(it)
        }

        profileViewModel.phoneNumberText.observe(this){
            binding.phoneNumberInputLayout.editText?.setText(it)
        }
        profileViewModel.getCustomerInfo(phoneNumberLoginFromSignIn, authCustomerTokenFromSignIn)

        binding.saveBtn.setOnClickListener { saveBtnOnclick ->
            val nameString = nameLayout.editText?.text.toString()
            val passwordString = newPasswordLayout.editText?.text.toString()
            alertDialog.show()
            profileViewModel.startEditingProfile(passwordString, nameString)
        }

        val statusObserver = Observer<String>{ status ->
            alertDialog.dismiss()
            Toast.makeText(this@ProfileActivity, status, Toast.LENGTH_LONG).show()
        }
        profileViewModel.editProfileStatus.observe(this, statusObserver)
    }
}