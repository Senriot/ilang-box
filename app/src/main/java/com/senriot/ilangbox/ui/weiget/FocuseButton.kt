package com.senriot.ilangbox.ui.weiget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import androidx.core.view.ViewCompat
import com.apkfuns.logutils.LogUtils


class FocuseButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    androidx.appcompat.widget.AppCompatButton(
        context,
        attrs,
        defStyleAttr
    )
{
    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?)
    {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused)
        {
            scaleUp();
        } else
        {
            scaleDown();
        }
        LogUtils.e("onFocusChanged")
    }

    //1.08表示放大倍数,可以随便改
    private fun scaleUp()
    {
        ViewCompat.animate(this)
            .setDuration(200)
            .scaleX(1.08f)
            .scaleY(1.08f)
            .start()
    }

    private fun scaleDown()
    {
        ViewCompat.animate(this)
            .setDuration(200)
            .scaleX(1f)
            .scaleY(1f)
            .start()
    }
}