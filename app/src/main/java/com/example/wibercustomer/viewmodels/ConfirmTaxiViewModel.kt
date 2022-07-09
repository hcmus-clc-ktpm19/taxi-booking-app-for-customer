package com.example.wibercustomer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ConfirmTaxiViewModel: ViewModel() {


    private val _arrivingText = MutableLiveData<String>().apply {
        value = "Arrive here"
    }

    val arrivingText : LiveData<String> = _arrivingText
}