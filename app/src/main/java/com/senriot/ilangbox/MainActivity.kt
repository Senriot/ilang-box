package com.senriot.ilangbox

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.android.karaoke.player.PlayerService
import com.android.karaoke.player.PlayerServiceConnection
import com.arthurivanets.mvvm.MvvmActivity
import com.arthurivanets.mvvm.MvvmFragment
import com.labo.kaji.relativepopupwindow.RelativePopupWindow
import com.senriot.ilangbox.databinding.ActivityMainBinding
import com.senriot.ilangbox.event.LoginEvent
import com.senriot.ilangbox.event.MainNavChangedEvent
import com.senriot.ilangbox.event.ShowReadListEvent
import com.senriot.ilangbox.ui.NavFragment
import com.senriot.ilangbox.ui.input.InputPopupWindow
import com.senriot.ilangbox.ui.karaoke.KaraokeFragment
import com.senriot.ilangbox.ui.karaoke.KaraokeFragments
import com.senriot.ilangbox.ui.karaoke.MediaListFragment
import com.senriot.ilangbox.ui.karaoke.MinorDisplayFragment
import com.senriot.ilangbox.ui.langdu.LangDuFragment
import com.senriot.ilangbox.ui.langdu.LangDuFragments
import com.senriot.ilangbox.ui.langdu.LdMainFragmentDirections
import com.senriot.ilangbox.ui.welcome.ProfileFragment
import com.senriot.ilangbox.ui.xuexi.XueXiFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.koin.android.viewmodel.ext.android.viewModel
import universum.studios.android.fragment.manage.FragmentController

class MainActivity : MvvmActivity<ActivityMainBinding, MainActViewModel>(R.layout.activity_main)
{

    val vm by viewModel<MainActViewModel>()

    val conn: PlayerServiceConnection by lazy { PlayerServiceConnection() }


    private val fragmentController by lazy {
        FragmentController(this, supportFragmentManager).apply {
            viewContainerId = R.id.container
            factory = MainActFragments()
        }
    }

    override val bindingVariable: Int = BR.vm


    override fun createViewModel(): MainActViewModel
    {
        return vm
    }

    override fun init(savedInstanceState: Bundle?)
    {
        super.init(savedInstanceState)
        EventBus.getDefault().register(this)
        nav.check(R.id.rb_langdu)
        nav.setOnCheckedChangeListener { group, checkedId ->
            val id = when (checkedId)
            {
                R.id.rb_xuexi -> MainActFragments.DZ
                R.id.rb_langdu -> MainActFragments.LANGDU
                R.id.rb_hongge -> MainActFragments.KARAOKE
                else           -> -1
            }
            fragmentController.newRequest(id).immediate(true).execute()
            EventBus.getDefault().post(MainNavChangedEvent(checkedId))
        }
        fragmentController.newRequest(MainActFragments.LANGDU).immediate(true).execute()
        startPlayerService()
    }

    override fun onStart()
    {
        super.onStart()
        if (App.wxUser != null)
        {
            vm.avatar.set(App.wxUser!!.headImgUrl)
        }
    }

    override fun onResume()
    {
        super.onResume()
        hideNavBar(true)
    }


    fun showLdContent(view: View)
    {
        val tag = view.tag.toString()
        val f = fragmentController.findCurrentFragment()
        if (f is LangDuFragment)
        {
            f.onBackPressed()
            f.fragmentController.newRequest(LangDuFragments.content)
                .arguments(Bundle().apply { putString("categoryId", tag) }).addToBackStack(true)
                .execute()
        }
    }


    fun goBack(view: View)
    {
        onBackPressed()
    }

    override fun onBackPressed()
    {
        val f = fragmentController.findCurrentFragment() as NavFragment<*, *>
        if (f.fragmentController.hasBackStackEntries())
        {
            f.fragmentController.fragmentManager.popBackStack()
        }
        else
            super.onBackPressed()
    }

    fun showLdBgmList(view: View)
    {
        val f = fragmentController.findCurrentFragment()
        if (f is LangDuFragment)
        {
            f.onBackPressed()
            f.fragmentController.newRequest(LangDuFragments.bgm)
                .addToBackStack(true)
                .execute()
        }
    }

    /**
     * 启动播放服务
     */
    private fun startPlayerService()
    {
        bindService(Intent(this, PlayerService::class.java), conn, Context.BIND_AUTO_CREATE)
    }


    fun karaokeCardClick(view: View)
    {
        val title = view.tag.toString()
        val args = Bundle()
        args.putString("title", title)
        val f = fragmentController.findCurrentFragment()
        if (f is KaraokeFragment)
        {
            if (title == "2")
            {
                f.fragmentController.newRequest(KaraokeFragments.artistList).addToBackStack(true)
                    .replaceSame(true).execute()
            }
            else
            {
                f.fragmentController.newRequest(KaraokeFragments.songList).arguments(args)
                    .addToBackStack(true).replaceSame(true).execute()
            }
        }
    }

    fun showMediaList(view: View)
    {
        MediaListFragment().show(supportFragmentManager, "mediaList")
    }

    fun showInputView(view: View)
    {
        val w = InputPopupWindow(this, vm)
        w.setOnDismissListener {
            vm.inputWindowIsShow = false
            vm.onClear()
        }
        w.showOnAnchor(
            view,
            RelativePopupWindow.VerticalPosition.ALIGN_TOP,
            RelativePopupWindow.HorizontalPosition.ALIGN_RIGHT, 0, 80, false
        )
    }

    fun showProfile(view: View)
    {
        ProfileDialogFragment().show(supportFragmentManager, "profile")
    }


    private fun hideNavBar(hide: Boolean)
    {
        val intent = Intent("android.intent.action.hideNaviBar")
        intent.putExtra("hide", hide)
        sendBroadcast(intent)
    }


    fun showMinorDisplay(view: View)
    {
        MinorDisplayFragment().show(supportFragmentManager, null)
    }

    fun openSetting(view: View)
    {
        startActivity(Intent(Settings.ACTION_SETTINGS));
    }

    @Subscribe
    fun loginEvent(event: LoginEvent)
    {
        vm.avatar.set(App.wxUser!!.headImgUrl)
    }


}