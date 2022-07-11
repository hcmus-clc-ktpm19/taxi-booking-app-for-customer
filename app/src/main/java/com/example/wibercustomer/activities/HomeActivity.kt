package com.example.wibercustomer.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.wibercustomer.MainActivity
import com.example.wibercustomer.R
import com.example.wibercustomer.databinding.ActivityHomeBinding
import com.example.wibercustomer.viewmodels.HomeViewModel
import com.example.wibercustomer.viewmodels.SignInViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.*

class HomeActivity : AppCompatActivity(), OnMapReadyCallback{
    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    internal var mGoogleApiClient : GoogleApiClient? = null
    internal lateinit var destinatioLocation : Location
    internal var destinationLocationMarker : Marker? = null
    internal lateinit var destinationLocationRequest : LocationRequest


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        var toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Home"
        var drawerLayout = binding.drawerLayout
        var navigationView = binding.navView
        var actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.openNavDrawer,
            R.string.closeNavDrawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_payment_method -> {
                    val intent = Intent(this, PaymentMethodActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_history -> {
                    startActivity(Intent(this,HistoryActivity::class.java))
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        //map
        val supportMapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        // async map
        Log.i("info", "async map")
        supportMapFragment.getMapAsync(this)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        Places.initialize(getApplicationContext(), "AIzaSyBnJElLitt6oQ2cwCL4FGIgYlZFPn1UKPE");
        val autoCompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        // Set up a PlaceSelectionListener to handle the response.
        autoCompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val destionation = place.latLng
                if (destinationLocationMarker!= null)
                    destinationLocationMarker!!.remove()
                destinationLocationMarker = mMap.addMarker(MarkerOptions().position(destionation))
            }

            override fun onError(status: Status) {
                Log.i("An loi", "An error occurred: $status")
            }
        })

    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker at current user location and move the camera
        mMap.uiSettings.isZoomControlsEnabled = false
        mMap.isMyLocationEnabled = true
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                Log.i("info", "current location: $currentLatLng")
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                mMap.addMarker(
                        MarkerOptions()
                        .position(currentLatLng)
                        .title("You are here")
                )
            }
        }
    }

//    protected fun buildGoogleApiClient(){
//        mGoogleApiClient = GoogleApiClient.Builder(this)
//            .addConnectionCallbacks(this)
//            .addOnConnectionFailedListener(this)
//            .addApi(LocationServices.API).build()
//        mGoogleApiClient!!.connect()
//    }
//
//    override fun onLocationChanged(p0: Location) {
//        destinatioLocation = p0
//        if (destinationLocationMarker != null)
//            destinationLocationMarker!!.remove()
//
//        val latLng = LatLng(p0.latitude, p0.longitude)
//        val markerOption = MarkerOptions()
//        markerOption.position(latLng)
//        markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
//        destinationLocationMarker = mMap.addMarker(markerOption)
//
//        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
//
//    }
//
//    override fun onConnected(p0: Bundle?) {
//        destinationLocationRequest = LocationRequest()
//        destinationLocationRequest.interval = 1000
//        destinationLocationRequest.fastestInterval = 1000
//        destinationLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
//        {
//
//        }
//    }
//
//    override fun onConnectionSuspended(p0: Int) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onConnectionFailed(p0: ConnectionResult) {
//        TODO("Not yet implemented")
//    }
}