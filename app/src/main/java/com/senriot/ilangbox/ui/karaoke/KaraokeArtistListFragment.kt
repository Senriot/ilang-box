package com.senriot.ilangbox.ui.karaoke

import android.os.Bundle
import com.arthurivanets.mvvm.MvvmFragment
import com.arthurivanets.mvvm.events.Command
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.R
import com.senriot.ilangbox.databinding.KaraokeArtistListFragmentBinding
import com.senriot.ilangbox.ui.GeneralViewModelCommands
import kotlinx.android.synthetic.main.karaoke_artist_list_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel

class KaraokeArtistListFragment :
    MvvmFragment<KaraokeArtistListFragmentBinding, KaraokeArtistListViewModel>(R.layout.karaoke_artist_list_fragment)
{

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<KaraokeArtistListViewModel>()

    override fun createViewModel(): KaraokeArtistListViewModel = vm


    override fun postInit()
    {
        super.postInit()
        scrollView.setAdapter(vm.adapter)
    }

    override fun onHandleCommand(command: Command<*>)
    {
        super.onHandleCommand(command)
        if (command is GeneralViewModelCommands.showArtistSongs)
        {
            val pf = this.parentFragment as KaraokeFragment
            pf.fragmentController.newRequest(KaraokeFragments.artistSongs)
                .arguments(Bundle().apply { putParcelable("artist", command.payload) })
                .replaceSame(true)
                .addToBackStack(true)
                .execute()
        }
    }
}
