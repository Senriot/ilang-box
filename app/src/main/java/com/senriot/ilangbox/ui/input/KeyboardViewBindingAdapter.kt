package com.senriot.ilangbox.ui.input

import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import androidx.databinding.BindingAdapter
import com.wwengine.hw.PaintView


/**
 * Created by Allen on 16/9/2.
 */
object KeyboardViewBindingAdapter
{
    @BindingAdapter("keyboard")
    @JvmStatic
    fun setKeyboard(
        keyboardView: KeyboardView,
        resId: Int
    )
    {
        if (resId > 0)
            keyboardView.keyboard = Keyboard(keyboardView.context, resId)
    }


    @BindingAdapter("keyboardActionListener")
    @JvmStatic
    fun setKeyboardActionListener(
        keyboardView: KeyboardView,
        onKeyboardActionListener: KeyboardView.OnKeyboardActionListener?
    )
    {
        try
        {
            keyboardView.setOnKeyboardActionListener(onKeyboardActionListener)
        } catch (e: Exception)
        {
            e.printStackTrace()
        }

    }

    @BindingAdapter("onOnKeyboardAction")
    @JvmStatic
    fun setOnKeyboardActionListener(
        keyboardView: KeyboardView,
        listener: KeyboardView.OnKeyboardActionListener?
    )
    {
        try
        {
            keyboardView.setOnKeyboardActionListener(listener)
        } catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    @BindingAdapter("listener")
    @JvmStatic
    fun setListener(view: PaintView, lister: PaintView.OnResultListener)
    {
        view.listener = lister
    }
}