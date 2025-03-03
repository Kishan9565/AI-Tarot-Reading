package com.example.tarotsphere.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val chatDao = ChatDatabase.getDatabase(application).chatDao()
    val allMessages: LiveData<List<ChatMessage>> = chatDao.getAllMessages()

    fun insertMessage(chatMessage: ChatMessage) {
        viewModelScope.launch {
            chatDao.insertMessage(chatMessage)
        }
    }
}
