package com.example.wibercustomer.states

import com.example.wibercustomer.interfaces.CarRequestState

class freeRequestState : CarRequestState {

    override fun freeRequest(): String {
        return "No booking"
    }

    override fun waitingRequest(): String {
        return "Book a request"
    }

    override fun acceptedRequest(): String {
        return "Book a request"
    }
}