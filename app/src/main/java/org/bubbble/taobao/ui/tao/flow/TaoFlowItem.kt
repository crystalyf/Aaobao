package org.bubbble.taobao.ui.tao.flow

/**
 * @author Andrew
 * @date 2020/11/17 19:35
 */
data class TaoFlowItem (
        val type: Int,
        val data: Int,
        val shopList: MutableList<Int>?)