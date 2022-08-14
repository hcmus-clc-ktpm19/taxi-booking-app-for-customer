package com.example.wibercustomer.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.wibercustomer.R

class FinishTripActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish_trip)
        val backBtn = findViewById<ImageView>(R.id.backToHomeWhenFinish)
        backBtn.setOnClickListener {
            finish()
        }
    }
}