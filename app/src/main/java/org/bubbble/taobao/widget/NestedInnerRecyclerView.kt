package org.bubbble.taobao.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import org.bubbble.taobao.util.logger
import kotlin.math.abs

/**
 * @author Andrew
 * @date 2020/09/07 14:50
 * 这里重写就是让子RV拿到事件，不让父RV拦截事件
 */
class NestedInnerRecyclerView : RecyclerView {

    private var downX : Float = 0f
    private var downY : Float = 0f

    constructor(context: Context?) : super(context!!)

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle)

    private var isNested = false

    /**
     * 内部RV拿到事件后判断是否横向移动，如果是横向移动则放弃拦截事件。
     */
    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        val x = e.x
        val y = e.y
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = x
                downY = y
            }
            MotionEvent.ACTION_MOVE -> {

                val dx: Float? = x.minus(downX)
                val dy: Float? = y.minus(downY)
                //通过距离差判断方向
                when (getOrientation(dx ?: 0f, dy ?: 0f)) {
                    "r", "l" -> {

                        dx?.let {
                            return false
                        }
                    }

                    // 上下滑动灵敏度降低一些
                    "b", "t" -> {
                        logger("abs(dy) : ${abs(dy ?: 0F)}")
                        dy?.let {
                            return if (abs(dy) > 100){
                                super.onInterceptTouchEvent(e)
                            }else{
                                false
                            }
                        }
                    }
                    else -> {
                        return super.onInterceptTouchEvent(e)
                    }
                }
            }
        }
        return super.onInterceptTouchEvent(e)
    }

    private fun getOrientation(dx: Float = 0f, dy: Float = 0f): String {
        return if (abs(dx) > abs(dy)) {
            //X轴移动
            if (dx > 0) "r" else "l"//右,左
        } else {
            //Y轴移动
            if (dy > 0) "b" else "t"//下//上
        }
    }

    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        isNested = if (canScrollVertically(-1)) {
            logger("CoordinatorLayout 认为外部RV没到顶部")
            false
        } else {
            true
        }
        return super.startNestedScroll(axes, type)
    }

    fun getIsNestedScroll() = isNested
}