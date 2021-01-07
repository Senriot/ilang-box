package com.senriot.ilangbox.ui.karaoke

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.karaoke.common.models.SongRecord
import com.android.karaoke.common.realm.UserDataHelper
import com.android.karaoke.common.realm.userConfig
import com.senriot.ilangbox.R
import com.senriot.ilangbox.adapter.HistoryAdapter
import com.senriot.ilangbox.adapter.PlaylistAdapter
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.media_list_dialog.*

class MediaListFragment : DialogFragment()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialog)
    }

    private val adapter by lazy {
        PlaylistAdapter(UserDataHelper.userData.playlist)
    }

    private val hisAdapter by lazy {
        val items = Realm.getInstance(userConfig).where<SongRecord>()
            .equalTo("openId", UserDataHelper.userData.id).findAll()
        HistoryAdapter(items)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.media_list_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        list.layoutManager = LinearLayoutManager(activity)
        list.adapter = adapter
        tabBar.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.rbHistory)
            {
                list.adapter = hisAdapter
            } else
            {
                list.adapter = adapter
            }
        }
    }

    override fun dismiss()
    {
        super.dismiss()
    }

    override fun onStart()
    {
        super.onStart()
        val window = dialog?.window
        val params = dialog?.window?.attributes
        params?.gravity = Gravity.CENTER or Gravity.RIGHT
        params?.height = 750
        params?.width = 700
        params?.horizontalMargin = 0.02f
        window?.attributes = params
//        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}