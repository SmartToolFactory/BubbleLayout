package com.smarttoolfactory.bubblelayout

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


class CurveDrawView : View {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = 6f
        color = Color.RED
    }


    private var path = Path()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

//        path.moveTo(0f,0f)
//        path.quadTo(50f,120f, 190f, 190f)
//        path.quadTo(190f,190f, 185f, 200f)
//        path.lineTo(0f, 200f)
//        canvas.drawPath(path, paint)

        val x1 = 30f
        val y1 = 30f

        val xfinal = 200f
        val yfinal = 200f

        val x3 = (xfinal - x1)
        val y3 = (yfinal - y1) * .9f

        val x2 = (x1 + x3) / 4
        val y2 = (y1 + y3) * 3 / 4


        path.moveTo(x1, y1)

        path.cubicTo(x1, y1, x2, y2, x3, y3)
//        path.quadTo(x3,y3,x3 - 20,yfinal)
        path.cubicTo(x3, y3, xfinal, yfinal, x3, yfinal)
        path.lineTo(x1, yfinal)
        path.close()
        canvas.drawPath(path, paint)

        paint.color = Color.YELLOW
        canvas.drawLine(x1, y1, x2, y2, paint)
        paint.color = Color.GREEN
        canvas.drawLine(x2, y2, x3, y3, paint)
        paint.color = Color.BLUE
        canvas.drawLine(x3, y3, xfinal, yfinal, paint)


//        path.moveTo(50f, 50f)
//        path.cubicTo(300f, 50f, 100f, 400f, 400f, 400f)
//        canvas.drawPath(path, paint)


        canvas.drawCircle(500f, 500f, 50f, paint)

        val rect = RectF()

        rect.set(width.toFloat() - 50f, 300f, width.toFloat(), 330f)

        path.moveTo(width.toFloat(), 300f)
        path.lineTo(width.toFloat()- 50f, 300f)
        path.addArc(rect, 270f, -180f)
        path.lineTo(width.toFloat(), 330f)

        paint.style = Paint.Style.STROKE
        paint.color = Color.RED
        canvas.drawRect(rect, paint)
        paint.color = Color.YELLOW
        canvas.drawPath(path, paint)

    }


    private fun drawLeftBottomArc(canvas: Canvas) {
        val x1 = 0f
        val y1 = 0f

        val xfinal = 200f
        val yfinal = 200f

        val x3 = xfinal * .9f
        val y3 = yfinal * .9f

        val x2 = (x1 + x3) / 4
        val y2 = (y1 + y3) * 3 / 4


        val r = yfinal - y3

        path.moveTo(x1, y1)
        path.cubicTo(x1, y1, x2, y2, x3, y3)
        path.cubicTo(x3, y3, xfinal, yfinal, x3 * .95f, yfinal)
        path.lineTo(x1, yfinal)
        path.close()
        canvas.drawPath(path, paint)
    }

}