package com.example.scamdetection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.gesture.Prediction
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import com.example.scamdetection.apiCall.InputData
import com.example.scamdetection.apiCall.ResponseData
import com.example.scamdetection.apiCall.RetrofitInstance
import com.example.scamdetection.smsMessages.SmsAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val pdus = bundle["pdus"] as Array<*>?
            if (pdus != null) {
                for (pdu in pdus) {
                    val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray)
                    val sender = smsMessage.displayOriginatingAddress
                    val messageBody = smsMessage.messageBody
                    val inputData = InputData(messageBody)
                    var prediction = ""

                    RetrofitInstance.api.getPrediction(inputData).enqueue(object :
                        Callback<ResponseData> {
                        override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
//                            binding.txtPrediction.text = response.body()?.let { "Results: ${it.prediction}" }
                            if (response.isSuccessful) {
                                prediction = response.body()?.output.toString()
                                // Show a toast notification
                                Toast.makeText(context, "SMS from $sender is $prediction", Toast.LENGTH_LONG).show()

                                // Show a notification
                                triggerNotification(context, sender, messageBody, prediction)

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
        }
    }
    private fun triggerNotification(context: Context, sender: String?, messageBody: String, prediction: String) {
        Log.d("Notification", "Triggering notification")
        val notificationHelper = NotificationHelper(context, "sms_channel_id", "SMS Notifications", "Notifications for incoming sms")
        if (prediction == "fraud"){
            notificationHelper.createNotification("Warning", "SMS from $sender might be a scammer")
        }else{
            notificationHelper.createNotification("Safe", "SMS from $sender is not a scammer")
        }

    }
}