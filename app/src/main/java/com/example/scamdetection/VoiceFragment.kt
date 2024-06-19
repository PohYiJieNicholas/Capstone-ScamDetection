package com.example.scamdetection

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.example.scamdetection.apiCall.InputData
import com.example.scamdetection.apiCall.PredictionData
import com.example.scamdetection.apiCall.ResponseData
import com.example.scamdetection.apiCall.RetrofitInstance
import com.example.scamdetection.databinding.FragmentVoiceBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class VoiceFragment : Fragment() {
    private var _binding: FragmentVoiceBinding? = null
    private val binding get() = _binding!!
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognizerIntent: Intent
    private lateinit var firebaseRef: DatabaseReference
    private var isListening = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVoiceBinding.inflate(inflater, container, false)

        // Check for microphone permission
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.RECORD_AUDIO), 1)
        } else {
            initializeSpeechRecognizer()
        }

        firebaseRef = FirebaseDatabase.getInstance().getReference("Conversation")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.idIVMic.setOnClickListener{
            if (isListening) {
                speechRecognizer.stopListening()
                isListening = false
            }else{
                isListening = true
                binding.idIVMic.setImageResource(R.drawable.mic_btn_active)
                speechRecognizer.startListening(recognizerIntent)
            }

        }
    }

    private fun initializeSpeechRecognizer(){
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                // Called when the speech recognition service is ready for user input
                Log.d("Speech", "Ready for speech")
            }

            override fun onBeginningOfSpeech() {
                // Called when the user has started to speak
                Log.d("Speech", "Beginning of speech")
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Called when the RMS (Root Mean Square) value of the audio input changes
                Log.d("Speech", "RMS: $rmsdB")
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // Called when partial recognition results are available
                Log.d("Speech", "Buffer received")
            }

            override fun onEndOfSpeech() {
                // Called when the user has finished speaking
                binding.idIVMic.setImageResource(R.drawable.mic_btn_default)
                Log.d("Speech", "End of speech")

            }

            override fun onError(error: Int) {
                // Called when an error occurs during speech recognition
                binding.idIVMic.setImageResource(R.drawable.mic_btn_default)
                binding.txtMessage.text = "Unrecognized speech"
                Log.d("Speech", "Error: $error")

            }

            override fun onResults(results: Bundle?) {
                // Called when the recognition service returns final results
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                binding.idIVMic.setImageResource(R.drawable.mic_btn_default)

                if (!matches.isNullOrEmpty()) {
                    // Process the recognized text (matches[0] contains the top result)
                    val recognizedText = matches[0]
                    val inputData = InputData(recognizedText)
                    var prediction = ""

                    Log.d("Speech", "Recognized text: $recognizedText")
                    // Get the current date
                    val formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

                    RetrofitInstance.api.getPrediction(inputData).enqueue(object : Callback<ResponseData> {
                        override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
//                            binding.txtPrediction.text = response.body()?.let { "Results: ${it.prediction}" }
                            if (response.isSuccessful) {
                                binding.txtPrediction.text= "Response: ${response.body()?.message}\nReceived Data: ${response.body()?.output}\nTime Taken: ${response.body()?.timeTaken}"
                                prediction = response.body()?.output.toString()
                                val predictionObject = PredictionData(recognizedText.toString(), prediction,formattedDate.toString())

                                firebaseRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
//      
                                        var highestKey = 0
                                        if (snapshot.exists()) {
                                            val lastChildSnapshot = snapshot.children.iterator().next()
                                            highestKey = lastChildSnapshot.key?.toIntOrNull() ?: 0
                                        }

                                        // Increment the highest key and use it for the new data
                                        val newKey = highestKey + 1
                                        firebaseRef.child(newKey.toString()).setValue(predictionObject)


                                    }
                                    override fun onCancelled(error: DatabaseError) {
                                        // Handle errors
                                    }
                                })
                            } else {
                                binding.txtPrediction.text = "Failed to receive response"
                                binding.idIVMic.setImageResource(R.drawable.mic_btn_default)

                            }
                        }

                        override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                            binding.txtPrediction.text = "Error: ${t.message}"
                            Log.d("Flask test", "${t.message}")
                            binding.idIVMic.setImageResource(R.drawable.mic_btn_default)
                        }
                    })

                    // Do something with the recognized text, e.g., display it in a TextView
                    binding.txtMessage.text = recognizedText
                    isListening = false
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                // Called when partial recognition results are available
                Log.d("Speech", "Partial results")

            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                // Called when an event related to the recognition service occurs
                Log.d("Speech", "Event: $eventType")
            }
        })

        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    }

    // Override onDestroy to release SpeechRecognizer resources
    override fun onDestroy() {
        speechRecognizer.destroy()
        super.onDestroy()
    }

    // Request microphone permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeSpeechRecognizer()
            } else {
                // Handle the case where the user denied the permission
            }
        }
    }
}