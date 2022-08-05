package com.example.wibercustomer.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.wibercustomer.R
import com.example.wibercustomer.databinding.ActivityHomeBinding
import com.example.wibercustomer.models.CarRequest
import com.example.wibercustomer.utils.Const
import com.example.wibercustomer.utils.StompUtils
import com.example.wibercustomer.viewmodels.HomeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import dmax.dialog.SpotsDialog
import org.json.JSONException
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.StompMessage
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class HomeActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    internal lateinit var destinatioLocation: LatLng
    internal var destinationLocationMarker: Marker? = null
    private lateinit var startLocation: LatLng

    private lateinit var bottomLayout: LinearLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var currentCarRequest: CarRequest
    private lateinit var alertDialog : AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        bottomLayout = findViewById(R.id.bottom_sheet_layout)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomLayout)
        val fromLayout = bottomLayout.findViewById<TextInputLayout>(R.id.fromInputLayout)
        val toWhereLayout = bottomLayout.findViewById<TextInputLayout>(R.id.toWhereInputLayout)
        val distanceLayout = bottomLayout.findViewById<TextInputLayout>(R.id.distanceToGo)
        val moneyLayout = bottomLayout.findViewById<TextInputLayout>(R.id.moneyToPay)
        val requestCarbtn = bottomLayout.findViewById<Button>(R.id.requestCar)

        alertDialog = SpotsDialog.Builder().setContext(this)
            .setCancelable(false)
            .setMessage("Uploading")
            .build()

        homeViewModel.pickingAddressValue.observe(this) {
            fromLayout.editText?.setText(it)
        }

        homeViewModel.arrivingAddressValue.observe(this) {
            toWhereLayout.editText?.setText(it)
        }

        homeViewModel.distanceValue.observe(this) {
            distanceLayout.editText?.setText("${it.toString()}m")
        }

        homeViewModel.moneyValue.observe(this) {
            moneyLayout.editText?.setText("${it.toInt().toString()} VND")
        }

        homeViewModel.carRequestValue.observe(this) {
            currentCarRequest = it
            binding.testState.text = currentCarRequest.status
        }

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Home"
        val drawerLayout = binding.drawerLayout
        val navigationView = binding.navView
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.openNavDrawer,
            R.string.closeNavDrawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_payment_method -> {
                    val intent = Intent(this, PaymentMethodActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        //map
        val supportMapFragment: SupportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        // async map
        supportMapFragment.getMapAsync(this)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)


        binding.destinationInputLayout.editText?.setOnKeyListener(View.OnKeyListener { textView, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {

                val destination = binding.destinationInputLayout.editText!!.text
                val coder = Geocoder(applicationContext, Locale.getDefault())
                try {
                    val adresses: ArrayList<Address> =
                        coder.getFromLocationName(destination.toString(), 1) as ArrayList<Address>
                    if (adresses.isNotEmpty()) {
                        alertDialog.show()
                        val location: Address = adresses[0]
                        destinatioLocation = LatLng(location.latitude, location.longitude)

                        if (destinationLocationMarker != null) {
                            mMap.clear()
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(startLocation)
                                    .title("You are here")
                            )
                            destinationLocationMarker!!.remove()
                        }
                        //Put marker on map on that LatLng
                        destinationLocationMarker = mMap.addMarker(
                            MarkerOptions().position(destinatioLocation).title("Destination")
                        )
                        homeViewModel.getDirectionAndDistance(
                            startLocation,
                            destinatioLocation,
                            coder
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(destinatioLocation))
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
                    } else
                        Toast.makeText(this, "No address found", Toast.LENGTH_LONG).show()
                } catch (e: IOException) {
                    Log.i("error", e.toString())
                }
                hideKeyboad()
                return@OnKeyListener true
            }
            false
        })


        requestCarbtn.setOnClickListener { reqBtnOnClick ->
            if (currentCarRequest.isFree())
            {
                val carTypeGroup = bottomLayout.findViewById<RadioGroup>(R.id.chooseCarTypeGroup)
                val idCheckType = carTypeGroup.checkedRadioButtonId
                val radioBtnChecked = bottomLayout.findViewById<RadioButton>(idCheckType)
                homeViewModel.checkCustomerIsValidAndRequestCar(startLocation, destinatioLocation, radioBtnChecked.text.toString())
            }
            else
                Toast.makeText(this, "You are currently unable to book", Toast.LENGTH_LONG).show()
        }


        val routeStatusObserver = Observer<Boolean> {status ->
            when (status){
                true -> {
                    alertDialog.dismiss()
                    if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
                else -> {
                    alertDialog.dismiss()
                    Toast.makeText(this@HomeActivity, "Can not connect to Route Service", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
        homeViewModel.routeServiceStatus.observe(this, routeStatusObserver)

        val carRequestStatusObserver = Observer<String> { status ->
            when (status) {
                "Request a car successfully" -> {
                    Toast.makeText(this@HomeActivity, currentCarRequest.id.toString(), Toast.LENGTH_LONG).show()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    // socket config
                    val stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, Const.address)
                    Toast.makeText(this, "Start connecting to server", Toast.LENGTH_SHORT).show()
                    stompClient.connect()
                    StompUtils.lifecycle(stompClient)
                    Log.i(Const.TAG, "Subscribe chat endpoint to receive response")
                    stompClient.topic(Const.chatResponse.replace(Const.placeholder, currentCarRequest.id.toString())).subscribe{
                            stompMessage: StompMessage ->
                        val jsonObject = JSONObject(stompMessage.payload)
                        Log.i(Const.TAG, "Receive: $jsonObject")
                        runOnUiThread {
                            try {
                                // show result here
                                val message = jsonObject.getString("message")
                                homeViewModel.nextStateCarRequest(currentCarRequest)
                                MaterialAlertDialogBuilder(this@HomeActivity)
                                    .setTitle("Result")
                                    .setMessage(message)
                                    .setPositiveButton("OK") { dialog, which ->
                                        dialog.dismiss()
                                    }
                                    .show()
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
                else -> {
                    Toast.makeText(this, "Request failed", Toast.LENGTH_LONG).show()
                }
            }
        }
        homeViewModel.requestCarStatus.observe(this, carRequestStatusObserver)
}

override fun onPause() {
    super.onPause()

}


@SuppressLint("MissingPermission")
override fun onMapReady(googleMap: GoogleMap) {
    mMap = googleMap
    homeViewModel.geoPoint.observe(this) {
        val polylineOptions = PolylineOptions()
        polylineOptions.addAll(it)
        mMap.addPolyline(polylineOptions)
    }
    // Add a marker at current user location and move the camera
    mMap.uiSettings.isZoomControlsEnabled = false
    val permissionCheck = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
        Log.i("info", "permission denied")
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ), 1
        )
    } else {
        Log.i("info", "permission granted")
        mMap.isMyLocationEnabled = true
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                startLocation = LatLng(location.latitude, location.longitude)
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

private fun hideKeyboad() {
    val view: View? = this.currentFocus
    if (view != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

}