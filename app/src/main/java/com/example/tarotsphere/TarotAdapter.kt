package com.example.tarotsphere

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.tarotsphere.utils.AnimationUtils

class TarotAdapter(
    private val tarotCards: ArrayList<TarotCard>,
    private val selectedCards: MutableList<TarotCard>,
    private val onCardsSelected: (List<TarotCard>) -> Unit
) : RecyclerView.Adapter<TarotAdapter.TarotViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TarotViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return TarotViewHolder(view)
    }

    override fun onBindViewHolder(holder: TarotViewHolder, position: Int) {
        val card = tarotCards[position]
        holder.bind(card)

//        AnimationUtils.animateRecyclerViewItem(holder.itemView, position)
//        AnimationUtils.addClickAnimation(holder.itemView)
    }

    override fun getItemCount() = tarotCards.size

    inner class TarotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.cardImage)

        fun bind(card: TarotCard) {
            imageView.setImageResource(card.imageRes)
            itemView.setBackgroundResource(
                if (selectedCards.contains(card)) R.drawable.border_selected else 0
            )

            itemView.setOnClickListener {
                if (selectedCards.size < 3 && !selectedCards.contains(card)) {
                    selectedCards.add(card)
                    Log.d("TarotAdapter", "Card Added: ${card.name}")
                    notifyItemChanged(adapterPosition) // ✅ Only update this item

                    if (selectedCards.size == 3) {
                        onCardsSelected(selectedCards.toList())
                    }
                }
            }
        }
    }
}
