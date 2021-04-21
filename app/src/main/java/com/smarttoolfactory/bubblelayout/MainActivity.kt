package com.smarttoolfactory.bubblelayout

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
            Alternative 1- Draw bubbles using Modifier programmatically
         */
//        setContentView(R.layout.activity_main)
//        drawBubbles()

        /*
            Alternative 2- Draw bubbles with XML properties
         */
        setContentView(R.layout.activity_main_bubble_linear_with_attrs)
    }

    private fun drawBubbles() {

        val dateBubble = findViewById<BubbleLayout>(R.id.bubbleDate)
        dateBubble.modifier.apply {
            arrowAlignment = NONE
            backgroundColor = Color.rgb(212, 234, 244)
        }
        dateBubble.update(modifier = dateBubble.modifier)

        dateBubble.paddingStart

        /*
            Sent bubble
         */
        val senderBubble1 = findViewById<BubbleLayout>(R.id.bubbleViewSender)
        senderBubble1.modifier.apply {
            arrowAlignment = RIGHT_TOP
            arrowOffsetY = dp(5f)
        }

        senderBubble1.update(modifier = senderBubble1.modifier)


        val senderBubble2 = findViewById<BubbleLayout>(R.id.bubbleViewSender2)
        senderBubble2.modifier.apply {
            arrowAlignment = RIGHT_BOTTOM
        }

        senderBubble2.update(modifier = senderBubble2.modifier)

        val senderBubble3 = findViewById<BubbleLayout>(R.id.bubbleViewSender3)
        senderBubble3.modifier.apply {
            arrowAlignment = RIGHT_BOTTOM
            withArrow = false
        }

        senderBubble3.update(modifier = senderBubble3.modifier)


        val senderBubble4 = findViewById<BubbleLayout>(R.id.bubbleViewSender4)
        senderBubble4.modifier.apply {
            arrowAlignment = RIGHT_CENTER
            arrowShape = ArrowShape.TRIANGLE_ISOSCELES
        }

        senderBubble4.update(modifier = senderBubble4.modifier)

        /*
            Received Bubbles
         */
        val receiverBubble = findViewById<BubbleLayout>(R.id.bubbleViewReceiver)

        val modifierR1 = receiverBubble.modifier.apply {
            backgroundColor = Color.WHITE
            arrowAlignment = LEFT_BOTTOM
            arrowOffsetY = -dp(5f)
        }

        receiverBubble.update(modifierR1)

        val receiverBubble2 = findViewById<BubbleLayout>(R.id.bubbleViewReceiver2)

        val modifierR2 = receiverBubble2.modifier.apply {
            backgroundColor = Color.WHITE
            arrowAlignment = LEFT_TOP
        }

        receiverBubble2.update(modifierR2)


        val receiverBubble3 = findViewById<BubbleLayout>(R.id.bubbleViewReceiver3)

        val modifierR3 = receiverBubble3.modifier.apply {
            backgroundColor = Color.WHITE
            arrowAlignment = LEFT_TOP
            withArrow = false
        }

        receiverBubble3.update(modifierR3)


        val receiverBubble4 = findViewById<BubbleLayout>(R.id.bubbleViewReceiver4)

        val modifierR4 = receiverBubble4.modifier.apply {
            arrowHeight = 24f.dp
            backgroundColor = Color.WHITE
            arrowAlignment = LEFT_CENTER
            arrowShape = ArrowShape.TRIANGLE_ISOSCELES
        }

        receiverBubble4.update(modifierR4)


        /*
            Bottom Bubbles
         */
        val bubbleBottom1 = findViewById<BubbleLayout>(R.id.bubbleViewBottom1)

        val modifierBottom1 = bubbleBottom1.modifier.apply {
            backgroundColor = Color.rgb(251, 192, 45)
            arrowAlignment = BOTTOM_CENTER
            arrowShape = ArrowShape.TRIANGLE_ISOSCELES

        }
        bubbleBottom1.update(modifierBottom1)

        val bubbleBottom2 = findViewById<BubbleLayout>(R.id.bubbleViewBottom2)
        val modifierBottom2 = bubbleBottom2.modifier.apply {
            backgroundColor = Color.rgb(142, 36, 170)
            arrowAlignment = BOTTOM_LEFT
            arrowShape = ArrowShape.TRIANGLE_RIGHT
            arrowWidth = dp(48f)
            arrowHeight = dp(16f)
        }
        bubbleBottom2.update(modifierBottom2)

        val bubbleBottom3 = findViewById<BubbleLayout>(R.id.bubbleViewBottom3)
        val modifierBottom3 = bubbleBottom3.modifier.apply {
            backgroundColor = Color.rgb(0, 121, 107)
            arrowAlignment = BOTTOM_RIGHT
            arrowShape = ArrowShape.TRIANGLE_RIGHT
        }
        bubbleBottom3.update(modifierBottom3)


        /*
            Custom corner radius
         */

        val bubbleCustomRad1 = findViewById<BubbleLayout>(R.id.bubbleCustomRad1)

        val modifierCR1 = bubbleCustomRad1.modifier.apply {
            backgroundColor = Color.rgb(92, 107, 192)
            arrowAlignment = LEFT_TOP
            withArrow = false
            cornerRadiusBundle = CornerRadius(
                topLeft = dp(24f),
                topRight = dp(16f),
                bottomLeft = dp(2f),
                bottomRight = dp(16f)
            )
        }

        bubbleCustomRad1.update(modifierCR1)

        val bubbleCustomRad2 = findViewById<BubbleLayout>(R.id.bubbleCustomRad2)

        val modifierCR2 = bubbleCustomRad2.modifier.apply {
            backgroundColor = Color.rgb(92, 107, 192)
            arrowAlignment = LEFT_TOP
            withArrow = false

            cornerRadiusBundle = CornerRadius(
                topLeft = dp(2f),
                topRight = dp(16f),
                bottomLeft = dp(2f),
                bottomRight = dp(16f)
            )
        }
        bubbleCustomRad2.update(modifierCR2)

        val bubbleCustomRad3 = findViewById<BubbleLayout>(R.id.bubbleCustomRad3)
        val modifierCR3 = bubbleCustomRad3.modifier.apply {
            backgroundColor = Color.rgb(92, 107, 192)
            arrowAlignment = LEFT_TOP
            withArrow = false
            cornerRadiusBundle = CornerRadius(
                topLeft = dp(2f),
                topRight = dp(16f),
                bottomLeft = dp(8f),
                bottomRight = dp(16f)
            )
        }
        bubbleCustomRad3.update(modifierCR3)

    }

    private val Float.dp
        get() = this@MainActivity.dp(this)

    private val Int.dp
        get() = this@MainActivity.dp(this)
}