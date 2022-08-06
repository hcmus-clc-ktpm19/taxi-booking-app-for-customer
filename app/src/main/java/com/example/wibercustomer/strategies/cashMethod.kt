package com.example.wibercustomer.strategies

import com.example.wibercustomer.interfaces.PaymentMethodStrategy

class cashMethod : PaymentMethodStrategy {
    override val fare: Double = 0.1
}