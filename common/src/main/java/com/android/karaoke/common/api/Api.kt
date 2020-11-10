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


val updateApiService = retrofit.create(UpdateApiService::class.java)

interface UpdateApiService
{
    @GET("sysParams/queryByCode?code=app_update_info")
    fun updateInfo(): Observable<Result<UpdateInfo>>
}

