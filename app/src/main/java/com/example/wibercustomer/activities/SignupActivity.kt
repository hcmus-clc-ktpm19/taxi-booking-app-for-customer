package com.example.wibercustomer.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.wibercustomer.R
import com.example.wibercustomer.databinding.ActivitySignupBinding
import com.example.wibercustomer.viewmodels.SignUpViewModel

class SignupActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySignupBinding
    private lateinit var signUpviewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        signUpviewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)

        signUpviewModel.phoneNumberText.observe(this){
            binding.phoneNumberInputText.editText?.setText(it)
        }

        signUpviewModel.passwordText.observe(this){
            binding.paswordInputText.editText?.setText(it)
        }

        signUpviewModel.confirmPasswordText.observe(this){
            binding.confirmPaswordInputText.editText?.setText(it)
        }

        binding.signinbtn.setOnClickListener {
            finish()
        }
    }
}