package com.example.wibercustomer.models

import com.example.wibercustomer.interfaces.CarRequestState
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
    var status: String
) {
    lateinit var currentState : CarRequestState
    init {
        currentState = freeRequestState()
    }

    fun setRequestState (state : CarRequestState)
    {
        currentState = state
    }


    fun getString() : String{
        return currentState.getString(this)
    }

}