package com.example.wibercustomer.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wibercustomer.api.CustomerService
import com.example.wibercustomer.models.AuthToken
import com.example.wibercustomer.models.CustomerInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel : ViewModel() {

    private val _nameText = MutableLiveData<String>().apply {
        value = ""
    }

    val nameText : LiveData<String> = _nameText


    private val _phoneNumberText = MutableLiveData<String>().apply {
        value = ""
    }

    val phoneNumberText : LiveData<String> = _phoneNumberText

    private val _newPasswordText = MutableLiveData<String>().apply {
        value = ""
    }

    val newPasswordText : LiveData<String> = _newPasswordText

    fun getCustomerInfo(phoneNumber : String, token: AuthToken){
        CustomerService.authService.getAPICustomerInfo(phoneNumber, "Bearer ${token.accessToken}")
            .enqueue(object : Callback<CustomerInfo>{
                override fun onResponse(
                    call: Call<CustomerInfo>,
                    response: Response<CustomerInfo>
                ) {
                    _phoneNumberText.value = phoneNumber
                    if (response.isSuccessful)
                    {
                        Log.i("CallApi", "true")
                        val customerFromApi = response.body()
                        _nameText.value = customerFromApi?.name
                    }
                    else
                    {
                        Log.i("CallApi", "false")
                        _nameText.value = ""
                    }
                }

                override fun onFailure(call: Call<CustomerInfo>, t: Throwable) {
                    //server dead
                }
            })
    }
}