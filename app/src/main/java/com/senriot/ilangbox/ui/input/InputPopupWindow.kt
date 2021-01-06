package com.senriot.ilangbox.ui.input

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.circularreveal.CircularRevealCompat
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import com.labo.kaji.relativepopupwindow.RelativePopupWindow
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.MainActViewModel
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.InputFragmentBinding
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import kotlin.math.hypot
import kotlin.math.max

@OptIn(KoinApiExtension::class)
class InputPopupWindow(context: Context, private val vm: MainActViewModel) :
    RelativePopupWindow(context), KoinComponent
{
    private val mBinding: InputFragmentBinding;

    init
    {
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.input_fragment,
            null,
            false
        )
        mBinding.setVariable(BR.vm, vm)
        contentView = mBinding.root
        width = 520
        height = 672
        isFocusable = true
        isOutsideTouchable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        mBinding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.rbPinYin)
            {
                mBinding.keyboardView.visibility = View.VISIBLE
                mBinding.hwView.visibility = View.GONE
            } else
            {
                mBinding.keyboardView.visibility = View.GONE
                mBinding.hwView.visibility = View.VISIBLE
            }
        }
        mBinding.btnClear.setOnClickListener { mBinding.paintView.resetRecognize() }
    }


    override fun showOnAnchor(
        anchor: View,
        vertPos: Int,
        horizPos: Int,
        x: Int,
        y: Int,
        fitInScreen: Boolean
    )
    {
        super.showOnAnchor(anchor, vertPos, horizPos, x, y, fitInScreen)
//        circularReveal(anchor)
        vm.inputWindowIsShow = true
        mBinding.blurLayout.startBlur()
    }

    override fun dismiss()
    {
        mBinding.blurLayout.pauseBlur()
        super.dismiss()
    }

    private fun circularReveal(anchor: View)
    {
        (contentView as CircularRevealCardView).run {
            post {
                val myLocation = IntArray(2).apply { getLocationOnScreen(this) }
                val anchorLocation = IntArray(2).apply { anchor.getLocationOnScreen(this) }
                val cx = anchorLocation[0] - myLocation[0] + anchor.width / 2
                val cy = anchorLocation[1] - myLocation[1] + anchor.height / 2
                val windowRect = Rect().apply { getWindowVisibleDisplayFrame(this) }

                measure(
                    makeDropDownMeasureSpec(this.width, windowRect.width()),
                    makeDropDownMeasureSpec(this.height, windowRect.height())
                )
                val dx = max(cx, measuredWidth - cx)
                val dy = max(cy, measuredHeight - cy)
                val finalRadius = hypot(dx.toFloat(), dy.toFloat())
                CircularRevealCompat.createCircularReveal(
                    this,
                    cx.toFloat(),
                    cy.toFloat(),
                    0f,
                    finalRadius
                ).run {
                    duration = 500
                    start()
                }
            }
        }
    }


    companion object
    {
        private fun makeDropDownMeasureSpec(measureSpec: Int, maxSize: Int): Int
        {
            return View.MeasureSpec.makeMeasureSpec(
                getDropDownMeasureSpecSize(measureSpec, maxSize),
                getDropDownMeasureSpecMode(measureSpec)
            )
        }

        private fun getDropDownMeasureSpecSize(measureSpec: Int, maxSize: Int): Int
        {
            return when (measureSpec)
            {
                ViewGroup.LayoutParams.MATCH_PARENT -> maxSize
                else                                -> View.MeasureSpec.getSize(measureSpec)
            }
        }

        private fun getDropDownMeasureSpecMode(measureSpec: Int): Int
        {
            return when (measureSpec)
            {
                ViewGroup.LayoutParams.WRAP_CONTENT -> View.MeasureSpec.UNSPECIFIED
                else                                -> View.MeasureSpec.EXACTLY
            }
        }
    }
}