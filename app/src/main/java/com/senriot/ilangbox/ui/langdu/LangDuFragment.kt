package com.senriot.ilangbox.ui.langdu

import android.net.Uri
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.android.karaoke.common.models.ReadCategory
import com.apkfuns.logutils.LogUtils
import com.arthurivanets.mvvm.MvvmFragment
import org.koin.android.viewmodel.ext.android.viewModel

import com.senriot.ilangbox.R
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.databinding.LangDuFragmentBinding
import com.senriot.ilangbox.event.ShowReadListEvent
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.lang_du_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class LangDuFragment :
    MvvmFragment<LangDuFragmentBinding, LangDuViewModel>(R.layout.lang_du_fragment)
{

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<LangDuViewModel>()

    override fun createViewModel(): LangDuViewModel = vm

    init
    {
        EventBus.getDefault().register(this)
    }

    override fun onDestroyView()
    {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    override fun postInit()
    {
        super.postInit()
        Realm.getDefaultInstance().where<ReadCategory>().findAll()
    }

    @Subscribe
    fun onShowReadList(event: ShowReadListEvent)
    {
        LogUtils.e("onShowReadList")
        val host = childFragmentManager.findFragmentById(R.id.ldNavHost) as NavHostFragment
        host.navController.navigate(Uri.parse("https://www.senriot.com/ilang-box/readlist"))
    }
}
