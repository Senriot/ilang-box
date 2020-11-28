package com.android.karaoke.common.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*


//var retrofit = Retrofit.Builder()
//    .baseUrl("https://ilang.senriot.com/sery/ilang/")
//    .addConverterFactory(MoshiConverterFactory.create())
//    .addCallAdapterFactory(CoroutineCallAdapterFactory())
//    .build()
//
//
//val updateApiService = retrofit.create(UpdateApiService::class.java)

interface UpdateApiService
{
    @GET("sysParams/queryByCode?code=app_update_info")
    fun updateInfo(): Deferred<Result<UpdateInfo>>

    @GET("wx/device/login/{deviceId}")
    fun authUrl(@Path("deviceId") deviceId: String): Deferred<Result<String>>
}

interface RecordApiService
{
    @Multipart
    @POST("record/upload")
    fun upload(@Part file: MultipartBody.Part): Deferred<Result<UploadResult>>

    @POST("record/add")
    fun add(@Body item: RecordVO): Deferred<Result<String>>
}

object Api
{
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://ilang.senriot.com/sery/ilang/")
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    val updateApiService: UpdateApiService by lazy { retrofit.create(UpdateApiService::class.java) }

    val recordApiService: RecordApiService by lazy { retrofit.create(RecordApiService::class.java) }
}

