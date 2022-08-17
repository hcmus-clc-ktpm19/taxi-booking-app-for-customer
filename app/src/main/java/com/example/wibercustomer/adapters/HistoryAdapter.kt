package com.example.wibercustomer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wibercustomer.R
import com.example.wibercustomer.models.CarRequest
import com.example.wibercustomer.models.History
import com.google.android.material.textfield.TextInputLayout

class HistoryAdapter(internal var historyList: List<CarRequest>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val fromLayout = listItemView.findViewById<TextInputLayout>(R.id.fromInputLayout)
        val toWhereLayout = listItemView.findViewById<TextInputLayout>(R.id.toWhereInputLayout)
        val moneyPaidTV = listItemView.findViewById<TextView>(R.id.moneyPaid)
        val distanceTV = listItemView.findViewById<TextView>(R.id.distanceTV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.history_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val perHistory: CarRequest = historyList.get(position)

        holder.fromLayout.editText?.setText(perHistory.pickingAddress)
        holder.toWhereLayout.editText?.setText(perHistory.arrivingAddress)
        holder.moneyPaidTV.text = perHistory.price.toInt().toString()
        holder.distanceTV.text = perHistory.distance.toString()
    }

    override fun getItemCount(): Int {
        return historyList.size
    }
}