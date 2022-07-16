package com.example.wibercustomer.api

import com.example.wibercustomer.models.Customer
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface SignUpService {

    @POST("auth/register")
    fun registerCustomer(@Body customer: Customer): Call<ResponseBody>

    companion object {
        private var url: String = "http://10.0.2.2:8080/api/v1/"
        val signUpService = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SignUpService::class.java);
    }
}