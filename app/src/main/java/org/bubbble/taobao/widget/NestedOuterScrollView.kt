package org.bubbble.taobao.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.EdgeEffect
import android.widget.LinearLayout
import android.widget.OverScroller
import androidx.core.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.bubbble.taobao.R
import org.bubbble.taobao.util.logger
import java.util.*
import kotlin.math.abs

class NestedOuterScrollView : LinearLayout, NestedScrollingParent2, NestedScrollingChild2, NestedScrollingChild3 {

    constructor(context: Context?) : super(context!!) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
            context!!,
            attrs,
            defStyle
    ) {
        init(attrs)
    }

    private var mScroller: OverScroller? = null
    private var mMaximumVelocity = 0
    private var mMinimumVelocity = 0

    private val mParentHelper = NestedScrollingParentHelper(this)
    private val mChildHelper = NestedScrollingChildHelper(this)

    private var mTopViewId = 0
    private var mInnerOffsetId = 0
    private var mContentViewId = 0

    private var mEdgeEffectTop: EdgeEffect? = null
    private var mEdgeEffectBottom: EdgeEffect? = null

    private var mOverFlingDistance = 0
    private var mOverScrollDistance = 0

    private var mFirstY = 0f
    private val mInnerOffsetView = ArrayList<View>(1)
    private val mTopStableView = ArrayList<View>(1)
    private var mContentView: View? = null
    private var mTopScrollHeight = 0
    private var mVelocityTracker: VelocityTracker? = null
    private var mTouchSlop = 0
    private var mLastY = 0f
    private var mDragging = false
    private var mTarget: View? = null
    private var mScrollType = ViewCompat.TYPE_TOUCH
    private var mTargetLastY = 0
    private var mScrollListener: onScrollListener? = null
    private var mInflated = false
    private var mOuterCoverTopHeight = 0
    private var mOuterCoverBottomHeight = 0
    private var innerOffsetHeight = 0
    private var mInsetTargetScrollView: View? = null
    @Volatile var mScrollY = 0
    private var consumeY = 0

    private fun init(attrs: AttributeSet?) {
        orientation = VERTICAL
        isNestedScrollingEnabled = true

        mScroller = OverScroller(context)
        val config = ViewConfiguration.get(context)
        mTouchSlop = config.scaledTouchSlop
        mMaximumVelocity = config.scaledMaximumFlingVelocity
        mMinimumVelocity = config.scaledMinimumFlingVelocity

        mOverFlingDistance = config.scaledOverflingDistance
        mOverScrollDistance = config.scaledOverscrollDistance
        mEdgeEffectBottom = EdgeEffect(context)
        mEdgeEffectTop = EdgeEffect(context)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.NestedOuterScrollView)
            mTopViewId =
                a.getResourceId(R.styleable.NestedOuterScrollView_nest_scroll_top_view3, 0)
            mInnerOffsetId =
                a.getResourceId(R.styleable.NestedOuterScrollView_nest_scroll_inner_header3, 0)
            mContentViewId =
                a.getResourceId(R.styleable.NestedOuterScrollView_nest_scroll_content3, 0)
            a.recycle()
        }

        /**
         * Because by default a layout does not need to draw,
         * so an optimization is to not call is draw method. By calling setWillNotDraw(
         * false) you tell the UI toolkit that you want to draw
         */
        setWillNotDraw(false) //必须！！！！
    }

    companion object {
        private const val TAG = "ExNestScroll"
    }

    fun setContentView(view: View?): NestedOuterScrollView {
        mContentView = view
        return this
    }

    fun setContentViewId(id: Int): NestedOuterScrollView {
        mContentViewId = id
        if (id > 0 && mInflated) {
            mContentView = findViewById(id)
        }
        return this
    }

    fun setTopView(topView: View?): NestedOuterScrollView {
        if (topView != null) {
            mTopStableView.add(topView)
        } else {
            mTopStableView.clear()
        }
        return this
    }

    fun setTopViewId(topId: Int): NestedOuterScrollView {
        mTopViewId = topId
        if (topId > 0 && mInflated) {
            val v = findViewById<View>(topId)
            if (v != null) {
                mTopStableView.add(v)
            }
        }
        return this
    }

    fun setInnerOffsetView(view: View): NestedOuterScrollView {
        if (!mInnerOffsetView.contains(view)) {
            mInnerOffsetView.add(view)
        }
        return this
    }

    fun setOuterCoverTopMargin(height: Int): NestedOuterScrollView {
        if (mOuterCoverTopHeight != height) {
            mOuterCoverTopHeight = height
            requestLayout()
        }
        return this
    }

    fun setOuterCoverBottomMargin(height: Int): NestedOuterScrollView {
        if (mOuterCoverBottomHeight != height) {
            mOuterCoverBottomHeight = height
            requestLayout()
        }
        return this
    }

    private fun calculateInnerOffsetHeight(): Int {
        var h = 0
        for (v in mInnerOffsetView) {
            h += v.measuredHeight
        }
        innerOffsetHeight = h
        return h
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
                canvas.translate((-width + paddingLeft).toFloat(), (Math.max(0, scrollY) + height).toFloat())
                canvas.rotate(180f, width.toFloat(), 0f)
                mEdgeEffectBottom!!.setSize(width, height)
                if (mEdgeEffectBottom!!.draw(canvas)) {
                    postInvalidate()
                }
                canvas.restoreToCount(count)
            }
        }
    }

    /**
     * 如果在TopView上，则拦截
     * 否则不拦截
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_MOVE -> {
                if (mDragging) {
                    return true
                }
                val y = ev.y
                if (y > mTopScrollHeight + innerOffsetHeight - scrollY) {
                    return false
                }
                val dy = Math.abs(mLastY - ev.y)
                if (dy > mTouchSlop) {
                    // 只有手指滑动距离大于阈值时，才会开始拦截
                    // Start scrolling!
                    parent.requestDisallowInterceptTouchEvent(true)
                    return true
                }
            }
            MotionEvent.ACTION_DOWN -> {
                clearAllFling()
                mLastY = ev.y
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> mDragging = false
        }
        return false
    }

    /**
     * 对内部View进行布局
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d(TAG, "onMeasure height:" + MeasureSpec.getSize(heightMeasureSpec))
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mTopStableView.size > 0) {
            mTopScrollHeight = 0
            for (stableView in mTopStableView) {
                val lp = stableView.layoutParams
                val selfWidthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
                val selfWidthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
                var childWidthSpec: Int
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

                stableView.measure(childWidthSpec,
                        MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.UNSPECIFIED)
                )
                mTopScrollHeight += stableView.measuredHeight
            }
            if (mContentView != null) {
                val innerOffsetHeight = calculateInnerOffsetHeight()

                var contentH = measuredHeight - innerOffsetHeight - mOuterCoverTopHeight
                if (contentH > mOuterCoverBottomHeight) {
                    contentH -= mOuterCoverBottomHeight
                }
                mContentView!!.measure(
                        widthMeasureSpec,
                        MeasureSpec.makeMeasureSpec(contentH, MeasureSpec.AT_MOST)
                )
            }
            setMeasuredDimension(
                    measuredWidth,
                    mTopScrollHeight + measuredHeight
            )
        }
    }

    /**
     * 其实就是TopView的高度
     */
    private val topScrollHeight: Int
        get() = mTopScrollHeight - mOuterCoverTopHeight

    override fun onFinishInflate() {
        super.onFinishInflate()
        mInflated = true
        if (mTopViewId > 0) {
            setTopViewId(mTopViewId)
        }
        if (mInnerOffsetId > 0) {
            mInnerOffsetView.add(findViewById(mInnerOffsetId))
        }
        if (mContentViewId > 0) {
            mContentView = findViewById(mContentViewId)
        }
    }

    private fun fling(velocityY: Float) {
        logger("fling 执行 startY： $scrollY, velocityY：$velocityY")
        mTargetLastY = 0

        if (!dispatchNestedPreFling(0f, velocityY)) {
            dispatchNestedFling(0f, velocityY, true)
            mScroller!!.fling(0, scrollY, 0, velocityY.toInt(), 0, 0, 0, Int.MAX_VALUE)
            postInvalidateOnAnimation()
//                invalidate()
        }

    }

    private fun clearSelfFling() {
        if (!mScroller!!.isFinished) {
            mScroller!!.abortAnimation()
        }
        mTargetLastY = 0
    }

    private fun clearAllFling() {
        if (!mScroller!!.isFinished) {
            mScroller!!.abortAnimation()
        }
        mTargetLastY = 0
        if (mTarget != null) {
            Log.d(TAG, "force stop nest scroll from child")
            ViewCompat.stopNestedScroll(mTarget!!, mScrollType)
        }
    }

    private fun findChildScrollView(dy: Int): View? {
        var delegateView = mContentView
        if (delegateView != null && delegateView.canScrollVertically(dy)) {
            return delegateView
        }
        delegateView = mInsetTargetScrollView
        if (delegateView != null
                && delegateView.canScrollVertically(dy)) {
            return delegateView
        }
        return null
    }


    /**
     * 当内部滚动时调用，所以RV全部显示时这里便不会被调用了。
     */
    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        mScrollY = t
        mScrollListener?.onNestScrolling(t - oldt, t)
    }

    fun setScrollListener(l: onScrollListener?): NestedOuterScrollView {
        mScrollListener = l
        return this
    }

    interface onScrollListener {
        fun onNestScrolling(dy: Int, scrollY: Int)
    }

    // 计算topView可以滚动的dy值
    private fun consumeY(target: View?, dy: Int): Int {
        // dy 代表滑动的距离，是正是负代表滑动方向。
        // 读代码 RecyclerView和ScrollView发现，dy = mLastY - currentY，是用前一次减这一次活动的deltaY
        // 也就是手指从上往下滑，currentY 肯定大于 mLastY，所以 dy < 0 是从上往下滑动。
        if (dy > 0) {
            // 手指从下往上滑动，如果top还没有完全隐藏，则必须消耗dy以隐藏top，执行 scroll hide top动作
            // getScrollY()会返回控件坐标向上偏移量，正数表示向上偏移，负数表示向下偏移
            val sY = scrollY
            if (sY < topScrollHeight) {
                return Math.min(topScrollHeight - sY, dy)
            }
        } else if (dy < 0) {
            val sY = scrollY
            target?.let {
                // 从上往下滑，scroll Y >=0 标识 Y 向下已有偏移，计算list是否到顶
                if (it is RecyclerView) {
                    val layoutManager = it.layoutManager as LinearLayoutManager
                    val firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                    if (firstCompletelyVisibleItemPosition != 0 || it.canScrollVertically(-1)) {
                        logger("CoordinatorLayout 认为外部RV没到顶部")
                        return 0
                    }
                } else {
                    if (it.canScrollVertically(-1)) {
                        return 0
                    }
                }
            }
            if (sY > 0) {
                logger("dy $dy: scrollY $scrollY")
                return Math.max(dy, -sY) //这里的值都是负数，取绝对值小的，所以要用max
            }
        }
        return 0
    }

    /**
     * 如果拦截到了，自身的处理内部滚动。
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        initVelocityTrackerIfNotExists()
        mVelocityTracker!!.addMovement(event)
        val action = event.action
        val y = event.y
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                clearAllFling()
                mLastY = y
                mFirstY = y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dy = y - mLastY
                if (!mDragging && abs(y - mFirstY) > mTouchSlop) {
                    mDragging = true
                }
                if (mDragging) {

                    val boolean = onScrollBehavior((-dy).toInt(), 1)
                    logger("mDragging ${(-dy).toInt()}  boolean: $boolean")
                    mLastY = if (boolean) y - dy else y
                }
//                mLastY = y - dy
                Log.d(TAG, "dispatchNestedPreScroll")
            }
            MotionEvent.ACTION_CANCEL -> {
                logger("ACTION_CANCEL 执行")
                mDragging = false
                recycleVelocityTracker()
                if (!mScroller!!.isFinished) {
                    mScroller!!.abortAnimation()
                }
            }
            MotionEvent.ACTION_UP -> {
                logger("ACTION_UP 执行")
                mDragging = false
                mVelocityTracker!!.computeCurrentVelocity(1000, mMaximumVelocity.toFloat())
                val velocityY = mVelocityTracker!!.yVelocity
                if (Math.abs(velocityY) > mMinimumVelocity) {
                    fling(-velocityY)
                }
                recycleVelocityTracker()
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * 滚动超过topView则滚动内部RecyclerView
     */
    override fun scrollTo(x: Int, y: Int) {
        Log.d(TAG, "讲武德的scrollTo $y")
        // 本次内部滑到的y点位置，上边就是 0，下边就是内部view高度
        var y1 = y
        // 自然就是以点到为止，如果 currY 大于 TopView高度，那就正常滚动到TopView底部的位置，剩下的继续滚内部RecyclerView
        if (y1 > topScrollHeight) {
            super.scrollTo(x, topScrollHeight)
            y1 -= topScrollHeight
            val dy = y1 - mTargetLastY
            val scrollView = findChildScrollView(dy)
            if (scrollView != null) {
                scrollView.scrollBy(x, dy)
                mTargetLastY += dy
                Log.d(TAG, "scrollTo transfer $y1")
            } else {
                Log.d(TAG, "scrollTo but container view can not scroll")
//                clearSelfFling()
            }
            return
        }

        logger("不讲武德 currY：$y1  topScrollHeight: $topScrollHeight   mTargetLastY: $mTargetLastY")

        // 如果不讲武德，就会直接super，但是currY不能小于0

        // 上边不能超滑
        if (y1 < 0) {
            y1 = 0
        }
        if (y1 != scrollY) {
            super.scrollTo(x, y1)
        }
    }

    /**
     * 专门处理Fling，只有Behavior完事了才能继续玩
     */
    private fun scrollTo(y: Int) {
        Log.d(TAG, "scrollTo $y  mTargetLastY： $mTargetLastY")
        if (mTargetLastY != 0) {
            // 本次内部滑到的y点位置，上边就是 0，下边就是内部view高度
            val dy = y - mTargetLastY
            onScrollBehavior(dy, 2)
            mTargetLastY += dy
        } else {
            mTargetLastY = y
        }

    }

    override fun computeScroll() {

        if (mScroller!!.isFinished) {
            return
        }
        mScroller!!.computeScrollOffset()
        Log.d(TAG, "computeScroll scrollTo " + mScroller!!.currY) // 计算得到 0
        scrollTo(mScroller!!.currY)

        if (!mScroller!!.isFinished) {
            ViewCompat.postInvalidateOnAnimation(this)
        } else {
            logger("computeScroll 这就完成了？？？？？")
            stopNestedScroll(ViewCompat.TYPE_NON_TOUCH)
        }
    }

    private val consumed = IntArray(3)

    private fun onScrollBehavior(y: Int, s: Int) : Boolean {
        logger("onScrollBehavior： y= $y")
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH)
        consumed[0] = 0
        consumed[1] = 0
        consumed[2] = s
        if (s == 1 || s == 2) {
            consumeY = consumeY(mTarget, y)
        }
        return dispatchNestedPreScroll(0, y, consumed, null, ViewCompat.TYPE_TOUCH)
    }

    private val consumed3 = IntArray(2)
    private fun onNestedScrollInternal(dyConsumed: Int, dyUnconsumed: Int, type: Int): Boolean {
        logger("二次dyUnconsumeddy Consumed: $dyConsumed dyUnconsumed $dyUnconsumed  consumeY: $consumeY")
        if (dyUnconsumed < 0 && consumeY != 0) {
            scrollBy(0, dyUnconsumed)
            return false
//            return mChildHelper.dispatchNestedScroll(0, dyConsumed, 0, dyUnconsumed - consumeY, null, type)
        } else {

            consumeY = consumeY(mTarget, dyUnconsumed)
            // 如果到了内部Scroll顶部，先不急着给滑动到Behavior,先滚动自己的topView。
            mTarget?.let {
                if (it is RecyclerView) {
                    val layoutManager = it.layoutManager as LinearLayoutManager
                    val firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                    if (firstCompletelyVisibleItemPosition == 0 || !it.canScrollVertically(-1)) {
                        if (consumeY != 0) {
                            return onNestedScrollInternal(dyConsumed, dyUnconsumed, type)
                        }
                    }
                } else {
                    if (!it.canScrollVertically(-1)) {
                        if (consumeY != 0) {
                            return onNestedScrollInternal(dyConsumed, dyUnconsumed, type)
                        }
                    }
                }
            }
            logger("不不不不不不不不不不不不不不 dyConsumed: $dyConsumed  dyUnconsumed: $dyUnconsumed  consumeY: $consumeY mTarget: $mTarget")

            return if (dyUnconsumed < 0) {
                consumed3[0] = 0
                consumed3[1] = 0
                mChildHelper.dispatchNestedScroll(0, dyConsumed, 0, dyUnconsumed, null, type, consumed3)
                // Behavior滚动
                if (consumed3[1] == 0) {
                    // 消除内部RV的Fling
                    mContentView?.let {
                        ViewCompat.stopNestedScroll(it, ViewCompat.TYPE_NON_TOUCH)
                    }
                }
                true
            } else {
                scrollBy(0, dyUnconsumed)
                false
            }

        }
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
        return onNestedScrollInternal(dyConsumed, dyUnconsumed, type)
    }

    /**
     * 子View滚动后通知本View后会调用这个方法通知Behavior
     */
    override fun dispatchNestedPreScroll(
            dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?,
            type: Int
    ): Boolean {
        logger("dispatchNestedPreScroll DY：$dy")

        val boolean = mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)

        consumed?.get(1)?.let { consumedY ->

            logger("不不不不不不不不不不不不不 dy: $dy  consumedY: $consumedY  consumeY: $consumeY  type: ${consumed[2]}")

            // Behavior消耗完了全部
            if (dy == consumedY) {
                return boolean
            }

            // 如果Behavior没有消费，那么看看是否可以滚动topView
            if (consumedY == 0 && consumeY != 0) {
                Log.d(TAG, "啊啊啊啊啊***** consumeY $consumeY dy: $dy")

                scrollBy(0, dy)

            } else if(consumedY != 0) {
                // 因为自己本身就不需要滚动了，而Behavior又继续消费
                Log.d(TAG, "啊啊啊啊啊*---* consumed $consumedY  unconsumed $dy")
//                mContentView?.scrollBy(0, dy)

            } else {
                // 这里实际上是下滑后 就会调用
                Log.d(TAG, "啊啊啊啊啊*+++* consumed $consumedY  unconsumed $dy")

                // 只有触摸topView才能触发
                if (consumed[2] == 1) {
                    mTarget?.let {
                        if (it is RecyclerView) {
                            val layoutManager = it.layoutManager as LinearLayoutManager
                            val firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                            if (firstCompletelyVisibleItemPosition != 0 || it.canScrollVertically(-1)) {
                                // 内部RV不在顶部
//                            rv.scrollBy(0, dy)
                                return boolean
                            }
                        } else {
                            if (it.canScrollVertically(-1)) {
                                return boolean
                            }
                        }
                    }
                    return dispatchNestedScroll(0, 0, 0, dy, null, type)
                } else if (consumed[2] == 2) {
//                    dispatchNestedScroll(0, 0, 0, dy, null, type)
                    mContentView?.scrollBy(0, dy)
                } else {
                    logger("正在fling")
                    return boolean
                }
            }
        }
        return boolean
    }


    // NestedScrollingChild3
    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, offsetInWindow: IntArray?, type: Int, consumed: IntArray) {
        mChildHelper.dispatchNestedScroll(
                dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow, ViewCompat.TYPE_TOUCH, consumed)
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
        return dispatchNestedScroll(
                dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow, ViewCompat.TYPE_TOUCH
        )
    }

    override fun dispatchNestedPreScroll(
            dx: Int,
            dy: Int,
            consumed: IntArray?,
            offsetInWindow: IntArray?
    ): Boolean {
        logger("不不不不不不不不不不不不不不不")
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
        logger("dispatchNestedPreFling velocityY: $velocityY ")
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY)
    }



    // NestedScrollingParent2
    override fun onStartNestedScroll(
            child: View, target: View, axes: Int,
            type: Int
    ): Boolean {
        mTarget = target
        mScrollType = type
//        clearSelfFling()
        return axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedScrollAccepted(
            child: View, target: View, axes: Int,
            type: Int
    ) {
        mTarget = target
        mScrollType = type
//        clearSelfFling()
        mParentHelper.onNestedScrollAccepted(child, target, axes, type)
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, type)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        if (mScrollType == type) {
            mTarget = null
        }
        mParentHelper.onStopNestedScroll(target, type)
        stopNestedScroll(type)
    }

    override fun onNestedScroll(
            target: View, dxConsumed: Int, dyConsumed: Int,
            dxUnconsumed: Int, dyUnconsumed: Int, type: Int
    ) {
        logger("不可能2")
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null, type)
    }

    /**
     * 这里拿到了内嵌RecyclerView的滚动前距离，如果topView需要滚动，则消耗完全部。
     * 如果Behavior消耗了一部分，则只减去这一部分
     */
    override fun onNestedPreScroll(
            target: View, dx: Int, dy: Int, consumed: IntArray,
            type: Int
    ) {

        consumeY = consumeY(target, dy)

        // 如果内部RecyclerView滚动时，TopView是不需要滚动的才会传给Behavior
        if (consumeY == 0 || consumeY > 0 ) {
            // 直接用onScrollBehavior传给父View，如果AppBar滚动了，则不让子RecyclerView滚动，也就是消耗完全部。Fling也是从这里传来的
            if (onScrollBehavior(dy, 0)) {
                // 消耗完全部
                consumed[1] = dy
            }
        } else {
            scrollBy(0, dy)
        }

        // 只有topView没有隐藏的时候才会 不让子RecyclerView滚动，也就是消耗完全部
        Log.d(TAG, "onNestedPreScroll unconsumed consumeY $consumeY")
        if (consumeY != 0) {
            // 消耗完全部
            consumed[1] = dy
            return
        }
    }

    // NestedScrollingParent
    override fun onStartNestedScroll(
            child: View, target: View, nestedScrollAxes: Int
    ): Boolean {
        return onStartNestedScroll(child, target, nestedScrollAxes, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedScrollAccepted(
            child: View, target: View, nestedScrollAxes: Int
    ) {
        onNestedScrollAccepted(child, target, nestedScrollAxes, ViewCompat.TYPE_TOUCH)
    }

    override fun onStopNestedScroll(target: View) {
        onStopNestedScroll(target, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedScroll(
            target: View, dxConsumed: Int, dyConsumed: Int,
            dxUnconsumed: Int, dyUnconsumed: Int
    ) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxConsumed, dyUnconsumed, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        onNestedPreScroll(target, dx, dy, consumed, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedFling(
            target: View, velocityX: Float, velocityY: Float, consumed: Boolean
    ): Boolean {
        if (!consumed) {
            dispatchNestedFling(0f, velocityY, true)
            fling(velocityY)
            return true
        }
        return false
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun getNestedScrollAxes(): Int {
        return mParentHelper.nestedScrollAxes
    }
}