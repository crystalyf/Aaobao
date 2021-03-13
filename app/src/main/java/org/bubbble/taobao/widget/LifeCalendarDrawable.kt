package org.bubbble.taobao.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import org.bubbble.taobao.R
import org.bubbble.taobao.util.dp
import org.bubbble.taobao.util.half
import kotlin.math.ceil

/**
 * @author Andrew
 * @date 2020/10/27 10:38
 */
class LifeCalendarDrawable(

    private val context: Context, private val dataList: MutableList<Int>
) : Drawable() {

    private var row = WRAP_COUNT

    private var column = WRAP_COUNT

    private var orientation = VERTICAL

    private var blockSize = 8.dp

    private val margin = 2F.dp

    private val round = 2F.dp

    private lateinit var viewRect: Rect

    private val blockRectF = RectF()

    private val paint = Paint().apply {
        // 抗锯齿
        isAntiAlias = true
        // 填充
        style = Paint.Style.FILL
        // 颜色
        color = ContextCompat.getColor(context, R.color.color_accent)

        alpha = 255
    }

    private var drawableHeight = 0
    private var drawableWidth = 0

    companion object {
        const val WRAP_COUNT = -1
        const val HORIZONTAL = 0
        const val VERTICAL = 1
    }

    fun draw(rect: Rect, canvas: Canvas) {
        viewRect = rect

        if (orientation == VERTICAL) {
            column = (viewRect.width() - margin.toInt()) / (blockSize + margin.toInt())
            drawableHeight = (ceil((dataList.size.toDouble() / column.toDouble())) * (blockSize + margin.toInt())).toInt()
        } else {
            row = (viewRect.height() - margin.toInt()) / (blockSize + margin.toInt())
            drawableWidth = (margin + ceil(dataList.size.toDouble() / row.toDouble()) * (blockSize + margin.toInt())).toInt()
        }

        draw(canvas)
    }

    override fun draw(canvas: Canvas) {
        if (orientation == VERTICAL) {

            canvas.save()
            val extra = (viewRect.width() - margin.toInt() - (column * (blockSize + margin.toInt()))).half
            var i = 0
            var a: Int
            while (i < dataList.size) {
                a = 0
                canvas.save()
                canvas.translate(margin + extra, 0F)
                while (i < dataList.size && a < column) {
                    paint.color = dataList[i]
                    blockRectF.set(0F, 0F, blockSize.toFloat(), blockSize.toFloat())
                    canvas.drawRoundRect(blockRectF, round, round, paint)
                    i++
                    a++
                    canvas.translate(blockSize + margin, 0F)
                }
                canvas.restore()
                canvas.translate(0F, blockSize + margin)
            }
            canvas.restore()
        } else {

            canvas.save()
            val extra = (viewRect.height() - margin.toInt() - (row * (blockSize + margin.toInt()))).half
            var i = 0
            var a: Int
            while (i < dataList.size) {
                a = 0
                canvas.save()
                canvas.translate(margin + extra, 0F)
                while (i < dataList.size && a < row) {
                    paint.color = dataList[i]
                    blockRectF.set(0F, 0F, blockSize.toFloat(), blockSize.toFloat())
                    canvas.drawRoundRect(blockRectF, round, round, paint)
                    i++
                    a++
                    canvas.translate(0F, blockSize + margin)
                }
                canvas.restore()
                canvas.translate(blockSize + margin, 0F)
            }
            canvas.restore()
        }

    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        invalidateSelf()
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    fun getDrawableHeight() = drawableHeight
    fun getDrawableWidth() = drawableWidth


    fun getOrientation() = orientation
}