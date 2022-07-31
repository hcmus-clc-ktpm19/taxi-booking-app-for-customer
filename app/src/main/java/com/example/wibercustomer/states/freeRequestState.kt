package com.example.wibercustomer.states

import com.example.wibercustomer.interfaces.CarRequestState
import com.example.wibercustomer.models.CarRequest

class freeRequestState() : CarRequestState {

    override fun getString(carRequest: CarRequest): String {
        carRequest.setRequestState(waitingRequestState())
        return "free"
    }
}