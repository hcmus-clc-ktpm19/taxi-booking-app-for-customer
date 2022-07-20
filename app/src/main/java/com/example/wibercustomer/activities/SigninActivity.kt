package com.example.wibercustomer.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.wibercustomer.api.AuthService
import com.example.wibercustomer.databinding.ActivitySigninBinding
import com.example.wibercustomer.models.AuthToken
import com.example.wibercustomer.viewmodels.SignInViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SigninActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private lateinit var loginviewModel: SignInViewModel

    companion object{
        lateinit var authCustomerTokenFromSignIn: AuthToken
        lateinit var phoneNumberLoginFromSignIn: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySigninBinding.inflate(layoutInflater)

        setContentView(binding.root)

        loginviewModel = ViewModelProvider(this).get(SignInViewModel::class.java)

        val phoneIL = binding.phoneNumberInputLayout
        val passwordIL = binding.passwordInputLayout

        binding.signupbtn.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.signinbtn.setOnClickListener {
            val phoneNumber = phoneIL.editText?.text.toString()
            val password = passwordIL.editText?.text.toString()
            signInGetToken(phoneNumber, password)
        }

    }

    fun signInGetToken(phoneNumber: String, password:String){
        AuthService.authService.loginAsCustomer(phoneNumber, password).enqueue(object :Callback<AuthToken>{
            override fun onResponse(call: Call<AuthToken>, response: Response<AuthToken>) {
                if (response.isSuccessful)
                {
                    authCustomerTokenFromSignIn = response.body()!!
                    phoneNumberLoginFromSignIn = phoneNumber
                    startActivity(Intent(this@SigninActivity, HomeActivity::class.java))
                }
                else
                {
                    Toast.makeText(this@SigninActivity, "Wrong phone number or password", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<AuthToken>, t: Throwable) {
                Toast.makeText(this@SigninActivity, t.toString(), Toast.LENGTH_LONG).show()
            }

        })
    }
}