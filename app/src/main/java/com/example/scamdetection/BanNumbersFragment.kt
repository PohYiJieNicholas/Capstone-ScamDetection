package com.example.scamdetection

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scamdetection.apiCall.PredictionData
import com.example.scamdetection.apiCall.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.scamdetection.databinding.FragmentBanNumbersBinding
import com.example.scamdetection.phoneNumbers.NumberAdapter


class BanNumbersFragment : Fragment() {

    private var _binding : FragmentBanNumbersBinding? = null
    private val binding get() = _binding!!

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

        // Retrieve ArrayList
        val retrievedList = sharedPreferences.getArrayList("myKey")
        Log.d("Ban Number Page", "Ban numbers = $retrievedList")

        binding.rvNumber.adapter = NumberAdapter(retrievedList)



    }


}