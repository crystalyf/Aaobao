package org.bubbble.taobao.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.appcompat.widget.AppCompatImageView
import org.bubbble.taobao.R
import org.bubbble.taobao.util.dp


/**
 * @author Andrew
 * @date 2020/09/05 15:20
 * 圆角矩形图片
 */
class FilletImageView(
    context: Context, attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int
) : AppCompatImageView(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context): this(context, null)

    private val paint = Paint()
    private var viewMatrix = Matrix(imageMatrix)
    private var width = 0F
    private var height = 0F
    private var radius = 6F.dp
    private var rectF: RectF

    init {
        paint.isAntiAlias = true
        rectF = RectF()
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.FilletImageView)
        radius = typeArray.getDimension(R.styleable.CircleImageView_strokeWidth, 6F.dp)
        typeArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        width = measuredWidth.toFloat()
        height = measuredWidth.toFloat()
        setMeasuredDimension(width.toInt(), height.toInt())
    }

    override fun onDraw(canvas: Canvas?) {
        if (drawable is BitmapDrawable) {
            paint.shader = initBitmapShader(drawable as BitmapDrawable)
            rectF.set(0F, 0F, width, height)
            canvas?.drawRoundRect(rectF, radius, radius, paint)
            return
        }

        super.onDraw(canvas)
    }

    /**
     * 获取ImageView中资源图片的Bitmap，利用Bitmap初始化图片着色器,通过缩放矩阵将原资源图片缩放到铺满整个绘制区域，避免边界填充
     */
    private fun initBitmapShader(drawable: BitmapDrawable): BitmapShader? {
        val bitmap = drawable.bitmap
        val bitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        val scale = (width / bitmap.width).coerceAtLeast(width / bitmap.height)
        viewMatrix.setScale(scale, scale)
        bitmapShader.setLocalMatrix(viewMatrix)
        return bitmapShader
    }
}