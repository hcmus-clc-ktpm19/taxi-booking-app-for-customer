package com.example.wibercustomer.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.text.TextUtils
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
import com.example.wibercustomer.models.enums.CarType
import com.example.wibercustomer.strategies.cardMethod
import com.example.wibercustomer.strategies.cashMethod
import com.example.wibercustomer.utils.Const
import com.example.wibercustomer.utils.StompUtils
import com.example.wibercustomer.viewmodels.HomeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import com.stripe.android.view.CardMultilineWidget
import com.stripe.android.view.CardValidCallback
import dmax.dialog.SpotsDialog
import org.json.JSONException
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.StompMessage
import java.io.IOException
import java.util.*
import kotlin.math.ln


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
    private lateinit var bottomPaymentLayout: LinearLayout
    private lateinit var bottomPaymentBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var currentCarRequest: CarRequest
    private lateinit var alertDialog: AlertDialog
    private lateinit var dialog: Dialog

    internal var driverLocationMarker: Marker? = null

    val paymentMethods = arrayOf("Cash", "VISA/MASTER Card")

    @SuppressLint("CheckResult", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        bottomLayout = findViewById(R.id.bottom_sheet_layout)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomLayout)

        bottomPaymentLayout = findViewById(R.id.bottom_sheet_payment)
        bottomPaymentBehavior = BottomSheetBehavior.from(bottomPaymentLayout)

        val fromLayout = bottomLayout.findViewById<TextInputLayout>(R.id.fromInputLayout)
        val toWhereLayout = bottomLayout.findViewById<TextInputLayout>(R.id.toWhereInputLayout)
        val distanceLayout = bottomLayout.findViewById<TextInputLayout>(R.id.distanceToGo)
        val moneyLayout = bottomPaymentLayout.findViewById<TextInputLayout>(R.id.moneyToPay)
        val requestCarBtn = bottomLayout.findViewById<Button>(R.id.requestCar)
        val paymentSpinner = bottomPaymentLayout.findViewById<Spinner>(R.id.paymentMethodSpinner)

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

        homeViewModel.paymentValue.observe(this) {
            moneyLayout.editText?.setText("${it.moneyToPay.toInt()} VND")
        }

        homeViewModel.distanceValue.observe(this) {
            distanceLayout.editText?.setText("${it.toString()}m")
        }

        homeViewModel.carRequestValue.observe(this) {
            currentCarRequest = it
            binding.testState.text = currentCarRequest.status
        }

        val arrayAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            paymentMethods
        )

        paymentSpinner.adapter = arrayAdapter


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
                            destinationLocationMarker!!.remove()
                        }
                        //Put marker on map on that LatLng
                        destinationLocationMarker = mMap.addMarker(
                            MarkerOptions().position(destinatioLocation).title("Destination")
                        )
                        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                            startLocation = LatLng(it.latitude, it.longitude)
                            homeViewModel.getDirectionAndDistance(
                                startLocation,
                                destinatioLocation,
                                coder
                            )
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(destinatioLocation))
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
                        }
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

        val payBtn = bottomPaymentLayout.findViewById<Button>(R.id.payButton)
        requestCarBtn.setOnClickListener { reqBtnOnClick ->
            if (currentCarRequest.isFree()) {
                homeViewModel.calculateMoneyValue()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                bottomPaymentBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else
                Toast.makeText(this, "You are currently unable to book", Toast.LENGTH_LONG).show()
        }

        val multiCardWidget =
            bottomPaymentLayout.findViewById<CardMultilineWidget>(R.id.card_multiline_widget)

        paymentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (paymentMethods[p2].equals("Cash")) {
                    homeViewModel.paymentValue.value?.currentPayment = cashMethod()
                    homeViewModel.calculateMoneyValue()
                    multiCardWidget.visibility = View.GONE
                    payBtn.isEnabled = true
                    payBtn.isClickable = true
                    payBtn.setBackgroundColor(getResources().getColor(R.color.primary_color))
                } else {
                    homeViewModel.paymentValue.value?.currentPayment = cardMethod()
                    homeViewModel.calculateMoneyValue()
                    multiCardWidget.visibility = View.VISIBLE
                    multiCardWidget.setCardValidCallback(object : CardValidCallback {
                        override fun onInputChanged(
                            isValid: Boolean,
                            invalidFields: Set<CardValidCallback.Fields>
                        ) {
                            if (isValid) {
                                payBtn.isEnabled = true
                                payBtn.isClickable = true
                                payBtn.setBackgroundColor(getResources().getColor(R.color.primary_color))
                            } else {
                                payBtn.isEnabled = false
                                payBtn.isClickable = false
                                payBtn.setBackgroundColor(Color.parseColor("#DAD6D6D6"))
                            }
                        }
                    })
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }



        payBtn.setOnClickListener {
            val carTypeGroup = bottomLayout.findViewById<RadioGroup>(R.id.chooseCarTypeGroup)
            val idCheckType = carTypeGroup.checkedRadioButtonId
            val radioBtnChecked = bottomLayout.findViewById<RadioButton>(idCheckType)
            val price = moneyLayout.editText!!.text.toString().split(" ")[0].toDouble()
            val distance = distanceLayout.editText!!.text.toString()
                .substring(0, distanceLayout.editText!!.text.toString().length - 1).toDouble()
            if (radioBtnChecked.text.toString().equals("4 seats"))
                homeViewModel.checkCustomerIsValidAndRequestCar(
                    startLocation,
                    destinatioLocation,
                    CarType.FOUR_SEATS.status,
                    price,
                    distance
                )
            else
                homeViewModel.checkCustomerIsValidAndRequestCar(
                    startLocation,
                    destinatioLocation,
                    CarType.SEVEN_SEATS.status,
                    price,
                    distance
                )
        }

        val routeStatusObserver = Observer<Boolean> { status ->
            when (status) {
                true -> {
                    alertDialog.dismiss()
                    if (bottomPaymentBehavior.state != BottomSheetBehavior.STATE_HIDDEN)
                        bottomPaymentBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
                else -> {
                    alertDialog.dismiss()
                    Toast.makeText(
                        this@HomeActivity,
                        "Can not connect to Route Service",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        }
        homeViewModel.routeServiceStatus.observe(this, routeStatusObserver)

        val carRequestStatusObserver = Observer<String> { status ->
            when (status) {
                "Request a car successfully" -> {
                    Toast.makeText(
                        this@HomeActivity,
                        currentCarRequest.id.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                    bottomPaymentBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    // socket config
                    val stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, Const.address)
                    Toast.makeText(this, "Start connecting to server", Toast.LENGTH_SHORT).show()
                    stompClient.connect()
                    StompUtils.lifecycle(stompClient)
                    Log.i(Const.TAG, "Subscribe chat endpoint to receive response")
                    stompClient.topic(
                        Const.chatResponse.replace(
                            Const.placeholder,
                            currentCarRequest.id.toString()
                        )
                    ).subscribe { stompMessage: StompMessage ->
                        val jsonObject = JSONObject(stompMessage.payload)
                        Log.i(Const.TAG, "Receive: $jsonObject")
                        runOnUiThread {
                            try {
                                // show driver information here
                                // TODO: we need to update this line because when the 2nd message come, it will change the status to FREE again.
                                if (!currentCarRequest.isAccepted()) {
                                    val message = jsonObject.getString("message")
                                    val driverInfo = message.split(" | ")
                                    Log.i("driverInfo", driverInfo[0])
                                    Log.i("driverInfo", driverInfo[1])
                                    Log.i("driverInfo", driverInfo[2])
                                    homeViewModel.nextStateCarRequest(currentCarRequest)
                                    showDriverInfoDialog(driverInfo[0], driverInfo[1], driverInfo[2])
//                                    MaterialAlertDialogBuilder(this@HomeActivity)
//                                        .setTitle("Result")
//                                        .setMessage(message)
//                                        .setPositiveButton("OK") { dialog, which ->
//                                            dialog.dismiss()
//                                        }
//                                        .show()

                                } else {
                                    if (jsonObject.has("message") && !jsonObject.isNull("message")) {
                                        //accepted -> free
                                        val message = jsonObject.getString("message")
                                        if (message.equals("Finished")) {
                                            homeViewModel.nextStateCarRequest(currentCarRequest)
                                            mMap.clear()
                                            //stop listen to old socket
                                            stompClient.disconnect()
                                            startActivity(
                                                Intent(
                                                    this@HomeActivity,
                                                    FinishTripActivity::class.java
                                                )
                                            )
                                        }
                                    } else {
                                        val latDriver = jsonObject.getDouble("latDriver")
                                        val lngDriver = jsonObject.getDouble("lngDriver")
                                        Log.i("testing", "$latDriver $lngDriver")
                                        homeViewModel.setDriverValue(LatLng(latDriver, lngDriver))
                                    }
                                }
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

        homeViewModel.driverDestinationValue.observe(this) { it ->
            if (it != null) {
                if (driverLocationMarker != null) {
                    driverLocationMarker!!.remove()
                }
                //Put marker on map on that LatLng
                driverLocationMarker = mMap.addMarker(
                    MarkerOptions().position(it).title("Driver")
                        .icon(bitMapFromVector(R.drawable.ic_driver))
                        .flat(true)
                )

                mMap.moveCamera(CameraUpdateFactory.newLatLng(it))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
            }
        }
    }

    override fun onPause() {
        super.onPause()

    }

    fun showDriverInfoDialog(name: String, phone: String, avatar: String) {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.driver_info)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val driverImageView = dialog.findViewById<ImageView>(R.id.driver_avatar)
        val driverName = dialog.findViewById<TextView>(R.id.driver_name)
        val driverPhone = dialog.findViewById<TextView>(R.id.driver_phone)
        val okBtn = dialog.findViewById<Button>(R.id.ok_button)

        // load image
        if (!TextUtils.isEmpty(avatar)) Picasso.get().load(avatar)
            .into(driverImageView)
        driverName.text = name
        driverPhone.text = phone
        dialog.show()
        okBtn.setOnClickListener {
            dialog.dismiss()
        }
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

    private fun bitMapFromVector(vectorResID: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(this, vectorResID)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable!!.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}