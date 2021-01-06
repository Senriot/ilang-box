package com.android.karaoke.common.api;

import java.util.*

/**
 * @Description: 用户录音
 * @Author: Allen
 * @Date:   2020-11-27
 * @Version: V1.0
 */
data class RecordVO(

    /**
     * 主键
     */
    var id: String = "",
    /**
     * 微信ID
     */
    var openId: String = "",

    var langduId: String = "",

    var songId: String = "",
    /**
     * 地址
     */
    var url: String = "",
    /**
     * 类别
     */
    var recordType: String? = null
)
{
    companion object
    {
        const val COL_ID = "id"
        const val COL_OPENID = "openid"
        const val COL_ITEMID = "itemid"
        const val COL_URL = "url"
        const val COL_RECORD_TYPE = "record_type"
        const val COL_CREATE_BY = "create_by"
        const val COL_CREATE_TIME = "create_time"
        const val COL_UPDATE_BY = "update_by"
        const val COL_UPDATE_TIME = "update_time"
        const val COL_SYS_ORG_CODE = "sys_org_code"
    }
}
