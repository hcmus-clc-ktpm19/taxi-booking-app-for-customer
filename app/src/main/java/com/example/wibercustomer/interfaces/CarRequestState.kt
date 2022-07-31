package com.example.wibercustomer.interfaces

import com.example.wibercustomer.models.CarRequest

interface CarRequestState {
    fun getString(carRequest: CarRequest) : String
}