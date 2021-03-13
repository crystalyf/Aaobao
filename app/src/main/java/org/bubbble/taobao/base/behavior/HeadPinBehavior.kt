package org.bubbble.taobao.base.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_tao.view.*
import org.bubbble.taobao.R
import org.bubbble.taobao.util.Utils
import org.bubbble.taobao.util.logger

/**
 * @author Andrew
 * @date 2020/09/06 21:13
 * Appbar 的 Behavior，负责处理RV滚动时上移
 */

class HeadPinBehavior<V: View>(context: Context, attrs: AttributeSet) :
    HeaderBehavior<View>(context, attrs) {

    private var childY = 0F
    private var pinLocation = 0F

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        // 保证执行了一次
        if (pinLocation == 0F) {
            // 获取默认距离
            childY = child.y
            pinLocation = 0F - child.findViewById<ConstraintLayout>(R.id.appbar).height
            logger("childY $childY  pinLocation $pinLocation")
        }
        return false
    }

    override fun onDependentViewChanged(
            parent: CoordinatorLayout,
            child: View,
            dependency: View
    ): Boolean {
        return true
    }

    override fun onNestedPreScroll(
            coordinatorLayout: CoordinatorLayout,
            child: View,
            target: View,
            dx: Int,
            dy: Int,
            consumed: IntArray,
            type: Int
    ) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)

        logger("CoordinatorLayout拿到了来自NestedChild的滚动传递")
        val recyclerView = target as RecyclerView
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
        if (firstCompletelyVisibleItemPosition != 0) {
            logger("CoordinatorLayout 认为外部RV没到顶部")
            return
        }

        logger("CoordinatorLayout 正在协调顶部")

        // 需要滑动 Pin 的View高度
        val childHeight = child.height

        // 现在看来，只有在看见Pin滚动时 dy 值才会改变。dy的值不是线性的，而是根据滑动速度有波动。
        // 上 -> 下 （显示）： - 值
        // 下 -> 上 （隐藏）： + 值

        // dy 就是滑动距离
        // 下 -> 上 （隐藏）dy正数
        if (dy > 0) {

            logger("CoordinatorLayout return1准备执行")
            // 如果RV的顶部 对于CoordinatorLayout的距离 小于等于 pin的底部（加上状态栏），那pin矫正到固定位置
            if (pinLocation >= child.y - dy) {
                // 那么 Pin 就会完全移出屏幕外，现在改成固定住的位置(状态栏向下的位置)
                child.y = pinLocation
                child.findViewById<ConstraintLayout>(R.id.appbar).alpha = 0F
                return
            }

            logger("CoordinatorLayout return1未执行")
            // v 表示偏移量，-dy就是上移量， child.y 应该就是 pin 距离CoordinatorLayout顶部距离
            var v = -dy + child.y

            // 懂了，如果过下了偏离正常默认位置，就先矫正。
            if (v < pinLocation) {
                v = pinLocation
            }

            // 这里就是根据滑动直接改变 距离 的值了
            child.y = v

        } else {

            logger("CoordinatorLayout return2准备执行")
            // 上 -> 下 （显示）dy负数

            // 这里RV的距离 大于等于 RV默认位置
            // 0 >= 0 + pin高度 + dy
            if (childY <= child.y - dy) {
                // 那么 Pin 就会完全显示,也就是默认位置
                child.y = childY
                child.findViewById<ConstraintLayout>(R.id.appbar).alpha = 1F
                return
            }

            logger("CoordinatorLayout return2未执行")
            // 这里 -dy就是下移量了。child.y 应该就是 pin 距离CoordinatorLayout顶部距离
            var v = -dy + child.y

            // 这里是矫正的逻辑，如果 pin 的距离大于默认位置，那么就等于默认位置
            if (v > childY) {
                v = childY
            }
            child.y = v
        }

        // 再次校验

        // 如果RV的顶部 对于CoordinatorLayout的距离 小于等于 pin的底部（加上状态栏），那pin矫正到固定位置
        if (child.y < pinLocation) {
            // 那么 Pin 就会完全移出屏幕外，现在改成固定住的位置(状态栏向下的位置)
            child.y = pinLocation
            child.findViewById<ConstraintLayout>(R.id.appbar).alpha = 0F
            logger("CoordinatorLayout return3执行")
            return
        }

        // 上 -> 下 （显示）
        // 这里RV的距离 大于等于 RV默认位置
        if (child.y > 0) {
            // 那么 Pin 就会完全显示,也就是默认位置
            child.y = childY
            child.findViewById<ConstraintLayout>(R.id.appbar).alpha = 1F
            logger("CoordinatorLayout return4执行")
            return
        }

        // 改变透明度，需要的其实就是：完全收起透明度为0，完全展开透明度为1
        // 0-1的透明度的比值：当前 pin的 y / 总共位移的高度 = 透明度比值

        // child.y: 0 ~ -200
        val alpha = 1 - (child.y / pinLocation)
        child.findViewById<ConstraintLayout>(R.id.appbar).alpha = alpha

        // consumed[1]代表消费垂直
        consumed[1] = dy
    }
}