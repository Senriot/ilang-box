// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.android.karaoke.common.models

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

open class CategoryItem() : RealmObject(), Serializable, Parcelable
{
    @PrimaryKey
    var id: Long = 0

    var Name: String = ""

    var Value: String = ""

    var Icon: String? = null

    constructor(source: Parcel) : this()
    {
        id = source.readLong()
        Name = source.readString()!!
        Value = source.readString()!!
        Icon = source.readString()
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {}

    companion object
    {
        @JvmField
        val CREATOR: Parcelable.Creator<CategoryItem> = object : Parcelable.Creator<CategoryItem>
        {
            override fun createFromParcel(source: Parcel): CategoryItem = CategoryItem(source)
            override fun newArray(size: Int): Array<CategoryItem?> = arrayOfNulls(size)
        }
    }
}
