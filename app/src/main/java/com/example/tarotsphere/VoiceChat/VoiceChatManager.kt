package com.example.tarotsphere.VoiceChat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import java.util.Locale

class VoiceChatManager(
    private val context: Context,
    private val listener: VoiceChatListener
) : RecognitionListener, TextToSpeech.OnInitListener {

    private var speechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    private var tts: TextToSpeech = TextToSpeech(context, this)

    init {
        speechRecognizer.setRecognitionListener(this)
    }


    interface VoiceChatListener {
        fun onVoiceResult(result: String)
        fun onVoiceError(errorMessage: String)
    }


    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
        speechRecognizer.startListening(intent)
    }


    fun stopListening() {
        speechRecognizer.stopListening()
    }


    fun speakOut(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }


    override fun onReadyForSpeech(params: Bundle?) {}
    override fun onBeginningOfSpeech() {}
    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {}
    override fun onEndOfSpeech() {}
    override fun onPartialResults(partialResults: Bundle?) {}
    override fun onEvent(eventType: Int, params: Bundle?) {}

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            listener.onVoiceResult(matches[0])
        } else {
            listener.onVoiceError("No voice input recognized")
        }
    }

    override fun onError(error: Int) {
        listener.onVoiceError("Speech recognition error: $error")
    }


    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.getDefault()
        }
    }

    fun shutdown() {
        speechRecognizer.destroy()
        tts.shutdown()
    }
}
