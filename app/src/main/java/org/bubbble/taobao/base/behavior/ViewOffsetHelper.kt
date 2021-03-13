package org.bubbble.opendesign.controller.behavior

import android.view.View
import androidx.core.view.ViewCompat

/**
 * @author Andrew
 * @date 2020/09/06 16:21
 *
 * 这可能是一个View偏移的计算工具类
 */
class ViewOffsetHelper(private val view: View) {

    private var layoutTop = 0
    private var layoutLeft = 0
    private var offsetTop = 0
    private var offsetLeft = 0

    fun onViewLayout() {
        // 对其成员变量值初始化，view的相对于父容器的距离
        // getTop() 获取到的是view自身的顶边到其父布局顶边的距离
        // getLeft() 获取到的是view自身的左边到其父布局左边的距离
        layoutTop = view.top
        layoutLeft = view.left
        updateOffsets()
    }

    /**
     * 实行偏移增量
     */
    private fun updateOffsets() {
        // 将view向垂直（水平）位置偏移 偏移值为 偏移变量 - （view自身边 到 父view边的距离 - 初始时 view自身边 到 父view边的距离）
        ViewCompat.offsetTopAndBottom(view, offsetTop - (view.top - layoutTop))
        ViewCompat.offsetLeftAndRight(view, offsetLeft - (view.left - layoutLeft))
    }

    /**
     * 设置垂直偏移增量值
     */
    fun setTopAndBottomOffset(offset: Int): Boolean {
        return if (offsetTop != offset) {
            offsetTop = offset
            updateOffsets()
            true
        } else {
            false
        }
    }

    /**
     * 设置水平偏移增量值
     */
    fun setLeftAndRightOffset(offset: Int): Boolean {
        return if (offsetLeft != offset) {
            offsetLeft = offset
            updateOffsets()
            true
        } else {
            false
        }
    }

    fun getTopAndBottomOffset(): Int = offsetTop
    fun getLeftAndRightOffset(): Int = offsetLeft
}