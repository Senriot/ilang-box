package com.android.karaoke.common.mvvm

import android.content.Context
import com.arthurivanets.mvvm.AbstractViewModel
import java.security.AccessControlContext

abstract class BaseViewModel(private val context: Context? = null) : AbstractViewModel(),MvvmViewModel
{

}