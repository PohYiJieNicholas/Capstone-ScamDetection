package com.example.scamdetection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.fragment.app.Fragment


class CallReceiver:BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val sharedPreferences = MySharedPreferences(context!!)
        val banPhoneNumber = sharedPreferences.getArrayList("myKey")
        Log.d("CallReceiver", "BanNumber = $banPhoneNumber")
        if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                if(incomingNumber != null){
                    Log.d("IncomingCall", "Incoming call from: $incomingNumber")

                    // Compare incoming number with ban numbers
                    for(phoneNumber in banPhoneNumber){
                        if (phoneNumber == incomingNumber){
                            showToastMsg(context!!,"Incoming call number $incomingNumber might be a scam call")
                            // Trigger the fragment change
                            if (context is MainActivity) {
                                context.replaceFragment(VoiceFragment())
                                context.binding.bottomNavigationView.selectedItemId = R.id.prediction
                            }
                            triggerNotification(context!!, incomingNumber)

                            Log.d("Call Receiver", "Might be scam")
                        }
                    }
                }

            }
        }

    }

    fun showToastMsg(c:Context, msg:String){
        val toast = Toast.makeText(c,msg, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER,0,0)
        toast.show()
    }

    private fun triggerNotification(context: Context, incomingNumber: String?) {
        Log.d("Notification", "Triggering notification")
        val notificationHelper = NotificationHelper(context, "incoming_call_channel", "Incoming Call Notifications", "Notifications for incoming calls")
        notificationHelper.createNotification("Warning", "Call from $incomingNumber might be a scammer")
    }
}