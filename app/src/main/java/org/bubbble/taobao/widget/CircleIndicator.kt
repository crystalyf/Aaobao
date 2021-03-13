package org.bubbble.taobao.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import org.bubbble.taobao.R
import org.bubbble.taobao.util.dp
import org.bubbble.taobao.util.half
import org.bubbble.taobao.util.logger

/**
 * @author Andrew
 * @date 2020/11/07 15:03
 */
class CircleIndicator(context: Context, attrs: AttributeSet?,
                      @AttrRes defStyleAttr: Int, @StyleRes defStyleRes: Int) : View(context, attrs, defStyleAttr, defStyleRes) {
    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr:Int):this(context,attrs,defStyleAttr,0)

    constructor(context: Context, attrs: AttributeSet?):this(context, attrs, 0)

    constructor(context: Context):this(context, null)

    private var currentPoint = 0

    private var itemCount = 0

    companion object {
        val POINT_RADIUS = 6F.dp
        val POINT_PADDING = 6.dp
    }

    private val paint = Paint().apply {
        // 抗锯齿
        isAntiAlias = true
        // 填充
        style = Paint.Style.FILL
        // 颜色
        color = ContextCompat.getColor(context, R.color.white)
    }

    fun setViewPager2(viewPager2: ViewPager2) {
        currentPoint = viewPager2.currentItem
        itemCount = (viewPager2.adapter?.itemCount ?: 2) - 2
        logger("$itemCount")
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                logger("position：$position")
                currentPoint = when (position) {
                    0 -> {
                        1
                    }
                    itemCount + 1 -> {
                        1
                    }
                    else -> {
                        position
                    }
                }
                invalidate()
            }
        })
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            var startX = if (itemCount % 2 != 0) {
                measuredWidth.half - (itemCount.half * (POINT_RADIUS * 2 + POINT_PADDING))
            } else {
                measuredWidth.half - (itemCount.half * (POINT_RADIUS * 2 + POINT_PADDING) - POINT_PADDING.half - POINT_RADIUS)
            }

            for (value in 1 .. itemCount) {
                if (value == currentPoint) {
                    paint.color = ContextCompat.getColor(context, R.color.color_primary)
                    canvas.drawCircle(startX, measuredHeight.toFloat().half, POINT_RADIUS, paint)
                    paint.color = ContextCompat.getColor(context, R.color.white)
                } else {
                    canvas.drawCircle(startX, measuredHeight.toFloat().half, POINT_RADIUS, paint)
                }
                startX += POINT_RADIUS * 2 + POINT_PADDING
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val measuredWidth = resolveSize((POINT_RADIUS.toInt() * 2 + POINT_PADDING) + paddingStart + paddingLeft + paddingEnd + paddingEnd * itemCount, widthMeasureSpec)
        val measuredHeight = resolveSize(POINT_RADIUS.toInt() * 2 + paddingTop + paddingEnd, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredHeight)
    }
}