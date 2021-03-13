package org.bubbble.taobao.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import org.bubbble.taobao.util.logger

/**
 * @author Andrew
 * @date 2020/11/24 20:47
 */
class AndScrollView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : OverScrollView(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    override fun computeScroll() {
        super.computeScroll()
        logger("computeScroll")
    }
}