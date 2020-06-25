package com.senriot.ilangbox.ui.karaoke

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.arthurivanets.mvvm.MvvmFragment
import org.koin.android.viewmodel.ext.android.viewModel

import com.senriot.ilangbox.R
import com.senriot.ilangbox.BR
import com.senriot.ilangbox.databinding.KaraokeArtistListFragmentBinding
import kotlinx.android.synthetic.main.karaoke_artist_list_fragment.*

class KaraokeArtistListFragment :
    MvvmFragment<KaraokeArtistListFragmentBinding, KaraokeArtistListViewModel>(R.layout.karaoke_artist_list_fragment) {

    override val bindingVariable: Int = BR.vm

    private val vm by viewModel<KaraokeArtistListViewModel>()

    override fun createViewModel(): KaraokeArtistListViewModel = vm

    override fun performDataBinding() {
        super.performDataBinding()
        scrollView.setAdapter(vm.adapter)
    }
}
