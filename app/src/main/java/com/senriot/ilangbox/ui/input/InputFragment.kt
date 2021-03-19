package com.senriot.ilangbox.ui.input

import android.inputmethodservice.KeyboardView
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.kingfisherphuoc.quickactiondialog.QuickActionDialogFragment
import com.senriot.ilangbox.R
import com.senriot.ilangbox.event.SearchTextChangedEvent
import io.alterac.blurkit.BlurLayout
import kotlinx.android.synthetic.main.input_fragment.*
import org.greenrobot.eventbus.EventBus
import kotlin.properties.Delegates


class InputFragment : QuickActionDialogFragment()
{

//    override val bindingVariable: Int = BR.vm
//
//    private val vm by viewModel<InputViewModel>()
//
//    override fun createViewModel(): InputViewModel = vm

    private lateinit var blurLayout: BlurLayout

    override fun getLayout(): Int
    {
        return R.layout.input_fragment
    }

    override fun getArrowImageViewId(): Int
    {
        return 0
    }

    private var searchKeyword by Delegates.observable("", onChange = { _, oldValue, newValue ->
        if (oldValue != newValue)
        {
            searchText.text = newValue
            EventBus.getDefault().post(SearchTextChangedEvent(newValue))
        }
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
//        val keyboard = view.findViewById<KeyboardView>(R.id.keyboardView)
////        blurLayout = view.findViewById(R.id.blurLayout)
//        keyboard.keyboard = Keyboard(context, R.xml.letter)
//        keyboard.setOnKeyboardActionListener(onKeyboardActionListener)
//        btnDel.setOnClickListener { this.onDelete() }
//        paintView.listener = PaintView.OnResultListener { result ->
//            LogUtils.e(result)
//            txtResult1.text = result[0].toString()
//            txtResult2.text = result[1].toString()
//            txtResult3.text = result[2].toString()
//            txtResult4.text = result[3].toString()
//            txtResult5.text = result[4].toString()
//        }

        btnClear.setOnClickListener {
            paintView.resetRecognize()
        }
        txtResult1.setOnClickListener(txtResultClick)
        txtResult2.setOnClickListener(txtResultClick)
        txtResult3.setOnClickListener(txtResultClick)
        txtResult4.setOnClickListener(txtResultClick)
        txtResult5.setOnClickListener(txtResultClick)

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.rbPinYin)
            {
                keyboardView.visibility = View.VISIBLE
                hwView.visibility = View.GONE
            }
            else
            {
                keyboardView.visibility = View.GONE
                hwView.visibility = View.VISIBLE
            }
        }
    }

    private val txtResultClick = View.OnClickListener {
        val v = (it as TextView).text
        if (!v.isNullOrBlank())
        {
            searchKeyword = "${searchKeyword}$v"
            paintView.resetRecognize()
        }
    }

    override fun onStart()
    {
        super.onStart()
//        blurLayout.startBlur()
    }

    override fun onStop()
    {
        super.onStop()
//        blurLayout.pauseBlur()
    }

    private val onKeyboardActionListener = object : KeyboardView.OnKeyboardActionListener
    {
        override fun swipeRight()
        {
        }

        override fun onPress(primaryCode: Int)
        {
        }

        override fun onRelease(primaryCode: Int)
        {
        }

        override fun swipeLeft()
        {
        }

        override fun swipeUp()
        {
        }

        override fun swipeDown()
        {
        }

        override fun onKey(primaryCode: Int, keyCodes: IntArray?)
        {
            when (primaryCode)
            {
                -5 -> onDelete()
                -4 -> onClear()
                else ->
                {
                    val s = primaryCode.toChar().toString()
                    var text = searchKeyword
                    text += s
                    searchKeyword = text
//                    searchText.set(text)
                }
            }
        }

        override fun onText(text: CharSequence?)
        {
        }

    }

    private fun onDelete()
    {
        val text = searchKeyword

        if (text.isNotEmpty())
            searchKeyword = text.substring(IntRange(0, text.length - 2))
    }

    private fun onClear()
    {
        searchKeyword = ""
    }
}
