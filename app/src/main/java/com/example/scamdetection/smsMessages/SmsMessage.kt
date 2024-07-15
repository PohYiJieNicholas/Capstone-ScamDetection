package com.example.scamdetection.smsMessages

// Data class for SMS messages
data class SmsMessage(val address: String, val body: String, val date: String, val prediction: String)
