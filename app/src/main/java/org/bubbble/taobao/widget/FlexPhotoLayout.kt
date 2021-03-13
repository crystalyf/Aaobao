package org.bubbble.taobao.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.*
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import org.bubbble.taobao.util.dp
import org.bubbble.taobao.util.logger
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

/**
 * @author Andrew
 * @date 2020/11/16 10:28
 */
class FlexPhotoLayout constructor(context: Context, attrs: AttributeSet?,
                                  @AttrRes defStyleAttr: Int, @StyleRes defStyleRes: Int) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr:Int):this(context,attrs,defStyleAttr,0)

    constructor(context: Context, attrs: AttributeSet?):this(context, attrs, 0)

    constructor(context: Context):this(context, null)

    private val childList = mutableListOf<View>()

    companion object {
        private const val MAX_PIXEL = 3
        private val PADDING = 1.dp
    }

    // 列数
    private var column = MAX_PIXEL

    private var childWidthSpec = 0

    fun addPhotoView(view: View) {
        childList.add(view)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 让子View自己计算自己的尺寸，传给子View的两个尺寸限制参数
        val selfWidthSpecMode = getMode(widthMeasureSpec)
        val selfWidthSpecSize = getSize(widthMeasureSpec)

        // 先计算每张的宽度
        val pixels = sqrt(childCount.toDouble())
        val photoWidthSpecSize = if (childCount <= MAX_PIXEL ) {
            // 如果子View不超过一行，直接就算一行
            column = childCount
            selfWidthSpecSize / childCount
        } else if (pixels%1.0 != 0.0 || pixels >= MAX_PIXEL) {
            // 如果计算结果大于等于默认值，并且不是方形，不提前换行
            column = MAX_PIXEL
            selfWidthSpecSize / MAX_PIXEL
        } else {
            // 第Pixels张后立刻换行
            column = pixels.toInt()
            selfWidthSpecSize / column
        }

        if (childCount == 1) {
            val childView = getChildAt(0)
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
                        makeMeasureSpec(selfWidthSpecSize, EXACTLY)
                    } else {
                        makeMeasureSpec(0, UNSPECIFIED)
                    }
                }
                // 固定尺寸
                else -> childWidthSpec = makeMeasureSpec(layoutParams.width, EXACTLY)
            }
//            childView.measure(childWidthSpec, childWidthSpec)
            childView.measure(childWidthSpec + (PADDING * 2), makeMeasureSpec(0, UNSPECIFIED))
            logger("childViewMeasureHeight: ${childView.measuredHeight}  childViewMeasureWidth: ${childView.measuredWidth}  selfWidthSpecSize $selfWidthSpecSize")
            setMeasuredDimension(makeMeasureSpec(selfWidthSpecSize - (PADDING * 2), EXACTLY), makeMeasureSpec(childView.measuredHeight, EXACTLY))

//            setMeasuredDimension(widthMeasureSpec, makeMeasureSpec(photoWidthSpecSize, EXACTLY))
        } else {
            for(value in 0 until childCount) {
                val childView = getChildAt(value)
                val layoutParams = childView.layoutParams

                when (layoutParams.width) {
                    MATCH_PARENT -> {
                        childWidthSpec = if (selfWidthSpecMode == EXACTLY || selfWidthSpecMode == AT_MOST) {
                            makeMeasureSpec(photoWidthSpecSize, EXACTLY)
                        } else {
                            makeMeasureSpec(0, UNSPECIFIED)
                        }
                    }
                    WRAP_CONTENT -> {
                        childWidthSpec = if (selfWidthSpecMode == EXACTLY || selfWidthSpecMode == AT_MOST) {
                            makeMeasureSpec(photoWidthSpecSize, EXACTLY)
                        } else {
                            makeMeasureSpec(0, UNSPECIFIED)
                        }
                    }
                    // 固定尺寸
                    else -> childWidthSpec = makeMeasureSpec(layoutParams.width, EXACTLY)
                }
                childView.measure(childWidthSpec, childWidthSpec)
            }
            setMeasuredDimension(widthMeasureSpec, makeMeasureSpec(ceil(childCount.toFloat() / column.toFloat()).toInt() * photoWidthSpecSize, EXACTLY))
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (value in 0 until childCount) {
            val childView = getChildAt(value)
            // 3的child会换行，3-5的child、6-8的child 的top会加一个childWidthSpec，
            // 行
            val rowCount = floor(value.toFloat() / column.toFloat()).toInt()
            // 列
            val columnCount = value % column

            childView.layout(columnCount * childView.measuredWidth + PADDING, rowCount * childView.measuredHeight + PADDING, columnCount * childView.measuredWidth + childView.measuredWidth - PADDING, rowCount * childView.measuredHeight + childView.measuredHeight - PADDING)
        }
    }
}