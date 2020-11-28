package com.android.karaoke.common.realm

import com.android.karaoke.common.events.FavoritesChangedEvent
import com.android.karaoke.common.events.PlaylistChangedEvent
import com.android.karaoke.common.events.ProfileDataInitEvent
import com.android.karaoke.common.models.Record
import com.android.karaoke.common.models.Song
import com.android.karaoke.common.models.SongRecord
import com.android.karaoke.common.models.UserData
import com.apkfuns.logutils.LogUtils
import io.realm.Realm
import io.realm.kotlin.where
import org.greenrobot.eventbus.EventBus
import java.io.File

object UserDataHelper
{
    private val realm = Realm.getInstance(userConfig)


    lateinit var userData: UserData

    @JvmStatic
    fun initUserData(userId: String?)
    {
        val id = userId ?: "guest"

        var data = realm.where<UserData>().equalTo("id", id).findFirst()
        if (data == null)
        {
            data = UserData(id)
            realm.executeTransaction {
                userData = realm.copyToRealmOrUpdate(data)
            }
        } else
        {
            userData = data
        }
        userData.favorites.addChangeListener { t, _ ->
            LogUtils.i("收藏改变 ${t.size}")
            EventBus.getDefault().post(FavoritesChangedEvent())
        }

        userData.playlist.addChangeListener { t, _ ->
            LogUtils.i("播放列表改变 ${t.size}")
            EventBus.getDefault().post(PlaylistChangedEvent(t))
        }
        userData.let {
            realm.executeTransaction { _ ->
                it.history.clear();
                it.currentPlay = null;
                it.playlist.clear()
            }
            EventBus.getDefault().post(ProfileDataInitEvent(it))
        }
    }

    @JvmStatic
    fun setFavorites(song: Song)
    {
        realm.executeTransaction {
            val s = it.copyToRealmOrUpdate(song)
            if (userData.favorites.contains(s))
            {
                userData.favorites.remove(s)
            } else
                userData.favorites.add(s)
        }
    }

    @JvmStatic
    fun isFavorites(song: Song?): Boolean
    {
        return userData.favorites.find { it.id == song?.id } != null
    }

    @JvmStatic
    fun addPlaylist(song: Song)
    {
        LogUtils.e("选中歌曲 $song")
        if (song.exist == true)
        {
            realm.executeTransaction {
                val s = it.copyToRealmOrUpdate(song)
                if (!userData.playlist.contains(s))
                {
                    userData.playlist.add(s)
                }
            }
        }
    }

    @JvmStatic
    fun checkPlaylistItem(song: Song)
    {
        realm.executeTransaction {
            val s = it.copyToRealmOrUpdate(song)
            if (userData.playlist.contains(s))
            {
                userData.playlist.remove(s)
            } else
            {
                userData.playlist.add(s)
            }
        }
    }

    /**
     * 是否选中
     */
    @JvmStatic
    fun isSelected(song: Song?): Boolean
    {
        return userData.playlist.find { it.id == song?.id } != null
    }

    /**
     * 是否置顶
     */
    @JvmStatic
    fun isTop(song: Song?): Boolean
    {
        val index = userData.playlist.indexOf(song)
        return index != 0
    }

    fun isFavorite(song: Song?): Boolean
    {
        return userData.favorites.find { it.id == song?.id } != null
    }

    fun currentPlayIsFavorite(): Boolean =
        userData.favorites.find { it.id == userData.currentPlay?.id } != null

    @JvmStatic
    fun removePlaylistItem(song: Song)
    {
        if (userData.playlist.contains(song))
        {
            realm.executeTransaction { userData.playlist.remove(song) }
        }
    }

    @JvmStatic
    fun removeHistoryItem(song: Song)
    {
        realm.executeTransaction { userData.history.remove(song) }
    }

    @JvmStatic
    fun removeSongRecord(item: SongRecord)
    {
        realm.executeTransaction { item.deleteFromRealm() }
    }

    @JvmStatic
    fun onTop(item: Song)
    {
        userData.let { params ->
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

    @JvmStatic
    fun hasUpload(item: Record): Boolean
    {
        return userData.id != "Guest" && !item.updated
    }
}