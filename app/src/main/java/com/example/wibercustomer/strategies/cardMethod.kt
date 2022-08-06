package com.example.wibercustomer.strategies

import com.example.wibercustomer.interfaces.PaymentMethodStrategy

class cardMethod : PaymentMethodStrategy {
    override val fare: Double = 0.05
}