package com.example.wibercustomer

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import com.example.wibercustomer.fragments.ConfirmTaxiFragment
import com.example.wibercustomer.fragments.SignInFragment
import com.stripe.android.PaymentConfiguration
import org.osmdroid.config.Configuration

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PaymentConfiguration.init(
            applicationContext,
            "pk_test_51LIYdhG4i8Ax0wzrc2r2q6arJe8ZggAJ1bL93xbht6nZBnJPjdbCgPm3YJ5V5l64rCWzfAdSXSjSXyio66tfxnPD00JgUunXWI"
        )

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        Configuration.getInstance().setUserAgentValue(getPackageName());

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_main_container, ConfirmTaxiFragment())
            .commit();

        supportActionBar?.hide()
    }
}