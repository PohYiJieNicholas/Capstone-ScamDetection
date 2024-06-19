package com.example.scamdetection


import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
//    private var banPhoneNumber:ArrayList<String> = arrayListOf()
//
//    private lateinit var telephonyManager: TelephonyManager
//    private lateinit var phoneStateListener: PhoneStateListener

    private lateinit var callReceiver: CallReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermissions()

        firebaseRef = FirebaseDatabase.getInstance().getReference("BanNumbers")

        val sharedPreferences = MySharedPreferences(this@MainActivity)

        callReceiver = CallReceiver()
        val filter = IntentFilter()
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        registerReceiver(callReceiver, filter)

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




    private fun checkPermissions(): Boolean {
        val permissions = arrayOf(
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.READ_CALL_LOG,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.MANAGE_OWN_CALLS,
            android.Manifest.permission.FOREGROUND_SERVICE,
            android.Manifest.permission.POST_NOTIFICATIONS

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