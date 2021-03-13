package org.bubbble.opendesign.controller.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout

/**
 * @author Andrew
 * @date 2020/09/06 16:42
 * 一个支持View偏移的Behavior
 */
open class ViewOffsetBehavior<V: View>(context: Context, attrs: AttributeSet) :
    CoordinatorLayout.Behavior<V>(context, attrs) {

    /**
     * View偏移Helper
     */
    private var viewOffsetHelper: ViewOffsetHelper? = null

    /**
     * 垂直偏移量
     */
    private var tempTopBottomOffset = 0

    /**
     * 水平偏移量
     */
    private var tempLeftRightOffset = 0

    /**
     * 对childView进行偏移
     */
    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        layoutChild(parent, child, layoutDirection)
        if (viewOffsetHelper == null) {
            viewOffsetHelper = ViewOffsetHelper(child)
        }

        viewOffsetHelper?.onViewLayout()

        // 对View应用偏移量，并重置偏移量
        if (tempTopBottomOffset != 0) {
            viewOffsetHelper?.setTopAndBottomOffset(tempTopBottomOffset)
            tempTopBottomOffset = 0
        }

        if (tempLeftRightOffset != 0) {
            viewOffsetHelper?.setLeftAndRightOffset(tempLeftRightOffset)
            tempLeftRightOffset = 0
        }

        return true
    }

    /**
     * child给CoordinatorLayout排列
     */
    private fun layoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int) {
        parent.onLayoutChild(child, layoutDirection)
    }

    /**
     * 暴露给外部设置垂直偏移量的方法
     */
    fun setTopAndBottomOffset(offset: Int): Boolean {
        return if (viewOffsetHelper != null) {
            viewOffsetHelper!!.setTopAndBottomOffset(offset)
        } else {
            tempLeftRightOffset = offset
            false
        }
    }

    /**
     * 暴露给外部设置水平偏移量的方法
     */
    fun setLeftAndRightOffset(offset: Int): Boolean {
        return if (viewOffsetHelper != null) {
            viewOffsetHelper!!.setLeftAndRightOffset(offset)
        } else {
            tempLeftRightOffset = offset
            false
        }
    }

    /**
     * 暴露给外部获取垂直偏移量的方法
     */
    fun getTopAndBottomOffset(): Int {
        return if (viewOffsetHelper != null) {
            viewOffsetHelper!!.getTopAndBottomOffset()
        } else {
            0
        }
    }

    /**
     * 暴露给外部获取水平偏移量的方法
     */
    fun getLeftAndRightOffset(): Int {
        return if (viewOffsetHelper != null) {
            viewOffsetHelper!!.getTopAndBottomOffset()
        } else {
            0
        }
    }

}