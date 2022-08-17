package com.example.wibercustomer.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wibercustomer.api.RequestCarService
import com.example.wibercustomer.models.AuthToken
import com.example.wibercustomer.models.CarRequest
import com.example.wibercustomer.models.History
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryViewModel : ViewModel() {

    private val _historyList = MutableLiveData<List<CarRequest>>().apply {
        value = ArrayList<CarRequest>()
    }

    val historyList: LiveData<List<CarRequest>> = _historyList

    private val _totalBalance = MutableLiveData<Double>().apply {
        value = 0.0
    }

    val totalBalance: LiveData<Double> = _totalBalance

    val historyStatus = MutableLiveData<String>()
    fun initHistoryData(phoneNumber : String, token: AuthToken) {
        RequestCarService.requestCarService.getAPICustomerCarRequestByPhoneNumber(phoneNumber, "Bearer ${token.accessToken}")
            .enqueue(object : Callback<List<CarRequest>>{
                override fun onResponse(
                    call: Call<List<CarRequest>>,
                    response: Response<List<CarRequest>>
                ) {
                    if (response.isSuccessful)
                    {
                        _historyList.value = response.body()
                        historyStatus.postValue("Done")
                        var total : Double = 0.0
                        for (i in _historyList.value!!)
                        {
                            total+= i.price
                        }
                        Log.i("history", total.toString())
                        _totalBalance.value = total
                        Log.i("history", _historyList.value!!.size.toString())
                    }
                    else
                    {
                        _historyList.value = ArrayList<CarRequest>()
                        Log.i("history", "error data")
                        historyStatus.postValue("Done")
                    }
                }

                override fun onFailure(call: Call<List<CarRequest>>, t: Throwable) {
                    historyStatus.postValue("Fail")
                }

            })
    }
}