package com.example.scamdetection.smsMessages

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.scamdetection.R


class SmsAdapter(private val messages:List<SmsMessage>) :
    RecyclerView.Adapter<SmsAdapter.DataViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sms_item, parent, false)
        return DataViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val dataModel = messages[position]
        holder.sms.text = dataModel.body
        holder.date.text = dataModel.date
        holder.prediction.text = dataModel.prediction
    }

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sms : TextView = itemView.findViewById(R.id.txt_sms)
        val date : TextView = itemView.findViewById(R.id.txt_sms_date)
        val prediction : TextView = itemView.findViewById(R.id.txt_sms_prediction)
    }



}