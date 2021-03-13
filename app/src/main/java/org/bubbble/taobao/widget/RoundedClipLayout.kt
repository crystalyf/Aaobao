package org.bubbble.taobao.widget

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.cardview.widget.CardView
import androidx.core.view.marginLeft
import org.bubbble.taobao.util.dp
import org.bubbble.taobao.util.logger

/**
 * @author Andrew
 * @date 2020/11/16 18:33
 */
class RoundedClipLayout(context: Context, attrs: AttributeSet?,
                        @AttrRes defStyleAttr: Int) : CardView(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context): this(context, null)

    companion object {
        private val PADDING = (-1).dp
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        // 让子View自己计算自己的尺寸，传给子View的两个尺寸限制参数

        val selfWidthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val selfWidthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val selfHeightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val selfHeightSpecSize = MeasureSpec.getSize(heightMeasureSpec)

        var childWidthSpec = widthMeasureSpec
        var childHeightSpec = heightMeasureSpec
        for(value in 0 until childCount) {
            val childView = getChildAt(value)
            val layoutParams = childView.layoutParams

            when (layoutParams.width) {
                ViewGroup.LayoutParams.MATCH_PARENT -> {
                    childWidthSpec = if (selfWidthSpecMode == MeasureSpec.EXACTLY || selfWidthSpecMode == MeasureSpec.AT_MOST) {
                        MeasureSpec.makeMeasureSpec(selfWidthSpecSize, MeasureSpec.EXACTLY)
                    } else {
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                    }

                    childHeightSpec = if (selfHeightSpecMode == MeasureSpec.EXACTLY || selfHeightSpecMode == MeasureSpec.AT_MOST) {
                        MeasureSpec.makeMeasureSpec(selfHeightSpecSize, MeasureSpec.AT_MOST)
                    } else {
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                    }
                }
                ViewGroup.LayoutParams.WRAP_CONTENT -> {
                    childWidthSpec = if (selfWidthSpecMode == MeasureSpec.EXACTLY || selfWidthSpecMode == MeasureSpec.AT_MOST) {
                        MeasureSpec.makeMeasureSpec(selfWidthSpecSize, MeasureSpec.EXACTLY)
                    } else {
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                    }

                    childHeightSpec = if (selfHeightSpecMode == MeasureSpec.EXACTLY || selfHeightSpecMode == MeasureSpec.AT_MOST) {
                        MeasureSpec.makeMeasureSpec(selfHeightSpecSize, MeasureSpec.AT_MOST)
                    } else {
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                    }
                }
                // 固定尺寸
                else -> {
                    childWidthSpec = MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.EXACTLY)
                    childHeightSpec = MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY)
                }
            }

            // 给出的测量限制是本身宽度，高度是UNSPECIFIED不限制
            childView.measure(childWidthSpec, childHeightSpec)
        }

        // 自己的宽高 = 子layout的宽高减去padding
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(getChildAt(0).measuredWidth + (PADDING * 2), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(getChildAt(0).measuredHeight + (PADDING * 2), MeasureSpec.EXACTLY))
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        for (value in 0 until childCount) {
            val childView = getChildAt(value)
            childView.layout(PADDING, PADDING, childView.measuredWidth + kotlin.math.abs(PADDING), childView.measuredHeight + kotlin.math.abs(PADDING))
        }
    }
}