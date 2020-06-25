package com.senriot.ilangbox.ui.langdu

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.android.karaoke.common.models.ReadItem
import com.arthurivanets.mvvm.AbstractViewModel

class LdItemDetailViewModel : AbstractViewModel() {
//    var item: ReadItem = ReadItem()

    val item = ObservableField<ReadItem>()
}
