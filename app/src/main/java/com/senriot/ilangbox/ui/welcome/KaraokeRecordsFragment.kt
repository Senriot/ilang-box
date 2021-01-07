package com.senriot.ilangbox.ui.welcome

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.senriot.ilangbox.R

class KaraokeRecordsFragment : Fragment()
{

    companion object
    {
        fun newInstance() = KaraokeRecordsFragment()
    }

    private lateinit var viewModel: KaraokeRecordsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        return inflater.inflate(R.layout.karaoke_records_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(KaraokeRecordsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}