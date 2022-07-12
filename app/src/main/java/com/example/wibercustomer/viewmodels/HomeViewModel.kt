package com.example.wibercustomer.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wibercustomer.api.RouteService
import com.google.android.gms.maps.model.LatLng
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val _geoPoint = MutableLiveData<ArrayList<LatLng>>().apply {
        value = ArrayList<LatLng>()
    }
    val geoPoint: LiveData<ArrayList<LatLng>> = _geoPoint

    private val _distanceValue = MutableLiveData<Double>().apply {
        value = 0.0
    }
    val distanceValue: LiveData<Double> = _distanceValue

    fun getDirectionAndDistance(startLocation: LatLng, destinatioLocation: LatLng) {
        RouteService.routeService.getPolyline(
            "5b3ce3597851110001cf62488405514894ed4132af5ce11377c3a573",
            "${startLocation.longitude},${startLocation.latitude}",
            "${destinatioLocation.longitude},${destinatioLocation.latitude}"
        )
            .enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                    Log.e("error Api", t.toString())
                }

                override fun onResponse(
                    call: Call<ResponseBody>?,
                    response: Response<ResponseBody>?
                ) {
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

                    _distanceValue.value = distanceAPI as Double
                    val coordinates = ArrayList<LatLng>()
                    (0 until lineString.length()).forEach {
                        val iteratorCoordinate = lineString.get(it) as JSONArray
                        coordinates.add(
                            LatLng(
                                iteratorCoordinate[1] as Double,
                                iteratorCoordinate[0] as Double
                            )
                        )
                    }
                    _geoPoint.value = coordinates
                }
            })
    }
}
