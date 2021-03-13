package org.bubbble.taobao.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View.MeasureSpec.*
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import kotlin.math.ceil

/**
 * @author Andrew
 * @date 2020/11/16 10:28
 */
class DemoLayout constructor(context: Context, attrs: AttributeSet?,
                             @AttrRes defStyleAttr: Int, @StyleRes defStyleRes: Int) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr:Int):this(context,attrs,defStyleAttr,0)

    constructor(context: Context, attrs: AttributeSet?):this(context, attrs, 0)

    constructor(context: Context):this(context, null)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // 让子View自己计算自己的尺寸，传给子View的两个尺寸限制参数

        val selfWidthSpecMode = getMode(widthMeasureSpec)
        val selfWidthSpecSize = getSize(widthMeasureSpec)

        var childWidthSpec = widthMeasureSpec
        for(value in 0 until childCount) {
            val childView = getChildAt(value)
            val layoutParams = childView.layoutParams

            when (layoutParams.width) {
                MATCH_PARENT -> {
                    childWidthSpec = if (selfWidthSpecMode == EXACTLY || selfWidthSpecMode == AT_MOST) {
                        makeMeasureSpec(selfWidthSpecSize, EXACTLY)
                    } else {
                        makeMeasureSpec(0, UNSPECIFIED)
                    }
                }
                WRAP_CONTENT -> {
                    childWidthSpec = if (selfWidthSpecMode == EXACTLY || selfWidthSpecMode == AT_MOST) {
                        makeMeasureSpec(selfWidthSpecSize, AT_MOST)
                    } else {
                        makeMeasureSpec(0, UNSPECIFIED)
                    }
                }
                // 固定尺寸
                else -> childWidthSpec = makeMeasureSpec(layoutParams.width, EXACTLY)
            }

            childView.measure(childWidthSpec, childWidthSpec)
        }

        setMeasuredDimension(widthMeasureSpec, makeMeasureSpec(selfWidthSpecSize, EXACTLY))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (value in 0 until childCount) {
            val childView = getChildAt(value)
//          childView.layout()
        }
    }
}