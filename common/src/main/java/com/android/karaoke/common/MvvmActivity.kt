package com.android.karaoke.common

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.arthurivanets.mvvm.BaseViewModel
import com.arthurivanets.mvvm.MvvmActivity

abstract class MvvmActivity<VDB : ViewDataBinding, VM : BaseViewModel>(
    @LayoutRes
    private val layoutId: Int
) : MvvmActivity<VDB, VM>(layoutId) {


}