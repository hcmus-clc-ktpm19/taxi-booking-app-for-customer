package com.example.wibercustomer.interfaces

import com.example.wibercustomer.models.CarRequest

interface CarRequestState {
    fun nextStatusRequest(carRequest: CarRequest) : String
    fun isFree(carRequest: CarRequest) : Boolean
    fun isAccepted(carRequest: CarRequest) : Boolean
}