package org.bubbble.taobao.widget.draw

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.ReplacementSpan
import org.bubbble.taobao.util.dp
import kotlin.math.roundToInt

/**
 * @author Andrew
 * @date 2020/11/09 19:45
 */
class RoundBackgroundSpan(private val backgroundColor: Int,private val textColor: Int) : ReplacementSpan() {

    private val cornerRadius = 4F.dp

    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int
        = paint.measureText(text, start, end).roundToInt()

    override fun draw(canvas: Canvas, text: CharSequence?, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        val fm = paint.fontMetrics
        val rect = RectF(x, top.toFloat(), x + measureText(paint, text.toString(), start, end), fm.bottom - fm.top)
        paint.color = backgroundColor
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
        paint.color = textColor
        canvas.drawText(text.toString(), start, end, x, y.toFloat(), paint)
    }

    private fun measureText(paint: Paint, text: CharSequence, start: Int, end: Int): Float {
        return paint.measureText(text, start, end)
    }
}