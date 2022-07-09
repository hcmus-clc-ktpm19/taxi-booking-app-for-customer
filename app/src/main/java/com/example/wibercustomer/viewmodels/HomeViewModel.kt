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



}
