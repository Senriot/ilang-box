package com.senriot.ilangbox.ui.langdu

import com.android.karaoke.common.models.ReadBgm
import com.arthurivanets.mvvm.AbstractViewModel
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import io.realm.Realm
import io.realm.kotlin.where
import me.tatarka.bindingcollectionadapter2.ItemBinding

class LdBgmViewModel : AbstractViewModel() {
    val items by lazy { Realm.getDefaultInstance().where<ReadBgm>().findAll() }
    val itemBinding by lazy {
        ItemBinding.of<ReadBgm>(
            BR.item,
            R.layout.ld_bgm_item
        )
    }
}

