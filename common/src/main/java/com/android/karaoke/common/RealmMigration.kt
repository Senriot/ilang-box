package com.android.karaoke.common

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration
import java.util.*

class RealmMigration : RealmMigration
{
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long)
    {
        var oldVersion = oldVersion
        if (oldVersion == 0L)
        {
            realm.schema.create("ok_dict")
                .addField("id", Int::class.java, FieldAttribute.PRIMARY_KEY)
                .addField("code", String::class.java, FieldAttribute.REQUIRED)
                .addField("key", String::class.java, FieldAttribute.REQUIRED)
                .addField("value", String::class.java)
                .addField("sort", String::class.java)
                .addField("remark", String::class.java)
                .addField("pid", Int::class.java)
                .setRequired("pid", false)

            realm.schema.create("Marquee")
                .addField("id", Int::class.java, FieldAttribute.PRIMARY_KEY)
                .addField("content", String::class.java, FieldAttribute.REQUIRED)

            realm.schema.create("PlayList")
                .addField(
                    "id",
                    String::class.java,
                    FieldAttribute.PRIMARY_KEY,
                    FieldAttribute.REQUIRED
                )
                .addRealmObjectField("currentPlay", realm.schema.get("Song"))
                .addRealmListField("selectedList", realm.schema["Song"])
                .addRealmListField("playedList", realm.schema["Song"])
            oldVersion++
        }

        if (oldVersion == 1L)
        {
            realm.schema.get("Artist")?.addField("genre", String::class.java)

            val artists = realm.where("Artist").findAll()

            artists.forEach {
                val item = it.getInt("category")

//                realm.executeTransaction { r ->
                when (item)
                {
                    1 -> it.setString("genre", "male")
                    2 -> it.setString("genre", "female")
                    3 -> it.setString("genre", "band")
//                    }
                }

            }

            oldVersion++
        }
        if (oldVersion == 2L)
        {
            val schema = realm.schema
            schema.create("SelectList")
                .addField(
                    "id",
                    String::class.java,
                    FieldAttribute.PRIMARY_KEY,
                    FieldAttribute.REQUIRED
                )
                .addRealmObjectField("song", schema["Song"])
                .addField("updateTime", Date::class.java, FieldAttribute.REQUIRED)
            schema.create("History")
                .addField(
                    "id",
                    String::class.java,
                    FieldAttribute.PRIMARY_KEY,
                    FieldAttribute.REQUIRED
                )
                .addRealmObjectField("song", schema["Song"])
                .addField("updateTime", Date::class.java, FieldAttribute.REQUIRED)
            oldVersion++
        }

        if (oldVersion == 3L)
        {
//            val schema = realm.schema
//            schema["Song"]?.addField("isCurrentPlay", Boolean::class.java)
//                ?.addField("favorite", Boolean::class.java)
//                ?.addField("edition", String::class.java)
            oldVersion++
        }

        if (oldVersion == 4L)
        {
//            val schema = realm.schema
//            schema.create("SystemParams").addField(
//                    "id", String::class.java, FieldAttribute.PRIMARY_KEY,
//                    FieldAttribute.REQUIRED
//                )
//                .addRealmListField("playlist", schema.get("Song"))
//                .addRealmListField("history", schema.get("Song"))
//                .addRealmObjectField("currentPlay", schema.get("Song"))
//
//            realm.createObject("SystemParams", getDeviceSN())
            oldVersion++
        }
    }

    override fun hashCode(): Int
    {
        return 40
    }

    override fun equals(other: Any?): Boolean
    {
        return (other is RealmMigration);
    }
}