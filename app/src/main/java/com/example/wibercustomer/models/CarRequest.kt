package com.example.wibercustomer.models

import com.example.wibercustomer.interfaces.CarRequestState
import com.example.wibercustomer.interfaces.PaymentMethodStrategy
import com.example.wibercustomer.models.enums.CarRequestStatus
import com.example.wibercustomer.states.freeRequestState
import com.example.wibercustomer.strategies.cashMethod
import java.util.*


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
    var carType: String,
    var price: Double,
    var distance: Double,
) {
    lateinit var currentState : CarRequestState
    lateinit var status: String
    lateinit var currentPayment : PaymentMethodStrategy
    var moneyToPay : Double = 0.0

    init {
        currentState = freeRequestState()
        status = CarRequestStatus.FREE.status
        currentPayment = cashMethod()
    }

    fun setRequestState (state : CarRequestState)
    {
        currentState = state
    }

    fun setPaymentStrategy(strategy: PaymentMethodStrategy)
    {
        currentPayment = strategy
    }


    fun nextStatusRequest() : String{
        return currentState.nextStatusRequest(this)
    }

    fun isFree() : Boolean{
        return currentState.isFree(this)
    }

    fun isAccepted() : Boolean{
        return currentState.isAccepted(this)
    }


}