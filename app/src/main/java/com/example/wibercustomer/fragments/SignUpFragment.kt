package com.example.wibercustomer.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentManager
import com.example.wibercustomer.R
import com.example.wibercustomer.viewmodels.SignUpViewModel
import com.google.android.material.textfield.TextInputLayout

class SignUpFragment : Fragment() {

    private lateinit var signUpviewModel: SignUpViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        signUpviewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)

        val root =  inflater.inflate(R.layout.fragment_sign_up, container, false)


        val phoneNumberTextField = root.findViewById<TextInputLayout>(R.id.phoneNumberInputText)
        val emailTextField = root.findViewById<TextInputLayout>(R.id.emailInputText)
        val passwordTextField = root.findViewById<TextInputLayout>(R.id.paswordInputText)
        val confirmPasswordTextField = root.findViewById<TextInputLayout>(R.id.confirmPaswordInputText)

        signUpviewModel.phoneNumberText.observe(viewLifecycleOwner){
            phoneNumberTextField.editText?.setText(it)
        }

        signUpviewModel.emailText.observe(viewLifecycleOwner){
            emailTextField.editText?.setText(it)
        }

        signUpviewModel.passwordText.observe(viewLifecycleOwner){
            passwordTextField.editText?.setText(it)
        }

        signUpviewModel.confirmPasswordText.observe(viewLifecycleOwner){
            confirmPasswordTextField.editText?.setText(it)
        }

        val signInbtn = root.findViewById<Button>(R.id.signinbtn)

        signInbtn.setOnClickListener {
            fragmentManager?.popBackStack("Already has an account",
            FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        return root
    }

}