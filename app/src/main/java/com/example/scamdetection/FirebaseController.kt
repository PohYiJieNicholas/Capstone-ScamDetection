package com.example.scamdetection

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseController {
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var banPhoneNumber:ArrayList<String>
    fun retrieveBanNumbers(){
        firebaseRef = FirebaseDatabase.getInstance().getReference("BanNumbers")

        // add event listener for Firebase database changes
        firebaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val samplingResult = data.value
                    banPhoneNumber.add(samplingResult.toString())
                    Log.d("PhoneNumbers", "Value = $samplingResult")

                }
            }

            override fun onCancelled(error: DatabaseError) {
                //Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
            }
        })
        Log.d("PhoneNumbers", "Ban Number = $banPhoneNumber")
    }

}