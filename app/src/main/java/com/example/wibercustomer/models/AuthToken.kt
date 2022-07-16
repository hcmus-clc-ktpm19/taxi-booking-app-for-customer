package com.example.wibercustomer.models

import com.google.gson.annotations.SerializedName

class AuthToken (
    @SerializedName("Access-Token") val accessToken: String,
    @SerializedName("Refresh-Token") val refressToken: String,
        ) {
}