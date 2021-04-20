package com.smarttoolfactory.bubblelayout

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import kotlin.math.min

/**
 * Linear layout that draws chat or speech bubble with specified properties.
 *
 * Properties are encapsulated inside [BubbleModifier]
 */
class BubbleLayout : FrameLayout {

    lateinit var modifier: BubbleModifier

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
        modifier = BubbleModifier()
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BubbleLayout)

        modifier = BubbleModifier()

        modifier.backgroundColor = typedArray.getColor(
            R.styleable.BubbleLayout_android_background,
            modifier.backgroundColor
        )
        modifier.cornerRadius =
            typedArray.getDimension(
                R.styleable.BubbleLayout_cornerRadius,
                modifier.cornerRadius
            )

        modifier.cornerRadiusBundle.topLeft = typedArray.getDimension(
            R.styleable.BubbleLayout_cornerRadiusTopLeft,
            modifier.cornerRadius
        )

        modifier.cornerRadiusBundle.topRight = typedArray.getDimension(
            R.styleable.BubbleLayout_cornerRadiusTopRight,
            modifier.cornerRadius
        )

        modifier.cornerRadiusBundle.bottomRight = typedArray.getDimension(
            R.styleable.BubbleLayout_cornerRadiusBottomRight,
            modifier.cornerRadius
        )

        modifier.cornerRadiusBundle.bottomLeft = typedArray.getDimension(
            R.styleable.BubbleLayout_cornerRadiusBottomLeft,
            modifier.cornerRadius
        )

        // Arrow
        modifier.arrowAlignment = typedArray.getInt(R.styleable.BubbleLayout_arrowAlignment, NONE)

        modifier.arrowWidth =
            typedArray.getDimension(R.styleable.BubbleLayout_arrowWidth, modifier.arrowWidth)
        modifier.arrowHeight =
            typedArray.getDimension(R.styleable.BubbleLayout_arrowHeight, modifier.arrowHeight)
        modifier.arrowRadius =
            typedArray.getDimension(R.styleable.BubbleLayout_arrowRadius, modifier.arrowRadius)

        val arrowShape =
            typedArray.getInt(R.styleable.BubbleLayout_arrowShape, modifier.arrowShape.ordinal)

        modifier.arrowShape = when (arrowShape) {
            0 -> ArrowShape.TRIANGLE_RIGHT
            1 -> ArrowShape.TRIANGLE_ISOSCELES
            else -> ArrowShape.CURVED
        }
        modifier.arrowOffsetY =
            typedArray.getDimension(R.styleable.BubbleLayout_arrowOffsetY, modifier.arrowOffsetY)
        modifier.arrowOffsetX =
            typedArray.getDimension(R.styleable.BubbleLayout_arrowOffsetY, modifier.arrowOffsetX)
        modifier.withArrow =
            typedArray.getBoolean(R.styleable.BubbleLayout_withArrow, modifier.withArrow)

        // Elevation/Shadow
        val shadowStyle =
            typedArray.getInt(R.styleable.BubbleLayout_shadowStyle, modifier.shadowStyle.ordinal)

        modifier.shadowStyle = if (shadowStyle == ShadowStyle.ELEVATION.ordinal) {
            ShadowStyle.ELEVATION
        } else ShadowStyle.SHADOW


        if (modifier.shadowStyle == ShadowStyle.SHADOW) {
            modifier.shadowColor =
                typedArray.getColor(
                    R.styleable.BubbleLayout_android_shadowColor,
                    modifier.shadowColor
                )
            modifier.shadowRadius = typedArray.getFloat(
                R.styleable.BubbleLayout_android_shadowRadius,
                modifier.shadowRadius
            )
            modifier.shadowRadius = typedArray.getFloat(
                R.styleable.BubbleLayout_android_shadowDx,
                modifier.shadowOffsetX
            )
            modifier.shadowRadius = typedArray.getFloat(
                R.styleable.BubbleLayout_android_shadowDy,
                modifier.shadowOffsetY
            )
        }

        typedArray.recycle()

        init(false)
    }


    private fun init(updateModifier: Boolean = true) {

        background = null

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
        layoutChildren(modifier)
    }

    private fun layoutChildren(modifier: BubbleModifier) {

        val alignment = modifier.arrowAlignment

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

        if (isDebug) {
            paintDebug.color = Color.RED
            canvas.drawRect(rectBubble, paintDebug)
            paintDebug.color = Color.BLUE
            canvas.drawRect(rectContent, paintDebug)
        }

        if (modifier.shadowStyle == ShadowStyle.ELEVATION) {
            outlineProvider = outlineProvider
        }
    }

    fun update(modifier: BubbleModifier) {
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
    modifier: BubbleModifier,
    contentRect: RectF
) {

    path.reset()


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

    getRoundedRectPath(modifier, path, contentRect)
}

private fun getRoundedRectPath(
    modifier: BubbleModifier,
    path: Path,
    contentRect: RectF
) {

    val alignment = modifier.arrowAlignment

    val cornerRadius = modifier.cornerRadiusBundle

    val maxRadius = contentRect.height() / 2f

    cornerRadius.apply {
        topLeft = min(topLeft, maxRadius)
        topRight = min(topRight, maxRadius)
        bottomRight = min(bottomRight, maxRadius)
        bottomLeft = min(bottomLeft, maxRadius)
    }

    val isWithArrow = modifier.withArrow


    if (isWithArrow) {
        when (alignment) {
            // Arrow on left side of the bubble
            LEFT_TOP, LEFT_CENTER, LEFT_BOTTOM -> {
                cornerRadius.topLeft = min(
                    modifier.arrowTop,
                    cornerRadius.topLeft
                )

                cornerRadius.bottomLeft =
                    min(cornerRadius.bottomLeft, (contentRect.height() - modifier.arrowBottom))
            }

            // Arrow on right side of the bubble
            RIGHT_TOP, RIGHT_CENTER, RIGHT_BOTTOM -> {
                cornerRadius.topRight = min(
                    modifier.arrowTop,
                    cornerRadius.topRight
                )

                cornerRadius.bottomRight =
                    min(cornerRadius.bottomRight, (contentRect.height() - modifier.arrowBottom))
            }

            // Arrow at the bottom of bubble
            BOTTOM_LEFT -> {
                cornerRadius.bottomLeft =
                    if (modifier.arrowOffsetY < maxRadius) 0f
                    else cornerRadius.bottomLeft
            }
            BOTTOM_RIGHT -> {
                cornerRadius.bottomRight =
                    if (modifier.arrowOffsetY < maxRadius) 0f
                    else cornerRadius.bottomRight
            }
            else -> Unit
        }
    }

    val radii = floatArrayOf(
        cornerRadius.topLeft,
        cornerRadius.topLeft,
        cornerRadius.topRight,
        cornerRadius.topRight,
        cornerRadius.bottomRight,
        cornerRadius.bottomRight,
        cornerRadius.bottomLeft,
        cornerRadius.bottomLeft
    )

    path.addRoundRect(contentRect, radii, Path.Direction.CW)
}
