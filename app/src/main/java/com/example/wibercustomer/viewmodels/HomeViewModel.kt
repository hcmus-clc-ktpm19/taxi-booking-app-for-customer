package com.example.wibercustomer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.MapView

class HomeViewModel : ViewModel() {

    private val _startingText = MutableLiveData<String>().apply {
        value = "Start here"
    }

    val startingText : LiveData<String> = _startingText

    private val _arrivingText = MutableLiveData<String>().apply {
        value = "Arrive here"
    }

    val arrivingText : LiveData<String> = _arrivingText


    //chua biet dung viewmodel khuc nay lam sao se hoi chi PA

//    private lateinit var mapConfig:MapView
//    private lateinit var mapController: IMapController
//    private lateinit var startPoint :GeoPoint
//
//    private val _mapView = MutableLiveData<MapView>().apply {
//        value = mapConfig
//    }
//
//    val mapView : LiveData<MapView> = _mapView
//
//    private fun initMap() {
//        mapConfig.setUseDataConnection(true)
//        mapConfig.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
//        mapController = mapConfig.getController()
//
//        mapController.setZoom(15)
//
//        startPoint = GeoPoint(10.762622, 106.660172)
//
//        mapController.setCenter(startPoint)
//
//    }

}
