package com.smarttoolfactory.bubblelayout

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
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

    lateinit var modifier: Modifier

    private val paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = modifier.backgroundColor
        }
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

    /**
     * Setting this flag to true draws content and bubble rectangles around this layout
     */
    var isDebug = false

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BubbleLayout)


        typedArray.recycle()

    }


    init {

        modifier = Modifier()

        modifier.dp = dp(1f)
        modifier.init()

        if (modifier.shadowStyle == ShadowStyle.SHADOW) {

            setLayerType(LAYER_TYPE_SOFTWARE, paint)
            paint.setShadowLayer(
                modifier.shadowRadius,
                modifier.shadowOffsetX,
                modifier.shadowOffsetY,
                modifier.shadowColor
            )
        }

        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val pair = measureBubbleLayout(widthMeasureSpec, heightMeasureSpec)

        val desiredWidth = pair.first
        val desiredHeight: Int = pair.second

        logMeasureSpecs("LOG: ", widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(desiredWidth, desiredHeight)
    }

    private fun measureBubbleLayout(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ): Pair<Int, Int> {

        // Set rectangle for content area, arrow is excluded, on
        val alignment = modifier.arrowAlignment

        val isHorizontalRightAligned = isHorizontalRightAligned(alignment)
        val isHorizontalLeftAligned = isHorizontalLeftAligned(alignment)
        val isVerticalBottomAligned = isVerticalBottomAligned(alignment)


        // Measure dimensions
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)


        println("🎃 onMeasures widthSize: $widthSize, heightSize: $heightSize childCount: $childCount")

        var maxContentWidth: Int = 0
        var maxContentHeight: Int = 0

        // Measure children to get width and height for content area
        for (i in 0..childCount) {
            val child: View? = getChildAt(i)
            child?.measure(widthMeasureSpec, heightMeasureSpec)
            child?.let { child ->
                println(
                    "🔥 onMeasure child #$i, " +
                            "measuredWidth: ${child.measuredWidth}, width: ${child.width}, " +
                            "measuredHeight: ${child.measuredHeight}, height: ${child.height}"
                )

                if (child.measuredWidth > maxContentWidth) maxContentWidth = child.measuredWidth
                maxContentHeight += child.measuredHeight
            }
        }

        var desiredWidth = resolveSize(
            maxContentWidth,
            widthMeasureSpec
        ) + paddingStart + paddingEnd

        if (isHorizontalLeftAligned || isHorizontalRightAligned) {
            desiredWidth += modifier.arrowWidth.toInt()
        }

        var desiredHeight: Int =
            resolveSize(maxContentHeight, heightMeasureSpec) + paddingTop + paddingBottom
        if (isVerticalBottomAligned) desiredHeight += modifier.arrowHeight.toInt()

        when {
            isHorizontalLeftAligned -> {
                rectContent.set(
                    modifier.arrowWidth,
                    0f,
                    maxContentWidth.toFloat() + modifier.arrowWidth + paddingStart + paddingEnd,
                    maxContentHeight.toFloat() + paddingTop + paddingBottom
                )

            }

            isHorizontalRightAligned -> {
                rectContent.set(
                    0f,
                    0f,
                    maxContentWidth.toFloat() + paddingStart + paddingEnd,
                    maxContentHeight.toFloat() + paddingTop + paddingBottom
                )

            }

            isVerticalBottomAligned -> {
                rectContent.set(
                    0f,
                    0f,
                    maxContentWidth.toFloat() + paddingStart + paddingEnd,
                    maxContentHeight.toFloat() + paddingTop + paddingBottom
                )
            }

            else -> {
                rectContent.set(
                    0f,
                    0f,
                    maxContentWidth.toFloat() + paddingStart + paddingEnd,
                    maxContentHeight.toFloat() + paddingTop + paddingBottom
                )
            }
        }


        rectBubble.set(0f, 0f, desiredWidth.toFloat(), desiredHeight.toFloat())
        return Pair(desiredWidth, desiredHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val alignment = modifier.arrowAlignment
        layoutChildren(alignment)
    }

    private fun layoutChildren(alignment: ArrowAlignment) {

        val isHorizontalRightAligned = isHorizontalRightAligned(alignment)
        val isHorizontalLeftAligned = isHorizontalLeftAligned(alignment)
        val isVerticalBottomAligned = isVerticalBottomAligned(alignment)

        when {

            // Arrow on left side
            isHorizontalLeftAligned -> {
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
            }

            // Arrow on right side
            isHorizontalRightAligned -> {

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

            // Arrow at the bottom
            isVerticalBottomAligned -> {
                for (i in 0..childCount) {
                    val child: View? = getChildAt(i)
                    child?.let { child ->
                        child.layout(
                            paddingStart,
                            paddingTop,
                            paddingStart + child.width,
                            (paddingTop + child.height + modifier.arrowHeight).toInt()
                        )
                    }
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
        )

        canvas.drawPath(path, paint)

        if (modifier.shadowStyle == ShadowStyle.ELEVATION) {
            outlineProvider = outlineProvider
        }
    }

    fun update(modifier: Modifier) {
        this.modifier = modifier
        paint.color = modifier.backgroundColor
        invalidate()
    }

    override fun getOutlineProvider(): ViewOutlineProvider? {
        return object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
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

/**
 * Function that returns bubble path.
 *
 * @param modifier sum of properties of this layout which includes arrow alignemnt, position,etc.
 * @param contentRect rectangle of content area
 *
 */
fun getBubbleClipPath(
    path: Path,
    modifier: Modifier,
    contentRect: RectF
) {

    path.reset()

    getRoundedRectPath(modifier, path, contentRect)

    if (modifier.withArrow) {
        if (isArrowHorizontalPosition(modifier.arrowAlignment)) {
            createHorizontalArrowPath(
                path = path,
                contentRect = contentRect,
                modifier = modifier
            )
        } else if (isArrowVerticalPosition(modifier.arrowAlignment)) {
            createVerticalArrowPath(
                path = path,
                contentRect = contentRect,
                modifier = modifier
            )
        }
    }
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
            cornerRadius.bottomLeftY
        )

        path.addRoundRect(contentRect, radii, Path.Direction.CW)

    } ?: run {
        when (alignment) {

            LEFT_TOP -> {

                val radiusTopLeftX =
                    if (modifier.withArrow && modifier.arrowOffsetY < radiusY) 0f else radiusX

                val radiusTopLeftY =
                    if (modifier.withArrow && modifier.arrowOffsetY < radiusY) 0f else radiusY

                val radii = floatArrayOf(
                    radiusTopLeftX,
                    radiusTopLeftY,
                    radiusX,
                    radiusY,
                    radiusX,
                    radiusY,
                    radiusX,
                    radiusY
                )
                path.addRoundRect(contentRect, radii, Path.Direction.CW)

            }
            LEFT_BOTTOM, BOTTOM_LEFT -> {

                val radiusBottomLeftX =
                    if (modifier.withArrow && modifier.arrowOffsetY < radiusY) 0f else radiusX
                val radiusBottomLeftY =
                    if (modifier.withArrow && modifier.arrowOffsetY < radiusY) 0f else radiusY


                val radii = floatArrayOf(
                    radiusX,
                    radiusY,
                    radiusX,
                    radiusY,
                    radiusX,
                    radiusY,
                    radiusBottomLeftX,
                    radiusBottomLeftY
                )
                path.addRoundRect(contentRect, radii, Path.Direction.CW)
            }

            RIGHT_TOP -> {

                val radiusTopRightX =
                    if (modifier.withArrow && modifier.arrowOffsetY < radiusY) 0f else radiusX
                val radiusTopRightY =
                    if (modifier.withArrow && modifier.arrowOffsetY < radiusY) 0f else radiusY

                val radii = floatArrayOf(
                    radiusX,
                    radiusY,
                    radiusTopRightX,
                    radiusTopRightY,
                    radiusX,
                    radiusY,
                    radiusX,
                    radiusY
                )
                path.addRoundRect(contentRect, radii, Path.Direction.CW)
            }

            RIGHT_BOTTOM, BOTTOM_RIGHT -> {

                val radiusBottomRightX =
                    if (modifier.withArrow && modifier.arrowOffsetY < radiusY) 0f else radiusX
                val radiusBottomRightY =
                    if (modifier.withArrow && modifier.arrowOffsetY < radiusY) 0f else radiusY

                val radii = floatArrayOf(
                    radiusX,
                    radiusY,
                    radiusX,
                    radiusY,
                    radiusBottomRightX,
                    radiusBottomRightY,
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
