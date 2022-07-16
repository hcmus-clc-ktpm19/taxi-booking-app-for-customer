package com.example.wibercustomer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wibercustomer.api.SignUpService
import com.example.wibercustomer.models.Customer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpViewModel : ViewModel() {

    private val _phoneNumberText = MutableLiveData<String>().apply {
        value = ""
    }

    val phoneNumberText : LiveData<String> = _phoneNumberText

    private val _passwordText = MutableLiveData<String>().apply {
        value = ""
    }

    val passwordText : LiveData<String> = _passwordText

    private val _confirmPasswordText = MutableLiveData<String>().apply {
        value = ""
    }

    val confirmPasswordText : LiveData<String> = _confirmPasswordText


}