package com.example.wibercustomer.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.wibercustomer.R
import com.example.wibercustomer.api.RouteService
import com.example.wibercustomer.databinding.ActivityHomeBinding
import com.example.wibercustomer.viewmodels.HomeViewModel
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.util.GeoPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

import java.util.*


class HomeActivity : AppCompatActivity(), OnMapReadyCallback{
    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    internal lateinit var destinatioLocation : LatLng
    internal var destinationLocationMarker : Marker? = null
    private lateinit var  startLocation : LatLng
    private var distance : Double = 0.0


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
        binding.findButton.setOnClickListener {
            val destination = binding.destinationInputLayout.editText!!.text
            val coder = Geocoder(applicationContext)
            try {
                val adresses: ArrayList<Address> =
                    coder.getFromLocationName(destination.toString(), 1) as ArrayList<Address>
                if (!adresses.isEmpty()){
                    val location: Address = adresses.get(0)
                    destinatioLocation = LatLng(location.latitude, location.longitude)

                    if (destinationLocationMarker != null)
                    {
                        mMap.clear()
                        mMap.addMarker(
                            com.google.android.gms.maps.model.MarkerOptions()
                                .position(startLocation)
                                .title("You are here")
                        )
                        destinationLocationMarker!!.remove()
                    }
                    //Put marker on map on that LatLng
                    destinationLocationMarker = mMap.addMarker(
                        MarkerOptions().position(destinatioLocation).title("Destination")
                    )

                    //Animate and Zoon on that map location

                    //Animate and Zoon on that map location
                    RouteService.routeService.getPolyline("5b3ce3597851110001cf62488405514894ed4132af5ce11377c3a573",
                        "${startLocation.longitude},${startLocation.latitude}",
                        "${destinatioLocation.longitude},${destinatioLocation.latitude}")
                        .enqueue(object : Callback<ResponseBody> {
                            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                                Log.e("error Api", t.toString())
                            }

                            override fun onResponse(
                                call: Call<ResponseBody>?,
                                response: Response<ResponseBody>?
                            ) {
                                val gson = GsonBuilder().create()
                                val dataFromApi = response?.body()?.string()
                                val elementObj = JSONObject(dataFromApi)
                                val lineString = elementObj.getJSONArray("features")
                                    .getJSONObject(0)
                                    .getJSONObject("geometry")
                                    .getJSONArray("coordinates")
                                val distanceAPI = (elementObj.getJSONArray("features")
                                    .getJSONObject(0)
                                    .getJSONObject("properties")
                                    .getJSONArray("segments")
                                    .getJSONObject(0)
                                    .get("distance"))
                                distance = distanceAPI as Double
                                Toast.makeText(applicationContext,"Distance: ${distance}m" , Toast.LENGTH_SHORT).show()
                                val geoPoints = ArrayList<LatLng>();
                                (0 until lineString.length()).forEach{
                                    val iteratorCoordinate = lineString.get(it) as JSONArray
                                    geoPoints.add(LatLng(iteratorCoordinate[1] as Double, iteratorCoordinate[0] as Double))
                                }

                                //add your points here
                                var lineoption = PolylineOptions()
                                lineoption.addAll(geoPoints)
                                lineoption.width(15F)
                                lineoption.color(Color.BLUE)
                                lineoption.geodesic(true)
                                mMap.addPolyline(lineoption)
                            }
                        })
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(destinatioLocation))
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
                }
            }
            catch(e : IOException) {
                e.printStackTrace();
            }
        }
    }


    private fun getDirectionURL(origin:LatLng, dest:LatLng, secret: String) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&sensor=false" +
                "&mode=driving" +
                "&key=$secret"
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker at current user location and move the camera
        mMap.uiSettings.isZoomControlsEnabled = false
        val permissionCheck = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            Log.i("info", "permission denied")
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION), 1)
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
}