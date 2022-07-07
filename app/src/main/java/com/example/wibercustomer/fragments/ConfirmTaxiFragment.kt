package com.example.wibercustomer.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.wibercustomer.R
import com.example.wibercustomer.api.RouteService
import com.example.wibercustomer.databinding.FragmentConfirmTaxiBinding
import com.example.wibercustomer.viewmodels.ConfirmTaxiViewModel
import com.example.wibercustomer.viewmodels.HomeViewModel
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.GsonBuilder
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfirmTaxiFragment: Fragment() {
    private lateinit var confirmTaxiViewModel: ConfirmTaxiViewModel
    private lateinit var map: MapView
    private lateinit var binding: FragmentConfirmTaxiBinding
    companion object {
        private const val TAG = "ConfirmTaxiFragment"
        private const val BACKEND_URL = "http://10.0.2.2:4242"
    }
    private lateinit var paymentIntentClientSecret: String
    private lateinit var paymentSheet: PaymentSheet

    private lateinit var payButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        confirmTaxiViewModel = ViewModelProvider(this).get(ConfirmTaxiViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_confirm_taxi, container, false)

        binding = FragmentConfirmTaxiBinding.bind(root)
//        val startingEdt = root.findViewById<TextInputLayout>(R.id.startingInputText)
//        val arrivingEdt = root.findViewById<TextInputLayout>(R.id.arrivingInputText)
        val startingEdt = binding.startingInputText
        val arrivingEdt = binding.arrivingInputText

        confirmTaxiViewModel.startingText.observe(viewLifecycleOwner){
            startingEdt.editText?.setText(it)
        }

        confirmTaxiViewModel.arrivingText.observe(viewLifecycleOwner){
            arrivingEdt.editText?.setText(it)
        }
        payButton = binding.payButton
        payButton.setOnClickListener(::onPayClicked)
        payButton.isEnabled = true

        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)


        val testTesting = root.findViewById<Button>(R.id.callApi)
        //106.660172,10.762622&end=106.7,10.762622
        val startPoint = GeoPoint(10.762622, 106.660172)
        val endPoint = GeoPoint(10.762622, 106.7)


        //here set map
        map = binding.mapView;

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

        map.overlays.add(startMarker)

        map.invalidate()

        return root
    }

    private fun showAlert(title: String, message: String? = null) {
        val builder = AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
            builder.setPositiveButton("Ok", null)
            builder.create().show()
    }
    private fun showToast(message: String) {
        Toast.makeText(context,  message, Toast.LENGTH_LONG).show()

    }

    private fun onPayClicked(view: View) {
        val configuration = PaymentSheet.Configuration("Example, Inc.")

        // Present Payment Sheet
        paymentIntentClientSecret = "ch_3LIa7JG4i8Ax0wzr1femUhKd";
        paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, configuration)
    }

    private fun onPaymentSheetResult(paymentResult: PaymentSheetResult) {
        when (paymentResult) {
            is PaymentSheetResult.Completed -> {
                showToast("Payment complete!")
            }
            is PaymentSheetResult.Canceled -> {
                Log.i(TAG, "Payment canceled!")
            }
            is PaymentSheetResult.Failed -> {
                showAlert("Payment failed", paymentResult.error.localizedMessage)
            }
        }
    }
}