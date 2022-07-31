package com.example.wibercustomer.viewmodels

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wibercustomer.activities.SigninActivity
import com.example.wibercustomer.api.AuthService
import com.example.wibercustomer.api.CustomerService
import com.example.wibercustomer.models.AuthToken
import com.example.wibercustomer.models.CustomerInfo
import com.example.wibercustomer.models.roleEnum
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class ProfileViewModel : ViewModel() {

    private val _nameText = MutableLiveData<String>().apply {
        value = ""
    }

    val nameText : LiveData<String> = _nameText


    private val _phoneNumberText = MutableLiveData<String>().apply {
        value = ""
    }

    val phoneNumberText : LiveData<String> = _phoneNumberText

    private val _newPasswordText = MutableLiveData<String>().apply {
        value = ""
    }

    val newPasswordText : LiveData<String> = _newPasswordText

    fun getCustomerInfo(phoneNumber : String, token: AuthToken){
        CustomerService.customerService.getAPICustomerInfo(phoneNumber, "Bearer ${token.accessToken}")
            .enqueue(object : Callback<CustomerInfo>{
                override fun onResponse(
                    call: Call<CustomerInfo>,
                    response: Response<CustomerInfo>
                ) {
                    _phoneNumberText.value = phoneNumber
                    if (response.isSuccessful)
                    {
                        val customerFromApi = response.body()
                        _nameText.value = customerFromApi?.name
                    }
                    else
                    {
                        _nameText.value = ""
                    }
                }

                override fun onFailure(call: Call<CustomerInfo>, t: Throwable) {
                    //server dead
                }
            })
    }

    var editProfileStatus = MutableLiveData<String>()
    fun startEditingProfile(passWordString : String, nameString : String){
        if (nameString.isNotEmpty()) {
            GlobalScope.launch {
                val accountDetail = AuthService.authService.getAccountDetail(
                    SigninActivity.phoneNumberLoginFromSignIn,
                    "Bearer ${SigninActivity.authCustomerTokenFromSignIn.accessToken}"
                )
                if (accountDetail != null) {
                    if (passWordString.isNotEmpty()) {
                        accountDetail.password = passWordString
                        try {
                            AuthService.authService.updatePasswordAPI(
                                accountDetail.id, accountDetail,
                                "Bearer ${SigninActivity.authCustomerTokenFromSignIn.accessToken}"
                            )
                            val customerUpdate = CustomerInfo(
                                accountDetail.id, SigninActivity.phoneNumberLoginFromSignIn,
                                nameString, roleEnum.CUSTOMER
                            )
                            updateCustomerInfo(customerUpdate)
                        } catch (e: Exception) {
                            editProfileStatus.postValue(
                                (e as? HttpException)?.response()?.errorBody()?.string()
                            )
                        }
                    }
                    else {
                        val customerUpdate = CustomerInfo(
                            accountDetail.id, SigninActivity.phoneNumberLoginFromSignIn,
                            nameString, roleEnum.CUSTOMER
                        )
                        updateCustomerInfo(customerUpdate)
                    }
                }
                else
                    editProfileStatus.postValue("Error while saving")
            }
        }
        else
            editProfileStatus.postValue("Please input name")

    }


private fun updateCustomerInfo(customerInfo: CustomerInfo)
{
    CustomerService.customerService.updateCustomerInfoAPI(customerInfo, "Bearer ${SigninActivity.authCustomerTokenFromSignIn.accessToken}")
        .enqueue(object : Callback<ResponseBody>{
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful)
                {
                    editProfileStatus.postValue("Update successfully")
                    getCustomerInfo(customerInfo.phone,
                        SigninActivity.authCustomerTokenFromSignIn
                    )
                }
                else
                {
                    editProfileStatus.postValue("Error while updating")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                editProfileStatus.postValue("Unable to connect to server")
            }

        })
}
}