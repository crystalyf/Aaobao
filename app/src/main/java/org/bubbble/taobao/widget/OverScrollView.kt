package org.bubbble.taobao.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.view.*
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import org.bubbble.taobao.util.dp
import org.bubbble.taobao.util.logger

/**
 * @author Andrew
 * @date 2020/11/27 20:45
 */
open class OverScrollView : LinearLayout, NestedScrollingParent3, NestedScrollingChild3 {

    private var mActivePointerId = INVALID_ID
    private var mLastY = 0f
    private var mSecondaryPointerId = INVALID_ID
    private val mSecondaryLastX = 0f
    private var mSecondaryLastY = 0f
    private var mIsBeingDragged = false
    private var mTouchSlop = 0
    private var mMinFlingSpeed = 0
    private var mMaxFlingSpeed = 0
    private var mOverFlingDistance = 0
    private var mOverScrollDistance = 0
    private var mScroller: OverScroller? = null
    private var mVelocityTracker: VelocityTracker? = null
    private var mEdgeEffectTop: EdgeEffect? = null
    private var mEdgeEffectBottom: EdgeEffect? = null

    private val mParentHelper = NestedScrollingParentHelper(this)
    private val mChildHelper = NestedScrollingChildHelper(this)

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        val configuration = ViewConfiguration.get(context)
        mTouchSlop = configuration.scaledTouchSlop
        mMinFlingSpeed = configuration.scaledMinimumFlingVelocity
        mMaxFlingSpeed = configuration.scaledMaximumFlingVelocity
//        mOverFlingDistance = configuration.scaledOverflingDistance
//        mOverScrollDistance = configuration.scaledOverscrollDistance
        mOverFlingDistance = 0
        mOverScrollDistance = 0
        mScroller = OverScroller(context)
        mEdgeEffectBottom = EdgeEffect(context)
        mEdgeEffectTop = EdgeEffect(context)
        //一般来说mOverScrollDistance为0，OverFlingDistance不一致，这里为了整强显示效果
        overScrollMode = OVER_SCROLL_ALWAYS
        // 这里还是需要的。overScrollBy中会使用到
        /**
         * Because by default a layout does not need to draw,
         * so an optimization is to not call is draw method. By calling setWillNotDraw(
         * false) you tell the UI toolkit that you want to draw
         */
        setWillNotDraw(false) //必须！！！！
    }


    /**
     * 对内部View进行布局
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var child: View
        for (current in 0 until childCount) {
            child = getChildAt(current)
            val lp = child.layoutParams
            val selfWidthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
            val selfWidthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
            val selfHeightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
            val selfHeightSpecSize = MeasureSpec.getSize(heightMeasureSpec)

            var childWidthSpec: Int
            var childHeightSpec: Int

            if (child is NestedScrollView || child is RecyclerView || child is ListView || child is ScrollView) {

                when (lp.width) {
                    ViewGroup.LayoutParams.MATCH_PARENT -> {
                        childWidthSpec =
                                if (selfWidthSpecMode == MeasureSpec.EXACTLY || selfWidthSpecMode == MeasureSpec.AT_MOST) {
                                    MeasureSpec.makeMeasureSpec(selfWidthSpecSize, MeasureSpec.EXACTLY)
                                } else {
                                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                                }
                    }

                    ViewGroup.LayoutParams.WRAP_CONTENT -> {
                        childWidthSpec =
                                if (selfWidthSpecMode == MeasureSpec.EXACTLY || selfWidthSpecMode == MeasureSpec.AT_MOST) {
                                    MeasureSpec.makeMeasureSpec(selfWidthSpecSize, MeasureSpec.AT_MOST)
                                } else {
                                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                                }
                    }

                    else -> {
                        childWidthSpec = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY)
                    }
                }

                when (lp.height) {
                    ViewGroup.LayoutParams.MATCH_PARENT -> {
                        childHeightSpec =
                                if (selfHeightSpecMode == MeasureSpec.EXACTLY || selfHeightSpecMode == MeasureSpec.AT_MOST) {
                                    MeasureSpec.makeMeasureSpec(selfHeightSpecSize, MeasureSpec.EXACTLY)
                                } else {
                                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                                }
                    }

                    ViewGroup.LayoutParams.WRAP_CONTENT -> {
                        childHeightSpec =
                                if (selfHeightSpecMode == MeasureSpec.EXACTLY || selfHeightSpecMode == MeasureSpec.AT_MOST) {
                                    MeasureSpec.makeMeasureSpec(selfHeightSpecSize, MeasureSpec.AT_MOST)
                                } else {
                                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                                }
                    }

                    else -> {
                        childHeightSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY)
                    }
                }

                child.measure(childWidthSpec, childHeightSpec)

            }
        }
        setMeasuredDimension(
                measuredWidth, measuredHeight
        )
    }

    private fun initVelocityTrackerIfNotExist() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
    }

    private fun recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker!!.recycle()
        }
        mVelocityTracker = null
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (mEdgeEffectTop != null) {
            val scrollY = scrollY
            if (!mEdgeEffectTop!!.isFinished) {
                val count = canvas.save()
                val width = width - paddingLeft - paddingRight
                canvas.translate(paddingLeft.toFloat(), Math.min(0, scrollY).toFloat())
                mEdgeEffectTop!!.setSize(width, height)
                if (mEdgeEffectTop!!.draw(canvas)) {
                    postInvalidate()
                }
                canvas.restoreToCount(count)
            }
        }
        if (mEdgeEffectBottom != null) {
            val scrollY = scrollY
            if (!mEdgeEffectBottom!!.isFinished) {
                val count = canvas.save()
                val width = width - paddingLeft - paddingRight
                canvas.translate((-width + paddingLeft).toFloat(), (Math.max(scrollRange, scrollY) + height).toFloat())
                canvas.rotate(180f, width.toFloat(), 0f)
                mEdgeEffectBottom!!.setSize(width, height)
                if (mEdgeEffectBottom!!.draw(canvas)) {
                    postInvalidate()
                }
                canvas.restoreToCount(count)
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.actionMasked
        val index: Int


        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onMotionEvent will be called and we do the actual
         * scrolling there.
         */

        /*
        * Shortcut the most recurring case: the user is in the dragging
        * state and he is moving his finger.  We want to intercept this
        * motion.
        */
        if (ev.action == MotionEvent.ACTION_MOVE && mIsBeingDragged) {
            return true
        }
        when (action) {
            MotionEvent.ACTION_DOWN -> {

                val y = ev.y.toInt()
                if (!inChild(ev.x.toInt(), y)) {
                    mIsBeingDragged = false
//                    recycleVelocityTracker()
                    return mIsBeingDragged
                }

                index = ev.actionIndex
                initVelocityTrackerIfNotExist()
                mVelocityTracker!!.addMovement(ev)
                mLastY = ev.getY(index)
                mActivePointerId = ev.getPointerId(index)
                //分两种情况，一种是初始动作，一个是界面正在滚动，down触摸停止滚动
                mIsBeingDragged = !mScroller!!.isFinished
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                index = ev.actionIndex
                mSecondaryPointerId = ev.getPointerId(index)
                mSecondaryLastY = ev.getY(index)
            }
            MotionEvent.ACTION_MOVE -> {


                /*
                 * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
                 * whether the user has moved far enough from his original down touch.
                 */

                /*
                * Locally do absolute value. mLastMotionY is set to the y value
                * of the down event.
                */
                val activePointerId = mActivePointerId
                if (activePointerId == INVALID_ID) {
                    // If we don't have a valid id, the touch down wasn't on content.
                    return mIsBeingDragged
                }

                val pointerIndex = ev.findPointerIndex(activePointerId)
                if (pointerIndex == -1) {

                    return mIsBeingDragged
                }
                index = ev.findPointerIndex(mActivePointerId)
                y = ev.getY(index)
                val yDiff = Math.abs(y - mLastY)
                if (yDiff > mTouchSlop) {
                    //是滚动状态啦
                    mIsBeingDragged = true
                    mLastY = y
                    initVelocityTrackerIfNotExist()
                    mVelocityTracker!!.addMovement(ev)
                    val parent = parent
                    parent?.requestDisallowInterceptTouchEvent(true)
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {
                index = ev.actionIndex
                val curId = ev.getPointerId(index)
                if (curId == mActivePointerId) {
                    mActivePointerId = mSecondaryPointerId
                    mLastY = mSecondaryLastY
                    mVelocityTracker!!.clear()
                } else {
                    mSecondaryPointerId = INVALID_ID
                    mSecondaryLastY = 0f
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                mIsBeingDragged = false
                mActivePointerId = INVALID_ID

                if (mScroller!!.springBack(scrollX, scrollY, 0, 0, 0, scrollRange)) {
                    ViewCompat.postInvalidateOnAnimation(this)
                }
                recycleVelocityTracker()
            }
            else -> {
            }
        }
        return mIsBeingDragged
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mScroller == null) {
            //
            return false
        }
        initVelocityTrackerIfNotExist()
        // ScrollView中设置了offsetLocation,这里需要设置吗？
        val action = event.actionMasked
        var index: Int
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                if (childCount == 0) {
                    return false
                }
                mIsBeingDragged = !mScroller!!.isFinished
                if (!mScroller!!.isFinished) { //fling
                    mScroller!!.abortAnimation()
                }
                index = event.actionIndex
                mActivePointerId = event.getPointerId(index)
                mLastY = event.getY(index)
            }
            MotionEvent.ACTION_MOVE -> {
                if (mActivePointerId == INVALID_ID) {
                    return true
                }
                index = event.findPointerIndex(mActivePointerId)
                if (index == -1) {
                    return true
                }
                val y = event.getY(index)
                var deltaY = mLastY - y
                if (!mIsBeingDragged && Math.abs(deltaY) > mTouchSlop) {
                    requestParentDisallowInterceptTouchEvent()
                    mIsBeingDragged = true
                    // 减少滑动的距离
                    if (deltaY > 0) {
                        deltaY -= mTouchSlop.toFloat()
                    } else {
                        deltaY += mTouchSlop.toFloat()
                    }
                }
                if (mIsBeingDragged) {
                    //直接滑动
                    Log.e("TEST", "overscroll$deltaY scrollRange$scrollRange overScrollDistance$mOverScrollDistance")
                    overScrollBy(0, deltaY.toInt(), 0, scrollY, 0, scrollRange, 0, mOverScrollDistance, true)

                    //EdgeEffect
                    val pulledToY = (scrollY + deltaY).toInt()
                    mLastY = y
                    if (pulledToY < 0) {
                        Log.e("TEST", "pulledTOY top" + height + "deltaY" + deltaY)
                        mEdgeEffectTop!!.onPull(deltaY / height, event.getX(mActivePointerId) / width)
                        if (!mEdgeEffectBottom!!.isFinished) {
                            mEdgeEffectBottom!!.onRelease()
                        }
                    } else if (pulledToY > scrollRange) {
                        Log.e("TEST", "pulledTOY top" + height + "deltaY" + deltaY)
                        mEdgeEffectBottom!!.onPull(deltaY / height, 1.0f - event.getX(mActivePointerId) / width)
                        if (!mEdgeEffectTop!!.isFinished) {
                            mEdgeEffectTop!!.onRelease()
                        }
                    }
                    if (mEdgeEffectTop != null && mEdgeEffectBottom != null && (!mEdgeEffectTop!!.isFinished
                                    || !mEdgeEffectBottom!!.isFinished)) {
                        postInvalidate()
                    }
                }
                if (mSecondaryPointerId != INVALID_ID) {
                    index = event.findPointerIndex(mSecondaryPointerId)
                    mSecondaryLastY = event.getY(index)
                }
            }
            MotionEvent.ACTION_CANCEL -> endDrag()
            MotionEvent.ACTION_UP -> if (mIsBeingDragged) {
                mVelocityTracker!!.computeCurrentVelocity(1000, mMaxFlingSpeed.toFloat())
                val initialVelocity = mVelocityTracker!!.getYVelocity(mActivePointerId).toInt()
                Log.e("TEST", "velocity$initialVelocity $mMinFlingSpeed")
                if (Math.abs(initialVelocity) > mMinFlingSpeed) {
                    // fling
                    doFling(-initialVelocity)
                } else if (mScroller!!.springBack(scrollX, scrollY, 0, 0, 0, scrollRange)) {

                    ViewCompat.postInvalidateOnAnimation(this)
                }
                endDrag()
            }
            else -> {
            }
        }
        if (mVelocityTracker != null) {
            mVelocityTracker!!.addMovement(event)
        }
        return true
    }

    private fun doFling(speed: Int) {
        if (mScroller == null) {
            return
        }
        mScroller!!.fling(scrollX, scrollY, // start
                0, speed, // velocities
                0, 0, // x
                Integer.MIN_VALUE, Integer.MAX_VALUE, // y
                0, 0)
        ViewCompat.postInvalidateOnAnimation(this)
    }

    private fun endDrag() {
        mIsBeingDragged = false
        recycleVelocityTracker()
        mActivePointerId = INVALID_ID
        mLastY = 0f
    }


    private fun inChild(x: Int, y: Int): Boolean {
        if (childCount > 0) {
            val scrollY = scrollY
            val child = getChildAt(0)
            return !(y < child.top - scrollY || y >= child.bottom - scrollY || x < child.left || x >= child.right)
        }
        return false
    }


    private fun requestParentDisallowInterceptTouchEvent() {
        val parent = parent
        parent?.requestDisallowInterceptTouchEvent(true)
    }

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        Log.e("TEST", "onOverScrolled x$scrollX y$scrollY")
        if (!mScroller!!.isFinished) {
            val oldX = getScrollX()
            val oldY = getScrollY()
            scrollTo(scrollX, scrollY)
            onScrollChanged(scrollX, scrollY, oldX, oldY)


            // 暂时注释掉，因为需要配合Nested，第二个条件出错fling可能不执行
//            if (clampedY && !hasNestedScrollingParent(ViewCompat.TYPE_NON_TOUCH)) {
//                Log.e("TEST1", "springBack")
//                mScroller!!.springBack(scrollX, scrollY, 0, 0, 0, scrollRange)
//            }
        } else {
            // TouchEvent中的overScroll调用
            super.scrollTo(scrollX, scrollY)
        }
    }

    override fun computeScroll() {
        if (mScroller!!.computeScrollOffset()) {
            val oldX = scrollX
            val oldY = scrollY
            val x = mScroller!!.currX
            val y = mScroller!!.currY
            val range = scrollRange
            if (oldX != x || oldY != y) {
                Log.e("TEST", "computeScroll value is" + (y - oldY) + "oldY" + oldY)
                overScrollBy(x - oldX, y - oldY, oldX, oldY, 0, range, 0, mOverFlingDistance, false)
            }
            val overScrollMode = overScrollMode
            val canOverScroll = overScrollMode == OVER_SCROLL_ALWAYS ||
                    overScrollMode == OVER_SCROLL_IF_CONTENT_SCROLLS && range > 0
            if (canOverScroll) {
                if (y < 0 && oldY >= 0) {
                    mEdgeEffectTop!!.onAbsorb(mScroller!!.currVelocity.toInt())
                } else if (y > range && oldY < range) {
                    mEdgeEffectBottom!!.onAbsorb(mScroller!!.currVelocity.toInt())
                }
            }
        } else {
            logger("fling已不复存在")
        }
    }

    //先假设没有margin的情况
    private val scrollRange: Int
        get() {
            var scrollRange = 0
            if (childCount > 0) {
                var totalHeight = 0
                if (childCount > 0) {
                    for (i in 0 until childCount) {
                        totalHeight += getChildAt(i).height
                        //先假设没有margin的情况
                    }
                }
                scrollRange = Math.max(0, totalHeight - height)
            }
            Log.e("TEST", "scrollRange is$scrollRange")
            return scrollRange
        }

    companion object {
        private const val INVALID_ID = -1
    }

    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        return false
    }

    override fun stopNestedScroll(type: Int) {

    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        return false
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, offsetInWindow: IntArray?, type: Int, consumed: IntArray) {

    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, offsetInWindow: IntArray?, type: Int): Boolean {
        return false
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?, type: Int): Boolean {
        return false
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return false
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {

    }

    override fun onStopNestedScroll(target: View, type: Int) {

    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int, consumed: IntArray) {

    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {

    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {

    }
}