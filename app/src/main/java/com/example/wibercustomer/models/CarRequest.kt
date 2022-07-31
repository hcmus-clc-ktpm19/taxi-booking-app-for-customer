package com.example.wibercustomer.models

import com.example.wibercustomer.interfaces.CarRequestState
import com.example.wibercustomer.states.freeRequestState


class CarRequest(
    var id: String?,
    var customerId: String,
    var pickingAddress: String,
    var arrivingAddress: String,
    var lngPickingAddress: Double,
    var latPickingAddress: Double,
    var lngArrivingAddress: Double,
    var latArrivingAddress: Double,
) {
    lateinit var currentState : CarRequestState
    init {
        currentState = freeRequestState()
    }

    fun setRequestState (state : CarRequestState)
    {
        currentState = state
    }

    fun freeRequest() : String{
        return currentState.freeRequest()
    }

    fun waitingRequest() : String{
        return currentState.waitingRequest()
    }

    fun acceptedRequest() : String{
        return currentState.acceptedRequest()
    }

}