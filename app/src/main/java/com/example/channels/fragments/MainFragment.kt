package com.example.channels.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.example.channels.Di
import com.example.channels.R
import com.example.channels.ViewModel.ChannelViewModel
import com.example.channels.ViewModel.ChannelViewModelFactory
import com.example.channels.databinding.FragmentMainBinding
import com.example.channels.fragments.ListFragments.AllFragment
import com.example.channels.fragments.ListFragments.FavoritesFragment
import com.example.channels.fragments.ListFragments.FragmentAdapter

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //ViewModel
        var channelViewModel = ViewModelProvider(
            requireActivity(),
            ChannelViewModelFactory(
                Di.downloadRepository,
                Di.channelRepository,
                Di.epgRepository,
            )
        )[ChannelViewModel::class.java]

        //Поиск
       binding.searchViewTvChannels.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val allFragment =
                    childFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewpagerForTabs + ":" + 0) as? AllFragment
                val favoritesFragment =
                    childFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewpagerForTabs + ":" + 1) as? FavoritesFragment

                allFragment?.searchQuery = newText
                favoritesFragment?.searchQuery = newText

                allFragment?.filterChannels(newText)
                favoritesFragment?.filterChannels(newText)

                return true
            }
        })

        //Вкладки
        val fragmentAdapter = FragmentAdapter(childFragmentManager)
        binding.viewpagerForTabs.adapter = fragmentAdapter
        binding.tabs.setupWithViewPager(binding.viewpagerForTabs)
    }

}