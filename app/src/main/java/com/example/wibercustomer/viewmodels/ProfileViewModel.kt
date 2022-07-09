package com.example.wibercustomer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {

    private val _nameText = MutableLiveData<String>().apply {
        value = "Nguyen Van A"
    }

    val nameText : LiveData<String> = _nameText

    private val _defaultAddressText = MutableLiveData<String>().apply {
        value = "96/69 HCMUS"
    }

    val defaultAddressText : LiveData<String> = _defaultAddressText

    private val _phoneNumberText = MutableLiveData<String>().apply {
        value = "+84 888"
    }

    val phoneNumberText : LiveData<String> = _phoneNumberText

    private val _newPasswordText = MutableLiveData<String>().apply {
        value = "New Password"
    }

    val newPasswordText : LiveData<String> = _newPasswordText
}