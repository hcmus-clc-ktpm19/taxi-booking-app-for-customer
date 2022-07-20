package com.example.wibercustomer.models

class CustomerInfo  (
    var id: String,
    var phone: String,
    var name : String,
    var role : roleEnum = roleEnum.CUSTOMER
){

}