package org.bubbble.taobao.base.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.OverScroller
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.math.MathUtils
import androidx.core.view.ViewCompat
import org.bubbble.opendesign.controller.behavior.ViewOffsetBehavior
import org.bubbble.taobao.util.logger

/**
 * @author Andrew
 * @date 2020/09/06 18:28
 * 类AppBarLayout的Behavior实现
 * 总之可以认为 可以设置一个View， 然后可以向一个列表滑动它
 */
abstract class HeaderBehavior<V : View>(context: Context, attrs: AttributeSet) : ViewOffsetBehavior<V>(
    context,
    attrs
) {

    companion object {
        // 无效的指针
        const val INVALID_POINTER = -1
    }

    // OverScroller类是为了实现View平滑滚动的一个Helper类
    var scroller: OverScroller? = null
    // 触摸到屏幕后手指可以移动的距离
    private var touchSlop = -1
    // 是否正在拖动
    private var isBeingDragged = false
    private var lastMotionY = 0
    // 这应该是第一个触摸点
    private var activePointerId = -1

    // 甩出去的惯性
    private var flingRunnable: Runnable? = null

    // VelocityTracker主要用跟踪触摸屏事件
    private var velocityTracker: VelocityTracker? = null

    /**
     * @param parent 当前接收此触摸事件的父视图
     * @param child 与此行为相关联的子视图
     * @return true 拦截并接管该事件
     */
    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: V, ev: MotionEvent): Boolean {
        if (touchSlop < 0) {
            // ViewConfiguration这个类主要定义了UI中所使用到的标准常量，像超时、尺寸、距离
            touchSlop = ViewConfiguration.get(parent.context).scaledTouchSlop
        }
        logger("touchSlop $touchSlop")

        val action = ev.action

        // 如果正在移动并正在拖拽，则拦截
        if (action == MotionEvent.ACTION_MOVE && isBeingDragged) {
            return true
        } else {
            var activePointerId = 0
            var pointerIndex = 0

            when (ev.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    isBeingDragged = false
                    activePointerId = ev.x.toInt()
                    if (canDragView(child) && parent.isPointInChildBounds(child, activePointerId, pointerIndex)) {
                        lastMotionY = pointerIndex
                        this.activePointerId = ev.getPointerId(0)
                        ensureVelocityTracker()
                    }

                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isBeingDragged = false
                    this.activePointerId = -1

                    if (velocityTracker != null) {
                        velocityTracker!!.recycle()
                        velocityTracker = null
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    activePointerId = this.activePointerId
                    if (activePointerId != -1) {
                        pointerIndex = ev.findPointerIndex(activePointerId)
                        if (pointerIndex != 1) {
                            val y = ev.getY(pointerIndex).toInt()
                            val yDiff = Math.abs(y - lastMotionY)
                            if (yDiff > touchSlop) {
                                isBeingDragged = true
                                lastMotionY = y
                            }
                        }
                    }
                }
            }
        }

        if (velocityTracker != null) {
            // addMovement(MotionEvent)函数将Motion event加入到VelocityTracker类实例中.
            // 你可以使用getXVelocity() 或getXVelocity()获得横向和竖向的速率到速率时
            velocityTracker!!.addMovement(ev)
        }

        return isBeingDragged
    }

    /**
     * @param parent 当前接收此触摸事件的父视图
     * @param child 与此行为相关联的子视图
     * @return true 拦截并接管该事件
     */
    override fun onTouchEvent(parent: CoordinatorLayout, child: V, ev: MotionEvent): Boolean {
        if (touchSlop < 0) {
            touchSlop = ViewConfiguration.get(parent.context).scaledTouchSlop
        }

        var activePointerIndex = 0
        var y = 0
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                activePointerIndex = ev.x.toInt()
                y = ev.y.toInt()

                if (!parent.isPointInChildBounds(child, activePointerIndex, y) || !canDragView(child)) {
                    return false
                }

                lastMotionY = y
                activePointerId = ev.getPointerId(0)
                ensureVelocityTracker()
            }

            MotionEvent.ACTION_UP -> {
                if (velocityTracker != null) {
                    velocityTracker!!.addMovement(ev)
                    velocityTracker!!.computeCurrentVelocity(1000)
                    val yVT = velocityTracker!!.getYVelocity(activePointerId)
                    fling(parent, child, -getScrollRangeForDragFling(child), 0, yVT)
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                isBeingDragged = false
                activePointerId = -1
                if (velocityTracker != null) {
                    velocityTracker!!.recycle()
                    velocityTracker = null
                }
            }

            MotionEvent.ACTION_MOVE -> {
                activePointerIndex = ev.findPointerIndex(activePointerId)
                if (activePointerIndex == -1) {
                    return false
                }

                y = ev.getY(activePointerIndex).toInt()
                var dy = lastMotionY - y
                if (!this.isBeingDragged && Math.abs(dy) > touchSlop) {
                    isBeingDragged = true
                    if (dy > 0) {
                        dy -= touchSlop
                    } else {
                        dy += touchSlop
                    }
                }

                if (isBeingDragged) {
                    lastMotionY = y
                    scroll(parent, child, dy, getMaxDragOffset(child), 0)
                }
            }
        }

        if (velocityTracker != null) {
            velocityTracker!!.addMovement(ev)
        }

        return true
    }

    private fun getTopBottomOffsetForScrollingSibling() = getTopAndBottomOffset()

    private fun scroll(parent: CoordinatorLayout, child: V, dy: Int, minOffset: Int, maxOffset: Int)
            = setHeaderTopBottomOffset(parent, child, getTopBottomOffsetForScrollingSibling() - dy, minOffset, maxOffset)

    fun fling(parent: CoordinatorLayout, child: V, minOffset: Int, maxOffset: Int, velocityY: Float): Boolean {
        if (flingRunnable != null) {
            child.removeCallbacks(flingRunnable)
            flingRunnable = null
        }

        if (scroller == null) {
            scroller = OverScroller(child.context)
        }

        scroller?.fling(0, getTopAndBottomOffset(), 0, Math.round(velocityY), 0, 0, minOffset, maxOffset)

        return if (scroller != null && scroller!!.computeScrollOffset()) {
            flingRunnable = FlingRunnable(parent, child)
            ViewCompat.postOnAnimation(child, flingRunnable)
            true
        } else {
            false
        }
    }

    fun ensureVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker = VelocityTracker.obtain()
        }
    }

    /**
     * 可以拖动View
     */
    open fun canDragView(child: V) = false

    fun setHeaderTopBottomOffset(parent: CoordinatorLayout, header: V, newOffset: Int)
            = setHeaderTopBottomOffset(parent, header, newOffset, -2147483648, 2147483647)

    private fun setHeaderTopBottomOffset(parent: CoordinatorLayout, header: V, newOffset: Int, minOffset: Int, maxOffset: Int): Int {
        val curOffset = getTopAndBottomOffset()
        var consumed = 0
        var newOffsetInside = 0
        if (minOffset != 0 && curOffset >= minOffset && curOffset <= maxOffset) {
            newOffsetInside = MathUtils.clamp(newOffset, minOffset, maxOffset)
            if (curOffset != newOffsetInside) {

                // 设置垂直偏移
                setTopAndBottomOffset(newOffsetInside)
                consumed = curOffset - newOffsetInside
            }
        }

        return consumed
    }

    fun getScrollRangeForDragFling(view: V) = view.height

    fun getMaxDragOffset(view: V) = -view.height

    inner class FlingRunnable(private val parent: CoordinatorLayout, private val child: V) : Runnable {

        override fun run() {
            if (scroller != null) {
                setHeaderTopBottomOffset(parent, child, scroller!!.currY)
                ViewCompat.postOnAnimation(child, this)
            }
        }
    }
}