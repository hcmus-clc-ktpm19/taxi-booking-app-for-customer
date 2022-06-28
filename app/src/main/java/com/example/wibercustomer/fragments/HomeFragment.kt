package com.example.wibercustomer.fragments

import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wibercustomer.BuildConfig
import com.example.wibercustomer.R
import com.example.wibercustomer.viewmodels.HomeViewModel
import com.google.android.material.textfield.TextInputLayout
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration

import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var map: MapView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)


        val startingEdt = root.findViewById<TextInputLayout>(R.id.startingInputText)
        val arrivingEdt = root.findViewById<TextInputLayout>(R.id.arrivingInputText)

        homeViewModel.startingText.observe(viewLifecycleOwner){
            startingEdt.editText?.setText(it)
        }

        homeViewModel.arrivingText.observe(viewLifecycleOwner){
            arrivingEdt.editText?.setText(it)
        }

        map = root.findViewById(R.id.mapView);

        map.setUseDataConnection(true)
        map.setTileSource(TileSourceFactory.MAPNIK);

        val mapController: IMapController

        mapController = map.getController()

        mapController.setZoom(19)

        val startPoint = GeoPoint(10.762622, 106.660172)

        mapController.setCenter(startPoint)



        return root
    }
}