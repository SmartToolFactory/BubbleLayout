package com.smarttoolfactory.bubblelayout

import android.graphics.Path
import android.graphics.RectF
import kotlin.math.min


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