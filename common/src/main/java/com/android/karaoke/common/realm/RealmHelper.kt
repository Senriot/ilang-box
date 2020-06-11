package com.android.karaoke.common.realm

import com.android.karaoke.common.events.FavoritesChangedEvent
import com.android.karaoke.common.events.PlaylistChangedEvent
import com.android.karaoke.common.events.ProfileDataInitEvent
import com.android.karaoke.common.models.Song
import com.android.karaoke.common.models.SystemParams
import com.apkfuns.logutils.LogUtils
import io.realm.Realm
import io.realm.kotlin.where
import org.greenrobot.eventbus.EventBus
import java.util.*

object RealmHelper
{
    private val realm by lazy {
        Realm.getInstance(appConfig)
    }

    var profileParams: SystemParams? = null

    @JvmStatic
    fun initPersonalSettings(userId: String?)
    {
        val id = userId ?: "guest"

        profileParams = realm.where<SystemParams>().equalTo("id", id).findFirst()
        LogUtils.i(profileParams)
        if (profileParams == null)
        {
            profileParams = SystemParams(id)
            realm.executeTransaction { realm.copyToRealmOrUpdate(profileParams) }
        }
        profileParams?.favorites?.addChangeListener { t, changeSet ->
            LogUtils.i("收藏改变 ${t.size}")
            EventBus.getDefault().post(FavoritesChangedEvent())
        }

        profileParams?.playlist?.addChangeListener { t, changeSet ->
            LogUtils.i("播放列表改变 ${t.size}")
            EventBus.getDefault().post(PlaylistChangedEvent(t))
        }
        profileParams?.let { EventBus.getDefault().post(ProfileDataInitEvent(it)) }
    }

    @JvmStatic
    fun setFavorites(song: Song)
    {
        realm.executeTransaction {
            val s = it.copyToRealmOrUpdate(song)
            if (profileParams?.favorites?.contains(s) == true)
            {
                profileParams?.favorites?.remove(s)
            }
            else
                profileParams?.favorites?.add(s)
        }
    }

    @JvmStatic
    fun isFavorites(song: Song?): Boolean
    {
        profileParams?.let {
            return it.favorites.find { it.id == song?.id } != null
        }
        return false
    }

    @JvmStatic
    fun addPlaylist(song: Song)
    {
        realm.executeTransaction {
            val s = it.copyToRealmOrUpdate(song)
            if (s.filePath != null && profileParams?.playlist?.contains(s) != true)
            {
                profileParams?.playlist?.add(s)
            }
        }
    }

    @JvmStatic
    fun checkPlaylistItem(song: Song)
    {
        profileParams?.let { params ->
            realm.executeTransaction {
                val s = it.copyToRealmOrUpdate(song)
                if (s.filePath != null)
                    if (params.playlist.contains(s))
                    {
                        params.playlist.remove(s)
                    }
                    else
                    {
                        params.playlist.add(s)
                    }
            }
        }
    }

    @JvmStatic
    fun isSelected(song: Song?): Boolean
    {
        profileParams?.let { params ->
            return params.playlist.find { it.id == song?.id } != null
        }
        return false
    }


    fun isFavorite(song: Song?): Boolean
    {
        profileParams?.let { params ->
            return params.favorites.find { it.id == song?.id } != null
        }
        return false
    }

    fun currentPlayIsFavorite(): Boolean =
        profileParams?.favorites?.find { it.id == profileParams?.currentPlay?.id } != null

    @JvmStatic
    fun removePlaylistItem(song: Song)
    {
        profileParams?.let { params ->
            if (params.playlist.contains(song))
            {
                realm.executeTransaction { params.playlist.remove(song) }
            }
        }
    }

    @JvmStatic
    fun onTop(item: Song)
    {
        profileParams?.let { params ->
            if (params.playlist.contains(item))
            {
                realm.executeTransaction {
                    params.playlist.remove(item)
                    params.playlist.add(0, item)
                }
            }
        }
    }
}