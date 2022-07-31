package com.example.wibercustomer.states

import com.example.wibercustomer.interfaces.CarRequestState

class waitingRequestState : CarRequestState {
    override fun freeRequest(): String {
        return "You currently can not book a request"
    }

    override fun waitingRequest(): String {
        return "Your request is waiting for a driver"
    }

    override fun acceptedRequest(): String {
        return "Wait for a driver accept your request"
    }
}