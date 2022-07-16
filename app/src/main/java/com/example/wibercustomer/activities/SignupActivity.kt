package com.example.wibercustomer.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.wibercustomer.api.SignUpService
import com.example.wibercustomer.databinding.ActivitySignupBinding
import com.example.wibercustomer.models.Customer
import com.example.wibercustomer.viewmodels.SignUpViewModel
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var signUpviewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        signUpviewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        val phoneNumberIL = binding.phoneNumberInputLayout
        val pwdIL = binding.paswordInputLayout
        val confirmPwdIL = binding.confirmPaswordInputLayout

        binding.signUpBtn.setOnClickListener {
            val phoneNumber = phoneNumberIL.editText?.text.toString()
            val password = pwdIL.editText?.text.toString()
            val confirmPassword = confirmPwdIL.editText?.text.toString()
            if (password.equals(confirmPassword) != true)
            {
                Toast.makeText(this, "Confirm Password does not match",Toast.LENGTH_LONG).show()
            }
            else
            {
                val newCustomer = Customer(phoneNumber, password)
                registerNewCustomer(newCustomer)
            }
        }

        binding.signinbtn.setOnClickListener {
            finish()
        }
    }

    fun registerNewCustomer(customer: Customer)
    {
        SignUpService.signUpService.registerCustomer(customer).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val dataFromResponse = response?.body()?.string()
                try {
                    val responseToJSON = JSONObject(dataFromResponse)
                    if (responseToJSON.has("phone"))
                        Toast.makeText(this@SignupActivity, responseToJSON.getString("phone").toString(),Toast.LENGTH_LONG).show()
                    else if (responseToJSON.has("password"))
                        Toast.makeText(this@SignupActivity, responseToJSON.getString("password").toString(),Toast.LENGTH_LONG).show()
                }
                catch (e: Throwable){
                    //2 cases: Success because respone will only contain a String ID so can not convert to JSON in try
                    //or response error
                    Log.i("error", e.toString())
                    finish()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@SignupActivity, t.toString(),Toast.LENGTH_LONG).show()
            }

        })
    }
}