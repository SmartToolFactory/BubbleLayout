package com.smarttoolfactory.bubblelayout

import android.util.Log
import android.view.View

internal fun logMeasureSpecs(text: String, widthMeasureSpec: Int, heightMeasureSpec: Int) {

    val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
    val width = View.MeasureSpec.getSize(widthMeasureSpec)
    val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
    val height = View.MeasureSpec.getSize(heightMeasureSpec)

    val measureSpecHeight: String = when (heightMode) {
        View.MeasureSpec.EXACTLY -> {
            "EXACTLY"
        }
        View.MeasureSpec.AT_MOST -> {
            "AT_MOST"
        }
        else -> {
            "UNSPECIFIED"
        }
    }
    val measureSpecWidth: String = when (widthMode) {
        View.MeasureSpec.EXACTLY -> {
            "EXACTLY"
        }
        View.MeasureSpec.AT_MOST -> {
            "AT_MOST"
        }
        else -> {
            "UNSPECIFIED"
        }
    }
    val TAG = "LogSpecs"

    Log.d(
        TAG, "TEXT: $text,  Width: " + measureSpecWidth + ", " + width + " Height: "
                + measureSpecHeight + ", " + height
    )

    println(
        "TEXT: $text,  Width: " + measureSpecWidth + ", " + width + " Height: "
                + measureSpecHeight + ", " + height
    )
}