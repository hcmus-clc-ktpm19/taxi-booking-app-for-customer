package com.example.wibercustomer.viewmodels

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wibercustomer.activities.SigninActivity.Companion.authCustomerTokenFromSignIn
import com.example.wibercustomer.activities.SigninActivity.Companion.phoneNumberLoginFromSignIn
import com.example.wibercustomer.api.AuthService
import com.example.wibercustomer.models.AuthToken
import com.example.wibercustomer.models.roleEnum
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInViewModel : ViewModel() {
    var status = MutableLiveData<String>()

    fun signIn(phoneNumber: String, password: String) {
        AuthService.authService.loginThroughAPI(phoneNumber, password).enqueue(object :
            Callback<AuthToken> {
            override fun onResponse(call: Call<AuthToken>, response: Response<AuthToken>) {
                if (response.isSuccessful)
                {
                    authCustomerTokenFromSignIn = response.body()!!
                    phoneNumberLoginFromSignIn = phoneNumber
                    GlobalScope.launch {
                        val accountDetail = AuthService.authService.getAccountDetail(
                            phoneNumberLoginFromSignIn,
                            "Bearer ${authCustomerTokenFromSignIn.accessToken}"
                        )
                        if (!accountDetail.role.equals(roleEnum.CUSTOMER))
                        {
                            Handler(Looper.getMainLooper()).post {
                                status.postValue("This account is not a customer")
                            }
                        }
                        else
                            status.postValue("Success")
                    }
                }
                else
                {
                    status.postValue("Wrong phone number or password")
                }
            }

            override fun onFailure(call: Call<AuthToken>, t: Throwable) {
                status.postValue(t.toString())
            }

        })
    }

    private val _phoneNumberText = MutableLiveData<String>().apply {
        value = ""
    }

    val phoneNumberText : LiveData<String> = _phoneNumberText

    private val _passwordText = MutableLiveData<String>().apply {
        value = ""
    }

    val passwordText : LiveData<String> = _passwordText

}