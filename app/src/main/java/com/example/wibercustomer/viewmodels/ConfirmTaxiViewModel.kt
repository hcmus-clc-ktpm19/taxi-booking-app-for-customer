package com.example.wibercustomer.viewmodels

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wibercustomer.dialog.ChoosePaymentMethodDialog

class ConfirmTaxiViewModel: ViewModel() {
    fun confirmTaxi(supportFragmentManager: FragmentManager) {
        val choosePaymentMethodDialog = ChoosePaymentMethodDialog()
        choosePaymentMethodDialog.show(supportFragmentManager, "ChoosePaymentMethodDialog")
    }


    private val _arrivingText = MutableLiveData<String>().apply {
        value = "Arrive here"
    }

    val arrivingText : LiveData<String> = _arrivingText
}