package com.example.wibercustomer.states

import com.example.wibercustomer.interfaces.CarRequestState

class acceptRequestState : CarRequestState {
    override fun freeRequest(): String {
        return "You currently can not book a request"
    }

    override fun waitingRequest(): String {
        return "We found a driver for you please wait"
    }

    override fun acceptedRequest(): String {
        return "We found a driver for you"
    }
}