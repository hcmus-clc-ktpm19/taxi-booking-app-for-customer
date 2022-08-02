package com.example.wibercustomer.states

import com.example.wibercustomer.interfaces.CarRequestState
import com.example.wibercustomer.models.CarRequest
import com.example.wibercustomer.models.enums.CarRequestStatus

class acceptRequestState : CarRequestState {

    override fun nextStatusRequest(carRequest: CarRequest): String {
        carRequest.setRequestState(freeRequestState())
        carRequest.status = CarRequestStatus.FREE.status
        return carRequest.status
    }

    override fun isFree(carRequest: CarRequest): Boolean {
        return false
    }
}