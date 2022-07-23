package com.example.wibercustomer.api

import com.example.wibercustomer.models.AuthToken
import com.example.wibercustomer.models.CustomerInfo
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface CustomerService {

    @GET("customer/{phone}")
    fun getAPICustomerInfo(@Path("phone") phoneNumber : String, @Header("Authorization") accessToken : String): Call<CustomerInfo>

    @POST("customer/create-or-update")
    fun updateCustomerInfoAPI(@Body info: CustomerInfo, @Header("Authorization") accessToken : String): Call<ResponseBody>

    companion object {
        private var url: String = "http://10.0.2.2:8080/api/v1/"
        val customerService = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CustomerService::class.java);
    }
}