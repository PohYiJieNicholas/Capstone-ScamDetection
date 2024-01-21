package com.example.scamdetection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Gravity
import android.widget.Toast

class CallReceiver:BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                if(incomingNumber != null){
                    Log.d("IncomingCall", "Incoming call from: $incomingNumber")
                    showToastMsg(context!!,"Incoming call from: $incomingNumber")
                    // Retrieve ArrayList
                    MainActivity().compareNumber(context!!,incomingNumber)

                }

            }
        }

    }

    fun showToastMsg(c:Context, msg:String){
        val toast = Toast.makeText(c,msg, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER,0,0)
        toast.show()
    }

}