package com.example.tarotsphere.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView

object AnimationUtils {


    fun animateRecyclerViewItem(view: View, position: Int) {
        view.alpha = 0f
        view.animate()
            .alpha(1f)
            .setDuration(250)  // 🔥 Fast animation
            .setStartDelay((position * 40).toLong())  // 🔥 Delay kam kiya
            .setInterpolator(DecelerateInterpolator())
            .start()
    }


    fun addClickAnimation(view: View) {
        view.setOnClickListener {
            view.animate()
                .scaleX(0.95f)  // 🔹 Thoda shrink effect
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction {
                    view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()
        }
    }

    // ✅ Smooth Flip Animation for Cards
    fun flipCard(cardView: ImageView, newImage: Int) {
        val animator = ObjectAnimator.ofFloat(cardView, "rotationY", 0f, 90f)
        animator.duration = 200  // 🔥 Flip fast kiya

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                cardView.setImageResource(newImage) // ✅ Card ka front change karega
                val animatorBack = ObjectAnimator.ofFloat(cardView, "rotationY", -90f, 0f)
                animatorBack.duration = 200
                animatorBack.start()
            }
        })
        animator.start()
    }
}
