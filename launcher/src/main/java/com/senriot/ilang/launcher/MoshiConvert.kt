package com.senriot.ilang.launcher

import com.alibaba.fastjson.JSON
import com.drake.net.convert.DefaultConvert
import org.json.JSONObject
import java.lang.reflect.Type

class MoshiConvert : DefaultConvert(code = "code", message = "message", success = "200")
{
//    val moshi = Moshi.Builder().build()


    override fun <S> String.parseBody(succeed: Type): S?
    {
        val b = JSONObject(this)
        val result = b.getString("result")
        return JSON.parseObject(result, succeed)
    }
}