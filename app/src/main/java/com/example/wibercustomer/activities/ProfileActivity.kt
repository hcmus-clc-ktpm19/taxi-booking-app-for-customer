package com.example.wibercustomer.activities

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.wibercustomer.activities.SigninActivity.Companion.authCustomerTokenFromSignIn
import com.example.wibercustomer.activities.SigninActivity.Companion.phoneNumberLoginFromSignIn
import com.example.wibercustomer.api.AuthService
import com.example.wibercustomer.api.CustomerService
import com.example.wibercustomer.databinding.ActivityProfileBinding
import com.example.wibercustomer.models.CustomerInfo
import com.example.wibercustomer.models.roleEnum
import com.example.wibercustomer.viewmodels.ProfileViewModel
import dmax.dialog.SpotsDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var alertDialog : AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        alertDialog = SpotsDialog.Builder().setContext(this)
            .setCancelable(false)
            .setMessage("Uploading")
            .build()

        val nameLayout = binding.nameInputLayout
        val newPasswordLayout = binding.newPasswordInputLayout

        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        profileViewModel.nameText.observe(this){
            nameLayout.editText?.setText(it)
        }

        profileViewModel.phoneNumberText.observe(this){
            binding.phoneNumberInputLayout.editText?.setText(it)
        }
        profileViewModel.getCustomerInfo(phoneNumberLoginFromSignIn, authCustomerTokenFromSignIn)

        binding.saveBtn.setOnClickListener { saveBtnOnclick ->
            if (nameLayout.editText?.text?.isNotEmpty() == true)
            {
                alertDialog.show()
                GlobalScope.launch {
                    val accountDetail = AuthService.authService.getAccountDetail(
                        phoneNumberLoginFromSignIn, "Bearer ${authCustomerTokenFromSignIn.accessToken}")
                    if (accountDetail != null) {
                        if (newPasswordLayout.editText?.text?.isNotEmpty() == true) {
                            accountDetail.password = newPasswordLayout.editText!!.text.toString()
                            try {
                                AuthService.authService.updatePasswordAPI(
                                    accountDetail.id, accountDetail,
                                    "Bearer ${authCustomerTokenFromSignIn.accessToken}"
                                )
                            } catch (e: Exception) {
                                alertDialog.dismiss()
                                Toast.makeText(
                                    this@ProfileActivity,
                                    (e as? HttpException)?.response()?.errorBody()?.string(),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        val customerUpdate = CustomerInfo(
                            accountDetail.id, phoneNumberLoginFromSignIn,
                            nameLayout.editText!!.text.toString(), roleEnum.CUSTOMER
                        )
                        updateCustomerInfo(customerUpdate)
                    }
                    else
                        Toast.makeText(this@ProfileActivity, "Error while saving", Toast.LENGTH_LONG).show()
                }
            }
            else
                Toast.makeText(this@ProfileActivity, "Please input name", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateCustomerInfo(customerInfo: CustomerInfo)
    {
        CustomerService.customerService.updateCustomerInfoAPI(customerInfo, "Bearer ${authCustomerTokenFromSignIn.accessToken}")
            .enqueue(object : Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful)
                    {
                        Toast.makeText(this@ProfileActivity, "Update successfully", Toast.LENGTH_LONG).show()
                        profileViewModel.getCustomerInfo(customerInfo.phone, authCustomerTokenFromSignIn)
                    }
                    else
                    {
                        Toast.makeText(this@ProfileActivity, "Error while updating", Toast.LENGTH_LONG).show()
                    }
                    alertDialog.dismiss()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@ProfileActivity, "Unable to connect to server", Toast.LENGTH_LONG).show()
                }

            })
    }
}