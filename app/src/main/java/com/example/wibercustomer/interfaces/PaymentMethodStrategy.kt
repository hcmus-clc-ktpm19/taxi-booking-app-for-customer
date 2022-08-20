package com.example.wibercustomer.interfaces

interface PaymentMethodStrategy {
    val fare : Double
    fun calculateMoney(distance : Double) : Double
}