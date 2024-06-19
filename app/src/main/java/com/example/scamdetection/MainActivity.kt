package com.example.scamdetection


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.scamdetection.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseRef: DatabaseReference
    private var banPhoneNumber:ArrayList<String> = arrayListOf()

    private lateinit var telephonyManager: TelephonyManager
    private lateinit var phoneStateListener: PhoneStateListener



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if(checkPermissions()){
            setupPhoneStateListener()
        }

        firebaseRef = FirebaseDatabase.getInstance().getReference("BanNumbers")

        val sharedPreferences = MySharedPreferences(this@MainActivity)

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
                sharedPreferences.saveArrayList("myKey", banPhoneNumber)

                Log.d("PhoneNumbers", "Ban Number = $banPhoneNumber")
            }

            override fun onCancelled(error: DatabaseError) {
                //Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
            }
        })

        replaceFragment(BanNumbersFragment())

        binding.bottomNavigationView.setOnItemSelectedListener{
            when(it.itemId) {
                R.id.banNumberTab -> {
                    replaceFragment(BanNumbersFragment())
                    true
                }

                R.id.prediction -> {
                    replaceFragment(VoiceFragment())
                    true
                }

                R.id.history-> {
                    replaceFragment(HistoryFragment())
                    true
                }

                else -> false
            }
        }

    }



    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.fragmentContainerView,fragment)
        fragmentTransaction.commit()
    }

    fun compareNumber(c: Context, incomingNumber:String){
        val sharedPreferences = MySharedPreferences(c)

        // Retrieve ArrayList
        val retrievedList = sharedPreferences.getArrayList("myKey")
        Log.d("Ban Number", "Ban numbers = $retrievedList")
        for(phoneNumber in retrievedList){
            if (phoneNumber == incomingNumber){
                val toast = Toast.makeText(c,"Incoming call number $incomingNumber might be a scam call", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER,0,0)
                toast.show()
                Log.d("Ban Number", "Might be scam")
            }
        }
    }

    private fun setupPhoneStateListener(){
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                super.onCallStateChanged(state, phoneNumber)
                when (state) {
                    TelephonyManager.CALL_STATE_RINGING -> {
                        // Phone is ringing
                        Log.d("Incoming Number", "Incoming number = $phoneNumber")
                    }
                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        // Call is answered
                        replaceFragment(VoiceFragment())
                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                        // Call ended
                    }
                }
            }
        }
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }
    private fun checkPermissions(): Boolean {
        val permissions = arrayOf(
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.READ_CALL_LOG,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.MANAGE_OWN_CALLS
        )
        val granted = permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
        if (!granted) {
            ActivityCompat.requestPermissions(this, permissions, 1)
        }
        return granted
    }



}