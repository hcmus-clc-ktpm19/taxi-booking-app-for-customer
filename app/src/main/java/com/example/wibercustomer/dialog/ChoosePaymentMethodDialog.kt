package com.example.wibercustomer.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wibercustomer.R
import com.example.wibercustomer.adapters.PaymentItemAdapter
import com.example.wibercustomer.models.Payment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class ChoosePaymentMethodDialog : BottomSheetDialogFragment() {

    private lateinit var recyclerView: RecyclerView

    //    private var mListener: BottomSheetListener? = null
    private lateinit var paymentAdapter: PaymentItemAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_choose_payment_method, container, false)
        recyclerView = view.findViewById(R.id.choose_payment_recycler_view)
        val paymentLists =
            arrayListOf<Payment>(
                Payment("Nguyen Van A", "visa", "4000 1234 5678 9012", Date()),
                Payment("Nguyen Van B", "visa", "4000 1234 5678 9012", Date()),
                Payment("Nguyen Van A", "visa", "4000 1234 5678 9012", Date())
            )
        paymentAdapter = PaymentItemAdapter(paymentLists)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = paymentAdapter
        return view
    }
}