package org.bubbble.taobao.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Insets
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowInsets
import androidx.appcompat.widget.Toolbar


/**
 * @author Andrew
 * @date 2020/08/04 19:49
 */
object Utils {

    /**
     * 获取界面到屏幕顶部的高度
     */
    private fun getAppTopHeight(activity: Activity): Int{
        val frame = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(frame)
        Log.e("appTopHeight", frame.top.toString())
        return frame.top
    }

    /**
     * Toolbar设置paddingTop
     */
    fun fixToolbarPadding(toolbar: Toolbar, activity: Activity) {
        val paddingTop = getAppTopHeight(activity)
        if (paddingTop in 1..300){
            Log.e("状态栏", paddingTop.toString())
            toolbar.setPadding(0, paddingTop, 0, 0)
            val lp = toolbar.layoutParams
            lp.height = paddingTop + toolbar.height
        }
    }

    /**
     * 是否是窗口模式(分屏) 如果在上方也认为是分屏
     */
    private fun isStatusBarVisible(activity: Activity):Boolean {
        val isInMultiWindowMode = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && activity.isInMultiWindowMode
        return true
    }

    /**
     * 获取状态栏的高度
     */
    fun getStatusBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        try {
            return context.resources.getDimensionPixelSize(resourceId)
        } catch (e: Resources.NotFoundException) {
            return 0
        }

    }

    /**
     * 获取可用高度
     */
    fun getScreenHeight(activity: Activity): Int{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.height() - insets.top - insets.bottom
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }
}