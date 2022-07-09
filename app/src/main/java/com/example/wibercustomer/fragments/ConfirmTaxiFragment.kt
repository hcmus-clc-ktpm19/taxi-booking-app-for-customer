package com.example.wibercustomer.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.wibercustomer.R
import com.example.wibercustomer.databinding.FragmentConfirmTaxiBinding
import com.example.wibercustomer.viewmodels.ConfirmTaxiViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class ConfirmTaxiFragment : Fragment(), OnMapReadyCallback {
    private lateinit var confirmTaxiViewModel: ConfirmTaxiViewModel
    private lateinit var mMap: GoogleMap
    private lateinit var binding: FragmentConfirmTaxiBinding
    private lateinit var payButton: Button
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        confirmTaxiViewModel = ViewModelProvider(this).get(ConfirmTaxiViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_confirm_taxi, container, false)

        binding = FragmentConfirmTaxiBinding.bind(root)
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
        val supportMapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        // async map
        supportMapFragment.getMapAsync(this)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        return root
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker at current user location and move the camera
        mMap.uiSettings.isZoomControlsEnabled = false
        setUpMap()
    }

    @SuppressLint("MissingPermission")
    private fun setUpMap() {
        val permissionCheck = ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            Log.i("info", "permission denied")
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            Log.i("info", "permission granted")
            mMap.isMyLocationEnabled = true
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    Log.i("info", "current location: $currentLatLng")
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    mMap.addMarker(
                        com.google.android.gms.maps.model.MarkerOptions()
                            .position(currentLatLng)
                            .title("You are here")
                    )
                }
            }
        }
    }
}
