package com.senriot.ilangbox.koin

import com.senriot.ilangbox.InitActViewModel
import com.senriot.ilangbox.MainActViewModel
import com.senriot.ilangbox.ui.karaoke.*
import com.senriot.ilangbox.ui.langdu.*
import com.senriot.ilangbox.ui.xuexi.DzContentViewModel
import com.senriot.ilangbox.ui.xuexi.XueXiViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ViewModels {
    val module = module {
        viewModel { MainActViewModel() }
        viewModel { XueXiViewModel() }
        viewModel { LangDuViewModel() }
        viewModel { KaraokeViewModel() }
        viewModel { LdMainViewModel() }
        viewModel { LdContentViewModel() }
        viewModel { LdItemDetailViewModel() }
        viewModel { LdRecordingViewModel() }
        viewModel { LdBgmViewModel() }
        viewModel { KaraokeMainViewModel() }
        viewModel { KaraokeListViewModel() }
        viewModel { KaraokeArtistListViewModel() }
        viewModel { KaraokeArtistSongsViewModel() }
        viewModel { ReadListViewModel() }
        viewModel { AuditionViewModel() }
        viewModel { DzContentViewModel() }
        viewModel { InitActViewModel() }
        viewModel { SoundEffectViewModel() }
    }
}