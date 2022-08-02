package com.example.wibercustomer.models

import com.example.wibercustomer.interfaces.CarRequestState
import com.example.wibercustomer.models.enums.CarRequestStatus
import com.example.wibercustomer.states.freeRequestState


class CarRequest(
    var id: String?,
    var customerId: String,
    var customerPhone: String,
    var pickingAddress: String,
    var arrivingAddress: String,
    var lngPickingAddress: Double,
    var latPickingAddress: Double,
    var lngArrivingAddress: Double,
    var latArrivingAddress: Double,
) {
    lateinit var currentState : CarRequestState
    lateinit var status: String
    init {
        currentState = freeRequestState()
        status = CarRequestStatus.FREE.status
    }

    fun setRequestState (state : CarRequestState)
    {
        currentState = state
    }


    fun nextStatusRequest() : String{
        return currentState.nextStatusRequest(this)
    }

    fun isFree() : Boolean{
        return currentState.isFree(this)
    }

}