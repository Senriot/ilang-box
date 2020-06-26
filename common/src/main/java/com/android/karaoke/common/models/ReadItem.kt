package com.android.karaoke.common.models;

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable


/**
 * @Description: 朗读曲目
 * @Author: Allen
 * @Date:   2020-06-04
 * @Version: V1.0
 */
open class ReadItem(

    /**
     * 主键
     */
    @PrimaryKey
    var id: String = "",
    /**
     * 名称
     */
    var name: String = "",
    /**
     * 作者
     */
    var author: String? = null,
    /**
     * 分类
     */
    var category: ReadCategory? = null,
    /**
     * 是否推荐
     */
    var recommends: Boolean? = null,
    /**
     * 状态
     */
    var status: String? = null,
    /**
     * 封面
     */
    var cover: String? = null,
    /**
     * 背景
     */
    var bgPic: String? = null,
    /**
     * 音乐
     */
    var bgMusic: String? = null,
    /**
     * 字幕
     */
    var lyric: String? = null,
    /**
     * 简介
     */
    var info: String? = null,
    /**
     * 热度
     */
    var readCount: Int? = null,

    var bgm: ReadBgm? = null
) : RealmObject(), Serializable
{
    companion object
    {
        const val COL_ID = "id"
        const val COL_NAME = "name"
        const val COL_AUTHOR = "author"
        const val COL_CATEGORY_ID = "category"
        const val COL_IS_RECOMMENDS = "recommends"
        const val COL_STATUS = "status"
        const val COL_COVER = "cover"
        const val COL_BG_PIC = "bgPic"
        const val COL_BG_MUSIC = "bgMusic"
        const val COL_LYRIC = "lyric"
        const val COL_INFO = "info"
        const val COL_READ_COUNT = "readCount"
    }
}
