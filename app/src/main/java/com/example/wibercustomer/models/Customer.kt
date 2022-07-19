package com.example.wibercustomer.models

enum class roleEnum {
    CUSTOMER,
}

class Customer (
    var phone: String,
    var password : String,
    var role : roleEnum = roleEnum.CUSTOMER
){

}