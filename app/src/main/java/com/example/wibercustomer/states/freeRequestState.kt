package com.example.wibercustomer.states

import com.example.wibercustomer.interfaces.CarRequestState
import com.example.wibercustomer.models.CarRequest
import com.example.wibercustomer.models.enums.CarRequestStatus

class freeRequestState() : CarRequestState {

    override fun nextStatusRequest(carRequest: CarRequest): String {
        carRequest.setRequestState(waitingRequestState())
        carRequest.status = CarRequestStatus.WAITING.status
        return carRequest.status
    }

    override fun isFree(carRequest: CarRequest): Boolean {
        return true
    }
    override fun isAccepted(carRequest: CarRequest): Boolean {
        return false
    }
}