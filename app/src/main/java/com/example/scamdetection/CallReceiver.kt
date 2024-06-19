package com.example.scamdetection

import android.Manifest
import android.R
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CallReceiver:BroadcastReceiver() {
    private lateinit var firebaseRef: DatabaseReference
    private var banPhoneNumber:ArrayList<String> = arrayListOf()

    override fun onReceive(context: Context?, intent: Intent?) {
        firebaseRef = FirebaseDatabase.getInstance().getReference("BanNumbers")
// add event listener for Firebase database changes
        firebaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                banPhoneNumber.clear()
                if(snapshot.exists()){
                    for (data in snapshot.children) {
                        val samplingResult = data.value
                        banPhoneNumber.add(samplingResult.toString())
                        Log.d("PhoneNumbers", "Value = $samplingResult")
                    }
                }
                Log.d("PhoneNumbers", "Ban Number = $banPhoneNumber")
            }
            override fun onCancelled(error: DatabaseError) {
                //Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
            }
        })
        if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                if(incomingNumber != null){
                    Log.d("IncomingCall", "Incoming call from: $incomingNumber")

                    for(phoneNumber in banPhoneNumber){
                        if (phoneNumber == incomingNumber){
                            showToastMsg(context!!,"Incoming call number $incomingNumber might be a scam call")

                            triggerNotification(context!!, incomingNumber)

                            Log.d("Ban Number", "Might be scam")
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
        val notificationHelper = NotificationHelper(context)
        notificationHelper.createNotification("Warning", "Call from $incomingNumber might be a scammer")
    }
}