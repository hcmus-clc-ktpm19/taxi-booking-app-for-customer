package com.example.wibercustomer.states

import com.example.wibercustomer.interfaces.CarRequestState
import com.example.wibercustomer.models.CarRequest

class waitingRequestState : CarRequestState {
    override fun getString(carRequest: CarRequest): String {
        carRequest.setRequestState(acceptRequestState())
        return "waiting"
    }
}