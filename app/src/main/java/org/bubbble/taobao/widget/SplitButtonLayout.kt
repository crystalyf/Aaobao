package org.bubbble.taobao.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import org.bubbble.taobao.R

/**
 * @author Andrew
 * @date 2020/09/05 14:04
 */
class SplitButtonLayout(context: Context, attrs: AttributeSet?,
                        @AttrRes defStyleAttr: Int, @StyleRes defStyleRes: Int) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr:Int):this(context,attrs,defStyleAttr,0)

    constructor(context: Context, attrs: AttributeSet?):this(context, attrs, 0)

    constructor(context: Context):this(context, null)

    fun addButton(context: Context, tag: String) {
        post {
            val button: TextView = LayoutInflater.from(context).inflate(R.layout.item_popular_split_button, this, false) as TextView
            button.text = tag
            addView(button)
        }
    }
}