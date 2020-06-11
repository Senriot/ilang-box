package com.android.karaoke.player.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.WindowManager
import com.android.karaoke.common.models.Marquee
import com.android.karaoke.player.R
import com.daasuu.library.DisplayObject
import com.daasuu.library.FPSSurfaceView
import com.daasuu.library.callback.AnimCallBack
import com.daasuu.library.drawer.TextDrawer
import com.daasuu.library.easing.Ease
import java.util.*

/**
 * Created by Allen on 16/8/26.
 */
class TextSurface @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FPSSurfaceView(context, attrs, defStyleAttr), AnimCallBack
{

    private val paint = Paint()
    /**
     * 滚动速度
     */
    var speed = 10

    /**
     * 字体颜色
     */
    var textColor = Color.WHITE
        set(value)
        {
            field = value
            paint.color = value
        }

    /**
     * 字体大小
     */
    var textSize = 50
        set(value)
        {
            field = value
            paint.textSize = value.toFloat()
        }

    var scrollTexts: ArrayList<Marquee> = ArrayList<Marquee>()
        set(value)
        {
            field = value
            if (value.isNotEmpty())
            {
                if (isScroll) onStop() else onStart()
            }
        }

    /**
     * 当前滚动index
     */
    private var index: Int = 0

    /**
     * 滚动字幕间隔
     */
    val interval = 5000L

    /**
     * 是否滚动
     */
    var isScroll = false

    init
    {
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.TextSurface, defStyleAttr, 0
        )
        textColor = a.getColor(R.styleable.TextSurface_tsTextColor, Color.WHITE)
        textSize = a.getInteger(R.styleable.TextSurface_tsTextSize, 50)
        a.recycle()

        paint.isAntiAlias = true
    }

    /**
     * 添加一条滚动字幕
     */
    fun add(text: Marquee)
    {
        scrollTexts.add(text)
        if (!isScroll) onStart()
    }

    /**
     * 添加一个集合
     */
    fun addAll(collection: Collection<Marquee>)
    {
        scrollTexts.addAll(collection)
        if (!isScroll) onStart()
    }

    /**
     * 删除一条滚动字幕
     */
    fun remove(index: Int)
    {
        scrollTexts.removeAt(index)
    }

    /**
     * 开始滚动
     */
    fun onStart()
    {
        createNextTextDisplay()
        tickStart()
    }

    fun onStop()
    {
        removeAllChildren()
    }

    private fun createNextTextDisplay()
    {
        if (!scrollTexts.isEmpty())
        {
            val text = scrollTexts.getOrElse(index++) {
                index = 0
                scrollTexts[index]
            }

            val textDrawer = TextDrawer(text.content, paint)
            val textDisplay = DisplayObject()
            val duration = (paint.measureText(text.content) + getWindowWidth(context)) * speed


            textDisplay.with(textDrawer)
                .tween()
                .transform(getWindowWidth(context).toFloat(), 0f)
                .toX(duration.toLong(), -paint.measureText(text.content), Ease.LINEAR)
                .call(this)
                .end()
            addChild(textDisplay)

        }
    }

    /**
     * 滚动完成回调
     */
    override fun call()
    {
        removeAllChildren()
        postDelayed({
            createNextTextDisplay()
        }, interval)
    }

    fun getWindowWidth(context: Context): Int
    {
        val disp = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val size = Point()
        disp.getSize(size)
        return size.x
    }
}