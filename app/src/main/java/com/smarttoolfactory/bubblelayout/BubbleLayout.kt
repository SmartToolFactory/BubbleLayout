package com.smarttoolfactory.bubblelayout

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout

/**
 * Linear layout that draws chat or speech bubble with specified properties.
 *
 * Properties are encapsulated inside [Modifier]
 */
class BubbleLayout : FrameLayout {

    var modifier: Modifier = Modifier()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = modifier.backgroundColor
    }

    private val paintDebug by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 4f
            color = Color.RED
        }
    }

    /**
     * Rectangle for drawing content, this is the are that contains child views. Arrow is
     * excluded from this rectangle.
     */
    private var rectContent = RectF()


    /**
     * Rectangle that covers content and arrow of the bubble layout. Total area is covered
     * in this rectangle.
     */
    private var rectBubble = RectF()

    private var path = Path()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


    init {
        modifier.arrowWidth = context.dp2Px(modifier.arrowWidth)
        modifier.arrowHeight = context.dp2Px(modifier.arrowHeight)
        modifier.radiusX = context.dp2Px(modifier.radiusX)
        modifier.radiusY = context.dp2Px(modifier.radiusY)


//        setLayerType(LAYER_TYPE_SOFTWARE, paint)
//        paint.setShadowLayer(
//            3f,
//            3f,
//            3f,
//            Color.GRAY
//        )

        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // Measure dimensions
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)


        println("🎃 onMeasures widthSize: $widthSize, heightSize: $heightSize childCount: $childCount")

        var maxWidth: Int = 0
        var maxHeight: Int = 0

        for (i in 0..childCount) {
            val child: View? = getChildAt(i)

            child?.measure(widthMeasureSpec, heightMeasureSpec)
            child?.let { child ->
                println("🔥 onMeasure child #$i measuredWidth: ${child.measuredWidth}, width: ${child.width}, measuredHeight: ${child.measuredHeight}, height: ${child.height}")
                if (child.measuredWidth > maxWidth) maxWidth = child.measuredWidth
                maxHeight += child.measuredHeight
            }

        }


        // Set rectangle for content area, arrow is excluded, on
        val alignment = modifier.arrowAlignment
        if (alignment == ArrowAlignment.BOTTOM_RIGHT || alignment == ArrowAlignment.TOP_RIGHT) {
            rectContent.set(0f, 0f, maxWidth.toFloat(), maxHeight.toFloat())
        } else {
            rectContent.set(
                modifier.arrowWidth,
                0f,
                maxWidth.toFloat() + modifier.arrowWidth,
                maxHeight.toFloat()
            )

        }

        maxWidth += modifier.arrowWidth.toInt()

        val desiredWidth = resolveSize(maxWidth, widthMeasureSpec)
        val desiredHeight: Int = resolveSize(maxHeight, heightMeasureSpec)

        rectBubble.set(0f, 0f, desiredWidth.toFloat(), desiredHeight.toFloat())

        logMeasureSpecs("LOG: ", widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(desiredWidth, desiredHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        val alignment = modifier.arrowAlignment

        if (alignment == ArrowAlignment.BOTTOM_LEFT || alignment == ArrowAlignment.TOP_LEFT) {

            for (i in 0..childCount) {
                val child: View? = getChildAt(i)
                child?.let { child ->
                    child.layout(
                        (paddingStart + modifier.arrowWidth).toInt(),
                        paddingTop,
                        (paddingStart + child.width + modifier.arrowWidth).toInt(),
                        paddingTop + child.height
                    )
                }

            }
        } else {

            for (i in 0..childCount) {
                val child: View? = getChildAt(i)
                child?.let { child ->
                    child.layout(
                        paddingStart,
                        paddingTop,
                        paddingStart + child.width,
                        paddingTop + child.height
                    )
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Get path for this bubble
        path = getBubbleClipPath(
            modifier = modifier,
            contentRect = rectContent,
            bubbleRect = rectBubble
        )

        canvas.drawPath(path, paint)

//        paintDebug.color = Color.RED
//        canvas.drawRect(rectBubble, paintDebug)
//        paintDebug.color = Color.BLUE
//        canvas.drawRect(rectContent, paintDebug)

        outlineProvider = outlineProvider
    }

    fun update(modifier: Modifier) {
        this.modifier = modifier
        paint.color = modifier.backgroundColor
        invalidate()
    }


    override fun getOutlineProvider(): ViewOutlineProvider? {
        return object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                if (!isInEditMode) {

                    try {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                            outline.setConvexPath(path)

                        } else {
                            outline.setPath(path)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun logMeasureSpecs(text: String, widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        val measureSpecHeight: String = when (heightMode) {
            MeasureSpec.EXACTLY -> {
                "EXACTLY"
            }
            MeasureSpec.AT_MOST -> {
                "AT_MOST"
            }
            else -> {
                "UNSPECIFIED"
            }
        }
        val measureSpecWidth: String = when (widthMode) {
            MeasureSpec.EXACTLY -> {
                "EXACTLY"
            }
            MeasureSpec.AT_MOST -> {
                "AT_MOST"
            }
            else -> {
                "UNSPECIFIED"
            }
        }
        val TAG = javaClass.simpleName

        Log.d(
            TAG, "TEXT: $text,  Width: " + measureSpecWidth + ", " + width + " Height: "
                    + measureSpecHeight + ", " + height
        )

        println(
            "TEXT: $text,  Width: " + measureSpecWidth + ", " + width + " Height: "
                    + measureSpecHeight + ", " + height
        )
    }

}


/**
 * Function that returns bubble path.
 *
 * @param modifier sum of properties of this layout which includes arrow alignement, position,etc.
 * @param contentRect rectangle of content area
 * @param bubbleRect rectangle of total area of this layout
 */
fun getBubbleClipPath(
    modifier: Modifier,
    contentRect: RectF,
    bubbleRect: RectF
): Path {

    val path = Path()

    val alignment = modifier.arrowAlignment


    val radiusX = modifier.radiusX
    val radiusY = modifier.radiusY

    when (alignment) {


        ArrowAlignment.TOP_LEFT -> {
            val radii = floatArrayOf(0f, 0f, radiusX, radiusY, radiusX, radiusY, radiusX, radiusY)
            path.addRoundRect(contentRect, radii, Path.Direction.CW)

        }
        ArrowAlignment.BOTTOM_LEFT -> {
            val radii = floatArrayOf(radiusX, radiusY, radiusX, radiusY, radiusX, radiusY, 0f, 0f)
            path.addRoundRect(contentRect, radii, Path.Direction.CW)
        }

        ArrowAlignment.TOP_RIGHT -> {
            val radii = floatArrayOf(radiusX, radiusY, 0f, 0f, radiusX, radiusY, radiusX, radiusY)
            path.addRoundRect(contentRect, radii, Path.Direction.CW)
        }

        ArrowAlignment.BOTTOM_RIGHT -> {
            val radii = floatArrayOf(radiusX, radiusY, radiusX, radiusY, 0f, 0f, radiusX, radiusY)
            path.addRoundRect(contentRect, radii, Path.Direction.CW)
        }


        else -> {
            path.addRoundRect(contentRect, modifier.radiusX, modifier.radiusY, Path.Direction.CW)
        }
    }


    createArrowPath(path = path, contentRect = contentRect, bubbleRect, modifier)

    return path
}

/**
 * Creates path for arrow
 */
fun createArrowPath(path: Path, contentRect: RectF, bubbleRect: RectF, modifier: Modifier) {

    when (modifier.arrowAlignment) {


        ArrowAlignment.TOP_LEFT -> {
            path.moveTo(contentRect.left, contentRect.top)
            path.lineTo(0f, 0f)
            path.lineTo(contentRect.left, contentRect.top + modifier.arrowHeight)
            path.close()

        }

        ArrowAlignment.BOTTOM_LEFT -> {
            path.moveTo(contentRect.left, contentRect.bottom)
            path.lineTo(0f, contentRect.bottom)
            path.lineTo(contentRect.left, contentRect.bottom - modifier.arrowHeight)
            path.close()

        }

        ArrowAlignment.TOP_RIGHT -> {
            path.moveTo(contentRect.right, contentRect.top)
            path.lineTo(bubbleRect.right, bubbleRect.top)
            path.lineTo(contentRect.right, contentRect.top + modifier.arrowHeight)
            path.close()
        }

        ArrowAlignment.BOTTOM_RIGHT -> {
            path.moveTo(contentRect.right, contentRect.bottom)
            path.lineTo(bubbleRect.right, contentRect.bottom)
            path.lineTo(contentRect.right, contentRect.bottom - modifier.arrowHeight)
            path.close()
        }

        else -> {
        }
    }

}


class Modifier {

    /**
     * Background of Bubble
     */
    var backgroundColor: Int = Color.rgb(220, 248, 198)


    /**
     * Corner radius of bubble layout for x axis
     */
    var radiusX = 8f


    /**
     * Corner radius of bubble layout for y axis
     */
    var radiusY = 8f

    /**
     * Scale to set initial values as dp
     */
//    var dp: Float = 1f

//    var padding = Padding(0f * dp, 0f * dp, 0f * dp, 0f * dp)
//    var margin = Margin(0f * dp, 0f * dp, 0f * dp, 0f * dp)


    var arrowAlignment: ArrowAlignment = ArrowAlignment.BOTTOM_LEFT
    var arrowWidth: Float = 14.0f
    var arrowHeight: Float = 14.0f
    var arrowRadius: Float = 0.0f
    var arrowOffset: Float = 0.0f

    var shadowStyle: ShadowStyle = ShadowStyle.ELEVATION
    var shadowColor: Int = 0
}

data class Corners(
    val topLeftX: Float = 0f,
    val topLeftY: Float = 0f,
    val topRightX: Float = 0f,
    val topRightY: Float = 0f,
    val bottomLeftX: Float = 0f,
    val bottomLeftY: Float = 0f,
    val bottomRightX: Float = 0f,
    val bottomRightY: Float = 0f
)

data class Padding(var start: Float, var top: Float, var end: Float, var bottom: Float)
data class Margin(var start: Float, var top: Float, var end: Float, var bottom: Float)

enum class ShadowStyle {
    ELEVATION,
    SHADOW
}

enum class ArrowAlignment {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
    NONE
}

//abstract class Modifier {
//
//    internal var backgroundColor: Int = 0
//
//    fun backgroundColor(color: Int): Modifier = apply { backgroundColor = color }
//
//
////    abstract var padding: Padding
////    abstract margin: Margin
////
////    abstract arrowAlignment: ArrowAlignment
////    abstract arrowWidth: Float
////    abstract arrowHeight: Float
////    abstract arrowRadius: Float
////    abstract arrowOffset: Float
////
////    abstract shadowStyle: ShadowStyle
////    abstract shadowColor: Int
//
//
//}

/**
 * Convert density to pixel to draw on Canvas
 */
fun Context.dp2Px(dpValue: Float): Float {
    return try {
        val scale = resources.displayMetrics.density
        (dpValue * scale + 0.5f)
    } catch (e: Exception) {
        (dpValue + 0.5f)
    }
}


//fun Modifier.arrowAlignment(arrowAlignment: ArrowAlignment): Modifier =
//    apply { this.arrowAlignment = arrowAlignment }
//
//fun Modifier.arrowWidth(width: Float): Modifier = apply {
//    require(width > 0)
//    arrowHeight = width
//}
//
//fun Modifier.arrowHeight(height: Float): Modifier = apply {
//    require(height > 0)
//    arrowHeight = height
//}


//class ArrowModifier(
//    var arrowAlignment: ArrowAlignment,
//    var arrowWidth: Float,
//    var arrowHeight: Float,
//    var arrowRadius: Float,
//    var arrowOffset: Float
//) : Modifier

