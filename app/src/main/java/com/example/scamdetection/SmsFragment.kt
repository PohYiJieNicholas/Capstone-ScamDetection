package com.example.scamdetection


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scamdetection.apiCall.InputData
import com.example.scamdetection.apiCall.PredictionData
import com.example.scamdetection.apiCall.ResponseData
import com.example.scamdetection.apiCall.RetrofitInstance
import com.example.scamdetection.databinding.FragmentSmsBinding
import com.example.scamdetection.smsMessages.SmsAdapter
import com.example.scamdetection.smsMessages.SmsMessage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SmsFragment : Fragment() {
    private var _binding: FragmentSmsBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSmsBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvSms.layoutManager = LinearLayoutManager(view.context)

        val smsList = readSmsMessages(view.context)
        val smsPredictedList = mutableListOf<SmsMessage>()
        smsList.forEach { sms ->
            val inputData = InputData(sms.body)
            var prediction = ""

            RetrofitInstance.api.getPrediction(inputData).enqueue(object : Callback<ResponseData> {
                override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
//                            binding.txtPrediction.text = response.body()?.let { "Results: ${it.prediction}" }
                    if (response.isSuccessful) {
                        prediction = response.body()?.output.toString()
                        Log.d("Sms Message", smsPredictedList.toString())

                        smsPredictedList.add(SmsMessage(sms.address, sms.body, sms.date, prediction))
                        binding.rvSms.adapter = SmsAdapter(smsPredictedList)

                    } else {
                         prediction = "Fail to predict"
                    }
                }

                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    Log.d("Flask test", "${t.message}")
                }
            })

        }



    }
    fun readSmsMessages(context: Context): List<SmsMessage> {
        val smsList = mutableListOf<SmsMessage>()
        val uri: Uri = Uri.parse("content://sms/inbox")
        val projection = arrayOf("_id", "address", "body", "date")
        val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, "date DESC")

        cursor?.use {
            val addressIndex = it.getColumnIndex("address")
            val bodyIndex = it.getColumnIndex("body")
            val dateIndex = it.getColumnIndex("date")

            while (it.moveToNext()) {
                val address = it.getString(addressIndex)
                val body = it.getString(bodyIndex)
                val date = it.getString(dateIndex)
                val inputData = InputData(body)
                val prediction = "Nothing"
                smsList.add(SmsMessage(address, body, date, prediction))

            }
        }

        return smsList
    }

}