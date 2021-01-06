package com.android.karaoke.common.api


import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class Auth(
    @JsonProperty("accessToken")
    var accessToken: AccessToken = AccessToken(),
    @JsonProperty("user")
    var user: WxUser = WxUser()
) : Serializable

class AccessToken(
    @JsonProperty("accessToken")
    var accessToken: String = "",
    @JsonProperty("expiresIn")
    var expiresIn: Int = 0,
    @JsonProperty("openId")
    var openId: String = "",
    @JsonProperty("refreshToken")
    var refreshToken: String = "",
    @JsonProperty("scope")
    var scope: String = "",
    @JsonProperty("unionId")
    var unionId: String = ""
)

class WxUser(
    @JsonProperty("openId")
    var openId: String = "",
    @JsonProperty("city")
    var city: String = "",
    @JsonProperty("country")
    var country: String = "",
    @JsonProperty("groupId")
    var groupId: Any = Any(),
    @JsonProperty("headImgUrl")
    var headImgUrl: String = "",
    @JsonProperty("language")
    var language: String = "",
    @JsonProperty("nickname")
    var nickname: String = "",

    @JsonProperty("privileges")
    var privileges: List<Any> = listOf(),
    @JsonProperty("province")
    var province: String = "",
    @JsonProperty("qrScene")
    var qrScene: Any = Any(),
    @JsonProperty("qrSceneStr")
    var qrSceneStr: Any = Any(),
    @JsonProperty("remark")
    var remark: Any = Any(),
    @JsonProperty("sex")
    var sex: Int = 0,
    @JsonProperty("sexDesc")
    var sexDesc: String = "",
    @JsonProperty("subscribe")
    var subscribe: Any = Any(),
    @JsonProperty("subscribeScene")
    var subscribeScene: Any = Any(),
    @JsonProperty("subscribeTime")
    var subscribeTime: Any = Any(),
    @JsonProperty("tagIds")
    var tagIds: Any = Any(),
    @JsonProperty("unionId")
    var unionId: String = ""
)