package com.example.scamdetection

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.scamdetection.apiCall.PredictionData
import com.example.scamdetection.apiCall.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.scamdetection.databinding.FragmentBanNumbersBinding


class BanNumbersFragment : Fragment() {

    private var firebaseController: FirebaseController= FirebaseController()
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
        RetrofitInstance.api.getUserData().enqueue(object : Callback<PredictionData> {
            override fun onResponse(call: Call<PredictionData>, response: Response<PredictionData>) {
                binding.txtPrediction.text = response.body()?.let { "Results: ${it.prediction}" }
            }

            override fun onFailure(call: Call<PredictionData>, t: Throwable) {
                binding.txtPrediction.text = "Error: ${t.message}"
                Log.d("Flask test", "${t.message}")
            }
        })
    }


}