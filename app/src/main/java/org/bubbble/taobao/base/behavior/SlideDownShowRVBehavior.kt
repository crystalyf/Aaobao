package org.bubbble.taobao.base.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import org.bubbble.taobao.util.Utils

/**
 * @author Andrew
 * @date 2020/09/06 20:44
 * Recyclerview只需要跟随headView移动即可
 */
class SlideDownShowRVBehavior<V: View>(context: Context, attrs: AttributeSet) :
    CoordinatorLayout.Behavior<V>(context, attrs) {

    override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View): Boolean {

        return dependency is AppBarLayout
    }

    override fun onDependentViewChanged(
            parent: CoordinatorLayout,
            child: V,
            dependency: View
    ): Boolean {

        val dependencyY = dependency.y

        var y = dependency.height + dependencyY
        if (y < 0) {
            y = 0F
        }
        child.y = y
        return true
    }
}