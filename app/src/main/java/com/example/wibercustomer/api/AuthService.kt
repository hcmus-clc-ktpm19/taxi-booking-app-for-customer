package com.example.wibercustomer.api

import com.example.wibercustomer.models.AuthToken
import com.example.wibercustomer.models.Account
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthService {

    @POST("register")
    fun registerCustomer(@Body customer: Account): Call<ResponseBody>

    @FormUrlEncoded
    @POST("login")
    fun loginAsCustomer(@Field("phone") phone: String,@Field("password") password : String): Call<AuthToken>

    companion object {
        private var url: String = "http://10.0.2.2:8080/api/v1/auth/"
        val authService = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java);
    }
}