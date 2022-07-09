package com.example.wibercustomer.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.wibercustomer.R
import com.example.wibercustomer.databinding.ActivityHomeBinding
import com.example.wibercustomer.viewmodels.HomeViewModel
import com.example.wibercustomer.viewmodels.SignInViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        //map

//        val supportMapFragment: SupportMapFragment =
//            binding.findFragmentById(R.id.map) as SupportMapFragment
//        // async map
//        supportMapFragment.getMapAsync(this)
//
//        fusedLocationProviderClient =
//            LocationServices.getFusedLocationProviderClient(this)
    }

//    @SuppressLint("MissingPermission")
//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//
//        // Add a marker at current user location and move the camera
//        mMap.uiSettings.isZoomControlsEnabled = false
//        mMap.isMyLocationEnabled = true
//        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
//            if (location != null) {
//                val currentLatLng = LatLng(location.latitude, location.longitude)
//                Log.i("info", "current location: $currentLatLng")
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
//                mMap.addMarker(
//                    com.google.android.gms.maps.model.MarkerOptions()
//                        .position(currentLatLng)
//                        .title("You are here")
//                )
//            }
//        }
//    }
}