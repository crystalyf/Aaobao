package org.bubbble.taobao.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.OverScroller
import androidx.core.view.*
import androidx.recyclerview.widget.RecyclerView
import org.bubbble.taobao.util.logger
import org.bubbble.taobao.widget.LifeCalendarDrawable.Companion.VERTICAL
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * @author Andrew
 * @date 2020/10/27 10:06
 */
class LifeCalendar(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
    View(context, attrs, defStyleAttr, defStyleRes), NestedScrollingChild2 {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private val rect = Rect()

    private var mLastY = 0f

    private var mLastX = 0f

    private var mScroller: OverScroller = OverScroller(context)

    private var mVelocityTracker: VelocityTracker? = null

    private var mTarget: View? = null

    private var mScrollType = ViewCompat.TYPE_TOUCH

    private var mMaximumVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity
    private var mMinimumVelocity = ViewConfiguration.get(context).scaledMinimumFlingVelocity

    private val mChildHelper = NestedScrollingChildHelper(this)

    init {
        isNestedScrollingEnabled = true
    }
    /**
     * 负责绘制及动画的Drawable
     */
    private val lifeCalendarDrawable = LifeCalendarDrawable(context, mutableListOf<Int>().apply {

        for (i in 0..4234) {
            add(Color.CYAN)
        }

        add(Color.YELLOW)

        for (i in 0..1834) {
            add(Color.LTGRAY)
        }
    })

    init {
        lifeCalendarDrawable.callback = this
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?:return
        lifeCalendarDrawable.draw(rect, canvas)
    }

    override fun invalidateDrawable(drawable: Drawable) {
        super.invalidateDrawable(drawable)
        if (drawable == lifeCalendarDrawable) {
            invalidate()
            invalidateOutline()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        rect.set(0, 0, measuredWidth, measuredHeight)
    }


    private fun initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
    }

    private fun recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker!!.recycle()
            mVelocityTracker = null
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (lifeCalendarDrawable.getDrawableHeight() <= rect.height() && lifeCalendarDrawable.getOrientation() == VERTICAL) {
            return false
        }

        initVelocityTrackerIfNotExists()
        mVelocityTracker!!.addMovement(event)
        val action = event.action
        val y = event.y
        val x = event.x

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mLastY = y
                mLastX = x
            }

            MotionEvent.ACTION_MOVE -> {
                if (lifeCalendarDrawable.getOrientation() == VERTICAL) {
                    val dy = y - mLastY
//                    scrollBy(0, (-dy).toInt())

                    onScrollBehavior((-dy).toInt())
                    mLastY = y
                } else {
                    val dx = x - mLastX
                    scrollBy((-dx).toInt(), 0)
                    mLastX = x
                }
            }

            MotionEvent.ACTION_CANCEL -> {

                recycleVelocityTracker()
            }

            MotionEvent.ACTION_UP -> {

                if (lifeCalendarDrawable.getOrientation() == VERTICAL) {
                    mVelocityTracker!!.computeCurrentVelocity(1000, mMaximumVelocity.toFloat())
                    val velocityY = mVelocityTracker!!.yVelocity.toInt()
                    if (abs(velocityY) > mMinimumVelocity) {
                        fling(-velocityY)
                    }
                    recycleVelocityTracker()
                } else {
                    mVelocityTracker!!.computeCurrentVelocity(1000, mMaximumVelocity.toFloat())
                    val velocityX = mVelocityTracker!!.xVelocity.toInt()
                    if (abs(velocityX) > mMinimumVelocity) {
                        fling(-velocityX)
                    }
                    recycleVelocityTracker()
                }
            }
        }

        return true
    }

    fun fling(velocity: Int) {

        if (lifeCalendarDrawable.getOrientation() == VERTICAL) {
            // 第三步-二小步，fling调整最大Y偏移，以便把fling传递给nestedScrollingChild。
            mScroller.fling(0, scrollY, 0, velocity, 0, 0, 0, Int.MAX_VALUE)
            invalidate()
        } else {
            mScroller.fling(scrollX, 0, velocity, 0, 0, Int.MAX_VALUE, 0, 0)
            invalidate()
        }
    }

    private fun clearSelfFling() {
        if (!mScroller.isFinished) {
            mScroller.abortAnimation()
        }
    }

    private fun clearAllFling() {
        if (!mScroller.isFinished) {
            mScroller.abortAnimation()
        }
        if (mTarget != null) {
            ViewCompat.stopNestedScroll(mTarget!!, mScrollType)
        }
    }

    override fun scrollTo(x: Int, y: Int) {
        if (lifeCalendarDrawable.getOrientation() == VERTICAL) {
            var dy = y
            val limit = max(lifeCalendarDrawable.getDrawableHeight(), rect.bottom) - min(lifeCalendarDrawable.getDrawableHeight(), rect.bottom)

            if (dy > limit) {
                super.scrollTo(0, limit)
                logger("scrollTo fling")
                clearSelfFling()
            }

            if (dy < 0) { dy = 0 }
            if (dy != scrollY && dy < limit) {
                super.scrollTo(0, dy)
            }
        } else {
            var dx = x
            val limit = max(lifeCalendarDrawable.getDrawableWidth(), rect.right) - min(lifeCalendarDrawable.getDrawableWidth(), rect.right)

            if (dx > limit) {
                super.scrollTo(limit, 0)
                clearSelfFling()
            }

            if (dx < 0) { dx = 0 }
            if (dx != scrollX && dx < limit) {
                super.scrollTo(dx, 0)
            }
        }
    }

    override fun computeScroll() {
        if (lifeCalendarDrawable.getOrientation() == VERTICAL) {
            if (mScroller.computeScrollOffset()) {
                scrollTo(0, mScroller.currY)
                logger("computeScroll fling")
                invalidate()
                return
            }
        } else {
            if (mScroller.computeScrollOffset()) {
                scrollTo(mScroller.currX, 0)
                invalidate()
                return
            }
        }
    }

    override fun overScrollBy(deltaX: Int, deltaY: Int, scrollX: Int, scrollY: Int,
                              scrollRangeX: Int, scrollRangeY: Int,
                              maxOverScrollX: Int, maxOverScrollY: Int, isTouchEvent: Boolean): Boolean {
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent)
    }

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
    }



    private val consumed = IntArray(2)

    private fun onScrollBehavior(y: Int) : Boolean {
        logger("onScrollBehavior： y= $y")
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH)
        consumed[0] = 0
        consumed[1] = 0
        return dispatchNestedPreScroll(0, y, consumed, null, ViewCompat.TYPE_TOUCH)
    }


    // NestedScrollingChild2
    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        return mChildHelper.startNestedScroll(axes, type)
    }

    override fun stopNestedScroll(type: Int) {
        mChildHelper.stopNestedScroll(type)
    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        return mChildHelper.hasNestedScrollingParent(type)
    }

    override fun dispatchNestedScroll(
            dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
            dyUnconsumed: Int, offsetInWindow: IntArray?, type: Int
    ): Boolean {
        return mChildHelper.dispatchNestedScroll(
                dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow, type
        )
    }

    /**
     * 子View滚动后通知本View后会调用这个方法通知Behavior
     */
    override fun dispatchNestedPreScroll(
            dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?,
            type: Int
    ): Boolean {

        val boolean = mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
        logger("dispatchNestedPreScroll DY：$dy  consumed: ${consumed?.get(1)}")

        dispatchNestedScroll(dx, dy, consumed?.get(0) ?: 0, consumed?.get(1) ?: 0, null, type)
//        consumed?.get(1)?.let {
//            scrollBy(0, dy - it)
//        }
        return boolean
    }

    // NestedScrollingChild
    override fun setNestedScrollingEnabled(enabled: Boolean) {
        mChildHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return mChildHelper.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return startNestedScroll(axes, ViewCompat.TYPE_TOUCH)
    }

    override fun stopNestedScroll() {
        stopNestedScroll(ViewCompat.TYPE_TOUCH)
    }

    override fun hasNestedScrollingParent(): Boolean {
        return hasNestedScrollingParent(ViewCompat.TYPE_TOUCH)
    }

    override fun dispatchNestedScroll(
            dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
            dyUnconsumed: Int, offsetInWindow: IntArray?
    ): Boolean {
        return mChildHelper.dispatchNestedScroll(
                dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow
        )
    }

    override fun dispatchNestedPreScroll(
            dx: Int,
            dy: Int,
            consumed: IntArray?,
            offsetInWindow: IntArray?
    ): Boolean {
        return dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, ViewCompat.TYPE_TOUCH)
    }

    override fun dispatchNestedFling(
            velocityX: Float,
            velocityY: Float,
            consumed: Boolean
    ): Boolean {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY)
    }
}