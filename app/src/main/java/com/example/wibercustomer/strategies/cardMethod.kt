package com.example.wibercustomer.strategies

import com.example.wibercustomer.interfaces.PaymentMethodStrategy

class cardMethod : PaymentMethodStrategy {
    override val fare: Double = 0.05

    override fun calculateMoney(distance: Double): Double {
        val moneyDistance = (distance/ 500) * 3000 + 15000
        return moneyDistance + fare * moneyDistance
    }
}