package com.example.wibercustomer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.wibercustomer.fragments.HomeFragment
import com.example.wibercustomer.fragments.SignInFragment
import org.osmdroid.config.Configuration


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        Configuration.getInstance().setUserAgentValue(getPackageName());

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_main_container,HomeFragment())
            .commit();

        supportActionBar?.hide()
    }
}