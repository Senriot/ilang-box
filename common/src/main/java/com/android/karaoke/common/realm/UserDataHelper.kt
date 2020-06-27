package com.android.karaoke.common.realm

import com.android.karaoke.common.events.FavoritesChangedEvent
import com.android.karaoke.common.events.PlaylistChangedEvent
import com.android.karaoke.common.events.ProfileDataInitEvent
import com.android.karaoke.common.models.Song
import com.android.karaoke.common.models.UserData
import com.apkfuns.logutils.LogUtils
import io.realm.Realm
import io.realm.kotlin.where
import org.greenrobot.eventbus.EventBus

object UserDataHelper
{
    private val realm by lazy {
        Realm.getInstance(userConfig)
    }

    var userData: UserData? = null

    @JvmStatic
    fun initUserData(userId: String?)
    {
        val id = userId ?: "guest"

        userData = realm.where<UserData>().equalTo("id", id).findFirst()
        LogUtils.i(userData)
        if (userData == null)
        {
            userData = UserData(id)
            realm.executeTransaction { realm.copyToRealmOrUpdate(userData) }
        }
        userData?.favorites?.addChangeListener { t, changeSet ->
            LogUtils.i("收藏改变 ${t.size}")
            EventBus.getDefault().post(FavoritesChangedEvent())
        }

        userData?.playlist?.addChangeListener { t, changeSet ->
            LogUtils.i("播放列表改变 ${t.size}")
            EventBus.getDefault().post(PlaylistChangedEvent(t))
        }
        userData?.let { EventBus.getDefault().post(ProfileDataInitEvent(it)) }
    }

    @JvmStatic
    fun setFavorites(song: Song)
    {
        realm.executeTransaction {
            val s = it.copyToRealmOrUpdate(song)
            if (userData?.favorites?.contains(s) == true)
            {
                userData?.favorites?.remove(s)
            } else
                userData?.favorites?.add(s)
        }
    }

    @JvmStatic
    fun isFavorites(song: Song?): Boolean
    {
        userData?.let {
            return it.favorites.find { it.id == song?.id } != null
        }
        return false
    }

    @JvmStatic
    fun addPlaylist(song: Song)
    {
        realm.executeTransaction {
            val s = it.copyToRealmOrUpdate(song)
            if (userData?.playlist?.contains(s) != true)
            {
                userData?.playlist?.add(s)
            }
        }
    }

    @JvmStatic
    fun checkPlaylistItem(song: Song)
    {
        userData?.let { params ->
            realm.executeTransaction {
                val s = it.copyToRealmOrUpdate(song)
                if (params.playlist.contains(s))
                {
                    params.playlist.remove(s)
                } else
                {
                    params.playlist.add(s)
                }
            }
        }
    }

    /**
     * 是否选中
     */
    @JvmStatic
    fun isSelected(song: Song?): Boolean
    {
        userData?.let { params ->
            return params.playlist.find { it.id == song?.id } != null
        }
        return false
    }

    /**
     * 是否置顶
     */
    @JvmStatic
    fun isTop(song: Song?): Boolean
    {
        val index = userData?.playlist?.indexOf(song)
        return index != 0
    }

    fun isFavorite(song: Song?): Boolean
    {
        userData?.let { params ->
            return params.favorites.find { it.id == song?.id } != null
        }
        return false
    }

    fun currentPlayIsFavorite(): Boolean =
        userData?.favorites?.find { it.id == userData?.currentPlay?.id } != null

    @JvmStatic
    fun removePlaylistItem(song: Song)
    {
        userData?.let { params ->
            if (params.playlist.contains(song))
            {
                realm.executeTransaction { params.playlist.remove(song) }
            }
        }
    }

    @JvmStatic
    fun removeHistoryItem(song: Song)
    {
        realm.executeTransaction { userData?.history?.remove(song) }
    }

    @JvmStatic
    fun onTop(item: Song)
    {
        userData?.let { params ->
            if (params.playlist.contains(item))
            {
                realm.executeTransaction {
                    params.playlist.remove(item)
                    params.playlist.add(0, item)
                }
            } else
            {
                realm.executeTransaction {
                    it.copyToRealmOrUpdate(item)
                    params.playlist.add(0, item)
                }
            }
        }
    }
}