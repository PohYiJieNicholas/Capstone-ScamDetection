package com.example.scamdetection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import com.example.scamdetection.apiCall.InputData
import com.example.scamdetection.apiCall.ResponseData
import com.example.scamdetection.apiCall.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

        if (messages.isNotEmpty()) {
            for (smsMessage in messages) {
                val sender = smsMessage.originatingAddress
                val messageBody = smsMessage.messageBody
                val inputData = InputData(messageBody)
                var prediction = ""

                RetrofitInstance.api.getPrediction(inputData).enqueue(object :
                    Callback<ResponseData> {
                    override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
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