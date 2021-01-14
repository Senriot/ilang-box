package com.senriot.ilangbox.ui

import androidx.databinding.ViewDataBinding
import com.arthurivanets.mvvm.BaseViewModel
import com.arthurivanets.mvvm.MvvmFragment
import universum.studios.android.fragment.manage.FragmentController

abstract class NavFragment<VDB : ViewDataBinding, VM : BaseViewModel>(private val layoutId: Int) :
    MvvmFragment<VDB, VM>(layoutId)
{
    abstract val fragmentController: FragmentController
}


