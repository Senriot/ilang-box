package com.senriot.ilangbox.ui.karaoke

import androidx.navigation.NavController
import androidx.navigation.fragment.navArgs
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

    private val args by navArgs<KaraokeArtistSongsFragmentArgs>()

    override fun createViewModel(): KaraokeArtistSongsViewModel = vm

    override fun preInit()
    {
        super.preInit()
        vm.artist.set(args.artist)
    }

    override fun performDataBinding()
    {
        super.performDataBinding()
        scrollView.setAdapter(vm.adapter)
    }
}
