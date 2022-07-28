package com.example.wibercustomer.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.wibercustomer.api.AuthService
import com.example.wibercustomer.databinding.ActivitySigninBinding
import com.example.wibercustomer.models.AuthToken
import com.example.wibercustomer.models.roleEnum
import com.example.wibercustomer.viewmodels.SignInViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
            loginviewModel.signIn(
                phoneIL.editText?.text.toString(),
                passwordIL.editText?.text.toString()
            )
        }
        val statusObserver = Observer<String>{ status ->
            when (status) {
                "This account is not a customer" -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Error")
                        .setMessage("This account is not a customer")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
                "Wrong phone number or password" -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Error")
                        .setMessage("Invalid phone number or password")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
                "Success" -> {
                    startActivity(Intent(this@SigninActivity, HomeActivity::class.java))
                }
                else -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Error")
                        .setMessage(status)
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        }
        loginviewModel.status.observe(this, statusObserver)
    }
}