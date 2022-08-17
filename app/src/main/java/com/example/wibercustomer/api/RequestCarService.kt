package com.example.wibercustomer.api

import com.example.wibercustomer.models.CarRequest
import com.example.wibercustomer.models.CustomerInfo
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface RequestCarService {

    @POST("car-request/create-or-update")
    fun requestCarByAPI(@Body carRequestInfo: CarRequest, @Header("Authorization") accessToken : String): Call<ResponseBody>

    @GET("car-request/{customer-phone}/history")
    fun getAPICustomerCarRequestByPhoneNumber(@Path("customer-phone") phoneNumber : String, @Header("Authorization") accessToken : String): Call<List<CarRequest>>

    companion object {
        private var url: String = "http://10.0.2.2:8080/api/v1/"
        val requestCarService = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RequestCarService::class.java);
    }

}