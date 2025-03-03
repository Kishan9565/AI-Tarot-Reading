package com.example.tarotsphere.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(chatMessage: ChatMessage)

    @Query("SELECT * FROM chat_history ORDER BY id ASC")
    fun getAllMessages(): LiveData<List<ChatMessage>>
}
