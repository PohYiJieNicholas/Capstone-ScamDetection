package com.example.scamdetection

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scamdetection.databinding.FragmentBanNumbersBinding
import com.example.scamdetection.phoneNumbers.NumberAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class BanNumbersFragment : Fragment() {

    private var _binding : FragmentBanNumbersBinding? = null
    private val binding get() = _binding!!
    private var banPhoneNumber:ArrayList<String> = arrayListOf()
    private lateinit var firebaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBanNumbersBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvNumber.layoutManager = LinearLayoutManager(view.context)

        val sharedPreferences = MySharedPreferences(view.context)

        firebaseRef = FirebaseDatabase.getInstance().getReference("BanNumbers")
        // add event listener for Firebase database changes
        firebaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                banPhoneNumber.clear()
                if(snapshot.exists()){
                    for (data in snapshot.children) {
                        val samplingResult = data.value
                        banPhoneNumber.add(samplingResult.toString())
                        Log.d("BanNumber", "Value = $samplingResult")
                    }
                }
                sharedPreferences.saveArrayList("myKey", banPhoneNumber)
                binding.rvNumber.adapter = NumberAdapter(banPhoneNumber)

            }
            override fun onCancelled(error: DatabaseError) {
                //Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }
}