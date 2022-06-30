package com.example.wibercustomer.fragments

import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.wibercustomer.BuildConfig
import com.example.wibercustomer.R
import com.example.wibercustomer.viewmodels.HomeViewModel
import com.google.android.material.textfield.TextInputLayout
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration

import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.*
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


        //here set map
        map = root.findViewById(R.id.mapView);

        map.setUseDataConnection(true)
        map.setTileSource(TileSourceFactory.MAPNIK);
        //set map controller (zoom, map center)
        val mapController: IMapController

        mapController = map.getController()

        mapController.setZoom(19)

        val startPoint = GeoPoint(10.762622, 106.660172)

        mapController.setCenter(startPoint)
        //create a start marker on map
        val startMarker = Marker(map)

        startMarker.position = startPoint
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)

        startMarker.setIcon(resources.getDrawable(R.drawable.ic_location))
        startMarker.setInfoWindow(null)

        //create a list of point to draw a connection line between points
        val geoPoints = ArrayList<GeoPoint>();
        //add your points here
        val line = Polyline();
        geoPoints.add(startPoint) //Call API (openrouteservice) to get list of point to draw line from start point to destination point
        geoPoints.add(GeoPoint(10.862632, 106.660572))
        geoPoints.add(GeoPoint(10.642, 106.660872))
        geoPoints.add(GeoPoint(10.2652, 106.667872))
        line.color = Color.parseColor("#F40505")
        line.setPoints(geoPoints);

        //need to add line and marker to map
        map.overlays.add(line);

        map.overlays.add(startMarker)

        map.invalidate()

        return root
    }
}