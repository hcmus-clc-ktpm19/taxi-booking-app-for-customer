package com.example.wibercustomer.models

enum class roleEnum {
    CUSTOMER,
}

class Account (
    var id : String,
    var phone: String,
    var password : String,
    var role : roleEnum = roleEnum.CUSTOMER
){

}