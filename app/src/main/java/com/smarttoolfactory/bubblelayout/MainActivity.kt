package com.smarttoolfactory.bubblelayout

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dateBubble = findViewById<BubbleLayout>(R.id.bubbleDate)
        dateBubble.modifier.apply {
            arrowAlignment = ArrowAlignment.NONE
            backgroundColor = Color.rgb(212, 234, 244)
        }
        dateBubble.update(modifier = dateBubble.modifier)


        // Sent bubble
        val senderBubble1 = findViewById<BubbleLayout>(R.id.bubbleViewSender)
        senderBubble1.modifier.apply {
            arrowAlignment = ArrowAlignment.TOP_RIGHT
        }

        senderBubble1.update(modifier = senderBubble1.modifier)


        val senderBubble2 = findViewById<BubbleLayout>(R.id.bubbleViewSender2)
        senderBubble2.modifier.apply {
            arrowAlignment = ArrowAlignment.BOTTOM_RIGHT
        }

        senderBubble2.update(modifier = senderBubble2.modifier)

        val senderBubble3 = findViewById<BubbleLayout>(R.id.bubbleViewSender3)
        senderBubble3.modifier.apply {
            arrowAlignment = ArrowAlignment.BOTTOM_RIGHT
            isSecondary = true
        }

        senderBubble3.update(modifier = senderBubble3.modifier)

        // Received Bubble
        val receiverBubble = findViewById<BubbleLayout>(R.id.bubbleViewReceiver)

        val modifierR1 = receiverBubble.modifier.apply {
            backgroundColor = Color.WHITE
            arrowAlignment = ArrowAlignment.BOTTOM_LEFT
        }

        receiverBubble.update(modifierR1)

        val receiverBubble2 = findViewById<BubbleLayout>(R.id.bubbleViewReceiver2)

        val modifierR2 = receiverBubble2.modifier.apply {
            backgroundColor = Color.WHITE
            arrowAlignment = ArrowAlignment.TOP_LEFT
        }

        receiverBubble2.update(modifierR2)


        val receiverBubble3 = findViewById<BubbleLayout>(R.id.bubbleViewReceiver3)

        val modifierR3 = receiverBubble3.modifier.apply {
            backgroundColor = Color.WHITE
            arrowAlignment = ArrowAlignment.TOP_LEFT
            isSecondary = true
        }

        receiverBubble3.update(modifierR3)


    }
}