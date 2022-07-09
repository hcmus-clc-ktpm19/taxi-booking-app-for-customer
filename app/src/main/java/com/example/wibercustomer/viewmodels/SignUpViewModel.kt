package com.example.wibercustomer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignUpViewModel : ViewModel() {

    private val _phoneNumberText = MutableLiveData<String>().apply {
        value = "+84 888"
    }

    val phoneNumberText : LiveData<String> = _phoneNumberText

    private val _passwordText = MutableLiveData<String>().apply {
        value = "ThisIsPassWord"
    }

    val passwordText : LiveData<String> = _passwordText

    private val _confirmPasswordText = MutableLiveData<String>().apply {
        value = "ThisIsPassWord"
    }

    val confirmPasswordText : LiveData<String> = _confirmPasswordText

}