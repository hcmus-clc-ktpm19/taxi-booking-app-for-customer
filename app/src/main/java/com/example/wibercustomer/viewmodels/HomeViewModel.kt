package com.example.wibercustomer.viewmodels

import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wibercustomer.activities.SigninActivity
import com.example.wibercustomer.api.CustomerService
import com.example.wibercustomer.api.RequestCarService
import com.example.wibercustomer.api.RouteService
import com.example.wibercustomer.models.CarRequest
import com.example.wibercustomer.models.CustomerInfo
import com.example.wibercustomer.models.Payment
import com.example.wibercustomer.models.enums.CarRequestStatus
import com.google.android.gms.maps.model.LatLng
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeViewModel : ViewModel() {

    private val _geoPoint = MutableLiveData<ArrayList<LatLng>>().apply {
        value = ArrayList<LatLng>()
    }
    val geoPoint: LiveData<ArrayList<LatLng>> = _geoPoint

    private val _distanceValue = MutableLiveData<Double>().apply {
        value = 0.0
    }
    val distanceValue: LiveData<Double> = _distanceValue

    private val _paymentValue = MutableLiveData<Payment>().apply {
        value = Payment()
    }
    val paymentValue: LiveData<Payment> = _paymentValue

    private val _pickingAdressValue = MutableLiveData<String>().apply {
        value = ""
    }
    val pickingAddressValue: LiveData<String> = _pickingAdressValue

    private val _arrivingAdressValue = MutableLiveData<String>().apply {
        value = ""
    }
    val arrivingAddressValue: LiveData<String> = _arrivingAdressValue

    private val _carRequestValue = MutableLiveData<CarRequest>().apply {
        value = CarRequest(null, "", "", "", "", 0.0, 0.0,
                                0.0, 0.0, "", 0.0, 0.0)
    }
    val carRequestValue: LiveData<CarRequest> = _carRequestValue

    private val _driverDestinationValue = MutableLiveData<LatLng?>().apply {
        value = null
    }
    val driverDestinationValue: LiveData<LatLng?> = _driverDestinationValue

    var routeServiceStatus = MutableLiveData<Boolean>()

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
                    routeServiceStatus.postValue(false)
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful)
                    {
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
                        routeServiceStatus.postValue(true)
                    }
                    else
                    {
                        routeServiceStatus.postValue(false)
                    }
                }
            })
    }

    var requestCarStatus = MutableLiveData<String>()

    fun checkCustomerIsValidAndRequestCar(startLocation: LatLng, destinatioLocation: LatLng, carType : String, price: Double, distance: Double){
        CustomerService.customerService.getAPICustomerInfo(SigninActivity.phoneNumberLoginFromSignIn, "Bearer ${SigninActivity.authCustomerTokenFromSignIn.accessToken}")
            .enqueue(object : Callback<CustomerInfo>{
                override fun onResponse(
                    call: Call<CustomerInfo>,
                    response: Response<CustomerInfo>
                ) {
                    if (response.isSuccessful)
                    {
                        val carRequest = CarRequest(null, response.body()!!.id, response.body()!!.phone,
                            pickingAddressValue.value!!, arrivingAddressValue.value!!,
                            startLocation.longitude, startLocation.latitude,
                            destinatioLocation.longitude, destinatioLocation.latitude, carType, price, distance)
                        carRequest.status = CarRequestStatus.WAITING.name
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
                        carRequest.id = response.body()?.string() //this consume that one line string so be careful to use this
                        Log.i("request car", carRequest.id.toString())
                        carRequest.nextStatusRequest()
                        _carRequestValue.value = carRequest
                        requestCarStatus.postValue("Request a car successfully")
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

    fun nextStateCarRequest(carRequest : CarRequest)
    {
        carRequest.nextStatusRequest()
        _carRequestValue.value = carRequest
    }

    fun calculateMoneyValue()
    {
        paymentValue.value?.calculateMoney(distanceValue.value!!)
        _paymentValue.value = paymentValue.value
    }

    fun setDriverValue(newLatLng: LatLng)
    {
        _driverDestinationValue.value = newLatLng
    }

}
