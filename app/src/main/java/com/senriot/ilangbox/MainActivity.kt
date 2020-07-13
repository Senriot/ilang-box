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
import com.android.karaoke.common.MvvmActivity
import com.android.karaoke.common.models.Artist
import com.android.karaoke.common.models.Song
import com.android.karaoke.player.PlayerService
import com.android.karaoke.player.PlayerServiceConnection
import com.apkfuns.logutils.LogUtils
import com.labo.kaji.relativepopupwindow.RelativePopupWindow
import com.senriot.ilangbox.databinding.ActivityMainBinding
import com.senriot.ilangbox.event.MainNavChangedEvent
import com.senriot.ilangbox.event.ShowReadListEvent
import com.senriot.ilangbox.ui.input.InputPopupWindow
import com.senriot.ilangbox.ui.karaoke.MediaListFragment
import com.senriot.ilangbox.ui.karaoke.MinorDisplayFragment
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
        nav.check(R.id.rb_langdu)
        nav.setOnCheckedChangeListener { group, checkedId ->
            val id = when (checkedId)
            {
//                R.id.rb_xuexi  -> R.id.xueXiFragment
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

    fun showInputView(view: View)
    {
        InputPopupWindow(this, vm).showOnAnchor(
            view,
            RelativePopupWindow.VerticalPosition.ALIGN_TOP,
            RelativePopupWindow.HorizontalPosition.ALIGN_RIGHT, 0, 80, false
        )
//        val f = InputFragment()
//        f.setAnchorView(view)
//        f.setAligmentFlags(AlignmentFlag.ALIGN_ANCHOR_VIEW_RIGHT or AlignmentFlag.ALIGN_ANCHOR_VIEW_BOTTOM)
//        f.show(supportFragmentManager, "input")
    }

    fun showProfile(view: View)
    {


        val realm = Realm.getDefaultInstance()
        val songs = realm.where<Song>().findAll()
        realm.beginTransaction()
        songs.forEach { song ->
            if (!song.artist.isNullOrBlank())
            {
                realm.where<Artist>().equalTo("name", song.artist).findFirst()?.let {
                    song.singer_id = it.id
                    it.status = 2
                }
            }
        }
        realm.commitTransaction()
        LogUtils.e("ok======")
    }
//        realm.executeTransaction { it.delete(Song::class.java) }
//        val dzs = realm.where<DangZheng>().findAll()
//        realm.beginTransaction()
//        dzs.forEach {
//            val song = Song().apply {
//                id = it.code
//                name = it.name!!
//                input_code = it.pinyin
//                artist = it.artist
//                track = it.bz
//                volume = it.vol
//                file_name = it.code + ".mkv"
//                hot = 0
//            }
//            when (it.type)
//            {
//                "军旅" -> song.type_id = 120
//                "抒情" -> song.type_id = 121
//                "民歌" -> song.type_id = 48
//                "经典" -> song.type_id = 44
//            }
//
//            when (it.lang)
//            {
//                "其他" -> song.lang_id = 28
//                "台语" -> song.lang_id = 24
//                "国语" -> song.lang_id = 22
//                "粤语" -> song.lang_id = 23
//                "英语" -> song.lang_id = 27
//                "韩语" -> song.lang_id = 25
//            }
//
//            realm.copyToRealmOrUpdate(song)
////            val ss = realm.where<Song>().equalTo("id", it.code).findAll()
////            ss.forEach { s ->
////                s.status = 3
////            }
//        }
//        realm.commitTransaction()


    private fun hideNavBar(hide: Boolean)
    {
        val intent = Intent("android.intent.action.hideNaviBar")
        intent.putExtra("hide", hide)
        sendBroadcast(intent)
    }

    private val minorDisplay by lazy { MinorDisplayFragment() }

    fun showMinorDisplay(view: View)
    {
        minorDisplay.show(supportFragmentManager, null)
    }

    fun openSetting(view: View){
        startActivity( Intent(Settings.ACTION_SETTINGS));
    }
}