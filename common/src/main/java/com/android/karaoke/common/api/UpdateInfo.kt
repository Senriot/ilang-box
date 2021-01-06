package com.android.karaoke.common.api


import com.fasterxml.jackson.annotation.JsonProperty

class UpdateInfo(
    @JsonProperty("createBy")
    var createBy: String = "",
    @JsonProperty("createTime")
    var createTime: String = "",
    @JsonProperty("id")
    var id: String = "",
    @JsonProperty("paramCode")
    var paramCode: String = "",

    @JsonProperty("paramsItems")
    var paramsItems: Map<String, SysParamsItem> = mapOf(),

    @JsonProperty("updateBy")
    var updateBy: String = "",
    @JsonProperty("updateTime")
    var updateTime: String = ""
)
{
    class SysParamsItem(
        @JsonProperty("createBy")
        var createBy: String = "",
        @JsonProperty("createTime")
        var createTime: String = "",
        @JsonProperty("id")
        var id: String = "",
        @JsonProperty("paramsId")
        var paramsId: String = "",
        @JsonProperty("paramsName")
        var paramsName: String = "",
        @JsonProperty("paramsRemark")
        var paramsRemark: String = "",
        @JsonProperty("paramsValue")
        var paramsValue: String = ""
    )
}