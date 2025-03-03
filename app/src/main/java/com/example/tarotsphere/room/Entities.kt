package com.example.tarotsphere.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_history")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val message: String,
    val response: String
)
