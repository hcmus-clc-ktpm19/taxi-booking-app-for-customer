package com.example.wibercustomer.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.wibercustomer.R
import com.example.wibercustomer.databinding.ActivityConfirmTaxiBinding
import com.example.wibercustomer.viewmodels.ConfirmTaxiViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class ConfirmTaxi : AppCompatActivity() {

    private lateinit var confirmTaxiViewModel: ConfirmTaxiViewModel
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityConfirmTaxiBinding
    private lateinit var payButton: Button
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConfirmTaxiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        confirmTaxiViewModel = ViewModelProvider(this).get(ConfirmTaxiViewModel::class.java)
        val arrivingEdt = binding.arrivingEditText

//        confirmTaxiViewModel.startingText.observe(viewLifecycleOwner) {
//            startingEdt.editText?.setText(it)
//        }
//
//        confirmTaxiViewModel.arrivingText.observe(viewLifecycleOwner) {
//            arrivingEdt.editText?.setText(it)
//        }
        payButton = binding.payButton

        // init map fragment
//        val supportMapFragment: SupportMapFragment =
//            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
//        // async map
//        supportMapFragment.getMapAsync(this)
//        fusedLocationProviderClient =
//            LocationServices.getFusedLocationProviderClient(this)

    }

//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//
//        // Add a marker at current user location and move the camera
//        mMap.uiSettings.isZoomControlsEnabled = false
//        setUpMap()
//    }

//    @SuppressLint("MissingPermission")
//    private fun setUpMap() {
//        val permissionCheck = ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        )
//        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
//            Log.i("info", "permission denied")
//            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
//        } else {
//            Log.i("info", "permission granted")
//            mMap.isMyLocationEnabled = true
//            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
//                if (location != null) {
//                    val currentLatLng = LatLng(location.latitude, location.longitude)
//                    Log.i("info", "current location: $currentLatLng")
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
//                    mMap.addMarker(
//                        com.google.android.gms.maps.model.MarkerOptions()
//                            .position(currentLatLng)
//                            .title("You are here")
//                    )
//                }
//            }
//        }
//    }
}