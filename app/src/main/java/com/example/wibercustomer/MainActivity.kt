package com.example.wibercustomer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wibercustomer.fragments.SignInFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_main_container,SignInFragment())
            .commit();

        supportActionBar?.hide()
    }
}