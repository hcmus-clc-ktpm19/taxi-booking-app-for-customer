package com.example.wibercustomer.states

import com.example.wibercustomer.interfaces.CarRequestState
import com.example.wibercustomer.models.CarRequest

class acceptRequestState : CarRequestState {

    override fun getString(carRequest: CarRequest): String {
        carRequest.setRequestState(freeRequestState())
        return "got accepted"
    }
}