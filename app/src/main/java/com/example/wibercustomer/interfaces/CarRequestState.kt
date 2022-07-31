package com.example.wibercustomer.interfaces

interface CarRequestState {
    fun freeRequest() : String
    fun waitingRequest(): String
    fun acceptedRequest(): String
}