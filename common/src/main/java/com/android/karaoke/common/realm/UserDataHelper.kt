package com.android.karaoke.common.realm

import android.view.View
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
import kotlin.properties.Delegates

object UserDataHelper
{
    private val realm = Realm.getInstance(userConfig)

//    var userId = "guest"

    private const val GUEST_ID = "Guest"

    var userData: UserData = UserData(GUEST_ID)

    var userId by Delegates.observable("", { property, oldValue, newValue ->
        if (oldValue != newValue)
        {
            if (newValue.isNotEmpty())
            {
                userData = realm.where<UserData>().equalTo("id", newValue).findFirst() ?: UserData(
                    newValue
                )
                if (!userData.isManaged)
                {
                    realm.executeTransaction {
                        userData = it.createObject(UserData::class.java, newValue)
                    }
                }
                userData.favorites.addChangeListener { t, _ ->
                    EventBus.getDefault().post(FavoritesChangedEvent())
                }
                userData.playlist.addChangeListener { t, _ ->
                    EventBus.getDefault().post(PlaylistChangedEvent(t))
                }
                EventBus.getDefault().post(ProfileDataInitEvent(userData))
            }
        }
    })

//    val userData by lazy {
//        var data = realm.where<UserData>().equalTo("id", userId).findFirst() ?: UserData(userId)
//        if (!data.isManaged)
//        {
//            realm.executeTransaction { data = realm.createObject(UserData::class.java, userId) }
//        }
//        data.favorites.addChangeListener { t, _ ->
//            EventBus.getDefault().post(FavoritesChangedEvent())
//        }
//        data.playlist.addChangeListener { t, _ ->
//            EventBus.getDefault().post(PlaylistChangedEvent(t))
//        }
//        EventBus.getDefault().post(ProfileDataInitEvent(data))
//        data
//    }


//    @JvmStatic
//    fun initUserData(uid: String?)
//    {
//        this.userId = uid ?: "guest"
//
//        var data = realm.where<UserData>().equalTo("id", userId).findFirst()
//        if (data == null)
//        {
//            data = UserData(userId)
//            realm.executeTransaction {
//                realm.copyToRealmOrUpdate(data)
//            }
//        }
//        userData = realm.where<UserData>().equalTo("id", userId).findFirst()!!
//        userData.favorites.addChangeListener { t, _ ->
//            LogUtils.i("???????????? ${t.size}")
//            EventBus.getDefault().post(FavoritesChangedEvent())
//        }
//
//        userData.playlist.addChangeListener { t, _ ->
//            LogUtils.i("?????????????????? ${t.size}")
//            EventBus.getDefault().post(PlaylistChangedEvent(t))
//        }
//        userData.let {
//            realm.executeTransaction { _ ->
//                try
//                {
//                    it.history.clear();
//                    it.currentPlay = null;
//                    it.playlist.clear()
//                }
//                catch (e: Exception)
//                {
//                    e.printStackTrace()
//                }
//            }
//            EventBus.getDefault().post(ProfileDataInitEvent(it))
//        }
//    }

    @JvmStatic
    fun setFavorites(song: Song)
    {
        realm.executeTransaction {
            val s = it.copyToRealmOrUpdate(song)
            if (userData.favorites.contains(s))
            {
                userData.favorites.remove(s)
            }
            else
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
        LogUtils.e("???????????? $song")
        val file = File(song.file_path + song.file_name)
        if (file.exists())
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
            }
            else
            {
                userData.playlist.add(s)
            }
        }
    }

    /**
     * ????????????
     */
    @JvmStatic
    fun isSelected(song: Song?): Boolean
    {
        return userData.playlist.find { it.id == song?.id } != null
    }

    /**
     * ????????????
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
    fun removeSongRecord(v: View, item: SongRecord)
    {
        try
        {
            realm.executeTransaction {
                try
                {
                    item.deleteFromRealm()
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }

            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }

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
            }
            else
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
        return userData.id != GUEST_ID && !item.updated
    }

    @JvmStatic
    fun hasUpload(item: SongRecord): Boolean
    {
        return userData.id != GUEST_ID && !item.updated
    }

    @JvmStatic
    fun delRecord(item: Record)
    {
        realm.executeTransaction { userData.records.remove(item) }
    }
}