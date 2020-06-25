package com.senriot.ilangbox.ui.langdu

import com.android.karaoke.common.models.Record
import com.android.karaoke.common.realm.UserDataHelper
import com.arthurivanets.mvvm.AbstractViewModel
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import io.realm.Realm
import io.realm.kotlin.where
import me.tatarka.bindingcollectionadapter2.ItemBinding

class ReadListViewModel : AbstractViewModel()
{
    val items by lazy {
        Realm.getDefaultInstance().where<Record>().findAll()
    }

    val itemBinding by lazy {
        ItemBinding.of<Record>(BR.item, R.layout.ld_record_item)
    }
}
