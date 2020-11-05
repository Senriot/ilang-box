package com.senriot.ilangbox.ui.xuexi

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.karaoke.common.models.Category
import com.android.karaoke.common.models.Dict
import com.arthurivanets.mvvm.MvvmFragment
import org.koin.android.viewmodel.ext.android.viewModel

import com.senriot.ilangbox.R
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.databinding.XueXiFragmentBinding
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.input_fragment.*
import kotlinx.android.synthetic.main.xue_xi_fragment.*

class XueXiFragment : MvvmFragment<XueXiFragmentBinding, XueXiViewModel>(R.layout.xue_xi_fragment)
{

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<XueXiViewModel>()

    override fun createViewModel(): XueXiViewModel = vm

    override fun performDataBinding()
    {
        super.performDataBinding()
        val categoryes = Realm.getDefaultInstance().where(Category::class.java)
            .equalTo("pid", "1277622648859443202").sort("sort_no").findAll()
        categoryes.forEachIndexed { index, category ->
            val item = RadioButton(activity)
            item.background = resources.getDrawable(R.drawable.dz_rb_selector)
            item.text = category.name
            item.id = category.id.toLong().toInt()
            item.textSize = 22f
            item.buttonDrawable = null
            val lp = RadioGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT
            )
            lp.weight = 1.0f
            item.layoutParams = lp
            item.textAlignment = TEXT_ALIGNMENT_CENTER
            item.tag = category
            navBar.addView(item)
            if (index == 0) item.isChecked = true
        }
//        val item = RadioButton(activity)
//        item.background = resources.getDrawable(R.drawable.dz_rb_selector)
//        dz_rb_123.isChecked = true
        navBar.setOnCheckedChangeListener { group, checkedId ->
            val btn = findViewById<RadioButton>(checkedId)

//            val id = when (checkedId)
//            {
//                R.id.dz_rb_123 -> 123
//                R.id.dz_rb_124 -> 124
//                R.id.dz_rb_125 -> 125
//                R.id.dz_rb_126 -> 126
//                R.id.dz_rb_127 -> 127
//                else           -> 0
//            }
//            val dict = Realm.getDefaultInstance().where<Dict>().equalTo("id", id).findFirst()!!
            vm.selectedDict = btn.tag as Category
        }
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = vm.adapter
    }
}
