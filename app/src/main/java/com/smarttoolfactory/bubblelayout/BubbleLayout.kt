package com.smarttoolfactory.bubblelayout

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import com.smarttoolfactory.bubblelayout.ArrowAlignment.*

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

        // Measure dimensions
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)


        println("🎃 onMeasures widthSize: $widthSize, heightSize: $heightSize childCount: $childCount")

        var maxContentWidth: Int = 0
        var maxContentHeight: Int = 0

        for (i in 0..childCount) {
            val child: View? = getChildAt(i)

            child?.measure(widthMeasureSpec, heightMeasureSpec)
            child?.let { child ->
                println("🔥 onMeasure child #$i measuredWidth: ${child.measuredWidth}, width: ${child.width}, measuredHeight: ${child.measuredHeight}, height: ${child.height}")
                if (child.measuredWidth > maxContentWidth) maxContentWidth = child.measuredWidth
                maxContentHeight += child.measuredHeight
            }

        }

        // Set rectangle for content area, arrow is excluded, on
        val alignment = modifier.arrowAlignment

        val isRightAligned =
            alignment == RIGHT_BOTTOM || alignment == RIGHT_TOP

        val isLeftAligned =
            alignment == LEFT_BOTTOM || alignment == LEFT_TOP

        var desiredWidth = resolveSize(
            maxContentWidth,
            widthMeasureSpec
        ) + paddingStart + paddingEnd

        if (isLeftAligned || isRightAligned) {
            desiredWidth += modifier.arrowWidth.toInt()
        }


        val desiredHeight: Int =
            resolveSize(maxContentHeight, heightMeasureSpec) + paddingTop + paddingBottom

        when {

            isLeftAligned -> {
                rectContent.set(
                    modifier.arrowWidth,
                    0f,
                    maxContentWidth.toFloat() + modifier.arrowWidth + paddingStart + paddingEnd,
                    maxContentHeight.toFloat() + paddingTop + paddingBottom
                )

            }

            isRightAligned -> {
                rectContent.set(
                    0f,
                    0f,
                    maxContentWidth.toFloat() + paddingStart + paddingEnd,
                    maxContentHeight.toFloat() + paddingTop + paddingBottom
                )

            }

            alignment == NONE -> {
                rectContent.set(
                    0f,
                    0f,
                    maxContentWidth.toFloat() + paddingStart + paddingEnd,
                    maxContentHeight.toFloat() + paddingTop + paddingBottom
                )
            }
        }


        rectBubble.set(0f, 0f, desiredWidth.toFloat(), desiredHeight.toFloat())

        logMeasureSpecs("LOG: ", widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(desiredWidth.toInt(), desiredHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        val alignment = modifier.arrowAlignment

        if (alignment == LEFT_BOTTOM || alignment == LEFT_TOP) {

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
        getBubbleClipPath(
            path,
            modifier = modifier,
            contentRect = rectContent,
            bubbleRect = rectBubble
        )

        canvas.drawPath(path, paint)

        paintDebug.color = Color.RED
        canvas.drawRect(rectBubble, paintDebug)
        paintDebug.color = Color.BLUE
        canvas.drawRect(rectContent, paintDebug)

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
    path: Path,
    modifier: Modifier,
    contentRect: RectF,
    bubbleRect: RectF
) {

    path.reset()

    getRoundedRectPath(modifier, path, contentRect)

    if (modifier.withArrow) createArrowPath(
        path = path,
        contentRect = contentRect,
        bubbleRect,
        modifier
    )
}

private fun getRoundedRectPath(
    modifier: Modifier,
    path: Path,
    contentRect: RectF
) {
    val alignment = modifier.arrowAlignment

    val radiusX =
        if (modifier.radiusX < contentRect.width() / 2) modifier.radiusX else contentRect.width() / 2
    val radiusY =
        if (modifier.radiusY < contentRect.height() / 2) modifier.radiusY else contentRect.height() / 2

    modifier.cornerRadius?.let { cornerRadius ->

        val radii = floatArrayOf(
            cornerRadius.topLeftX,
            cornerRadius.topLeftY,
            cornerRadius.topRightX,
            cornerRadius.topRightY,
            cornerRadius.bottomRightX,
            cornerRadius.bottomRightY,
            cornerRadius.bottomLeftX,
            cornerRadius.bottomRightY
        )

        path.addRoundRect(contentRect, radii, Path.Direction.CW)

    } ?: run {
        when (alignment) {

            LEFT_TOP -> {

                val topLeftX =
                    if (modifier.withArrow && modifier.arrowOffsetY < radiusY) 0f else radiusX
                val topLeftY =
                    if (modifier.withArrow && modifier.arrowOffsetY < radiusY) 0f else radiusY

                val radii = floatArrayOf(
                    topLeftX,
                    topLeftY,
                    radiusX,
                    radiusY,
                    radiusX,
                    radiusY,
                    radiusX,
                    radiusY
                )
                path.addRoundRect(contentRect, radii, Path.Direction.CW)

            }
            LEFT_BOTTOM -> {

                val bottomLeftX =
                    if (modifier.withArrow && modifier.arrowOffsetY < radiusY) 0f else radiusX
                val bottomLeftY =
                    if (modifier.withArrow && modifier.arrowOffsetY < radiusY) 0f else radiusY


                val radii = floatArrayOf(
                    radiusX,
                    radiusY,
                    radiusX,
                    radiusY,
                    radiusX,
                    radiusY,
                    bottomLeftX,
                    bottomLeftY
                )
                path.addRoundRect(contentRect, radii, Path.Direction.CW)
            }

            RIGHT_TOP -> {

                val topRightX =
                    if (modifier.withArrow && modifier.arrowOffsetY < radiusY) 0f else radiusX
                val topRightY =
                    if (modifier.withArrow && modifier.arrowOffsetY < radiusY) 0f else radiusY

                val radii = floatArrayOf(
                    radiusX,
                    radiusY,
                    topRightX,
                    topRightY,
                    radiusX,
                    radiusY,
                    radiusX,
                    radiusY
                )
                path.addRoundRect(contentRect, radii, Path.Direction.CW)
            }

            RIGHT_BOTTOM -> {

                val bottomRightX =
                    if (modifier.withArrow && modifier.arrowOffsetY < radiusY) 0f else radiusX
                val bottomRightY =
                    if (modifier.withArrow && modifier.arrowOffsetY < radiusY) 0f else radiusY

                val radii = floatArrayOf(
                    radiusX,
                    radiusY,
                    radiusX,
                    radiusY,
                    bottomRightX,
                    bottomRightY,
                    radiusX,
                    radiusY
                )
                path.addRoundRect(contentRect, radii, Path.Direction.CW)
            }

            else -> {
                path.addRoundRect(
                    contentRect,
                    modifier.radiusX,
                    modifier.radiusY,
                    Path.Direction.CW
                )
            }
        }
    }
}

/**
 * Creates path for arrow
 */
fun createArrowPath(path: Path, contentRect: RectF, bubbleRect: RectF, modifier: Modifier) {

    val contentHeight = contentRect.height()
    val contentWidth = contentRect.width()

    val arrowWidth = modifier.arrowWidth
    val contentLeft = contentRect.left
    val contentRight = contentRect.right
    val contentTop = contentRect.top
    val contentBottom = contentRect.bottom


    val arrowHeight =
        if (modifier.arrowHeight + modifier.radiusY * 2 > contentHeight)
            contentHeight - modifier.radiusY * 2 else modifier.arrowHeight

    // This is offset from top or bottom. Maximum offset + arrow height cannot be bigger
    // than bottom of bubble or smaller than top of bubble. Y increases as we go to bottom
    val offsetY =
        if (modifier.arrowOffsetY + arrowHeight > contentHeight) {
            contentHeight - arrowHeight
        } else modifier.arrowOffsetY


    val arrowShape = modifier.arrowShape

    when (modifier.arrowAlignment) {

        LEFT_TOP -> {
            // move to top left corner of the content
            path.moveTo(contentLeft, contentTop + offsetY)

            when (arrowShape) {

                ArrowShape.TRIANGLE_RIGHT -> {
                    // Draw horizontal line to left
                    path.lineTo(0f, contentTop + offsetY)
                    path.lineTo(contentLeft, contentTop + offsetY + arrowHeight)
                }

                ArrowShape.TRIANGLE_ISOSCELES -> {
                    path.lineTo(0f, contentTop + offsetY + arrowHeight / 2f)
                    path.lineTo(contentLeft, contentTop + offsetY + arrowHeight)
                }

                ArrowShape.CURVED -> {

                    val x1 = 0f
                    val y1 = contentTop + offsetY

                    val x2 = contentLeft
                    val y2 = contentTop + offsetY + arrowHeight

                    path.lineTo(arrowWidth - contentLeft * .9f, y1)
                    path.quadTo(x1, y1, x2, y2)
                    //                path.quadTo((x1 + x2) / 4f, (y1 + y2) * 3 / 4f, x2, y2)
                    //                path.cubicTo((x1 + x2) * .2f, y1*1.2f, (x1 + x2) * .7f, (y1 + y2) * .35f, x2, y2)
                }
            }

            path.close()

        }

        LEFT_BOTTOM -> {

            // move to bottom left corner of the content
            path.moveTo(contentLeft, contentBottom - offsetY)

            when (arrowShape) {

                ArrowShape.TRIANGLE_RIGHT -> {
                    // Draw horizontal line to left
                    path.lineTo(0f, contentBottom - offsetY)
                    path.lineTo(contentLeft, contentBottom - offsetY - arrowHeight)
                }

                ArrowShape.TRIANGLE_ISOSCELES -> {
                    // Draw horizontal line to left
                    path.lineTo(contentLeft, contentBottom - offsetY - arrowHeight)
                    path.lineTo(0f, contentBottom - offsetY - arrowHeight / 2f)
                }

                ArrowShape.CURVED -> {

                }

            }

            path.close()

        }

        RIGHT_TOP -> {

            // move to top right corner of the content
            path.moveTo(contentRight, contentTop + offsetY)

            when (arrowShape) {

                ArrowShape.TRIANGLE_RIGHT -> {
                    path.lineTo(contentRight + arrowWidth, contentTop + offsetY)
                    path.lineTo(contentRight, contentTop + offsetY + arrowHeight)
                }

                ArrowShape.TRIANGLE_ISOSCELES -> {
                    path.lineTo(contentRight + arrowWidth, contentTop + offsetY + arrowHeight / 2f)
                    path.lineTo(contentRight, contentTop + offsetY + arrowHeight)
                }

                ArrowShape.CURVED -> {

                }

            }


            path.close()
        }

        RIGHT_BOTTOM -> {

            // move to bottom right corner of the content
            path.moveTo(contentRight, contentBottom - offsetY)

            when (arrowShape) {

                ArrowShape.TRIANGLE_RIGHT -> {
                    path.lineTo(contentRight + arrowWidth, contentBottom - offsetY)
                    path.lineTo(contentRight, contentBottom - offsetY - arrowHeight)
                }

                ArrowShape.TRIANGLE_ISOSCELES -> {
                    path.lineTo(
                        contentRight + arrowWidth,
                        contentBottom - offsetY - arrowHeight / 2f
                    )
                    path.lineTo(contentRight, contentBottom - offsetY - arrowHeight)
                }

                ArrowShape.CURVED -> {

                }

            }


            path.close()
        }
        NONE -> Unit
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
     * Custom corner radius for each side of the rectangle, if this is not null parameters
     * of this data class is used to draw rounded rectangle.
     */
    var cornerRadius: CornerRadius? = null

    /**
     * Scale to set initial values as dp
     */
//    var dp: Float = 1f

//    var padding = Padding(0f * dp, 0f * dp, 0f * dp, 0f * dp)
//    var margin = Margin(0f * dp, 0f * dp, 0f * dp, 0f * dp)


    var arrowAlignment: ArrowAlignment = NONE
    var arrowWidth: Float = 14.0f
    var arrowHeight: Float = 14.0f
    var arrowRadius: Float = 0.0f

    var arrowShape: ArrowShape = ArrowShape.TRIANGLE_ISOSCELES

    /**
     * Vertical offset for arrow. If arrow is aligned to top it moves arrow downwards.
     * For
     */
    var arrowOffsetY: Float = 0f

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
}

data class CornerRadius(
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

enum class ArrowShape {
    TRIANGLE_RIGHT,
    TRIANGLE_ISOSCELES,
    CURVED
}

enum class ArrowAlignment {
    LEFT_TOP,
    LEFT_BOTTOM,
    RIGHT_TOP,
    RIGHT_BOTTOM,
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

