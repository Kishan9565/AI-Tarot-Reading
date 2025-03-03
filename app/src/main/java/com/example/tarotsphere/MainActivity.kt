package com.example.tarotsphere

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.tarotsphere.room.ChatMessage
import com.example.tarotsphere.room.ChatViewModel
import com.example.tarotsphere.utils.AnimationUtils
import com.example.tarotsphere.VoiceChat.VoiceChatManager
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), VoiceChatManager.VoiceChatListener {

    private lateinit var generative: GenerativeModel
    private lateinit var inputText: EditText
    private lateinit var responseText: TextView
    private lateinit var sendButton: ImageView
    private lateinit var micButton: ImageView

    private lateinit var chatViewModel: ChatViewModel
    private lateinit var voiceChatManager: VoiceChatManager

    private var isFirstQuestion = true
    private var isVoiceInput: Boolean = false


    private val cardSelectionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedCards: ArrayList<TarotCard>? = result.data?.getParcelableArrayListExtra("SELECTED_CARDS")

            if (selectedCards != null) {
                Log.e("CARD_SELECTION", "Selected Cards Received in MainActivity: ${selectedCards.joinToString { it.name }}")  // ✅ Confirm karo ki data aa raha hai ya nahi
                onCardsSelected(selectedCards)
            } else {
                Log.e("CARD_SELECTION", "No cards received!")  // ✅ Check karo ki data null toh nahi aa raha
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inputText = findViewById(R.id.inputText)
        responseText = findViewById(R.id.responseText)
        sendButton = findViewById(R.id.sendButton)
        micButton = findViewById(R.id.micButton)

        voiceChatManager = VoiceChatManager(this, this)

        loadModel()

        sendButton.setOnClickListener {
            isVoiceInput = false
            val question = inputText.text.toString().trim()
            if (question.isNotEmpty()) {
                if (isFirstQuestion) {
                    showCardSelection()
                } else {
                    lifecycleScope.launch { performAction(question, null) }
                }
            }
            inputText.setText("")
            hideKeyboard(it)
        }

        micButton.setOnClickListener {
            isVoiceInput = true
            voiceChatManager.startListening()
        }

        chatViewModel.allMessages.observe(this) { messages ->
            val chatHistory = messages.joinToString("\n") { "User: ${it.message}\nAI: ${it.response}" }
            responseText.text = chatHistory
        }
    }

    private suspend fun performAction(question: String, selectedCards: List<TarotCard>?) {
        Log.e("AI_CALL", "performAction() called with question: $question")

        try {
            responseText.text = "Thinking..."

            val predefinedCards = listOf(
                TarotCard("The Fool", R.drawable.fool),
                TarotCard("The Magician", R.drawable.mgician),
                TarotCard("The High Priestess", R.drawable.per)
            )

            val cardDetails = predefinedCards.joinToString("\n") { "${it.name} (Image: ${it.imageRes})" }
            val fullPrompt = "User's new question: $question\nSelected Cards: $cardDetails"

            Log.e("AI_CALL", "Sending to AI: $fullPrompt")  // ✅ Ensure naya question ja raha hai

            val response = generative.generateContent(fullPrompt)  // ✅ AI Call
            val aiResponse = response.text ?: "No response from AI."

            Log.e("AI_CALL", "Received from AI: $aiResponse")  // ✅ Check naya response aaya ya nahi

            responseText.text = aiResponse  // ✅ Ensure response update ho raha hai

            chatViewModel.insertMessage(ChatMessage(message = question, response = aiResponse))
            showSelectedCards(predefinedCards)

        } catch (e: Exception) {
            Log.e("AI_CALL", "Error calling AI: ${e.message}")
            responseText.text = "Error: ${e.message}"
        }
    }



    private fun showCardSelection() {
        val intent = Intent(this, CardSelection::class.java)
        cardSelectionLauncher.launch(intent)
    }

    private fun onCardsSelected(selectedCards: List<TarotCard>) {
        val question = inputText.text.toString().trim()

        Log.d("CARD_SELECTION", "Selected Cards Received: ${selectedCards.joinToString { it.name }}") // ✅ Check selected cards

        lifecycleScope.launch {
            performAction(question, selectedCards)
            isFirstQuestion = false
        }
    }


    private fun showSelectedCards(selectedCards: List<TarotCard>) {
        if (selectedCards.size == 3) {
            AnimationUtils.flipCard(findViewById(R.id.image1), selectedCards[0].imageRes)
            AnimationUtils.flipCard(findViewById(R.id.image2), selectedCards[1].imageRes)
            AnimationUtils.flipCard(findViewById(R.id.image3), selectedCards[2].imageRes)
        }
    }


    private fun loadModel() {
        Log.d("AI_CALL", "Initializing Gemini AI Model") // ✅ Model load ho raha hai?

        generative = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = "AIzaSyC9tZFKGlLTFcGFeNgF3D5537vQ-VLzTYw"
        )

        Log.d("AI_CALL", "Model Initialized Successfully") // ✅ Model load hone ke baad confirm kar
    }

    override fun onVoiceResult(result: String) {
        inputText.setText(result)
        lifecycleScope.launch {
            performAction(result, null)
        }
    }

    override fun onVoiceError(errorMessage: String) {
        responseText.text = errorMessage
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceChatManager.shutdown()
    }

    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}