package com.example.wibercustomer.models

import com.example.wibercustomer.interfaces.PaymentMethodStrategy
import com.example.wibercustomer.models.enums.CarRequestStatus
import com.example.wibercustomer.states.freeRequestState
import com.example.wibercustomer.strategies.cashMethod
import java.util.*

data class Payment(
    var name: String,
    var type: String,
    var number: String,
    var since: Date
) {
    constructor() : this("", "", "", Date())
    lateinit var currentPayment : PaymentMethodStrategy
    var moneyToPay : Double = 0.0

    init {
        currentPayment = cashMethod()
    }

    fun calculateMoney(distance : Double)
    {
        val moneyDistance = (distance/ 500) * 3000 + 15000
        moneyToPay =  moneyDistance + currentPayment.fare * moneyDistance
    }

}