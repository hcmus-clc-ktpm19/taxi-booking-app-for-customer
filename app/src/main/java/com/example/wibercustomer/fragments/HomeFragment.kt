package com.example.wibercustomer.fragments

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.wibercustomer.R
import com.example.wibercustomer.api.RouteService
import com.example.wibercustomer.viewmodels.HomeViewModel
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.api.IMapController

import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList


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

        val testTesting = root.findViewById<Button>(R.id.callApi)
        //106.660172,10.762622&end=106.7,10.762622
        val startPoint = GeoPoint(10.762622, 106.660172)
        val endPoint = GeoPoint(10.762622, 106.7)

        //here set map
        map = root.findViewById(R.id.mapView);

        map.setUseDataConnection(true)
        map.setTileSource(TileSourceFactory.MAPNIK);
        //set map controller (zoom, map center)
        val mapController: IMapController

        mapController = map.getController()

        mapController.setZoom(19)



        mapController.setCenter(startPoint)
        //create a start marker on map
        val startMarker = Marker(map)

        startMarker.position = startPoint
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)

        startMarker.setIcon(resources.getDrawable(R.drawable.ic_location))
        startMarker.setInfoWindow(null)

        //need to add line and marker to map
        testTesting.setOnClickListener {
            RouteService.routeService.getPolyline("5b3ce3597851110001cf62488405514894ed4132af5ce11377c3a573",
                "${startPoint.longitude},${startPoint.latitude}",
                        "${endPoint.longitude},${endPoint.latitude}")
                .enqueue(object : Callback<ResponseBody>{
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
                        val geoPoints = ArrayList<GeoPoint>();
                        (0 until lineString.length()).forEach{
                            val iteratorCoordinate = lineString.get(it) as JSONArray
                            geoPoints.add(GeoPoint(iteratorCoordinate[1] as Double, iteratorCoordinate[0] as Double))
                        }

                        //add your points here
                        val line = Polyline();
                        Log.i("points", geoPoints.toString())
                        line.color = Color.parseColor("#F40505")
                        line.setPoints(geoPoints);

                        map.overlays.add(line);

                        map.invalidate()
                    }
                })

        }

        map.overlays.add(startMarker)

        map.invalidate()



        return root
    }
}