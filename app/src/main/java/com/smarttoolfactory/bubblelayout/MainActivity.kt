package com.smarttoolfactory.bubblelayout

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val senderBubble = findViewById<BubbleLayout>(R.id.bubbleViewSender)
        val receiverBubble = findViewById<BubbleLayout>(R.id.bubbleViewReceiver)

        val modifier = receiverBubble.modifier.apply {
            backgroundColor = Color.WHITE
            arrowAlignment = ArrowAlignment.BOTTOM_RIGHT
        }

        receiverBubble.update(modifier)
    }
}