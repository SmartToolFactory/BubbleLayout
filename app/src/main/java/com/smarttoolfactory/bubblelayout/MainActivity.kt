package com.smarttoolfactory.bubblelayout

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawBubbles()


    }

    private fun drawBubbles() {

        val dateBubble = findViewById<BubbleLayout>(R.id.bubbleDate)
        dateBubble.modifier.apply {
            arrowAlignment = ArrowAlignment.NONE
            backgroundColor = Color.rgb(212, 234, 244)
        }
        dateBubble.update(modifier = dateBubble.modifier)


        /*
            Sent bubble
         */
        val senderBubble1 = findViewById<BubbleLayout>(R.id.bubbleViewSender)
        senderBubble1.modifier.apply {
            arrowAlignment = ArrowAlignment.RIGHT_TOP
            arrowOffsetY = 18f
        }

        senderBubble1.update(modifier = senderBubble1.modifier)


        val senderBubble2 = findViewById<BubbleLayout>(R.id.bubbleViewSender2)
        senderBubble2.modifier.apply {
            arrowAlignment = ArrowAlignment.RIGHT_BOTTOM
        }

        senderBubble2.update(modifier = senderBubble2.modifier)

        val senderBubble3 = findViewById<BubbleLayout>(R.id.bubbleViewSender3)
        senderBubble3.modifier.apply {
            arrowAlignment = ArrowAlignment.RIGHT_BOTTOM
            withArrow = false
        }

        senderBubble3.update(modifier = senderBubble3.modifier)


        val senderBubble4 = findViewById<BubbleLayout>(R.id.bubbleViewSender4)
        senderBubble4.modifier.apply {
            arrowAlignment = ArrowAlignment.RIGHT_CENTER
            arrowShape = ArrowShape.TRIANGLE_ISOSCELES
        }

        senderBubble4.update(modifier = senderBubble4.modifier)

        /*
            Received Bubbles
         */
        val receiverBubble = findViewById<BubbleLayout>(R.id.bubbleViewReceiver)

        val modifierR1 = receiverBubble.modifier.apply {
            backgroundColor = Color.WHITE
            arrowAlignment = ArrowAlignment.LEFT_BOTTOM
            arrowOffsetY = -20f
        }

        receiverBubble.update(modifierR1)

        val receiverBubble2 = findViewById<BubbleLayout>(R.id.bubbleViewReceiver2)

        val modifierR2 = receiverBubble2.modifier.apply {
            backgroundColor = Color.WHITE
            arrowAlignment = ArrowAlignment.LEFT_TOP
        }

        receiverBubble2.update(modifierR2)


        val receiverBubble3 = findViewById<BubbleLayout>(R.id.bubbleViewReceiver3)

        val modifierR3 = receiverBubble3.modifier.apply {
            backgroundColor = Color.WHITE
            arrowAlignment = ArrowAlignment.LEFT_TOP
            withArrow = false
        }

        receiverBubble3.update(modifierR3)


        val receiverBubble4 = findViewById<BubbleLayout>(R.id.bubbleViewReceiver4)

        val modifierR4 = receiverBubble4.modifier.apply {
            backgroundColor = Color.WHITE
            arrowAlignment = ArrowAlignment.LEFT_CENTER
            arrowShape = ArrowShape.TRIANGLE_ISOSCELES
        }

        receiverBubble4.update(modifierR4)


        /*
            Bottom Bubbles
         */
        val bubbleBottom1 = findViewById<BubbleLayout>(R.id.bubbleViewBottom1)

        val modifierBottom1 = bubbleBottom1.modifier.apply {
            backgroundColor = Color.rgb(251, 192, 45)
            arrowAlignment = ArrowAlignment.LEFT_TOP

        }
        bubbleBottom1.update(modifierBottom1)

        val bubbleBottom2 = findViewById<BubbleLayout>(R.id.bubbleViewBottom2)
        val modifierBottom2 = bubbleBottom2.modifier.apply {
            backgroundColor = Color.rgb(142, 36, 170)
            arrowAlignment = ArrowAlignment.BOTTOM_LEFT
            arrowShape = ArrowShape.TRIANGLE_RIGHT
            arrowWidth = 35f
            arrowHeight = 40f
        }
        bubbleBottom2.update(modifierBottom2)

        val bubbleBottom3 = findViewById<BubbleLayout>(R.id.bubbleViewBottom3)
        val modifierBottom3 = bubbleBottom3.modifier.apply {
            backgroundColor = Color.rgb(0, 121, 107)
            arrowAlignment = ArrowAlignment.BOTTOM_RIGHT
            arrowShape = ArrowShape.TRIANGLE_RIGHT
        }
        bubbleBottom3.update(modifierBottom3)


        /*
            Custom corner radius
         */

        val bubbleCustomRad1 = findViewById<BubbleLayout>(R.id.bubbleCustomRad1)

        val modifierCR1 = bubbleCustomRad1.modifier.apply {
            backgroundColor = Color.rgb(92,107,192)
            arrowAlignment = ArrowAlignment.LEFT_TOP
            withArrow = false
            cornerRadius = CornerRadius(
                topLeftX = 50f,
                topLeftY = 50f,
                topRightX = 24f,
                topRightY = 24f,
                bottomLeftX = 8f,
                bottomLeftY = 8f,
                bottomRightX = 24f,
                bottomRightY = 24f
            )
        }

        bubbleCustomRad1.update(modifierCR1)

        val bubbleCustomRad2 = findViewById<BubbleLayout>(R.id.bubbleCustomRad2)

        val modifierCR2 = bubbleCustomRad2.modifier.apply {
            backgroundColor = Color.rgb(92,107,192)
            arrowAlignment = ArrowAlignment.LEFT_TOP
            withArrow = false

            cornerRadius = CornerRadius(
                topLeftX = 8f,
                topLeftY = 8f,
                topRightX = 24f,
                topRightY = 24f,
                bottomLeftX = 8f,
                bottomLeftY = 8f,
                bottomRightX = 24f,
                bottomRightY = 24f
            )
        }
        bubbleCustomRad2.update(modifierCR2)

        val bubbleCustomRad3 = findViewById<BubbleLayout>(R.id.bubbleCustomRad3)
        val modifierCR3 = bubbleCustomRad3.modifier.apply {
            backgroundColor = Color.rgb(92,107,192)
            arrowAlignment = ArrowAlignment.LEFT_TOP
            withArrow = false
            cornerRadius = CornerRadius(
                topLeftX = 8f,
                topLeftY = 8f,
                topRightX = 24f,
                topRightY = 24f,
                bottomLeftX = 24f,
                bottomLeftY = 24f,
                bottomRightX = 24f,
                bottomRightY = 24f
            )
        }
        bubbleCustomRad3.update(modifierCR3)

    }
}