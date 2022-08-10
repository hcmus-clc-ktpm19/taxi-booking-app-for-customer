package com.example.wibercustomer.states

import com.example.wibercustomer.interfaces.CarRequestState
import com.example.wibercustomer.models.CarRequest
import com.example.wibercustomer.models.enums.CarRequestStatus

class waitingRequestState : CarRequestState {

    override fun nextStatusRequest(carRequest: CarRequest): String {
        carRequest.setRequestState(acceptRequestState())
        carRequest.status = CarRequestStatus.ACCEPTED.status
        return carRequest.status
    }

    override fun isFree(carRequest: CarRequest): Boolean {
        return false
    }

    override fun isAccepted(carRequest: CarRequest): Boolean {
        return false
    }
}