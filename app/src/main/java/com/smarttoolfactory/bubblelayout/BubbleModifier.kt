package com.smarttoolfactory.bubblelayout

import android.app.Activity
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import androidx.fragment.app.Fragment

class BubbleModifier {

    /**
     * Scale to set initial values as dp
     */
    internal var dp: Float = 1f

    /**
     * Background of Bubble
     */
    var backgroundColor: Int = Color.rgb(220, 248, 198)

    /**
     * Corner radius of bubble layout for y axis
     */
    var cornerRadius = 8f
        set(value) {
            cornerRadiusBundle.topLeft = cornerRadius
            cornerRadiusBundle.topRight = cornerRadius
            cornerRadiusBundle.bottomRight = cornerRadius
            cornerRadiusBundle.bottomLeft = cornerRadius
            field = value
        }

    /**
     * Custom corner radius for each side of the rectangle, if this is not null parameters
     * of this data class is used to draw rounded rectangle.
     */
    var cornerRadiusBundle: CornerRadius = CornerRadius(
        topLeft = cornerRadius,
        topRight = cornerRadius,
        bottomLeft = cornerRadius,
        bottomRight = cornerRadius
    )

    /**
     * Arrow alignment determines in which side of the bubble this arrow should be drawn.
     * When [NONE] is selected no arrow is drawn
     */
    var arrowAlignment: Int = NONE


    /**
     * Top position of arrow
     */
    var arrowTop: Float = 0f

    /**
     * Bottom position of arrow
     */
    var arrowBottom = 0f

    var arrowWidth: Float = 14.0f
    var arrowHeight: Float = 14.0f
    var arrowRadius: Float = 0.0f

    var arrowShape: ArrowShape = ArrowShape.TRIANGLE_RIGHT

    /**
     * Vertical offset for arrow that is positioned on left or right side of the bubble.
     *
     * Positive values move arrow bottom while negative values move up. Arrow position
     * is limited between top of content and  content bottom minus arrow height.
     */
    var arrowOffsetY: Float = 0f


    /**
     * Vertical offset for arrow that is positioned on top or at the bottom of the bubble.
     *
     * Positive values move arrow right while negative values move left. Arrow position
     * is limited between left of content and  content right minus arrow width.
     */
    var arrowOffsetX: Float = 0f


    /**
     * If set to true an arrow is drawn depending on it's alignment, horizontal and vertical
     * offset.
     */
    var withArrow = true

    /**
     * Select how shadow of this bubble should be drawn. If [ShadowStyle.ELEVATION]
     *
     * set elevation in xml or programmatically. If [ShadowStyle.SHADOW] is selected
     * it adds shadow layer to [Paint]
     */
    var shadowStyle: ShadowStyle = ShadowStyle.ELEVATION

    /**
     * This only effect when shadow style is [ShadowStyle.SHADOW]
     */
    var shadowColor: Int = Color.argb(55, 55, 55, 55)

    var shadowRadius: Float = 0f

    var shadowOffsetX: Float = 0f

    var shadowOffsetY: Float = 0f

    fun init() {

        cornerRadius *= dp

        cornerRadiusBundle.apply {
            topLeft = cornerRadius
            topRight = cornerRadius
            bottomLeft = cornerRadius
            bottomRight = cornerRadius
        }

        arrowWidth *= dp
        arrowHeight *= dp
        arrowRadius *= dp
        arrowOffsetY *= dp
        arrowOffsetX *= dp

        shadowRadius *= dp
        shadowOffsetX *= dp
        shadowOffsetY *= dp
    }
}

class CornerRadius(
    var topLeft: Float = 0f,
    var topRight: Float = 0f,
    var bottomLeft: Float = 0f,
    var bottomRight: Float = 0f
)

enum class ShadowStyle {
    ELEVATION,
    SHADOW
}

enum class ArrowShape {
    TRIANGLE_RIGHT,
    TRIANGLE_ISOSCELES,
    CURVED
}

const val NONE = 0
const val LEFT_TOP = 1
const val LEFT_CENTER = 2
const val LEFT_BOTTOM = 3
const val RIGHT_TOP = 4
const val RIGHT_CENTER = 5
const val RIGHT_BOTTOM = 6
const val BOTTOM_LEFT = 7
const val BOTTOM_CENTER = 8
const val BOTTOM_RIGHT = 9

fun Activity.dp(dpValue: Float): Float {
    return try {
        val scale = resources.displayMetrics.density
        (dpValue * scale + 0.5f)
    } catch (e: Exception) {
        (dpValue + 0.5f)
    }
}

fun Fragment.dp(dpValue: Float): Float {
    return try {
        val scale = resources.displayMetrics.density
        (dpValue * scale + 0.5f)
    } catch (e: Exception) {
        (dpValue + 0.5f)
    }
}

fun View.dp(dpValue: Float): Float {
    return try {
        val scale = resources.displayMetrics.density
        (dpValue * scale + 0.5f)
    } catch (e: Exception) {
        (dpValue + 0.5f)
    }
}

fun Activity.dp(dpValue: Int): Int {
    return try {
        val scale = resources.displayMetrics.density
        (dpValue * scale + 0.5f)
    } catch (e: Exception) {
        (dpValue + 0.5f)
    }.toInt()
}

fun Fragment.dp(dpValue: Int): Int {
    return try {
        val scale = resources.displayMetrics.density
        (dpValue * scale + 0.5f)
    } catch (e: Exception) {
        (dpValue + 0.5f)
    }.toInt()
}

fun View.dp(dpValue: Int): Int {
    return try {
        val scale = resources.displayMetrics.density
        (dpValue * scale + 0.5f)
    } catch (e: Exception) {
        (dpValue + 0.5f)
    }.toInt()
}
