package com.example.wibercustomer.viewmodels

import android.content.Context
import android.location.Geocoder
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wibercustomer.activities.SigninActivity
import com.example.wibercustomer.api.CustomerService
import com.example.wibercustomer.api.RequestCarService
import com.example.wibercustomer.api.RouteService
import com.example.wibercustomer.models.CarRequest
import com.example.wibercustomer.models.CustomerInfo
import com.google.android.gms.maps.model.LatLng
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt

class HomeViewModel : ViewModel() {

    private val _geoPoint = MutableLiveData<ArrayList<LatLng>>().apply {
        value = ArrayList<LatLng>()
    }
    val geoPoint: LiveData<ArrayList<LatLng>> = _geoPoint

    private val _distanceValue = MutableLiveData<Double>().apply {
        value = 0.0
    }
    val distanceValue: LiveData<Double> = _distanceValue

    private val _moneyValue = MutableLiveData<Double>().apply {
        value = 0.0
    }
    val moneyValue: LiveData<Double> = _moneyValue

    private val _pickingAdressValue = MutableLiveData<String>().apply {
        value = ""
    }
    val pickingAddressValue: LiveData<String> = _pickingAdressValue

    private val _arrivingAdressValue = MutableLiveData<String>().apply {
        value = ""
    }
    val arrivingAddressValue: LiveData<String> = _arrivingAdressValue

    fun getDirectionAndDistance(startLocation: LatLng, destinatioLocation: LatLng, geocoder : Geocoder) {
        val startAddress = geocoder.getFromLocation(startLocation.latitude, startLocation.longitude, 1)
        val destinationAddress = geocoder.getFromLocation(destinatioLocation.latitude, destinatioLocation.longitude, 1)

        val startAddressline = startAddress.get(0).getAddressLine(0)
        val toAddressline = destinationAddress.get(0).getAddressLine(0)
        _pickingAdressValue.value = startAddressline.substring(0, startAddressline.indexOf(','))
        _arrivingAdressValue.value = toAddressline.substring(0, toAddressline.indexOf(','))
        RouteService.routeService.getPolyline(
            "5b3ce3597851110001cf62488405514894ed4132af5ce11377c3a573",
            "${startLocation.longitude},${startLocation.latitude}",
            "${destinatioLocation.longitude},${destinatioLocation.latitude}"
        )
            .enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("error Api", t.toString())
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val dataFromApi = response.body()?.string()
                    val elementObj = JSONObject(dataFromApi.toString())
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
                    _moneyValue.value = (distanceAPI / 500) * 3000 + 15000
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

    var requestCarStatus = MutableLiveData<String>()

    fun checkCustomerIsValidAndRequestCar(startLocation: LatLng, destinatioLocation: LatLng,){
        CustomerService.customerService.getAPICustomerInfo(SigninActivity.phoneNumberLoginFromSignIn, "Bearer ${SigninActivity.authCustomerTokenFromSignIn.accessToken}")
            .enqueue(object : Callback<CustomerInfo>{
                override fun onResponse(
                    call: Call<CustomerInfo>,
                    response: Response<CustomerInfo>
                ) {
                    if (response.isSuccessful)
                    {
                        val carRequest = CarRequest(null, response.body()!!.id,
                            pickingAddressValue.value!!, arrivingAddressValue.value!!,
                            startLocation.longitude, startLocation.latitude,
                            destinatioLocation.longitude, destinatioLocation.latitude)

                        requestCarByCustomer(carRequest)

                    }
                    else
                    {
                        requestCarStatus.postValue("Please input name in profile")
                    }
                }

                override fun onFailure(call: Call<CustomerInfo>, t: Throwable) {
                    //Server dead
                    requestCarStatus.postValue(t.toString())
                }

            })
    }

    private fun requestCarByCustomer(carRequest : CarRequest)
    {
        RequestCarService.requestCarService.requestCarByAPI(carRequest, "Bearer ${SigninActivity.authCustomerTokenFromSignIn.accessToken}")
            .enqueue(object : Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful)
                    {
                        requestCarStatus.postValue("Request a car sucessfully")
                    }
                    else
                    {
                        requestCarStatus.postValue("error: ${response.errorBody().toString()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    //server Dead
                    requestCarStatus.postValue(t.toString())
                }

            })
    }
}
