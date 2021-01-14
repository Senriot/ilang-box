package com.senriot.ilangbox.ui.karaoke

import com.android.karaoke.common.models.Artist
import com.arthurivanets.mvvm.MvvmFragment
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.KaraokeArtistSongsFragmentBinding
import kotlinx.android.synthetic.main.karaoke_artist_songs_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel

class KaraokeArtistSongsFragment :
        MvvmFragment<KaraokeArtistSongsFragmentBinding, KaraokeArtistSongsViewModel>(R.layout.karaoke_artist_songs_fragment)
{

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<KaraokeArtistSongsViewModel>()

    override fun createViewModel(): KaraokeArtistSongsViewModel = vm

    override fun preInit()
    {
        super.preInit()
        arguments?.getParcelable<Artist>("artist")?.let {  vm.artist.set(it) }
    }

    override fun postInit()
    {
        super.postInit()
        scrollView.setAdapter(vm.adapter)
    }

//    override fun performDataBinding()
//    {
//        super.performDataBinding()
//        scrollView.setAdapter(vm.adapter)
//    }
}
