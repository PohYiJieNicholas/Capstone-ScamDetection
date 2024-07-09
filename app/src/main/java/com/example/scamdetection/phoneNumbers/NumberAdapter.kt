package com.example.scamdetection.phoneNumbers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.scamdetection.R

class NumberAdapter(private val numberList:ArrayList<String>) :
    RecyclerView.Adapter<NumberAdapter.DataViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.number_item, parent, false)
        return DataViewHolder(view)
    }

    override fun getItemCount(): Int {
        return numberList.size
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val dataModel = numberList[position]
        holder.banNumber.text = dataModel
    }

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val banNumber : TextView = itemView.findViewById(R.id.txt_banNumber)
    }



}