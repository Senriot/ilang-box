package com.senriot.ilangbox

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.android.karaoke.common.MvvmActivity
import com.android.karaoke.common.models.*
import com.android.karaoke.common.realm.userConfig
import com.android.karaoke.player.PlayerService
import com.android.karaoke.player.PlayerServiceConnection
import com.apkfuns.logutils.LogUtils
import com.senriot.ilangbox.databinding.ActivityMainBinding
import com.senriot.ilangbox.event.MainNavChangedEvent
import com.senriot.ilangbox.event.ShowReadListEvent
import com.senriot.ilangbox.ui.karaoke.MediaListFragment
import com.senriot.ilangbox.ui.langdu.LdMainFragmentDirections
import com.senriot.ilangbox.ui.xuexi.XueXiFragment
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : MvvmActivity<ActivityMainBinding, MainActViewModel>(R.layout.activity_main)
{

    val vm by viewModel<MainActViewModel>()

    val conn: PlayerServiceConnection by lazy { PlayerServiceConnection() }

    val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.navNavigation) as NavHostFragment
    }

    private var currentFragment: Fragment = XueXiFragment()

    override val bindingVariable: Int = BR.vm

    override fun createViewModel(): MainActViewModel
    {
        return vm
    }

    override fun init(savedInstanceState: Bundle?)
    {
        super.init(savedInstanceState)
        nav.check(R.id.rb_xuexi)
        nav.setOnCheckedChangeListener { group, checkedId ->
            val id = when (checkedId)
            {
                R.id.rb_xuexi  -> R.id.xueXiFragment
                R.id.rb_langdu -> R.id.langDuFragment
                R.id.rb_hongge -> R.id.karaokeFragment
                else           -> -1
            }
            val builder = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setEnterAnim(R.anim.nav_default_enter_anim)
                .setExitAnim(R.anim.nav_default_exit_anim)
                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
            navHostFragment.navController.navigate(id, null, builder.build())
            if (id == R.id.langDuFragment)
            {
                ldList.visibility = View.VISIBLE
            } else
            {
                ldList.visibility = View.GONE
            }
            EventBus.getDefault().post(MainNavChangedEvent(checkedId))
        }
        startPlayerService()
    }

    override fun onSupportNavigateUp(): Boolean
    {
        return Navigation.findNavController(this, R.id.navNavigation).navigateUp()
    }

    override fun onResume()
    {
        super.onResume()
        hideNavBar(true)
    }

    fun showLdContent(view: View)
    {
        val nc = findNavController(view.id)
        val tag = view.tag.toString()
        nc.navigate(LdMainFragmentDirections.actionLdMainFragmentToLdContentFragment(tag))
    }

    fun goBack(view: View)
    {
        view.findNavController().popBackStack()
    }

    fun showLdList(view: View)
    {

        EventBus.getDefault().post(ShowReadListEvent())
//        navHostFragment.navController.navigate(Uri.parse("https://www.senriot.com/ilang-box/readlist"))
//        val artists = Realm.getDefaultInstance().where<Dict>().findAll()
//
//        Realm.getInstance(userConfig).executeTransaction {
//            it.copyToRealmOrUpdate(artists)
//        }
//
//        LogUtils.e("保存完成")
//        Realm.getInstance(ldConfig).executeTransaction {
//            it.delete(ReadBgm::class.java)
//        }
//        val file = File("/sdcard/ilang-box/bgm")

//        file.listFiles().forEach {
//            val names = it.name.split("-")
//            val bgm = ReadBgm().apply {
//                uuid = UUID.randomUUID().toString()
//                artist = names[0]
//                name = names[1].replace(".mp3", "")
//                this.file = it.path
//            }
//            bgm.saveManaged(Realm.getInstance(ldConfig))
//        }
    }

    fun showLdBgmList(view: View)
    {
        val navController = view.findNavController()
        navController.navigate(Uri.parse("https://senriot.com/bgm"))
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
        val nc = findNavController(view.id)
        val title = view.tag.toString()
        val args = Bundle()
        args.putString("title", title)
        if (title == "2")
        {
            nc.navigate(R.id.action_karaokeMainFragment_to_karaokeArtistListFragment)
        } else
        {
            nc.navigate(R.id.action_karaokeMainFragment_to_karaokeListFragment, args)
        }
    }


    private val mediaListFragment by lazy { MediaListFragment() }

    fun showMediaList(view: View)
    {
        mediaListFragment.show(supportFragmentManager, "mediaList")
    }

    private fun hideNavBar(hide: Boolean)
    {
        val intent = Intent("android.intent.action.hideNaviBar")
        intent.putExtra("hide", hide)
        sendBroadcast(intent)
    }
}