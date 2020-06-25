package com.android.karaoke.common.models;

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


/**
 * @Description: 朗读分类
 * @Author: Allen
 * @Date:   2020-06-05
 * @Version: V1.0
 */
open class ReadCategory(

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
     * 图片
     */
    var pic: String? = null,
    /**
     * 备注
     */
    var remark: String? = null,
    /**
     * 状态
     */
    var status: String? = null

) : RealmObject(){
    companion object {
        const val COL_ID = "id"
        const val COL_NAME = "name"
        const val COL_PIC = "pic"
        const val COL_REMARK = "remark"
        const val COL_STATUS = "status"
    }
}
