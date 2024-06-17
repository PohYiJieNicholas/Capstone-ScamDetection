package com.example.scamdetection

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scamdetection.apiCall.PredictionData
import com.example.scamdetection.conversations.ConversationAdapter
import com.example.scamdetection.conversations.ConversationModel
import com.example.scamdetection.databinding.FragmentHistoryBinding
import com.example.scamdetection.databinding.FragmentVoiceBinding
import com.example.scamdetection.phoneNumbers.NumberAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistoryFragment : Fragment() {
    private lateinit var firebaseRef: DatabaseReference
    private val binding get() = _binding!!
    private var _binding: FragmentHistoryBinding? = null
    private var conversationList:ArrayList<ConversationModel> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        firebaseRef = FirebaseDatabase.getInstance().getReference("Conversation")

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvConversation.layoutManager = LinearLayoutManager(view.context)

        // add event listener for Firebase database changes
        firebaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                conversationList.clear()
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val samplingResult = data.getValue(ConversationModel::class.java)
                        conversationList.add(samplingResult!!)
                        Log.d("Conversations", "Value = $samplingResult")
                    }
                }
                Log.d("Conversations", "Conversation List = $conversationList")
                binding.rvConversation.adapter = ConversationAdapter(conversationList)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Conversations", "Failed to read value.", error.toException())
            }
        })
    }
}

