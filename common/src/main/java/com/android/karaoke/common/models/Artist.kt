// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.android.karaoke.common.models

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.io.Serializable
import java.util.*

@RealmClass(name = "ilang_artist")
open class Artist() : RealmObject(), Serializable, Parcelable
{

    @PrimaryKey
    var id: Long = 0

    @Index
    var name: String = ""

    @Index
    var input_code: String? = null
    var area_id: String? = null
    var area_text: String? = null
    var gender: String? = null
    var avatar: String? = null
    var birthplace: String? = null
    var birthday: Date? = null
    var company: String? = null
    var constellation: String? = null
    var info: String? = null

    @Index
    var hot: Long? = null
    var weight: String? = null
    var stature: String? = null
    var status: String? = null

    constructor(parcel: Parcel) : this()
    {
        id = parcel.readLong()
        name = parcel.readString() ?: ""
        input_code = parcel.readString()
        area_id = parcel.readString()
        area_text = parcel.readString()
        gender = parcel.readString()
        avatar = parcel.readString()
        birthplace = parcel.readString()
        company = parcel.readString()
        constellation = parcel.readString()
        info = parcel.readString()
        hot = parcel.readValue(Long::class.java.classLoader) as? Long
        weight = parcel.readString()
        stature = parcel.readString()
        status = parcel.readString()
    }


    //    override fun describeContents() = 0
//
//    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {}
//
//    companion object
//    {
//        @JvmField
//        val CREATOR: Parcelable.Creator<Artist> = object : Parcelable.Creator<Artist>
//        {
//            override fun createFromParcel(source: Parcel): Artist = Artist(source)
//            override fun newArray(size: Int): Array<Artist?> = arrayOfNulls(size)
//        }
//    }
    override fun writeToParcel(parcel: Parcel, flags: Int)
    {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(input_code)
        parcel.writeString(area_id)
        parcel.writeString(area_text)
        parcel.writeString(gender)
        parcel.writeString(avatar)
        parcel.writeString(birthplace)
        parcel.writeString(company)
        parcel.writeString(constellation)
        parcel.writeString(info)
        parcel.writeValue(hot)
        parcel.writeString(weight)
        parcel.writeString(stature)
        parcel.writeString(status)
    }

    override fun describeContents(): Int
    {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Artist>
    {
        override fun createFromParcel(parcel: Parcel): Artist
        {
            return Artist(parcel)
        }

        override fun newArray(size: Int): Array<Artist?>
        {
            return arrayOfNulls(size)
        }
    }

}
