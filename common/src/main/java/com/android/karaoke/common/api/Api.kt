package com.android.karaoke.common.api

import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.GET


var retrofit = Retrofit.Builder()
    .baseUrl("https://ilang.senriot.com/jeecg-boot/ilang/")
    .addConverterFactory(JacksonConverterFactory.create())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .build()


val dbVerService = retrofit.create(DbVerService::class.java)

interface DbVerService
{
    @GET("sysParams/queryByCode?code=ld_db_ver")
    fun getDbVer(): Observable<Result<DbVer>>
}

