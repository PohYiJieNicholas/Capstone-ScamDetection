package com.example.scamdetection.conversations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.scamdetection.R
import com.example.scamdetection.phoneNumbers.NumberAdapter.DataViewHolder

class ConversationAdapter(private val convoList:ArrayList<ConversationModel>) :
    RecyclerView.Adapter<ConversationAdapter.DataViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConversationAdapter.DataViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.convo_item, parent, false)
        return DataViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationAdapter.DataViewHolder, position: Int) {
        val dataModel = convoList[position]
        holder.conversation_txt.text = dataModel.conversation
        holder.prediction_txt.text = dataModel.prediction
        holder.date_txt.text = dataModel.date

    }

    override fun getItemCount(): Int {
        return convoList.size
    }

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val conversation_txt: TextView = itemView.findViewById(R.id.txt_conversation)
        val prediction_txt : TextView = itemView.findViewById(R.id.txt_prediction)
        val date_txt : TextView = itemView.findViewById(R.id.txt_date)

    }

}