package com.example.wibercustomer.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.wibercustomer.databinding.ActivitySigninBinding
import com.example.wibercustomer.viewmodels.SignInViewModel

class SigninActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private lateinit var loginviewModel: SignInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySigninBinding.inflate(layoutInflater)

        setContentView(binding.root)

        loginviewModel = ViewModelProvider(this).get(SignInViewModel::class.java)

        loginviewModel.phoneNumberText.observe(this) {
            binding.phoneNumberInputLayout.editText?.setText(it)
        }


        loginviewModel.passwordText.observe(this) {
            binding.paswordInputText.editText?.setText(it)
        }

        binding.signupbtn.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.signinbtn.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

    }
}