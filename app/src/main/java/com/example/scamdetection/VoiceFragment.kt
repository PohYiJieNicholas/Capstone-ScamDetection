package com.example.scamdetection

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.example.scamdetection.databinding.FragmentVoiceBinding
import java.util.Locale

class VoiceFragment : Fragment() {
    private var _binding: FragmentVoiceBinding? = null
    private val binding get() = _binding!!
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognizerIntent: Intent

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


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.idIVMic.setOnClickListener{
           speechRecognizer.startListening(recognizerIntent)
        }
    }

    private fun initializeSpeechRecognizer(){
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                // Called when the speech recognition service is ready for user input
            }

            override fun onBeginningOfSpeech() {
                // Called when the user has started to speak
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Called when the RMS (Root Mean Square) value of the audio input changes
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // Called when partial recognition results are available
            }

            override fun onEndOfSpeech() {
                // Called when the user has finished speaking
            }

            override fun onError(error: Int) {
                // Called when an error occurs during speech recognition
            }

            override fun onResults(results: Bundle?) {
                // Called when the recognition service returns final results
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    // Process the recognized text (matches[0] contains the top result)
                    val recognizedText = matches[0]
                    // Do something with the recognized text, e.g., display it in a TextView
                    binding.txtMessage.setText(recognizedText)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                // Called when partial recognition results are available
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                // Called when an event related to the recognition service occurs
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