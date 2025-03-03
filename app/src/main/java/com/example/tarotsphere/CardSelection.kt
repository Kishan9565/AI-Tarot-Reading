package com.example.tarotsphere

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CardSelection : AppCompatActivity() {

    private lateinit var adapter: TarotAdapter
    private val selectedCards = mutableListOf<TarotCard>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_selection)

        val recyclerView = findViewById<RecyclerView>(R.id.rvItem)

        // ✅ Fix: Proper Grid Layout
        val gridLayoutManager = GridLayoutManager(this, 5)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)

        // ✅ Fix: Dummy cards with back image
        val tarotCards = (1..72).map { index ->
            TarotCard("Card$index", R.drawable.back)
        }

        adapter = TarotAdapter(ArrayList(tarotCards), selectedCards) { selected ->
            if (selected.size == 3) {
                returnSelectedCards()  // ✅ Selected cards return karo
            }
        }

        recyclerView.adapter = adapter
    }

    private fun returnSelectedCards() {
        Log.e("CARD_SELECTION", "Returning Selected Cards: ${selectedCards.joinToString { it.name }}")

        val intent = Intent()
        intent.putParcelableArrayListExtra("SELECTED_CARDS", ArrayList(selectedCards))  // ✅ Parcelable List Send karo
        setResult(RESULT_OK, intent)  // ✅ Data return karo
        finish()
    }
}

