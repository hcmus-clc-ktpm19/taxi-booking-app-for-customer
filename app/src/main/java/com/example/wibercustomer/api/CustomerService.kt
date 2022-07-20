package com.example.wibercustomer.api

import com.example.wibercustomer.models.CustomerInfo
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface CustomerService {
    @GET("{phone}")
    fun getAPICustomerInfo(@Path("phone") phoneNumber : String, @Header("Authorization") accessToken : String): Call<CustomerInfo>

    companion object {
        private var url: String = "http://10.0.2.2:8080/api/v1/customer/"
        val authService = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CustomerService::class.java);
    }
}